package odms.cli;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import odms.dao.DAOFactory;
import odms.dao.ReadOnlyDAO;

import odms.data.ProfileDatabase;
import odms.enums.OrganEnum;
import odms.profile.Profile;

public class CommandUtils {

    public static ArrayList<String> currentSessionHistory = new ArrayList<>();

    public static int historyPosition = 0;
    protected static ArrayList<Profile> deletedProfiles = new ArrayList<>();
    private static ArrayList<Profile> unaddedProfiles = new ArrayList<>();

    protected static String searchErrorText = "Please enter only one search criteria\n "
                                            + "Profiles: given-names, last-names, ird\n"
                                            + "Users: name, staffID";
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
     * @param rawInput User entered CLI command.
     * @return the enum Commands appropriate value
     */
    static Commands validateCommandType(ArrayList<String> cmd, String rawInput) {
        switch (cmd.get(0).toLowerCase()) {
            case "print":
                if (cmd.size() == 2) { return Commands.INVALID; }
                if (cmd.get(2).toLowerCase().equals("profiles")) {
                    return Commands.PRINTALLPROFILES;
                } else if (cmd.get(2).toLowerCase().equals("clinicians")) {
                    return Commands.PRINTALLCLINICIANS;
                } else if (cmd.get(2).toLowerCase().equals("donors")) {
                    return Commands.PRINTDONORS;
                } else {
                    return Commands.INVALID;
                }
            case "help":
                return Commands.HELP;
            case "import":
                return Commands.IMPORT;
            case "export":
                return Commands.EXPORT;
            case "undo":
                return Commands.UNDO;
            case "redo":
                return Commands.REDO;
            case "create-profile":
                if (rawInput.matches(cmdRegexCreate)) {
                    return Commands.PROFILECREATE;
                }
                break;
            case "create-clinician":
                if (rawInput.matches(cmdRegexCreate)) {
                    return Commands.CLINICIANCREATE;
                }
                break;
            case "profile":
                if (rawInput.matches(cmdRegexProfileView)) {
                    switch (rawInput.substring(rawInput.indexOf('>') + 2)) {
                        case "view":
                            return Commands.PROFILEVIEW;
                        case "date-created":
                            return Commands.PROFILEDATECREATED;
                        case "organs":
                            return Commands.PROFILEORGANS;
                        case "delete":
                            return Commands.PROFILEDELETE;
                    }
                } else if (rawInput.matches(cmdRegexOrganUpdate)
                    && rawInput.contains("organ")) {
                    switch (rawInput.substring(rawInput.indexOf('>') + 2,
                        rawInput.lastIndexOf('=')).trim()) {
                        case "add-organ":
                            return Commands.ORGANADD;
                        case "receive-organ":
                            return Commands.RECEIVERADD;
                        case "remove-organ":
                            return Commands.ORGANREMOVE;
                        case "removereceive-organ":
                            return Commands.RECEIVEREMOVE;
                        case "donate-organ":
                            return Commands.ORGANDONATE;
                    }
                } else if (rawInput.matches(cmdRegexProfileUpdate)
                    && cmd.get(0).equals("profile")) {
                    return Commands.PROFILEUPDATE;
                }
                break;
            case "clinician":
                if (rawInput.matches(cmdRegexProfileView)) {
                    switch (rawInput.substring(rawInput.indexOf('>') + 2)) {
                        case "view":
                            return Commands.CLINICIANEVIEW;
                        case "date-created":
                            return Commands.CLINICIANDATECREATED;
                        case "delete":
                            return Commands.CLINICIANDELETE;
                    }
                } else if (rawInput.matches(cmdRegexProfileUpdate)
                        && cmd.get(0).equals("clinician")) {
                    return Commands.CLINICIANUPDATE;
                }
                break;
            case "db-read":
                return Commands.SQLREADONLY;
            default:
                return Commands.INVALID;
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
            expression.lastIndexOf("=") + 1).replace("\"", "").replace(" ", "").split(",");

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
                String attr_ird = expression.substring(expression.indexOf("\"") + 1,
                        expression.indexOf(">") - 2);
                ArrayList<Profile> profileList = currentDatabase.searchIRDNumber(Integer.valueOf(attr_ird));

                addOrgans(profileList, organList);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * Add organs to a profile's received organs.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    static void addReceiverOrgansBySearch(ProfileDatabase currentDatabase, String expression) {
        String[] organList = expression.substring(
                expression.lastIndexOf("=") + 1).replace("\"", "").replace(" ", "").split(",");

        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") ==
                expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                    expression.indexOf(">") - 2);

