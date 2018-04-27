package odms.profile;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum Organ {
    LIVER("liver"),
    KIDNEY("kidney"),
    PANCREAS("pancreas"),
    HEART("heart"),
    LUNG("lung"),
    INTESTINE("intestine"),
    CORNEA("cornea"),
    MIDDLE_EAR("middle-ear"),
    SKIN("skin"),
    BONE("bone"),
    BONE_MARROW("bone-marrow"),
    CONNECTIVE_TISSUE("connective-tissue");

    private String name;

    public String getName() {
        return name;
    }

    /**
     * Generate an ArrayList of Strings with organs capitalised appropriately.
     * @return ArrayList of Organ name Strings
     */
    public List<String> toArrayList() {
        ArrayList<String> organs = new ArrayList<>();

        for (Organ organ : new ArrayList<>(EnumSet.allOf(Organ.class))) {
            ArrayList<String> organFullName = new ArrayList<>();
            for (String organTerm : organ.getName().split("-")) {
                organFullName.add(Character.toUpperCase(organTerm.charAt(0)) + organTerm.substring(1));
            }
            organs.add(String.join(" ", organFullName));
        }
        return organs;
    }

    Organ(String name) {
        this.name = name;
    }
}
