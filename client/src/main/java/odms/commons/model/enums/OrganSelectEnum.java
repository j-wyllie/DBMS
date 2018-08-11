package odms.commons.model.enums;

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
                return "Past Donations";
            case DONATING:
                return "Organs to Donate";
            case REQUIRED:
                return "Organs Required";
            default:
                throw new IllegalArgumentException();
        }
    }
}
