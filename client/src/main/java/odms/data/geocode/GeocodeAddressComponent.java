package odms.data.geocode;

public class GeocodeAddressComponent {
    private final String longName;
    private final String shortName;
    private final String[] types;

    public GeocodeAddressComponent(String longName, String shortName, String[] types) {
        this.longName = longName;
        this.shortName = shortName;
        this.types = types;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    public String[] getTypes() {
        return types;
    }
}
