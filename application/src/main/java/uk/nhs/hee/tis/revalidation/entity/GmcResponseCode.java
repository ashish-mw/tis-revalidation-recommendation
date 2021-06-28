package uk.nhs.hee.tis.revalidation.entity;

public enum GmcResponseCode {

  SUCCESS("0", "Success"),
  MISSING_OR_INVALID_GMC_REF_NUMBER("70", "Missing / Invalid Doctor GMC reference number"),
  MISSING_OR_INVALID_DESIGNATED_BODY("80", "Missing / Invalid Designated Body code"),
  YOUR_ACCOUNT_DOES_NOT_HAVE_ACCESS_TO_DB("81", "Your account does not have access to this DB"),
  MISSING_DOCTOR_SURNAME("82", "Missing Doctor surname"),
  MISSING_OR_INVALID_RO_GMC_REF_NUMBER("84",
      "Missing / Invalid Responsible Officer GMC reference number"),
  MISSING_RO_SURNAME("85", "Missing Responsible Officer’s surname"),
  INVALID_RECOMMENDATION("87", "Invalid Recommendation"),
  INVALID_RECOMMENDATION_REASON("88", "Invalid Recommendation Reason"),
  INVALID_OR_UNEXPECTED_DEFER_DATE("89", "Invalid / Unexpected Defer Date"),
  DOCTOR_NOT_SUBJECT_TO_REVALIDATION("90", "Doctor not subject to revalidation"),
  OPEN_RECOMMENDATION_EXISTS("91", "Open recommendation exists"),
  INTERNAL_ERROR("98", "Internal error"),
  INVALID_CREDENTIALS("99", "Invalid Credentials (user name / password / IP address)"),
  MISSING_INTERNAL_USER("110", "Missing Internal User"),
  MISSING_OR_INVALID_REASON_CODE("120", "Missing / Invalid Change Code (Reason)"),
  DOCTOR_NOT_ASSOCIATED_WITH_DESIGNATED_BODY("140",
      "Doctor not associated with your Designated Body");

  final String code;
  final String message;

  GmcResponseCode(final String code, final String message) {
    this.code = code;
    this.message = message;
  }

  public static GmcResponseCode fromCode(final String code) {
    for (final var responseCode : GmcResponseCode.values()) {
      if (responseCode.getCode().equals(code)) {
        return responseCode;
      }
    }
    return null;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
