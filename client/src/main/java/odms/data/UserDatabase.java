package odms.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import odms.controller.user.UserNotFoundException;
import odms.commons.model.user.User;
import odms.commons.model.enums.UserType;

public class UserDatabase {

    private Set<Integer> deletedUsers = new HashSet<>();
    private Map<Integer, User> userDb = new HashMap<>();

    private Integer lastID = -1;
    private String path;

    /**
     * Find clinician by ID
     *
     * @param id unique ID for requested User
     * @return User object
     */
    public User getUser(Integer id) throws UserNotFoundException {

        User user = userDb.get(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with id " + id, id);
        }
        return user;
    }

    /**
     * Determine unique ID for user and add the user to the database
     * find user by username
     *
     * @param username username requested
     * @return the user object.
     */
    public User getUser(String username) throws UserNotFoundException {
        for(User value : userDb.values()) {
            if (value.getUsername() != null && value.getUsername().equalsIgnoreCase(username)) {
                return value;
            }
        }
        throw new UserNotFoundException("User not found with username " + username, username);
    }

    /**
     * Checks whether a user exists in the database with a certain username
     * @param username Username to be searched for
     * @return Boolean based on whether the user exists or not.
     */
    public Boolean isUser(String username) {
        for(User value : userDb.values()) {
            if (value.getUsername() != null && value.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a user exists in the database with a certain user id
     * @param userId Id to be searched for
     * @return Boolean based on whether the user exists or not.
     */
    public Boolean isUser(Integer userId) {
        return userDb.containsKey(userId);
    }

    /**
     * Determine unique ID for the user and add the user to the database
     *
     * @param user new user object
     */
    public void addUser(User user){
        lastID += 1;
        user.setStaffID(lastID);

        userDb.put(lastID, user);
    }

    /**
     * Returns all the users in the current database
     * @return ArrayList of users
     */
    public Collection<User> getUsers() {
        return new ArrayList<>(userDb.values());
    }

    /**
     * Returns all the users in the current database
     * @return ArrayList of users
     */
    public ArrayList<User> getUsersAsArrayList() {
        ArrayList<User> users = new ArrayList<>();

        userDb.forEach((id, user) -> {
            users.add(user);
        });

        return users;

    }

    /**
     * Restore a previously deleted user
     *
     * @param id ODMS ID of deleted profile
     * @param user the profile to be restored
     * @return current ProfileDatabase lastId
     */
    public int restoreProfile(Integer id, User user) {
        try {
            lastID += 1;
            user.setStaffID(lastID);
            userDb.put(lastID, user);
            deletedUsers.remove(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Checks for a duplicate username in the user database.
     * @param username The requested username for the user
     * @return boolean for whether a username is unique
     */
    public boolean checkUniqueUsername(String username) {
        for (User user : userDb.values()) {
            if (user.getUsername() != null && user.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generate a list of clinicians
     * @return Array of clinicians
     */
    public Collection<User> getClinicians() {
        ArrayList<User> clinicians = new ArrayList<>();

        userDb.forEach((id, user) -> {

            if (user.getUserType() == UserType.CLINICIAN) {
                clinicians.add(user);
            }
        });
        return clinicians;
    }

    /**
     * Fuzzy search that finds the top 30 users that match the provided search string.
     * @param searchString the string that the user names will be searched against.
     * @return list of users that match the provided search string, with a max size of 30.
     */
    public Collection<User> searchUsers(String searchString) {
        ArrayList<String> users = new ArrayList<>();

        if (searchString == null || searchString.equals("")) {
            return getUsers();
        }

        for (User user : getUsers()) {
            users.add(user.getName());
        }

        // Fuzzywuzzy, fuzzy search algorithm. Returns list of names sorted by closest match to the searchString.
        List<ExtractedResult> result;
        result = FuzzySearch.extractSorted(searchString, users, 50);

        // Use index values from fuzzywuzzy search to build list of user object in same order returned from fuzzywuzzy.
        ArrayList<User> resultUsers = new ArrayList<>();
        for (ExtractedResult er : result) {
            resultUsers.add(getUsersAsArrayList().get(er.getIndex()));
        }
        return resultUsers;
    }

    /**
     * Remove user from the user database, adding their ID to the deletedID's set for
     * logging of removed users.
     *
     * @param staffID unique user ID
     */
    public boolean deleteUser(Integer staffID) {
        try {
            if (isUser(staffID)) {
                deletedUsers.add(staffID);
                userDb.remove(staffID);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Search for users via their given names
     *
     * @param searchTerm string of the names
     * @return Array of users found that match
     */
    public ArrayList<User> searchNames(String searchTerm) {
        ArrayList<User> results = new ArrayList<>();

        userDb.forEach((id, user) -> {
            if (user.getName().equalsIgnoreCase(searchTerm)) {
                results.add(user);
            }
        });

        return results;
    }

    /**
     * Search for users via their staff ID number
     *
     * @param searchTerm integer of the staff ID number
     * @return Array of users found that match staff ID number
     */
    public ArrayList<User> searchStaffID(Integer searchTerm) {
        ArrayList<User> results = new ArrayList<>();

        userDb.forEach((id, user) -> {
            if (user.getStaffID() == (searchTerm)) {
                results.add(user);
            }
        });

        return results;
    }

}
