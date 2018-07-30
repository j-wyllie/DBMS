package odms.model.data;

public class NHIConflictException extends Exception {

    private String nhi;

    public NHIConflictException(String message, String nhi) {
        super(message);
        this.nhi = nhi;
    }

    public String getNHI() {
        return nhi;
    }

}
