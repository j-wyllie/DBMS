package server.model.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum NewZealandRegionsEnum {
    NORTHLAND("Northland"),
    AUCKLAND("Auckland"),
    WAIKATO("Waikato"),
    BAY_OF_PLENTY("Bay of Plenty"),
    GISBORNE("Gisborne"),
    HAWKES_BAY("Hawke's Bay"),
    TARANAKI("Taranaki"),
    MANAWATU_WANGANUI("Manawatu-Wanganui"),
    WELLINGTON("Wellington"),
    TASMAN("Tasman"),
    NELSON("Nelson"),
    MARLBOROUGH("Marlborough"),
    WEST_COAST("West Coast"),
    CANTERBURY("Canterbury"),
    OTAGO("Otago"),
    SOUTHLAND("Southland"),
    CHATHAM_ISLANDS("Chatham Islands");

    private String name;

    NewZealandRegionsEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Generate an ArrayList of strings of country names.
     * @return the array list of countries
     */
    public static List<String> toArrayList() {
        ArrayList<NewZealandRegionsEnum> regions = new ArrayList<>(EnumSet.allOf(NewZealandRegionsEnum.class));
        ArrayList<String> regionStrings = new ArrayList<>();

        for (NewZealandRegionsEnum region : regions) {
            regionStrings.add(region.getName());
        }

        return regionStrings;
    }
}
