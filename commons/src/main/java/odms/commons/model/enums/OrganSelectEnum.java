package odms.commons.model.enums;

/**
 * Constants for the different organ selection views.
 */
public enum OrganSelectEnum {
    DONATED,
    DONATING,
    REQUIRED,
    PROCEDURE;

    @Override
    public String toString() {
        switch (this) {
            case DONATED:
                return "Past Donations";
            case DONATING:
                return "Organs to Donate";
            case REQUIRED:
                return "Organs Required";
            case PROCEDURE:
                return "Procedure Affected Organs";
            default:
                throw new IllegalArgumentException();
        }
    }
}
