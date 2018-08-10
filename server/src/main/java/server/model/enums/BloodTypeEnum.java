package server.model.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;

public enum BloodTypeEnum {
    O_NEGATIVE("O-"),
    O_POSITIVE("O+"),
    A_NEGATIVE("A-"),
    A_POSITIVE("A+"),
    B_NEGATIVE("B-"),
    B_POSITIVE("B+"),
    AB_NEGATIVE("AB-"),
    AB_POSITIVE("AB+");

    private String name;

    BloodTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Generate an ArrayList of Strings with the BloodTypes
     *
     * @return ArrayList of BloodType name Strings
     */
    public static ArrayList<String> toArrayList() {
        ArrayList<String> bloodTypes = new ArrayList<>();

        for (BloodTypeEnum bloodType : new ArrayList<>(EnumSet.allOf(BloodTypeEnum.class))) {
            bloodTypes.add(bloodType.getName());
        }

        Collections.sort(bloodTypes);

        return bloodTypes;
    }
}
