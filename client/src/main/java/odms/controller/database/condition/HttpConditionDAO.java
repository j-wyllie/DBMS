package odms.controller.database.condition;

import java.util.ArrayList;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.Profile;

public class HttpConditionDAO implements ConditionDAO {

    @Override
    public ArrayList<Condition> getAll(Profile profile, boolean chronic) {
        return null;
    }

    @Override
    public void add(Profile profile, Condition condition) {

    }

    @Override
    public void remove(Condition condition) {

    }

    @Override
    public void update(Condition condition) {

    }
}
