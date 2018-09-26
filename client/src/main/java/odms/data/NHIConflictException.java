package odms.data;

public class NHIConflictException extends Exception {

    private final String nhi;

    public NHIConflictException(String message, String nhi) {
        super(message);
        this.nhi = nhi;
    }

    public String getNHI() {
        return nhi;
    }

}
