package odms.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;

public enum Organ {
    BONE("bone"),
    BONE_MARROW("bone-marrow"),
    CONNECTIVE_TISSUE("connective-tissue"),
    CORNEA("cornea"),
    HEART("heart"),
    INTESTINE("intestine"),
    KIDNEY("kidney"),
    LIVER("liver"),
    LUNG("lung"),
    MIDDLE_EAR("middle-ear"),
    PANCREAS("pancreas"),
    SKIN("skin");

    private String name;

    public String getName() {
        return name;
    }

    /**
     * Generate an ArrayList of Strings with organs capitalised appropriately.
     * @return ArrayList of Organ name Strings
     */
    public static ArrayList<String> toArrayList() {
        ArrayList<String> organs = new ArrayList<>();

        for (Organ organ : new ArrayList<>(EnumSet.allOf(Organ.class))) {
            ArrayList<String> organFullName = new ArrayList<>();
            for (String organTerm : organ.getName().split("-")) {
                organFullName.add(Character.toUpperCase(organTerm.charAt(0)) + organTerm.substring(1));
            }
            organs.add(String.join(" ", organFullName));
        }

        Collections.sort(organs);

        return organs;
    }

    Organ(String name) {
        this.name = name;
    }
}
