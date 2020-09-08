package uk.nhs.hee.tis.revalidation.service;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.hee.tis.revalidation.dto.TraineeAdminDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeCoreDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeRequestDto;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.RecommendationStatus;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.exception.RecommendationException;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static java.util.List.of;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;
import static uk.nhs.hee.tis.revalidation.entity.UnderNotice.ON_HOLD;
import static uk.nhs.hee.tis.revalidation.entity.UnderNotice.YES;


@RunWith(MockitoJUnitRunner.class)
public class DoctorsForDBServiceTest {

    private final Faker faker = new Faker();

    @InjectMocks
    private DoctorsForDBService doctorsForDBService;

    @Mock
    private DoctorsForDBRepository repository;

    @Mock
    private TraineeCoreService traineeCoreService;

    @Mock
    private TraineeCoreDto coreDTO1, coreDTO2, coreDTO3, coreDTO4, coreDTO5;

    @Mock
    private Page page;

    private DoctorsForDB doc1, doc2, doc3, doc4, doc5;
    private String gmcRef1, gmcRef2, gmcRef3, gmcRef4, gmcRef5;
    private String fname1, fname2, fname3, fname4, fname5;
    private String lname1, lname2, lname3, lname4, lname5;
    private LocalDate subDate1, subDate2, subDate3, subDate4, subDate5;
    private LocalDate addedDate1, addedDate2, addedDate3, addedDate4, addedDate5;
    private UnderNotice un1, un2, un3, un4, un5;
    private String sanction1, sanction2, sanction3, sanction4, sanction5;
    private RecommendationStatus status1, status2, status3, status4, status5;
    private LocalDate cctDate1, cctDate2, cctDate3, cctDate4, cctDate5;
    private String progName1, progName2, progName3, progName4, progName5;
    private String memType1, memType2, memType3, memType4, memType5;
    private String grade1, grade2, grade3, grade4, grade5;
    private String designatedBody;
    private String admin1, admin2, admin3, admin4, admin5;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(doctorsForDBService, "pageSize", 20);
        setupData();
    }

    @Test
    public void shouldReturnListOfAllDoctors() {

        final Pageable pageableAndSortable = PageRequest.of(1, 20, by(DESC, "submissionDate"));
        when(repository.findAll(pageableAndSortable, "")).thenReturn(page);
        when(traineeCoreService.getTraineeInformationFromCore(of(gmcRef1, gmcRef2, gmcRef3, gmcRef4, gmcRef5)))
                .thenReturn(Map.of(gmcRef1, coreDTO1, gmcRef2, coreDTO2, gmcRef3, coreDTO3, gmcRef4, coreDTO4, gmcRef5, coreDTO5));
        when(coreDTO1.getCctDate()).thenReturn(cctDate1);
        when(coreDTO1.getProgrammeName()).thenReturn(progName1);
        when(coreDTO1.getProgrammeMembershipType()).thenReturn(memType1);
        when(coreDTO1.getCurrentGrade()).thenReturn(grade1);

        when(coreDTO2.getCctDate()).thenReturn(cctDate2);
        when(coreDTO2.getProgrammeName()).thenReturn(progName2);
        when(coreDTO2.getProgrammeMembershipType()).thenReturn(memType2);
        when(coreDTO2.getCurrentGrade()).thenReturn(grade2);

        when(coreDTO3.getCctDate()).thenReturn(cctDate3);
        when(coreDTO3.getProgrammeName()).thenReturn(progName3);
        when(coreDTO3.getProgrammeMembershipType()).thenReturn(memType3);
        when(coreDTO3.getCurrentGrade()).thenReturn(grade3);

        when(coreDTO4.getCctDate()).thenReturn(cctDate4);
        when(coreDTO4.getProgrammeName()).thenReturn(progName4);
        when(coreDTO4.getProgrammeMembershipType()).thenReturn(memType4);
        when(coreDTO4.getCurrentGrade()).thenReturn(grade4);

        when(coreDTO5.getCctDate()).thenReturn(cctDate5);
        when(coreDTO5.getProgrammeName()).thenReturn(progName5);
        when(coreDTO5.getProgrammeMembershipType()).thenReturn(memType5);
        when(coreDTO5.getCurrentGrade()).thenReturn(grade5);

        when(page.get()).thenReturn(Stream.of(doc1, doc2, doc3, doc4, doc5));
        when(page.getTotalPages()).thenReturn(1);
        when(repository.countByUnderNoticeIn(YES, ON_HOLD)).thenReturn(2l);
        when(repository.count()).thenReturn(5l);
        final var requestDTO = TraineeRequestDto.builder()
                .sortOrder("desc")
                .sortColumn("submissionDate")
                .pageNumber(1)
                .searchQuery("")
                .build();

        final var allDoctors = doctorsForDBService.getAllTraineeDoctorDetails(requestDTO);

        final var doctorsForDB = allDoctors.getTraineeInfo();
        assertThat(allDoctors.getCountTotal(), is(5L));
        assertThat(allDoctors.getCountUnderNotice(), is(2L));
        assertThat(allDoctors.getTotalPages(), is(1L));
        assertThat(doctorsForDB, hasSize(5));

        assertThat(doctorsForDB.get(0).getGmcReferenceNumber(), is(gmcRef1));
        assertThat(doctorsForDB.get(0).getDoctorFirstName(), is(fname1));
        assertThat(doctorsForDB.get(0).getDoctorLastName(), is(lname1));
        assertThat(doctorsForDB.get(0).getSubmissionDate(), is(subDate1));
        assertThat(doctorsForDB.get(0).getDateAdded(), is(addedDate1));
        assertThat(doctorsForDB.get(0).getUnderNotice(), is(un1.name()));
        assertThat(doctorsForDB.get(0).getSanction(), is(sanction1));
        assertThat(doctorsForDB.get(0).getDoctorStatus(), is(status1.name()));
        assertThat(doctorsForDB.get(0).getCctDate(), is(cctDate1));
        assertThat(doctorsForDB.get(0).getProgrammeName(), is(progName1));
        assertThat(doctorsForDB.get(0).getProgrammeMembershipType(), is(memType1));
        assertThat(doctorsForDB.get(0).getCurrentGrade(), is(grade1));

        assertThat(doctorsForDB.get(1).getGmcReferenceNumber(), is(gmcRef2));
        assertThat(doctorsForDB.get(1).getDoctorFirstName(), is(fname2));
        assertThat(doctorsForDB.get(1).getDoctorLastName(), is(lname2));
        assertThat(doctorsForDB.get(1).getSubmissionDate(), is(subDate2));
        assertThat(doctorsForDB.get(1).getDateAdded(), is(addedDate2));
        assertThat(doctorsForDB.get(1).getUnderNotice(), is(un2.name()));
        assertThat(doctorsForDB.get(1).getSanction(), is(sanction2));
        assertThat(doctorsForDB.get(1).getDoctorStatus(), is(status2.name()));
        assertThat(doctorsForDB.get(1).getCctDate(), is(cctDate2));
        assertThat(doctorsForDB.get(1).getProgrammeName(), is(progName2));
        assertThat(doctorsForDB.get(1).getProgrammeMembershipType(), is(memType2));
        assertThat(doctorsForDB.get(1).getCurrentGrade(), is(grade2));

        assertThat(doctorsForDB.get(2).getGmcReferenceNumber(), is(gmcRef3));
        assertThat(doctorsForDB.get(2).getDoctorFirstName(), is(fname3));
        assertThat(doctorsForDB.get(2).getDoctorLastName(), is(lname3));
        assertThat(doctorsForDB.get(2).getSubmissionDate(), is(subDate3));
        assertThat(doctorsForDB.get(2).getDateAdded(), is(addedDate3));
        assertThat(doctorsForDB.get(2).getUnderNotice(), is(un3.name()));
        assertThat(doctorsForDB.get(2).getSanction(), is(sanction3));
        assertThat(doctorsForDB.get(2).getDoctorStatus(), is(status3.name()));
        assertThat(doctorsForDB.get(2).getCctDate(), is(cctDate3));
        assertThat(doctorsForDB.get(2).getProgrammeName(), is(progName3));
        assertThat(doctorsForDB.get(2).getProgrammeMembershipType(), is(memType3));
        assertThat(doctorsForDB.get(2).getCurrentGrade(), is(grade3));

        assertThat(doctorsForDB.get(3).getGmcReferenceNumber(), is(gmcRef4));
        assertThat(doctorsForDB.get(3).getDoctorFirstName(), is(fname4));
        assertThat(doctorsForDB.get(3).getDoctorLastName(), is(lname4));
        assertThat(doctorsForDB.get(3).getSubmissionDate(), is(subDate4));
        assertThat(doctorsForDB.get(3).getDateAdded(), is(addedDate4));
        assertThat(doctorsForDB.get(3).getUnderNotice(), is(un4.name()));
        assertThat(doctorsForDB.get(3).getSanction(), is(sanction4));
        assertThat(doctorsForDB.get(3).getDoctorStatus(), is(status4.name()));
        assertThat(doctorsForDB.get(3).getCctDate(), is(cctDate4));
        assertThat(doctorsForDB.get(3).getProgrammeName(), is(progName4));
        assertThat(doctorsForDB.get(3).getProgrammeMembershipType(), is(memType4));
        assertThat(doctorsForDB.get(3).getCurrentGrade(), is(grade4));

        assertThat(doctorsForDB.get(4).getGmcReferenceNumber(), is(gmcRef5));
        assertThat(doctorsForDB.get(4).getDoctorFirstName(), is(fname5));
        assertThat(doctorsForDB.get(4).getDoctorLastName(), is(lname5));
        assertThat(doctorsForDB.get(4).getSubmissionDate(), is(subDate5));
        assertThat(doctorsForDB.get(4).getDateAdded(), is(addedDate5));
        assertThat(doctorsForDB.get(4).getUnderNotice(), is(un5.name()));
        assertThat(doctorsForDB.get(4).getSanction(), is(sanction5));
        assertThat(doctorsForDB.get(4).getDoctorStatus(), is(status5.name()));
        assertThat(doctorsForDB.get(4).getCctDate(), is(cctDate5));
        assertThat(doctorsForDB.get(4).getProgrammeName(), is(progName5));
        assertThat(doctorsForDB.get(4).getProgrammeMembershipType(), is(memType5));
        assertThat(doctorsForDB.get(4).getCurrentGrade(), is(grade5));
    }

    @Test
    public void shouldReturnListOfUnderNoticeDoctors() {

        final Pageable pageableAndSortable = PageRequest.of(1, 20, by(DESC, "submissionDate"));
        when(repository.findByUnderNotice(pageableAndSortable, "", YES, ON_HOLD)).thenReturn(page);
        when(traineeCoreService.getTraineeInformationFromCore(of(gmcRef1, gmcRef2)))
                .thenReturn(Map.of(gmcRef1, coreDTO1, gmcRef2, coreDTO2));
        when(coreDTO1.getCctDate()).thenReturn(cctDate1);
        when(coreDTO1.getProgrammeName()).thenReturn(progName1);
        when(coreDTO1.getProgrammeMembershipType()).thenReturn(memType1);
        when(coreDTO1.getCurrentGrade()).thenReturn(grade1);

        when(coreDTO2.getCctDate()).thenReturn(cctDate2);
        when(coreDTO2.getProgrammeName()).thenReturn(progName2);
        when(coreDTO2.getProgrammeMembershipType()).thenReturn(memType2);
        when(coreDTO2.getCurrentGrade()).thenReturn(grade2);

        when(page.get()).thenReturn(Stream.of(doc1, doc2));
        when(page.getTotalPages()).thenReturn(1);
        when(repository.countByUnderNoticeIn(YES, ON_HOLD)).thenReturn(2l);
        when(repository.count()).thenReturn(5l);
        final var requestDTO = TraineeRequestDto.builder()
                .sortOrder("desc")
                .sortColumn("submissionDate")
                .underNotice(true)
                .pageNumber(1)
                .searchQuery("")
                .build();
        final var allDoctors = doctorsForDBService.getAllTraineeDoctorDetails(requestDTO);
        final var doctorsForDB = allDoctors.getTraineeInfo();
        assertThat(allDoctors.getCountTotal(), is(5L));
        assertThat(allDoctors.getCountUnderNotice(), is(2L));
        assertThat(allDoctors.getTotalPages(), is(1L));
        assertThat(doctorsForDB, hasSize(2));

        assertThat(doctorsForDB.get(0).getGmcReferenceNumber(), is(gmcRef1));
        assertThat(doctorsForDB.get(0).getDoctorFirstName(), is(fname1));
        assertThat(doctorsForDB.get(0).getDoctorLastName(), is(lname1));
        assertThat(doctorsForDB.get(0).getSubmissionDate(), is(subDate1));
        assertThat(doctorsForDB.get(0).getDateAdded(), is(addedDate1));
        assertThat(doctorsForDB.get(0).getUnderNotice(), is(un1.name()));
        assertThat(doctorsForDB.get(0).getSanction(), is(sanction1));
        assertThat(doctorsForDB.get(0).getDoctorStatus(), is(status1.name()));
        assertThat(doctorsForDB.get(0).getCctDate(), is(cctDate1));
        assertThat(doctorsForDB.get(0).getProgrammeName(), is(progName1));
        assertThat(doctorsForDB.get(0).getProgrammeMembershipType(), is(memType1));
        assertThat(doctorsForDB.get(0).getCurrentGrade(), is(grade1));

        assertThat(doctorsForDB.get(1).getGmcReferenceNumber(), is(gmcRef2));
        assertThat(doctorsForDB.get(1).getDoctorFirstName(), is(fname2));
        assertThat(doctorsForDB.get(1).getDoctorLastName(), is(lname2));
        assertThat(doctorsForDB.get(1).getSubmissionDate(), is(subDate2));
        assertThat(doctorsForDB.get(1).getDateAdded(), is(addedDate2));
        assertThat(doctorsForDB.get(1).getUnderNotice(), is(un2.name()));
        assertThat(doctorsForDB.get(1).getSanction(), is(sanction2));
        assertThat(doctorsForDB.get(1).getDoctorStatus(), is(status2.name()));
        assertThat(doctorsForDB.get(1).getCctDate(), is(cctDate2));
        assertThat(doctorsForDB.get(1).getProgrammeName(), is(progName2));
        assertThat(doctorsForDB.get(1).getProgrammeMembershipType(), is(memType2));
        assertThat(doctorsForDB.get(1).getCurrentGrade(), is(grade2));
    }

    @Test
    public void shouldReturnEmptyListOfDoctorsWhenNoRecordFound() {
        final Pageable pageableAndSortable = PageRequest.of(1, 20, by(DESC, "submissionDate"));
        when(repository.findAll(pageableAndSortable, "")).thenReturn(page);
        when(page.get()).thenReturn(Stream.of());
        when(repository.countByUnderNoticeIn(YES, ON_HOLD)).thenReturn(0l);
        final var requestDTO = TraineeRequestDto.builder()
                .sortOrder("desc")
                .sortColumn("submissionDate")
                .pageNumber(1)
                .searchQuery("")
                .build();
        final var allDoctors = doctorsForDBService.getAllTraineeDoctorDetails(requestDTO);
        final var doctorsForDB = allDoctors.getTraineeInfo();
        assertThat(allDoctors.getCountTotal(), is(0L));
        assertThat(allDoctors.getCountUnderNotice(), is(0L));
        assertThat(allDoctors.getTotalPages(), is(0L));
        assertThat(doctorsForDB, hasSize(0));
    }

    @Test
    public void shouldReturnListOfAllDoctorsWhoMatchSearchQuery() {

        final Pageable pageableAndSortable = PageRequest.of(1, 20, by(DESC, "submissionDate"));
        when(repository.findAll(pageableAndSortable, "query")).thenReturn(page);
        when(traineeCoreService.getTraineeInformationFromCore(of(gmcRef1, gmcRef4)))
                .thenReturn(Map.of(gmcRef1, coreDTO1, gmcRef4, coreDTO4));
        when(coreDTO1.getCctDate()).thenReturn(cctDate1);
        when(coreDTO1.getProgrammeName()).thenReturn(progName1);
        when(coreDTO1.getProgrammeMembershipType()).thenReturn(memType1);
        when(coreDTO1.getCurrentGrade()).thenReturn(grade1);

        when(coreDTO4.getCctDate()).thenReturn(cctDate4);
        when(coreDTO4.getProgrammeName()).thenReturn(progName4);
        when(coreDTO4.getProgrammeMembershipType()).thenReturn(memType4);
        when(coreDTO4.getCurrentGrade()).thenReturn(grade4);

        when(page.get()).thenReturn(Stream.of(doc1, doc4));
        when(page.getTotalPages()).thenReturn(1);
        when(page.getTotalElements()).thenReturn(2l);
        when(repository.countByUnderNoticeIn(YES, ON_HOLD)).thenReturn(2l);
        when(repository.count()).thenReturn(5l);
        final var requestDTO = TraineeRequestDto.builder()
                .sortOrder("desc")
                .sortColumn("submissionDate")
                .pageNumber(1)
                .searchQuery("query")
                .build();
        final var allDoctors = doctorsForDBService.getAllTraineeDoctorDetails(requestDTO);
        final var doctorsForDB = allDoctors.getTraineeInfo();
        assertThat(allDoctors.getCountTotal(), is(5L));
        assertThat(allDoctors.getCountUnderNotice(), is(2L));
        assertThat(allDoctors.getTotalPages(), is(1L));
        assertThat(allDoctors.getTotalResults(), is(2L));
        assertThat(doctorsForDB, hasSize(2));

        assertThat(doctorsForDB.get(0).getGmcReferenceNumber(), is(gmcRef1));
        assertThat(doctorsForDB.get(0).getDoctorFirstName(), is(fname1));
        assertThat(doctorsForDB.get(0).getDoctorLastName(), is(lname1));
        assertThat(doctorsForDB.get(0).getSubmissionDate(), is(subDate1));
        assertThat(doctorsForDB.get(0).getDateAdded(), is(addedDate1));
        assertThat(doctorsForDB.get(0).getUnderNotice(), is(un1.name()));
        assertThat(doctorsForDB.get(0).getSanction(), is(sanction1));
        assertThat(doctorsForDB.get(0).getDoctorStatus(), is(status1.name()));
        assertThat(doctorsForDB.get(0).getCctDate(), is(cctDate1));
        assertThat(doctorsForDB.get(0).getProgrammeName(), is(progName1));
        assertThat(doctorsForDB.get(0).getProgrammeMembershipType(), is(memType1));
        assertThat(doctorsForDB.get(0).getCurrentGrade(), is(grade1));

        assertThat(doctorsForDB.get(1).getGmcReferenceNumber(), is(gmcRef4));
        assertThat(doctorsForDB.get(1).getDoctorFirstName(), is(fname4));
        assertThat(doctorsForDB.get(1).getDoctorLastName(), is(lname4));
        assertThat(doctorsForDB.get(1).getSubmissionDate(), is(subDate4));
        assertThat(doctorsForDB.get(1).getDateAdded(), is(addedDate4));
        assertThat(doctorsForDB.get(1).getUnderNotice(), is(un4.name()));
        assertThat(doctorsForDB.get(1).getSanction(), is(sanction4));
        assertThat(doctorsForDB.get(1).getDoctorStatus(), is(status4.name()));
        assertThat(doctorsForDB.get(1).getCctDate(), is(cctDate4));
        assertThat(doctorsForDB.get(1).getProgrammeName(), is(progName4));
        assertThat(doctorsForDB.get(1).getProgrammeMembershipType(), is(memType4));
        assertThat(doctorsForDB.get(1).getCurrentGrade(), is(grade4));
    }

    @Test
    public void shouldUpdateAdmin() {
        final String newAdmin1 = faker.internet().emailAddress();
        final String newAdmin2 = faker.internet().emailAddress();
        final String newAdmin3 = faker.internet().emailAddress();
        final String newAdmin4 = faker.internet().emailAddress();
        final String newAdmin5 = faker.internet().emailAddress();
        final var ta1 = TraineeAdminDto.builder().admin(newAdmin1).gmcNumber(gmcRef1).build();
        final var ta2 = TraineeAdminDto.builder().admin(newAdmin2).gmcNumber(gmcRef2).build();
        final var ta3 = TraineeAdminDto.builder().admin(newAdmin3).gmcNumber(gmcRef3).build();
        final var ta4 = TraineeAdminDto.builder().admin(newAdmin4).gmcNumber(gmcRef4).build();
        final var ta5 = TraineeAdminDto.builder().admin(newAdmin5).gmcNumber(gmcRef5).build();
        when(repository.findById(gmcRef1)).thenReturn(Optional.of(doc1));
        when(repository.findById(gmcRef2)).thenReturn(Optional.of(doc2));
        when(repository.findById(gmcRef3)).thenReturn(Optional.of(doc3));
        when(repository.findById(gmcRef4)).thenReturn(Optional.of(doc4));
        when(repository.findById(gmcRef5)).thenReturn(Optional.of(doc5));
        doctorsForDBService.updateTraineeAdmin(List.of(ta1, ta2, ta3, ta4, ta5));
        verify(repository, times(5)).save(any());
    }


    private void setupData() {
        gmcRef1 = faker.number().digits(8);
        gmcRef2 = faker.number().digits(8);
        gmcRef3 = faker.number().digits(8);
        gmcRef4 = faker.number().digits(8);
        gmcRef5 = faker.number().digits(8);

        fname1 = faker.name().firstName();
        fname2 = faker.name().firstName();
        fname3 = faker.name().firstName();
        fname4 = faker.name().firstName();
        fname5 = faker.name().firstName();

        lname1 = faker.name().lastName();
        lname2 = faker.name().lastName();
        lname3 = faker.name().lastName();
        lname4 = faker.name().lastName();
        lname5 = faker.name().lastName();

        subDate1 = now();
        subDate2 = now();
        subDate3 = now();
        subDate4 = now();
        subDate5 = now();

        addedDate1 = now().minusDays(5);
        addedDate2 = now().minusDays(5);
        addedDate3 = now().minusDays(5);
        addedDate4 = now().minusDays(5);
        addedDate5 = now().minusDays(5);

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

        status1 = RecommendationStatus.NOT_STARTED;
        status2 = RecommendationStatus.NOT_STARTED;
        status3 = RecommendationStatus.NOT_STARTED;
        status4 = RecommendationStatus.NOT_STARTED;
        status5 = RecommendationStatus.NOT_STARTED;

        cctDate1 = now();
        cctDate2 = now();
        cctDate3 = now();
        cctDate4 = now();
        cctDate5 = now();

        progName1 = faker.lorem().sentence(3);
        progName2 = faker.lorem().sentence(3);
        progName3 = faker.lorem().sentence(3);
        progName4 = faker.lorem().sentence(3);
        progName5 = faker.lorem().sentence(3);

        memType1 = faker.lorem().characters(8);
        memType2 = faker.lorem().characters(8);
        memType3 = faker.lorem().characters(8);
        memType4 = faker.lorem().characters(8);
        memType5 = faker.lorem().characters(8);

        grade1 = faker.lorem().characters(5);
        grade2 = faker.lorem().characters(5);
        grade3 = faker.lorem().characters(5);
        grade4 = faker.lorem().characters(5);
        grade5 = faker.lorem().characters(5);

        designatedBody = faker.lorem().characters(8);
        admin1 = faker.internet().emailAddress();
        admin2 = faker.internet().emailAddress();
        admin3 = faker.internet().emailAddress();
        admin4 = faker.internet().emailAddress();
        admin5 = faker.internet().emailAddress();

        doc1 = new DoctorsForDB(gmcRef1, fname1, lname1, subDate1, addedDate1, un1, sanction1, status1, now(), designatedBody, admin1);
        doc2 = new DoctorsForDB(gmcRef2, fname2, lname2, subDate2, addedDate2, un2, sanction2, status2, now(), designatedBody, admin2);
        doc3 = new DoctorsForDB(gmcRef3, fname3, lname3, subDate3, addedDate3, un3, sanction3, status3, now(), designatedBody, admin3);
        doc4 = new DoctorsForDB(gmcRef4, fname4, lname4, subDate4, addedDate4, un4, sanction4, status4, now(), designatedBody, admin4);
        doc5 = new DoctorsForDB(gmcRef5, fname5, lname5, subDate5, addedDate5, un5, sanction5, status5, now(), designatedBody, admin5);
    }
}