package odms.user;

public enum UserType {
    CLINICIAN("clinician"),
    DONOR("donor");


    private String name;

    public String getName() {
        return name;
    }

    UserType(String name) {
        this.name = name;
    }
}

