package odms.dao;

import odms.medications.Drug;

public interface MedicationInteractionsDAO {

    /**
     * Get all interactions between two drugs stored in the database.
     * @param drugA of the interactions.
     * @param drugB of the interactions
     */
    void get(Drug drugA, Drug drugB);

    /**
     * Add a new interaction between two drugs in the database.
     * @param drugA to interact.
     * @param drugB to interact.
     * @param symptom of the drug interactions.
     * @param duration of the drug interactions.
     */
    void add(Drug drugA, Drug drugB, String symptom, String duration);

    /**
     * Remove all interactions between two drugs.
     * @param drugA of the interactions to remove.
     * @param drugB of the interactions to remove.
     */
    void removeAll(Drug drugA, Drug drugB);

    /**
     * Remove a particular interaction between two drugs.
     * @param drugA of the interaction to remove.
     * @param drugB of the interaction to remove.
     * @param symptom of the interaction to remove.
     * @param duration of the interaction to remove.
     */
    void remove(Drug drugA, Drug drugB, String symptom, String duration);

    /**
     * Update a particular interaction between two drugs.
     * @param drugA of the interaction to update.
     * @param drugB of the interaction to update.
     * @param symptom of the interaction to update.
     * @param duration of the interaction to update.
     */
    void update(Drug drugA, Drug drugB, String symptom, String duration);
}
