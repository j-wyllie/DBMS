package odms.controller.database.profile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;


public interface ProfileDAO {

    /**
     * Gets all profiles from the database.
     */
    List<Profile> getAll() throws SQLException;

    /**
     * Get a single profile from the database by id.
     * @param profileId of the profile.
     * @return a profile.
     */
    Profile get(int profileId) throws SQLException;

    /**
     * Get a single profile from the database by username.
     * @param username of the profile.
     * @return a profile.
     */
    Profile get(String username) throws SQLException;

    /**
     * Adds a new profile to the database.
     * @param profile to add.
     */
    void add(Profile profile) throws SQLException;

    /**
     * Checks if a username already exists in the database.
     * @param username to check.
     * @return true is the username does not already exist.
     */
    boolean isUniqueUsername(String username) throws SQLException;

    int isUniqueNHI(String nhi) throws SQLException;

    /**
     * Removes a profile from the database.
     * @param profile to remove.
     */
    void remove(Profile profile) throws SQLException;

    /**
     * Updates a profiles information in the database.
     * @param profile to update.
     */
    void update(Profile profile) throws SQLException;

    /**
     * Searches for a sublist of profiles based on criteria.
     * @param searchString filter based on search field.
     * @param ageSearchInt filter based on age.
     * @param ageRangeSearchInt filter based on age range.
     * @param region filter based on region.
     * @param gender filter based on gender.
     * @param type filter based on profile type.
     * @param organs filter based on organs selected.
     * @return a sublist of profiles.
     */
    List<Profile> search(String searchString, int ageSearchInt, int ageRangeSearchInt, String region,
            String gender, String type, Set<OrganEnum> organs) throws SQLException;

    /**
     * Gets the number of profiles in the database.
     * @return the number of profiles.
     */
    int size() throws SQLException;

    /**
     * Gets all profiles that require organs.
     * @return a list of entries for the waiting list.
     */
    List<Entry<Profile, OrganEnum>> getAllReceiving();

    /**
     * Filter the waiting list by a search string.
     * @param searchString to filter by.
     * @return a sublist of the waiting list.
     */
    List<Entry<Profile, OrganEnum>> searchReceiving(String searchString);
}
