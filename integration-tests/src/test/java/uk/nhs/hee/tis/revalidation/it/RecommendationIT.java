package uk.nhs.hee.tis.revalidation.it;

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
import uk.nhs.hee.tis.revalidation.dto.RecommendationDTO;
import uk.nhs.hee.tis.revalidation.entity.*;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.repository.SnapshotRepository;
import uk.nhs.hee.tis.revalidation.service.RecommendationService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static java.util.Map.of;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RevalidationApplication.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
public class RecommendationIT extends BaseIT {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private DoctorsForDBRepository doctorsForDBRepository;

    @Autowired
    private SnapshotRepository snapshotRepository;

    private String proposedOutcomeCode1, proposedOutcomeCode2;
    private String deferralDate1, deferralDate2;
    private String deferralReason1, deferralReason2;
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

    private Snapshot snapshot1, snapshot2;
    private SnapshotRevalidation snapshotRevalidation1, snapshotRevalidation2;

    @Before
    public void setup() {
        doctorsForDBRepository.deleteAll();
        snapshotRepository.deleteAll();
        setupData();
        setupSnapshotData();
     }

    @Test
    public void shouldReturnCoreDataForTrainee() throws Exception {
        doctorsForDBRepository.saveAll(List.of(doc1));
        final var coreData = of(gmcRef1, coreDTO1);
        stubCoreRequest(coreData);

        final RecommendationDTO recommendation = recommendationService.getTraineeInfo(gmcRef1);

        assertThat(recommendation.getGmcNumber(), is(gmcRef1));
        assertThat(recommendation.getFullName(), is(fullName(fName1, lName1)));
        assertThat(recommendation.getCctDate(), is(cctDate1));
        assertThat(recommendation.getProgrammeMembershipType(), is(memType1));
        assertThat(recommendation.getCurrentGrade(), is(grade1));

        assertThat(recommendation.getRevalidations(), hasSize(2));
        var revalidationDTO = recommendation.getRevalidations().get(0);
        assertThat(revalidationDTO.getDeferralComment(), is(deferralComment1));
        assertThat(revalidationDTO.getDeferralDate(), is(deferralDate1));
        assertThat(revalidationDTO.getDeferralReason(), is(deferralReason1));
        assertThat(revalidationDTO.getAdmin(), is(admin1));
        assertThat(revalidationDTO.getActualSubmissionDate(), is(SIMPLE_DATE_FORMAT.parse(submissionDate1)));
        assertThat(revalidationDTO.getGmcSubmissionDate(), is(SIMPLE_DATE_FORMAT.parse(gmcSubmissionDateTime1)));
        assertThat(revalidationDTO.getRevalidationStatus(), is(revalidationStatusCode1));
        assertThat(revalidationDTO.getRevalidationType(), is(proposedOutcomeCode1));
        assertThat(revalidationDTO.getGmcOutcome(), is(gmcOutcomeCode1));

        revalidationDTO = recommendation.getRevalidations().get(1);
        assertThat(revalidationDTO.getDeferralComment(), is(deferralComment2));
        assertThat(revalidationDTO.getDeferralDate(), is(deferralDate2));
        assertThat(revalidationDTO.getDeferralReason(), is(deferralReason2));
        assertThat(revalidationDTO.getAdmin(), is(admin2));
        assertThat(revalidationDTO.getActualSubmissionDate(), is(SIMPLE_DATE_FORMAT.parse(submissionDate2)));
        assertThat(revalidationDTO.getGmcSubmissionDate(), is(SIMPLE_DATE_FORMAT.parse(gmcSubmissionDateTime2)));
        assertThat(revalidationDTO.getRevalidationStatus(), is(revalidationStatusCode2));
        assertThat(revalidationDTO.getRevalidationType(), is(proposedOutcomeCode2));
        assertThat(revalidationDTO.getGmcOutcome(), is(gmcOutcomeCode2));
    }

