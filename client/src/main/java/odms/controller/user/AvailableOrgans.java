package odms.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import odms.controller.database.DAOFactory;
import odms.controller.database.MySqlOrganDAO;
import odms.controller.database.ProfileDAO;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Math.abs;

public class AvailableOrgans {

    public final static long ONE_SECOND = 1000;
    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long ONE_DAY = ONE_HOUR * 24;
    public final static long ONE_YEAR = ONE_DAY * 365;
    private List<Map.Entry<Profile, OrganEnum>> donaters = new ArrayList<>();
    private List<Profile> allDonaters = new ArrayList<>();

    private odms.view.user.AvailableOrgans view;

    public void setView(odms.view.user.AvailableOrgans v) {
        view = v;
    }


    public static Long getWaitTimeRaw(OrganEnum selectedOrgan, HashSet<OrganEnum> organsRequired, Profile p) {
        LocalDateTime dateOrganRegistered = LocalDateTime.now();

        for (OrganEnum organ : organsRequired) {
            if (organ.getNamePlain().equalsIgnoreCase(selectedOrgan.getNamePlain())) {
                if (organ.getDate(p) != null) {
                    dateOrganRegistered = organ.getDate(p);
                } else {
                    return Long.valueOf(-1);
                }
            }
        }
        return abs(Duration.between(LocalDateTime.now(), dateOrganRegistered).toMillis());
    }

    public static String getWaitTime(OrganEnum selectedOrgan, HashSet<OrganEnum> organsRequired, Profile p) {

        LocalDateTime dateOrganRegistered = null;
        String durationFormatted = "";

        Long waitTime = getWaitTimeRaw(selectedOrgan, organsRequired, p);
        if (waitTime == -1) {
            // Means a date was not entered when a organ was registered
            return "Insufficient data";
        }

        if (waitTime == 0) {
            return "Registered today";
        }

        long temp = 0;
        if (waitTime >= ONE_SECOND) {
            temp = waitTime / ONE_DAY;
            if (temp > 0) {
                if (temp > 1) {
                    durationFormatted += temp + " days ";
                } else {
                    durationFormatted += temp + " day ";
                }
                waitTime -= temp * ONE_DAY;

            }
            temp = waitTime / ONE_HOUR;
            if (temp > 0) {
                if (temp > 1) {
                    durationFormatted += temp + " hours ";
                } else {
                    durationFormatted += temp + " hour ";
                }
            } else {
                temp = waitTime / ONE_MINUTE;
                if (temp >= 0) {
                    durationFormatted += temp + "m ";
                }
            }
        }
        return durationFormatted;
    }

