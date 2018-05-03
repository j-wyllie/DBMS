package odms.profile;

public class OrganConflictException extends Exception {

    private Organ organ;

    public OrganConflictException() {
        super();
    }

    public OrganConflictException(String message) {
        super(message);
    }

    public OrganConflictException(String message, Organ organ) {
        super(message);
        this.organ = organ;
    }

    public Organ getOrgan() {
        return organ;
    }
}
