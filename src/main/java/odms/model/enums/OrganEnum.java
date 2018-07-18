package odms.model.enums;

import java.time.LocalDate;
import java.util.*;

public enum OrganEnum {
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

    OrganEnum(String name) {
        this.name = name;
    }

    /**
     * Generate an ArrayList of Strings with organs capitalised appropriately.
     *
     * @return ArrayList of Organ name Strings
     */
    public static ArrayList<String> toArrayList() {
        ArrayList<String> organs = new ArrayList<>();

        for (OrganEnum organ : new ArrayList<>(EnumSet.allOf(OrganEnum.class))) {
            organs.add(organ.getNamePlain());
        }

        Collections.sort(organs);

        return organs;
    }

    /**
     * Generate a HashSet of Organs from a List of Organ Strings
     *
     * @param organStrings List of Organ Strings
     * @return HashSet of Organs
     */
    public static HashSet<OrganEnum> stringListToOrganSet(List<String> organStrings) {
        HashSet<OrganEnum> organs = new HashSet<>();

        for (String organ : organStrings) {
            organs.add(valueOf(organ.toUpperCase()));
        }

        return organs;
    }

    /**
     * Take a HashSet of Organ objects and return a sorted comma delimited string
     *
     * @param organs Organ HashSet to be converted
     * @return comma delimited string
     */
    public static String organSetToString(HashSet<OrganEnum> organs) {
        List<String> organsList = new ArrayList<>();

        for (OrganEnum organ : organs) {
            organsList.add(organ.getNamePlain());
        }

        Collections.sort(organsList);

        return String.join(", ", organsList);
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

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

}