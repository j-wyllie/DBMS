package odms.cli.commands;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import odms.cli.CommandUtils;
import odms.data.ProfileDatabase;
import odms.enums.OrganEnum;
import odms.profile.Profile;

public class Print extends CommandUtils {

    /**
     * Print all profiles in the Database
     *
     * @param currentDatabase Database reference
     */
    public static void printAll(ProfileDatabase currentDatabase) {
        ArrayList<Profile> allProfiles = currentDatabase.getProfiles(false);
        if (allProfiles.size() > 0) {
            for (Profile profile : allProfiles) {
                printProfileAttributes(profile);
                System.out.println();
            }
        }
        else {
            System.out.println("There are no profiles to show.");
        }
    }

    /**
     * Print all profiles with donations in the Database
     *
     * @param currentDatabase Database reference
     */
    public static void printDonors(ProfileDatabase currentDatabase) {
        ArrayList<Profile> allProfiles = currentDatabase.getProfiles(true);
        if (allProfiles.size() > 0) {
            for (Profile profile : allProfiles) {
                printProfileAttributes(profile);
                System.out.println("Organs Donating: " + OrganEnum.organSetToString(profile.getOrgansDonating()));
                System.out.println();
            }
        }
        else {
            System.out.println("There are no profile profiles to show.");
        }
    }

    /**
     * Display and print profile details in a list.
     *
     * @param profileList List of profiles
     */
    public static void printProfileList(ArrayList<Profile> profileList) {
        for (Profile profile : profileList) {
            System.out.println("IRD: " + profile.getIrdNumber());
            System.out.println("Given Names: " + profile.getGivenNames());
            System.out.println("Last Names: " + profile.getLastNames());
            System.out.println("Date/Time Created: " + profile.getTimeOfCreation());
            System.out.println();
        }
    }

    /**
     * Display and print profile donations.
     *
     * @param profileList list of profiles
     */
    public static void printProfileDonations(ArrayList<Profile> profileList) {
        for (Profile profile : profileList) {
            System.out.println("IRD: " + profile.getIrdNumber());
            System.out.println("Given Names: " + profile.getGivenNames());
            System.out.println("Last Names: " + profile.getLastNames());
            System.out.println("Organs Donated:" + profile.getOrgansDonated());
            System.out.println("Organs Donating: " + OrganEnum.organSetToString(profile.getOrgansDonating()));
            System.out.println("Organs Required: " + OrganEnum.organSetToString(profile.getOrgansRequired()));
            System.out.println();
        }
    }

    /**
     * Display and print printAll search results from Profile array. If array empty, no search results
     * have been found.
     *
     * @param profileList Results from searching
     */
    public static void printSearchResults(ArrayList<Profile> profileList) {
        if (profileList.size() > 0) {
            for (Profile profile : profileList) {
                printProfileAttributes(profile);
                System.out.println();
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Display and print the attributes of a profile
     *
     * @param profile to be displayed
     */
    private static void printProfileAttributes(Profile profile) {
        System.out.println("IRD: " + profile.getIrdNumber());
        System.out.println("ODMS ID: " + profile.getId());
        System.out.println("Given Names: " + profile.getGivenNames());
        System.out.println("Last Names: " + profile.getLastNames());
        System.out.println("Date Of Birth: " + profile.getDateOfBirth().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        );

        if (profile.getDateOfDeath() != null) {
            System.out.println("Date Of Death: " + profile.getDateOfDeath().format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            );
        }

        if (profile.getGender() != null) {
            System.out.println("Gender: " + profile.getGender());
        }

        if (profile.getHeight() != null && profile.getHeight() != 0.0) {
            System.out.println("Height: " + profile.getHeight() + "cm");
        }

        if (profile.getWeight() != null && profile.getWeight() != 0.0) {
            System.out.println("Weight: " + profile.getWeight());
        }

        if (profile.getBloodType() != null) {
            System.out.println("Blood Type: " + profile.getBloodType());
        }

        if (profile.getAddress() != null) {
            System.out.println("Address: " + profile.getAddress());
        }

        if (profile.getRegion() != null) {
            System.out.println("Region: " + profile.getRegion());
        }

        if (profile.getOrgansDonating().size() > 0) {
            System.out.println("Organs Donating: " + OrganEnum.organSetToString(profile.getOrgansDonating()));
        }

        if (profile.getOrgansDonating().size() > 0) {
            System.out.println("Organs Donated: " + OrganEnum.organSetToString(profile.getOrgansDonated()));
        }

        if (profile.getOrgansDonating().size() > 0) {
            System.out.println("Organs Required: " + OrganEnum.organSetToString(profile.getOrgansRequired()));
        }

        System.out.println("Last updated at: " + profile.getLastUpdated().format(
                DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"))
        );
    }

}
