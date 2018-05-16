package odms.data;

import java.util.*;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import odms.user.User;
import odms.user.UserType;

public class UserDatabase {

    private HashSet<Integer> deletedUsers = new HashSet<>();
    private HashMap<Integer, User> userDb = new HashMap<>();

    private Integer lastID = -1;
    private String path;

    /**
     * Find clinician by ID
     *
     * @param id unique ID for requested clinician
     * @return User object
     */
    public User getUser(Integer id){
        return userDb.get(id);
    }



    /**
     * Determine unique ID for user and add the user the the database
     *
     * @param user new user object
     */
    public void addUser(User user){
        lastID += 1;
        user.setStaffID(lastID);

        userDb.put(lastID, user);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }



    /**
     * Generate a list of clinicians
     * @return Array of clinicians
     */
    public ArrayList<User> getClinicians() {
        ArrayList<User> clinicians = new ArrayList<>();

        userDb.forEach((id, user) -> {

                if (user.getUserType() == UserType.CLINICIAN) {
                    clinicians.add(user);
                }

        });

        //profiles.sort(Comparator.comparing(Profile::getLastNames));   todo

        return clinicians;
    }


    /**
     * Generate a list of users
     * @return Array of users
     */
    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();

        userDb.forEach((id, user) -> {
            users.add(user);

        });

        //profiles.sort(Comparator.comparing(Profile::getLastNames));        todo

        return users;
    }



    /**
     * Fuzzy search that finds the top 30 users that match the provided search string.
     * @param searchString the string that the user names will be searched against.
     * @return list of users that match the provided search string, with a max size of 30.
     */
    public ArrayList<User> searchUsers(String searchString) {
        ArrayList<String> users = new ArrayList<>();

        if (searchString == null || searchString.equals("")) {
            return getUsers();
        }

        for (User user : getUsers()) {
            users.add(user.getName());
        }

        //Fuzzywuzzy, fuzzy search algorithm. Returns list of names sorted by closest match to the searchString.
        List<ExtractedResult> result;
        result = FuzzySearch.extractSorted(searchString, users, 50);

        //Use index values from fuzzywuzzy search to build list of user object in same order returned from fuzzywuzzy.
        ArrayList<User> resultUsers = new ArrayList<>();
        for (ExtractedResult er : result) {
            resultUsers.add(getUsers().get(er.getIndex()));
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
            // Should deleted users simply be disabled for safety reasons?
            deletedUsers.add(staffID);
            userDb.remove(staffID);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
            if (user.getName().toLowerCase().equals(searchTerm.toLowerCase())) {
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
            if (user.getStaffID().equals(searchTerm)) {
                results.add(user);
            }
        });

        return results;
    }




}
