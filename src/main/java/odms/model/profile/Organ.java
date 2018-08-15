package odms.model.profile;

import odms.model.enums.OrganEnum;

import java.time.LocalDateTime;

/**
 * Data model class that defines an organ object.
 */
public class Organ {
    private OrganEnum organEnum;
    private LocalDateTime dateOfRegistration;

    /**
     * Constructs an organ object.
     * @param o OrganEnum of the organ to create
     * @param time data and time the organ was added to a profile
     */
    public Organ(OrganEnum o, LocalDateTime time) {
        this.organEnum = o;
        this.dateOfRegistration = time;
    }

    public LocalDateTime getDate() {
        return dateOfRegistration;
    }

    public OrganEnum getOrganEnum() {
        return organEnum;
    }
}
