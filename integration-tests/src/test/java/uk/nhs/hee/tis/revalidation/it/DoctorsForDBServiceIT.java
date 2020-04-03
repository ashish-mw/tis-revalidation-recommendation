package uk.nhs.hee.tis.revalidation.it;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.hee.tis.revalidation.RevalidationApplication;
import uk.nhs.hee.tis.revalidation.dto.RevalidationRequestDTO;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.service.DoctorsForDBService;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RevalidationApplication.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
public class DoctorsForDBServiceIT {

    private final Faker faker = new Faker();

    @Autowired
    private DoctorsForDBService service;

    @Autowired
    private DoctorsForDBRepository repository;

    private DoctorsForDB doc1, doc2, doc3, doc4, doc5;
    private String gmcRef1, gmcRef2, gmcRef3, gmcRef4, gmcRef5;
    private String fName1, fName2, fName3, fName4, fName5;
    private String lName1, lName2, lName3, lName4, lName5;
    private LocalDate subDate1, subDate2, subDate3, subDate4, subDate5;
    private LocalDate addedDate1, addedDate2, addedDate3, addedDate4, addedDate5;
    private UnderNotice un1, un2, un3, un4, un5;
    private String sanction1, sanction2, sanction3, sanction4, sanction5;
    private String status1, status2, status3, status4, status5;

    @Before
    public void setup() {
        repository.deleteAll();
        setupData();
    }

