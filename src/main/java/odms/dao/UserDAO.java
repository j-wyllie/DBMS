package odms.dao;

import odms.user.User;

import java.util.ArrayList;

public interface UserDAO {

    /**
     * Gets all users from the database.
     */
    ArrayList<User> getAll();

    /**
     * Get a single user from the database by id.
     * @return a user.
     */
    User getUser(int userId);

    /**
     * Get a single user from the database by uername.
     * @return a user.
     */
    User getUser(String username);

    /**
     * Adds a new user to the database.
     * @param user to add.
     */
    void add(User user);

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
