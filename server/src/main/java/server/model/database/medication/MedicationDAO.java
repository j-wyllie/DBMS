package server.model.database.medication;

import java.util.List;
import odms.commons.model.medications.Drug;

public interface MedicationDAO {

    /**
     * Gets all the current and past drugs from the database for a single profile.
     * @param profile to get the drugs from.
     * @param current true if the current drugs are required for that profile.
     * @return a list of current or past drugs.
     */
    List<Drug> getAll(int profile, Boolean current);

    /**
     * Adds a new drug for a profile stored in the database.
     * @param drug to add.
     * @param profile to add the drug to.
     * @param current is true if the profile is currently taking the drug.
     */
    void add(Drug drug, int profile, Boolean current);

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
