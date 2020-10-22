package uk.nhs.hee.tis.revalidation.service;

import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.INVALID_RECOMMENDATION;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.SUCCESS;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome.APPROVED;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome.REJECTED;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome.UNDER_REVIEW;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.NOT_STARTED;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.READY_TO_REVIEW;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.SUBMITTED_TO_GMC;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationType.DEFER;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationType.NON_ENGAGEMENT;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationType.REVALIDATE;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.formatDate;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.formatDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.hee.tis.gmc.client.generated.TryRecommendationResponseCT;
import uk.nhs.hee.tis.gmc.client.generated.TryRecommendationV2Response;
import uk.nhs.hee.tis.revalidation.dto.DeferralReasonDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeCoreDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationRecordDto;
import uk.nhs.hee.tis.revalidation.entity.DeferralReason;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.Recommendation;
import uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome;
import uk.nhs.hee.tis.revalidation.entity.RecommendationStatus;
import uk.nhs.hee.tis.revalidation.entity.RecommendationType;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.exception.RecommendationException;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.repository.RecommendationRepository;

@RunWith(MockitoJUnitRunner.class)
public class RecommendationServiceTest {

  private final Faker faker = new Faker();

  @InjectMocks
  private RecommendationService recommendationService;

  @Mock
  private RecommendationRepository recommendationRepository;

  @Mock
  private GmcClientService gmcClientService;

  @Mock
  private DeferralReasonService deferralReasonService;

  @Mock
  private DoctorsForDBRepository doctorsForDBRepository;

  @Mock
  private TraineeRecommendationRecordDto snapshot1;

  @Mock
  private TraineeRecommendationRecordDto snapshot2;

  @Mock
  private DoctorsForDB doctorsForDB;

  @Mock
  private DeferralReason deferralReason;

  @Mock
  private DeferralReason deferralSubReason;

  @Mock
  private TraineeCoreDto traineeCoreDTO;

  @Mock
  private SnapshotService snapshotService;

  private String firstName;
  private String lastName;
  private LocalDate submissionDate;
  private LocalDate actualSubmissionDate;
  private LocalDate dateAdded;
  private UnderNotice underNotice;
  private String sanction;
  private String designatedBodyCode;
  private RecommendationStatus status;
  private LocalDate cctDate;
  private String programmeMembershipType;
  private String currentGrade;

  private String deferralComment1, deferralComment2;
  private LocalDate deferralDate1, deferralDate2;
  private String deferralResaon1, deferralResaon2;
  private String deferralSubResaon1, deferralSubResaon2;
  private String gmcOutcome1, gmcOutcome2;
  private String revalidatonType1, revalidatonType2;
  private String revalidationStatus1, revalidationStatus2;
  private String gmcSubmissionDate1, gmcSubmissionDate2;
  private String acutalSubmissionDate1, acutalSubmissionDate2;
  private String gmcRecommendationId1, gmcRecommendationId2;
  private String admin1, admin2;

  private String gmcNumber1, gmcNumber2;
  private List<String> comments;
  private String recommendationId, newRecommendationId;
  private String snapshotRevalidationId1, snapshotRevalidationId2;
  private List<DeferralReasonDto> deferralReasons;

  @Before
  public void setup() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();
    status = NOT_STARTED;
    submissionDate = LocalDate.now();
    actualSubmissionDate = LocalDate.now();
    dateAdded = LocalDate.now();
    underNotice = faker.options().option(UnderNotice.class);
    sanction = faker.lorem().characters(2);
    designatedBodyCode = faker.lorem().characters(7);
    cctDate = LocalDate.now();
    programmeMembershipType = faker.lorem().characters(10);
    currentGrade = faker.lorem().characters(5);

    deferralComment1 = faker.lorem().characters(20);
    deferralDate1 = LocalDate.of(2018, 03, 15);
    deferralResaon1 = "1";
    deferralSubResaon1 = "4";
    revalidatonType1 = faker.options().option(RecommendationType.class).name();
    revalidationStatus1 = faker.options().option(RecommendationStatus.class).name();
    gmcOutcome1 = APPROVED.getOutcome();
    gmcSubmissionDate1 = "2018-03-15 12:00:00";
    acutalSubmissionDate1 = "2018-03-15";
    admin1 = faker.internet().emailAddress();
    gmcRecommendationId1 = faker.number().digits(10);
    snapshotRevalidationId1 = faker.number().digits(10);

