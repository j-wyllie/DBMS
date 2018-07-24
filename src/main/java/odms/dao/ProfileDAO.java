package odms.dao;

import java.util.List;
import odms.profile.Profile;

public interface ProfileDAO {

    /**
     * Gets all profiles from the database.
     */
    List<Profile> getAll();

    /**
     * Get a single profile from the database by id.
     * @param profileId of the profile.
     * @return a profile.
     */
    Profile get(int profileId);

    /**
     * Get a single profile from the database by username.
     * @param username of the profile.
     * @return a profile.
     */
    Profile get(String username);

    /**
     * Adds a new profile to the database.
     * @param profile to add.
     */
    void add(Profile profile);

    /**
     * Checks if a username already exists in the database.
     * @param username to check.
     * @return true is the username does not already exist.
     */
    boolean isUniqueUsername(String username);

    /**
     * Removes a profile from the database.
     * @param profile to remove.
     */
    void remove(Profile profile);

    /**
     * Updates a profiles information in the database.
     * @param profile to update.
     */
    void update(Profile profile);
}
