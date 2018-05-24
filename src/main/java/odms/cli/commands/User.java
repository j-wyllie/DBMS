package odms.cli.commands;

import odms.cli.CommandUtils;
import odms.controller.HistoryController;
import odms.data.UserDatabase;
import odms.history.History;
import odms.user.UserType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class User extends CommandUtils {


    /**
     * Add history for profile
     *
     * @param Id profile ID
     */
    protected static void addClinicianHistory(int Id) {
        History action = new History("User",Id,"added","",-1,LocalDateTime.now());
        HistoryController.updateHistory(action);
    }

    /**
     * Create clinician.
     *
     * @param currentDatabase Database reference
     * @param rawInput raw command input
     * @return staffID the staff id of created clinician
     */
    public static odms.user.User createClinician(UserDatabase currentDatabase, String rawInput) {

        try {
            String[] attrList = rawInput.substring(16).split("\"\\s");
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            UserType userType = UserType.CLINICIAN;
            odms.user.User newUser = new odms.user.User(userType, attrArray);
            currentDatabase.addUser(newUser);
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
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void deleteUserBySearch(UserDatabase currentDatabase, String expression, String type) {
        // Depending what type of user, the length to skip will change accordingly
        Integer lengthToSkip = 10;   //for clinician
        if (type.equals("clinician")) {
            lengthToSkip = 10;
        }
        ArrayList<odms.user.User> userList;

        if (expression.lastIndexOf("=") == expression.indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                    expression.lastIndexOf("\""));

            if (expression.substring(lengthToSkip, lengthToSkip + "name".length()).equals("name")) {
               userList = currentDatabase.searchNames(attr);

                deleteUsers(userList, currentDatabase);
            } else if (expression.substring(lengthToSkip, lengthToSkip + "staffID".length()).equals("staffID")) {
                userList = currentDatabase
                        .searchStaffID(Integer.valueOf(attr));

                deleteUsers(userList, currentDatabase);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * Delete users.
     *
     * @param userList list of users
     * @param currentDatabase Database reference
     */
    private static void deleteUsers(ArrayList<odms.user.User> userList,
                                    UserDatabase currentDatabase) {
        boolean result;
        if (userList.size() > 0) {
            for (odms.user.User user : userList) {
                result = currentDatabase.deleteUser(user.getStaffID());
                if (result) {
                    HistoryController.deletedUsers.add(user);
                    HistoryController.updateHistory(new History("Profile",user.getStaffID(),
                            "deleted","",-1,LocalDateTime.now()));
                }
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Update user attributes.
     *
     * @param userList List of users
     * @param attrList Attributes to be updated and their values
     */
    private static void updateUserAttr(ArrayList<odms.user.User> userList, String[] attrList) {
        if (userList.size() > 0) {
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            for (odms.user.User user : userList) {
                History action = new History("User" , user.getStaffID() ,"update",user.getAttributesSummary(),-1,null);
                user.setExtraAttributes(attrArray);
                action.setHistoryData(action.getHistoryData()+user.getAttributesSummary());
                action.setHistoryTimestamp(LocalDateTime.now());
                HistoryController.updateHistory(action);
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Update user attributes by search.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void updateUserBySearch(UserDatabase currentDatabase, String expression, String type) {
        // Depending what type of user, the length to skip will change accordingly
        Integer lengthToSkip = 10;   //for clinician
        if (type.equals("clinician")) {
            lengthToSkip = 10;
        }
        ArrayList<odms.user.User> userList;

        String[] attrList = expression.substring(expression.indexOf('>') + 1)
                .trim()
                .split("\"\\s");


        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") ==
                expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                    expression.indexOf(">") - 2);

            if (expression.substring(lengthToSkip, lengthToSkip + "name".length()).equals("name")) {
                userList = currentDatabase.searchNames(attr);

                updateUserAttr(userList, attrList);
            }  else if (expression.substring(lengthToSkip, lengthToSkip + "staffID".length()).equals("staffID")) {
                userList = currentDatabase
                        .searchStaffID(Integer.valueOf(attr));

                updateUserAttr(userList, attrList);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * Displays users attributes via the search methods
     *
     * @param currentDatabase Database reference
     * @param expression Search expression being used for searching
     */
    public static void viewAttrBySearch(UserDatabase currentDatabase, String expression, String type) {
        // Depending what type of user, the length to skip will change accordingly
        Integer lengthToSkip = 10;   //for clinician
        if (type.equals("clinician")) {
            lengthToSkip = 10;
        }

        if (expression.lastIndexOf("=") == expression.indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                    expression.lastIndexOf("\""));

            if (expression.substring(lengthToSkip, lengthToSkip + "name".length()).equals("name")) {
                Print.printUserSearchResults(currentDatabase.searchNames(attr));
            } else if (expression.substring(lengthToSkip, lengthToSkip + "staffID".length()).equals("staffID")) {
                Print.printUserSearchResults(currentDatabase.searchStaffID(Integer.valueOf(attr)));
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
    public static void viewDateTimeCreatedBySearch(UserDatabase currentDatabase, String expression, String type) {
        // Depending what type of user, the length to skip will change accordingly
        Integer lengthToSkip = 10;   //for clinician
        if (type.equals("clinician")) {
            lengthToSkip = 10;
        }
        ArrayList<odms.user.User> userList;

        String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.lastIndexOf("\""));

        if (expression.substring(lengthToSkip, lengthToSkip + "name".length()).equals("name")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                userList = currentDatabase.searchNames(attr);

                Print.printUserList(userList);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(lengthToSkip, lengthToSkip + "staffID".length()).equals("staffID")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                userList = currentDatabase.searchStaffID(Integer.valueOf(attr));

                Print.printUserList(userList);
            } else {
                System.out.println(searchErrorText);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }
}
