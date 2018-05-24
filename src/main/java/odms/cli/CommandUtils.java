package odms.cli;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import odms.controller.HistoryController;
import odms.data.ProfileDatabase;
import odms.enums.OrganEnum;
import odms.profile.Profile;

public class CommandUtils {

    protected static String searchErrorText = "Please enter only one search criteria "
        + "(given-names, last-names, ird).";
    protected static String searchNotFoundText = "There are no profiles that match this criteria.";
    private static int historyPosition = HistoryController.getPosition();

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
                        case "donations": // TODO is this meant to be donating or donated
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

                removeOrgansDonating(profileList, organList);
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
                ArrayList<Profile> profileList = currentDatabase.searchLastNames(attr);

                removeOrgansDonating(profileList, organList);
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                ArrayList<Profile> profileList = currentDatabase
                    .searchIRDNumber(Integer.valueOf(attr));

                removeOrgansDonating(profileList, organList);
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
            HashSet<OrganEnum> organSet = OrganEnum.stringListToOrganSet(Arrays.asList(organList));

            for (Profile profile : profileList) {
                try {
                    profile.addOrgansDonating(organSet);
                } catch (IllegalArgumentException e) {
                    System.out.println("This organ already exists.");
                } catch (Exception e) {
                    System.out.println("A profile cannot be both a receiver and donor "
                            + "for the same organ");
                    System.out.println(e.getMessage());
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

            for (Profile profile : profileList) {
                try {
                    profile.addOrgansDonated(OrganEnum.stringListToOrganSet(Arrays.asList(organList)));
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
    private static void removeOrgansDonating(ArrayList<Profile> profileList, String[] organList) {
        if (profileList.size() > 0) {
            Set<String> organSet = new HashSet<>(Arrays.asList(organList));

            for (Profile profile : profileList) {
                try {
                    profile.removeOrgansDonating(OrganEnum.stringListToOrganSet(Arrays.asList(organList)));
                } catch (IllegalArgumentException e) {
                    System.out.println("This organ doesn't exist.");
                }
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

}
