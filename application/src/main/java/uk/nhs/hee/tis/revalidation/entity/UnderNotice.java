package uk.nhs.hee.tis.revalidation.entity;

public enum UnderNotice {

    YES("Yes"), NO("No"), ON_HOLD("On Hold");

    private final String notice;

    UnderNotice(final String notice) {
        this.notice = notice;
    }

    public String value() {
        return notice;
    }

    public static UnderNotice fromString(final String value) {
        for (final UnderNotice underNotice : UnderNotice.values()) {
            if (underNotice.notice.equals(value)) {
                return underNotice;
            }
        }
        return null;
    }

}
