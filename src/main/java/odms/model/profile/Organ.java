package odms.model.profile;

import odms.model.enums.OrganEnum;

import java.time.LocalDateTime;

public class Organ {
    private OrganEnum organEnum;
    private LocalDateTime dateOfRegistration = null;

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
