package odms.dao;

import odms.medications.Interaction;

public interface MedicationInteractionsDAO {

    void get(String drugA, String drugB);

}
