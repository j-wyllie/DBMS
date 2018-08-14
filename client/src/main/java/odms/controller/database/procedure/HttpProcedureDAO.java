package odms.controller.database.procedure;

import java.util.List;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;

public class HttpProcedureDAO implements ProcedureDAO {

    @Override
    public List<Procedure> getAll(Profile profile, Boolean pending) {
        return null;
    }

    @Override
    public void add(Profile profile, Procedure procedure) {

    }

    @Override
    public void remove(Procedure procedure) {

    }

    @Override
    public void update(Procedure procedure) {

    }

    @Override
    public List<OrganEnum> getAffectedOrgans(int procedureId) {
        return null;
    }

    @Override
    public void addAffectedOrgan(Procedure procedure, OrganEnum organ) {

    }

    @Override
    public void removeAffectedOrgan(Procedure procedure, OrganEnum organ) {

    }
}
