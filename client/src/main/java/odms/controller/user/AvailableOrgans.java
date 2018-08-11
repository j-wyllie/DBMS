package odms.controller.user;

import odms.commons.model.enums.OrganEnum;
import odms.model.profile.Profile;

import java.time.LocalDateTime;

public class AvailableOrgans {

    public void setOrganExpired(OrganEnum organ, Profile profile) {
        profile.getOrgansDonating().remove(organ);
        profile.getOrgansExpired().add(organ);
    }

    public void checkOrganExpired(OrganEnum organ, Profile profile) {
        if(!profile.getDateOfDeath().equals(null) && LocalDateTime.now().isAfter(getExpiryTime(organ, profile))) {
            setOrganExpired(organ,profile);
        }
    }

    public LocalDateTime getExpiryTime(OrganEnum organ, Profile profile) {
        LocalDateTime expiryTime;
        switch (organ) {
            case HEART:
                expiryTime = profile.getDateOfDeath().plusHours(6);
                break;
            case PANCREAS:
                expiryTime = profile.getDateOfDeath().plusHours(24);
                break;
            case LIVER:
                expiryTime = profile.getDateOfDeath().plusHours(24);
                break;
            case KIDNEY:
                expiryTime = profile.getDateOfDeath().plusHours(72);
                break;
            case CORNEA:
                expiryTime = profile.getDateOfDeath().plusDays(7);
                break;
            default:
                expiryTime = profile.getDateOfDeath().plusYears(5);
                break;
        }
        return expiryTime;
    }

    

}
