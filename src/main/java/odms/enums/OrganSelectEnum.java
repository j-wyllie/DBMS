package odms.enums;

/**
 * Constants for the different organ selection views
 */
public enum OrganSelectEnum {
    DONATED,
    DONATING,
    REQUIRED;

    @Override
    public String toString() {
        switch (this) {
            case DONATED:
                return "Organs to Donate";
            case DONATING:
                return "Past Donations";
            case REQUIRED:
                return "Organs Required";
            default:
                throw new IllegalArgumentException();
        }
    }
}
