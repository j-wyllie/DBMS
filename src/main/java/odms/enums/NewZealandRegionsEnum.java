package odms.enums;

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
    SOUTHLAND("Southland");

    private String name;

    NewZealandRegionsEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
