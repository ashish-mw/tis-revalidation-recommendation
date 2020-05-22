package uk.nhs.hee.tis.revalidation.service;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.hee.tis.revalidation.dto.TraineeCoreDTO;
import uk.nhs.hee.tis.revalidation.entity.*;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.repository.SnapshotRepository;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.formatDate;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.formatDateTime;

@RunWith(MockitoJUnitRunner.class)
public class RecommendationServiceTest {

    private final Faker faker = new Faker();

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private TraineeCoreService traineeCoreService;

    @Mock
    private DoctorsForDBRepository doctorsForDBRepository;

    @Mock
    private SnapshotRepository snapshotRepository;

    @Mock
    private Snapshot snapshot1;

    @Mock
    private Snapshot snapshot2;

    @Mock
    private SnapshotRevalidation snapshotRevalidation1;

    @Mock
    private SnapshotRevalidation snapshotRevalidation2;

    @Mock
    private TraineeCoreDTO traineeCoreDTO;

    private String firstName;
    private String lastName;
    private LocalDate submissionDate;
    private LocalDate dateAdded;
    private UnderNotice underNotice;
    private String sanction;
    private RevalidationStatus status;
    private LocalDate cctDate;
    private String programmeName;
    private String programmeMembershipType;
    private String currentGrade;

    private String deferralComment1, deferralComment2;
    private String deferralDate1, deferralDate2;
    private String deferralResaon1, deferralResaon2;
    private String gmcOutcome1, gmcOutcome2;
    private String revalidatonType1, revalidatonType2;
    private String revalidationStatus1, revalidationStatus2;
    private String gmcSubmissionDate1, gmcSubmissionDate2;
    private String acutalSubmissionDate1, acutalSubmissionDate2;
    private String admin1, admin2;

    @Before
    public void setup() {
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        status = RevalidationStatus.NOT_STARTED;
        submissionDate = LocalDate.now();
        dateAdded = LocalDate.now();
        underNotice = UnderNotice.NO;
        sanction = faker.lorem().characters(2);
        cctDate = LocalDate.now();
        programmeName = faker.lorem().sentence(3);
        programmeMembershipType = faker.lorem().characters(10);
        currentGrade = faker.lorem().characters(5);

        deferralComment1 = faker.lorem().characters(20);
        deferralDate1 = faker.date().toString();
        deferralResaon1 = faker.options().option(DeferralReason.class).name();
        revalidatonType1 = faker.options().option(RevalidationType.class).name();
        revalidationStatus1 = faker.options().option(RevalidationStatus.class).name();
        gmcOutcome1 = RevalidationGmcOutcome.APPROVED.name();
        gmcSubmissionDate1 = "2018-03-15 12:00:00";
        acutalSubmissionDate1 = "2018-03-15";
        admin1 = faker.funnyName().name();

        deferralComment2 = faker.lorem().characters(20);
        deferralDate2 = faker.date().toString();
        deferralResaon2 = faker.options().option(DeferralReason.class).name();
        revalidatonType2 = faker.options().option(RevalidationType.class).name();
        revalidationStatus2 = faker.options().option(RevalidationStatus.class).name();
        gmcOutcome2 = RevalidationGmcOutcome.APPROVED.name();
        gmcSubmissionDate2 = "2018-03-15 12:00:00";
        acutalSubmissionDate2 = "2018-03-15";
        admin2 = faker.funnyName().name();
    }

