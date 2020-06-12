package uk.nhs.hee.tis.revalidation.it;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.hee.tis.revalidation.RevalidationApplication;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationRecordDto;
import uk.nhs.hee.tis.revalidation.entity.RecommendationStatus;
import uk.nhs.hee.tis.revalidation.entity.RecommendationType;
import uk.nhs.hee.tis.revalidation.entity.Snapshot;
import uk.nhs.hee.tis.revalidation.entity.SnapshotRevalidation;
import uk.nhs.hee.tis.revalidation.exception.RecommendationException;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.repository.RecommendationRepository;
import uk.nhs.hee.tis.revalidation.repository.SnapshotRepository;
import uk.nhs.hee.tis.revalidation.service.DeferralReasonService;
import uk.nhs.hee.tis.revalidation.service.RecommendationService;

import java.util.List;
import java.util.UUID;

import static java.util.Map.of;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome.*;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.READY_TO_REVIEW;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.SUBMITTED_TO_GMC;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationType.*;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.formatDate;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.formatDateTime;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RevalidationApplication.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
public class RecommendationIT extends BaseIT {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private DoctorsForDBRepository doctorsForDBRepository;

    @Autowired
    private SnapshotRepository snapshotRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private DeferralReasonService deferralReasonService;

    private String proposedOutcomeCode1, proposedOutcomeCode2;
    private String deferralDate1, deferralDate2;
    private String deferralReason1, deferralReason2;
    private String deferralSubReason1, deferralSubReason2;
    private String deferralComment1, deferralComment2;
    private String revalidationStatusCode1, revalidationStatusCode2;
    private String gmcSubmissionDateTime1, gmcSubmissionDateTime2;
    private String gmcSubmissionReturnCode1, gmcSubmissionReturnCode2;
    private String gmcRecommendationId1, gmcRecommendationId2;
    private String gmcOutcomeCode1, gmcOutcomeCode2;
    private String gmcStatusCheckDateTime1, gmcStatusCheckDateTime2;
    private String admin1, admin2;
    private String submissionDate1, submissionDate2;
    private String recommendationSubmitter1, recommendationSubmitter2;
    private String dateAdded1, dateAdded2;
    private String snapshotRevalidationId1, snapshotRevalidationId2;

    private Snapshot snapshot1, snapshot2;
    private SnapshotRevalidation snapshotRevalidation1, snapshotRevalidation2;

    @Before
    public void setup() {
        doctorsForDBRepository.deleteAll();
        snapshotRepository.deleteAll();
        recommendationRepository.deleteAll();
        setupData();
        setupSnapshotData();
     }

    @Test
    public void shouldReturnCoreDataForTrainee() throws Exception {
        doctorsForDBRepository.saveAll(List.of(doc1));
        final var coreData = of(gmcRef1, coreDTO1);
        stubCoreRequest(coreData);

        final var recommendation = recommendationService.getTraineeInfo(gmcRef1);

        assertThat(recommendation.getGmcNumber(), is(gmcRef1));
        assertThat(recommendation.getFullName(), is(fullName(fName1, lName1)));
        assertThat(recommendation.getCctDate(), is(cctDate1));
        assertThat(recommendation.getProgrammeMembershipType(), is(memType1));
        assertThat(recommendation.getCurrentGrade(), is(grade1));

        assertThat(recommendation.getRevalidations(), hasSize(2));
        var revalidationDTO = recommendation.getRevalidations().get(0);
        assertThat(revalidationDTO.getDeferralComment(), is(deferralComment1));
        assertThat(revalidationDTO.getDeferralDate(), is(formatDate(deferralDate1)));
        assertThat(revalidationDTO.getDeferralReason(), is(deferralReason1));
        assertThat(revalidationDTO.getAdmin(), is(admin1));
        assertThat(revalidationDTO.getActualSubmissionDate(), is(formatDate(submissionDate1)));
        assertThat(revalidationDTO.getGmcSubmissionDate(), is(formatDateTime(gmcSubmissionDateTime1)));
        assertThat(revalidationDTO.getRecommendationStatus(), is(revalidationStatusCode1));
        assertThat(revalidationDTO.getRecommendationType(), is(proposedOutcomeCode1));
        assertThat(revalidationDTO.getGmcOutcome(), is(gmcOutcomeCode1));
        assertThat(revalidationDTO.getRecommendationId(), is(snapshotRevalidationId1));

        revalidationDTO = recommendation.getRevalidations().get(1);
        assertThat(revalidationDTO.getDeferralComment(), is(deferralComment2));
        assertThat(revalidationDTO.getDeferralDate(), is(formatDate(deferralDate2)));
        assertThat(revalidationDTO.getDeferralReason(), is(deferralReason2));
        assertThat(revalidationDTO.getAdmin(), is(admin2));
        assertThat(revalidationDTO.getActualSubmissionDate(), is(formatDate(submissionDate2)));
        assertThat(revalidationDTO.getGmcSubmissionDate(), is(formatDateTime(gmcSubmissionDateTime2)));
        assertThat(revalidationDTO.getRecommendationStatus(), is(revalidationStatusCode2));
        assertThat(revalidationDTO.getRecommendationType(), is(proposedOutcomeCode2));
        assertThat(revalidationDTO.getGmcOutcome(), is(gmcOutcomeCode2));
        assertThat(revalidationDTO.getRecommendationId(), is(snapshotRevalidationId2));
    }

