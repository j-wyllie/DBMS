package odms.commons.model.locations;

import java.util.List;

public class Hospital {

    private String name;
    private Double latitude;
    private Double longitude;
    private String address;
    private List programs;

    public Hospital(String name, Double lat, Double lon, String address, List programs) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
        this.address = address;
        this.programs = programs;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public List getPrograms() {
        return programs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setPrograms(List programs) {
        this.programs = programs;
    }
}
