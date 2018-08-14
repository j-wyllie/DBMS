package odms.controller.database.medication;

import java.util.List;
import odms.commons.model.medications.Drug;
import odms.commons.model.profile.Profile;

public class HttpMedicationDAO implements MedicationDAO {

    @Override
    public List<Drug> getAll(Profile profile, Boolean current) {
        return null;
    }

    @Override
    public void add(Drug drug, Profile profile, Boolean current) {

    }

    @Override
    public void remove(Drug drug) {

    }

    @Override
    public void update(Drug drug, Boolean current) {

    }
}
