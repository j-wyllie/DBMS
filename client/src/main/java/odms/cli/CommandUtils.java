package odms.cli;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import odms.controller.database.CommonDAO;
import odms.controller.database.DAOFactory;
import odms.controller.database.ProfileDAO;
import odms.controller.profile.ProfileGeneralControllerTODOContainsOldProfileMethods;
import odms.controller.profile.UndoRedoCLIService;
import odms.model.data.ProfileDatabase;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;

public class CommandUtils {

    public CommandUtils() {
        throw new UnsupportedOperationException();
    }

    private static final String CMD_REGEX_CREATE =
            "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]"
                    + "([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)?)+)"
                    + "[\"]))*";

    private static final String CMD_REGEX_PROFILE_VIEW =
            "([a-z]+)((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]"
                    + "(([a-zA-Z0-9][-]?(\\s)?)+)[\"]))+(\\s[>]\\s"
                    + "([a-z]+)([-]([a-z]+))?)";

    private static final String CMD_REGEX_PROFILE_UPDATE =
            "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]"
                    + "([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)"
                    + "?)+)[\"]))*(\\s[>])((\\s([a-z]+)([-]([a-z]"
                    + "+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)?)+)"
                    + "[\"]))*";

    private static final String CMD_REGEX_ORGAN_UPDATE =
            "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]"
                    + "([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)"
                    + "?)+)[\"]))*(\\s[>](\\s([a-z]+)([-]([a-z]+)"
                    + ")?)([=][\"](([a-zA-Z]([-])?([,](\\s)?)*)+)"
                    + "[\"]))*";

    public static ArrayList<String> currentSessionHistory = new ArrayList<>();

    public static int historyPosition;

    protected static ArrayList<Profile> deletedProfiles = new ArrayList<>();

    protected static String searchErrorText = "Please enter only one search criteria\n "
            + "Profiles: given-names, last-names, nhi\n"
            + "Users: name, staffID";

    protected static String searchNotFoundText = "There are no profiles that match this criteria.";

    private static ArrayList<Profile> unaddedProfiles = new ArrayList<>();

