package odms.controller.database.user;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import odms.commons.model.user.User;
import odms.controller.user.UserNotFoundException;

public interface UserDAO {

    /**
     * Gets all users from the database.
     */
    List<User> getAll() throws SQLException;

    /**
     * Gets a single user from the database by id.
     * @param userId of the user.
     * @return the specified user.
     * @throws UserNotFoundException error.
     */
    User get(int userId) throws UserNotFoundException, SQLException;

    /**
     * Gets a single user from the database by username.
     * @param username of the user.
     * @return the specified user.
     * @throws UserNotFoundException error.
     */
    User get(String username) throws UserNotFoundException, SQLException;

    /**
     * Adds a new user to the database.
     * @param user to add.
     */
    void add(User user) throws SQLException;

    /**
     * Checks if a username already exists in the database.
     * @param username to check.
     * @return true is the username does not already exist.
     */
    boolean isUniqueUsername(String username) throws SQLException;

    /**
     * Removes a user from the database.
     * @param user to remove.
     */
    void remove(User user) throws SQLException;

    /**
     * Updates a users information in the database.
     * @param user to update.
     */
    void update(User user) throws SQLException;

    /**
     * Searches the database for users based on their id.
     * @param name of the user.
     * @return a list of users with matching names.
     * @throws SQLException error.
     */
    List<User> search(String name) throws SQLException;

    /**
     * Searches the database for users based on their id.
     * @param id of the user.
     * @return the user with a matching id.
     * @throws SQLException error.
     */
    List<User> search(int id) throws SQLException;
}
