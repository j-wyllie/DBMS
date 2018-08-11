package odms.cli.commands;

import java.sql.SQLException;
import java.util.List;
import odms.cli.CommandUtils;
import odms.controller.database.DAOFactory;
import odms.controller.database.UserDAO;
import odms.controller.history.CurrentHistory;
import odms.history.History;
import odms.commons.model.enums.UserType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class User extends CommandUtils {


    /**
     * Add history for profile.
     * @param id profile id.
     */
    protected static void addClinicianHistory(Integer id) {
        History action = new History("user", id, "added", "", -1, LocalDateTime.now());
        CurrentHistory.updateHistory(action);
    }

    /**
     * Create clinician.
     * @param rawInput raw command input.
     * @return staffID the staff id of created clinician.
     */
    public static odms.commons.model.user.User createClinician(String rawInput) {
        try {
            String[] attrList = rawInput.substring(16).split("\"\\s");
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            UserType userType = UserType.CLINICIAN;
            odms.commons.model.user.User newUser = new odms.commons.model.user.User(userType, attrArray);

            addClinicianHistory(newUser.getStaffID());
            System.out.println("Clinician created.");
            return newUser;

        } catch (IllegalArgumentException e) {
            System.out.println("Please enter the required attributes correctly.");

        } catch (Exception e) {
            System.out.println("Please enter a valid command.");
        }
        return null;
    }

    /**
     * Delete user from the database by search.
     * @param expression search expression.
     * @param type of user.
     */
    public static void deleteUserBySearch(String expression, String type) {
        List<User> users = search(expression, type);
        deleteUsers(users);
    }

    /**
     * Delete users.
     * @param userList list of users.
     */
    private static void deleteUsers(List<User> userList) {
        UserDAO database = DAOFactory.getUserDao();
        if (userList.size() > 0) {
            for (User user : userList) {
                try {
                    database.remove(user);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                CurrentHistory.deletedUsers.add(user);
                CurrentHistory.updateHistory(new History("profile", user.getStaffID(),
                        "deleted", "", -1, LocalDateTime.now()));
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Update user attributes.
     * @param userList list of users.
     * @param attrList attributes to be updated and their values.
     */
    private static void updateUserAttr(List<User> userList, String[] attrList) {
        if (userList.size() > 0) {
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            for (User user : userList) {
                History action = new History("user", user.getStaffID(), "update",
                        user.getAttributesSummary(), -1, null);
                user.setExtraAttributes(attrArray);
                action.setHistoryData(action.getHistoryData() + user.getAttributesSummary());
                action.setHistoryTimestamp(LocalDateTime.now());
                CurrentHistory.updateHistory(action);
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Update user attributes by search.
     * @param expression search expression.
     * @param type of user.
     */
    public static void updateUserBySearch(String expression, String type) {
        List<User> users = search(expression, type);
        String[] attrList = expression.substring(expression.indexOf('>') + 1)
                .trim()
                .split("\"\\s");
        updateUserAttr(users, attrList);
    }

    /**
     * Displays users attributes via the search methods.
     * @param expression search expression being used for searching.
     * @param type of user.
     */
    public static void viewAttrBySearch(String expression, String type) {
        List<User> users = search(expression, type);
        Print.printUserSearchResults(users);
    }

    /**
     * View date and time of profile creation.
     * @param expression search expression.
     * @param type of user.
     */
    public static void viewDateTimeCreatedBySearch(String expression, String type) {
        List<User> users = search(expression, type);
        Print.printUserList(users);
    }

    /**
     * Gives a list of relevant users to be edited.
     * @param expression to search by.
     * @param type of user.
     * @return a list of users.
     */
    private static List<User> search(String expression, String type) {
        // Depending what type of user, the length to skip will change accordingly
        Integer lengthToSkip = 10;   //for clinician
        if (type.equals("clinician")) {
            lengthToSkip = 10;
        }

        UserDAO database = DAOFactory.getUserDao();
        List<User> users = new ArrayList<>();
        String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.lastIndexOf("\""));
        try {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                if (expression.substring(lengthToSkip, lengthToSkip + "name".length()).equals("name")) {
                    users = database.search(attr);
                } else if (expression.substring(lengthToSkip, lengthToSkip + "staffID".length())
                        .equals("staffID")) {
                    users = database.search(Integer.valueOf(attr));
                } else {
                    System.out.println(searchErrorText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
