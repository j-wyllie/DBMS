package odms.cli.commands;

import odms.cli.CommandUtils;
import odms.data.UserDatabase;
import odms.user.UserType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class User extends CommandUtils {

    /**
     * Add history for clinician
     *
     * @param Id clinician ID
     */
    protected static void addClinicianHistory(int Id) {
        if (currentSessionHistory.size() != 0) {
            if (historyPosition != currentSessionHistory.size() - 1) {
                currentSessionHistory.subList(historyPosition, currentSessionHistory.size() - 1)
                        .clear();
            }
        }
        currentSessionHistory.add("Clinician " + Id + " added at " + LocalDateTime.now());
        historyPosition = currentSessionHistory.size() - 1;
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
    public static void deleteUserBySearch(UserDatabase currentDatabase, String expression) {
        //todo what other user types will use this function? the length to skip will need to change accordingly
        int lengthToSkip = 10;   //for clinician
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
                    deletedUsers.add(user);
                    if (currentSessionHistory.size() != 0) {
                        if (historyPosition != currentSessionHistory.size() - 1) {
                            currentSessionHistory
                                    .subList(historyPosition, currentSessionHistory.size() - 1).clear();
                        }
                    }
                    currentSessionHistory
                            .add("User " + user.getStaffID() + " deleted at " + LocalDateTime.now());
                    historyPosition = currentSessionHistory.size() - 1;
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
            String action;
            for (odms.user.User user : userList) {
                action = "User " + user.getStaffID() + " updated details previous = " + user
                        .getAttributesSummary() + " new = ";
                user.setExtraAttributes(attrArray);
                action = action + user.getAttributesSummary() + " at " + LocalDateTime.now();
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
     * Update user attributes by search.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void updateUserBySearch(UserDatabase currentDatabase, String expression) {
        //todo what other user types will use this function? the length to skip will need to change accordingly
        int lengthToSkip = 10;   //for clinician
        ArrayList<odms.user.User> userList;

        String[] attrList = expression.substring(expression.indexOf('>') + 1)
                .trim()
                .split("\"\\s");

        System.out.println(attrList);

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
    public static void viewAttrBySearch(UserDatabase currentDatabase, String expression) {
        //todo what other user types will use this function? the length to skip will need to change accordingly
        int lengthToSkip = 10;   //for clinician

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
    public static void viewDateTimeCreatedBySearch(UserDatabase currentDatabase, String expression) {
        //todo what other user types will use this function? the length to skip will need to change accordingly
        int lengthToSkip = 10;   //for clinician
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
                userList = currentDatabase
                        .searchStaffID(Integer.valueOf(attr));

                Print.printUserList(userList);
            } else {
                System.out.println(searchErrorText);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }


}
