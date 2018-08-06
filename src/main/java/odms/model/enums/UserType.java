package odms.model.enums;

public enum UserType {
    ADMIN("administrator"),
    CLINICIAN("clinician"),
    DONOR("profile"),
    PROFILE("profile");

    private String name;

    UserType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

