package odms.dao;

import odms.user.User;

public interface UserDAO {

    /**
     * Gets all users from the database.
     */
    void getAll();

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
