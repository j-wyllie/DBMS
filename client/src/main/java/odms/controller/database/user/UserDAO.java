package odms.controller.database.user;

import odms.commons.model.user.User;
import odms.controller.user.UserNotFoundException;

import java.sql.SQLException;
import java.util.List;

/**
 * Defines all the methods for the UserDAO.
 */
public interface UserDAO {

    /**
     * Gets all users from the database.
     * @return list of all the users.
     * @throws SQLException thrown when there is an error querying the db.
     */
    List<User> getAll() throws SQLException;

    /**
     * Gets a single user from the database by id.
     * @param userId of the user.
     * @return the specified user.
     * @throws UserNotFoundException error.
     * @throws SQLException thrown when there is an error querying the db.
     */
    User get(int userId) throws UserNotFoundException, SQLException;

    /**
     * Gets a single user from the database by username.
     * @param username of the user.
     * @return the specified user.
     * @throws UserNotFoundException error.
     * @throws SQLException thrown when there is an error querying the db.
     */
    User get(String username) throws UserNotFoundException, SQLException;

    /**
     * Adds a new user to the database.
     * @param user to add.
     * @throws SQLException thrown when there is an error querying the db.
     */
    void add(User user) throws SQLException;

    /**
     * Removes a user from the database.
     * @param user to remove.
     * @throws SQLException thrown when there is an error querying the db.
     */
    void remove(User user) throws SQLException;

    /**
     * Updates a users information in the database.
     * @param user to update.
     * @throws SQLException thrown when there is an error querying the db.
     * @throws IllegalArgumentException thrown when an argument is incorrect.
     */
    void update(User user) throws SQLException, IllegalArgumentException;

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

    /**
     * Checks the credentials of a user.
     * @param username username entered.
     * @param password password entered.
     * @return boolean based on success or failure.
     */
    Boolean checkCredentials(String username, String password);
}
