package odms.commons.model.profile;

import odms.commons.model.enums.OrganEnum;

import java.time.LocalDateTime;

public class ExpiredOrgan {
    private OrganEnum organ;
    private String note;
    private String clinicianName;
    private LocalDateTime expiryDate;

    public ExpiredOrgan (OrganEnum organ, String note, String clinicianName, LocalDateTime expiryDate){
        this.organ = organ;
        this.note = note;
        this.clinicianName = clinicianName;
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public String getOrgan() {
        return organ.getNamePlain();
    }

    public String getClinicianName() {
        return clinicianName;
    }

    public String getNote() {
        return note;
    }
}
