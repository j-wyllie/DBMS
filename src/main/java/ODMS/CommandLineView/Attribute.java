package ODMS.CommandLineView;

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
    REGION("region");

    private String text;

    public String getText() {
        return text;
    }

    private Attribute(String text) {
        this.text = text;
    }
}
