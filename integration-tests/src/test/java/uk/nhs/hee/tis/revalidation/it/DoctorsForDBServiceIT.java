package uk.nhs.hee.tis.revalidation.it;

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
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.hee.tis.revalidation.RevalidationApplication;
import uk.nhs.hee.tis.revalidation.dto.RevalidationRequestDTO;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.service.DoctorsForDBService;

import java.util.List;

import static java.time.LocalDate.now;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RevalidationApplication.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
public class DoctorsForDBServiceIT extends BaseIT {

    @Autowired
    private DoctorsForDBService service;

    @Autowired
    private DoctorsForDBRepository repository;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(service, "pageSize", 20);
        repository.deleteAll();
        setupData();
    }

    @DisplayName("Trainee doctors information should be sorted by submission date in desc order")
    @Test
    public void shouldReturnDataSortBySubmissionDateInDescOrder() {
        subDate1 = now().minusDays(5);
        subDate2 = now().minusDays(2);
        subDate3 = now().minusDays(8);
        subDate4 = now().minusDays(1);
        subDate5 = now().minusDays(3);

        doc1.setSubmissionDate(subDate1);
        doc2.setSubmissionDate(subDate2);
        doc3.setSubmissionDate(subDate3);
        doc4.setSubmissionDate(subDate4);
        doc5.setSubmissionDate(subDate5);
        repository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5));

        final var requestDTO = RevalidationRequestDTO.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .searchQuery("")
                .build();
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

        final var requestDTO = RevalidationRequestDTO.builder()
                .sortColumn("doctorFirstName")
                .sortOrder("asc")
                .searchQuery("")
                .build();
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

        final var requestDTO = RevalidationRequestDTO.builder()
                .sortColumn("doctorLastName")
                .sortOrder("desc")
                .searchQuery("")
                .build();
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
    public void shouldReturnDataWithTotalCountAndUnderNoticeCount() {
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

        final var requestDTO = RevalidationRequestDTO.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .searchQuery("")
                .build();
        final var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getCountUnderNotice(), is(3L));
    }

    @DisplayName("Get Under Notice Trainee doctors information also sorted by submission date in desc order")
    @Test
    public void shouldReturnUnderNoticeDoctorsSortBySubmissionDate() {

        subDate1 = now().minusDays(5);
        subDate2 = now().minusDays(2);
        subDate3 = now().minusDays(8);
        subDate4 = now().minusDays(1);
        subDate5 = now().minusDays(3);

        un1 = UnderNotice.YES;
        un2 = UnderNotice.YES;
        un3 = UnderNotice.NO;
        un4 = UnderNotice.ON_HOLD;
        un5 = UnderNotice.YES;

        doc1.setSubmissionDate(subDate1);
        doc2.setSubmissionDate(subDate2);
        doc3.setSubmissionDate(subDate3);
        doc4.setSubmissionDate(subDate4);
        doc5.setSubmissionDate(subDate5);

        doc1.setUnderNotice(un1);
        doc2.setUnderNotice(un2);
        doc3.setUnderNotice(un3);
        doc4.setUnderNotice(un4);
        doc5.setUnderNotice(un5);

        repository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5));

        final var requestDTO = RevalidationRequestDTO.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .underNotice(true)
                .searchQuery("")
                .build();
        final var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getCountUnderNotice(), is(4L));

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
    }

    @DisplayName("Trainee doctors information should be paginated and sorted by submission date in desc order")
    @Test
    public void shouldReturnTraineeInfoInPaginatedForm() {
        subDate1 = now().minusDays(5);
        subDate2 = now().minusDays(2);
        subDate3 = now().minusDays(8);
        subDate4 = now().minusDays(1);
        subDate5 = now().minusDays(3);

        doc1.setSubmissionDate(subDate1);
        doc2.setSubmissionDate(subDate2);
        doc3.setSubmissionDate(subDate3);
        doc4.setSubmissionDate(subDate4);
        doc5.setSubmissionDate(subDate5);
        repository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5));

        var requestDTO = RevalidationRequestDTO.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .pageNumber(0)
                .searchQuery("")
                .build();
        ReflectionTestUtils.setField(service, "pageSize", 2);
        //fetch record for first page
        var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getTraineeInfo(), hasSize(2));
        assertThat(doctorDTO.getTotalPages(), is(3L));

        var doctorsForDB = doctorDTO.getTraineeInfo();

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

        requestDTO = RevalidationRequestDTO.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .pageNumber(1)
                .searchQuery("")
                .build();
        //fetch record for second page
        doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getTraineeInfo(), hasSize(2));

        doctorsForDB = doctorDTO.getTraineeInfo();

        assertThat(doctorsForDB.get(0).getGmcReferenceNumber(), is(gmcRef5));
        assertThat(doctorsForDB.get(0).getDoctorFirstName(), is(fName5));
        assertThat(doctorsForDB.get(0).getDoctorLastName(), is(lName5));
        assertThat(doctorsForDB.get(0).getSubmissionDate(), is(subDate5));
        assertThat(doctorsForDB.get(0).getDateAdded(), is(addedDate5));
        assertThat(doctorsForDB.get(0).getUnderNotice(), is(un5));
        assertThat(doctorsForDB.get(0).getSanction(), is(sanction5));
        assertThat(doctorsForDB.get(0).getDoctorStatus(), is(status5));

        assertThat(doctorsForDB.get(1).getGmcReferenceNumber(), is(gmcRef1));
        assertThat(doctorsForDB.get(1).getDoctorFirstName(), is(fName1));
        assertThat(doctorsForDB.get(1).getDoctorLastName(), is(lName1));
        assertThat(doctorsForDB.get(1).getSubmissionDate(), is(subDate1));
        assertThat(doctorsForDB.get(1).getDateAdded(), is(addedDate1));
        assertThat(doctorsForDB.get(1).getUnderNotice(), is(un1));
        assertThat(doctorsForDB.get(1).getSanction(), is(sanction1));
        assertThat(doctorsForDB.get(1).getDoctorStatus(), is(status1));

        requestDTO = RevalidationRequestDTO.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .pageNumber(2)
                .searchQuery("")
                .build();

        //fetch record for third page
        doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getTraineeInfo(), hasSize(1));

        doctorsForDB = doctorDTO.getTraineeInfo();

        assertThat(doctorsForDB.get(0).getGmcReferenceNumber(), is(gmcRef3));
        assertThat(doctorsForDB.get(0).getDoctorFirstName(), is(fName3));
        assertThat(doctorsForDB.get(0).getDoctorLastName(), is(lName3));
        assertThat(doctorsForDB.get(0).getSubmissionDate(), is(subDate3));
        assertThat(doctorsForDB.get(0).getDateAdded(), is(addedDate3));
        assertThat(doctorsForDB.get(0).getUnderNotice(), is(un3));
        assertThat(doctorsForDB.get(0).getSanction(), is(sanction3));
        assertThat(doctorsForDB.get(0).getDoctorStatus(), is(status3));
    }

    @DisplayName("Trainee doctors information should be not return any data if page number is not correct")
    @Test
    public void shouldReturnNoDataWhenPassInvalidPageNumber() {
        subDate1 = now().minusDays(5);
        subDate2 = now().minusDays(2);
        subDate3 = now().minusDays(8);
        subDate4 = now().minusDays(1);
        subDate5 = now().minusDays(3);

        doc1.setSubmissionDate(subDate1);
        doc2.setSubmissionDate(subDate2);
        doc3.setSubmissionDate(subDate3);
        doc4.setSubmissionDate(subDate4);
        doc5.setSubmissionDate(subDate5);
        repository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5));

        var requestDTO = RevalidationRequestDTO.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .pageNumber(6)
                .searchQuery("")
                .build();
        ReflectionTestUtils.setField(service, "pageSize", 2);
        var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getTraineeInfo(), hasSize(0));
        assertThat(doctorDTO.getTotalPages(), is(3L));
    }

}
