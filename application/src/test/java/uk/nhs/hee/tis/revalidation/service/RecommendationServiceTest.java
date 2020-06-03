package uk.nhs.hee.tis.revalidation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.hee.tis.gmc.client.generated.CheckRecommendationStatusResponse;
import uk.nhs.hee.tis.gmc.client.generated.CheckRecommendationStatusResponseCT;
import uk.nhs.hee.tis.gmc.client.generated.TryRecommendationResponseCT;
import uk.nhs.hee.tis.gmc.client.generated.TryRecommendationV2Response;
import uk.nhs.hee.tis.revalidation.dto.DeferralReasonDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeCoreDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationRecordDto;
import uk.nhs.hee.tis.revalidation.entity.*;
import uk.nhs.hee.tis.revalidation.exception.InvalidDeferralDateException;
import uk.nhs.hee.tis.revalidation.exception.InvalidRecommendationIdException;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.repository.RecommendationRepository;
import uk.nhs.hee.tis.revalidation.repository.SnapshotRepository;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.*;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.INVALID_RECOMMENDATION;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.SUCCESS;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.NOT_STARTED;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.SUBMITTED_TO_GMC;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationType.*;
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
    private DoctorsForDB doctorsForDB;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private GmcClientService gmcClientService;

    @Mock
    private DeferralReasonService deferralReasonService;

    @Mock
    private DeferralReason deferralReason;

    @Mock
    private DeferralReason deferralSubReason;

    @Mock
    private TraineeCoreDto traineeCoreDTO;

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
        gmcOutcome1 = RecommendationGmcOutcome.APPROVED.getOutcome();
        gmcSubmissionDate1 = "2018-03-15 12:00:00";
        acutalSubmissionDate1 = "2018-03-15";
        admin1 = faker.funnyName().name();
        gmcRecommendationId1 = faker.number().digits(10);
        snapshotRevalidationId1 = faker.number().digits(10);

        deferralComment2 = faker.lorem().characters(20);
        deferralDate2 = LocalDate.of(2018, 03, 15);
        deferralResaon2 = "2";
        deferralSubResaon2 = null;
        revalidatonType2 = faker.options().option(RecommendationType.class).name();
        revalidationStatus2 = faker.options().option(RecommendationStatus.class).name();
        gmcOutcome2 = RecommendationGmcOutcome.APPROVED.getOutcome();
        gmcSubmissionDate2 = "2018-03-15 12:00:00";
        acutalSubmissionDate2 = "2018-03-15";
        admin2 = faker.funnyName().name();
        gmcRecommendationId2 = faker.number().digits(10);
        snapshotRevalidationId2 = faker.number().digits(10);

        gmcNumber1 = faker.number().digits(7);
        gmcNumber2 = faker.number().digits(7);
        comments = List.of(faker.lorem().sentence(3), faker.lorem().sentence(3), faker.lorem().sentence(7));
        recommendationId = faker.lorem().characters(10);
        newRecommendationId = faker.lorem().characters(10);
        deferralReasons = List.of(new DeferralReasonDto("1", "evidence", List.of()), new DeferralReasonDto("2", "ongoing", List.of()));
    }

    @Test
    public void shouldReturnRecommendationWithCurrentAndLegacyRecommendations() throws ParseException {
        final var gmcId = faker.number().digits(7);
        when(doctorsForDBRepository.findById(gmcId)).thenReturn(of(buildDoctorForDB(gmcId)));
        when(traineeCoreService.getTraineeInformationFromCore(List.of(gmcId))).thenReturn(Map.of(gmcId, traineeCoreDTO));
        when(snapshotRepository.findByGmcNumber(gmcId)).thenReturn(List.of(snapshot1, snapshot2));
        when(recommendationRepository.findByGmcNumber(gmcId)).thenReturn(List.of(buildRecommendation(gmcNumber1, recommendationId, status, REVALIDATE)));
        when(gmcClientService.checkRecommendationStatus(gmcId, gmcRecommendationId1, gmcId, designatedBodyCode))
                .thenReturn(buildCheckStatusResponse("0", "Approved"));
        when(gmcClientService.checkRecommendationStatus(gmcId, gmcRecommendationId2, gmcId, designatedBodyCode))
                .thenReturn(buildCheckStatusResponse("0", "Approved"));
        when(deferralReasonService.getAllDeferralReasons()).thenReturn(deferralReasons);
        when(traineeCoreDTO.getCctDate()).thenReturn(cctDate);
        when(traineeCoreDTO.getProgrammeMembershipType()).thenReturn(programmeMembershipType);
        when(traineeCoreDTO.getCurrentGrade()).thenReturn(currentGrade);

        when(snapshot1.getRevalidation()).thenReturn(snapshotRevalidation1);
        when(snapshotRevalidation1.getAdmin()).thenReturn(admin1);
        when(snapshotRevalidation1.getDeferralComment()).thenReturn(deferralComment1);
        when(snapshotRevalidation1.getDeferralReason()).thenReturn(deferralResaon1);
        when(snapshotRevalidation1.getDeferralDate()).thenReturn(deferralDate1.toString());
        when(snapshotRevalidation1.getRevalidationStatusCode()).thenReturn(revalidationStatus1);
        when(snapshotRevalidation1.getProposedOutcomeCode()).thenReturn(revalidatonType1);
        when(snapshotRevalidation1.getGmcSubmissionDateTime()).thenReturn(gmcSubmissionDate1);
        when(snapshotRevalidation1.getSubmissionDate()).thenReturn(acutalSubmissionDate1);
        when(snapshotRevalidation1.getGmcRecommendationId()).thenReturn(gmcRecommendationId1);
        when(snapshotRevalidation1.getId()).thenReturn(snapshotRevalidationId1);

        when(snapshot2.getRevalidation()).thenReturn(snapshotRevalidation2);
        when(snapshotRevalidation2.getAdmin()).thenReturn(admin2);
        when(snapshotRevalidation2.getDeferralComment()).thenReturn(deferralComment2);
        when(snapshotRevalidation2.getDeferralReason()).thenReturn(deferralResaon2);
        when(snapshotRevalidation2.getDeferralDate()).thenReturn(deferralDate2.toString());
        when(snapshotRevalidation2.getRevalidationStatusCode()).thenReturn(revalidationStatus2);
        when(snapshotRevalidation2.getProposedOutcomeCode()).thenReturn(revalidatonType2);
        when(snapshotRevalidation2.getGmcSubmissionDateTime()).thenReturn(gmcSubmissionDate2);
        when(snapshotRevalidation2.getSubmissionDate()).thenReturn(acutalSubmissionDate2);
        when(snapshotRevalidation2.getGmcRecommendationId()).thenReturn(gmcRecommendationId2);
        when(snapshotRevalidation2.getId()).thenReturn(snapshotRevalidationId2);

        final var recommendation = recommendationService.getTraineeInfo(gmcId);
        assertThat(recommendation.getGmcNumber(), is(gmcId));
        assertThat(recommendation.getFullName(), is(getFullName(firstName, lastName)));
        assertThat(recommendation.getUnderNotice(), is(underNotice.value()));
        assertThat(recommendation.getCctDate(), is(cctDate));
        assertThat(recommendation.getProgrammeMembershipType(), is(programmeMembershipType));
        assertThat(recommendation.getCurrentGrade(), is(currentGrade));
        assertThat(recommendation.getDeferralReasons(), hasSize(2));

        assertThat(recommendation.getRevalidations(), hasSize(3));
        var revalidationDTO = recommendation.getRevalidations().get(0);
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

        revalidationDTO = recommendation.getRevalidations().get(1);
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

        revalidationDTO = recommendation.getRevalidations().get(2);
        assertNull(revalidationDTO.getGmcOutcome());
        assertThat(revalidationDTO.getAdmin(), is(admin1));
        assertThat(revalidationDTO.getRecommendationType(), is(REVALIDATE.getType()));
        assertThat(revalidationDTO.getRecommendationStatus(), is(status.name()));
        assertThat(revalidationDTO.getGmcSubmissionDate(), is(submissionDate));
        assertThat(revalidationDTO.getActualSubmissionDate(), is(actualSubmissionDate));
        assertThat(revalidationDTO.getRecommendationId(), is(recommendationId));
        assertThat(revalidationDTO.getComments(), is(comments));
    }

    @Test
    public void shouldReturnCurrentRecommendationWhichAreSubmittedToGMC() throws ParseException {
        final var gmcId = faker.number().digits(7);
        when(doctorsForDBRepository.findById(gmcId)).thenReturn(of(buildDoctorForDB(gmcId)));
        when(traineeCoreService.getTraineeInformationFromCore(List.of(gmcId))).thenReturn(Map.of(gmcId, traineeCoreDTO));
        when(snapshotRepository.findByGmcNumber(gmcId)).thenReturn(List.of(snapshot1));
        when(recommendationRepository.findByGmcNumber(gmcId)).thenReturn(List.of(buildRecommendation(gmcId, newRecommendationId,
                SUBMITTED_TO_GMC, REVALIDATE)));
        when(gmcClientService.checkRecommendationStatus(gmcId, gmcRecommendationId1, gmcId, designatedBodyCode))
                .thenReturn(buildCheckStatusResponse("0", "Approved"));
        when(gmcClientService.checkRecommendationStatus(gmcId, gmcRecommendationId2, newRecommendationId, designatedBodyCode))
                .thenReturn(buildCheckStatusResponse("0", "Under Review"));
        when(deferralReasonService.getAllDeferralReasons()).thenReturn(deferralReasons);
        when(traineeCoreDTO.getCctDate()).thenReturn(cctDate);
        when(traineeCoreDTO.getProgrammeMembershipType()).thenReturn(programmeMembershipType);
        when(traineeCoreDTO.getCurrentGrade()).thenReturn(currentGrade);

        when(snapshot1.getRevalidation()).thenReturn(snapshotRevalidation1);
        when(snapshotRevalidation1.getAdmin()).thenReturn(admin1);
        when(snapshotRevalidation1.getDeferralComment()).thenReturn(deferralComment1);
        when(snapshotRevalidation1.getDeferralReason()).thenReturn(deferralResaon1);
        when(snapshotRevalidation1.getDeferralDate()).thenReturn(deferralDate1.toString());
        when(snapshotRevalidation1.getRevalidationStatusCode()).thenReturn(revalidationStatus1);
        when(snapshotRevalidation1.getProposedOutcomeCode()).thenReturn(revalidatonType1);
        when(snapshotRevalidation1.getGmcSubmissionDateTime()).thenReturn(gmcSubmissionDate1);
        when(snapshotRevalidation1.getSubmissionDate()).thenReturn(acutalSubmissionDate1);
        when(snapshotRevalidation1.getGmcRecommendationId()).thenReturn(gmcRecommendationId1);

        final var recommendation = recommendationService.getTraineeInfo(gmcId);
        assertThat(recommendation.getGmcNumber(), is(gmcId));
        assertThat(recommendation.getFullName(), is(getFullName(firstName, lastName)));
        assertThat(recommendation.getUnderNotice(), is(underNotice.value()));
        assertThat(recommendation.getCctDate(), is(cctDate));
        assertThat(recommendation.getProgrammeMembershipType(), is(programmeMembershipType));
        assertThat(recommendation.getCurrentGrade(), is(currentGrade));
        assertThat(recommendation.getDeferralReasons(), hasSize(2));

        assertThat(recommendation.getRevalidations(), hasSize(2));
        var revalidationDTO = recommendation.getRevalidations().get(0);
        assertThat(revalidationDTO.getDeferralReason(), is(deferralResaon1));
        assertThat(revalidationDTO.getDeferralDate(), is(deferralDate1));
        assertThat(revalidationDTO.getDeferralComment(), is(deferralComment1));
        assertThat(revalidationDTO.getAdmin(), is(admin1));
        assertThat(revalidationDTO.getGmcOutcome(), is(gmcOutcome1));
        assertThat(revalidationDTO.getRecommendationType(), is(revalidatonType1));
        assertThat(revalidationDTO.getRecommendationStatus(), is(revalidationStatus1));
        assertThat(revalidationDTO.getGmcSubmissionDate(), is(formatDateTime(gmcSubmissionDate1)));
        assertThat(revalidationDTO.getActualSubmissionDate(), is(formatDate(acutalSubmissionDate1)));

        revalidationDTO = recommendation.getRevalidations().get(1);
        assertThat(revalidationDTO.getGmcOutcome(), is(RecommendationGmcOutcome.UNDER_REVIEW.getOutcome()));
        assertThat(revalidationDTO.getAdmin(), is(admin1));
        assertThat(revalidationDTO.getRecommendationType(), is(REVALIDATE.getType()));
        assertThat(revalidationDTO.getRecommendationStatus(), is(SUBMITTED_TO_GMC.name()));
        assertThat(revalidationDTO.getGmcSubmissionDate(), is(submissionDate));
        assertThat(revalidationDTO.getActualSubmissionDate(), is(actualSubmissionDate));
        assertThat(revalidationDTO.getComments(), is(comments));
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

    @Test
    public void shouldSaveRevalidateRecommendationInDraftState() throws JsonProcessingException {
        final var recordDTO = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcNumber1)
                .recommendationType(REVALIDATE.name())
                .comments(comments)
                .build();

        final var s = new ObjectMapper().writeValueAsString(recordDTO);
        System.out.println(s);

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
                .build();

        when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
        when(doctorsForDB.getSubmissionDate()).thenReturn(submissionDate);
        when(deferralReasonService.getDeferralReasonByCode(deferralResaon1)).thenReturn(deferralReason);
        when(deferralReason.getSubReasonByCode(deferralSubResaon1)).thenReturn(deferralSubReason);

        recommendationService.saveRecommendation(recordDTO);

        verify(recommendationRepository).save(anyObject());
    }

    @Test(expected = InvalidDeferralDateException.class)
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

    @Test(expected = InvalidDeferralDateException.class)
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
        final var recommendation = buildRecommendation(gmcNumber1, recommendationId, status, REVALIDATE);
        when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
        when(recommendationRepository.findByIdAndGmcNumber(recommendationId, gmcNumber1)).thenReturn(recommendation);
        when(gmcClientService.submitToGmc(doctorsForDB, recommendation)).thenReturn(buildRecommendationV2Response(SUCCESS.getCode()));
        recommendationService.submitRecommendation(recommendationId, gmcNumber1);
        verify(recommendationRepository).save(recommendation);
    }

    @Test
    public void shouldNotUpdateRecommendationWhenSubmitFail() {
        final var recommendation = buildRecommendation(gmcNumber1, recommendationId, status, REVALIDATE);
        when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
        when(recommendationRepository.findByIdAndGmcNumber(recommendationId, gmcNumber1)).thenReturn(recommendation);
        when(gmcClientService.submitToGmc(doctorsForDB, recommendation)).thenReturn(buildRecommendationV2Response(INVALID_RECOMMENDATION.getCode()));
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

        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(buildRecommendation(gmcNumber1, recommendationId, status, REVALIDATE)));
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

        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(buildRecommendation(gmcNumber1, recommendationId, status, REVALIDATE)));
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

        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(buildRecommendation(gmcNumber1, recommendationId, status, REVALIDATE)));
        when(doctorsForDBRepository.findById(gmcNumber1)).thenReturn(Optional.of(doctorsForDB));
        when(doctorsForDB.getSubmissionDate()).thenReturn(submissionDate);
        when(deferralReasonService.getDeferralReasonByCode(deferralResaon1)).thenReturn(deferralReason);
        when(deferralReason.getSubReasonByCode(deferralSubResaon1)).thenReturn(deferralSubReason);

        recommendationService.updateRecommendation(recordDTO);

        verify(recommendationRepository).save(anyObject());
    }

    @Test(expected = InvalidRecommendationIdException.class)
    public void shouldThrowExceptionWhenInvalidRecommendationIdProvidedForUpdate() {
        final var recordDTO = TraineeRecommendationRecordDto.builder()
                .gmcNumber(gmcNumber1)
                .recommendationId(recommendationId)
                .recommendationType(REVALIDATE.name())
                .comments(comments)
                .build();

        recommendationService.updateRecommendation(recordDTO);
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
                .build();

    }

    private String getFullName(final String firstName, final String lastName) {
        return String.format("%s %s", firstName, lastName);
    }


    private CheckRecommendationStatusResponse buildCheckStatusResponse(final String returnCode, final String status) {
        final var checkRecommendationStatusResponse = new CheckRecommendationStatusResponse();
        final var checkRecommendationStatusResponseCT = new CheckRecommendationStatusResponseCT();
        checkRecommendationStatusResponseCT.setReturnCode(returnCode);
        checkRecommendationStatusResponseCT.setStatus(status);
        checkRecommendationStatusResponse.setCheckRecommendationStatusResult(checkRecommendationStatusResponseCT);
        return checkRecommendationStatusResponse;
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
                                               final RecommendationStatus status, final RecommendationType recommendationType) {
        return Recommendation.builder()
                .id(recommendationId)
                .gmcNumber(gmcId)
                .recommendationStatus(status)
                .recommendationType(recommendationType)
                .admin(admin1)
                .gmcRevalidationId(gmcRecommendationId2)
                .gmcSubmissionDate(submissionDate)
                .actualSubmissionDate(actualSubmissionDate)
                .comments(comments)
                .build();
    }
}