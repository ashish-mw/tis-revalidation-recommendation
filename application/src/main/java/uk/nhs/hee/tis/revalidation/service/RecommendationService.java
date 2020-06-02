package uk.nhs.hee.tis.revalidation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationDTO;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationRecordDTO;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.Recommendation;
import uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome;
import uk.nhs.hee.tis.revalidation.entity.RecommendationType;
import uk.nhs.hee.tis.revalidation.exception.InvalidDeferralDateException;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.repository.RecommendationRepository;
import uk.nhs.hee.tis.revalidation.repository.SnapshotRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.List.of;
import static java.util.stream.Collectors.toList;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.SUCCESS;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.fromCode;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome.UNDER_REVIEW;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.SUBMITTED_TO_GMC;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationType.NON_ENGAGEMENT;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationType.REVALIDATE;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.formatDate;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.formatDateTime;

@Slf4j
@Transactional
@Service
public class RecommendationService {

    private static final int MIN_DAYS_FROM_SUBMISSION_DATE = 60;
    private static final int MAX_DAYS_FROM_SUBMISSION_DATE = 365;

    @Autowired
    private DoctorsForDBRepository doctorsForDBRepository;

    @Autowired
    private SnapshotRepository snapshotRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private TraineeCoreService traineeCoreService;

    @Autowired
    private DeferralReasonService deferralReasonService;

    @Autowired
    private GmcClientService gmcClientService;

    public TraineeRecommendationDTO getTraineeInfo(final String gmcId) {
        log.info("Fetching trainee info for GmcId: {}", gmcId);
        final var optionalDoctorsForDB = doctorsForDBRepository.findById(gmcId);

        if (optionalDoctorsForDB.isPresent()) {
            final var doctorsForDB = optionalDoctorsForDB.get();
            final var traineeCoreInfo = traineeCoreService.getTraineeInformationFromCore(of(gmcId));

            final var recommendationDTOBuilder = TraineeRecommendationDTO.builder()
                    .fullName(format("%s %s", doctorsForDB.getDoctorFirstName(), doctorsForDB.getDoctorLastName()))
                    .gmcNumber(doctorsForDB.getGmcReferenceNumber())
                    .underNotice(doctorsForDB.getUnderNotice().value());
            if (traineeCoreInfo.get(gmcId) != null) {
                final var traineeCoreDTO = traineeCoreInfo.get(gmcId);
                recommendationDTOBuilder.cctDate(traineeCoreDTO.getCctDate());
                recommendationDTOBuilder.currentGrade(traineeCoreDTO.getCurrentGrade());
                recommendationDTOBuilder.programmeMembershipType(traineeCoreDTO.getProgrammeMembershipType());
            }

            recommendationDTOBuilder.revalidations(getCurrentAndLegacyRecommendation(doctorsForDB));
            recommendationDTOBuilder.deferralReasons(deferralReasonService.getAllDeferralReasons());
            return recommendationDTOBuilder.build();
        }

        return null;
    }

    public Recommendation saveRecommendation(final TraineeRecommendationRecordDTO recordDTO) {
        final var doctorsForDB = doctorsForDBRepository.findById(recordDTO.getGmcNumber());
        final var submissionDate = doctorsForDB.get().getSubmissionDate();

        final var recommendationType = RecommendationType.valueOf(recordDTO.getRecommendationType());
        Recommendation recommendation = null;

        if (REVALIDATE.equals(recommendationType) || NON_ENGAGEMENT.equals(recommendationType)) {
            recommendation = Recommendation.builder()
                    .gmcNumber(recordDTO.getGmcNumber())
                    .recommendationType(recommendationType)
                    .comments(recordDTO.getComments())
                    .gmcSubmissionDate(submissionDate)
                    .build();
        } else {
            final var deferralDate = recordDTO.getDeferralDate();
            final var validateDeferralDate = validateDeferralDate(deferralDate, submissionDate);
            final var deferralReason = deferralReasonService.getDeferralReasonByCode(recordDTO.getDeferralReason());
            final var deferralSubReason = deferralReason.getSubReasonByCode(recordDTO.getDeferralSubReason());
            if (validateDeferralDate) {
                recommendation = Recommendation.builder()
                        .gmcNumber(recordDTO.getGmcNumber())
                        .comments(recordDTO.getComments())
                        .recommendationType(recommendationType)
                        .deferralDate(recordDTO.getDeferralDate())
                        .deferralReason(deferralReason.getCode())
                        .deferralSubReason(deferralSubReason.getCode())
                        .gmcSubmissionDate(submissionDate)
                        .build();
            } else {
                throw new InvalidDeferralDateException("Deferral date is invalid");
            }
        }

        return recommendationRepository.save(recommendation);
    }

