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
import uk.nhs.hee.tis.revalidation.dto.TraineeRequestDto;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.service.DoctorsForDBService;

import java.util.List;

import static java.util.Map.of;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RevalidationApplication.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
public class DoctorsSearchIT extends BaseIT {

    @Autowired
    private DoctorsForDBService service;

    @Autowired
    private DoctorsForDBRepository repository;

    @Before
    public void setup() {
        repository.deleteAll();
        setupData();
    }

    @Test
    public void shouldSearchForDoctorsByGmcReferenceNumber() throws Exception {

        repository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5));
        final var coreData = of(gmcRef3, coreDTO3);
        stubCoreRequest(coreData);


        final var requestDTO = TraineeRequestDto.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .searchQuery(doc3.getGmcReferenceNumber())
                .dbcs(List.of("1-AIIDR8", "1-AIIDVS"))
                .build();
        final var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getTotalResults(), is(1L));

        final var traineeInfo = doctorDTO.getTraineeInfo();

        assertThat(traineeInfo.get(0).getGmcReferenceNumber(), is(gmcRef3));
        assertThat(traineeInfo.get(0).getDoctorFirstName(), is(fName3));
        assertThat(traineeInfo.get(0).getDoctorLastName(), is(lName3));
        assertThat(traineeInfo.get(0).getSubmissionDate(), is(subDate3));
        assertThat(traineeInfo.get(0).getDateAdded(), is(addedDate3));
        assertThat(traineeInfo.get(0).getUnderNotice(), is(un3.name()));
        assertThat(traineeInfo.get(0).getSanction(), is(sanction3));
        assertThat(traineeInfo.get(0).getDoctorStatus(), is(status3.name()));
        assertThat(traineeInfo.get(0).getCctDate(), is(cctDate3));
        assertThat(traineeInfo.get(0).getProgrammeName(), is(progName3));
        assertThat(traineeInfo.get(0).getProgrammeMembershipType(), is(memType3));
        assertThat(traineeInfo.get(0).getCurrentGrade(), is(grade3));
    }

    @Test
    public void shouldSearchForDoctorsByDoctorFirstName() throws Exception {

        repository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5));
        final var coreData = of(gmcRef2, coreDTO2);
        stubCoreRequest(coreData);


        final var requestDTO = TraineeRequestDto.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .searchQuery(doc2.getDoctorFirstName())
                .dbcs(List.of("1-AIIDR8", "1-AIIDVS"))
                .build();
        final var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getTotalResults(), is(1L));

        final var traineeInfo = doctorDTO.getTraineeInfo();

        assertThat(traineeInfo.get(0).getGmcReferenceNumber(), is(gmcRef2));
        assertThat(traineeInfo.get(0).getDoctorFirstName(), is(fName2));
        assertThat(traineeInfo.get(0).getDoctorLastName(), is(lName2));
        assertThat(traineeInfo.get(0).getSubmissionDate(), is(subDate2));
        assertThat(traineeInfo.get(0).getDateAdded(), is(addedDate2));
        assertThat(traineeInfo.get(0).getUnderNotice(), is(un2.name()));
        assertThat(traineeInfo.get(0).getSanction(), is(sanction2));
        assertThat(traineeInfo.get(0).getDoctorStatus(), is(status2.name()));
        assertThat(traineeInfo.get(0).getCctDate(), is(cctDate2));
        assertThat(traineeInfo.get(0).getProgrammeName(), is(progName2));
        assertThat(traineeInfo.get(0).getProgrammeMembershipType(), is(memType2));
        assertThat(traineeInfo.get(0).getCurrentGrade(), is(grade2));
    }

    @Test
    public void shouldSearchForDoctorsByDoctorLastName() throws Exception {

        repository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5));
        final var coreData = of(gmcRef5, coreDTO5);
        stubCoreRequest(coreData);

        final var requestDTO = TraineeRequestDto.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .searchQuery(doc5.getDoctorLastName())
                .dbcs(List.of("1-AIIDR8", "1-AIIDVS"))
                .build();
        final var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getTotalResults(), is(1L));

        final var traineeInfo = doctorDTO.getTraineeInfo();

        assertThat(traineeInfo.get(0).getGmcReferenceNumber(), is(gmcRef5));
        assertThat(traineeInfo.get(0).getDoctorFirstName(), is(fName5));
        assertThat(traineeInfo.get(0).getDoctorLastName(), is(lName5));
        assertThat(traineeInfo.get(0).getSubmissionDate(), is(subDate5));
        assertThat(traineeInfo.get(0).getDateAdded(), is(addedDate5));
        assertThat(traineeInfo.get(0).getUnderNotice(), is(un5.name()));
        assertThat(traineeInfo.get(0).getSanction(), is(sanction5));
        assertThat(traineeInfo.get(0).getDoctorStatus(), is(status5.name()));
        assertThat(traineeInfo.get(0).getCctDate(), is(cctDate5));
        assertThat(traineeInfo.get(0).getProgrammeName(), is(progName5));
        assertThat(traineeInfo.get(0).getProgrammeMembershipType(), is(memType5));
        assertThat(traineeInfo.get(0).getCurrentGrade(), is(grade5));
    }

    @Test
    public void shouldSearchForFirstOrLastName() throws Exception {
        fName2 = "smith";
        lName4 = "smith";
        doc2.setDoctorFirstName(fName2);
        doc4.setDoctorLastName(lName4);
        repository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5));
        final var coreData = of(gmcRef2, coreDTO2, gmcRef4, coreDTO4);
        stubCoreRequest(coreData);

        final var requestDTO = TraineeRequestDto.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .searchQuery("smith")
                .dbcs(List.of("1-AIIDR8", "1-AIIDVS"))
                .build();
        final var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getTotalResults(), is(2L));

        final var traineeInfo = doctorDTO.getTraineeInfo();

        assertThat(traineeInfo.get(0).getGmcReferenceNumber(), is(gmcRef2));
        assertThat(traineeInfo.get(0).getDoctorFirstName(), is(fName2));
        assertThat(traineeInfo.get(0).getDoctorLastName(), is(lName2));
        assertThat(traineeInfo.get(0).getSubmissionDate(), is(subDate2));
        assertThat(traineeInfo.get(0).getDateAdded(), is(addedDate2));
        assertThat(traineeInfo.get(0).getUnderNotice(), is(un2.name()));
        assertThat(traineeInfo.get(0).getSanction(), is(sanction2));
        assertThat(traineeInfo.get(0).getDoctorStatus(), is(status2.name()));
        assertThat(traineeInfo.get(0).getCctDate(), is(cctDate2));
        assertThat(traineeInfo.get(0).getProgrammeName(), is(progName2));
        assertThat(traineeInfo.get(0).getProgrammeMembershipType(), is(memType2));
        assertThat(traineeInfo.get(0).getCurrentGrade(), is(grade2));

        assertThat(traineeInfo.get(1).getGmcReferenceNumber(), is(gmcRef4));
        assertThat(traineeInfo.get(1).getDoctorFirstName(), is(fName4));
        assertThat(traineeInfo.get(1).getDoctorLastName(), is(lName4));
        assertThat(traineeInfo.get(1).getSubmissionDate(), is(subDate4));
        assertThat(traineeInfo.get(1).getDateAdded(), is(addedDate4));
        assertThat(traineeInfo.get(1).getUnderNotice(), is(un4.name()));
        assertThat(traineeInfo.get(1).getSanction(), is(sanction4));
        assertThat(traineeInfo.get(1).getDoctorStatus(), is(status4.name()));
        assertThat(traineeInfo.get(1).getCctDate(), is(cctDate4));
        assertThat(traineeInfo.get(1).getProgrammeName(), is(progName4));
        assertThat(traineeInfo.get(1).getProgrammeMembershipType(), is(memType4));
        assertThat(traineeInfo.get(1).getCurrentGrade(), is(grade4));
    }

    @Test
    public void shouldSearchForFirstOrLastNameOrGmcNumber() throws Exception {
        fName1 = "wAqar";
        fName2 = "saqab";
        lName4 = "wAQAs";
        gmcRef5 = "2345aqa9";
        doc1.setDoctorFirstName(fName1);
        doc2.setDoctorFirstName(fName2);
        doc4.setDoctorLastName(lName4);
        doc5.setGmcReferenceNumber(gmcRef5);
        repository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5));
        final var coreData = of(this.gmcRef1, coreDTO1, gmcRef2, coreDTO2, gmcRef4, coreDTO4, gmcRef5, coreDTO5);
        stubCoreRequest(coreData);

        final var requestDTO = TraineeRequestDto.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .searchQuery("aqa")
                .dbcs(List.of("1-AIIDR8", "1-AIIDVS"))
                .build();
        final var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getTotalResults(), is(4L));

        final var traineeInfo = doctorDTO.getTraineeInfo();

        assertThat(traineeInfo.get(0).getGmcReferenceNumber(), is(gmcRef1));
        assertThat(traineeInfo.get(0).getDoctorFirstName(), is(fName1));
        assertThat(traineeInfo.get(0).getDoctorLastName(), is(lName1));
        assertThat(traineeInfo.get(0).getSubmissionDate(), is(subDate1));
        assertThat(traineeInfo.get(0).getDateAdded(), is(addedDate1));
        assertThat(traineeInfo.get(0).getUnderNotice(), is(un1.name()));
        assertThat(traineeInfo.get(0).getSanction(), is(sanction1));
        assertThat(traineeInfo.get(0).getDoctorStatus(), is(status1.name()));
        assertThat(traineeInfo.get(0).getCctDate(), is(cctDate1));
        assertThat(traineeInfo.get(0).getProgrammeName(), is(progName1));
        assertThat(traineeInfo.get(0).getProgrammeMembershipType(), is(memType1));
        assertThat(traineeInfo.get(0).getCurrentGrade(), is(grade1));

        assertThat(traineeInfo.get(1).getGmcReferenceNumber(), is(gmcRef2));
        assertThat(traineeInfo.get(1).getDoctorFirstName(), is(fName2));
        assertThat(traineeInfo.get(1).getDoctorLastName(), is(lName2));
        assertThat(traineeInfo.get(1).getSubmissionDate(), is(subDate2));
        assertThat(traineeInfo.get(1).getDateAdded(), is(addedDate2));
        assertThat(traineeInfo.get(1).getUnderNotice(), is(un2.name()));
        assertThat(traineeInfo.get(1).getSanction(), is(sanction2));
        assertThat(traineeInfo.get(1).getDoctorStatus(), is(status2.name()));
        assertThat(traineeInfo.get(1).getCctDate(), is(cctDate2));
        assertThat(traineeInfo.get(1).getProgrammeName(), is(progName2));
        assertThat(traineeInfo.get(1).getProgrammeMembershipType(), is(memType2));
        assertThat(traineeInfo.get(1).getCurrentGrade(), is(grade2));

        assertThat(traineeInfo.get(2).getGmcReferenceNumber(), is(gmcRef4));
        assertThat(traineeInfo.get(2).getDoctorFirstName(), is(fName4));
        assertThat(traineeInfo.get(2).getDoctorLastName(), is(lName4));
        assertThat(traineeInfo.get(2).getSubmissionDate(), is(subDate4));
        assertThat(traineeInfo.get(2).getDateAdded(), is(addedDate4));
        assertThat(traineeInfo.get(2).getUnderNotice(), is(un4.name()));
        assertThat(traineeInfo.get(2).getSanction(), is(sanction4));
        assertThat(traineeInfo.get(2).getDoctorStatus(), is(status4.name()));
        assertThat(traineeInfo.get(2).getCctDate(), is(cctDate4));
        assertThat(traineeInfo.get(2).getProgrammeName(), is(progName4));
        assertThat(traineeInfo.get(2).getProgrammeMembershipType(), is(memType4));
        assertThat(traineeInfo.get(2).getCurrentGrade(), is(grade4));

        assertThat(traineeInfo.get(3).getGmcReferenceNumber(), is(gmcRef5));
        assertThat(traineeInfo.get(3).getDoctorFirstName(), is(fName5));
        assertThat(traineeInfo.get(3).getDoctorLastName(), is(lName5));
        assertThat(traineeInfo.get(3).getSubmissionDate(), is(subDate5));
        assertThat(traineeInfo.get(3).getDateAdded(), is(addedDate5));
        assertThat(traineeInfo.get(3).getUnderNotice(), is(un5.name()));
        assertThat(traineeInfo.get(3).getSanction(), is(sanction5));
        assertThat(traineeInfo.get(3).getDoctorStatus(), is(status5.name()));
        assertThat(traineeInfo.get(3).getCctDate(), is(cctDate5));
        assertThat(traineeInfo.get(3).getProgrammeName(), is(progName5));
        assertThat(traineeInfo.get(3).getProgrammeMembershipType(), is(memType5));
        assertThat(traineeInfo.get(3).getCurrentGrade(), is(grade5));
    }

    @Test
    public void shouldSearchUnderNoticeDoctorsForFirstOrLastNameOrGmcNumber() throws Exception {
        un1 = UnderNotice.YES;
        un2 = UnderNotice.YES;
        un3 = UnderNotice.NO;
        un4 = UnderNotice.ON_HOLD;
        un5 = UnderNotice.YES;

        doc1.setUnderNotice(un1);
        doc2.setUnderNotice(un2);
        doc3.setUnderNotice(un3);
        doc4.setUnderNotice(un4);
        doc5.setUnderNotice(un5);

        fName1 = "wAqar";
        fName2 = "saqab";
        gmcRef3 = "AQabbbbb";
        lName4 = "wAQAs";

        doc1.setDoctorFirstName(fName1);
        doc2.setDoctorFirstName(fName2);
        doc3.setGmcReferenceNumber(gmcRef3);
        doc4.setDoctorLastName(lName4);
        repository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5));

        final var coreData = of(this.gmcRef1, coreDTO1, gmcRef2, coreDTO2, gmcRef4, coreDTO4);
        stubCoreRequest(coreData);

        final var requestDTO = TraineeRequestDto.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .underNotice(true)
                .searchQuery("aqa")
                .dbcs(List.of("1-AIIDR8", "1-AIIDVS"))
                .build();
        final var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getCountUnderNotice(), is(4L));
        assertThat(doctorDTO.getTotalResults(), is(3L));

        final var traineeInfo = doctorDTO.getTraineeInfo();

        assertThat(traineeInfo.get(0).getGmcReferenceNumber(), is(gmcRef1));
        assertThat(traineeInfo.get(0).getDoctorFirstName(), is(fName1));
        assertThat(traineeInfo.get(0).getDoctorLastName(), is(lName1));
        assertThat(traineeInfo.get(0).getSubmissionDate(), is(subDate1));
        assertThat(traineeInfo.get(0).getDateAdded(), is(addedDate1));
        assertThat(traineeInfo.get(0).getUnderNotice(), is(un1.name()));
        assertThat(traineeInfo.get(0).getSanction(), is(sanction1));
        assertThat(traineeInfo.get(0).getDoctorStatus(), is(status1.name()));
        assertThat(traineeInfo.get(0).getCctDate(), is(cctDate1));
        assertThat(traineeInfo.get(0).getProgrammeName(), is(progName1));
        assertThat(traineeInfo.get(0).getProgrammeMembershipType(), is(memType1));
        assertThat(traineeInfo.get(0).getCurrentGrade(), is(grade1));

        assertThat(traineeInfo.get(1).getGmcReferenceNumber(), is(gmcRef2));
        assertThat(traineeInfo.get(1).getDoctorFirstName(), is(fName2));
        assertThat(traineeInfo.get(1).getDoctorLastName(), is(lName2));
        assertThat(traineeInfo.get(1).getSubmissionDate(), is(subDate2));
        assertThat(traineeInfo.get(1).getDateAdded(), is(addedDate2));
        assertThat(traineeInfo.get(1).getUnderNotice(), is(un2.name()));
        assertThat(traineeInfo.get(1).getSanction(), is(sanction2));
        assertThat(traineeInfo.get(1).getDoctorStatus(), is(status2.name()));
        assertThat(traineeInfo.get(1).getCctDate(), is(cctDate2));
        assertThat(traineeInfo.get(1).getProgrammeName(), is(progName2));
        assertThat(traineeInfo.get(1).getProgrammeMembershipType(), is(memType2));
        assertThat(traineeInfo.get(1).getCurrentGrade(), is(grade2));

        assertThat(traineeInfo.get(2).getGmcReferenceNumber(), is(gmcRef4));
        assertThat(traineeInfo.get(2).getDoctorFirstName(), is(fName4));
        assertThat(traineeInfo.get(2).getDoctorLastName(), is(lName4));
        assertThat(traineeInfo.get(2).getSubmissionDate(), is(subDate4));
        assertThat(traineeInfo.get(2).getDateAdded(), is(addedDate4));
        assertThat(traineeInfo.get(2).getUnderNotice(), is(un4.name()));
        assertThat(traineeInfo.get(2).getSanction(), is(sanction4));
        assertThat(traineeInfo.get(2).getDoctorStatus(), is(status4.name()));
        assertThat(traineeInfo.get(2).getCctDate(), is(cctDate4));
        assertThat(traineeInfo.get(2).getProgrammeName(), is(progName4));
        assertThat(traineeInfo.get(2).getProgrammeMembershipType(), is(memType4));
        assertThat(traineeInfo.get(2).getCurrentGrade(), is(grade4));
    }

    @Test
    public void shouldSearchUnderNoticeDoctorsForFirstOrLastName() throws Exception {
        un1 = UnderNotice.NO;
        un2 = UnderNotice.YES;
        un3 = UnderNotice.NO;
        un4 = UnderNotice.ON_HOLD;
        un5 = UnderNotice.NO;

        doc1.setUnderNotice(un1);
        doc2.setUnderNotice(un2);
        doc3.setUnderNotice(un3);
        doc4.setUnderNotice(un4);
        doc5.setUnderNotice(un5);

        fName2 = "smith";
        lName4 = "Smith";
        fName5 = "smith";
        doc2.setDoctorFirstName(fName2);
        doc4.setDoctorLastName(lName4);
        doc5.setDoctorFirstName(fName5);
        repository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5));

        final var coreData = of(gmcRef2, coreDTO2, gmcRef4, coreDTO4);
        stubCoreRequest(coreData);

        final var requestDTO = TraineeRequestDto.builder()
                .sortColumn("submissionDate")
                .sortOrder("desc")
                .underNotice(true)
                .searchQuery("smith")
                .dbcs(List.of("1-AIIDR8", "1-AIIDVS"))
                .build();
        final var doctorDTO = service.getAllTraineeDoctorDetails(requestDTO);
        assertThat(doctorDTO.getCountTotal(), is(5L));
        assertThat(doctorDTO.getTotalResults(), is(2L));

        final var traineeInfo = doctorDTO.getTraineeInfo();

        assertThat(traineeInfo.get(0).getGmcReferenceNumber(), is(gmcRef2));
        assertThat(traineeInfo.get(0).getDoctorFirstName(), is(fName2));
        assertThat(traineeInfo.get(0).getDoctorLastName(), is(lName2));
        assertThat(traineeInfo.get(0).getSubmissionDate(), is(subDate2));
        assertThat(traineeInfo.get(0).getDateAdded(), is(addedDate2));
        assertThat(traineeInfo.get(0).getUnderNotice(), is(un2.name()));
        assertThat(traineeInfo.get(0).getSanction(), is(sanction2));
        assertThat(traineeInfo.get(0).getDoctorStatus(), is(status2.name()));
        assertThat(traineeInfo.get(0).getCctDate(), is(cctDate2));
        assertThat(traineeInfo.get(0).getProgrammeName(), is(progName2));
        assertThat(traineeInfo.get(0).getProgrammeMembershipType(), is(memType2));
        assertThat(traineeInfo.get(0).getCurrentGrade(), is(grade2));

        assertThat(traineeInfo.get(1).getGmcReferenceNumber(), is(gmcRef4));
        assertThat(traineeInfo.get(1).getDoctorFirstName(), is(fName4));
        assertThat(traineeInfo.get(1).getDoctorLastName(), is(lName4));
        assertThat(traineeInfo.get(1).getSubmissionDate(), is(subDate4));
        assertThat(traineeInfo.get(1).getDateAdded(), is(addedDate4));
        assertThat(traineeInfo.get(1).getUnderNotice(), is(un4.name()));
        assertThat(traineeInfo.get(1).getSanction(), is(sanction4));
        assertThat(traineeInfo.get(1).getDoctorStatus(), is(status4.name()));
        assertThat(traineeInfo.get(1).getCctDate(), is(cctDate4));
        assertThat(traineeInfo.get(1).getProgrammeName(), is(progName4));
        assertThat(traineeInfo.get(1).getProgrammeMembershipType(), is(memType4));
        assertThat(traineeInfo.get(1).getCurrentGrade(), is(grade4));
    }
}
