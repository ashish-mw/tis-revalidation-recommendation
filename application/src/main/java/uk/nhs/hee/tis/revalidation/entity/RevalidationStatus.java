package uk.nhs.hee.tis.revalidation.entity;

public enum RevalidationStatus {

    NOT_STARTED("Not started"),
    STARTED("Started"),
    READY_TO_REVIEW("Ready to review"),
    READY_TO_SUBMIT("Ready to submit"),
    SUBMITTED_TO_GMC("Submitted to GMC");

    private String status;

    private RevalidationStatus(final String value) {
        this.status = value;
    }

    public String value() {
        return this.status;
    }

    public RevalidationStatus fromValue(final String status) {
        for (RevalidationStatus revalidationStatus : RevalidationStatus.values()) {
            if (revalidationStatus.value().equalsIgnoreCase(status)) {
                return revalidationStatus;
            }
        }
        return NOT_STARTED;
    }
}