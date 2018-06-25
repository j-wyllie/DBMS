package odms.dao;

import odms.cli.commands.User;

public interface UserDAO {

    /**
     * Removes a user from the database.
     * @param user to remove.
     */
    public void addUser(User user);

}
