package odms.profile;

import java.time.LocalDate;
import odms.enums.OrganEnum;

/**
 * Organ class that contains the organ type and stores the current date when created.
 */
public class Organ {

    OrganEnum organType;
    LocalDate dateOfRegistration;

    public Organ(OrganEnum organType) {
        this.organType = organType;
        this.dateOfRegistration = LocalDate.now();
    }

    public LocalDate getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(LocalDate date) { dateOfRegistration = date; }

    public OrganEnum getOrganType() {
        return organType;
    }
}
