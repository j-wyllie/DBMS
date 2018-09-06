package odms.commons.model.locations;

import java.util.List;

/**
 * Hospital class. Objects describe details about a hospital.
 */
public class Hospital {

    private String name;
    private Double latitude;
    private Double longitude;
    private String address;
    private List programs;
    private Integer id;

    /**
     * Constructor for hospital objects.
     * @param name Name of hte hospital
     * @param lat latitude of hte hospital
     * @param lon longitude of hte hospital
     * @param address address of the hospital
     * @param programs list of transplant programs that the hospital provides
     * @param id the unique id for the hospital
     */
    public Hospital(String name, Double lat, Double lon, String address, List programs,
            Integer id) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
        this.address = address;
        this.programs = programs;
        this.id = id;
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

    public Integer getId() {
        return id;
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