    @DisplayName("Trainee doctors information should be sorted by submission date in desc order")
    @Test
    public void shouldReturnDataSortBySubmissionDateInDescOrder() {
        subDate1 = LocalDate.now().minusDays(5);
        subDate2 = LocalDate.now().minusDays(2);
        subDate3 = LocalDate.now().minusDays(8);
        subDate4 = LocalDate.now().minusDays(1);
        subDate5 = LocalDate.now().minusDays(3);

        doc1.setSubmissionDate(subDate1);
        doc2.setSubmissionDate(subDate2);
        doc3.setSubmissionDate(subDate3);
        doc4.setSubmissionDate(subDate4);
        doc5.setSubmissionDate(subDate5);
        repository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5));

        final var requestDTO = RevalidationRequestDTO.builder().sortColumn("submissionDate").sortOrder("desc").build();
        final var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));

        final var doctorsForDB = doctorDTO.getTraineeInfo();

        assertThat(doctorsForDB.get(0).getGmcReferenceNumber(), is(gmcRef4));
        assertThat(doctorsForDB.get(0).getDoctorFirstName(), is(fName4));
        assertThat(doctorsForDB.get(0).getDoctorLastName(), is(lName4));
        assertThat(doctorsForDB.get(0).getSubmissionDate(), is(subDate4));
        assertThat(doctorsForDB.get(0).getDateAdded(), is(addedDate4));
        assertThat(doctorsForDB.get(0).getUnderNotice(), is(un4));
        assertThat(doctorsForDB.get(0).getSanction(), is(sanction4));
        assertThat(doctorsForDB.get(0).getDoctorStatus(), is(status4));

        assertThat(doctorsForDB.get(1).getGmcReferenceNumber(), is(gmcRef2));
        assertThat(doctorsForDB.get(1).getDoctorFirstName(), is(fName2));
        assertThat(doctorsForDB.get(1).getDoctorLastName(), is(lName2));
        assertThat(doctorsForDB.get(1).getSubmissionDate(), is(subDate2));
        assertThat(doctorsForDB.get(1).getDateAdded(), is(addedDate2));
        assertThat(doctorsForDB.get(1).getUnderNotice(), is(un2));
        assertThat(doctorsForDB.get(1).getSanction(), is(sanction2));
        assertThat(doctorsForDB.get(1).getDoctorStatus(), is(status2));

        assertThat(doctorsForDB.get(2).getGmcReferenceNumber(), is(gmcRef5));
        assertThat(doctorsForDB.get(2).getDoctorFirstName(), is(fName5));
        assertThat(doctorsForDB.get(2).getDoctorLastName(), is(lName5));
        assertThat(doctorsForDB.get(2).getSubmissionDate(), is(subDate5));
        assertThat(doctorsForDB.get(2).getDateAdded(), is(addedDate5));
        assertThat(doctorsForDB.get(2).getUnderNotice(), is(un5));
        assertThat(doctorsForDB.get(2).getSanction(), is(sanction5));
        assertThat(doctorsForDB.get(2).getDoctorStatus(), is(status5));

        assertThat(doctorsForDB.get(3).getGmcReferenceNumber(), is(gmcRef1));
        assertThat(doctorsForDB.get(3).getDoctorFirstName(), is(fName1));
        assertThat(doctorsForDB.get(3).getDoctorLastName(), is(lName1));
        assertThat(doctorsForDB.get(3).getSubmissionDate(), is(subDate1));
        assertThat(doctorsForDB.get(3).getDateAdded(), is(addedDate1));
        assertThat(doctorsForDB.get(3).getUnderNotice(), is(un1));
        assertThat(doctorsForDB.get(3).getSanction(), is(sanction1));
        assertThat(doctorsForDB.get(3).getDoctorStatus(), is(status1));

        assertThat(doctorsForDB.get(4).getGmcReferenceNumber(), is(gmcRef3));
        assertThat(doctorsForDB.get(4).getDoctorFirstName(), is(fName3));
        assertThat(doctorsForDB.get(4).getDoctorLastName(), is(lName3));
        assertThat(doctorsForDB.get(4).getSubmissionDate(), is(subDate3));
        assertThat(doctorsForDB.get(4).getDateAdded(), is(addedDate3));
        assertThat(doctorsForDB.get(4).getUnderNotice(), is(un3));
        assertThat(doctorsForDB.get(4).getSanction(), is(sanction3));
        assertThat(doctorsForDB.get(4).getDoctorStatus(), is(status3));
    }

    @DisplayName("Trainee doctors information should be sorted by first name in asc order")
    @Test
    public void shouldReturnDataSortByFirstNameInAscOrder() {
        fName1 = "Zolo";
        fName2 = "Andy";
        fName3 = "Mark";

        doc1.setDoctorFirstName(fName1);
        doc2.setDoctorFirstName(fName2);
        doc3.setDoctorFirstName(fName3);
        repository.saveAll(List.of(doc1, doc2, doc3));

        final var requestDTO = RevalidationRequestDTO.builder().sortColumn("doctorFirstName").sortOrder("asc").build();
        final var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(3L));

        final var doctorsForDB = doctorDTO.getTraineeInfo();

        assertThat(doctorsForDB.get(0).getGmcReferenceNumber(), is(gmcRef2));
        assertThat(doctorsForDB.get(0).getDoctorFirstName(), is(fName2));
        assertThat(doctorsForDB.get(0).getDoctorLastName(), is(lName2));
        assertThat(doctorsForDB.get(0).getSubmissionDate(), is(subDate2));
        assertThat(doctorsForDB.get(0).getDateAdded(), is(addedDate2));
        assertThat(doctorsForDB.get(0).getUnderNotice(), is(un2));
        assertThat(doctorsForDB.get(0).getSanction(), is(sanction2));
        assertThat(doctorsForDB.get(0).getDoctorStatus(), is(status2));

        assertThat(doctorsForDB.get(1).getGmcReferenceNumber(), is(gmcRef3));
        assertThat(doctorsForDB.get(1).getDoctorFirstName(), is(fName3));
        assertThat(doctorsForDB.get(1).getDoctorLastName(), is(lName3));
        assertThat(doctorsForDB.get(1).getSubmissionDate(), is(subDate3));
        assertThat(doctorsForDB.get(1).getDateAdded(), is(addedDate3));
        assertThat(doctorsForDB.get(1).getUnderNotice(), is(un3));
        assertThat(doctorsForDB.get(1).getSanction(), is(sanction3));
        assertThat(doctorsForDB.get(1).getDoctorStatus(), is(status3));

        assertThat(doctorsForDB.get(2).getGmcReferenceNumber(), is(gmcRef1));
        assertThat(doctorsForDB.get(2).getDoctorFirstName(), is(fName1));
        assertThat(doctorsForDB.get(2).getDoctorLastName(), is(lName1));
        assertThat(doctorsForDB.get(2).getSubmissionDate(), is(subDate1));
        assertThat(doctorsForDB.get(2).getDateAdded(), is(addedDate1));
        assertThat(doctorsForDB.get(2).getUnderNotice(), is(un1));
        assertThat(doctorsForDB.get(2).getSanction(), is(sanction1));
        assertThat(doctorsForDB.get(2).getDoctorStatus(), is(status1));

    }

    @DisplayName("Trainee doctors information should be sorted by last name in desc order")
    @Test
    public void shouldReturnDataSortByLastNameInDescOrder() {
        lName1 = "John";
        lName2 = "Adam";
        lName3 = "Webber";

        doc1.setDoctorLastName(lName1);
        doc2.setDoctorLastName(lName2);
        doc3.setDoctorLastName(lName3);

        repository.saveAll(List.of(doc1, doc2, doc3));

        final var requestDTO = RevalidationRequestDTO.builder().sortColumn("doctorLastName").sortOrder("desc").build();
        final var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(3L));

        final var doctorsForDB = doctorDTO.getTraineeInfo();

        assertThat(doctorsForDB.get(0).getGmcReferenceNumber(), is(gmcRef3));
        assertThat(doctorsForDB.get(0).getDoctorFirstName(), is(fName3));
        assertThat(doctorsForDB.get(0).getDoctorLastName(), is(lName3));
        assertThat(doctorsForDB.get(0).getSubmissionDate(), is(subDate3));
        assertThat(doctorsForDB.get(0).getDateAdded(), is(addedDate3));
        assertThat(doctorsForDB.get(0).getUnderNotice(), is(un3));
        assertThat(doctorsForDB.get(0).getSanction(), is(sanction3));
        assertThat(doctorsForDB.get(0).getDoctorStatus(), is(status3));

        assertThat(doctorsForDB.get(1).getGmcReferenceNumber(), is(gmcRef1));
        assertThat(doctorsForDB.get(1).getDoctorFirstName(), is(fName1));
        assertThat(doctorsForDB.get(1).getDoctorLastName(), is(lName1));
        assertThat(doctorsForDB.get(1).getSubmissionDate(), is(subDate1));
        assertThat(doctorsForDB.get(1).getDateAdded(), is(addedDate1));
        assertThat(doctorsForDB.get(1).getUnderNotice(), is(un1));
        assertThat(doctorsForDB.get(1).getSanction(), is(sanction1));
        assertThat(doctorsForDB.get(1).getDoctorStatus(), is(status1));

        assertThat(doctorsForDB.get(2).getGmcReferenceNumber(), is(gmcRef2));
        assertThat(doctorsForDB.get(2).getDoctorFirstName(), is(fName2));
        assertThat(doctorsForDB.get(2).getDoctorLastName(), is(lName2));
        assertThat(doctorsForDB.get(2).getSubmissionDate(), is(subDate2));
        assertThat(doctorsForDB.get(2).getDateAdded(), is(addedDate2));
        assertThat(doctorsForDB.get(2).getUnderNotice(), is(un2));
        assertThat(doctorsForDB.get(2).getSanction(), is(sanction2));
        assertThat(doctorsForDB.get(2).getDoctorStatus(), is(status2));
    }

    @DisplayName("Trainee doctors information should be sorted by submission date in desc order")
    @Test
    public void shouldReturnDataWithTotalAndUnderNoticeCounts() {
        un1 = UnderNotice.YES;
        un2 = UnderNotice.NO;
        un3 = UnderNotice.YES;
        un4 = UnderNotice.ON_HOLD;
        un5 = UnderNotice.NO;

        doc1.setUnderNotice(un1);
        doc2.setUnderNotice(un2);
        doc3.setUnderNotice(un3);
        doc4.setUnderNotice(un4);
        doc5.setUnderNotice(un5);

        repository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5));

        final var requestDTO = RevalidationRequestDTO.builder().sortColumn("submissionDate").sortOrder("desc").build();
        final var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getCountUnderNotice(), is(3L));
    }

    private void setupData() {
        gmcRef1 = faker.number().digits(8);
        gmcRef2 = faker.number().digits(8);
        gmcRef3 = faker.number().digits(8);
        gmcRef4 = faker.number().digits(8);
        gmcRef5 = faker.number().digits(8);

        fName1 = faker.name().firstName();
        fName2 = faker.name().firstName();
        fName3 = faker.name().firstName();
        fName4 = faker.name().firstName();
        fName5 = faker.name().firstName();

        lName1 = faker.name().lastName();
        lName2 = faker.name().lastName();
        lName3 = faker.name().lastName();
        lName4 = faker.name().lastName();
        lName5 = faker.name().lastName();

        subDate1 = LocalDate.now();
        subDate2 = LocalDate.now();
        subDate3 = LocalDate.now();
        subDate4 = LocalDate.now();
        subDate5 = LocalDate.now();

        addedDate1 = LocalDate.now().minusDays(5);
        addedDate2 = LocalDate.now().minusDays(5);
        addedDate3 = LocalDate.now().minusDays(5);
        addedDate4 = LocalDate.now().minusDays(5);
        addedDate5 = LocalDate.now().minusDays(5);

        un1 = faker.options().option(UnderNotice.class);
        un2 = faker.options().option(UnderNotice.class);
        un3 = faker.options().option(UnderNotice.class);
        un4 = faker.options().option(UnderNotice.class);
        un5 = faker.options().option(UnderNotice.class);

        sanction1 = faker.lorem().characters(2);
        sanction2 = faker.lorem().characters(2);
        sanction3 = faker.lorem().characters(2);
        sanction4 = faker.lorem().characters(2);
        sanction5 = faker.lorem().characters(2);

        status1 = faker.lorem().characters(10);
        status2 = faker.lorem().characters(10);
        status3 = faker.lorem().characters(10);
        status4 = faker.lorem().characters(10);
        status5 = faker.lorem().characters(10);

        doc1 = new DoctorsForDB(gmcRef1, fName1, lName1, subDate1, addedDate1, un1, sanction1, status1);
        doc2 = new DoctorsForDB(gmcRef2, fName2, lName2, subDate2, addedDate2, un2, sanction2, status2);
        doc3 = new DoctorsForDB(gmcRef3, fName3, lName3, subDate3, addedDate3, un3, sanction3, status3);
        doc4 = new DoctorsForDB(gmcRef4, fName4, lName4, subDate4, addedDate4, un4, sanction4, status4);
        doc5 = new DoctorsForDB(gmcRef5, fName5, lName5, subDate5, addedDate5, un5, sanction5, status5);
    }
}
