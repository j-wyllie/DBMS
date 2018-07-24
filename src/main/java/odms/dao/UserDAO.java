package odms.dao;

import odms.controller.UserNotFoundException;
import odms.user.User;

import java.util.ArrayList;

public interface UserDAO {

    /**
     * Gets all users from the database.
     */
    ArrayList<User> getAll();

    /**
     * Gets a single user from the database by id.
     * @param userId of the user.
     * @return the specified user.
     * @throws UserNotFoundException error.
     */
    User get(int userId) throws UserNotFoundException;

    /**
     * Gets a single user from the database by username.
     * @param username of the user.
     * @return the specified user.
     * @throws UserNotFoundException error.
     */
    User get(String username) throws UserNotFoundException;

    /**
     * Adds a new user to the database.
     * @param user to add.
     */
    void add(User user);

    /**
     * Checks if a username already exists in the database.
     * @param username to check.
     * @return true is the username does not already exist.
     */
    boolean isUniqueUsername(String username);

    /**
     * Removes a user from the database.
     * @param user to remove.
     */
    void remove(User user);

    /**
     * Updates a users information in the database.
     * @param user to update.
     */
    void update(User user);

}
