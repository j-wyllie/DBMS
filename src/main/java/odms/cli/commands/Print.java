package odms.cli.commands;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import odms.cli.CommandUtils;
import odms.data.ProfileDatabase;
import odms.data.UserDatabase;
import odms.enums.OrganEnum;
import odms.profile.Profile;
import odms.user.User;
import odms.user.UserType;

public class Print extends CommandUtils {

    /**
     * Print all profiles in the Database
     *
     * @param currentDatabase Database reference
     */
    public static void printAllProfiles(ProfileDatabase currentDatabase) {
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
     * Print all profiles in the Database
     *
     * @param currentDatabase Database reference
     */
    public static void printAllClinicians(UserDatabase currentDatabase) {
        ArrayList<User> allUsers = currentDatabase.getUsersAsArrayList();
        if (allUsers.size() > 0) {
            for (User user : allUsers) {
                if (user.getUserType() == UserType.CLINICIAN) {
                    printUserAttributesAttributes(user);
                    System.out.println();
                }
            }
        }
        else {
            System.out.println("There are no clinicians to show.");
        }
    }

    /**
     * Print all profiles in the Database
     *
     * @param currentDatabase Database reference
     */
    public static void printAllUsers(UserDatabase currentDatabase) {
        ArrayList<User> allUsers = currentDatabase.getUsersAsArrayList();
        if (allUsers.size() > 0) {
            for (User user : allUsers) {
                printUserAttributesAttributes(user);
                System.out.println();
            }
        }
        else {
            System.out.println("There are no users to show.");
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
            System.out.println("NHI: " + profile.getNhi());
            System.out.println("Given Names: " + profile.getGivenNames());
            System.out.println("Last Names: " + profile.getLastNames());
            System.out.println("Date/Time Created: " + profile.getTimeOfCreation());
            System.out.println();
        }
    }

    /**
     * Display and print profile details in a list.
     *
     * @param userlist List of profiles
     */
    public static void printUserList(ArrayList<User> userlist) {
        for (User user : userlist) {
            System.out.println("Staff ID: " + user.getStaffID());
            System.out.println("Name: " + user.getName());
            System.out.println("User type: " + user.getUserType());
            System.out.println("Date/Time Created: " + user.getTimeOfCreation());
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
            System.out.println("NHI: " + profile.getNhi());
            System.out.println("Given Names: " + profile.getGivenNames());
            System.out.println("Last Names: " + profile.getLastNames());
            System.out.println("Organs Donated:" + profile.getOrgansDonated());
            System.out.println("Organs Donating: " + OrganEnum.organSetToString(profile.getOrgansDonating()));
            System.out.println("Organs Required: " + OrganEnum.organSetToString(profile.getOrgansRequired()));
            System.out.println();
        }
    }

    /**
     * Display and print printAllProfiles search results from Profile array. If array empty, no search results
     * have been found.
     *
     * @param profileList Results from searching
     */
    public static void printProfileSearchResults(ArrayList<Profile> profileList) {
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
     * Display and print printAllProfiles search results from Profile array. If array empty, no search results
     * have been found.
     *
     * @param userlist Results from searching
     */
    public static void printUserSearchResults(ArrayList<User> userlist) {
        if (userlist.size() > 0) {
            for (User user : userlist) {
                printUserAttributesAttributes(user);
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
        System.out.println("NHI: " + profile.getNhi());
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

        if (profile.getHeight() != 0.0) {
            System.out.println("Height: " + profile.getHeight() + "cm");
        }

        if (profile.getWeight() != 0.0) {
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

        if (profile.getCountry() != null) {
            System.out.println("Country: " + profile.getCountry());
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

    /**
     * Display and print the attributes of a profile
     *
     * @param user to be displayed
     */
    private static void printUserAttributesAttributes(User user) {
        System.out.println("User type: " + user.getUserType());
        System.out.println("ODMS Staff ID: " + user.getStaffID());

        if (user.getName() != null) {
            System.out.println("Name: " + user.getName());
        }

        if (user.getUsername() != null) {
            System.out.println("Username: " + user.getUsername());
        }

        if (user.getRegion() != null) {
            System.out.println("Region: " + user.getRegion());
        }

        if (user.getWorkAddress() != null) {
            System.out.println("Work address: " + user.getWorkAddress());
        }

        System.out.println("Last updated at: " + user.getLastUpdated().format(
                DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"))
        );
    }

}
