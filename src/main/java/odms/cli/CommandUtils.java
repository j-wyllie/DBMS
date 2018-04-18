package odms.cli;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import odms.data.IrdNumberConflictException;
import odms.data.ProfileDatabase;
import odms.profile.Profile;

public class CommandUtils {

    public static ArrayList<String> currentSessionHistory = new ArrayList<>();

    public static int historyPosition = 0;
    private static ArrayList<Profile> deletedProfiles = new ArrayList<>();
    private static ArrayList<Profile> unaddedProfiles = new ArrayList<>();

    private static String searchErrorText = "Please enter only one search criteria "
        + "(given-names, last-names, ird).";
    private static String searchNotFoundText = "There are no donors that match this criteria.";

    private static final String cmdRegexCreate =
        "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]"
        + "([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)?)+)"
        + "[\"]))*";

    private static final String cmdRegexProfileView =
        "([a-z]+)((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]"
        + "(([a-zA-Z0-9][-]?(\\s)?)+)[\"]))+(\\s[>]\\s"
        + "([a-z]+)([-]([a-z]+))?)";

    private static final String cmdRegexDonorUpdate =
        "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]"
        + "([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)"
        + "?)+)[\"]))*(\\s[>])((\\s([a-z]+)([-]([a-z]"
        + "+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)?)+)"
        + "[\"]))*";

    private static final String cmdRegexOrganUpdate =
        "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]"
        + "([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)"
        + "?)+)[\"]))*(\\s[>](\\s([a-z]+)([-]([a-z]+)"
        + ")?)([=][\"](([a-zA-Z]([-])?([,](\\s)?)*)+)"
        + "[\"]))*";

    /**
     * Performs checks over the input to match a valid command
     *
     * If nothing is matched than an invalid command has been used.
     *
     * @param cmd the command being validated
     * @return the enum Commands appropriate value
     */
    public static Commands validateCommandType(ArrayList<String> cmd, String rawInput) {
        switch (cmd.get(0).toLowerCase()) {
            case "print":
                switch (cmd.get(1).toLowerCase()) {
                    case "all":
                        return Commands.PRINTALL;
                    case "donors":
                        return Commands.PRINTDONORS;
                }
                break;
            case "help":
                return Commands.HELP;
            case "import":
                return Commands.IMPORT;
            case "export":
                if (cmd.size() > 1) {
                    return Commands.EXPORT;
                }
                break;
            case "undo":
                return Commands.UNDO;
            case "redo":
                return Commands.REDO;
            case "create-profile":
                if (rawInput.matches(cmdRegexCreate)) {
                    return Commands.PROFILECREATE;
                }
            case "profile":
                if (rawInput.matches(cmdRegexProfileView)) {
                    switch (rawInput.substring(rawInput.indexOf('>') + 2)) {
                        case "view":
                            return Commands.PROFILEVIEW;
                        case "date-created":
                            return Commands.PROFILEDATECREATED;
                        case "donations":
                            return Commands.PROFILEDONATIONS;
                        case "delete":
                            return Commands.PROFILEDELETE;
                    }
                } else if (rawInput.matches(cmdRegexOrganUpdate)
                    && rawInput.contains("organ")) {
                    switch (rawInput.substring(rawInput.indexOf('>') + 2,
                        rawInput.lastIndexOf('=')).trim()) {
                        case "add-organ":
                            return Commands.ORGANADD;
                        case "remove-organ":
                            return Commands.ORGANREMOVE;
                        case "donate":
                            return Commands.ORGANDONATE;
                    }

                } else if (rawInput.matches(cmdRegexDonorUpdate)
                    && cmd.get(0).equals("profile")) {
                    return Commands.PROFILEUPDATE;
                }
        }
        return Commands.INVALID;
    }

