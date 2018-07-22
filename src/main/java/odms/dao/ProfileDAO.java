package odms.dao;

import java.util.List;
import odms.profile.Profile;

public interface ProfileDAO {

    /**
     * Gets all profiles from the database.
     */
    List<Profile> getAll();

    /**
     * Get a single profile from the database.
     * @return a profile.
     */
    Profile getProfile(int profileId);

    /**
     * Adds a new profile to the database.
     * @param profile to add.
     */
    void add(Profile profile);

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
