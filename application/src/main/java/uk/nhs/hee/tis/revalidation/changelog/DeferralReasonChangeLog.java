package uk.nhs.hee.tis.revalidation.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import java.util.List;
import java.util.Set;
import uk.nhs.hee.tis.revalidation.entity.DeferralReason;
import uk.nhs.hee.tis.revalidation.entity.Status;

@ChangeLog
public class DeferralReasonChangeLog {

  @ChangeSet(order = "001", id = "insertInitialDeferralReason", author = "")
  public void insertInitialDeferralReason(MongockTemplate mongockTemplate) {
    final var insufficientEvidence = buildDeferralReason("1",
        "Insufficient evidence for a positive recommendation",
        "INSUFFICIENT_EVIDENCE",
        buildDeferralSubReason("1"),
        Status.CURRENT);
    final var doctorSubjectToProcess = buildDeferralReason("2",
        "The doctor is subject to an ongoing process",
        "ONGOING_PROCESS",
        buildDeferralSubReason("2"),
        Status.CURRENT);
    final var below1YearToCct = buildDeferralReason("3",
        "Below 1 year to CCT",
        "BELOW_1_YEAR_TO_CCT",
        buildDeferralSubReason("3"),
        Status.INACTIVE);
    final var sickCarersLeave = buildDeferralReason("4",
        "Sick carers leave",
        "SICK_CARERS_LEAVE",
        buildDeferralSubReason("4"),
        Status.INACTIVE);
    final var parentalLeave = buildDeferralReason("5",
        "Parental leave",
        "PARENTAL_LEAVE",
        buildDeferralSubReason("5"),
        Status.INACTIVE);
    final var examfailure = buildDeferralReason("6",
        "Exam failure",
        "EXAM_FAILURE",
        buildDeferralSubReason("6"),
        Status.INACTIVE);
    final var other = buildDeferralReason("7",
        "Other",
        "OTHER",
        buildDeferralSubReason("7"),
        Status.INACTIVE);
    final var outOfClinicalTraining = buildDeferralReason("8",
        "Out of Clinical training",
        "OUT_OF_CLINICAL_TRAINING",
        buildDeferralSubReason("8"),
        Status.INACTIVE);
    final var below5YearsFullReg = buildDeferralReason("9",
        "Below 5 years full reg",
        "BELOW_5_YEARS_FULL_REG",
        buildDeferralSubReason("9"),
        Status.INACTIVE);

    mongockTemplate.insertAll(Set.of(insufficientEvidence,
        doctorSubjectToProcess,
        below1YearToCct,
        sickCarersLeave,
        parentalLeave,
        examfailure,
        other,
        outOfClinicalTraining,
        below5YearsFullReg));
  }

  private DeferralReason buildDeferralReason(final String code,
      final String reason,
      final String abbr,
      final List<DeferralReason> subReason,
      final Status status
  ) {
    return DeferralReason.builder()
        .code(code)
        .reason(reason)
        .abbr(abbr)
        .deferralSubReasons(subReason)
        .status(status)
        .build();
  }

  private List<DeferralReason> buildDeferralSubReason(final String reasonCode) {
    if ("1".equalsIgnoreCase(reasonCode)) {
      return List.of(DeferralReason.builder().code("1").reason("Appraisal activity").build(),
          DeferralReason.builder().code("2").reason("Colleague feedback").build(),
          DeferralReason.builder().code("3").reason("Compliments and Complaints").build(),
          DeferralReason.builder().code("4").reason("CPD").build(),
          DeferralReason.builder().code("5").reason("Interruption to practice").build(),
          DeferralReason.builder().code("6").reason("Patient feedback").build(),
          DeferralReason.builder().code("7").reason("QIA").build(),
          DeferralReason.builder().code("8").reason("Significant events").build()
      );
    }
    return List.of();
  }
}
