package odms.cli.commands;

import java.util.ArrayList;
import odms.cli.CommandUtils;
import odms.data.ProfileDatabase;
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
                profile.viewAttributes();
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
                profile.viewAttributes();
                profile.viewOrgans();
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
            profile.viewDonations();
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
                profile.viewAttributes();
                System.out.println();
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

}
