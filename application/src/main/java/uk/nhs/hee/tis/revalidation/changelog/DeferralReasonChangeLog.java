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
    var deferralSubReasons = List.of(DeferralReason.builder().code("1").reason("Appraisal activity").build(),
        DeferralReason.builder().code("2").reason("Colleague feedback").build(),
        DeferralReason.builder().code("3").reason("Compliments and Complaints").build(),
        DeferralReason.builder().code("4").reason("CPD").build(),
        DeferralReason.builder().code("5").reason("Interruption to practice").build(),
        DeferralReason.builder().code("6").reason("Patient feedback").build(),
        DeferralReason.builder().code("7").reason("QIA").build(),
        DeferralReason.builder().code("8").reason("Significant events").build());

    final var insufficientEvidence = DeferralReason.builder()
        .code("1")
        .abbr("INSUFFICIENT_EVIDENCE")
        .reason("Insufficient evidence for a positive recommendation")
        .deferralSubReasons(deferralSubReasons)
        .status(Status.CURRENT)
        .build();

    final var doctorSubjectToProcess = DeferralReason.builder()
        .code("2")
        .abbr("ONGOING_PROCESS")
        .reason("The doctor is subject to an ongoing process")
        .deferralSubReasons(List.of())
        .status(Status.CURRENT)
        .build();
    final var below1YearToCct = DeferralReason.builder()
        .code("3")
        .abbr("BELOW_1_YEAR_TO_CCT")
        .reason("Below 1 year to CCT")
        .deferralSubReasons(List.of())
        .status(Status.INACTIVE)
        .build();
    final var sickCarersLeave = DeferralReason.builder()
        .code("4")
        .abbr("SICK_CARERS_LEAVE")
        .reason("Sick carers leave")
        .deferralSubReasons(List.of())
        .status(Status.INACTIVE)
        .build();
    final var parentalLeave = DeferralReason.builder()
        .code("5")
        .abbr("PARENTAL_LEAVE")
        .reason("Parental leave")
        .deferralSubReasons(List.of())
        .status(Status.INACTIVE)
        .build();
    final var examfailure = DeferralReason.builder()
        .code("6")
        .abbr("EXAM_FAILURE")
        .reason("Exam failure")
        .deferralSubReasons(List.of())
        .status(Status.INACTIVE)
        .build();
    final var other = DeferralReason.builder()
        .code("7")
        .abbr("OTHER")
        .reason("other")
        .deferralSubReasons(List.of())
        .status(Status.INACTIVE)
        .build();
    final var outOfClinicalTraining = DeferralReason.builder()
        .code("8")
        .abbr("OUT_OF_CLINICAL_TRAINING")
        .reason("Out of Clinical training")
        .deferralSubReasons(List.of())
        .status(Status.INACTIVE)
        .build();
    final var below5YearsFullReg = DeferralReason.builder()
        .code("9")
        .abbr("BELOW_5_YEARS_FULL_REG")
        .reason("Below 5 years full reg")
        .deferralSubReasons(List.of())
        .status(Status.INACTIVE)
        .build();

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
}
