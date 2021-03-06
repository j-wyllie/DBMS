package odms.cli.commands;

import java.time.format.DateTimeFormatter;
import java.util.List;
import odms.cli.CommandUtils;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;

public final class Print extends CommandUtils {

    private Print() {
        throw new UnsupportedOperationException();
    }

    /**
     * Print all profiles in the database.
     *
     * @param profiles the list of profiles to print.
     */
    public static void printAllProfiles(List<Profile> profiles) {
        if (!profiles.isEmpty()) {
            for (Profile profile : profiles) {
                printProfileAttributes(profile);
                System.out.println();
            }
        } else {
            System.out.println("There are no profiles to show.");
        }
    }

    /**
     * Print all clinicians in the database.
     *
     * @param users to print if the are of type clinican.
     */
    public static void printAllClinicians(List<User> users) {
        if (!users.isEmpty()) {
            for (User user : users) {
                if (user.getUserType() == UserType.CLINICIAN) {
                    printUserAttributesAttributes(user);
                    System.out.println();
                }
            }
        } else {
            System.out.println("There are no clinicians to show.");
        }
    }

    /**
     * Print all users in the database.
     *
     * @param users to print.
     */
    public static void printAllUsers(List<User> users) {
        if (!users.isEmpty()) {
            for (User user : users) {
                printUserAttributesAttributes(user);
                System.out.println();
            }
        } else {
            System.out.println("There are no users to show.");
        }
    }

    /**
     * Print all profiles with donations in the database.
     *
     * @param profiles to print if they are donors.
     */
    public static void printDonors(List<Profile> profiles) {
        if (!profiles.isEmpty()) {
            for (Profile profile : profiles) {
                if (profile.getDonor()) {
                    printProfileAttributes(profile);
                    System.out.println("Organs Donating: " + OrganEnum
                            .organSetToString(profile.getOrgansDonating()));
                    System.out.println();
                }
            }
        } else {
            System.out.println("There are no profile profiles to show.");
        }
    }

    /**
     * Display and print profile details in a list.
     *
     * @param profileList list of profiles.
     */
    public static void printProfileList(List<Profile> profileList) {
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
     * @param userlist List of profiles.
     */
    public static void printUserList(List<User> userlist) {
        for (User user : userlist) {
            System.out.println("Staff ID: " + user.getId());
            System.out.println("Name: " + user.getName());
            System.out.println("user type: " + user.getUserType());
            System.out.println("Date/Time Created: " + user.getTimeOfCreation());
            System.out.println();
        }
    }

    /**
     * Display and print profile donations.
     *
     * @param profileList list of profiles.
     */
    public static void printProfileDonations(List<Profile> profileList) {
        for (Profile profile : profileList) {
            System.out.println("NHI: " + profile.getNhi());
            System.out.println("Given Names: " + profile.getGivenNames());
            System.out.println("Last Names: " + profile.getLastNames());
            System.out.println("Organs Donated:" + profile.getOrgansDonated());
            System.out.println(
                    "Organs Donating: " + OrganEnum.organSetToString(profile.getOrgansDonating()));
            System.out.println(
                    "Organs Required: " + OrganEnum.organSetToString(profile.getOrgansRequired()));
            System.out.println();
        }
    }

    /**
     * Display and print printAllProfiles search results from profile array. If array empty, no
     * search results have been found.
     *
     * @param profileList results from searching.
     */
    public static void printProfileSearchResults(List<Profile> profileList) {
        if (!profileList.isEmpty()) {
            for (Profile profile : profileList) {
                printProfileAttributes(profile);
                System.out.println();
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Display and print printAllProfiles search results from profile array. If array empty, no
     * search results have been found.
     *
     * @param userlist results from searching.
     */
    public static void printUserSearchResults(List<User> userlist) {
        if (!userlist.isEmpty()) {
            for (User user : userlist) {
                printUserAttributesAttributes(user);
                System.out.println();
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Display and print the attributes of a profile.
     *
     * @param profile to be displayed.
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

        if (profile.getHeight() != null) {
            System.out.println("Height: " + profile.getHeight() + "cm");
        }

        if (profile.getWeight() != null) {
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
            System.out.println("country: " + profile.getCountry());
        }

        if (profile.getOrgansDonating().size() > 0) {
            System.out.println(
                    "Organs Donating: " + OrganEnum.organSetToString(profile.getOrgansDonating()));
        }

        if (profile.getOrgansDonating().size() > 0) {
            System.out.println(
                    "Organs Donated: " + OrganEnum.organSetToString(profile.getOrgansDonated()));
        }

        if (profile.getOrgansDonating().size() > 0) {
            System.out.println(
                    "Organs Required: " + OrganEnum.organSetToString(profile.getOrgansRequired()));
        }

        System.out.println("Last updated at: " + profile.getLastUpdated().format(
                DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"))
        );
    }

    /**
     * Display and print the attributes of a profile.
     *
     * @param user to be displayed.
     */
    private static void printUserAttributesAttributes(User user) {
        System.out.println("user type: " + user.getUserType());
        System.out.println("ODMS Staff ID: " + user.getId());

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
