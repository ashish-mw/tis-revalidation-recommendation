/*
 * The MIT License (MIT)
 *
 * Copyright 2021 Crown Copyright (Health Education England)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.nhs.hee.tis.revalidation.messages;

import static java.time.LocalDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome.APPROVED;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome.UNDER_REVIEW;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.COMPLETED;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.SUBMITTED_TO_GMC;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.parseDate;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.hee.tis.revalidation.dto.RecommendationStatusCheckDto;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.Recommendation;
import uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome;
import uk.nhs.hee.tis.revalidation.entity.RecommendationStatus;
import uk.nhs.hee.tis.revalidation.entity.RecommendationType;
import uk.nhs.hee.tis.revalidation.entity.Snapshot;
import uk.nhs.hee.tis.revalidation.entity.SnapshotRevalidation;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.repository.RecommendationRepository;
import uk.nhs.hee.tis.revalidation.repository.SnapshotRepository;
import uk.nhs.hee.tis.revalidation.service.RecommendationService;
import uk.nhs.hee.tis.revalidation.service.SnapshotService;

@ExtendWith(MockitoExtension.class)
class RecommendationStatusCheckUpdatedMessageHandlerTest {

  private final Faker faker = new Faker();
  @Captor
  ArgumentCaptor<DoctorsForDB> doctorCaptor;
  @Captor
  ArgumentCaptor<Recommendation> recommendationCaptor;
  @Captor
  ArgumentCaptor<Snapshot> snapshotCaptor;
  @InjectMocks
  private RecommendationStatusCheckUpdatedMessageHandler recommendationStatusCheckUpdatedMessageHandler;
  @Mock
  private RecommendationRepository recommendationRepository;
  @Mock
  private DoctorsForDBRepository doctorsForDBRepository;
  @Mock
  private SnapshotRepository snapshotRepository;
  @Mock
  private RecommendationService recommendationService;
  @Mock
  private SnapshotService snapshotService;

  private String recommendationId;
  private String gmcRecommendationId;
  private String firstName;
  private String lastName;
  private LocalDate submissionDate;
  private LocalDate actualSubmissionDate;
  private LocalDate dateAdded;
  private UnderNotice underNotice;
  private String sanction;
  private String designatedBodyCode;
  private RecommendationStatus status;
  private String admin1;
  private Recommendation recommendation;
  private String gmcId;

  @BeforeEach
  public void setup() {
    recommendationId = faker.lorem().characters(10);
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();
    status = SUBMITTED_TO_GMC;
    submissionDate = LocalDate.now();
    actualSubmissionDate = LocalDate.now();
    dateAdded = LocalDate.now();
    underNotice = faker.options().option(UnderNotice.class);
    sanction = faker.lorem().characters(2);
    designatedBodyCode = faker.lorem().characters(7);
    admin1 = faker.internet().emailAddress();
    gmcId = faker.number().digits(7);
    recommendation = buildRecommendation(gmcId, recommendationId, status, APPROVED);
    Snapshot snapshot = buildSnapshot(gmcId);
    gmcRecommendationId = faker.lorem().characters(7);
  }

  @ParameterizedTest(name = "GMC Outcome: {0} should Update Recommendation, Snapshot and Doctor")
  @EnumSource(value = RecommendationGmcOutcome.class, names = {"APPROVED", "REJECTED"})
  void shouldUpdateRecommendationAndSnapshotAndTisStatus(RecommendationGmcOutcome newOutcome) {

    final RecommendationStatusCheckDto recommendationStatusCheckDto =
        buildRecommendationStatusCheckDto(newOutcome);

    final var doctorsForDB = buildDoctorForDB(gmcId);
    when(doctorsForDBRepository.findById(gmcId)).thenReturn(Optional.of(doctorsForDB));

    when(recommendationRepository.findById(recommendationId))
        .thenReturn(Optional.of(buildRecommendation(gmcId, recommendationId, status,
            UNDER_REVIEW)));

    when(recommendationService.getRecommendationStatusForTrainee(gmcId)).thenReturn(COMPLETED);

    recommendationStatusCheckUpdatedMessageHandler
        .updateRecommendationAndTisStatus(recommendationStatusCheckDto);

    verify(doctorsForDBRepository).save(doctorCaptor.capture());
    assertThat(doctorCaptor.getValue().getDoctorStatus(), is(COMPLETED));

    verify(recommendationRepository).save(recommendationCaptor.capture());
    assertThat(recommendationCaptor.getValue().getRecommendationStatus(), is(COMPLETED));
    assertThat(recommendationCaptor.getValue().getOutcome(), is(newOutcome));

    verify(snapshotService).saveRecommendationToSnapshot(recommendationCaptor.capture());
    final var actualSnapshotRecommendation = recommendationCaptor.getValue();
    assertThat(actualSnapshotRecommendation.getId(), is(recommendationId));
    assertThat(actualSnapshotRecommendation.getRecommendationStatus(), is(COMPLETED));

  }

  @ParameterizedTest(name = "GMC Outcome: {0} should Check Recommendation Repository contains empty recommendation")
  @EnumSource(value = RecommendationGmcOutcome.class, names = {"APPROVED", "REJECTED"})
  void shouldCheckRecommendationRepositoryContainsEmptyRecommendation(
      RecommendationGmcOutcome newOutcome) {
    final RecommendationStatusCheckDto recommendationStatusCheckDto =
        buildRecommendationStatusCheckDto(newOutcome);
    when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.empty());

    recommendationStatusCheckUpdatedMessageHandler
        .updateRecommendationAndTisStatus(recommendationStatusCheckDto);

    verify(recommendationRepository, times(0)).save(recommendation);
  }

  private RecommendationStatusCheckDto buildRecommendationStatusCheckDto(
      RecommendationGmcOutcome outcome) {
    return RecommendationStatusCheckDto.builder()
        .gmcReferenceNumber(gmcId)
        .recommendationId(recommendationId)
        .outcome(outcome)
        .build();
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

  private Recommendation buildRecommendation(final String gmcId, final String recommendationId,
      final RecommendationStatus status,
      final RecommendationGmcOutcome outcome) {
    return Recommendation.builder()
        .id(recommendationId)
        .gmcNumber(gmcId)
        .recommendationStatus(status)
        .recommendationType(RecommendationType.REVALIDATE)
        .admin(admin1)
        .gmcSubmissionDate(submissionDate)
        .actualSubmissionDate(actualSubmissionDate)
        .outcome(outcome)
        .recommendationStatus(status)
        .build();
  }

  private Snapshot buildSnapshot(String gmcId) {
    return Snapshot.builder()
        .gmcNumber(recommendation.getGmcNumber())
        .revalidation(SnapshotRevalidation.builder()
            .id(recommendation.getId())
            .proposedOutcomeCode(recommendation.getRecommendationType().name())
            .gmcOutcomeCode(recommendation.getOutcome().getOutcome())
            .gmcRecommendationId(recommendation.getGmcRevalidationId())
            .deferralDate(parseDate(recommendation.getDeferralDate()))
            .revalidationStatusCode(recommendation.getRecommendationStatus().name())
            .gmcSubmissionDateTime(parseDate(recommendation.getGmcSubmissionDate()))
            .gmcOutcomeCode(recommendation.getOutcome().getOutcome())
            .submissionDate(parseDate(recommendation.getActualSubmissionDate()))
            .dateAdded(now().toString())
            .admin(recommendation.getAdmin())
            .recommendationSubmitter(recommendation.getAdmin())
            .comments(recommendation.getComments())
            .build())
        .build();
  }
}