    @Test
    public void shouldReturnRecommendationWithCurrentAndLegacyRevalidations() throws ParseException {
        final var gmcId = faker.number().digits(8);
        when(doctorsForDBRepository.findById(gmcId)).thenReturn(of(buildDoctorForDB(gmcId)));
        when(traineeCoreService.getTraineeInformationFromCore(List.of(gmcId))).thenReturn(Map.of(gmcId, traineeCoreDTO));
        when(snapshotRepository.findByGmcNumber(gmcId)).thenReturn(List.of(snapshot1, snapshot2));
        when(traineeCoreDTO.getCctDate()).thenReturn(cctDate);
        when(traineeCoreDTO.getProgrammeMembershipType()).thenReturn(programmeMembershipType);
        when(traineeCoreDTO.getCurrentGrade()).thenReturn(currentGrade);

        when(snapshot1.getRevalidation()).thenReturn(snapshotRevalidation1);
        when(snapshotRevalidation1.getAdmin()).thenReturn(admin1);
        when(snapshotRevalidation1.getDeferralComment()).thenReturn(deferralComment1);
        when(snapshotRevalidation1.getDeferralReason()).thenReturn(deferralResaon1);
        when(snapshotRevalidation1.getDeferralDate()).thenReturn(deferralDate1);
        when(snapshotRevalidation1.getRevalidationStatusCode()).thenReturn(revalidationStatus1);
        when(snapshotRevalidation1.getProposedOutcomeCode()).thenReturn(revalidatonType1);
        when(snapshotRevalidation1.getGmcSubmissionDateTime()).thenReturn(gmcSubmissionDate1);
        when(snapshotRevalidation1.getSubmissionDate()).thenReturn(acutalSubmissionDate1);

        when(snapshot2.getRevalidation()).thenReturn(snapshotRevalidation2);
        when(snapshotRevalidation2.getAdmin()).thenReturn(admin2);
        when(snapshotRevalidation2.getDeferralComment()).thenReturn(deferralComment2);
        when(snapshotRevalidation2.getDeferralReason()).thenReturn(deferralResaon2);
        when(snapshotRevalidation2.getDeferralDate()).thenReturn(deferralDate2);
        when(snapshotRevalidation2.getRevalidationStatusCode()).thenReturn(revalidationStatus2);
        when(snapshotRevalidation2.getProposedOutcomeCode()).thenReturn(revalidatonType2);
        when(snapshotRevalidation2.getGmcSubmissionDateTime()).thenReturn(gmcSubmissionDate2);
        when(snapshotRevalidation2.getSubmissionDate()).thenReturn(acutalSubmissionDate2);

        final var recommendation = recommendationService.getTraineeInfo(gmcId);
        assertThat(recommendation.getGmcNumber(), is(gmcId));
        assertThat(recommendation.getFullName(), is(getFullName(firstName, lastName)));
        assertThat(recommendation.getCctDate(), is(cctDate));
        assertThat(recommendation.getProgrammeMembershipType(), is(programmeMembershipType));
        assertThat(recommendation.getCurrentGrade(), is(currentGrade));

        assertThat(recommendation.getRevalidations(), hasSize(2));
        var revalidationDTO = recommendation.getRevalidations().get(0);
        assertThat(revalidationDTO.getDeferralReason(), is(deferralResaon1));
        assertThat(revalidationDTO.getDeferralDate(), is(deferralDate1));
        assertThat(revalidationDTO.getDeferralComment(), is(deferralComment1));
        assertThat(revalidationDTO.getAdmin(), is(admin1));
        assertThat(revalidationDTO.getGmcOutcome(), is(gmcOutcome1));
        assertThat(revalidationDTO.getRevalidationType(), is(revalidatonType1));
        assertThat(revalidationDTO.getRevalidationStatus(), is(revalidationStatus1));
        assertThat(revalidationDTO.getGmcSubmissionDate(), is(formatDateTime(gmcSubmissionDate1)));
        assertThat(revalidationDTO.getActualSubmissionDate(), is(formatDate(acutalSubmissionDate1)));

        revalidationDTO = recommendation.getRevalidations().get(1);
        assertThat(revalidationDTO.getDeferralReason(), is(deferralResaon2));
        assertThat(revalidationDTO.getDeferralDate(), is(deferralDate2));
        assertThat(revalidationDTO.getDeferralComment(), is(deferralComment2));
        assertThat(revalidationDTO.getAdmin(), is(admin2));
        assertThat(revalidationDTO.getGmcOutcome(), is(gmcOutcome2));
        assertThat(revalidationDTO.getRevalidationType(), is(revalidatonType2));
        assertThat(revalidationDTO.getRevalidationStatus(), is(revalidationStatus2));
        assertThat(revalidationDTO.getGmcSubmissionDate(), is(formatDateTime(gmcSubmissionDate2)));
        assertThat(revalidationDTO.getActualSubmissionDate(), is(formatDate(acutalSubmissionDate2)));

    }

    @Test
    public void shouldReturnRecommendationWithCurrentAndLegacyRevalidationsWithoutTisCoreInformation() {
        final var gmcId = faker.number().digits(8);
        when(doctorsForDBRepository.findById(gmcId)).thenReturn(of(buildDoctorForDB(gmcId)));
        when(snapshotRepository.findByGmcNumber(gmcId)).thenReturn(List.of());
        when(traineeCoreService.getTraineeInformationFromCore(List.of(gmcId))).thenReturn(Map.of());
        final var recommendation = recommendationService.getTraineeInfo(gmcId);
        assertThat(recommendation.getGmcNumber(), is(gmcId));
        assertThat(recommendation.getFullName(), is(getFullName(firstName, lastName)));
        assertThat(recommendation.getCctDate(), is(nullValue()));
        assertThat(recommendation.getProgrammeMembershipType(), is(nullValue()));
        assertThat(recommendation.getCurrentGrade(), is(nullValue()));
        assertThat(recommendation.getRevalidations(), hasSize(0));
    }

    private DoctorsForDB buildDoctorForDB(final String gmcId) {
        return DoctorsForDB.builder()
                .gmcReferenceNumber(gmcId)
                .doctorFirstName(firstName)
                .doctorLastName(lastName)
                .doctorStatus(status)
                .submissionDate(submissionDate)
                .dateAdded(dateAdded)
                .underNotice(underNotice)
                .sanction(sanction)
                .build();

    }

    private String getFullName(final String firstName, final String lastName) {
        return String.format("%s %s", firstName, lastName);
    }
 }