            if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {
                ArrayList<Profile> profileList = currentDatabase.searchGivenNames(attr);

                addReceiverOrgans(profileList, organList);
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {

                ArrayList<Profile> profileList = currentDatabase.searchLastNames(attr);

                addReceiverOrgans(profileList, organList);
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                String attr_ird = expression.substring(expression.indexOf("\"") + 1,
                        expression.indexOf(">") - 2);
                ArrayList<Profile> profileList = currentDatabase.searchIRDNumber(Integer.valueOf(attr_ird));

                addReceiverOrgans(profileList, organList);
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
        // TODO remove for printAllProfiles john smiths etc
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
     * Remove organs available for donation to a profile profile.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    static void removeReceiverOrgansBySearch(ProfileDatabase currentDatabase, String expression) {
        String[] organList = expression.substring(expression.lastIndexOf("=") + 1).
                replace("\"", "")
                .split(",");

        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") ==
                expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                    expression.indexOf(">") - 2);

            if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {
                ArrayList<Profile> profileList = currentDatabase.searchGivenNames(attr);

                removeReceiverOrgansDonating(profileList, organList);
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
                ArrayList<Profile> profileList = currentDatabase.searchLastNames(attr);

                removeReceiverOrgansDonating(profileList, organList);
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                ArrayList<Profile> profileList = currentDatabase
                        .searchIRDNumber(Integer.valueOf(attr));

                removeReceiverOrgansDonating(profileList, organList);
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
     * Add organ donations.
     *
     * @param profileList list of profiles
     * @param organList list of organs to be added
     */
    private static void addReceiverOrgans(ArrayList<Profile> profileList, String[] organList) {
        if (profileList.size() > 0) {
            HashSet<OrganEnum> organSet = OrganEnum.stringListToOrganSet(Arrays.asList(organList));

            for (Profile profile : profileList) {
                try {
                    profile.addOrgansRequired(organSet);
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
                    profile.setDonor(true);
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

    /**
     * Remove organ from receiver list for profiles.
     *
     * @param profileList list of profile
     * @param organList list of organs to be removed
     */
    private static void removeReceiverOrgansDonating(ArrayList<Profile> profileList, String[] organList) {
        if (profileList.size() > 0) {
            Set<String> organSet = new HashSet<>(Arrays.asList(organList));

            for (Profile profile : profileList) {
                try {
                    profile.removeOrgansRequired(OrganEnum.stringListToOrganSet(Arrays.asList(organList)));
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
                                        action.indexOf("Profile " + oldid) + 6 + Integer
                                                .toString(id)
                                                .length())));
                    }
                }
                currentSessionHistory
                        .set(historyPosition,
                                ("Profile " + id + " deleted at " + LocalDateTime.now()));
                if (historyPosition != 0) {
                    historyPosition -= 1;
                }
            } else if (action.contains("removed")) {
                int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                Profile profile = currentDatabase.getProfile(id);
                profile.addOrgansDonating(OrganEnum.stringListToOrganSet(Arrays.asList(
                        action.substring(
                                action.indexOf("[") + 1,
                                action.indexOf("]")).split(",")
                )));
                if (historyPosition != 0) {
                    historyPosition -= 1;
                }
            } else if (action.contains("set")) {
                int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                Profile profile = currentDatabase.getProfile(id);
                List<String> organSet = new ArrayList<>(Arrays.asList(
                        action.substring(
                                action.indexOf("[") + 1,
                                action.indexOf("]")).split(","))
                );
                profile.removeOrgansDonating(OrganEnum.stringListToOrganSet(organSet));
                if (historyPosition != 0) {
                    historyPosition -= 1;
                }
            } else if (action.contains("donate")) {
                int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                Profile profile = currentDatabase.getProfile(id);
                List<String> organSet = new ArrayList<>(Arrays.asList(
                        action.substring(
                                action.indexOf("[") + 1,
                                action.indexOf("]")).split(","))
                );
                // TODO bug here for removing organs from wrong list based on command
                profile.removeOrgansDonated(OrganEnum.stringListToOrganSet(organSet));
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
            } else if (action.contains("EDITED")) {
                int id = Integer.parseInt(
                        action.substring(0, action.indexOf("PROCEDURE")).replaceAll("[\\D]", ""));
                Profile profile = currentDatabase.getProfile(id);
                int procedurePlace = Integer.parseInt(
                        action.substring(action.indexOf("PROCEDURE"), action.indexOf("EDITED"))
                                .replaceAll("[\\D]", ""));
                String previous = action
                        .substring(action.indexOf("PREVIOUS(") + 9, action.indexOf(") OLD"));
                String[] previousValues = previous.split(",");
                String organs = action
                        .substring(action.indexOf("[") + 1, action.indexOf("] CURRENT"));
                List<String> List = new ArrayList<>(Arrays.asList(organs.split(",")));
                ArrayList<OrganEnum> organList = new ArrayList<>();
                System.out.println(organs);
                for (String organ : List) {
                    System.out.println(organ);
                    try {
                        organList.add(OrganEnum.valueOf(organ.replace(" ", "")));
                    } catch (IllegalArgumentException e) {
                        System.out.println(e);
                    }
                }
                profile.getAllProcedures().get(procedurePlace).setSummary(previousValues[0]);
                profile.getAllProcedures().get(procedurePlace)
                        .setDate(LocalDate.parse(previousValues[1]));
                if (previousValues.length == 3) {
                    profile.getAllProcedures().get(procedurePlace)
                            .setLongDescription(previousValues[2]);
                }
                profile.getAllProcedures().get(procedurePlace).setOrgansAffected(organList);
                if (historyPosition != 0) {
                    historyPosition -= 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    List<String> organSet = new ArrayList<>(Arrays.asList(
                        action.substring(action.indexOf("[") + 1, action.indexOf("]")).split(",")));
                    profile.removeOrgansDonating(OrganEnum.stringListToOrganSet(organSet));
                } else if (action.contains("set")) {
                    int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    profile.addOrgansDonating(OrganEnum.stringListToOrganSet(Arrays.asList(
                        action.substring(
                                action.indexOf("[") + 1,
                                action.indexOf("]")).split(",")
                    )));
                } else if (action.contains("donate")) {
                    int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    profile.addOrgansDonated(
                            OrganEnum.stringListToOrganSet(
                                    Arrays.asList(
                                            action.substring(
                                                    action.indexOf("[") + 1,
                                                    action.indexOf("]")).split(",")
                                    )
                            )
                    );
                } else if (action.contains("update")) {
                    int id = Integer.parseInt(
                        action.substring(0, action.indexOf("previous")).replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    String newInfo = action.substring(action.indexOf("ird"));
                    profile.setExtraAttributes(new ArrayList<>(Arrays.asList(newInfo.split(","))));
                }  else if(action.contains("EDITED")){
                    int id = Integer.parseInt(action.substring(0, action.indexOf("PROCEDURE")).replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    int procedurePlace = Integer.parseInt(action.substring(action.indexOf("PROCEDURE"), action.indexOf("EDITED")).replaceAll("[\\D]", ""));
                    String previous = action.substring(action.indexOf("CURRENT(")+8, action.indexOf(") NEW"));
                    String[] previousValues = previous.split(",");
                    String organs;
                    ArrayList<OrganEnum> organList = new ArrayList<>();
                    organs = action.substring(action.indexOf("NEWORGANS["), action.indexOf("]END"));
                    List<String> List = new ArrayList<>(Arrays.asList(organs.split(",")));
                    for(String organ : List){
                        System.out.println(organ);
                        organList.add(OrganEnum.valueOf(organ
                                .replace(" ","")
                                .replace("NEWORGANS[",""))
                        );
                    }
                    profile.getAllProcedures().get(procedurePlace).setSummary(previousValues[0]);
                    profile.getAllProcedures().get(procedurePlace).setDate(LocalDate.parse(previousValues[1]));
                    if (previousValues.length == 3) {
                        profile.getAllProcedures().get(procedurePlace)
                                .setLongDescription(previousValues[2]);
                    }
                    profile.getAllProcedures().get(procedurePlace).setOrgansAffected(organList);
                }
                System.out.println("Command redone");
            } else {
                System.out.println("There are no commands to redo");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("No commands have been entered.");
        }

    }

    /**
     * Supplys the read-only query to the database connection DAO.
     * @param input query to be executed.
     */
    public static void executeDatabaseRead(String input) {
        String query = input.substring(input.indexOf(' '));

        ReadOnlyDAO accessObject = DAOFactory.getReadOnlyDao();
        accessObject.queryDatabase(query);
    }

    public static int getPosition() {
        return historyPosition;
    }
}
