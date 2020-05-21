package uk.nhs.hee.tis.revalidation.entity;

public enum DeferralReason {
  BELOW_1_YEAR_TO_CCT("1"),
  SICK_CARERS_LEAVE("1"),
  PARENTAL_LEAVE("1"),
  OUT_OF_CLINICAL_TRAINING("1"),
  INSUFFICIENT_EVIDENCE("1"),
  EXAM_FAILURE("1"),
  BELOW_5_YEARS_FULL_REG("1"),
  OTHER("1"),
  ONGOING_PROCESS("2");

  private String gmcCode;

  DeferralReason(String gmcCode) {
    this.gmcCode = gmcCode;
  }

  public String getGmcCode() {
    return gmcCode;
  }

}
