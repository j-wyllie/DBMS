package server.model.enums;

public enum KeyEnum {
    ID("id"),
    NAME("name"),
    PENDING("pending"),
    VALID("valid");

    private final String key;

    KeyEnum(final String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
