package odms.controller.user;

import static java.lang.Math.abs;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.ExpiredOrgan;
import odms.commons.model.profile.Profile;
import odms.controller.database.DAOFactory;
import odms.controller.database.organ.OrganDAO;
import odms.controller.database.profile.ProfileDAO;

/**
 * Controller for the available organs tab.
 */
public class AvailableOrgans {

    private static final long ONE_SECOND = 1000;
    private static final long ONE_MINUTE = ONE_SECOND * 60;
    private static final long ONE_HOUR = ONE_MINUTE * 60;
    private static final long ONE_DAY = ONE_HOUR * 24;
    private static final long ONE_YEAR = ONE_DAY * 365;
    private static final int COUNTDOWN_PERIOD = 1000;
    private static final int EXPIRED_ORGANS_DELAY = 15000;

    private List<Entry<Profile, OrganEnum>> donaters = new ArrayList<>();
    private List<ExpiredOrgan> expiredList = new ArrayList<>();
    private Timer timer;
    private odms.view.user.AvailableOrgans view;


    public void setView(odms.view.user.AvailableOrgans v) {
        view = v;
    }

    /**
     * Retrieves the raw unformatted wait time.
     *
     * @param selectedOrgan Organ with the wait time.
     * @param organsRequired Set of organs required.
     * @param p Current profile.
     * @return A list of unformatted wait times.
     */
    public static Long getWaitTimeRaw(OrganEnum selectedOrgan, Set<OrganEnum> organsRequired,
            Profile p) {
        LocalDateTime dateOrganRegistered = LocalDateTime.now();

        for (OrganEnum organ : organsRequired) {
            if (organ.getNamePlain().equalsIgnoreCase(selectedOrgan.getNamePlain())) {
                if (organ.getDate(p) != null) {
                    dateOrganRegistered = organ.getDate(p);
                } else {
                    return (long) -1;
                }
            }
        }
        return abs(Duration.between(LocalDateTime.now(), dateOrganRegistered).toMillis());
    }

    /**
     * Gets the wait time for an expired organ.
     *
     * @param selectedOrgan Selected organ.
     * @param organsRequired Set of organs required.
     * @param p Current profile.
     * @return Organs wait time.
     */
    public static String getWaitTime(OrganEnum selectedOrgan, Set<OrganEnum> organsRequired,
            Profile p) {

        String durationFormatted = "";

        Long waitTime = getWaitTimeRaw(selectedOrgan, organsRequired, p);
        if (waitTime == -1) {
            // Means a date was not entered when a organ was registered
            return "Insufficient data";
        }

        if (waitTime == 0) {
            return "Registered today";
        }

        durationFormatted = formatDuration(durationFormatted, waitTime);
        return durationFormatted;
    }

    /**
     * Formats the duration string.
     *
     * @param duration Duration string to be formatted.
     * @param waitTime Current wait time.
     * @return The formatted duration string.
     */
    private static String formatDuration(String duration, Long waitTime) {
        long temp;
        if (waitTime >= ONE_SECOND) {
            temp = waitTime / ONE_DAY;
            if (temp > 0) {
                if (temp > 1) {
                    duration += temp + " days ";
                } else {
                    duration += temp + " day ";
                }
                waitTime -= temp * ONE_DAY;
            }
            duration = checkWaitTimeMinutesHours(duration, waitTime);
        }
        return duration;
    }

    /**
     * Checks the wait time minutes and hours.
     *
     * @param duration Current duration.
     * @param waitTime Current wait time.
     * @return the duration.
     */
    private static String checkWaitTimeMinutesHours(String duration, Long waitTime) {
        long temp;
        temp = waitTime / ONE_HOUR;
        if (temp > 0) {
            if (temp > 1) {
                duration += temp + " hours ";
            } else {
                duration += temp + " hour ";
            }
        } else {
            temp = waitTime / ONE_MINUTE;
            if (temp >= 0) {
                duration += temp + "m ";
            }
        }
        return duration;
    }

