package odms.cli;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import odms.data.ProfileDatabase;
import odms.profile.Profile;

public class CommandUtils {

    public static ArrayList<String> currentSessionHistory = new ArrayList<>();

    public static int historyPosition = 0;
    protected static ArrayList<Profile> deletedProfiles = new ArrayList<>();
    private static ArrayList<Profile> unaddedProfiles = new ArrayList<>();

    protected static String searchErrorText = "Please enter only one search criteria "
        + "(given-names, last-names, ird).";
    protected static String searchNotFoundText = "There are no profiles that match this criteria.";

    private static final String cmdRegexCreate =
        "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]"
        + "([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)?)+)"
        + "[\"]))*";

    private static final String cmdRegexProfileView =
        "([a-z]+)((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]"
        + "(([a-zA-Z0-9][-]?(\\s)?)+)[\"]))+(\\s[>]\\s"
        + "([a-z]+)([-]([a-z]+))?)";

    private static final String cmdRegexProfileUpdate =
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
    static Commands validateCommandType(ArrayList<String> cmd, String rawInput) {
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

                } else if (rawInput.matches(cmdRegexProfileUpdate)
                    && cmd.get(0).equals("profile")) {
                    return Commands.PROFILEUPDATE;
                }
        }
        return Commands.INVALID;
    }

    /**
     * Add organs to a profile.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    static void addOrgansBySearch(ProfileDatabase currentDatabase, String expression) {
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
    static void removeOrgansBySearch(ProfileDatabase currentDatabase, String expression) {
        String[] organList = expression.substring(expression.lastIndexOf("=") + 1).
            replace("\"", "")
            .split(",");

        // TODO should we be able to remove organs using search by names, as this means it will
        // TODO remove for printAll john smiths etc
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
    static void addDonationsMadeBySearch(ProfileDatabase currentDatabase,
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
     * Add organ donations.
     *
     * @param profileList list of profiles
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
     * Remove organ from donations list for profiles.
     *
     * @param profileList list of profile
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
                System.out.println(action);
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
            System.out.println(currentSessionHistory.size());

            if (historyPosition != currentSessionHistory.size()) {
                System.out.println("a");
                historyPosition += 1;
                String action;
                if (historyPosition == 0) {
                    historyPosition = 1;
                    action = currentSessionHistory.get(historyPosition);
                    historyPosition = 0;
                } else {
                    System.out.println(historyPosition);
                    System.out.println(currentSessionHistory);
                    action = currentSessionHistory.get(historyPosition);
                }
                System.out.println(action);
                action = action.substring(0, action.indexOf(" at"));
                System.out.println("c");
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
