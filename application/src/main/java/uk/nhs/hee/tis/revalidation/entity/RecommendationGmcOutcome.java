package uk.nhs.hee.tis.revalidation.entity;

public enum RecommendationGmcOutcome {

  APPROVED("Approved"), REJECTED("Rejected"), UNDER_REVIEW("Under Review");

  private final String outcome;

  RecommendationGmcOutcome(final String outcome) {
    this.outcome = outcome;
  }

  public static RecommendationGmcOutcome fromString(final String value) {
    for (final RecommendationGmcOutcome gmcOutcome : RecommendationGmcOutcome.values()) {
      if (gmcOutcome.outcome.equals(value)) {
        return gmcOutcome;
      }
    }
    return UNDER_REVIEW;
  }

  public String getOutcome() {
    return this.outcome;
  }
}