    /**
     * Sets the expired organs in the profile.
     *
     * @param organ Organ to add.
     * @param profile Current profile.
     */
    private void setOrganExpired(OrganEnum organ, Profile profile) {
        profile.getOrgansDonating().remove(organ);
        OrganDAO dao = DAOFactory.getOrganDao();
        dao.removeDonating(profile, organ);
        profile.getOrgansExpired().add(organ);
    }


    /**
     * Checks the expired organs in the profile.
     *
     * @param organ Organ to check.
     * @param profile Current profile.
     */
    public void checkOrganExpired(OrganEnum organ, Profile profile) {
        if (LocalDateTime.now()
                .isAfter(getExpiryTime(organ, profile))) {
            setOrganExpired(organ, profile);
        }
    }

    /**
     * Removes for organs in the expired organ list.
     *
     * @param organ Organ to remove.
     * @param profile Current profile.
     * @param m Expired organs list.
     */
    private void checkOrganExpiredListRemoval(OrganEnum organ, Profile profile,
            Map.Entry<Profile, OrganEnum> m) {
        if (LocalDateTime.now()
                .isAfter(getExpiryTime(organ, profile))) {
            view.removeItem(m);
            setOrganExpired(organ, profile);
        }
        try {
            expiredList = DAOFactory.getOrganDao().getExpired(profile);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (ExpiredOrgan currentOrgan : expiredList) {
            if (currentOrgan.getOrgan().equalsIgnoreCase(organ.getNamePlain())) {
                view.removeItem(m);
            }
        }
    }

    /**
     * Gets the expiry time for an organ.
     *
     * @param organ Expired organ.
     * @param profile Current profile.
     * @return LocalDateTime of the expiry time.
     */
    private static LocalDateTime getExpiryTime(OrganEnum organ, Profile profile) {
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
     * Gives remaining time in milliseconds that a organ has until it expires.
     *
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
     * Gives the expiry time of a 'fresh organ'.
     *
     * @param organ the organ given.
     * @return the expiry time for the 'fresh' given organ.
     */
    public static Double getExpiryLength(OrganEnum organ) {

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
     * @param organ the organ being checked against.
     * @param profile the selected profile.
     * @return formatted string.
     */
    public static String getTimeToExpiryStd(OrganEnum organ, Profile profile) {
        return getTimeToExpiryFormatted(organ, profile, true);
    }

    /**
     * Get remaining time in standard '2h 1s' format.
     *
     * @param organ the organ being checked against.
     * @param profile the selected profile.
     * @return formatted string.
     */
    public static String getTimeToExpiryHoursSeconds(OrganEnum organ, Profile profile) {
        return getTimeToExpiryFormatted(organ, profile, false);
    }

    /**
     * Calculates how long a Organ has til expiry, returns in formatted string
     *
     * @param organ Given organ
     * @param profile Given profile the organ belongs to
     * @param isStd true if it's standard time.
     * @return How long the organ has til expiry in days, minutes, hours and seconds
     */
    private static String getTimeToExpiryFormatted(OrganEnum organ, Profile profile, Boolean
            isStd) {
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
    private static String msToHoursAndSeconds(Long timeToExpiry) {
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
     * Converts the expiry time to hours and seconds.
     *
     * @param timeToExpiry Time to convert.
     * @return Converted time.
     */
    public static int hoursAndSecondsToMs(String timeToExpiry) {
        int timeRaw = 0;
        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < timeToExpiry.length(); i++) {
            char c = timeToExpiry.charAt(i);

            try {

                if (Character.isDigit(c)) {
                    temp.append(c);
                }

                if (timeToExpiry.charAt(i + 1) == 'h') {
                    timeRaw += Integer.parseInt(temp.toString()) * ONE_HOUR;
                    temp = new StringBuilder();
                } else if (timeToExpiry.charAt(i + 1) == 's') {
                    timeRaw += Integer.parseInt(temp.toString()) * ONE_SECOND;
                    temp = new StringBuilder();
                }
            } catch (IndexOutOfBoundsException e) {
                return Math.abs(timeRaw);
            }
        }
        return Math.abs(timeRaw);
    }

    /**
     * Support function to convert ms to a string representing years, hours, minutes and seconds.
     *
     * @param timeToExpiry in ms
     * @return formatted string
     */
    private static String msToStandard(Long timeToExpiry) {
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
     * returns list of potential organ matches for a given organ and the donor the organ came from.
     *
     * @param organAvailable the available organ
     * @param donorProfile the donor the organ came from
     * @param checkedBloodTypes the checked blood types.
     * @param checkedRegions the checked regions.
     * @param ageLower the lower age range.
     * @param ageUpper the upper age range.
     * @param ageRangeChecked if age range should be checked.
     * @return A list of potential organ matches
     */
    public static ObservableList<Profile> getSuitableRecipientsSorted(OrganEnum organAvailable,
            Profile donorProfile,
            ObservableList checkedBloodTypes, ObservableList checkedRegions,
            String ageLower, String ageUpper, boolean ageRangeChecked) {
        // sort by longest wait time first,
        // then weight by closest location to where the donor profiles region of death
        ObservableList<Profile> potentialOrganMatches = FXCollections.observableArrayList();
        ObservableList<Profile> potentialOrganMatchesUnfiltered = FXCollections
                .observableArrayList();

        List<Profile> receivingProfiles;

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
        potentialOrganMatchesUnfiltered.addAll(receivingProfiles);
        SortedList<Profile> sortedByCountry = sortCountry(donorProfile,
                potentialOrganMatchesUnfiltered);

        SortedList<Profile> sortedByRegion = sortRegion(donorProfile, sortedByCountry);
        SortedList<Profile> sortedByCity = filterCity(donorProfile, sortedByRegion);
        for (Profile p : sortedByCity) {
            potentialOrganMatchesUnfiltered.remove(p);
            potentialOrganMatchesUnfiltered.add(p);
        }

        // No point filtering
        if ("".equals(ageLower) && checkedRegions.size() == 0 && checkedBloodTypes.size() == 0) {
            return potentialOrganMatchesUnfiltered;
        }

        filterBloodType(checkedBloodTypes, potentialOrganMatches, potentialOrganMatchesUnfiltered);
        filterRegions(checkedRegions, potentialOrganMatches, potentialOrganMatchesUnfiltered);

        filterAgeRange(ageLower, ageUpper, ageRangeChecked, potentialOrganMatches,
                potentialOrganMatchesUnfiltered);

        return potentialOrganMatches;
    }

    /**
     * Filters the region.
     *
     * @param checkedRegions Regions being checked.
     * @param potentialOrganMatches The potential organ matches.
     * @param potentialOrganMatchesUnfiltered The unfiltered potential organ matches.
     */
    private static void filterRegions(ObservableList checkedRegions,
            ObservableList<Profile> potentialOrganMatches,
            ObservableList<Profile> potentialOrganMatchesUnfiltered) {
        // Regions filtering
        if (checkedRegions.size() > 0) {
            potentialOrganMatches.clear();
            for (Profile p : potentialOrganMatchesUnfiltered) {
                if (checkedRegions.contains(p.getRegion())) {
                    potentialOrganMatches.add(p);
                }
            }
            potentialOrganMatchesUnfiltered.clear();
            potentialOrganMatchesUnfiltered.addAll(potentialOrganMatches);
        }
    }

    /**
     * Filters the checked blood types.
     *
     * @param checkedBloodTypes the checked blood types.
     * @param potentialOrganMatches The potential organ matches.
     * @param potentialOrganMatchesUnfiltered The unfiltered potential organ matches.
     */
    private static void filterBloodType(ObservableList checkedBloodTypes,
            ObservableList<Profile> potentialOrganMatches,
            ObservableList<Profile> potentialOrganMatchesUnfiltered) {
        // Blood type filtering
        if (checkedBloodTypes.size() > 0) {
            potentialOrganMatches.clear();
            for (Profile p : potentialOrganMatchesUnfiltered) {
                if (checkedBloodTypes.contains(p.getBloodType())) {
                    potentialOrganMatches.add(p);
                }
            }
            potentialOrganMatchesUnfiltered.clear();
            potentialOrganMatchesUnfiltered.addAll(potentialOrganMatches);
        }
    }

    /**
     * Filters the age range.
     *
     * @param ageLower lower age range.
     * @param ageUpper upper age range.
     * @param ageRangeChecked need to filter by age range.
     * @param potentialOrganMatches The potential organ matches.
     * @param potentialOrganMatchesUnfiltered The unfiltered potential organ matches.
     */
    private static void filterAgeRange(String ageLower, String ageUpper, boolean ageRangeChecked,
            ObservableList<Profile> potentialOrganMatches,
            ObservableList<Profile> potentialOrganMatchesUnfiltered) {
        // Age filtering
        if (!"".equals(ageLower)) {
            if (ageRangeChecked) {
                if (!"".equals(ageUpper)) {
                    filterUpperAge(ageLower, ageUpper, potentialOrganMatches,
                            potentialOrganMatchesUnfiltered);
                }
            } else {
                filterLowerAge(ageLower, potentialOrganMatches, potentialOrganMatchesUnfiltered);
            }
        }
    }

    /**
     * Filters the lower age limit.
     *
     * @param ageLower lower age limit.
     * @param potentialOrganMatches potential matches.
     * @param potentialOrganMatchesUnfiltered unfiltered organ matches list.
     */
    private static void filterLowerAge(String ageLower,
            ObservableList<Profile> potentialOrganMatches,
            ObservableList<Profile> potentialOrganMatchesUnfiltered) {
        potentialOrganMatches.clear();
        for (Profile p : potentialOrganMatchesUnfiltered) {
            if (p.getAge() == Integer.parseInt(ageLower)) {
                potentialOrganMatches.add(p);
            }
        }
        potentialOrganMatchesUnfiltered.clear();
        potentialOrganMatchesUnfiltered.addAll(potentialOrganMatches);
    }

    /**
     * Filters the upper age limit.
     *
     * @param ageLower lower age limit.
     * @param ageUpper upper age limit.
     * @param potentialOrganMatches potential matches.
     * @param potentialOrganMatchesUnfiltered unfiltered organ matches list.
     */
    private static void filterUpperAge(String ageLower, String ageUpper,
            ObservableList<Profile> potentialOrganMatches,
            ObservableList<Profile> potentialOrganMatchesUnfiltered) {
        potentialOrganMatches.clear();
        for (Profile p : potentialOrganMatchesUnfiltered) {
            if (p.getAge() > Integer.parseInt(ageLower) && p.getAge() < Integer
                    .parseInt(ageUpper)) {
                potentialOrganMatches.add(p);
            }
        }
        potentialOrganMatchesUnfiltered.clear();
        potentialOrganMatchesUnfiltered.addAll(potentialOrganMatches);
    }

    /**
     * Filters by the city.
     *
     * @param donorProfile Current profile.
     * @param sortedByRegion region list.
     * @return Regions.
     */
    private static SortedList<Profile> filterCity(Profile donorProfile,
            SortedList<Profile> sortedByRegion) {
        return new SortedList<>(sortedByRegion,
                (Profile profile1, Profile profile2) -> {
                    if (profile1.getCity().equals(donorProfile.getCityOfDeath()) && !profile2
                            .getCity().equals(donorProfile.getCityOfDeath())) {
                        return -1;
                    } else if (!profile1.getCity().equals(donorProfile.getCityOfDeath()) &&
                            profile2.getCity().equals(donorProfile.getCityOfDeath())) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
    }

    /**
     * Sorts list of profiles based on region.
     *
     * @param donorProfile current donor profile.
     * @param sortedByCountry sorted list of profiles by country.
     * @return a sorted list of profiles.
     */
    private static SortedList<Profile> sortRegion(Profile donorProfile,
            SortedList<Profile> sortedByCountry) {
        return new SortedList<>(sortedByCountry,
                (Profile profile1, Profile profile2) -> {
                    if (profile1.getRegion().equals(donorProfile.getRegionOfDeath()) &&
                            !profile2.getRegion().equals(donorProfile.getRegionOfDeath())) {
                        return -1;
                    } else if (!profile1.getRegion().equals(donorProfile.getRegionOfDeath()) &&
                            profile2.getRegion().equals(donorProfile.getRegionOfDeath())) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
    }

    /**
     * Sorts by the country.
     *
     * @param donorProfile current profile.
     * @param potentialOrganMatchesUnfiltered unfiltered list of potential organ matches.
     * @return sorted list of profiles based on country.
     */
    private static SortedList<Profile> sortCountry(Profile donorProfile,
            ObservableList<Profile> potentialOrganMatchesUnfiltered) {
        return new SortedList<>(potentialOrganMatchesUnfiltered,
                (Profile profile1, Profile profile2) -> {
                    if (profile1.getCountry().equals(donorProfile.getCountryOfDeath()) &&
                            !profile2.getCountry().equals(donorProfile.getCountryOfDeath())) {
                        return -1;
                    } else if (!profile1.getCountry().equals(donorProfile.getCountryOfDeath()) &&
                            profile2.getCountry().equals(donorProfile.getCountryOfDeath())) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
    }

    /**
     * Generates a collection of a profile and organ for each organ that a receiver donates after
     * death.
     *
     * @return Collection of Profile and Organ that match
     * @throws SQLException error in sql.
     */
    public List<Map.Entry<Profile, OrganEnum>> getAllOrgansAvailable() throws SQLException {
        donaters = new ArrayList<>();
        ProfileDAO database = DAOFactory.getProfileDao();

        List<Profile> allDonaters = database.getDead();
        for (Profile profile : allDonaters) {

            for (OrganEnum organ : profile.getOrgansDonatingNotExpired()) {
                final List<ExpiredOrgan> expired = DAOFactory.getOrganDao().getExpired(profile);

                if (expired.isEmpty()) {
                    Map.Entry<Profile, OrganEnum> pair = new AbstractMap.SimpleEntry<>(profile,
                            organ);
                    if (!donaters.contains(pair)) {
                        donaters.add(pair);
                    }
                } else {
                    addExpiredOrganToPair(profile, organ, expired);
                }
            }
        }
        return donaters;
    }

    /**
     * Adds the expired organs to the pair.
     *
     * @param profile profile with expired organs.
     * @param organ Organ to check.
     * @param expired list of expired organs.
     */
    private void addExpiredOrganToPair(Profile profile, OrganEnum organ,
            List<ExpiredOrgan> expired) {
        for (ExpiredOrgan expiredOrgan : expired) {
            if (!expiredOrgan.getOrgan().equals(organ.getNamePlain())) {
                Entry<Profile, OrganEnum> pair = new AbstractMap.SimpleEntry<>(profile,
                        organ);
                if (!donaters.contains(pair)) {
                    donaters.add(pair);
                }
                break;
            }
        }
    }

    /**
     * Starts the timers for fetching expired organs and counting down the expiry date.
     */
    public void startTimers() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                view.availableOrgansTable.refresh();
                view.potentialOrganMatchTable.refresh();

            }
        }, 0, COUNTDOWN_PERIOD);

        timer.schedule(new TimerTask() {
            public void run() {
                List<Entry<Profile, OrganEnum>> toRemove = new ArrayList<>(
                        view.listOfAvailableOrgans);

                for (Map.Entry<Profile, OrganEnum> m : toRemove) {
                    checkOrganExpiredListRemoval(m.getValue(), m.getKey(), m);
                }
            }
        }, 0, EXPIRED_ORGANS_DELAY);
    }

    /**
     * Cancels the current timer. Called when the tab has lost focus.
     */
    public void pauseTimers() {
        timer.cancel();
    }
}
