package uk.nhs.hee.tis.revalidation.changelog;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.RecommendationStatus;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.service.RecommendationServiceImpl;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TisStatusCorrectionChangeLogTest {

    TisStatusCorrectionChangeLog changeLog;

    @Mock
    DoctorsForDBRepository doctorsForDBRepository;

    @Mock
    RecommendationServiceImpl recommendationService;

    @Captor
    ArgumentCaptor<DoctorsForDB> doctorCaptor;

    List<DoctorsForDB> doctors;

    DoctorsForDB doctor1;

    private final Faker faker = new Faker();

    @BeforeEach
    public void setup() {
        changeLog = new TisStatusCorrectionChangeLog();
        setupTestData();
    }

    @Test
    void shouldSetCorrectTisStatusForEachDoctor() {

        assert (doctor1.getDoctorStatus()).equals(RecommendationStatus.NOT_STARTED);
        when(doctorsForDBRepository.findAll()).thenReturn(doctors);
        when(recommendationService
            .getRecommendationStatusForTrainee(doctor1.getGmcReferenceNumber()))
            .thenReturn(RecommendationStatus.SUBMITTED_TO_GMC);

        changeLog.correctTisStatuses(
            doctorsForDBRepository,
            recommendationService
        );
        verify(doctorsForDBRepository).save(doctorCaptor.capture());
        assert (doctorCaptor.getValue().getDoctorStatus().equals(RecommendationStatus.SUBMITTED_TO_GMC));
    }

    private void setupTestData() {
        doctor1 = DoctorsForDB.builder()
            .gmcReferenceNumber(faker.idNumber().toString())
            .doctorFirstName(faker.name().firstName())
            .doctorLastName(faker.name().lastName())
            .submissionDate(LocalDate.now())
            .dateAdded(LocalDate.now())
            .underNotice(UnderNotice.NO)
            .sanction(faker.lorem().fixedString(5))
            .doctorStatus(RecommendationStatus.NOT_STARTED)
            .lastUpdatedDate(LocalDate.now())
            .designatedBodyCode(faker.lorem().fixedString(5))
            .build();
        doctors = List.of(doctor1);
    }
}
