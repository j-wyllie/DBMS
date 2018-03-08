package odms.donor;

public enum Organ {
    LIVER("liver"),
    KIDNEY("kidney"),
    PANCREAS("pancreas"),
    HEART("heart"),
    LUNG("lung"),
    INTESTINE("intestine"),
    CORNEA("cornea"),
    MIDDLE_EAR("middle ear"),
    SKIN("skin"),
    BONE("bone"),
    BONE_MARROW("bone marrow"),
    CONNECTIVE_TISSUE("connective tissue");

    private String name;

    public String getName() {
        return name;
    }

    Organ(String name) {
        this.name = name;
    }
}
