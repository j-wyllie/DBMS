package odms.commons.model.profile;

public enum Attribute {
    ADDRESS("address"),
    ALCOHOL_CONSUMPTION("alcohol-consumption"),
    BLOOD_PRESSURE_DIASTOLIC("blood-pressure-diastolic"),
    BLOOD_PRESSURE_SYSTOLIC("blood-pressure-systolic"),
    BLOOD_TYPE("blood-type"),
    COUNTRY("country"),
    DATE_OF_BIRTH("dob"),
    DATE_OF_DEATH("dod"),
    EMAIL("email"),
    GIVEN_NAMES("given-names"),
    GENDER("gender"),
    HEIGHT("height"),
    IS_SMOKER("is-smoker"),
    LAST_NAMES("last-names"),
    NHI("nhi"),
    PHONE("phone"),
    REGION("region"),
    WEIGHT("weight");


    private String text;

    public String getText() {
        return text;
    }

    Attribute(String text) {
        this.text = text;
    }
}
