package odms.model.profile;

import java.util.Set;
import odms.model.enums.OrganEnum;

public class OrganConflictException extends Exception {

    private final OrganEnum organ;
    private final Set<OrganEnum> organs;

    public OrganConflictException() {
        this("");
    }

    public OrganConflictException(String message) {
        super(message);
        this.organ = null;
        this.organs = null;
    }

    public OrganConflictException(String message, Set<OrganEnum> organs) {
        super(message);
        this.organ = null;
        this.organs = organs;
    }

    public OrganConflictException(String message, OrganEnum organ) {
        super(message);
        this.organ = organ;
        this.organs = null;
    }

    public OrganEnum getOrgan() {
        return organ;
    }

    public Set<OrganEnum> getOrgans() {
        return organs;
    }
}
