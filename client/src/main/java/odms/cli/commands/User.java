package odms.cli.commands;

import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import odms.cli.CommandUtils;
import odms.commons.model.history.History;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;
import odms.controller.database.user.UserDAO;
import odms.controller.history.CurrentHistory;
import odms.commons.model.enums.UserType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import odms.controller.user.UserNotFoundException;

@Slf4j
public class User extends CommandUtils {

    /**
     * Add history for profile.
     *
     * @param id profile id.
     */
    protected static void addClinicianHistory(Integer id) {
        History action = new History("user", id, "added", "", -1, LocalDateTime.now());
        CurrentHistory.updateHistory(action);
    }

    /**
     * Create clinician.
     *
     * @param rawInput raw command input.
     */
    public static void createClinician(String rawInput) {
        try {
            String[] attrList = rawInput.substring(16).split("\"\\s");
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            UserType userType = UserType.CLINICIAN;
            odms.commons.model.user.User newUser = new odms.commons.model.user.User(userType,
                    attrArray);
            if ((newUser.getUsername() != null && !newUser.getUsername().equals("")) &&
                    (newUser.getPassword() != null && !newUser.getPassword().equals("")) &&
                    (newUser.getName() != null && !newUser.getName().equals(""))) {
                DAOFactory.getUserDao().add(newUser);
                System.out.println("Clinician created.");
            } else {
                throw new IllegalArgumentException("Please enter all fields.");
            }
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);

        } catch (Exception e) {
            System.out.println("Please enter a valid command.");
        }
    }

    /**
     * Create admin.
     *
     * @param rawInput raw command input.
     */
    public static void createAdmin(String rawInput) {
        try {
            String[] attrList = rawInput.substring(12).split("\"\\s");
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            UserType userType = UserType.ADMIN;
            odms.commons.model.user.User newUser = new odms.commons.model.user.User(userType,
                    attrArray);
            if ((newUser.getUsername() != null && !newUser.getUsername().equals("")) &&
                    (newUser.getPassword() != null && !newUser.getPassword().equals("")) &&
                    (newUser.getName() != null && !newUser.getName().equals(""))) {
                DAOFactory.getUserDao().add(newUser);
                System.out.println("Admin created.");
            } else {
                throw new IllegalArgumentException("Please enter all fields.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());

        } catch (Exception e) {
            System.out.println("Please enter a valid command.");
        }
    }

    /**
     * Delete user from the database by search.
     *
     * @param expression search expression.
     * @param type of user.
     */
    public static void deleteUserBySearch(String expression, String type) {
        odms.commons.model.user.User users = get(expression, type);
        deleteUsers(users);
    }



    /**
     * Delete users.
     *
     * @param user list of users.
     */
    private static void deleteUsers(odms.commons.model.user.User user) {
        UserDAO database = DAOFactory.getUserDao();
        if (user != null) {
            try {
                database.remove(user);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Update user attributes.
     *
     * @param user list of users.
     * @param attrList attributes to be updated and their values.
     */
    private static void updateUserAttr(odms.commons.model.user.User user, String[] attrList) {
        if (user != null) {
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            History action = new History("user", user.getStaffID(), "update",
                    user.getAttributesSummary(), -1, null);
            user.setExtraAttributes(attrArray, user);
            action.setHistoryData(action.getHistoryData() + user.getAttributesSummary());
            action.setHistoryTimestamp(LocalDateTime.now());
            CurrentHistory.updateHistory(action);
            try {
                DAOFactory.getUserDao().update(user);
                System.out.println("Clinician(s) successfully updated.");
            } catch (SQLException e) {
                System.out.println("Could not update attributes.");
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Update user attributes by search.
     *
     * @param expression search expression.
     * @param type of user.
     */
    public static void updateUserBySearch(String expression, String type) {
        String searchTerm = expression.substring(0, expression.indexOf('>'));

        odms.commons.model.user.User users = get(searchTerm, type);

        String[] attrList = expression.substring(expression.indexOf('>') + 1)
                .trim()
                .split("\"\\s");
        updateUserAttr(users, attrList);
    }

    /**
     * Displays users attributes via the search methods.
     *
     * @param expression search expression being used for searching.
     * @param type of user.
     */
    public static void viewAttrBySearch(String expression, String type) {
        List<odms.commons.model.user.User> users = search(expression, type);
        Print.printUserSearchResults(users);
    }

    /**
     * View date and time of profile creation.
     *
     * @param expression search expression.
     * @param type of user.
     */
    public static void viewDateTimeCreatedBySearch(String expression, String type) {
        List<odms.commons.model.user.User> users = search(expression, type);
        Print.printUserList(users);
    }

    /**
     * Gives a list of relevant users to be edited.
     *
     * @param expression to search by.
     * @param type of user.
     * @return a list of users.
     */
    private static List<odms.commons.model.user.User> search(String expression, String type) {
        // Depending what type of user, the length to skip will change accordingly
        int lengthToSkip = 10;   //for clinician
        if (type.equals("clinician")) {
            lengthToSkip = 10;
        }

        UserDAO database = DAOFactory.getUserDao();
        List<odms.commons.model.user.User> user = new ArrayList<>();
        String attr = expression.substring(expression.indexOf('\"') + 1,
                expression.lastIndexOf('\"'));
        try {
            if (expression.lastIndexOf('=') == expression.indexOf('=')) {
                if (expression.substring(lengthToSkip, lengthToSkip + "name".length())
                        .equals("name")) {
                    user = database.search(attr);
                } else if (expression.substring(lengthToSkip, lengthToSkip + "staffID".length())
                        .equals("staffID")) {
                    user = database.search(Integer.valueOf(attr));
                } else {
                    System.out.println(searchErrorText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return user;
    }

    /**
     * Gives a list of relevant users to be edited.
     *
     * @param expression to search by.
     * @param type of user.
     * @return a list of users.
     */
    private static odms.commons.model.user.User get(String expression, String type) {
        // Depending what type of user, the length to skip will change accordingly
        Integer lengthToSkip = 10;   //for clinician
        if (type.equals("clinician")) {
            lengthToSkip = 10;
        }
        if (type.equals(UserType.ADMIN.getName())) {
            lengthToSkip = "admin".length() + 1;
        }

        UserDAO database = DAOFactory.getUserDao();
        odms.commons.model.user.User user;
        String attr = expression.substring(expression.indexOf('\"') + 1,
                expression.lastIndexOf('\"'));
        try {
            if (expression.lastIndexOf('=') == expression.indexOf('=')) {
                if (expression.substring(lengthToSkip, lengthToSkip + "username".length())
                        .equals("username")) {
                    user = database.get(attr);
                    return user;
                } else {
                    System.out.println(searchErrorText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } catch (UserNotFoundException e) {
            System.out.println("user not found");
        }
        return null;
    }
}
