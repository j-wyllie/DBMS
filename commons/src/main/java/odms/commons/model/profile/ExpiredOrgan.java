package odms.commons.model.profile;

import odms.commons.model.enums.OrganEnum;

import java.time.LocalDateTime;

/**
 * ExpiredOrgan class, wrapper of OrganEnum for organs that have been manually expired.
 */
public class ExpiredOrgan {
    private OrganEnum organ;
    private String note;
    private String clinicianName;
    private LocalDateTime expiryDate;

    /**
     * Constructor for ExpiredOrgan class.
     * @param organ organEnum object wraps around
     * @param note string, reason for expiry
     * @param clinicianName name of clinician that expired organ
     * @param expiryDate date organ was manually expired
     */
    public ExpiredOrgan(OrganEnum organ, String note, String clinicianName,
                        LocalDateTime expiryDate) {
        this.organ = organ;
        this.note = note;
        this.clinicianName = clinicianName;
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public String getOrganName() {
        return organ.getNamePlain();
    }

    public OrganEnum getOrgan() {
        return organ;
    }

    public String getClinicianName() {
        return clinicianName;
    }

    public String getNote() {
        return note;
    }
}
