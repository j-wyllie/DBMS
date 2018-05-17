package odms.profile;

import java.util.HashSet;
import odms.enums.OrganEnum;

public class OrganConflictException extends Exception {

    private OrganEnum organ;
    private HashSet<OrganEnum> organs;

    public OrganConflictException() {
        super();
    }

    public OrganConflictException(String message) {
        super(message);
    }

    public OrganConflictException(String message, HashSet<OrganEnum> organs) {
        super(message);
        this.organs = organs;
    }

    public OrganConflictException(String message, OrganEnum organ) {
        super(message);
        this.organ = organ;
    }

    public OrganEnum getOrgan() {
        return organ;
    }

    public HashSet<OrganEnum> getOrgans() {
        return organs;
    }
}
