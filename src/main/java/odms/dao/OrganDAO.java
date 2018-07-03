package odms.dao;

import odms.enums.OrganEnum;
import odms.profile.Profile;

public interface OrganDAO {

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
     */
    void addDonating(Profile profile, OrganEnum organ);

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
}