    /**
     * Performs checks over the input to match a valid command.
     * If nothing is matched than an invalid command has been used.
     *
     * @param cmd      the command being validated
     * @param rawInput user entered CLI command.
     * @return the enum Commands appropriate value
     */
    static Commands validateCommandType(ArrayList<String> cmd, String rawInput) {
        switch (cmd.get(0).toLowerCase()) {
            case "print":
                if (cmd.size() == 2) { return Commands.INVALID;                 }
                if (cmd.get(2).equalsIgnoreCase("profiles")) {
                    return Commands.PRINTALLPROFILES;
                } else if (cmd.get(2).equalsIgnoreCase("clinicians")) {
                    return Commands.PRINTALLCLINICIANS;
                } else if (cmd.get(2).toLowerCase().equals("users")) {
                    return Commands.PRINTALLUSERS;
                }else if (cmd.get(2).toLowerCase().equals("donors")) {
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
                if (rawInput.matches(CMD_REGEX_CREATE)) {
                    return Commands.PROFILECREATE;
                }
                break;
            case "create-clinician":
                if (rawInput.matches(CMD_REGEX_CREATE)) {
                    return Commands.CLINICIANCREATE;
                }
                break;
            case "profile":
                if (rawInput.matches(CMD_REGEX_PROFILE_VIEW)) {
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
                } else if (rawInput.matches(CMD_REGEX_ORGAN_UPDATE)
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

                } else if (rawInput.matches(CMD_REGEX_PROFILE_UPDATE)
                        && cmd.get(0).equals("profile")) {
                    return Commands.PROFILEUPDATE;
                }
                break;
            case "clinician":
                if (rawInput.matches(CMD_REGEX_PROFILE_VIEW)) {
                    switch (rawInput.substring(rawInput.indexOf('>') + 2)) {
                        case "view":
                            return Commands.CLINICIANEVIEW;
                        case "date-created":
                            return Commands.CLINICIANDATECREATED;
                        case "delete":
                            return Commands.CLINICIANDELETE;
                    }
                } else if (rawInput.matches(CMD_REGEX_PROFILE_UPDATE)
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
     * @param expression search expression.
     */
    static void addOrgansBySearch(String expression) {
        String[] organList = expression.substring(
                expression.lastIndexOf("=") + 1).replace("\"", "").replace(" ", "").split(",");
        List<Profile> profiles = new ArrayList<>();
        try {
            profiles = search(expression);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        addOrgans(profiles, organList);
    }

    /**
     * Add organs to a profile's received organs.
     * @param expression search expression.
     */
    static void addReceiverOrgansBySearch(String expression) {
        String[] organList = expression.substring(
                expression.lastIndexOf("=") + 1).replace("\"", "").replace(" ", "").split(",");
        List<Profile> profiles = new ArrayList<>();
        try {
            profiles = search(expression);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        addReceiverOrgans(profiles, organList);
    }

    /**
     * Remove organs available for donation to a profile profile.
     * @param expression search expression.
     */
    static void removeOrgansBySearch(String expression) {
        String[] organList = expression.substring(expression.lastIndexOf("=") + 1)
                .replace("\"", "")
                .split(",");
        List<Profile> profiles = new ArrayList<>();
        try {
            profiles = search(expression);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        removeOrgansDonating(profiles, organList);

        // TODO should we be able to remove organs using search by names, as this means it will
        // TODO remove for printAllProfiles john smiths etc
    }

    /**
     * Remove organs available for donation to a profile profile.
     * @param expression search expression.
     */
    static void removeReceiverOrgansBySearch(String expression) {
        List<Profile> profiles = new ArrayList<>();
        try {
            profiles = search(expression);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] organList = expression.substring(expression.lastIndexOf("=") + 1)
                .replace("\"", "")
                .split(",");
        removeReceiverOrgansDonating(profiles, organList);
    }

    /**
     * Add organs available for donation to a profile profile.
     * @param expression search expression.
     */
    static void addDonationsMadeBySearch(String expression) {
        List<Profile> profiles = new ArrayList<>();
        try {
            profiles = search(expression);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] organList = expression.substring(expression.lastIndexOf("=") + 1)
                .replace("\"", "").split(",");

        if (profiles.size() > 0) {
            addDonation(profiles, organList);
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Gets the profiles that match the criteria entered in the expression.
     * @param expression representing the fields.
     * @return the list of profiles.
     * @throws SQLException error.
     */
    private static List<odms.model.profile.Profile> search(String expression) throws SQLException {
        ProfileDAO database = DAOFactory.getProfileDao();
        List<odms.model.profile.Profile> profiles = new ArrayList<>();

        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") ==
                expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                    expression.indexOf(">") - 2);

            if (expression.substring(8, 8 + "given-names".length()).equals("given-names")
                || expression.substring(8, 8 + "last-names".length()).equals("last-names")
                || expression.substring(8, 8 + "nhi".length()).equals("nhi")) {

                profiles = database.search(attr, 0, 0, null,
                        null, null, null);
            } else {
                System.out.println(searchErrorText);
            }
        } else {
            System.out.println(searchErrorText);
        }
        return profiles;
    }

//    // TODO check what main purpose this serves and name appropriately
//    private static void test(String expression,
//            String[] organList) {
//        String attr = expression.substring(expression.indexOf("\"") + 1,
//                expression.indexOf(">") - 2);
//        List<Profile> profileList = currentDatabase.searchNHI(attr);
//
//        addOrgans(profileList, organList);
//    }

    /**
     * Add organ donations.
     * @param profileList list of profiles.
     * @param organList list of organs to be added.
     */
    private static void addOrgans(List<Profile> profileList, String[] organList) {
        if (profileList.size() > 0) {
            Set<OrganEnum> organSet = OrganEnum.stringListToOrganSet(Arrays.asList(organList));

            for (Profile profile : profileList) {
                try {
                    UndoRedoCLIService.addOrgansDonating(organSet, profile);
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
     * @param profileList list of profiles.
     * @param organList list of organs to be added.
     */
    private static void addReceiverOrgans(List<Profile> profileList, String[] organList) {
        if (!profileList.isEmpty()) {
            Set<OrganEnum> organSet = OrganEnum.stringListToOrganSet(Arrays.asList(organList));

            for (Profile profile : profileList) {
                try {
                    UndoRedoCLIService.addOrgansRequired(organSet, profile);
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
    private static void addDonation(List<Profile> profileList, String[] organList) {
        if (profileList.size() > 0) {

            for (Profile profile : profileList) {
                try {
                    UndoRedoCLIService.addOrgansDonated(
                            OrganEnum.stringListToOrganSet(Arrays.asList(organList)), profile);
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
     * @param profileList list of profile
     * @param organList list of organs to be removed
     */
    private static void removeOrgansDonating(List<Profile> profileList, String[] organList) {
        if (profileList.size() > 0) {
            Set<String> organSet = new HashSet<>(Arrays.asList(organList));

            for (Profile profile : profileList) {
                try {
                    UndoRedoCLIService.removeOrgansDonating(
                            OrganEnum.stringListToOrganSet(Arrays.asList(organList)), profile);
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
     * @param profileList list of profile
     * @param organList list of organs to be removed
     */
    private static void removeReceiverOrgansDonating(List<Profile> profileList,
            String[] organList) {
        if (profileList.size() > 0) {
            Set<String> organSet = new HashSet<>(Arrays.asList(organList));

            for (Profile profile : profileList) {
                try {
                    UndoRedoCLIService.removeOrgansRequired(
                            OrganEnum.stringListToOrganSet(Arrays.asList(organList)), profile);
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
                    if (currentSessionHistory.get(i).contains("profile " + oldid)) {
                        currentSessionHistory.set(i,
                                "profile " + id + " " + currentSessionHistory.get(i).substring(
                                        action.indexOf("profile " + oldid) + 6 + Integer
                                                .toString(id)
                                                .length()));
                    }
                }
                currentSessionHistory
                        .set(historyPosition,
                                "profile " + id + " deleted at " + LocalDateTime.now());
                if (historyPosition != 0) {
                    historyPosition -= 1;
                }
            } else if (action.contains("removed")) {
                int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                Profile profile = currentDatabase.getProfile(id);
                UndoRedoCLIService.addOrgansDonating(OrganEnum.stringListToOrganSet(Arrays.asList(
                        action.substring(
                                action.indexOf("[") + 1,
                                action.indexOf("]")).split(",")
                )), profile);
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
                UndoRedoCLIService.removeOrgansDonating(OrganEnum.stringListToOrganSet(organSet), profile);
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
                UndoRedoCLIService.removeOrgansDonated(OrganEnum.stringListToOrganSet(organSet), profile);
                if (historyPosition != 0) {
                    historyPosition -= 1;
                }
            } else if (action.contains("update")) {
                int id = Integer.parseInt(
                        action.substring(0, action.indexOf("previous")).replaceAll("[\\D]", ""));
                Profile profile = currentDatabase.getProfile(id);
                System.out.println(action);
                String old = action.substring(action.indexOf("nhi"), action.indexOf("new"));
                UndoRedoCLIService.setExtraAttributes(new ArrayList<>(Arrays.asList(old.split(","))), profile);
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
                List<String> list = new ArrayList<>(Arrays.asList(organs.split(",")));
                ArrayList<OrganEnum> organList = new ArrayList<>();
                System.out.println(organs);
                for (String organ : list) {
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
                        if (currentSessionHistory.get(i).contains("profile " + oldid)) {
                            currentSessionHistory.set(i,
                                    "profile " + id + currentSessionHistory.get(i).substring(
                                            action.indexOf("profile " + oldid) + 6 + Integer
                                                    .toString(id)
                                                    .length()));
                        }
                    }
                    currentSessionHistory.set(historyPosition,
                            "profile " + id + " added at " + LocalDateTime.now());
                } else if (action.contains("deleted")) {
                    int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    currentDatabase.deleteProfile(id);
                    deletedProfiles.add(profile);
                } else if (action.contains("removed")) {
                    int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    List<String> organSet = new ArrayList<>(Arrays.asList(
                            action.substring(action.indexOf("[") + 1, action.indexOf("]"))
                                    .split(",")));
                    UndoRedoCLIService.removeOrgansDonating(OrganEnum.stringListToOrganSet(organSet), profile);
                } else if (action.contains("set")) {
                    int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    UndoRedoCLIService.addOrgansDonating(OrganEnum.stringListToOrganSet(Arrays.asList(
                            action.substring(
                                    action.indexOf("[") + 1,
                                    action.indexOf("]")).split(",")
                    )), profile);
                } else if (action.contains("donate")) {
                    int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    UndoRedoCLIService.addOrgansDonated(
                            OrganEnum.stringListToOrganSet(
                                    Arrays.asList(
                                            action.substring(
                                                    action.indexOf("[") + 1,
                                                    action.indexOf("]")).split(",")
                                    )
                            )
                    , profile);
                } else if (action.contains("update")) {
                    int id = Integer.parseInt(
                            action.substring(0, action.indexOf("previous"))
                                    .replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    String newInfo = action.substring(action.indexOf("nhi"));
                    ProfileGeneralControllerTODOContainsOldProfileMethods.setExtraAttributes(
                            new ArrayList<>(Arrays.asList(newInfo.split(","))), profile);
                } else if (action.contains("EDITED")) {
                    int id = Integer.parseInt(action.substring(0, action.indexOf("PROCEDURE"))
                            .replaceAll("[\\D]", ""));
                    Profile profile = currentDatabase.getProfile(id);
                    int procedurePlace = Integer.parseInt(
                            action.substring(action.indexOf("PROCEDURE"), action.indexOf("EDITED"))
                                    .replaceAll("[\\D]", ""));
                    String previous = action
                            .substring(action.indexOf("CURRENT(") + 8, action.indexOf(") NEW"));
                    String[] previousValues = previous.split(",");
                    String organs;
                    ArrayList<OrganEnum> organList = new ArrayList<>();
                    organs = action.substring(action.indexOf("NEWORGANS["), action.indexOf("]END"));
                    List<String> list = new ArrayList<>(Arrays.asList(organs.split(",")));
                    for (String organ : list) {
                        System.out.println(organ);
                        organList.add(OrganEnum.valueOf(organ
                                .replace(" ", "")
                                .replace("NEWORGANS[", ""))
                        );
                    }
                    profile.getAllProcedures().get(procedurePlace).setSummary(previousValues[0]);
                    profile.getAllProcedures().get(procedurePlace)
                            .setDate(LocalDate.parse(previousValues[1]));
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

        CommonDAO accessObject = DAOFactory.getCommonDao();
        accessObject.queryDatabase(query);
    }

    public static int getPosition() {
        return historyPosition;
    }
}
