package uk.nhs.hee.tis.revalidation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationRecordDto;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.Recommendation;
import uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome;
import uk.nhs.hee.tis.revalidation.entity.RecommendationType;
import uk.nhs.hee.tis.revalidation.exception.InvalidDeferralDateException;
import uk.nhs.hee.tis.revalidation.exception.InvalidRecommendationIdException;
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
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.READY_TO_REVIEW;
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

    public TraineeRecommendationDto getTraineeInfo(final String gmcId) {
        log.info("Fetching trainee info for GmcId: {}", gmcId);
        final var optionalDoctorsForDB = doctorsForDBRepository.findById(gmcId);

        if (optionalDoctorsForDB.isPresent()) {
            final var doctorsForDB = optionalDoctorsForDB.get();
            final var traineeCoreInfo = traineeCoreService.getTraineeInformationFromCore(of(gmcId));

            final var recommendationDTOBuilder = TraineeRecommendationDto.builder()
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

    public Recommendation saveRecommendation(final TraineeRecommendationRecordDto recordDTO) {
        final var doctorsForDB = doctorsForDBRepository.findById(recordDTO.getGmcNumber());
        final var submissionDate = doctorsForDB.get().getSubmissionDate();

        final var recommendationType = RecommendationType.valueOf(recordDTO.getRecommendationType());
        Recommendation recommendation = null;

        if (REVALIDATE.equals(recommendationType) || NON_ENGAGEMENT.equals(recommendationType)) {
            recommendation = Recommendation.builder()
                    .id(recordDTO.getRecommendationId())
                    .gmcNumber(recordDTO.getGmcNumber())
                    .recommendationType(recommendationType)
                    .recommendationStatus(READY_TO_REVIEW)
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
                        .id(recordDTO.getRecommendationId())
                        .gmcNumber(recordDTO.getGmcNumber())
                        .comments(recordDTO.getComments())
                        .recommendationType(recommendationType)
                        .recommendationStatus(READY_TO_REVIEW)
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

    public Recommendation updateRecommendation(final TraineeRecommendationRecordDto recordDTO) {
        validationRecommendationId(recordDTO.getRecommendationId());
        return saveRecommendation(recordDTO);
    }

    public boolean submitRecommendation(final String recommendationId, final String gmcNumber) {
        log.info("submitting request to gmc for recommendation: {} and gmcNumber: {}", recommendationId, gmcNumber);
        final var doctorsForDB = doctorsForDBRepository.findById(gmcNumber);
        final var recommendation = findRecommendationByIdAndGmcNumber(recommendationId, gmcNumber);

        final var doctor = doctorsForDB.get();
        final var tryRecommendationV2Response = gmcClientService.submitToGmc(doctor, recommendation);
        final var tryRecommendationV2Result = tryRecommendationV2Response.getTryRecommendationV2Result();
        if (tryRecommendationV2Result != null) {
            final var returnCode = tryRecommendationV2Result.getReturnCode();
            if (SUCCESS.getCode().equals(returnCode)) {
                recommendation.setRecommendationStatus(SUBMITTED_TO_GMC);
                recommendation.setOutcome(UNDER_REVIEW);
                recommendation.setActualSubmissionDate(doctor.getSubmissionDate());
                recommendation.setGmcRevalidationId(tryRecommendationV2Result.getRecommendationID());
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

    private List<TraineeRecommendationRecordDto> getCurrentAndLegacyRecommendation(final DoctorsForDB doctorsForDB) {
        final var gmcId = doctorsForDB.getGmcReferenceNumber();
        log.info("Fetching snapshot record for GmcId: {}", gmcId);
        final var snapshots = snapshotRepository.findByGmcNumber(gmcId);
        final var recommendations = recommendationRepository.findByGmcNumber(gmcId);
        final var currentRecommendations = recommendations.stream().map(rec -> {
            String gmcOutcome = null;
            //only check outcome status, if request has been submitted to GMC
            if (SUBMITTED_TO_GMC == rec.getRecommendationStatus()) {
                gmcOutcome = checkRecommendationStatus(gmcId, rec.getGmcRevalidationId(),
                        rec.getId(), doctorsForDB.getDesignatedBodyCode());
            }
            return TraineeRecommendationRecordDto.builder()
                    .gmcNumber(gmcId)
                    .recommendationId(rec.getId())
                    .deferralDate(rec.getDeferralDate())
                    .deferralReason(rec.getDeferralReason())
                    .deferralSubReason(rec.getDeferralSubReason())
                    .gmcOutcome(gmcOutcome)
                    .recommendationStatus(rec.getRecommendationStatus().name())
                    .recommendationType(rec.getRecommendationType().getType())
                    .gmcSubmissionDate(doctorsForDB.getSubmissionDate())
                    .actualSubmissionDate(rec.getActualSubmissionDate())
                    .admin(rec.getAdmin())
                    .build();
        }).collect(toList());

        final var snapshotRecommendations = snapshots.stream().map(snapshot -> {
            final var revalidation = snapshot.getRevalidation();
            return TraineeRecommendationRecordDto.builder()
                    .gmcNumber(gmcId)
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

        snapshotRecommendations.addAll(currentRecommendations);
        return snapshotRecommendations;
    }

    private String checkRecommendationStatus(final String gmcId, final String gmcRecommendationId,
                                             final String recommendationId, final String designatedBody) {
        final var checkRecommendationStatusResponse = gmcClientService.checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId, designatedBody);
        final var checkRecommendationStatusResult = checkRecommendationStatusResponse.getCheckRecommendationStatusResult();
        final var gmdReturnCode = checkRecommendationStatusResult.getReturnCode();
        if (SUCCESS.getCode().equals(gmdReturnCode)) {
            final var status = checkRecommendationStatusResult.getStatus();
            return RecommendationGmcOutcome.fromString(status).getOutcome();
        } else {
            final var responseCode = fromCode(gmdReturnCode);
            log.error("Gmc recommendation check status request is failed for GmcId: {} and recommendationId: {} with Response: {}." +
                    " Recommendation will stay in Under Review state", gmcId, recommendationId, responseCode.getMessage());
        }

        return UNDER_REVIEW.getOutcome();
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

    //before saving new recommendationId, check if the request is for update
    private void validationRecommendationId(final String recommendationId) {
        log.info("Request for the update of existing recommendation: {}", recommendationId);
        final var existingRecommendation = recommendationRepository.findById(recommendationId);
        if (existingRecommendation.isEmpty()) {
            throw new InvalidRecommendationIdException("No recommendation record found against given recommendationId");
        }
    }
}
