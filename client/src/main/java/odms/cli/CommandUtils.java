package odms.cli;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.controller.database.DAOFactory;
import odms.controller.database.common.CommonDAO;
import odms.controller.database.profile.ProfileDAO;
import odms.controller.profile.OrganCLIService;

@Slf4j
public class CommandUtils {

    protected CommandUtils() {
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

    private static final String ERR_ORGAN_EXISTS = "This organ already exists.";

    protected static String searchErrorText = "Please enter only one search criteria\n "
            + "Profiles: given-names, last-names, nhi\n"
            + "Users: name, staffID";

    protected static String searchNotFoundText = "There are no profiles that match this criteria.";

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
                } else if (cmd.get(2).equalsIgnoreCase("users")) {
                    return Commands.PRINTALLUSERS;
                }else if (cmd.get(2).equalsIgnoreCase("donors")) {
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
                        default:
                            // noop
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
                        case "remove-receive-organ":
                            return Commands.RECEIVEREMOVE;
                        case "donate-organ":
                            return Commands.ORGANDONATE;
                        default:
                            // noop
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
                        default:
                            // noop
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
                expression.lastIndexOf('=') + 1).replace("\"", "").replace(" ", "").split(",");
        List<Profile> profiles = new ArrayList<>();
        try {
            profiles = search(expression);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        addOrgans(profiles, organList);
    }

    /**
     * Add organs to a profile's received organs.
     * @param expression search expression.
     */
    static void addReceiverOrgansBySearch(String expression) {
        String[] organList = expression.substring(
                expression.lastIndexOf('=') + 1).replace("\"", "").replace(" ", "").split(",");
        List<Profile> profiles = new ArrayList<>();
        try {
            profiles = search(expression);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        addReceiverOrgans(profiles, organList);
    }

    /**
     * Remove organs available for donation to a profile profile.
     * @param expression search expression.
     */
    static void removeOrgansBySearch(String expression) {
        String[] organList = expression.substring(expression.lastIndexOf('=') + 1)
                .replace("\"", "")
                .split(",");
        List<Profile> profiles = new ArrayList<>();
        try {
            profiles = search(expression);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        removeOrgansDonating(profiles, organList);
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
            log.error(e.getMessage(), e);
        }
        String[] organList = expression.substring(expression.lastIndexOf('=') + 1)
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
            log.error(e.getMessage(), e);
        }
        String[] organList = expression.substring(expression.lastIndexOf('=') + 1)
                .replace("\"", "").split(",");

        if (!profiles.isEmpty()) {
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
    private static List<Profile> search(String expression) throws SQLException {
        ProfileDAO database = DAOFactory.getProfileDao();
        List<Profile> profiles = new ArrayList<>();

        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf('=') ==
                expression.substring(0, expression.lastIndexOf('>')).indexOf('=')) {
            String attr = expression.substring(expression.indexOf('\"') + 1,
                    expression.indexOf('>') - 2);

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

    /**
     * Add organ donations.
     * @param profileList list of profiles.
     * @param organList list of organs to be added.
     */
    private static void addOrgans(List<Profile> profileList, String[] organList) {
        if (!profileList.isEmpty()) {
            Set<OrganEnum> organSet = OrganEnum.stringListToOrganSet(Arrays.asList(organList));

            for (Profile profile : profileList) {
                try {
                    OrganCLIService.addOrgansDonating(organSet, profile);
                } catch (IllegalArgumentException e) {
                    System.out.println(ERR_ORGAN_EXISTS);
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
                    OrganCLIService.addOrgansRequired(organSet, profile);
                } catch (IllegalArgumentException e) {
                    System.out.println(ERR_ORGAN_EXISTS);
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
     * Add donations to profile.
     *
     * @param profileList the list of profiles.
     * @param organList the list of organs to add.
     */
    private static void addDonation(List<Profile> profileList, String[] organList) {
        if (!profileList.isEmpty()) {
            for (Profile profile : profileList) {
                try {
                    OrganCLIService.addOrgansDonated(
                            OrganEnum.stringListToOrganSet(Arrays.asList(organList)), profile);
                    profile.setDonor(true);
                } catch (IllegalArgumentException e) {
                    System.out.println(ERR_ORGAN_EXISTS);
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
        if (!profileList.isEmpty()) {
            for (Profile profile : profileList) {
                try {
                    OrganCLIService.removeOrgansDonating(
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
        if (!profileList.isEmpty()) {
            for (Profile profile : profileList) {
                try {
                    OrganCLIService.removeOrgansRequired(
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
     * Supplys the read-only query to the database connection DAO.
     * @param input query to be executed.
     */
    static void executeDatabaseRead(String input) {
        String query = input.substring(input.indexOf(' '));

        CommonDAO accessObject = DAOFactory.getCommonDao();
        accessObject.queryDatabase(query);
    }
}
