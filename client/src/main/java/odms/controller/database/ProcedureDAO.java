package odms.controller.database;


import java.util.List;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;

public interface ProcedureDAO {

    /**
     * Get all procedures for the profile.
     * @param profile to get the conditions for.
     * @param pending procedures or false for past procedures.
     * @return a list of past or current procedures.
     */
    List<Procedure> getAll(Profile profile, Boolean pending);

    /**
     * Add a new procedure to a profile.
     * @param profile to add the procedure to.
     * @param procedure to add.
     */
    void add(Profile profile, Procedure procedure);

    /**
     * Remove a procedure from a profile.
     * @param procedure to remove.
     */
    void remove(Procedure procedure);

    /**
     * Update a procedure for the profile.
     * @param procedure to update.
     */
    void update(Procedure procedure);

    /**
     * Gets all affected organs for a procedure.
     * @param procedureId the procedure id.
     * @return a list of organs.
     */
    List<OrganEnum> getAffectedOrgans(int procedureId);

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
