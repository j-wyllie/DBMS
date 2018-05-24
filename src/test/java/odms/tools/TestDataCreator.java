package odms.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import odms.data.IrdNumberConflictException;
import odms.data.ProfileDatabase;
import odms.profile.Condition;
import odms.enums.OrganEnum;
import odms.profile.OrganConflictException;
import odms.profile.Profile;

public class TestDataCreator {
    private ProfileDatabase database;
    private List<OrganEnum> organs = Arrays.asList(OrganEnum.values());

    private List<String> names = Arrays.asList(
        "Ash Ketchup",
        "Basashi Tabetai",
        "Boaty McBoatFace",
        "Boobafina Otter",
        "Chewy Pancake",
        "Cloud Strife",
        "Darbage Gumpster",
        "Fried McChicken",
        "Galil AR",
        "Gordon Freeman",
        "Hadron Collider",
        "Hato Pigeon",
        "Hojn Bjorn",
        "Jake Dogg",
        "John Wick",
        "Marcus Fenix",
        "Nathan Drake",
        "Peppermint Butler",
        "Ronald McDonald",
        "Sammie Salmon",
        "Samus Aran",
        "Slim Flapjack",
        "Snoop Dogg",
        "Vorian Atreides",
        "Xavier Harkonnen"
    );

    private List<String> regions = Arrays.asList(
            "Bay of Plenty",
            "Gisborne",
            "Hawke's Bay",
            "Taranaki",
            "Wellington",
            "Tasman",
            "Nelson",
            "Marlborough",
            "West Coast",
            "Canterbury",
            "Otago",
            "Southland",
            "Northland",
            "Auckland",
            "Wanganui",
            "Waikato"
            );

    public TestDataCreator() {
        database = new ProfileDatabase();

        try {

            generateProfiles();

        } catch (IrdNumberConflictException e) {

            e.printStackTrace();

        }
    }

    /**
     * Generate profiles with random organ data and add them to the database.
     *
     * @throws IrdNumberConflictException if there is a duplicate IRD number in the database
     */
    private void generateProfiles() throws IrdNumberConflictException {
        List<Integer> irdNumbers = new ArrayList<>();

        while (irdNumbers.size() < names.size()) {
            Integer irdNumber = randInRange(100000000, 999999999);

            if (!irdNumbers.contains(irdNumber)) {
                irdNumbers.add(irdNumber);
            }
        }

        for (String name : names) {
            String[] profileName = name.split(" ");
            Profile profile = new Profile(
                    profileName[0],
                    profileName[1],
                    randomDOB(),
                    irdNumbers.remove(0)
            );
            addOrganDonations(profile);
            addOrganDonors(profile);
            addOrgansRequired(profile);

            if (Math.random() < 0.5) {
                profile.setGender("male");
            } else {
                profile.setGender("female");
            }

            profile.setRegion(regions.get(randInRange(0, 15)));

            // Give this Galil Ar some diseases
            if (profile.getFullName().equals("Galil AR")) {
                addConditions(profile);
            }

            database.addProfile(profile);
        }

    }

    public ProfileDatabase getDatabase() {
        return database;
    }

    /**
     * Select a random number of organs to add as previously donated organs to
     * the profile.
     *
     * @param profile the profile in which to add the organs
     */
    private void addOrganDonations(Profile profile) {
        Integer numberDonations = randInRange(0, OrganEnum.values().length);

        if (numberDonations > 0) {
            profile.setDonor(true);
            for (Integer i = 0; i < numberDonations; i++) {
                profile.addOrganDonated(organs.get(i));
            }
        }
    }

    /**
     * Just adds 2 chronic and 2 non-chronic conditions to a profile
     *
     * @param profile the profile that the diseases will be given
     */
    private void addConditions(Profile profile) {
        profile.addCondition(new Condition("Heart Disease", "01-04-2018", true));
        profile.addCondition(new Condition("Heart Palpitations", "01-03-2018", "01-04-2018", false));
    }

    /**
     * Select a random number of organs to donate to add to the profile.
     *
     * @param profile the profile in which to add the organs
     */
    private void addOrganDonors(Profile profile) {
        Integer numberDonating = randInRange(0, OrganEnum.values().length);

        if (numberDonating > 0) {
            profile.setDonor(true);
            for (Integer i = 0; i < numberDonating; i++) {
                try {
                    profile.addOrganDonating(organs.get(i));
                } catch (OrganConflictException e) {
                    // As is test data, no action required.
                }
            }
        }
    }

    /**
     * Select a random number of organs that a profile requires to receive.
     *
     * @param profile the profile in which to add the required organs
     */
    private void addOrgansRequired(Profile profile) {
        Integer numberReceiving = randInRange(0, OrganEnum.values().length);

        if (numberReceiving > 0) {
            profile.setReceiver(true);
            for (Integer i = 0; i < numberReceiving; i++) {
                profile.addOrganRequired(organs.get(i));
            }
        }
    }

    /**
     * Generate a random Date of Birth string.
     *
     * @return the Date of Birth string
     */
    private String randomDOB() {
        GregorianCalendar gc = new GregorianCalendar();
        Integer year = randInRange(1900, 2018);
        gc.set(GregorianCalendar.YEAR, year);

        Integer yearDay = randInRange(1, gc.getActualMaximum(GregorianCalendar.DAY_OF_YEAR));
        gc.set(GregorianCalendar.DAY_OF_YEAR, yearDay);

        return gc.get(GregorianCalendar.DAY_OF_MONTH) + "-" +
            (gc.get(GregorianCalendar.MONTH) + 1) + "-" +
            gc.get(GregorianCalendar.YEAR);
    }

    /**
     * Generate a random Integer in the range of a min and max.
     *
     * @param min the minimum bound
     * @param max the maximum bound
     * @return the randomly generated value
     */
    private Integer randInRange(Integer min, Integer max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

}
