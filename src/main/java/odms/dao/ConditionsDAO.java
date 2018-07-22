package odms.dao;

import odms.profile.Condition;
import odms.profile.Profile;

import java.util.ArrayList;

public interface ConditionsDAO {

    /**
     * Get all conditions for the profile.
     * @param profile to get the conditions for.
     * @param current conditions or false for past conditions.
     */
    ArrayList<Condition> getAll(Profile profile, Boolean current);

    /**
     * Add a new condition to a profile.
     * @param profile to add the condition to.
     * @param condition to add.
     */
    void add(Profile profile, Condition condition);

    /**
     * Remove a condition from a profile.
     * @param condition to remove.
     */
    void remove(Condition condition);

    /**
     * Update a condition for the profile.
     * @param condition to update.
     */
    void update(Condition condition);
}
