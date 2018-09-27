package odms.commons.model.locations;

import odms.commons.model.enums.OrganEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Hospital class. Objects describe details about a hospital.
 */
public class Hospital {

    private String name;
    private Double latitude;
    private Double longitude;
    private String address;
    private List<Boolean> programs;
    private Integer id;

    /**
     * Constructor for hospital objects.
     *
     * @param name Name of hte hospital
     * @param lat latitude of hte hospital
     * @param lon longitude of hte hospital
     * @param address address of the hospital
     * @param programs list of transplant programs that the hospital provides
     * @param id the unique id for the hospital
     */
    public Hospital(String name, Double lat, Double lon, String address, List<Boolean> programs,
            Integer id) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
        this.address = address;
        this.programs = programs;
        this.id = id;
    }

    /**
     * Constructor for hospital objects.
     * @param name Name of hte hospital
     * @param lat latitude of hte hospital
     * @param lon longitude of hte hospital
     * @param address address of the hospital
     * @param programs list of transplant programs that the hospital provides
     */
    public Hospital(String name, Double lat, Double lon, String address, List<Boolean> programs) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
        this.address = address;
        this.programs = programs;
    }

    /**
     * Constructor for hospital objects.
     *
     * @param name Name of hte hospital
     * @param lat latitude of hte hospital
     * @param lon longitude of hte hospital
     * @param address address of the hospital
     */
    public Hospital(String name, Double lat, Double lon, String address) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
        this.address = address;
        programs = new ArrayList<>();
        for (int i = 0; i < OrganEnum.values().length; i++) {
            programs.add(false);
        }
    }

    /**
     * Constructor for hospital objects.
     *
     * @param name Name of hte hospital
     * @param lat latitude of hte hospital
     * @param lon longitude of hte hospital
     * @param address address of the hospital
     * @param id the unique id for the hospital
     */
    public Hospital(String name, Double lat, Double lon, String address, Integer id) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
        this.address = address;
        this.id = id;
        programs = new ArrayList<>();
        for (int i = 0; i < OrganEnum.values().length; i++) {
            programs.add(false);
        }
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

    public List<Boolean> getPrograms() {
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

    public void setPrograms(List<Boolean> programs) {
        this.programs = programs;
    }
}
