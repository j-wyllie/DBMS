package odms.controller.database.hlaType;

import odms.commons.model.profile.HLAType;

public class HttpHLATypeDAO implements HLATypeDAO {

    private static final String HLAType_URL = "http://localhost:6969/api/v1/countries";

    @Override
    public HLAType get(int profile) {
        return null;
    }

    @Override
    public void add(int profile, HLAType hla) {

    }

    @Override
    public void remove(int profile) {

    }

    @Override
    public void update(HLAType hla, int profile) {

    }
}
