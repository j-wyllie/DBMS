package odms.Model.user;

public enum UserType {
    ADMIN("administrator"),
    CLINICIAN("clinician"),
    DONOR("profile"),
    PROFILE("profile");

    private String name;

    public String getName() {
        return name;
    }

    UserType(String name) {
        this.name = name;
    }
}

