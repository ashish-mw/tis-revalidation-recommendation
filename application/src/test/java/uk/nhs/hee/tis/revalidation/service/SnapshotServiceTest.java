package uk.nhs.hee.tis.revalidation.service;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.hee.tis.revalidation.entity.*;
import uk.nhs.hee.tis.revalidation.repository.SnapshotRepository;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SnapshotServiceTest {

    private Faker faker = new Faker();

    @InjectMocks
    private SnapshotService snapshotService;

    @Mock
    private SnapshotRepository snapshotRepository;

    @Mock
    private DeferralReasonService deferralReasonService;

    @Mock
    private GmcClientService gmcClientService;

    @Mock
    private Recommendation recommendation;

    @Mock
    private DeferralReason reason;

    @Mock
    private DeferralReason subReason;

    @Mock
    private DoctorsForDB doctorsForDB;

    @Mock
    private Snapshot snapshot1;

    @Mock
    private SnapshotRevalidation snapshotRevalidation1;

    private String id;
    private String gmcNumber;
    private RecommendationGmcOutcome outcome;
    private RecommendationType recommendationType;
    private RecommendationStatus recommendationStatus;
    private LocalDate gmcSubmissionDate;
    private LocalDate actualSubmissionDate;
    private String gmcRevalidationId;
    private LocalDate deferralDate;
    private String deferralReason;
    private String deferralSubReason;
    private List<String> comments;
    private String admin;
    private String designatedBody;

    @Before
    public void setup() {
        id = faker.number().digits(10);
        gmcNumber = faker.number().digits(7);
        outcome = faker.options().option(RecommendationGmcOutcome.class);
        recommendationType = faker.options().option(RecommendationType.class);
        recommendationStatus = faker.options().option(RecommendationStatus.class);
        gmcSubmissionDate = LocalDate.now();
        actualSubmissionDate = LocalDate.now();
        deferralDate = LocalDate.now().plusDays(60);
        gmcRevalidationId = faker.number().digits(10);
        deferralReason = "1";
        deferralSubReason = "1";
        comments = List.of(faker.lorem().sentence(3));
        admin = faker.name().fullName();
        designatedBody = faker.lorem().characters(7);
    }

    @Test
    public void shouldSaveRecommendationToSnapshot() {
        when(recommendation.getId()).thenReturn(id);
        when(recommendation.getGmcNumber()).thenReturn(gmcNumber);
        when(recommendation.getOutcome()).thenReturn(outcome);
        when(recommendation.getRecommendationType()).thenReturn(recommendationType);
        when(recommendation.getRecommendationStatus()).thenReturn(recommendationStatus);
        when(recommendation.getGmcSubmissionDate()).thenReturn(gmcSubmissionDate);
        when(recommendation.getActualSubmissionDate()).thenReturn(actualSubmissionDate);
        when(recommendation.getDeferralDate()).thenReturn(deferralDate);
        when(recommendation.getGmcRevalidationId()).thenReturn(gmcRevalidationId);
        when(recommendation.getDeferralReason()).thenReturn(deferralReason);
        when(recommendation.getDeferralSubReason()).thenReturn(deferralSubReason);
        when(recommendation.getComments()).thenReturn(comments);
        when(recommendation.getAdmin()).thenReturn(admin);

        when(deferralReasonService.getDeferralReasonByCode(deferralReason)).thenReturn(reason);
        when(deferralReasonService.getDeferralSubReasonByReasonCodeAndReasonSubCode(deferralReason, deferralSubReason)).thenReturn(subReason);
        snapshotService.saveRecommendationToSnapshot(recommendation);

        verify(snapshotRepository).save(anyObject());
    }

    @Test
    public void shouldGetSnapshotRecommendations() {
        when(doctorsForDB.getGmcReferenceNumber()).thenReturn(gmcNumber);
        when(doctorsForDB.getDesignatedBodyCode()).thenReturn(designatedBody);
        when(snapshotRepository.findByGmcNumber(gmcNumber)).thenReturn(List.of(snapshot1));
        when(snapshot1.getRevalidation()).thenReturn(snapshotRevalidation1);
        when(snapshotRevalidation1.getId()).thenReturn(id);
        when(snapshotRevalidation1.getGmcRecommendationId()).thenReturn(gmcRevalidationId);
        when(snapshotRevalidation1.getDeferralDate()).thenReturn(deferralDate.toString());
        when(snapshotRevalidation1.getDeferralReason()).thenReturn(deferralReason);
        when(snapshotRevalidation1.getDeferralSubReason()).thenReturn(deferralSubReason);
        when(snapshotRevalidation1.getRevalidationStatusCode()).thenReturn(recommendationStatus.name());
        when(snapshotRevalidation1.getProposedOutcomeCode()).thenReturn(recommendationType.getType());
        when(snapshotRevalidation1.getSubmissionDate()).thenReturn(actualSubmissionDate.toString());
        when(snapshotRevalidation1.getGmcSubmissionDateTime()).thenReturn(gmcSubmissionDate.toString());
        when(snapshotRevalidation1.getAdmin()).thenReturn(admin);
        when(gmcClientService.checkRecommendationStatus(gmcNumber, gmcRevalidationId,
                id, designatedBody)).thenReturn(outcome.getOutcome());

        final var snapshotRecommendations = snapshotService.getSnapshotRecommendations(doctorsForDB);
        assertThat(snapshotRecommendations, hasSize(1));

        final var traineeRecommendationRecordDto = snapshotRecommendations.get(0);

        assertThat(traineeRecommendationRecordDto.getRecommendationId(), is(id));
        assertThat(traineeRecommendationRecordDto.getDeferralDate(),is(deferralDate));
        assertThat(traineeRecommendationRecordDto.getDeferralReason(),is(deferralReason));
        assertThat(traineeRecommendationRecordDto.getDeferralSubReason(),is(deferralSubReason));
        assertThat(traineeRecommendationRecordDto.getRecommendationStatus(),is(recommendationStatus.name()));
        assertThat(traineeRecommendationRecordDto.getRecommendationType(), is(recommendationType.name()));
        assertThat(traineeRecommendationRecordDto.getActualSubmissionDate(),is(actualSubmissionDate));
        assertThat(traineeRecommendationRecordDto.getGmcSubmissionDate(),is(gmcSubmissionDate));
        assertThat(traineeRecommendationRecordDto.getGmcOutcome(),is(outcome.getOutcome()));
        assertThat(traineeRecommendationRecordDto.getAdmin(),is(admin));
    }

}