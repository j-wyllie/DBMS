package odms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import odms.data.IrdNumberConflictException;
import odms.data.ProfileDatabase;
import odms.profile.Organ;
import odms.profile.Profile;

public class TestDataCreator {
    private ProfileDatabase database;
    private Integer profilesDonors;
    private Integer profilesDonated;
    private Integer profilesRequired;

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

    public TestDataCreator() {
        database = new ProfileDatabase();

        try {

            generateClinicianProfiles();
            generateProfiles();


            addOrganDonations();
            addOrganDonors();
            addOrgansRequired();

            // TODO remove these debug lines
            System.out.println(database.getProfilePopulation());

        } catch (IrdNumberConflictException e) {

            e.printStackTrace();

        }
    }

    private void generateClinicianProfiles() {

    }

    private void generateProfiles() throws IrdNumberConflictException {
        List<Integer> irdNumbers = new ArrayList<>();

        while (irdNumbers.size() < names.size()) {
            Integer irdNumber = randBetween(100000000, 999999999);

            if (!irdNumbers.contains(irdNumber)) {
                irdNumbers.add(irdNumber);
            }
        }

        for (String name : names) {
            String[] profileName = name.split(" ");
            database.addProfile(new Profile(
                profileName[0],
                profileName[1],
                randomDOB(),
                irdNumbers.remove(0)
            ));
        }
    }

    private void addOrganDonations() {

    }

    private void addOrganDonors() {

    }

    private void addOrgansRequired() {

    }

    private String randomDOB() {
        GregorianCalendar gc = new GregorianCalendar();
        Integer year = randBetween(1900, 2018);
        Integer yearDay = randBetween(1, gc.getActualMaximum(GregorianCalendar.DAY_OF_YEAR));

        gc.set(GregorianCalendar.YEAR, year);
        gc.set(GregorianCalendar.DAY_OF_YEAR, yearDay);

        return gc.get(GregorianCalendar.DAY_OF_MONTH) + "-" +
            (gc.get(GregorianCalendar.MONTH) + 1) + "-" +
            gc.get(GregorianCalendar.YEAR);
    }

    /**
     * Generate a random Integer between a min and a max
     * @param min the minimum bound
     * @param max the maximum bound
     * @return the randomly generated value
     */
    private Integer randBetween(Integer min, Integer max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static void main(String[] args) {
        TestDataCreator test = new TestDataCreator();

    }

}
