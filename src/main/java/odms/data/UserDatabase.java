package odms.data;

import odms.controller.UserNotFoundException;
import odms.user.User;

import java.util.HashMap;

public class UserDatabase {

    private HashMap<Integer, User> userDb = new HashMap<>();
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

    // TODO REMOVE THE EXCEPTION() AND MAKE IT PROPER
    /**
     * find user by username
     *
     * @param username username requested
     * @return the user object.
     */
    public User getUser(String username) throws UserNotFoundException {
        for(User value : userDb.values()) {
            if (value.getUsername() != null) {
                if (value.getUsername().equals(username)) {
                    return value;
                }
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
            if (value.getUsername() != null) {
                if (value.getUsername().equals(username)) {
                    return true;
                }
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
        user.setStaffId(lastID);

        userDb.put(lastID, user);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