    @Test
    public void shouldReturnRecommendationWhenTcsReturn404() throws Exception {
        doctorsForDBRepository.saveAll(List.of(doc1));
        stubCoreRequestReturn404();

        final RecommendationDTO recommendation = recommendationService.getTraineeInfo(gmcRef1);

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

        final RecommendationDTO recommendation = recommendationService.getTraineeInfo(gmcRef1);

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

        final RecommendationDTO recommendation = recommendationService.getTraineeInfo(gmcRef1);

        assertThat(recommendation.getGmcNumber(), is(gmcRef1));
        assertThat(recommendation.getFullName(), is(fullName(fName1, lName1)));
        assertThat(recommendation.getCctDate(), is(nullValue()));
        assertThat(recommendation.getProgrammeMembershipType(), is(nullValue()));
        assertThat(recommendation.getCurrentGrade(), is(nullValue()));
    }

    public void setupSnapshotData() {
        proposedOutcomeCode1 = faker.options().option(RevalidationType.class).name();
        deferralDate1 = "2018-03-15";
        deferralReason1 = faker.options().option(DeferralReason.class).name();
        deferralComment1 = faker.lorem().sentence(5);
        revalidationStatusCode1 = faker.options().option(RevalidationStatus.class).name();
        gmcSubmissionDateTime1 = "2018-03-15";
        gmcSubmissionReturnCode1 = "0";
        gmcRecommendationId1 = faker.idNumber().toString();
        gmcOutcomeCode1 = faker.options().option(RevalidationGmcOutcome.class).name();
        gmcStatusCheckDateTime1 = "2018-03-15";
        admin1 = faker.name().fullName();
        submissionDate1 = "2018-03-15";
        recommendationSubmitter1 = admin1;
        dateAdded1 = "2018-04-15";

        proposedOutcomeCode2 = faker.options().option(RevalidationType.class).name();
        deferralDate2 = "2018-03-15";
        deferralReason2 = faker.options().option(DeferralReason.class).name();
        deferralComment2 = faker.lorem().sentence(5);
        revalidationStatusCode2 = faker.options().option(RevalidationStatus.class).name();
        gmcSubmissionDateTime2 = "2018-03-15";
        gmcSubmissionReturnCode2 = "0";
        gmcRecommendationId2 = faker.idNumber().toString();
        gmcOutcomeCode2 = faker.options().option(RevalidationGmcOutcome.class).name();
        gmcStatusCheckDateTime2 = "2018-03-15";
        admin2 = faker.name().fullName();
        submissionDate2 = "2018-03-15";
        recommendationSubmitter2 = admin1;
        dateAdded2 = "2018-04-15";

        snapshotRevalidation1 = new SnapshotRevalidation(proposedOutcomeCode1, deferralDate1, deferralReason1, deferralComment1,
                revalidationStatusCode1, gmcSubmissionDateTime1, gmcSubmissionReturnCode1, gmcRecommendationId1, gmcOutcomeCode1,
                gmcStatusCheckDateTime1, admin1, submissionDate1, recommendationSubmitter1, dateAdded1);

        snapshotRevalidation2 = new SnapshotRevalidation(proposedOutcomeCode2, deferralDate2, deferralReason2, deferralComment2,
                revalidationStatusCode2, gmcSubmissionDateTime2, gmcSubmissionReturnCode2, gmcRecommendationId2, gmcOutcomeCode2,
                gmcStatusCheckDateTime2, admin2, submissionDate2, recommendationSubmitter2, dateAdded2);

        snapshot1 = new Snapshot(UUID.randomUUID().toString(), null, null, null, gmcRef1, snapshotRevalidation1);
        snapshot2 = new Snapshot(UUID.randomUUID().toString(), null, null, null, gmcRef1, snapshotRevalidation2);

        snapshotRepository.saveAll(List.of(snapshot1, snapshot2));
    }

    private String fullName(final String fName, final String lName) {
        return String.format("%s %s", fName, lName);
    }
}