    @Test
    public void shouldReturnRecommendationWhenTcsReturn404() throws Exception {
        doctorsForDBRepository.saveAll(List.of(doc1));
        stubCoreRequestReturn404();

        final var recommendation = recommendationService.getTraineeInfo(gmcRef1);

        assertThat(recommendation.getGmcNumber(), is(gmcRef1));
        assertThat(recommendation.getFullName(), is(fullName(fName1, lName1)));
        assertThat(recommendation.getCctDate(), is(nullValue()));
        assertThat(recommendation.getProgrammeMembershipType(), is(nullValue()));
        assertThat(recommendation.getCurrentGrade(), is(nullValue()));
    }

    @Test
    public void shouldReturnRecommendationWhenTcsReturn400() throws Exception {
        doctorsForDBRepository.saveAll(List.of(doc1));
        stubCoreRequestReturn400();

        final var recommendation = recommendationService.getTraineeInfo(gmcRef1);

        assertThat(recommendation.getGmcNumber(), is(gmcRef1));
        assertThat(recommendation.getFullName(), is(fullName(fName1, lName1)));
        assertThat(recommendation.getCctDate(), is(nullValue()));
        assertThat(recommendation.getProgrammeMembershipType(), is(nullValue()));
        assertThat(recommendation.getCurrentGrade(), is(nullValue()));
    }

    @Test
    public void shouldReturnRecommendationWhenTcsReturn500() throws Exception {
        doctorsForDBRepository.saveAll(List.of(doc1));
        stubCoreRequestReturn500();

        final var recommendation = recommendationService.getTraineeInfo(gmcRef1);

        assertThat(recommendation.getGmcNumber(), is(gmcRef1));
        assertThat(recommendation.getFullName(), is(fullName(fName1, lName1)));
        assertThat(recommendation.getCctDate(), is(nullValue()));
        assertThat(recommendation.getProgrammeMembershipType(), is(nullValue()));
        assertThat(recommendation.getCurrentGrade(), is(nullValue()));
    }

    @Test
    public void shouldGetTraineeListOfRecommendation() throws JsonProcessingException {
        doctorsForDBRepository.saveAll(List.of(doc3));
        final var coreData = of(gmcRef3, coreDTO2);
        stubCoreRequest(coreData);
        final var recordDTO1 = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcRef3)
                .recommendationType(REVALIDATE.name())
                .comments(List.of("recommendation without outcome"))
                .build();

