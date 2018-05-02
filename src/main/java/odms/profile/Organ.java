package odms.profile;

import java.time.LocalDate;
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
    private LocalDate date = LocalDate.now();

    public String getName() {
        return name;
    }

    public LocalDate getDate() { return date; }

    /**
     * Correctly space and case the name of the organ for display/printing purposes.
     *
     * @return corrected organ String
     */
    public String getNamePlain() {
        ArrayList<String> organNamePlain = new ArrayList<>();
        for (String organTerm : getName().split("-")) {
            organNamePlain.add(Character.toUpperCase(organTerm.charAt(0)) + organTerm.substring(1));
        }
        return String.join(" ", organNamePlain);
    }

    /**
     * Generate an ArrayList of Strings with organs capitalised appropriately.
     * @return ArrayList of Organ name Strings
     */
    public static ArrayList<String> toArrayList() {
        ArrayList<String> organs = new ArrayList<>();

        for (Organ organ : new ArrayList<>(EnumSet.allOf(Organ.class))) {
            organs.add(organ.getNamePlain());
        }

        Collections.sort(organs);

        return organs;
    }

    Organ(String name) {
        this.name = name;
    }


}