    public void setOrganExpired(OrganEnum organ, Profile profile) {
        private void setOrganExpired (OrganEnum organ, Profile profile){
            profile.getOrgansDonating().remove(organ);
            MySqlOrganDAO dao = new MySqlOrganDAO();
            dao.removeDonating(profile, organ);
            profile.getOrgansExpired().add(organ);
        }

        public void checkOrganExpired (OrganEnum organ, Profile profile,
                Map.Entry < Profile, OrganEnum > m){
            if (!profile.getDateOfDeath().equals(null) && LocalDateTime.now()
                    .isAfter(getExpiryTime(organ, profile))) {
                setOrganExpired(organ, profile);
            }
        }

        public void checkOrganExpiredListRemoval (OrganEnum organ, Profile profile,
                Map.Entry < Profile, OrganEnum > m){
            if (!profile.getDateOfDeath().equals(null) && LocalDateTime.now()
                    .isAfter(getExpiryTime(organ, profile))) {
                view.removeItem(m);
                setOrganExpired(organ, profile);
            }
        }

        public static LocalDateTime getExpiryTime (OrganEnum organ, Profile profile){
            LocalDateTime expiryTime;
            switch (organ) {
                case HEART:
                    expiryTime = profile.getDateOfDeath().plusHours(6);
                    break;
                case LUNG:
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
         *
         * @param organ   the given organ object
         * @param profile the donor that the organ belongs to
         * @return the time a organ has til it expires in milliseconds
         */
        public static Double getTimeRemaining (OrganEnum organ, Profile profile){

            Long timeToExpiry = Duration.between(LocalDateTime.now(), getExpiryTime(organ, profile))
                    .toMillis();

            return Double.valueOf(timeToExpiry);
        }

        /**
         * Gives the expiry time of a 'fresh organ'
         *
         * @param organ the organ given
         * @return the expiry time for the 'fresh' given organ
         */
        public static Double getExpiryLength (OrganEnum organ){

            Long expiryTime;

            switch (organ) {
                case HEART:
                    expiryTime = 6 * ONE_HOUR;
                    break;
                case LUNG:
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
         * Get remaining time in standard '5y 4d 3h 2m 1s' format.
         *
         * @param organ   the organ being checked against.
         * @param profile the selected profile.
         * @return formatted string.
         */
        public static String getTimeToExpiryStd (OrganEnum organ, Profile profile){
            return getTimeToExpiryFormatted(organ, profile, true);
        }

        /**
         * Get remaining time in standard '2h 1s' format.
         *
         * @param organ   the organ being checked against.
         * @param profile the selected profile.
         * @return formatted string.
         */
        public static String getTimeToExpiryHoursSeconds (OrganEnum organ, Profile profile){
            return getTimeToExpiryFormatted(organ, profile, false);
        }

        /**
         * Calculates how long a Organ has til expiry, returns in formatted string
         *
         * @param organ   Given organ
         * @param profile Given profile the organ belongs to
         * @return How long the organ has til expiry in days, minutes, hours and seconds
         */
        public static String getTimeToExpiryFormatted (OrganEnum organ, Profile profile, Boolean
        isStd){
            Long timeToExpiry = Duration.between(
                    LocalDateTime.now(),
                    getExpiryTime(organ, profile)
            ).toMillis();

            if (isStd) {
                return msToStandard(timeToExpiry);
            } else {
                return msToHoursAndSeconds(timeToExpiry);
            }

        }

        /**
         * Support function to convert ms to a string representing hours and seconds.
         *
         * @param timeToExpiry in ms
         * @return formatted string
         */
        private static String msToHoursAndSeconds (Long timeToExpiry){
            String durationFormatted = "";

            long temp;
            if (timeToExpiry >= ONE_SECOND) {

                temp = timeToExpiry / ONE_HOUR;
                if (temp > 0) {
                    durationFormatted += temp + "h ";
                    timeToExpiry -= temp * ONE_HOUR;
                }

                temp = timeToExpiry / ONE_SECOND;
                if (temp > 0) {
                    durationFormatted += temp + "s ";
                }
            }

            return durationFormatted;
        }

        /**
         * Support function to convert ms to a string representing years, hours, minutes and seconds.
         *
         * @param timeToExpiry in ms
         * @return formatted string
         */
        private static String msToStandard (Long timeToExpiry){
            String durationFormatted = "";

            long temp;
            if (timeToExpiry >= ONE_SECOND) {

                temp = timeToExpiry / ONE_YEAR;
                if (temp > 0) {
                    durationFormatted += temp + "y ";
                    timeToExpiry -= temp * ONE_YEAR;
                }

                temp = timeToExpiry / ONE_DAY;
                if (temp > 0) {
                    durationFormatted += temp + "d ";
                    timeToExpiry -= temp * ONE_DAY;
                }

                temp = timeToExpiry / ONE_HOUR;
                if (temp > 0) {
                    durationFormatted += temp + "h ";
                    timeToExpiry -= temp * ONE_HOUR;
                }

                temp = timeToExpiry / ONE_MINUTE;
                if (temp > 0) {
                    durationFormatted += temp + "m ";
                    timeToExpiry -= temp * ONE_MINUTE;
                }

                temp = timeToExpiry / ONE_SECOND;
                if (temp > 0) {
                    durationFormatted += temp + "s ";
                }

            }

            return durationFormatted;
        }

        /**
         * returns list of potential organ matches for a given organ and the donor the organ came from
         *
         * @param organAvailable the available organ
         * @param donorProfile   the donor the organ came from
         * @param selectedOrgan
         * @return a list of potential organ matches
         */
        public static ObservableList<Profile> getSuitableRecipientsSorted (OrganEnum organAvailable,
                Profile donorProfile, OrganEnum selectedOrgan){

            // sort by longest wait time first, then weight by closest location to where the donor profiles region of death
            ObservableList<Profile> potentialOrganMatches = FXCollections.observableArrayList();
            List<Profile> receivingProfiles = new ArrayList<>();

            String organLocation = donorProfile.getRegionOfDeath();
            String reqBloodType = donorProfile.getBloodType();
            Integer minAge;
            Integer maxAge;
            if (donorProfile.getAge() < 12) {
                minAge = 0;
                maxAge = 12;
            } else {
                minAge = donorProfile.getAge() - 15;
                if (minAge < 12) {
                    minAge = 12;
                }
                maxAge = donorProfile.getAge() + 15;
            }

            receivingProfiles = DAOFactory.getProfileDao()
                    .getOrganReceivers(organAvailable.getName(), reqBloodType, minAge, maxAge);
            potentialOrganMatches.addAll(receivingProfiles);
            SortedList<Profile> sortedByCountry = new SortedList<>(potentialOrganMatches,
                    (Profile profile1, Profile profile2) -> {
                        if (profile1.getCountry().equals(donorProfile.getCountryOfDeath())
                                && !profile2
                                .getCountry().equals(donorProfile.getCountryOfDeath())) {
                            return -1;
                        } else if (!profile1.getCountry().equals(donorProfile.getCountryOfDeath())
                                && profile2.getCountry().equals(donorProfile.getCountryOfDeath())) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });
            SortedList<Profile> sortedByRegion = new SortedList<>(sortedByCountry,
                    (Profile profile1, Profile profile2) -> {
                        if (profile1.getRegion().equals(donorProfile.getRegionOfDeath())
                                && !profile2
                                .getRegion().equals(donorProfile.getRegionOfDeath())) {
                            return -1;
                        } else if (!profile1.getRegion().equals(donorProfile.getRegionOfDeath())
                                && profile2.getRegion().equals(donorProfile.getRegionOfDeath())) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });
            SortedList<Profile> sortedByCity = new SortedList<>(sortedByRegion,
                    (Profile profile1, Profile profile2) -> {
                        if (profile1.getCity().equals(donorProfile.getCityOfDeath()) && !profile2
                                .getCity().equals(donorProfile.getCityOfDeath())) {
                            return -1;
                        } else if (!profile1.getCity().equals(donorProfile.getCityOfDeath())
                                && profile2
                                .getCity().equals(donorProfile.getCityOfDeath())) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });
            for (Profile p : sortedByCity) {
                potentialOrganMatches.remove(p);
                potentialOrganMatches.add(p);
            }
            return potentialOrganMatches;
        }

        /**
         * Generates a collection of a profile and organ for each organ that a receiver donates after
         * death
         *
         * @return Collection of Profile and Organ that match
         */
        public List<Map.Entry<Profile, OrganEnum>> getAllOrgansAvailable () throws SQLException {
            donaters = new ArrayList<>();
            ProfileDAO database = DAOFactory.getProfileDao();

            allDonaters = database.getDead();

            for (Profile profile : allDonaters) {
                for (OrganEnum organ : profile.getOrgansDonating()) {
                    Map.Entry<Profile, OrganEnum> pair = new AbstractMap.SimpleEntry<>(profile,
                            organ);
                    if (!donaters.contains(pair)) {
                        donaters.add(pair);
                    }
                }
            }
            return donaters;
        }

        /**
         * Returns a list of available organs as per the filters provided
         *
         * @param organs    list of organs to filter by
         * @param countries list of countries to filter by
         * @param regions   list of regions to filter by
         * @return
         */
        public ObservableList<Map.Entry<Profile, OrganEnum>> performSearch (ObservableList organs,
                ObservableList countries, ObservableList regions){

            ObservableList<Map.Entry<Profile, OrganEnum>> searchResults = null;
            return searchResults;
        }
    }
}
