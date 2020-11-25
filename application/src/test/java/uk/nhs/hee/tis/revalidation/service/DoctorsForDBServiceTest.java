package uk.nhs.hee.tis.revalidation.service;

import static java.time.LocalDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;
import static uk.nhs.hee.tis.revalidation.entity.UnderNotice.ON_HOLD;
import static uk.nhs.hee.tis.revalidation.entity.UnderNotice.YES;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
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
import uk.nhs.hee.tis.revalidation.dto.ConnectionMessageDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeAdminDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeRequestDto;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.RecommendationStatus;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;


@RunWith(MockitoJUnitRunner.class)
public class DoctorsForDBServiceTest {

  private final Faker faker = new Faker();

  @InjectMocks
  private DoctorsForDBService doctorsForDBService;

  @Mock
  private DoctorsForDBRepository repository;


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
  private String designatedBody1, designatedBody2, designatedBody3, designatedBody4, designatedBody5;
  private String admin1, admin2, admin3, admin4, admin5;

  @Before
  public void setup() {
    ReflectionTestUtils.setField(doctorsForDBService, "pageSize", 20);
    setupData();
  }

  @Test
  public void shouldReturnListOfAllDoctors() {

    final Pageable pageableAndSortable = PageRequest.of(1, 20, by(DESC, "submissionDate"));
    List<String> dbcs = List
        .of(designatedBody1, designatedBody2, designatedBody3, designatedBody4, designatedBody5);
    when(repository.findAll(pageableAndSortable, "", dbcs)).thenReturn(page);

    when(page.get()).thenReturn(Stream.of(doc1, doc2, doc3, doc4, doc5));
    when(page.getTotalPages()).thenReturn(1);
    when(repository.countByUnderNoticeIn(YES, ON_HOLD)).thenReturn(2l);
    when(repository.count()).thenReturn(5l);
    final var requestDTO = TraineeRequestDto.builder()
        .sortOrder("desc")
        .sortColumn("submissionDate")
        .pageNumber(1)
        .searchQuery("")
        .dbcs(dbcs)
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

    assertThat(doctorsForDB.get(1).getGmcReferenceNumber(), is(gmcRef2));
    assertThat(doctorsForDB.get(1).getDoctorFirstName(), is(fname2));
    assertThat(doctorsForDB.get(1).getDoctorLastName(), is(lname2));
    assertThat(doctorsForDB.get(1).getSubmissionDate(), is(subDate2));
    assertThat(doctorsForDB.get(1).getDateAdded(), is(addedDate2));
    assertThat(doctorsForDB.get(1).getUnderNotice(), is(un2.name()));
    assertThat(doctorsForDB.get(1).getSanction(), is(sanction2));
    assertThat(doctorsForDB.get(1).getDoctorStatus(), is(status2.name()));

    assertThat(doctorsForDB.get(2).getGmcReferenceNumber(), is(gmcRef3));
    assertThat(doctorsForDB.get(2).getDoctorFirstName(), is(fname3));
    assertThat(doctorsForDB.get(2).getDoctorLastName(), is(lname3));
    assertThat(doctorsForDB.get(2).getSubmissionDate(), is(subDate3));
    assertThat(doctorsForDB.get(2).getDateAdded(), is(addedDate3));
    assertThat(doctorsForDB.get(2).getUnderNotice(), is(un3.name()));
    assertThat(doctorsForDB.get(2).getSanction(), is(sanction3));
    assertThat(doctorsForDB.get(2).getDoctorStatus(), is(status3.name()));

    assertThat(doctorsForDB.get(3).getGmcReferenceNumber(), is(gmcRef4));
    assertThat(doctorsForDB.get(3).getDoctorFirstName(), is(fname4));
    assertThat(doctorsForDB.get(3).getDoctorLastName(), is(lname4));
    assertThat(doctorsForDB.get(3).getSubmissionDate(), is(subDate4));
    assertThat(doctorsForDB.get(3).getDateAdded(), is(addedDate4));
    assertThat(doctorsForDB.get(3).getUnderNotice(), is(un4.name()));
    assertThat(doctorsForDB.get(3).getSanction(), is(sanction4));
    assertThat(doctorsForDB.get(3).getDoctorStatus(), is(status4.name()));

    assertThat(doctorsForDB.get(4).getGmcReferenceNumber(), is(gmcRef5));
    assertThat(doctorsForDB.get(4).getDoctorFirstName(), is(fname5));
    assertThat(doctorsForDB.get(4).getDoctorLastName(), is(lname5));
    assertThat(doctorsForDB.get(4).getSubmissionDate(), is(subDate5));
    assertThat(doctorsForDB.get(4).getDateAdded(), is(addedDate5));
    assertThat(doctorsForDB.get(4).getUnderNotice(), is(un5.name()));
    assertThat(doctorsForDB.get(4).getSanction(), is(sanction5));
    assertThat(doctorsForDB.get(4).getDoctorStatus(), is(status5.name()));

  }

