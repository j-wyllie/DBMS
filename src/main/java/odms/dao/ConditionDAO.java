package odms.dao;

import odms.profile.Condition;
import odms.profile.Profile;

public interface ConditionDAO {

    /**
     * Get all conditions for the profile.
     * @param profile to get the conditions for.
     * @param current conditions or false for past conditions.
     */
    void getAll(Profile profile, Boolean current);

    /**
     * Add a new condition to a profile.
     * @param profile to add the condition to.
     * @param condition to add.
     */
    void add(Profile profile, Condition condition);

    /**
     * Remove a condition from a profile.
     * @param profile to remove the condition from.
     * @param condition to remove.
     */
    void remove(Profile profile, Condition condition);

    /**
     * Update a condition for the profile.
     * @param profile to update the condition for.
     * @param condition to update.
     */
    void update(Profile profile, Condition condition);
}
