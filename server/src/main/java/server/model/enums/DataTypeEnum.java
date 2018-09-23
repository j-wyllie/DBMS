package server.model.enums;

/**
 * The data types for requests and responses used by the server.
 */
public enum DataTypeEnum {
    JSON("application/json");

    private final String type;

    DataTypeEnum(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
