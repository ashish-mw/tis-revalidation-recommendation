package uk.nhs.hee.tis.revalidation.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.revalidation.dto.DeferralReasonDto;
import uk.nhs.hee.tis.revalidation.entity.DeferralReason;
import uk.nhs.hee.tis.revalidation.entity.Status;
import uk.nhs.hee.tis.revalidation.exception.RecommendationException;
import uk.nhs.hee.tis.revalidation.repository.DeferralReasonRepository;

//Its a temporary class to create a Deferral Reason table in Database. Will get rid of this once we have Reference DB access
@Slf4j
@Service
public class DeferralReasonService {

  @Autowired
  private DeferralReasonRepository deferralReasonRepository;

  @PostConstruct
  public void init() {
    insertDeferralReason();
  }

  //insert DeferralReason's data in DB on startup
  public void insertDeferralReason() {
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
    deferralReasonRepository.saveAll(List.of(insufficientEvidence, 
        doctorSubjectToProcess,
        below1YearToCct,
        sickCarersLeave,
        parentalLeave,
        examfailure,
        other,
        outOfClinicalTraining,
        below5YearsFullReg));
  }

  //get All deferral reasons
  public List<DeferralReasonDto> getAllDeferralReasons() {
    final var deferralReasons = deferralReasonRepository.findAll();
    return deferralReasons.stream().map(dr -> convertToDTO(dr)).collect(toList());
  }

  //get All CURRENT deferral reasons
  public List<DeferralReasonDto> getAllCurrentDeferralReasons() {
    final var deferralReasons = deferralReasonRepository.findAllByStatus(Status.CURRENT);
    return deferralReasons.stream().map(dr -> convertToDTO(dr)).collect(toList());
  }

  //get deferral reason by code
  public DeferralReason getDeferralReasonByCode(final String reasonCode) {
    final var deferralReason = deferralReasonRepository.findById(reasonCode);
    if (deferralReason.isEmpty()) {
      throw new RecommendationException("Deferral Reason code is invalid");
    }
    return deferralReason.get();
  }

  //get deferral subreason by code and sub reason code
  public DeferralReason getDeferralSubReasonByReasonCodeAndReasonSubCode(final String reasonCode,
      final String reasonSubCode) {
    final var deferralReason = getDeferralReasonByCode(reasonCode);
    return deferralReason.getSubReasonByCode(reasonSubCode);
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

  private DeferralReasonDto convertToDTO(DeferralReason dr) {
    return DeferralReasonDto.builder().code(dr.getCode()).reason(dr.getReason())
        .subReasons(dr.getDeferralSubReasons().stream()
            .map(sub -> DeferralReasonDto.builder().code(sub.getCode())
                .reason(sub.getReason()).build()).collect(toList())).build();
  }
}
