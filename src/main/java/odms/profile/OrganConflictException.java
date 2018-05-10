package odms.profile;

import java.util.HashSet;

public class OrganConflictException extends Exception {

    private Organ organ;
    private HashSet<Organ> organs;

    public OrganConflictException() {
        super();
    }

    public OrganConflictException(String message) {
        super(message);
    }

    public OrganConflictException(String message, HashSet<Organ> organs) {
        super(message);
        this.organs = organs;
    }

    public OrganConflictException(String message, Organ organ) {
        super(message);
        this.organ = organ;
    }

    public Organ getOrgan() {
        return organ;
    }

    public HashSet<Organ> getOrgans() {
        return organs;
    }
}
