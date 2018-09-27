package server.model.database.profile;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.UserNotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Interface to define all of the ProfileDAO methods.
 */
public interface ProfileDAO {

    /**
     * Gets all profiles from the database.
     *
     * @return a list of all the profiles in the db.
     */
    List<Profile> getAll();

    /**
     * Gets all of the dead profiles in the db.
     *
     * @return a list of all the dead profiles.
     */
    List<Profile> getDead();

    /**
     * Gets all of the dead profiles in the db.
     *
     * @return a list of all the dead profiles.
     * @throws SQLException thrown on invalid sql.
     */
    List<Profile> getDeadFiltered(String searchString) throws SQLException;

    /**
     * Get a single profile from the database by id.
     *
     * @param profileId of the profile.
     * @return a profile.
     */
    Profile get(int profileId);

    /**
     * Get a single profile from the database by username.
     *
     * @param username of the profile.
     * @return a profile.
     * @throws SQLException thrown on invalid sql.
     */
    Profile get(String username) throws SQLException;

    /**
     * Adds a new profile to the database.
     *
     * @param profile to add.
     * @throws SQLException thrown on invalid sql.
     */
    void add(Profile profile) throws SQLException;

    /**
     * Checks if a username already exists in the database.
     *
     * @param username to check.
     * @return true is the username does not already exist.
     */
    boolean isUniqueUsername(String username);

    /**
     * checks that an nhi is unique in the database.
     *
     * @param nhi nhi to be checked.
     * @return 0 if not unique, 1 if unique.
     */
    int isUniqueNHI(String nhi);

    /**
     * Removes a profile from the database.
     *
     * @param profile to remove.
     * @throws SQLException thrown on invalid sql.
     */
    void remove(Profile profile) throws SQLException;

    /**
     * Updates a profiles information in the database.
     *
     * @param profile to update.
     * @throws SQLException thrown on invalid sql.
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
     */
    List<Profile> search(String searchString, int ageSearchInt, int ageRangeSearchInt,
            String region,
            String gender, String type, Set<OrganEnum> organs);

    /**
     * Gets the number of profiles in the database.
     *
     * @return the number of profiles.
     * @throws SQLException thrown on invalid sql.
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
     * Checks that a profile has a password.
     *
     * @param nhi nhi of the profile.
     * @return true if they have a profile.
     * @throws SQLException thrown when there is a server error.
     */
    Boolean hasPassword(String nhi) throws SQLException;

    /**
     * Checks the credentials of the profile.
     *
     * @param username username.
     * @param password password.
     * @return boolean, true if valid.
     * @throws UserNotFoundException thrown when a profile is not found in the database.
     */
    Boolean checkCredentials(String username, String password) throws UserNotFoundException;

    /**
     * Saves a password to the database for a certain profile.
     *
     * @param nhi nhi of the profile.
     * @param password password to be set.
     * @return true if successfully set, false otherwise.
     * @throws UserNotFoundException thrown when a user is not found in the database.
     */
    Boolean savePassword(String nhi, String password) throws UserNotFoundException;

    /**
     * Updates the blood donation points and last donation datetime for a profile.
     * @param id of the profile.
     * @param points to update to.
     * @throws SQLException error.
     */
    void updateBloodDonation(int id, int points) throws SQLException;
}
