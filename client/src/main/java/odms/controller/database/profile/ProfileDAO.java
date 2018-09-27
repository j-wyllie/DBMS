package odms.controller.database.profile;

import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.data.NHIConflictException;

/**
 * Profile DAO class.
 */
public interface ProfileDAO {

    /**
     * Gets all profiles from the database.
     *
     * @return List of all profiles in the database.
     * @throws SQLException Thrown when there is an error in the sql.
     */
    List<Profile> getAll() throws SQLException;

    /**
     * Gets all profiles from the database where the person is dead.
     *
     * @return List of all dead profiles in the database.
     * @throws SQLException Thrown when there is an error in the sql.
     */
    List<Profile> getDead() throws SQLException;

    /**
     * Gets all profiles from the database where the person is dead and matches the given search
     * string.
     *
     * @param searchString The search string to filter
     * @return List of all dead profiles filtered by the search string.
     * @throws SQLException Thrown when there is an error in the sql.
     */
    List<Profile> getDeadFiltered(String searchString) throws SQLException;


    /**
     * Get a single profile from the database by id.
     *
     * @param profileId of the profile.
     * @return a profile.
     * @throws SQLException Thrown when there is an error in the sql.
     */
    Profile get(int profileId) throws SQLException;

    /**
     * Get a single profile from the database by username.
     *
     * @param username of the profile.
     * @return a profile.
     * @throws SQLException Thrown when there is an error in the sql.
     */
    Profile get(String username) throws SQLException;

    /**
     * Adds a new profile to the database.
     *
     * @param profile to add.
     * @throws SQLException Thrown when there is an error in the sql.
     * @throws NHIConflictException Thrown when a profile with the same NHI already exists in the
     * database.
     */
    void add(Profile profile) throws SQLException, NHIConflictException;

    /**
     * Checks if a username already exists in the database.
     *
     * @param username to check.
     * @return true is the username does not already exist.
     * @throws SQLException Thrown when there is an error in the sql.
     */
    boolean isUniqueUsername(String username) throws SQLException;

    /**
     * Checks for a unique NHI.
     *
     * @param nhi nhi to check.
     * @return 1 if unique.
     * @throws SQLException Thrown when there is an error in the sql.
     */
    int isUniqueNHI(String nhi) throws SQLException;

    /**
     * Removes a profile from the database.
     *
     * @param profile to remove.
     * @throws SQLException Thrown when there is an error in the sql.
     */
    void remove(Profile profile) throws SQLException;

    /**
     * Updates a profiles information in the database.
     *
     * @param profile to update.
     * @throws SQLException Thrown when there is an error in the sql.
     */
    void update(Profile profile) throws SQLException;

    /**
     * Searches for a sublist of profiles based on criteria.
     *
     * @param searchString filter based on search field.
     * @param ageSearchInt filter based on age.
     * @param ageRangeSearchInt filter based on age range.
     * @param region filter based on region.
     * @param gender filter based on gender.
     * @param type filter based on profile type.
     * @param organs filter based on organs selected.
     * @return a sublist of profiles.
     * @throws SQLException Thrown when there is an error in the sql.
     */
    List<Profile> search(String searchString, int ageSearchInt, int ageRangeSearchInt,
            String region,
            String gender, String type, Set<OrganEnum> organs) throws SQLException;

    /**
     * Gets the number of profiles in the database.
     *
     * @return the number of profiles.
     * @throws SQLException Thrown when there is an error in the sql.
     */
    Integer size() throws SQLException;

    /**
     * Gets all profiles that require organs.
     *
     * @return a list of entries for the waiting list.
     */
    List<Entry<Profile, OrganEnum>> getAllReceiving();

    /**
     * Filter the waiting list by a search string.
     *
     * @param searchString to filter by.
     * @return a sublist of the waiting list.
     */
    List<Entry<Profile, OrganEnum>> searchReceiving(String searchString);

    /**
     * Get list of receivers that could be recipients of a selected organ.
     *
     * @param organ type of organ that is being donated
     * @param bloodTypes blood type recipient needs to have
     * @param lowerAgeRange lowest age the recipient can have
     * @param upperAgeRange highest age the recipient can have
     * @return list of profile objects
     */
    List<Profile> getOrganReceivers(String organ, String bloodTypes,
            Integer lowerAgeRange, Integer upperAgeRange);

    /**
     * Checks that a profile has a password set.
     *
     * @param nhi nhi to check.
     * @return true if password is set.
     */
    Boolean hasPassword(String nhi);

    /**
     * Checks the username and password of the profile.
     *
     * @param username username to check.
     * @param password password to check.
     * @return boolean, true if credentials are valid.
     */
    Boolean checkCredentials(String username, String password);

    /**
     * Saves a profiles password.
     *
     * @param nhi nhi of the profile.
     * @param password password to be saved.
     * @return Boolean, true if successful.
     */
    Boolean savePassword(String nhi, String password);

    /**
     * Updates the blood donation points and last donation datetime for a profile.
     * @param profileId of the profile.
     * @param points to update to.
     * @throws SQLException error.
     */
    void updateBloodDonation(int profileId, int points);
}
