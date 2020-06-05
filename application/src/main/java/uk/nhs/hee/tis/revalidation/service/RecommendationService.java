package uk.nhs.hee.tis.revalidation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationRecordDto;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.Recommendation;
import uk.nhs.hee.tis.revalidation.entity.RecommendationType;
import uk.nhs.hee.tis.revalidation.exception.RecommendationException;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.repository.RecommendationRepository;

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

@Slf4j
@Transactional
@Service
public class RecommendationService {

    private static final int MIN_DAYS_FROM_SUBMISSION_DATE = 60;
    private static final int MAX_DAYS_FROM_SUBMISSION_DATE = 365;

    @Autowired
    private DoctorsForDBRepository doctorsForDBRepository;

    @Autowired
    private SnapshotService snapshotService;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private TraineeCoreService traineeCoreService;

    @Autowired
    private DeferralReasonService deferralReasonService;

    @Autowired
    private GmcClientService gmcClientService;

    //get trainee information with current and legacy recommendations
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

    //save a new recommendation
    public Recommendation saveRecommendation(final TraineeRecommendationRecordDto recordDTO) {
        isAllowedToCreateNewRecommendation(recordDTO.getGmcNumber(), recordDTO.getRecommendationId());

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
                throw new RecommendationException(format("Deferral date is invalid, should be in between of 60 and 365 days of Gmc Submission Date: %s", submissionDate));
            }
        }

        return recommendationRepository.save(recommendation);
    }

    //update an existing recommendation
    public Recommendation updateRecommendation(final TraineeRecommendationRecordDto recordDTO) {
        validationRecommendationId(recordDTO.getRecommendationId());
        return saveRecommendation(recordDTO);
    }

    //submit a recommendation to gmc
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
                snapshotService.saveRecommendationToSnapshot(recommendation);
                return true;
            } else {
                final var responseCode = fromCode(returnCode);
                log.error("Submission of recommendation to GMC is failed for GmcId: {} and RecommendationId: {}. Gmc response is: {}",
                        gmcNumber, recommendation.getId(), responseCode.getMessage());
                throw new RecommendationException(format("Fail to submit recommendation: %s", responseCode.getMessage()));
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

        final var recommendations = recommendationRepository.findByGmcNumber(gmcId);
        final var currentRecommendations = recommendations.stream().map(rec -> {
            String gmcOutcome = null;
            //only check outcome status, if request has been submitted to GMC
            if (SUBMITTED_TO_GMC == rec.getRecommendationStatus()) {
                gmcOutcome = gmcClientService.checkRecommendationStatus(gmcId, rec.getGmcRevalidationId(),
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
                    .recommendationType(rec.getRecommendationType().name())
                    .gmcSubmissionDate(doctorsForDB.getSubmissionDate())
                    .actualSubmissionDate(rec.getActualSubmissionDate())
                    .admin(rec.getAdmin())
                    .comments(rec.getComments())
                    .build();
        }).collect(toList());

        final var snapshotRecommendations = snapshotService.getSnapshotRecommendations(doctorsForDB);
        currentRecommendations.addAll(snapshotRecommendations);
        return currentRecommendations;
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
            throw new RecommendationException("No recommendation record found against given recommendationId");
        }
    }

    //if recommendation for trainee is already in draft state, admin are not allowed to create a new one but allow to update
    //if recommendation for trainee is Submitted to gmc but still in Under Review state, admin are not allowed to create a new one.
    private void isAllowedToCreateNewRecommendation(final String gmcNumber, final String recommendationId) {
        final var recommendations = recommendationRepository.findByGmcNumber(gmcNumber);
        final var recommendation = recommendations.stream().filter(r -> {
            if (r.getId().equals(recommendationId)) { //check if the request is for update
                return false;
            }
            if (SUBMITTED_TO_GMC != r.getRecommendationStatus() || UNDER_REVIEW == r.getOutcome()) {
                return true;
            }
            return false;
        }).findFirst();

        if (recommendation.isPresent()) {
            throw new RecommendationException("Trainee already have an recommendation in draft state or waiting for approval from GMC.");
        }
    }
}