    /**
     * Displays profiles attributes via the search methods
     *
     * @param currentDatabase Database reference
     * @param expression Search expression being used for searching
     */
    static void viewAttrBySearch(ProfileDatabase currentDatabase, String expression) {
        if (expression.lastIndexOf("=") == expression.indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.lastIndexOf("\""));

            if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {
                printSearchResults(currentDatabase.searchGivenNames(attr));
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
                printSearchResults(currentDatabase.searchLastNames(attr));
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                printSearchResults(currentDatabase.searchIRDNumber(Integer.valueOf(attr)));
            } else {
                System.out.println(searchErrorText);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * View date and time of profile creation.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void viewDateTimeCreatedBySearch(ProfileDatabase currentDatabase,
        String expression) {
        String attr = expression.substring(expression.indexOf("\"") + 1,
            expression.lastIndexOf("\""));

        if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                ArrayList<Profile> profileList = currentDatabase.searchGivenNames(attr);

                printProfileList(profileList);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                ArrayList<Profile> profileList = currentDatabase.searchLastNames(attr);

                printProfileList(profileList);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                ArrayList<Profile> profileList = currentDatabase
                    .searchIRDNumber(Integer.valueOf(attr));

                printProfileList(profileList);
            } else {
                System.out.println(searchErrorText);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * View organs available for donation.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void viewDonationsBySearch(ProfileDatabase currentDatabase, String expression) {
        String attr = expression.substring(expression.indexOf("\"") + 1,
            expression.lastIndexOf("\""));
        ArrayList<Profile> profileList = null;

        if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {

            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                profileList = currentDatabase.searchGivenNames(attr);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                profileList = currentDatabase.searchLastNames(attr);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                profileList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));
            } else {
                System.out.println(searchErrorText);
            }

        } else {
            System.out.println(searchErrorText);
        }

        if (profileList != null && profileList.size() > 0) {
            printDonorDonations(profileList);
        } else {
            System.out.println("No matching profiles found.");
        }

    }

