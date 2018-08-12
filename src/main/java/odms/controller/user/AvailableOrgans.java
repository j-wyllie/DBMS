package odms.controller.user;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import odms.controller.database.DAOFactory;
import odms.controller.database.ProfileDAO;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;

public class AvailableOrgans {

    public final static long ONE_SECOND = 1000;
    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long ONE_DAY = ONE_HOUR * 24;
    public final static long ONE_YEAR = ONE_DAY * 365;


    public void setOrganExpired(OrganEnum organ, Profile profile) {
        profile.getOrgansDonating().remove(organ);
        profile.getOrgansExpired().add(organ);
    }

    public void checkOrganExpired(OrganEnum organ, Profile profile) {
        if(!profile.getDateOfDeath().equals(null) && LocalDateTime.now().isAfter(getExpiryTime(organ, profile))) {
            setOrganExpired(organ,profile);
        }
    }

    public static LocalDateTime getExpiryTime(OrganEnum organ, Profile profile) {
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
     * Gives remaining time in milliseconds that a organ has until it expires
     * @param organ the given organ object
     * @param profile the donor that the organ belongs to
     * @return the time a organ has til it expires in milliseconds
     */
    public static Double getTimeRemaining(OrganEnum organ, Profile profile) {

        Long timeToExpiry = Duration.between(LocalDateTime.now(), getExpiryTime(organ, profile))
                .toMillis();

        return Double.valueOf(timeToExpiry);
    }

    /**
     * Gives the expiry time of a 'fresh organ'
     * @param organ the organ given
     * @return the expiry time for the 'fresh' given organ
     */
    public static Double getExpiryLength(OrganEnum organ) {

        Long expiryTime;

        switch (organ) {
            case HEART:
                expiryTime = 6 * ONE_HOUR;
                break;
            case PANCREAS:
                expiryTime = ONE_DAY;
                break;
            case LIVER:
                expiryTime = ONE_DAY;
                break;
            case KIDNEY:
                expiryTime = 72 * ONE_HOUR;
                break;
            case CORNEA:
                expiryTime = 7 * ONE_DAY;
                break;
            default:
                expiryTime = 5 * ONE_YEAR;
                break;
        }

        return Double.valueOf(expiryTime);
    }

    /**
     * Calculates how long a Organ has til expiry, returns in formatted string
     * @param organ Given organ
     * @param profile Given profile the organ belongs to
     * @return How long the organ has til expiry in days, minutes, hours and seconds
     */
    public static String getTimeToExpiryFormatted(OrganEnum organ, Profile profile) {

        String durationFormatted = "";
        Long timeToExpiry = Duration.between(LocalDateTime.now(), getExpiryTime(organ, profile))
                .toMillis();

        long temp = 0;
        if (timeToExpiry >= ONE_SECOND) {
            temp = timeToExpiry / ONE_HOUR;
            if (temp > 0) {
                if (temp > 1) {
                    durationFormatted += temp + " hours ";
                } else {
                    durationFormatted += temp + " hour ";
                }
                timeToExpiry -= temp * ONE_HOUR;
            }
            temp = timeToExpiry / ONE_SECOND;
            if (temp > 0) {
                if (temp > 1) {
                    durationFormatted += temp + " seconds ";
                } else {
                    durationFormatted += temp + " second ";
                }
            }
        }

        return durationFormatted;

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

    /**
     * Returns a list of available organs as per the filters provided
     * @param organs list of organs to filter by
     * @param countries list of countries to filter by
     * @param regions list of regions to filter by
     * @return
     */
    public ObservableList<Map.Entry<Profile,OrganEnum>> performSearch(ObservableList organs, ObservableList countries, ObservableList regions) {

        ObservableList<Map.Entry<Profile,OrganEnum>> searchResults = null;

        return searchResults;
    }


}