    public boolean submitRecommendation(final String recommendationId, final String gmcNumber) {
        final var doctorsForDB = doctorsForDBRepository.findById(gmcNumber);
        final var recommendation = findRecommendationByIdAndGmcNumber(recommendationId, gmcNumber);

        final var tryRecommendationV2Response = gmcClientService.submitToGmc(doctorsForDB.get(), recommendation);
        final var tryRecommendationV2Result = tryRecommendationV2Response.getTryRecommendationV2Result();
        if (tryRecommendationV2Result != null) {
            final var returnCode = tryRecommendationV2Result.getReturnCode();
            if (SUCCESS.getCode().equals(returnCode)) {
                recommendation.setRecommendationStatus(SUBMITTED_TO_GMC);
                recommendation.setOutcome(UNDER_REVIEW);
                recommendationRepository.save(recommendation);
                return true;
            } else {
                final var responseCode = fromCode(returnCode);
                log.error("Submission of recommendation to GMC is failed for GmcId: {} and RecommendationId: {}. Gmc response is: {}",
                        gmcNumber, recommendation.getId(), responseCode.getMessage());
            }
        }
        return false;
    }

    public Recommendation findRecommendationByIdAndGmcNumber(final String recommendationId, final String gmcNumber) {
        return recommendationRepository.findByIdAndGmcNumber(recommendationId, gmcNumber);
    }

    public Optional<Recommendation> findRecommendationById(final String recommendationId) {
        return recommendationRepository.findById(recommendationId);
    }

    private List<TraineeRecommendationRecordDTO> getCurrentAndLegacyRecommendation(final DoctorsForDB doctorsForDB) {
        final var gmcId = doctorsForDB.getGmcReferenceNumber();
        log.info("Fetching snapshot record for GmcId: {}", gmcId);
        final var snapshots = snapshotRepository.findByGmcNumber(gmcId);

        return snapshots.stream().map(snapshot -> {
            final var revalidation = snapshot.getRevalidation();
            return TraineeRecommendationRecordDTO.builder()
                    .deferralDate(formatDate(revalidation.getDeferralDate()))
                    .deferralReason(revalidation.getDeferralReason())
                    .deferralComment(revalidation.getDeferralComment())
                    .gmcOutcome(checkRecommendationStatus(gmcId, revalidation.getGmcRecommendationId(),
                            gmcId, doctorsForDB.getDesignatedBodyCode())) //TODO: find the correct legacy revalidationId insetead passing gmc number as reference.
                    .recommendationStatus(toUpperCase(revalidation.getRevalidationStatusCode()))
                    .recommendationType(toUpperCase(revalidation.getProposedOutcomeCode()))
                    .gmcSubmissionDate(formatDateTime(revalidation.getGmcSubmissionDateTime()))
                    .actualSubmissionDate(formatDate(revalidation.getSubmissionDate()))
                    .admin(revalidation.getAdmin())
                    .build();
        }).collect(toList());
    }

    private String checkRecommendationStatus(final String gmcId, final String gmcRecommendationId,
                                                           final String recommendationId, final String designatedBody) {
        final var checkRecommendationStatusResponse = gmcClientService.checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId, designatedBody);
        final var checkRecommendationStatusResult = checkRecommendationStatusResponse.getCheckRecommendationStatusResult();
        final var gmdReturnCode = checkRecommendationStatusResult.getReturnCode();
        if (SUCCESS.getCode().equals(gmdReturnCode)) {
            final var status = checkRecommendationStatusResult.getStatus();
            return RecommendationGmcOutcome.fromString(status).name();
        } else {
            final var responseCode = fromCode(gmdReturnCode);
            log.error("Gmc recommendation check status request is failed for GmcId: {} and recommendationId: {} with Response: {}." +
                    " Recommendation will stay in Under Review state", gmcId, recommendationId, responseCode.getMessage());
        }

        return UNDER_REVIEW.name();
    }

    private String toUpperCase(final String code) {
        return !StringUtils.isEmpty(code) ? code.toUpperCase() : code;
    }

    //Deferral date should be atleast after 60 days from submission date and max up to 365 days from submission date
    private boolean validateDeferralDate(final LocalDate deferralDate, final LocalDate submissionDate) {
        final var submissionDateWith60Days = submissionDate.plusDays(MIN_DAYS_FROM_SUBMISSION_DATE);
        final var submissionDateWith365Days = submissionDate.plusDays(MAX_DAYS_FROM_SUBMISSION_DATE);

        return deferralDate.isAfter(submissionDateWith60Days) && deferralDate.isBefore(submissionDateWith365Days);
    }
}