    deferralComment2 = faker.lorem().characters(20);
    deferralDate2 = LocalDate.of(2018, 03, 15);
    deferralResaon2 = "2";
    deferralSubResaon2 = null;
    revalidatonType2 = faker.options().option(RecommendationType.class).name();
    revalidationStatus2 = faker.options().option(RecommendationStatus.class).name();
    gmcOutcome2 = APPROVED.getOutcome();
    gmcSubmissionDate2 = "2018-03-15 12:00:00";
    acutalSubmissionDate2 = "2018-03-15";
    admin2 = faker.internet().emailAddress();
    gmcRecommendationId2 = faker.number().digits(10);
    snapshotRevalidationId2 = faker.number().digits(10);

    gmcNumber1 = faker.number().digits(7);
    gmcNumber2 = faker.number().digits(7);
    comments = List
        .of(faker.lorem().sentence(3), faker.lorem().sentence(3), faker.lorem().sentence(7));
    recommendationId = faker.lorem().characters(10);
    newRecommendationId = faker.lorem().characters(10);
    deferralReasons = List.of(new DeferralReasonDto("1", "evidence", List.of()),
        new DeferralReasonDto("2", "ongoing", List.of()));
  }

  @Test
  public void shouldReturnRecommendationWithCurrentAndLegacyRecommendations()
      throws ParseException {
    final var gmcId = faker.number().digits(7);
    final var doctorsForDB = buildDoctorForDB(gmcId);
    when(doctorsForDBRepository.findById(gmcId)).thenReturn(of(doctorsForDB));
    when(snapshotService.getSnapshotRecommendations(doctorsForDB))
        .thenReturn(List.of(snapshot1, snapshot2));
    when(recommendationRepository.findByGmcNumber(gmcId)).thenReturn(List.of(
        buildRecommendation(gmcNumber1, recommendationId, status, REVALIDATE, UNDER_REVIEW)));
    when(deferralReasonService.getAllDeferralReasons()).thenReturn(deferralReasons);

    when(snapshot1.getAdmin()).thenReturn(admin1);
    when(snapshot1.getGmcNumber()).thenReturn(gmcId);
    when(snapshot1.getDeferralComment()).thenReturn(deferralComment1);
    when(snapshot1.getDeferralReason()).thenReturn(deferralResaon1);
    when(snapshot1.getDeferralDate()).thenReturn(deferralDate1);
    when(snapshot1.getRecommendationStatus()).thenReturn(revalidationStatus1);
    when(snapshot1.getRecommendationType()).thenReturn(revalidatonType1);
    when(snapshot1.getGmcOutcome()).thenReturn(gmcOutcome1);
    when(snapshot1.getGmcSubmissionDate()).thenReturn(formatDateTime(gmcSubmissionDate1));
    when(snapshot1.getActualSubmissionDate()).thenReturn(formatDate(acutalSubmissionDate1));
    when(snapshot1.getGmcRevalidationId()).thenReturn(gmcRecommendationId1);
    when(snapshot1.getRecommendationId()).thenReturn(snapshotRevalidationId1);

    when(snapshot2.getAdmin()).thenReturn(admin2);
    when(snapshot2.getGmcNumber()).thenReturn(gmcId);
    when(snapshot2.getDeferralComment()).thenReturn(deferralComment2);
    when(snapshot2.getDeferralReason()).thenReturn(deferralResaon2);
    when(snapshot2.getDeferralDate()).thenReturn(deferralDate2);
    when(snapshot2.getRecommendationStatus()).thenReturn(revalidationStatus2);
    when(snapshot2.getRecommendationType()).thenReturn(revalidatonType2);
    when(snapshot2.getGmcOutcome()).thenReturn(gmcOutcome1);
    when(snapshot2.getGmcSubmissionDate()).thenReturn(formatDateTime(gmcSubmissionDate2));
    when(snapshot2.getActualSubmissionDate()).thenReturn(formatDate(acutalSubmissionDate2));
    when(snapshot2.getGmcRevalidationId()).thenReturn(gmcRecommendationId2);
    when(snapshot2.getRecommendationId()).thenReturn(snapshotRevalidationId2);

    final var recommendation = recommendationService.getTraineeInfo(gmcId);
    assertThat(recommendation.getGmcNumber(), is(gmcId));
    assertThat(recommendation.getFullName(), is(getFullName(firstName, lastName)));
    assertThat(recommendation.getUnderNotice(), is(underNotice.value()));

    assertThat(recommendation.getRevalidations(), hasSize(3));
    var revalidationDTO = recommendation.getRevalidations().get(0);
    assertThat(revalidationDTO.getGmcOutcome(), is(UNDER_REVIEW.getOutcome()));
    assertThat(revalidationDTO.getAdmin(), is(admin1));
    assertThat(revalidationDTO.getRecommendationType(), is(REVALIDATE.name()));
    assertThat(revalidationDTO.getRecommendationStatus(), is(status.name()));
    assertThat(revalidationDTO.getGmcSubmissionDate(), is(submissionDate));
    assertThat(revalidationDTO.getActualSubmissionDate(), is(actualSubmissionDate));
    assertThat(revalidationDTO.getRecommendationId(), is(recommendationId));
    assertThat(revalidationDTO.getComments(), is(comments));

    revalidationDTO = recommendation.getRevalidations().get(1);
    assertThat(revalidationDTO.getGmcNumber(), is(gmcId));
    assertThat(revalidationDTO.getDeferralReason(), is(deferralResaon1));
    assertThat(revalidationDTO.getDeferralDate(), is(deferralDate1));
    assertThat(revalidationDTO.getDeferralComment(), is(deferralComment1));
    assertThat(revalidationDTO.getAdmin(), is(admin1));
    assertThat(revalidationDTO.getGmcOutcome(), is(gmcOutcome1));
    assertThat(revalidationDTO.getRecommendationType(), is(revalidatonType1));
    assertThat(revalidationDTO.getRecommendationStatus(), is(revalidationStatus1));
    assertThat(revalidationDTO.getGmcSubmissionDate(), is(formatDateTime(gmcSubmissionDate1)));
    assertThat(revalidationDTO.getActualSubmissionDate(), is(formatDate(acutalSubmissionDate1)));
    assertThat(revalidationDTO.getRecommendationId(), is(snapshotRevalidationId1));
    assertThat(revalidationDTO.getGmcRevalidationId(), is(gmcRecommendationId1));

    revalidationDTO = recommendation.getRevalidations().get(2);
    assertThat(revalidationDTO.getGmcNumber(), is(gmcId));
    assertThat(revalidationDTO.getDeferralReason(), is(deferralResaon2));
    assertThat(revalidationDTO.getDeferralDate(), is(deferralDate2));
    assertThat(revalidationDTO.getDeferralComment(), is(deferralComment2));
    assertThat(revalidationDTO.getAdmin(), is(admin2));
    assertThat(revalidationDTO.getGmcOutcome(), is(gmcOutcome2));
    assertThat(revalidationDTO.getRecommendationType(), is(revalidatonType2));
    assertThat(revalidationDTO.getRecommendationStatus(), is(revalidationStatus2));
    assertThat(revalidationDTO.getGmcSubmissionDate(), is(formatDateTime(gmcSubmissionDate2)));
    assertThat(revalidationDTO.getActualSubmissionDate(), is(formatDate(acutalSubmissionDate2)));
    assertThat(revalidationDTO.getRecommendationId(), is(snapshotRevalidationId2));
    assertThat(revalidationDTO.getGmcRevalidationId(), is(gmcRecommendationId2));
  }

  @Test
  public void shouldUpdateSnapshotIfRecommendationStatusBecomeApprove() throws ParseException {
    final var gmcId = faker.number().digits(7);
    final var doctorsForDB = buildDoctorForDB(gmcId);
    when(doctorsForDBRepository.findById(gmcId)).thenReturn(of(doctorsForDB));
    when(snapshotService.getSnapshotRecommendations(doctorsForDB)).thenReturn(List.of(snapshot1));
    final var recommendation1 = buildRecommendation(gmcNumber1, recommendationId, SUBMITTED_TO_GMC,
        REVALIDATE, APPROVED);
    when(recommendationRepository.findByGmcNumber(gmcId)).thenReturn(List.of(recommendation1));
    when(gmcClientService.checkRecommendationStatus(gmcNumber1,
        gmcRecommendationId2, recommendationId, designatedBodyCode)).thenReturn(APPROVED);
    when(deferralReasonService.getAllDeferralReasons()).thenReturn(deferralReasons);

    when(snapshot1.getAdmin()).thenReturn(admin1);
    when(snapshot1.getGmcNumber()).thenReturn(gmcId);
    when(snapshot1.getDeferralComment()).thenReturn(deferralComment1);
    when(snapshot1.getDeferralReason()).thenReturn(deferralResaon1);
    when(snapshot1.getDeferralDate()).thenReturn(deferralDate1);
    when(snapshot1.getRecommendationStatus()).thenReturn(revalidationStatus1);
    when(snapshot1.getRecommendationType()).thenReturn(revalidatonType1);
    when(snapshot1.getGmcOutcome()).thenReturn(gmcOutcome1);
    when(snapshot1.getGmcSubmissionDate()).thenReturn(formatDateTime(gmcSubmissionDate1));
    when(snapshot1.getActualSubmissionDate()).thenReturn(formatDate(acutalSubmissionDate1));
    when(snapshot1.getGmcRevalidationId()).thenReturn(gmcRecommendationId1);
    when(snapshot1.getRecommendationId()).thenReturn(snapshotRevalidationId1);

    final var recommendation = recommendationService.getTraineeInfo(gmcId);
    assertThat(recommendation.getGmcNumber(), is(gmcId));
    assertThat(recommendation.getFullName(), is(getFullName(firstName, lastName)));
    assertThat(recommendation.getUnderNotice(), is(underNotice.value()));
    assertThat(recommendation.getDeferralReasons(), hasSize(2));

    assertThat(recommendation.getRevalidations(), hasSize(2));
    var revalidationDTO = recommendation.getRevalidations().get(0);
    assertThat(revalidationDTO.getGmcOutcome(), is(APPROVED.getOutcome()));
    assertThat(revalidationDTO.getAdmin(), is(admin1));
    assertThat(revalidationDTO.getRecommendationType(), is(REVALIDATE.name()));
    assertThat(revalidationDTO.getRecommendationStatus(), is(SUBMITTED_TO_GMC.name()));
    assertThat(revalidationDTO.getGmcSubmissionDate(), is(submissionDate));
    assertThat(revalidationDTO.getActualSubmissionDate(), is(actualSubmissionDate));
    assertThat(revalidationDTO.getRecommendationId(), is(recommendationId));
    assertThat(revalidationDTO.getComments(), is(comments));

    revalidationDTO = recommendation.getRevalidations().get(1);
    assertThat(revalidationDTO.getGmcNumber(), is(gmcId));
    assertThat(revalidationDTO.getDeferralReason(), is(deferralResaon1));
    assertThat(revalidationDTO.getDeferralDate(), is(deferralDate1));
    assertThat(revalidationDTO.getDeferralComment(), is(deferralComment1));
    assertThat(revalidationDTO.getAdmin(), is(admin1));
    assertThat(revalidationDTO.getGmcOutcome(), is(gmcOutcome1));
    assertThat(revalidationDTO.getRecommendationType(), is(revalidatonType1));
    assertThat(revalidationDTO.getRecommendationStatus(), is(revalidationStatus1));
    assertThat(revalidationDTO.getGmcSubmissionDate(), is(formatDateTime(gmcSubmissionDate1)));
    assertThat(revalidationDTO.getActualSubmissionDate(), is(formatDate(acutalSubmissionDate1)));
    assertThat(revalidationDTO.getRecommendationId(), is(snapshotRevalidationId1));
    assertThat(revalidationDTO.getGmcRevalidationId(), is(gmcRecommendationId1));

    verify(recommendationRepository).save(recommendation1);
    verify(snapshotService).saveRecommendationToSnapshot(recommendation1);
  }

  @Test
  public void shouldReturnCurrentRecommendationWhichAreSubmittedToGMC() throws ParseException {
    final var gmcId = faker.number().digits(7);
    final var doctorsForDB = buildDoctorForDB(gmcId);
    when(doctorsForDBRepository.findById(gmcId)).thenReturn(of(doctorsForDB));
    when(snapshotService.getSnapshotRecommendations(doctorsForDB)).thenReturn(List.of(snapshot1));
    when(recommendationRepository.findByGmcNumber(gmcId))
        .thenReturn(List.of(buildRecommendation(gmcId, newRecommendationId,
            SUBMITTED_TO_GMC, REVALIDATE, UNDER_REVIEW)));
    when(deferralReasonService.getAllDeferralReasons()).thenReturn(deferralReasons);

    when(snapshot1.getAdmin()).thenReturn(admin1);
    when(snapshot1.getDeferralComment()).thenReturn(deferralComment1);
    when(snapshot1.getDeferralReason()).thenReturn(deferralResaon1);
    when(snapshot1.getDeferralDate()).thenReturn(deferralDate1);
    when(snapshot1.getRecommendationStatus()).thenReturn(revalidationStatus1);
    when(snapshot1.getRecommendationType()).thenReturn(revalidatonType1);
    when(snapshot1.getGmcOutcome()).thenReturn(gmcOutcome1);
    when(snapshot1.getGmcSubmissionDate()).thenReturn(formatDateTime(gmcSubmissionDate1));
    when(snapshot1.getActualSubmissionDate()).thenReturn(formatDate(acutalSubmissionDate1));
    when(snapshot1.getGmcRevalidationId()).thenReturn(gmcRecommendationId1);
    when(snapshot1.getRecommendationId()).thenReturn(recommendationId);

    final var recommendation = recommendationService.getTraineeInfo(gmcId);
    assertThat(recommendation.getGmcNumber(), is(gmcId));
    assertThat(recommendation.getFullName(), is(getFullName(firstName, lastName)));
    assertThat(recommendation.getUnderNotice(), is(underNotice.value()));

    assertThat(recommendation.getRevalidations(), hasSize(2));
    var revalidationDTO = recommendation.getRevalidations().get(0);
    assertThat(revalidationDTO.getGmcOutcome(), is(UNDER_REVIEW.getOutcome()));
    assertThat(revalidationDTO.getAdmin(), is(admin1));
    assertThat(revalidationDTO.getRecommendationType(), is(REVALIDATE.name()));
    assertThat(revalidationDTO.getRecommendationStatus(), is(SUBMITTED_TO_GMC.name()));
    assertThat(revalidationDTO.getGmcSubmissionDate(), is(submissionDate));
    assertThat(revalidationDTO.getActualSubmissionDate(), is(actualSubmissionDate));
    assertThat(revalidationDTO.getComments(), is(comments));

    revalidationDTO = recommendation.getRevalidations().get(1);
    assertThat(revalidationDTO.getDeferralReason(), is(deferralResaon1));
    assertThat(revalidationDTO.getDeferralDate(), is(deferralDate1));
    assertThat(revalidationDTO.getDeferralComment(), is(deferralComment1));
    assertThat(revalidationDTO.getAdmin(), is(admin1));
    assertThat(revalidationDTO.getGmcOutcome(), is(gmcOutcome1));
    assertThat(revalidationDTO.getRecommendationType(), is(revalidatonType1));
    assertThat(revalidationDTO.getRecommendationStatus(), is(revalidationStatus1));
    assertThat(revalidationDTO.getGmcSubmissionDate(), is(formatDateTime(gmcSubmissionDate1)));
    assertThat(revalidationDTO.getActualSubmissionDate(), is(formatDate(acutalSubmissionDate1)));
    assertThat(revalidationDTO.getGmcRevalidationId(), is(gmcRecommendationId1));
    assertThat(revalidationDTO.getRecommendationId(), is(recommendationId));
  }

  @Test
  public void shouldReturnRecommendationWithCurrentAndLegacyRevalidationsWithoutTisCoreInformation() {
    final var gmcId = faker.number().digits(8);
    final var doctorsForDB = buildDoctorForDB(gmcId);
    when(doctorsForDBRepository.findById(gmcId)).thenReturn(of(doctorsForDB));
    final var recommendation = recommendationService.getTraineeInfo(gmcId);
    assertThat(recommendation.getGmcNumber(), is(gmcId));
    assertThat(recommendation.getFullName(), is(getFullName(firstName, lastName)));
    assertThat(recommendation.getCctDate(), is(nullValue()));
    assertThat(recommendation.getProgrammeMembershipType(), is(nullValue()));
    assertThat(recommendation.getCurrentGrade(), is(nullValue()));
    assertThat(recommendation.getRevalidations(), hasSize(0));
  }

  @Test
  public void shouldSaveRevalidateRecommendationInDraftState() throws JsonProcessingException {
    final var recordDTO = TraineeRecommendationRecordDto.builder()
        .gmcNumber(gmcNumber1)
        .recommendationType(REVALIDATE.name())
        .comments(comments)
        .admin(admin1)
        .build();

    when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
    when(doctorsForDB.getSubmissionDate()).thenReturn(submissionDate);

    recommendationService.saveRecommendation(recordDTO);

    verify(recommendationRepository).save(anyObject());
  }

  @Test
  public void shouldSaveNonEngagementRecommendationInDraftState() {
    final var recordDTO = TraineeRecommendationRecordDto.builder()
        .gmcNumber(gmcNumber1)
        .recommendationType(NON_ENGAGEMENT.name())
        .comments(comments)
        .admin(admin1)
        .build();

    when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
    when(doctorsForDB.getSubmissionDate()).thenReturn(submissionDate);

    recommendationService.saveRecommendation(recordDTO);

    verify(recommendationRepository).save(anyObject());
  }

  @Test
  public void shouldSaveDeferRecommendationInDraftState() {
    final var deferralDate = LocalDate.now().plusDays(90);
    final var recordDTO = TraineeRecommendationRecordDto.builder()
        .gmcNumber(gmcNumber1)
        .recommendationType(DEFER.name())
        .deferralDate(deferralDate)
        .deferralReason(deferralResaon1)
        .deferralSubReason(deferralSubResaon1)
        .comments(comments)
        .admin(admin1)
        .build();

    when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
    when(doctorsForDB.getSubmissionDate()).thenReturn(submissionDate);
    when(deferralReasonService.getDeferralReasonByCode(deferralResaon1)).thenReturn(deferralReason);
    when(deferralReason.getSubReasonByCode(deferralSubResaon1)).thenReturn(deferralSubReason);

    recommendationService.saveRecommendation(recordDTO);

    verify(recommendationRepository).save(anyObject());
  }

  @Test
  public void shouldSaveDeferRecommendationInDraftStateWhenDeferralReasonIsNotRequiredSubReason() {
    final var deferralDate = LocalDate.now().plusDays(90);
    final var recordDTO = TraineeRecommendationRecordDto.builder()
        .gmcNumber(gmcNumber1)
        .recommendationType(DEFER.name())
        .deferralDate(deferralDate)
        .deferralReason(deferralResaon2)
        .deferralSubReason(null)
        .comments(comments)
        .admin(admin1)
        .build();

    when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
    when(doctorsForDB.getSubmissionDate()).thenReturn(submissionDate);
    when(deferralReasonService.getDeferralReasonByCode(deferralResaon2)).thenReturn(deferralReason);
    recommendationService.saveRecommendation(recordDTO);

    verify(recommendationRepository).save(anyObject());
  }

  @Test(expected = RecommendationException.class)
  public void shouldThrowExceptionWhenDeferralDateWithin60DaysOfSubmissionDate() {
    final var deferralDate = submissionDate.plusDays(59);
    final var recordDTO = TraineeRecommendationRecordDto.builder()
        .gmcNumber(gmcNumber1)
        .recommendationType(DEFER.name())
        .deferralDate(deferralDate)
        .deferralReason(deferralResaon1)
        .deferralSubReason(deferralSubResaon1)
        .comments(comments)
        .build();

    when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
    when(doctorsForDB.getSubmissionDate()).thenReturn(submissionDate);
    when(deferralReasonService.getDeferralReasonByCode(deferralResaon1)).thenReturn(deferralReason);
    when(deferralReason.getSubReasonByCode(deferralSubResaon1)).thenReturn(deferralSubReason);

    recommendationService.saveRecommendation(recordDTO);

    verify(recommendationRepository).save(anyObject());
  }

  @Test(expected = RecommendationException.class)
  public void shouldThrowExceptionWhenDeferralDateMoreThen365DaysOfSubmissionDate() {
    final var deferralDate = submissionDate.plusDays(366);
    final var recordDTO = TraineeRecommendationRecordDto.builder()
        .gmcNumber(gmcNumber1)
        .recommendationType(DEFER.name())
        .deferralDate(deferralDate)
        .deferralReason(deferralResaon1)
        .deferralSubReason(deferralSubResaon1)
        .comments(comments)
        .build();

    when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
    when(doctorsForDB.getSubmissionDate()).thenReturn(submissionDate);
    when(deferralReasonService.getDeferralReasonByCode(deferralResaon1)).thenReturn(deferralReason);
    when(deferralReason.getSubReasonByCode(deferralSubResaon1)).thenReturn(deferralSubReason);

    recommendationService.saveRecommendation(recordDTO);

    verify(recommendationRepository).save(anyObject());
  }

  @Test
  public void shouldSubmitRecommendation() {
    final var recommendation = buildRecommendation(gmcNumber1, recommendationId, status, REVALIDATE,
        UNDER_REVIEW);
    when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
    when(recommendationRepository.findByIdAndGmcNumber(recommendationId, gmcNumber1))
        .thenReturn(recommendation);
    when(gmcClientService.submitToGmc(doctorsForDB, recommendation))
        .thenReturn(buildRecommendationV2Response(SUCCESS.getCode()));
    recommendationService.submitRecommendation(recommendationId, gmcNumber1);
    verify(recommendationRepository).save(recommendation);
  }

  @Test(expected = RecommendationException.class)
  public void shouldNotUpdateRecommendationWhenSubmitFail() {
    final var recommendation = buildRecommendation(gmcNumber1, recommendationId, status, REVALIDATE,
        UNDER_REVIEW);
    when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
    when(recommendationRepository.findByIdAndGmcNumber(recommendationId, gmcNumber1))
        .thenReturn(recommendation);
    when(gmcClientService.submitToGmc(doctorsForDB, recommendation))
        .thenReturn(buildRecommendationV2Response(INVALID_RECOMMENDATION.getCode()));
    recommendationService.submitRecommendation(recommendationId, gmcNumber1);
    verify(recommendationRepository, times(0)).save(recommendation);
  }

  @Test
  public void shouldUpdateRevalidateRecommendationInDraftState() {
    final var recordDTO = TraineeRecommendationRecordDto.builder()
        .gmcNumber(gmcNumber1)
        .recommendationId(recommendationId)
        .recommendationType(REVALIDATE.name())
        .comments(comments)
        .build();

    final var recommendation = buildRecommendation(gmcNumber1, recommendationId, status, REVALIDATE,
        UNDER_REVIEW);
    when(recommendationRepository.findByGmcNumber(gmcNumber1)).thenReturn(List.of(recommendation));
    when(recommendationRepository.findById(recommendationId))
        .thenReturn(Optional.of(recommendation));
    when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
    when(doctorsForDB.getSubmissionDate()).thenReturn(submissionDate);

    recommendationService.updateRecommendation(recordDTO);

    verify(recommendationRepository).save(anyObject());
  }

  @Test
  public void shouldUpdateNonEngagementRecommendationInDraftState() {
    final var recordDTO = TraineeRecommendationRecordDto.builder()
        .gmcNumber(gmcNumber1)
        .recommendationId(recommendationId)
        .recommendationType(NON_ENGAGEMENT.name())
        .comments(comments)
        .build();

    when(recommendationRepository.findById(recommendationId)).thenReturn(Optional
        .of(buildRecommendation(gmcNumber1, recommendationId, status, REVALIDATE, UNDER_REVIEW)));
    when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
    when(doctorsForDB.getSubmissionDate()).thenReturn(submissionDate);

    recommendationService.updateRecommendation(recordDTO);

    verify(recommendationRepository).save(anyObject());
  }

  @Test
  public void shouldUpdateDeferRecommendationInDraftState() {
    final var deferralDate = LocalDate.now().plusDays(90);
    final var recordDTO = TraineeRecommendationRecordDto.builder()
        .gmcNumber(gmcNumber1)
        .recommendationId(recommendationId)
        .recommendationType(DEFER.name())
        .deferralDate(deferralDate)
        .deferralReason(deferralResaon1)
        .deferralSubReason(deferralSubResaon1)
        .comments(comments)
        .build();

    when(recommendationRepository.findById(recommendationId)).thenReturn(Optional
        .of(buildRecommendation(gmcNumber1, recommendationId, status, REVALIDATE, UNDER_REVIEW)));
    when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
    when(doctorsForDB.getSubmissionDate()).thenReturn(submissionDate);
    when(deferralReasonService.getDeferralReasonByCode(deferralResaon1)).thenReturn(deferralReason);
    when(deferralReason.getSubReasonByCode(deferralSubResaon1)).thenReturn(deferralSubReason);

    recommendationService.updateRecommendation(recordDTO);

    verify(recommendationRepository).save(anyObject());
  }

  @Test(expected = RecommendationException.class)
  public void shouldThrowExceptionWhenInvalidRecommendationIdProvidedForUpdate() {
    final var recordDTO = TraineeRecommendationRecordDto.builder()
        .gmcNumber(gmcNumber1)
        .recommendationId(recommendationId)
        .recommendationType(REVALIDATE.name())
        .comments(comments)
        .build();

    recommendationService.updateRecommendation(recordDTO);
  }

  @Test
  public void shouldAllwedSaveRecommendationWhenOneAlreadyInSubmittedAndRejectedState() {
    final var recordDTO = TraineeRecommendationRecordDto.builder()
        .gmcNumber(gmcNumber1)
        .recommendationType(REVALIDATE.name())
        .comments(comments)
        .build();

    when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
    when(doctorsForDB.getSubmissionDate()).thenReturn(submissionDate);
    final var draftRecommendation1 = Recommendation.builder().id(recommendationId)
        .gmcNumber(gmcNumber1)
        .recommendationStatus(SUBMITTED_TO_GMC).outcome(REJECTED).build();
    when(recommendationRepository.findByGmcNumber(gmcNumber1))
        .thenReturn(List.of(draftRecommendation1));

    recommendationService.saveRecommendation(recordDTO);

    verify(recommendationRepository).save(anyObject());
  }

  @Test(expected = RecommendationException.class)
  public void shouldNotSaveRecommendationWhenOneAlreadyInDraft() throws JsonProcessingException {
    final var recordDTO = TraineeRecommendationRecordDto.builder()
        .gmcNumber(gmcNumber1)
        .recommendationType(REVALIDATE.name())
        .comments(comments)
        .build();

    final var draftRecommendation1 = Recommendation.builder().id(recommendationId)
        .gmcNumber(gmcNumber1).recommendationStatus(READY_TO_REVIEW).build();
    when(recommendationRepository.findByGmcNumber(gmcNumber1))
        .thenReturn(List.of(draftRecommendation1));

    recommendationService.saveRecommendation(recordDTO);
  }

  @Test(expected = RecommendationException.class)
  public void shouldNotSaveRecommendationWhenStatusIsSubmittedButStillUnderReview()
      throws JsonProcessingException {
    final var recordDTO = TraineeRecommendationRecordDto.builder()
        .gmcNumber(gmcNumber1)
        .recommendationType(REVALIDATE.name())
        .comments(comments)
        .build();

    final var draftRecommendation1 = Recommendation.builder().id(recommendationId)
        .gmcNumber(gmcNumber1)
        .recommendationStatus(SUBMITTED_TO_GMC).outcome(UNDER_REVIEW).build();
    when(recommendationRepository.findByGmcNumber(gmcNumber1))
        .thenReturn(List.of(draftRecommendation1));

    recommendationService.saveRecommendation(recordDTO);
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
        .designatedBodyCode(designatedBodyCode)
        .admin(admin1)
        .build();

  }

  private String getFullName(final String firstName, final String lastName) {
    return String.format("%s %s", firstName, lastName);
  }

  private TryRecommendationV2Response buildRecommendationV2Response(final String returnCode) {
    final var tryRecommendationV2Response = new TryRecommendationV2Response();
    final var tryRecommendationResponseCT = new TryRecommendationResponseCT();
    tryRecommendationResponseCT.setReturnCode(returnCode);
    tryRecommendationResponseCT.setRecommendationID(recommendationId);
    tryRecommendationV2Response.setTryRecommendationV2Result(tryRecommendationResponseCT);
    return tryRecommendationV2Response;
  }

  private Recommendation buildRecommendation(final String gmcId, final String recommendationId,
      final RecommendationStatus status, final RecommendationType recommendationType,
      final RecommendationGmcOutcome outcome) {
    return Recommendation.builder()
        .id(recommendationId)
        .gmcNumber(gmcId)
        .recommendationStatus(status)
        .recommendationType(recommendationType)
        .admin(admin1)
        .gmcRevalidationId(gmcRecommendationId2)
        .gmcSubmissionDate(submissionDate)
        .actualSubmissionDate(actualSubmissionDate)
        .outcome(outcome)
        .comments(comments)
        .build();
  }
}