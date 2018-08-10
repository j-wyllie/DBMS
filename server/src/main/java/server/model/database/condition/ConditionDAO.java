package server.model.database.condition;

import java.util.ArrayList;
import server.model.profile.Condition;
import server.model.profile.Profile;

public interface ConditionDAO {

    /**
     * Get all conditions for the profile.
     * @param profile to get the conditions for.
     * @param chronic true if the conditions required are chronic.
     */
    ArrayList<Condition> getAll(Profile profile, boolean chronic);

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
