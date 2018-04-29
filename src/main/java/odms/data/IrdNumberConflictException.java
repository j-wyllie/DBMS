package odms.data;

public class IrdNumberConflictException extends Exception {

    private Integer irdNumber;

    public IrdNumberConflictException(String message, int irdNumber) {
        super(message);
        this.irdNumber = irdNumber;
    }

    public Integer getIrdNumber() {
        return irdNumber;
    }

}
