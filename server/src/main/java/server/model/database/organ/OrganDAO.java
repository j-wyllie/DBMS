package server.model.database.organ;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.ExpiredOrgan;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;

public interface OrganDAO {

    /**
     * Gets all organs that a profile has donated in the past.
     * @param profile to get the organs for.
     */
    Set<OrganEnum> getDonations(Profile profile);

    /**
     * Gets all organs that a profile has registered to donate.
     * @param profile to get the organs for.
     */
    Set<OrganEnum> getDonating(Profile profile);

    /**
     * Gets all organs that a profile requires.
     * @param profile to get the organs for.
     */
    Set<OrganEnum> getRequired(Profile profile);

    /**
     * Gets all organs that a profile has received in the past.
     * @param profile to get the organs for.
     */
    Set<OrganEnum> getReceived(Profile profile);

    /**
     * Adds an organ to a profiles past donations.
     * @param profile to add the past donation to.
     * @param organ donated.
     */
    void addDonation(Profile profile, OrganEnum organ);

    /**
     * Adds an organ to a profiles organs to donate.
     * @param profile to donate.
     * @param organ to donate.
     * @throws OrganConflictException error.
     */
    void addDonating(Profile profile, OrganEnum organ) throws OrganConflictException;

    /**
     * Adds a organ to a profiles required organs.
     * @param profile requiring the organ.
     * @param organ required.
     */
    void addRequired(Profile profile, OrganEnum organ);

    /**
     * Adds a organ to a profiles received organs.
     * @param profile receiving the organ.
     * @param organ received.
     */
    void addReceived(Profile profile, OrganEnum organ);

    /**
     * Removes an organ from a profiles past donations.
     * @param profile to remove the past donation from.
     * @param organ to remove.
     */
    void removeDonation(Profile profile, OrganEnum organ);

    /**
     * Removes an organ from a profiles organs to donate.
     * @param profile to remove the organ from.
     * @param organ to remove.
     */
    void removeDonating(Profile profile, OrganEnum organ);

    /**
     * Removes an organ from a profiles required organs.
     * @param profile to remove the organ from.
     * @param organ to remove.
     */
    void removeRequired(Profile profile, OrganEnum organ);

    /**
     * Removes an organ from a profiles received organs.
     * @param profile to remove the organ from.
     * @param organ to remove.
     */
    void removeReceived(Profile profile, OrganEnum organ);


    /**
     * Updates organ to be expired.
     *
     * @param profile to updateCountries the organ.
     * @param organ to updateCountries.
     * @param expired expired boolean.
     * @param note Clinician's reason to updateCountries.
     * @param userId Clinician's user Id.
     */
    void setExpired(Profile profile, OrganEnum organ, Integer expired, String note, Integer userId)
            throws SQLException;

    /**
     * Updates organ to be non-expired.
     *
     * @param profileId to revert organ expired.
     * @param organ to revert.
     */
    void revertExpired(Integer profileId, OrganEnum organ) throws SQLException;

    /**
     * Gets all organs that have expired from a profile.
     *
     * @param profile to get the organs for.
     */
    List<ExpiredOrgan> getExpired(Profile profile) throws SQLException;
}
