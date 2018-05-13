package odms.user;

public enum UserType {
    CLINICIAN("clinician"),
    DONOR("profile"),
    PROFILE("profile"),
    ADMIN("administrator");

    private String name;

    public String getName() {
        return name;
    }

    UserType(String name) {
        this.name = name;
    }
}