  @Test
  public void shouldReturnListOfDoctorsAttachedToASpecificDbc() {

    final Pageable pageableAndSortable = PageRequest.of(1, 20, by(DESC, "submissionDate"));
    List<String> dbcs = List.of(designatedBody1);
    when(repository.findAll(pageableAndSortable, "", dbcs)).thenReturn(page);
    when(page.get()).thenReturn(Stream.of(doc1));
    when(page.getTotalPages()).thenReturn(1);
    when(repository.countByUnderNoticeIn(YES, ON_HOLD)).thenReturn(2l);
    when(repository.count()).thenReturn(5l);
    final var requestDTO = TraineeRequestDto.builder()
        .sortOrder("desc")
        .sortColumn("submissionDate")
        .pageNumber(1)
        .searchQuery("")
        .dbcs(dbcs)
        .build();

    final var allDoctors = doctorsForDBService.getAllTraineeDoctorDetails(requestDTO);

    final var doctorsForDB = allDoctors.getTraineeInfo();
    assertThat(allDoctors.getCountTotal(), is(5L));
    assertThat(allDoctors.getCountUnderNotice(), is(2L));
    assertThat(allDoctors.getTotalPages(), is(1L));
    assertThat(doctorsForDB, hasSize(1));

    assertThat(doctorsForDB.get(0).getGmcReferenceNumber(), is(gmcRef1));
    assertThat(doctorsForDB.get(0).getDoctorFirstName(), is(fname1));
    assertThat(doctorsForDB.get(0).getDoctorLastName(), is(lname1));
    assertThat(doctorsForDB.get(0).getSubmissionDate(), is(subDate1));
    assertThat(doctorsForDB.get(0).getDateAdded(), is(addedDate1));
    assertThat(doctorsForDB.get(0).getUnderNotice(), is(un1.name()));
    assertThat(doctorsForDB.get(0).getSanction(), is(sanction1));
    assertThat(doctorsForDB.get(0).getDoctorStatus(), is(status1.name()));
  }

  @Test
  public void shouldReturnListOfUnderNoticeDoctors() {

    final Pageable pageableAndSortable = PageRequest.of(1, 20, by(DESC, "submissionDate"));
    List<String> dbcs = List
        .of(designatedBody1, designatedBody2, designatedBody3, designatedBody4, designatedBody5);
    when(repository.findByUnderNotice(pageableAndSortable, "", dbcs, YES, ON_HOLD))
        .thenReturn(page);

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
        .dbcs(dbcs)
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

    assertThat(doctorsForDB.get(1).getGmcReferenceNumber(), is(gmcRef2));
    assertThat(doctorsForDB.get(1).getDoctorFirstName(), is(fname2));
    assertThat(doctorsForDB.get(1).getDoctorLastName(), is(lname2));
    assertThat(doctorsForDB.get(1).getSubmissionDate(), is(subDate2));
    assertThat(doctorsForDB.get(1).getDateAdded(), is(addedDate2));
    assertThat(doctorsForDB.get(1).getUnderNotice(), is(un2.name()));
    assertThat(doctorsForDB.get(1).getSanction(), is(sanction2));
    assertThat(doctorsForDB.get(1).getDoctorStatus(), is(status2.name()));

  }