    /**
     * Update profile attributes.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void updateProfilesBySearch(ProfileDatabase currentDatabase, String expression) {
        String[] attrList = expression.substring(expression.indexOf('>') + 1)
            .trim()
            .split("\"\\s");

        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") ==
            expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.indexOf(">") - 2);

            if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {
                ArrayList<Profile> profileList = currentDatabase.searchGivenNames(attr);

                updateProfileAttr(profileList, attrList);
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
                ArrayList<Profile> profileList = currentDatabase.searchLastNames(attr);

                updateProfileAttr(profileList, attrList);
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                ArrayList<Profile> profileList = currentDatabase
                    .searchIRDNumber(Integer.valueOf(attr));

                updateProfileAttr(profileList, attrList);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * Add organs to a profile.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void addOrgansBySearch(ProfileDatabase currentDatabase, String expression) {
        String[] organList = expression.substring(
            expression.lastIndexOf("=") + 1).replace("\"", "").split(",");

        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") ==
            expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.indexOf(">") - 2);

            if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {
                ArrayList<Profile> profileList = currentDatabase.searchGivenNames(attr);

                addOrgans(profileList, organList);
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {

                ArrayList<Profile> profileList = currentDatabase.searchLastNames(attr);

                addOrgans(profileList, organList);
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                test(currentDatabase, expression, organList);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * Remove organs available for donation to a profile profile.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void removeOrgansBySearch(ProfileDatabase currentDatabase, String expression) {
        String[] organList = expression.substring(expression.lastIndexOf("=") + 1).
            replace("\"", "")
            .split(",");

        // TODO should we be able to remove organs using search by names, as this means it will
        // TODO remove for all john smiths etc
        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") ==
            expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.indexOf(">") - 2);

            if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {
                ArrayList<Profile> profileList = currentDatabase.searchGivenNames(attr);

                removeOrgans(profileList, organList);
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
                ArrayList<Profile> profileList = currentDatabase.searchLastNames(attr);

                removeOrgans(profileList, organList);
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                ArrayList<Profile> profileList = currentDatabase
                    .searchIRDNumber(Integer.valueOf(attr));

                removeOrgans(profileList, organList);
            }
        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * Add organs available for donation to a profile profile.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void addDonationsMadeBySearch(ProfileDatabase currentDatabase,
        String expression) {
        String[] organList = expression.substring(expression.lastIndexOf("=") + 1)
            .replace("\"", "").split(",");

        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") ==
            expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.indexOf(">") - 2);

            if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {
                ArrayList<Profile> profileList = currentDatabase.searchGivenNames(attr);

                if (profileList.size() > 0) {
                    addDonation(profileList, organList);
                } else {
                    System.out.println(searchNotFoundText);
                }

            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
                ArrayList<Profile> profileList = currentDatabase.searchLastNames(attr);

                if (profileList.size() > 0) {
                    addDonation(profileList, organList);
                } else {
                    System.out.println(searchNotFoundText);
                }

            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                test(currentDatabase, expression, organList);
            } else {
                System.out.println(searchErrorText);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    // TODO check what main purpose this serves and name appropriately
    private static void test(ProfileDatabase currentDatabase, String expression,
        String[] organList) {
        String attr = expression.substring(expression.indexOf("\"") + 1,
            expression.indexOf(">") - 2);
        ArrayList<Profile> profileList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));

        addOrgans(profileList, organList);
    }

    /**
     * Delete donors from the database.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void deleteDonorBySearch(ProfileDatabase currentDatabase, String expression) {
        if (expression.lastIndexOf("=") == expression.indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.lastIndexOf("\""));

            if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {
                ArrayList<Profile> profileList = currentDatabase.searchGivenNames(attr);

                deleteProfiles(profileList, currentDatabase);
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
                ArrayList<Profile> profileList = currentDatabase.searchLastNames(attr);

                deleteProfiles(profileList, currentDatabase);
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                ArrayList<Profile> profileList = currentDatabase
                    .searchIRDNumber(Integer.valueOf(attr));

                deleteProfiles(profileList, currentDatabase);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * Display and print profile details in a list.
     *
     * @param profileList List of profiles
     */
    private static void printProfileList(ArrayList<Profile> profileList) {
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
     * @param profileList list of donors
     */
    private static void printDonorDonations(ArrayList<Profile> profileList) {
        for (Profile profile : profileList) {
            System.out.println("IRD: " + profile.getIrdNumber());
            System.out.println("Given Names: " + profile.getGivenNames());
            System.out.println("Last Names: " + profile.getLastNames());
            profile.viewDonations();
            System.out.println();
        }
    }

    /**
     * Display and print all search results from Profile array. If array empty, no search results
     * have been found.
     *
     * @param profileList Results from searching
     */
    private static void printSearchResults(ArrayList<Profile> profileList) {
        if (profileList.size() > 0) {
            for (Profile profile : profileList) {
                profile.viewAttributes();
                System.out.println();
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Create profile.
     *
     * @param currentDatabase Database reference
     * @param rawInput raw command input
     */
    public static void createProfile(ProfileDatabase currentDatabase, String rawInput) {
        try {
            String[] attrList = rawInput.substring(15).split("\"\\s");
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            Profile newProfile = new Profile(attrArray);
            currentDatabase.addProfile(newProfile);
            CommandUtils.addDonorHistory(newProfile.getId());
            System.out.println("Profile created.");

        } catch (IllegalArgumentException e) {
            System.out.println("Please enter the required attributes correctly.");

        } catch (IrdNumberConflictException e) {
            Integer errorIrdNumber = e.getIrdNumber();
            Profile errorProfile = currentDatabase.searchIRDNumber(errorIrdNumber).get(0);

            System.out.println("Error: IRD Number " + errorIrdNumber +
                    " already in use by profile " +
                    errorProfile.getGivenNames() + " " +
                    errorProfile.getLastNames());

        } catch (Exception e) {
            System.out.println("Please enter a valid command.");
        }
    }

    /**
     * Update profile attributes.
     *
     * @param profileList List of profiles
     * @param attrList Attributes to be updated and their values
     */
    private static void updateProfileAttr(ArrayList<Profile> profileList, String[] attrList) {
        if (profileList.size() > 0) {
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            String action;
            for (Profile profile : profileList) {
                action = "Profile " + profile.getId() + " updated details previous = " + profile
                    .getAttributesSummary() + " new = ";
                profile.setExtraAttributes(attrArray);
                action = action + profile.getAttributesSummary() + " at " + LocalDateTime.now();
                if (currentSessionHistory.size() != 0) {
                    if (historyPosition != currentSessionHistory.size() - 1) {
                        currentSessionHistory
                            .subList(historyPosition, currentSessionHistory.size() - 1).clear();
                    }
                }
                currentSessionHistory.add(action);
                historyPosition = currentSessionHistory.size() - 1;

            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Delete profiles.
     *
     * @param profileList list of profiles
     * @param currentDatabase Database reference
     */
    private static void deleteProfiles(ArrayList<Profile> profileList,
        ProfileDatabase currentDatabase) {
        boolean result;
        if (profileList.size() > 0) {
            for (Profile profile : profileList) {
                result = currentDatabase.deleteProfile(profile.getId());
                if (result) {
                    deletedProfiles.add(profile);
                    if (currentSessionHistory.size() != 0) {
                        if (historyPosition != currentSessionHistory.size() - 1) {
                            currentSessionHistory
                                .subList(historyPosition, currentSessionHistory.size() - 1).clear();
                        }
                    }
                    currentSessionHistory
                        .add("Profile " + profile.getId() + " deleted at " + LocalDateTime.now());
                    historyPosition = currentSessionHistory.size() - 1;
                }


            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Add organ donations.
     *
     * @param profileList list of donors
     * @param organList list of organs to be added
     */
    private static void addOrgans(ArrayList<Profile> profileList, String[] organList) {
        if (profileList.size() > 0) {
            Set<String> organSet = new HashSet<>(Arrays.asList(organList));

            for (Profile profile : profileList) {
                try {
                    profile.addOrgans(organSet);
                    if (currentSessionHistory.size() != 0) {
                        if (historyPosition != currentSessionHistory.size() - 1) {
                            currentSessionHistory
                                .subList(historyPosition, currentSessionHistory.size() - 1).clear();
                        }
                    }
                    currentSessionHistory.add(
                        "Profile " + profile.getId() + " set organs " + organSet + " at "
                            + LocalDateTime.now());
                    historyPosition = currentSessionHistory.size() - 1;
                } catch (IllegalArgumentException e) {
                    System.out.println("This organ already exists.");
                }
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * TODO Is this a duplicate function for a reason, unsure
     */
    private static void addDonation(ArrayList<Profile> profileList, String[] organList) {
        if (profileList.size() > 0) {
            Set<String> organSet = new HashSet<>(Arrays.asList(organList));

            for (Profile profile : profileList) {
                try {
                    profile.addDonations(organSet);
                    if (currentSessionHistory.size() != 0) {
                        if (historyPosition != currentSessionHistory.size() - 1) {
                            currentSessionHistory
                                .subList(historyPosition, currentSessionHistory.size() - 1).clear();
                        }
                    }
                    currentSessionHistory.add(
                        "Profile " + profile.getId() + " decided to donate these organs " + organSet
                            + " at " + LocalDateTime.now());
                    historyPosition = currentSessionHistory.size() - 1;
                } catch (IllegalArgumentException e) {
                    System.out.println("This organ already exists.");
                }
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Remove organ from donations list for donors.
     *
     * @param profileList list of donors
     * @param organList list of organs to be removed
     */
    private static void removeOrgans(ArrayList<Profile> profileList, String[] organList) {
        if (profileList.size() > 0) {
            Set<String> organSet = new HashSet<>(Arrays.asList(organList));

            for (Profile profile : profileList) {
                try {
                    profile.removeOrgans(organSet);
                    if (currentSessionHistory.size() != 0) {
                        if (historyPosition != currentSessionHistory.size() - 1) {
                            currentSessionHistory
                                .subList(historyPosition, currentSessionHistory.size() - 1).clear();
                        }
                    }
                    currentSessionHistory.add(
                        "Profile " + profile.getId() + " removed these organs " + organSet + " at "
                            + LocalDateTime.now());
                    historyPosition = currentSessionHistory.size() - 1;
                } catch (IllegalArgumentException e) {
                    System.out.println("This organ doesn't exist.");
                }
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    public static void help() {
        System.out.println("\nCreate a new profile:");
        System.out.println("create-profile {attributes (given-names, last-names, dob and ird is "
            + "required)}");
        System.out.println("e.g. create-profile given-names=\"Abby Rose\" last-names=\"Walker\" "
            + "dob=\"03-03-1998\" ird=\"123456789\"");

        System.out.println("\nView a profile:");
        System.out.println("profile {attributes to search donors by} > view");

        System.out.println("\nView the date a profile was created:");
        System.out.println("profile {attributes to search donors by} > date-created");

        System.out.println("\nView a donors past donations:");
        System.out.println("profile {attributes to search donors by} > donations");

        System.out.println("\nUpdate a donors attributes:");
        System.out.println("profile {attributes to search donors by} > {attributes to update}");
        System.out
            .println("e.g. profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03"
                + "-1998\" > height=\"169\"");

        System.out.println("\nAdd an organ to donate:");
        System.out
            .println("profile {attributes to search donors by} > add-organ=\" {list of organs "
                + "to donate} \"");
        System.out
            .println("e.g. profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03"
                + "-1998\" > add-organ=\"liver, kidney\"");

        System.out.println("\nRemove an organ to donate:");
        System.out.println("profile {attributes to search donors by} > remove-organ=\" {list of "
            + "organs to remove} \"");
        System.out.println("e.g. profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-"
            + "03-1998\" > remove-organ=\"liver, kidney\"");

        System.out.print("\nPrint all profiles: ");
        System.out.println("print all");

        System.out.print("\nPrint all donors: ");
        System.out.println("print donors");

        System.out.print("\nClose the app: ");
        System.out.println("quit");

        System.out.println("\nAttributes:");
        System.out.println("given-names, last-names, dob, dod, gender, height, weight, blood-type,"
            + " address, region, ird");

        System.out.println("\nOrgans:");
        System.out.println(
            "Liver, Kidney, Pancreas, Heart, Lung, Intestine, Cornea, Middle Ear, Skin, Bone, "
                + "Bone Marrow, Connective Tissue");

    }

    public static void helpSpecific(String cmd) {
        String[] cmdArray = {"create-profile", "view", "date-created", "donations", "update",
            "add-organ", "remove-organ", "print all", "print donors", "quit", "attributes",
            "organs"};
        String[] definitionArray = {"\nCreate a new profile", "\nView a profile",
            "\nView the date a profile was created", "\nView a donors past donations",
            "\nUpdate a donors attributes",
            "\nAdd an organ to donate", "\nRemove an organ to donate", "\nPrint all profiles ",
            "\nPrint all donors ", "\nClose the app ",
            "The possible attriubtes are : given-names, last-names, dob, dod, gender, height, "
                + "weight, blood-type, address, region, ird",
            "The possible organs are : Liver, Kidney, Pancreas, Heart, Lung, Intestine, Cornea, "
                + "Middle Ear, Skin, Bone, Bone Marrow, Connective Tissue"};
        String[] exampleArray = {
            "create-profile {attributes (given-names, last-names, dob and ird is required)}",
            "profile {attributes to search donors by} > view",
            "profile {attributes to search donors by} > date-created",
            "profile {attributes to search donors by} > donations",
            "profile {attributes to search donors by} > {attributes to update}"
            ,
            "profile {attributes to search donors by} > add-organ=\" {list of organs to donate} \"",
            "profile {attributes to search donors by} > remove-organ=\" {list of organs to remove} \"",
            "print all: ", "print donors", "quit"};
        if (Arrays.asList(cmdArray).contains(cmd)) {
            int position = Arrays.asList(cmdArray).indexOf(cmd);
            System.out.println(definitionArray[position]);
            if (position < 10) {
                System.out.println("The command is entered in this format:");
                System.out.println(exampleArray[position]);
            }
        } else {
            System.out.println("Invalid command");
        }
    }

    private static void addDonorHistory(int Id) {
        if (currentSessionHistory.size() != 0) {
            if (historyPosition != currentSessionHistory.size() - 1) {
                currentSessionHistory.subList(historyPosition, currentSessionHistory.size() - 1)
                    .clear();
            }
        }
        currentSessionHistory.add("Profile " + Id + " added at " + LocalDateTime.now());
        historyPosition = currentSessionHistory.size() - 1;
    }

    public static ArrayList<String> getHistory() {
        return currentSessionHistory;
    }

    /**
     * Undo the previous action.
     *
     * @param currentDatabase Database reference
     */
    public static void undo(ProfileDatabase currentDatabase) {
        try {
            String action = currentSessionHistory.get(historyPosition);
            action = action.substring(0, action.indexOf(" at"));
            if (action.contains("added")) {
                int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                Profile profile = currentDatabase.getProfile(id);
                currentDatabase.deleteProfile(id);
                unaddedProfiles.add(profile);
                if (historyPosition != 0) {
                    historyPosition -= 1;
                } else {
                    historyPosition = 1;
                }
            } else if (action.contains("deleted")) {
                int oldid = Integer.parseInt(action.replaceAll("[\\D]", ""));
                int id = currentDatabase
                    .restoreProfile(oldid, deletedProfiles.get(deletedProfiles.size() - 1));
                deletedProfiles.remove(deletedProfiles.get(deletedProfiles.size() - 1));
                for (int i = 0; i < currentSessionHistory.size() - 1; i++) {
                    if (currentSessionHistory.get(i).contains("Profile " + oldid)) {
                        currentSessionHistory.set(i,
                            ("Profile " + id + " " + currentSessionHistory.get(i).substring(
                                action.indexOf("Profile " + oldid) + 6 + Integer.toString(id)
                                    .length())));
                    }
                }
                currentSessionHistory
                    .set(historyPosition, ("Profile " + id + " deleted at " + LocalDateTime.now()));
                if (historyPosition != 0) {
                    historyPosition -= 1;
                }
            } else if (action.contains("removed")) {
                int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                Profile profile = currentDatabase.getProfile(id);
                Set<String> organSet = new HashSet<>(Arrays.asList(
                    action.substring(action.indexOf("[") + 1, action.indexOf("]")).split(",")));
                profile.addOrgans(organSet);
                if (historyPosition != 0) {
                    historyPosition -= 1;
                }
            } else if (action.contains("set")) {
                int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                Profile profile = currentDatabase.getProfile(id);
                Set<String> organSet = new HashSet<>(Arrays.asList(
                    action.substring(action.indexOf("[") + 1, action.indexOf("]")).split(",")));
                profile.removeOrgans(organSet);
                if (historyPosition != 0) {
                    historyPosition -= 1;
                }
            } else if (action.contains("donate")) {
                int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                Profile profile = currentDatabase.getProfile(id);
                Set<String> organSet = new HashSet<>(Arrays.asList(
                    action.substring(action.indexOf("[") + 1, action.indexOf("]")).split(",")));
                profile.removeDonations(organSet);
                if (historyPosition != 0) {
                    historyPosition -= 1;
                }
            } else if (action.contains("update")) {
                int id = Integer.parseInt(
                    action.substring(0, action.indexOf("previous")).replaceAll("[\\D]", ""));
                Profile profile = currentDatabase.getProfile(id);
                String old = action.substring(action.indexOf("ird"), action.indexOf("new"));
                profile.setExtraAttributes(new ArrayList<>(Arrays.asList(old.split(","))));
                if (historyPosition != 0) {
                    historyPosition -= 1;
                }
            }
            System.out.println("Action undo");
        } catch (Exception e) {
            System.out.println("No commands have been entered");
        }
    }

    /**
     * Redo previously undone action.
     *
     * @param currentDatabase Database reference
     */
    public static void redo(ProfileDatabase currentDatabase) {
        try {
            System.out.println(historyPosition);

            if (historyPosition != currentSessionHistory.size()) {
                historyPosition += 1;
                String action;
                if (historyPosition == 0) {
                    historyPosition = 1;
                    action = currentSessionHistory.get(historyPosition);
                    historyPosition = 0;
                } else {
                    action = currentSessionHistory.get(historyPosition);
                }
                action = action.substring(0, action.indexOf(" at"));
                if (action.contains("added")) {
                    int oldid = Integer.parseInt(action.replaceAll("[\\D]", ""));
                    int id = currentDatabase
                        .restoreProfile(oldid, unaddedProfiles.get(unaddedProfiles.size() - 1));
                    unaddedProfiles.remove(unaddedProfiles.get(unaddedProfiles.size() - 1));
                    for (int i = 0; i < currentSessionHistory.size() - 1; i++) {
                        if (currentSessionHistory.get(i).contains("Profile " + oldid)) {
                            currentSessionHistory.set(i,
                                ("Profile " + id + currentSessionHistory.get(i).substring(
                                    action.indexOf("Profile " + oldid) + 6 + Integer.toString(id)
                                        .length())));
                        }
                    }
                    currentSessionHistory.set(historyPosition,
                        ("Profile " + id + " added at " + LocalDateTime.now()));
                } else if (action.contains("deleted")) {
                    int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    currentDatabase.deleteProfile(id);
                    deletedProfiles.add(profile);
                } else if (action.contains("removed")) {
                    int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    Set<String> organSet = new HashSet<>(Arrays.asList(
                        action.substring(action.indexOf("[") + 1, action.indexOf("]")).split(",")));
                    profile.removeOrgans(organSet);
                } else if (action.contains("set")) {
                    int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    Set<String> organSet = new HashSet<>(Arrays.asList(
                        action.substring(action.indexOf("[") + 1, action.indexOf("]")).split(",")));
                    profile.addOrgans(organSet);
                } else if (action.contains("donate")) {
                    int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    Set<String> organSet = new HashSet<>(Arrays.asList(
                        action.substring(action.indexOf("[") + 1, action.indexOf("]")).split(",")));
                    profile.addDonations(organSet);
                } else if (action.contains("update")) {
                    int id = Integer.parseInt(
                        action.substring(0, action.indexOf("previous")).replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    String newInfo = action.substring(action.indexOf("ird"));
                    profile.setExtraAttributes(new ArrayList<>(Arrays.asList(newInfo.split(","))));
                }
                System.out.println("Command redone");
            } else {
                System.out.println("There are no commands to redo");
            }
        } catch (Exception e) {
            System.out.println("No commands have been entered.");
        }

    }

    public static int getPosition() {
        return historyPosition;
    }
}