        final var recordDTO3 = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcRef3)
                .recommendationType(REVALIDATE.name())
                .recommendationStatus(SUBMITTED_TO_GMC.name())
                .gmcOutcome(APPROVED.getOutcome())
                .comments(List.of("recommendation with approve outcome"))
                .build();


        var recommendation = recommendationService.saveRecommendation(recordDTO3);
        recommendationService.submitRecommendation(recommendation.getId(), gmcRef3);
        recommendation = recommendationRepository.findById(recommendation.getId()).get();
        recommendation.setOutcome(APPROVED);
        recommendationRepository.save(recommendation);
        recommendationService.saveRecommendation(recordDTO1);
        final var recommendations = recommendationService.getTraineeInfo(gmcRef3);
        assertThat(recommendations.getRevalidations(), hasSize(1));
        final var traineeRecommendationRecordDto = recommendations.getRevalidations().get(0);
        assertThat(traineeRecommendationRecordDto.getRecommendationType(), is(REVALIDATE.name()));
        assertThat(traineeRecommendationRecordDto.getGmcOutcome(), is(nullValue()));
        assertThat(traineeRecommendationRecordDto.getComments(), contains("recommendation without outcome"));
    }

    @Test
    public void shouldSaveRecommendationOfTypeRevalidate() {
        doctorsForDBRepository.saveAll(List.of(doc1));
        final var recordDTO = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcRef1)
                .recommendationType(REVALIDATE.name())
                .comments(List.of("recommendation comments"))
                .build();

        final var recommendation = recommendationService.saveRecommendation(recordDTO);
        final var recommendationById = recommendationRepository.findById(recommendation.getId());
        final var savedRecommendation = recommendationById.get();
        assertNotNull(savedRecommendation);
        assertThat(savedRecommendation.getGmcNumber(), is(gmcRef1));
        assertThat(savedRecommendation.getRecommendationType(), is(REVALIDATE));
        assertThat(savedRecommendation.getGmcSubmissionDate(), is(subDate1));
    }

    @Test
    public void shouldSaveRecommendationOfTypeNonEngagement() {
        doctorsForDBRepository.saveAll(List.of(doc2));
        final var recordDTO = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcRef2)
                .recommendationType(NON_ENGAGEMENT.name())
                .comments(List.of("recommendation comments"))
                .build();

        final var recommendation = recommendationService.saveRecommendation(recordDTO);
        final var recommendationById = recommendationRepository.findById(recommendation.getId());
        final var savedRecommendation = recommendationById.get();
        assertNotNull(savedRecommendation);
        assertThat(savedRecommendation.getGmcNumber(), is(gmcRef2));
        assertThat(savedRecommendation.getRecommendationType(), is(NON_ENGAGEMENT));
        assertThat(savedRecommendation.getGmcSubmissionDate(), is(subDate2));
    }

    @Test
    public void shouldSaveRecommendationOfTypeDefer() {
        doctorsForDBRepository.saveAll(List.of(doc2));
        final var deferralDate = subDate2.plusDays(70);
        final var recordDTO = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcRef2)
                .recommendationType(DEFER.name())
                .deferralDate(deferralDate)
                .deferralReason(deferralReason1)
                .deferralSubReason(deferralSubReason1)
                .comments(List.of("recommendation comments"))
                .build();

        final var recommendation = recommendationService.saveRecommendation(recordDTO);
        final var recommendationById = recommendationRepository.findById(recommendation.getId());
        final var savedRecommendation = recommendationById.get();
        assertNotNull(savedRecommendation);
        assertThat(savedRecommendation.getGmcNumber(), is(gmcRef2));
        assertThat(savedRecommendation.getRecommendationType(), is(DEFER));
        assertThat(savedRecommendation.getDeferralDate(), is(deferralDate));
        assertThat(savedRecommendation.getDeferralReason(), is(deferralReason1));
        assertThat(savedRecommendation.getDeferralSubReason(), is(deferralSubReason1));
        assertThat(savedRecommendation.getGmcSubmissionDate(), is(subDate2));
    }

    @Test
    public void shouldSubmitRevalidateRecommendation() {
        doctorsForDBRepository.saveAll(List.of(doc1));
        final var recordDTO = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcRef1)
                .recommendationType(REVALIDATE.name())
                .comments(List.of("recommendation comments"))
                .build();

        final var recommendation = recommendationService.saveRecommendation(recordDTO);

        final var submitRecommendation = recommendationService.submitRecommendation(recommendation.getId(), gmcRef1);
        assertTrue(submitRecommendation);
        //check if status is changes
        final var recommendationById = recommendationRepository.findById(recommendation.getId());
        assertTrue(recommendationById.isPresent());
        final var savedRecommendation = recommendationById.get();
        assertThat(savedRecommendation.getRecommendationStatus(), is(SUBMITTED_TO_GMC));
    }

    @Test
    public void shouldSubmitDeferRecommendation() {
        doctorsForDBRepository.saveAll(List.of(doc1));
        final var deferralReasonByCode = deferralReasonService.getDeferralReasonByCode("1");
        final var deferralSubReason = deferralReasonByCode.getDeferralSubReasons().get(0);
        final var deferralDate = subDate1.plusDays(70);
        final var recordDTO = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcRef1)
                .recommendationType(DEFER.name())
                .comments(List.of("recommendation comments"))
                .deferralReason(deferralReasonByCode.getCode())
                .deferralSubReason(deferralSubReason.getCode())
                .deferralDate(deferralDate)
                .build();

        final var recommendation = recommendationService.saveRecommendation(recordDTO);

        final var submitRecommendation = recommendationService.submitRecommendation(recommendation.getId(), gmcRef1);
        assertTrue(submitRecommendation);
        //check if status is changes
        final var recommendationById = recommendationRepository.findById(recommendation.getId());
        assertTrue(recommendationById.isPresent());
        final var savedRecommendation = recommendationById.get();
        assertThat(savedRecommendation.getRecommendationStatus(), is(SUBMITTED_TO_GMC));
        assertNotNull(savedRecommendation.getOutcome());
    }

    @Test
    public void shouldUpdateRecommendationOfTypeRevalidate() {
        doctorsForDBRepository.saveAll(List.of(doc1));
        final var recordDTO = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcRef1)
                .recommendationType(REVALIDATE.name())
                .comments(List.of("recommendation comments"))
                .build();
        final var savedRecommendation = recommendationService.saveRecommendation(recordDTO);
        assertNotNull(savedRecommendation);
        assertThat(savedRecommendation.getGmcNumber(), is(gmcRef1));
        assertThat(savedRecommendation.getRecommendationType(), is(REVALIDATE));
        assertThat(savedRecommendation.getGmcSubmissionDate(), is(subDate1));
        assertThat(savedRecommendation.getRecommendationStatus(), is(READY_TO_REVIEW));
        assertThat(savedRecommendation.getComments(), is(List.of("recommendation comments")));

        final var updateRecordDTO = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcRef1)
                .recommendationId(savedRecommendation.getId())
                .recommendationType(REVALIDATE.name())
                .comments(List.of("recommendation comments", "new comments"))
                .build();

        final var updatedRecommendation = recommendationService.updateRecommendation(updateRecordDTO);
        assertNotNull(updatedRecommendation);
        assertThat(updatedRecommendation.getGmcNumber(), is(gmcRef1));
        assertThat(updatedRecommendation.getRecommendationType(), is(REVALIDATE));
        assertThat(updatedRecommendation.getGmcSubmissionDate(), is(subDate1));
        assertThat(updatedRecommendation.getRecommendationStatus(), is(READY_TO_REVIEW));
        assertThat(updatedRecommendation.getComments(), is(List.of("recommendation comments", "new comments")));
    }

    @Test (expected = RecommendationException.class)
    public void shouldNotAllowToCreateRecommendationWhenOneAlreadyInDraft() {
        doctorsForDBRepository.saveAll(List.of(doc1));
        final var recordDTO = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcRef1)
                .recommendationType(REVALIDATE.name())
                .comments(List.of("recommendation comments"))
                .build();
        final var savedRecommendation = recommendationService.saveRecommendation(recordDTO);
        assertNotNull(savedRecommendation);
        assertThat(savedRecommendation.getGmcNumber(), is(gmcRef1));
        assertThat(savedRecommendation.getRecommendationType(), is(REVALIDATE));
        assertThat(savedRecommendation.getGmcSubmissionDate(), is(subDate1));
        assertThat(savedRecommendation.getRecommendationStatus(), is(READY_TO_REVIEW));
        assertThat(savedRecommendation.getComments(), is(List.of("recommendation comments")));

        final var newRecortDTO = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcRef1)
                .recommendationType(REVALIDATE.name())
                .comments(List.of("recommendation comments", "new comments"))
                .build();

        final var updatedRecommendation = recommendationService.saveRecommendation(newRecortDTO);

    }

    @Test (expected = RecommendationException.class)
    public void shouldNotAllowToCreateRecommendationWhenSubmitToGmcButStillUnderReview() {
        doctorsForDBRepository.saveAll(List.of(doc1));
        final var recordDTO = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcRef1)
                .recommendationType(REVALIDATE.name())
                .comments(List.of("recommendation comments"))
                .build();
        var savedRecommendation = recommendationService.saveRecommendation(recordDTO);
        assertNotNull(savedRecommendation);
        assertThat(savedRecommendation.getGmcNumber(), is(gmcRef1));
        assertThat(savedRecommendation.getRecommendationType(), is(REVALIDATE));
        assertThat(savedRecommendation.getGmcSubmissionDate(), is(subDate1));
        assertThat(savedRecommendation.getRecommendationStatus(), is(READY_TO_REVIEW));
        assertThat(savedRecommendation.getComments(), is(List.of("recommendation comments")));
        recommendationService.submitRecommendation(savedRecommendation.getId(), gmcRef1);

        final var newRecortDTO = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcRef1)
                .recommendationType(REVALIDATE.name())
                .comments(List.of("recommendation comments", "new comments"))
                .build();

        recommendationService.saveRecommendation(newRecortDTO);

    }

    private void setupSnapshotData() {
        proposedOutcomeCode1 = faker.options().option(RecommendationType.class).name();
        deferralDate1 = "2018-03-15";
        deferralReason1 = "1";
        deferralSubReason1 = "1";
        deferralComment1 = faker.lorem().sentence(5);
        revalidationStatusCode1 = faker.options().option(RecommendationStatus.class).name();
        gmcSubmissionDateTime1 = "2018-03-15 12:00:00";
        gmcSubmissionReturnCode1 = "0";
        gmcRecommendationId1 = faker.idNumber().toString();
        gmcOutcomeCode1 = getGmcOutCome(gmcRef1);
        gmcStatusCheckDateTime1 = "2018-03-15";
        admin1 = faker.name().fullName();
        submissionDate1 = "2018-03-15";
        recommendationSubmitter1 = admin1;
        dateAdded1 = "2018-04-15";
        snapshotRevalidationId1 = faker.number().digits(10);

        proposedOutcomeCode2 = faker.options().option(RecommendationType.class).name();
        deferralDate2 = "2018-03-15";
        deferralReason2 = "2";
        deferralSubReason2 = null;
        deferralComment2 = faker.lorem().sentence(5);
        revalidationStatusCode2 = faker.options().option(RecommendationStatus.class).name();
        gmcSubmissionDateTime2 = "2018-03-15 12:00:00";
        gmcSubmissionReturnCode2 = "0";
        gmcRecommendationId2 = faker.idNumber().toString();
        gmcOutcomeCode2 = getGmcOutCome(gmcRef1);
        gmcStatusCheckDateTime2 = "2018-03-15";
        admin2 = faker.name().fullName();
        submissionDate2 = "2018-03-15";
        recommendationSubmitter2 = admin1;
        dateAdded2 = "2018-04-15";
        snapshotRevalidationId2 = faker.number().digits(10);

        snapshotRevalidation1 = new SnapshotRevalidation(snapshotRevalidationId1, proposedOutcomeCode1, deferralDate1, deferralReason1, deferralSubReason1,
                deferralComment1, revalidationStatusCode1, gmcSubmissionDateTime1, gmcSubmissionReturnCode1, gmcRecommendationId1, gmcOutcomeCode1,
                gmcStatusCheckDateTime1, admin1, submissionDate1, recommendationSubmitter1, dateAdded1, List.of());

        snapshotRevalidation2 = new SnapshotRevalidation(snapshotRevalidationId2, proposedOutcomeCode2, deferralDate2, deferralReason2, deferralSubReason2,
                deferralComment2, revalidationStatusCode2, gmcSubmissionDateTime2, gmcSubmissionReturnCode2, gmcRecommendationId2, gmcOutcomeCode2,
                gmcStatusCheckDateTime2, admin2, submissionDate2, recommendationSubmitter2, dateAdded2, List.of());

        snapshot1 = new Snapshot(UUID.randomUUID().toString(), null, null, null, gmcRef1, snapshotRevalidation1);
        snapshot2 = new Snapshot(UUID.randomUUID().toString(), null, null, null, gmcRef1, snapshotRevalidation2);

        snapshotRepository.saveAll(List.of(snapshot1, snapshot2));
    }

    private String fullName(final String fName, final String lName) {
        return String.format("%s %s", fName, lName);
    }

    //GMC Mock has this logic to randomly return the gmc outcome status so using the same assumptions here.
    private String getGmcOutCome(final String gmcReferenceNumber) {
        final String lastDigit = gmcReferenceNumber.substring(gmcReferenceNumber.length() - 1);

        if (lastDigit.matches("[012]")) {
            return UNDER_REVIEW.getOutcome();
        } else if (lastDigit.matches("[345]")) {
            return REJECTED.getOutcome();
        } else {
            return APPROVED.getOutcome();
        }
    }
}