  @Test
  public void shouldReturnEmptyListOfDoctorsWhenNoRecordFound() {
    final Pageable pageableAndSortable = PageRequest.of(1, 20, by(DESC, "submissionDate"));
    List<String> dbcs = List
        .of(designatedBody1, designatedBody2, designatedBody3, designatedBody4, designatedBody5);
    when(repository.findAll(pageableAndSortable, "", dbcs)).thenReturn(page);
    when(page.get()).thenReturn(Stream.of());
    when(repository.countByUnderNoticeIn(YES, ON_HOLD)).thenReturn(0l);
    final var requestDTO = TraineeRequestDto.builder()
        .sortOrder("desc")
        .sortColumn("submissionDate")
        .pageNumber(1)
        .searchQuery("")
        .dbcs(dbcs)
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
    List<String> dbcs = List
        .of(designatedBody1, designatedBody2, designatedBody3, designatedBody4, designatedBody5);
    when(repository.findAll(pageableAndSortable, "query", dbcs)).thenReturn(page);

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
        .dbcs(dbcs)
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

    assertThat(doctorsForDB.get(1).getGmcReferenceNumber(), is(gmcRef4));
    assertThat(doctorsForDB.get(1).getDoctorFirstName(), is(fname4));
    assertThat(doctorsForDB.get(1).getDoctorLastName(), is(lname4));
    assertThat(doctorsForDB.get(1).getSubmissionDate(), is(subDate4));
    assertThat(doctorsForDB.get(1).getDateAdded(), is(addedDate4));
    assertThat(doctorsForDB.get(1).getUnderNotice(), is(un4.name()));
    assertThat(doctorsForDB.get(1).getSanction(), is(sanction4));
    assertThat(doctorsForDB.get(1).getDoctorStatus(), is(status4.name()));

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

  @Test
  public void shouldUpdateDesignatedBodyCode() {
    when(repository.findById(gmcRef1)).thenReturn(Optional.of(doc1));
    final var message = ConnectionMessageDto.builder().gmcId(gmcRef1).build();
    doctorsForDBService.removeDesignatedBodyCode(message);

    verify(repository).save(doc1);
  }

  @Test
  public void shouldNotUpdateDesignatedBodyCodeWhenNoDoctorFound() {
    when(repository.findById(gmcRef1)).thenReturn(Optional.empty());
    final var message = ConnectionMessageDto.builder().gmcId(gmcRef1).build();
    doctorsForDBService.removeDesignatedBodyCode(message);

    verify(repository, times(0)).save(doc1);
  }

  @Test
  public void shouldGetDesignatedBodyCode() {
    when(repository.findById(gmcRef1)).thenReturn(Optional.of(doc1));
    final var designatedBody = doctorsForDBService.getDesignatedBodyCode(gmcRef1);
    assertThat(designatedBody.getDesignatedBodyCode(), is(doc1.getDesignatedBodyCode()));
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

    designatedBody1 = faker.lorem().characters(8);
    designatedBody2 = faker.lorem().characters(8);
    designatedBody3 = faker.lorem().characters(8);
    designatedBody4 = faker.lorem().characters(8);
    designatedBody5 = faker.lorem().characters(8);

    admin1 = faker.internet().emailAddress();
    admin2 = faker.internet().emailAddress();
    admin3 = faker.internet().emailAddress();
    admin4 = faker.internet().emailAddress();
    admin5 = faker.internet().emailAddress();

    doc1 = new DoctorsForDB(gmcRef1, fname1, lname1, subDate1, addedDate1, un1, sanction1, status1,
        now(), designatedBody1, admin1);
    doc2 = new DoctorsForDB(gmcRef2, fname2, lname2, subDate2, addedDate2, un2, sanction2, status2,
        now(), designatedBody2, admin2);
    doc3 = new DoctorsForDB(gmcRef3, fname3, lname3, subDate3, addedDate3, un3, sanction3, status3,
        now(), designatedBody3, admin3);
    doc4 = new DoctorsForDB(gmcRef4, fname4, lname4, subDate4, addedDate4, un4, sanction4, status4,
        now(), designatedBody4, admin4);
    doc5 = new DoctorsForDB(gmcRef5, fname5, lname5, subDate5, addedDate5, un5, sanction5, status5,
        now(), designatedBody5, admin5);
  }
}