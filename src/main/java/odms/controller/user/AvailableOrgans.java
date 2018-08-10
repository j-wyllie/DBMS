package odms.controller.user;

import odms.controller.GuiMain;
import odms.controller.database.DAOFactory;
import odms.controller.database.MySqlProfileDAO;
import odms.controller.database.ProfileDAO;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * Generates a collection of a profile and organ for each organ that a receiver donates after death
     *
     *  @return Collection of Profile and Organ that match
     */
    public List<Map.Entry<Profile, OrganEnum>> getAllOrgansAvailable() throws SQLException{
        List<Map.Entry<Profile, OrganEnum>> donaters = new ArrayList<>();
        ProfileDAO database = DAOFactory.getProfileDao();

        List<Profile> allDonaters = database.getDead();
        System.out.println(allDonaters.size());

        for (Profile profile : allDonaters) {
            for (OrganEnum organ : profile.getOrgansDonating()) {
                Map.Entry<Profile, OrganEnum> pair = new AbstractMap.SimpleEntry<>(profile, organ);
                if(!donaters.contains(pair)) {
                    donaters.add(pair);
                }
            }
        }
        return donaters;
    }

    

}
