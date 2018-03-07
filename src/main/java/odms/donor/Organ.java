package ODMS.Donor;

public enum Organ {
    LIVER("Liver"),
    KIDNEY("Kidney"),
    PANCREAS("Pancreas"),
    HEART("Heart"),
    LUNG("Lung"),
    INTESTINE("Intestine"),
    CORNEA("Cornea"),
    MIDDLE_EAR("Middle Ear"),
    SKIN("Skin"),
    BONE("Bone"),
    BONE_MARROW("Bone Marrow"),
    CONNECTIVE_TISSUE("Connective Tissue");

    private String name;

    public String getName() {
        return name;
    }

    Organ(String name) {
        this.name = name;
    }
}
