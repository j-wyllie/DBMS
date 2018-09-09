package odms.cli.commands;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import odms.cli.CommandUtils;
import odms.commons.model.history.History;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;
import odms.controller.history.CurrentHistory;
import odms.controller.profile.ProfileGeneralControllerTODOContainsOldProfileMethods;
import odms.data.NHIConflictException;


public class Profile extends CommandUtils {

    /**
     * Add history for profile.
     *
     * @param id profile ID.
     */
    protected static void addProfileHistory(Integer id) {
        History action = new History("profile", id, "added", "", -1, LocalDateTime.now());
        CurrentHistory.updateHistory(action);
    }

    /**
     * Create profile.
     *
     * @param rawInput raw command input.
     */
    public static void createProfile(String rawInput) throws SQLException {
        ProfileDAO database = DAOFactory.getProfileDao();

        try {
            String[] attrList = rawInput.substring(15).split("\"\\s");
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            odms.commons.model.profile.Profile newProfile = new odms.commons.model.profile.Profile(
                    attrArray);
            database.add(newProfile);
            System.out.println("profile created.");
        } catch (IllegalArgumentException e) {
            System.out.println("Please enter the required attributes correctly.");

        } catch (NHIConflictException e) {
            String errorNhiNumber = e.getNHI();

            System.out.println("Error: NHI " + errorNhiNumber +
                    " already in use by a profile.");

        } catch (Exception e) {
            System.out.println("Please enter a valid command.");
        }
    }

    /**
     * Delete profiles from the database.
     *
     * @param expression search expression.
     */
    public static void deleteProfileBySearch(String expression) {
        List<odms.commons.model.profile.Profile> profiles = search(expression);
        try {
            deleteProfiles(profiles);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a user from the database by their NHI.
     */
    public static void deleteProfileByNHI(String NHI) {
        ProfileDAO database = DAOFactory.getProfileDao();
        try {
            // We can do this because the username == NHI.
            odms.commons.model.profile.Profile profile = get(NHI);
            database.remove(profile);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Delete profiles.
     *
     * @param profileList list of profiles.
     */
    private static void deleteProfiles(List<odms.commons.model.profile.Profile> profileList)
            throws SQLException {
        ProfileDAO database = DAOFactory.getProfileDao();
        boolean result;
        if (profileList.size() > 0) {
            for (odms.commons.model.profile.Profile profile : profileList) {
                database.remove(profile);
                CurrentHistory.deletedProfiles.add(profile);
                CurrentHistory.updateHistory(
                        new odms.commons.model.history.History("profile", profile.getId(),
                                "deleted", "", -1, LocalDateTime.now()));
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Update profile attributes.
     *
     * @param profile list of profiles
     * @param attrList attributes to be updated and their values
     */
    private static void updateProfileAttr(odms.commons.model.profile.Profile profile,
            String[] attrList) {
        if (profile != null) {
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            odms.commons.model.history.History action = new odms.commons.model.history.History(
                    "profile", profile.getId(), "update",
                    profile.getAttributesSummary(), -1, null);
            ProfileGeneralControllerTODOContainsOldProfileMethods
                    .setExtraAttributes(attrArray, profile);
            action.setHistoryData(action.getHistoryData() + profile.getAttributesSummary());
            action.setHistoryTimestamp(LocalDateTime.now());
            //CurrentHistory.updateHistory(action);

        } else {
            System.out.println(searchNotFoundText);
        }

    }

    /**
     * Update profile attributes.
     *
     * @param expression search expression.
     */
    public static void updateProfilesBySearch(String expression) {
        String[] attrList = expression.substring(expression.indexOf('>') + 1)
                .trim()
                .split("\"\\s");
        String searchTerm = expression.substring(0, expression.indexOf('>'));
        odms.commons.model.profile.Profile profiles = get(searchTerm);
        updateProfileAttr(profiles, attrList);

    }

    /**
     * Displays profiles attributes via the search methods.
     *
     * @param expression search expression being used for searching.
     */
    public static void viewAttrBySearch(String expression) {
        List<odms.commons.model.profile.Profile> profiles = search(expression);
        Print.printProfileSearchResults(profiles);
    }

    /**
     * view date and time of profile creation.
     *
     * @param expression search expression.
     */
    public static void viewDateTimeCreatedBySearch(String expression) {
        List<odms.commons.model.profile.Profile> profiles = search(expression);
        Print.printProfileList(profiles);
    }

    /**
     * Returns a list of the relevant profiles that are to be edited.
     *
     * @param expression to search by.
     * @return a list of profile objects.
     */
    private static List<odms.commons.model.profile.Profile> search(String expression) {
        ProfileDAO database = DAOFactory.getProfileDao();
        List<odms.commons.model.profile.Profile> profiles = new ArrayList<>();
        if (expression.lastIndexOf("=") == expression.indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                    expression.lastIndexOf("\""));
            if (expression.substring(8, 8 + "given-names".length()).equals("given-names") ||
                    expression.substring(8, 8 + "last-names".length()).equals("last-names") ||
                    expression.substring(8, 8 + "nhi".length()).equals("nhi")) {
                try {
                    profiles = database.search(attr, 0,
                            0, null, null, null, null);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println(searchErrorText);
        }
        return profiles;
    }

    /**
     * Returns a list of the relevant profiles that are to be edited.
     *
     * @param expression to search by.
     * @return a list of profile objects.
     */
    private static odms.commons.model.profile.Profile get(String expression) {
        ProfileDAO database = DAOFactory.getProfileDao();
        if (expression.lastIndexOf("=") == expression.indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                    expression.lastIndexOf("\""));
            if (expression.substring(8, 8 + "given-names".length()).equals("given-names") ||
                    expression.substring(8, 8 + "last-names".length()).equals("last-names") ||
                    expression.substring(8, 8 + "nhi".length()).equals("nhi")) {
                try {
                    odms.commons.model.profile.Profile profiles = database.get(attr);
                    return profiles;

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println(searchErrorText);
        }
        return null;
    }

    /**
     * view organs available for donation.
     *
     * @param expression search expression.
     */
    public static void viewDonationsBySearch(String expression) {
        List<odms.commons.model.profile.Profile> profiles = search(expression);

        if (profiles != null && !profiles.isEmpty()) {
            Print.printProfileDonations(profiles);
        } else {
            System.out.println("No matching profiles found.");
        }
    }
}
