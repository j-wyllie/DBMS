package odms.dao;

import odms.profile.Procedure;
import odms.profile.Profile;

public interface ProcedureDAO {

    /**
     * Get all procedures for the profile.
     */
    void getAll();

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
}
