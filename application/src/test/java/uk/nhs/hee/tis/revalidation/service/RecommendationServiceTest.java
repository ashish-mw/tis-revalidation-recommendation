package uk.nhs.hee.tis.revalidation.service;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.hee.tis.revalidation.dto.TraineeCoreDTO;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

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
    private TraineeCoreDTO traineeCoreDTO;

    private String firstName;
    private String lastName;
    private LocalDate submissionDate;
    private LocalDate dateAdded;
    private UnderNotice underNotice;
    private String sanction;
    private String status;
    private LocalDate cctDate;
    private String programmeName;
    private String programmeMembershipType;
    private String currentGrade;

    @Before
    public void setup() {
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        status = faker.lorem().characters(5);
        submissionDate = LocalDate.now();
        dateAdded = LocalDate.now();
        underNotice = UnderNotice.NO;
        sanction = faker.lorem().characters(2);
        cctDate = LocalDate.now();
        programmeName = faker.lorem().sentence(3);
        programmeMembershipType = faker.lorem().characters(10);
        currentGrade = faker.lorem().characters(5);
    }

    @Test
    public void shouldFetchDoctorForDBWithCoreInfoByGmcNumber() {
        final var gmcId = faker.number().digits(8);
        when(doctorsForDBRepository.findById(gmcId)).thenReturn(of(buildDoctorForDB(gmcId)));
        when(traineeCoreService.getTraineeInformationFromCore(List.of(gmcId))).thenReturn(Map.of(gmcId, traineeCoreDTO));
        when(traineeCoreDTO.getCctDate()).thenReturn(cctDate);
        when(traineeCoreDTO.getProgrammeName()).thenReturn(programmeName);
        when(traineeCoreDTO.getProgrammeMembershipType()).thenReturn(programmeMembershipType);
        when(traineeCoreDTO.getCurrentGrade()).thenReturn(currentGrade);
        final var traineeInfo = recommendationService.getTraineeInfo(gmcId);
        assertThat(traineeInfo.getGmcReferenceNumber(), is(gmcId));
        assertThat(traineeInfo.getDoctorFirstName(), is(firstName));
        assertThat(traineeInfo.getDoctorLastName(), is(lastName));
        assertThat(traineeInfo.getSubmissionDate(), is(submissionDate));
        assertThat(traineeInfo.getDateAdded(), is(dateAdded));
        assertThat(traineeInfo.getUnderNotice(), is(underNotice));
        assertThat(traineeInfo.getSanction(), is(sanction));
        assertThat(traineeInfo.getDoctorStatus(), is(status));
        assertThat(traineeInfo.getCctDate(), is(cctDate));
        assertThat(traineeInfo.getProgrammeName(), is(programmeName));
        assertThat(traineeInfo.getProgrammeMembershipType(), is(programmeMembershipType));
        assertThat(traineeInfo.getCurrentGrade(), is(currentGrade));
    }

    @Test
    public void shouldFetchDoctorForDBWithoutCoreInfoByGmcNumber() {
        final var gmcId = faker.number().digits(8);
        when(doctorsForDBRepository.findById(gmcId)).thenReturn(of(buildDoctorForDB(gmcId)));
        when(traineeCoreService.getTraineeInformationFromCore(List.of(gmcId))).thenReturn(Map.of());
        final var traineeInfo = recommendationService.getTraineeInfo(gmcId);
        assertThat(traineeInfo.getGmcReferenceNumber(), is(gmcId));
        assertThat(traineeInfo.getDoctorFirstName(), is(firstName));
        assertThat(traineeInfo.getDoctorLastName(), is(lastName));
        assertThat(traineeInfo.getSubmissionDate(), is(submissionDate));
        assertThat(traineeInfo.getDateAdded(), is(dateAdded));
        assertThat(traineeInfo.getUnderNotice(), is(underNotice));
        assertThat(traineeInfo.getSanction(), is(sanction));
        assertThat(traineeInfo.getDoctorStatus(), is(status));
        assertThat(traineeInfo.getCctDate(), is(nullValue()));
        assertThat(traineeInfo.getProgrammeName(), is(nullValue()));
        assertThat(traineeInfo.getProgrammeMembershipType(), is(nullValue()));
        assertThat(traineeInfo.getCurrentGrade(), is(nullValue()));
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
 }