package odms.dao;

import java.util.List;
import java.util.Map;
import odms.medications.Drug;
import odms.profile.Profile;

public interface MedicationDAO {

    /**
     * Gets all drugs from the database for a single profile.
     * @param profile to get the drugs from.
     */
    Map<String, List<Drug>> getAll(Profile profile);

    /**
     * Adds a new drug for a profile stored in the database.
     * @param drug to add.
     * @param profile to add the drug to.
     * @param current is true if the profile is currently taking the drug.
     */
    void add(Drug drug, Profile profile, Boolean current);

    /**
     * Removes a drug from a profile stored in the database.
     * @param drug to remove.
     */
    void remove(Drug drug);

    /**
     * Updates drug information for a profile in the database.
     * @param drug to update.
     * @param current is true if the profile is currently taking the drug.
     */
    void update(Drug drug, Boolean current);
}
