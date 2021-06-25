package uk.nhs.hee.tis.revalidation.entity;

public enum RecommendationType {

  REVALIDATE("1", "Revalidate"), DEFER("2", "Defer"), NON_ENGAGEMENT("3", "Non_Engagement");

  final String code;
  final String type;

  RecommendationType(final String code, final String type) {
    this.code = code;
    this.type = type;
  }

  public String getCode() {
    return this.code;
  }
}
