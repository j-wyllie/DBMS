package odms.dao;

import odms.enums.OrganEnum;
import odms.profile.Procedure;
import odms.profile.Profile;

public interface ProcedureDAO {

    /**
     * Get all procedures for the profile.
     * @param profile to get the conditions for.
     * @param current procedures or false for past procedures.
     */
    void getAll(Profile profile, Boolean current);

    /**
     * Add a new procedure to a profile.
     * @param profile to add the procedure to.
     * @param procedure to add.
     */
    void add(Profile profile, Procedure procedure);

    /**
     * Remove a procedure from a profile.
     * @param profile to remove the procedure from.
     * @param procedure to remove.
     */
    void remove(Profile profile, Procedure procedure);

    /**
     * Update a procedure for the profile.
     * @param profile to update the procedure for.
     * @param procedure to update.
     */
    void update(Profile profile, Procedure procedure);

    /**
     * Add an affected organ to a procedure for a profile.
     * @param procedure to add the organ to.
     * @param organ to add.
     */
    void addAffectedOrgan(Procedure procedure, OrganEnum organ);

    /**
     * Remove an affected organ from a procedure for a profile.
     * @param procedure to remove the organ from.
     * @param organ to remove.
     */
    void removeAffectedOrgan(Procedure procedure, OrganEnum organ);
}
