package odms.dao;

import odms.user.User;

public interface UserDAO {

    /**
     * Gets all users from the database.
     */
    void getAllUsers();

    /**
     * Adds a new user to the database.
     * @param user to add.
     */
    void addUser(User user);

    /**
     * Removes a user from the database.
     * @param user to remove.
     */
    void removeUser(User user);

    /**
     * Updates a users information in the database.
     * @param user to update.
     */
    void updateUser(User user);

}
