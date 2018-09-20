package server.model.enums;

public enum ResponseMsgEnum {
    BAD_REQUEST("Bad Request"),
    FORBIDDEN("Forbidden"),
    INTERNAL_SERVER_ERROR("Internal Server Error");

    private final String message;

    ResponseMsgEnum(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
