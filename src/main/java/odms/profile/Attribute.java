package odms.profile;

public enum Attribute {
    GIVENNAMES("given-names"),
    LASTNAMES("last-names"),
    DATEOFBIRTH("dob"),
    DATEOFDEATH("dod"),
    GENDER("gender"),
    HEIGHT("height"),
    WEIGHT("weight"),
    BLOODTYPE("blood-type"),
    ADDRESS("address"),
    REGION("region"),
    IRD("ird");

    private String text;

    public String getText() {
        return text;
    }

    Attribute(String text) {
        this.text = text;
    }
}