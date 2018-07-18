package odms.dao;

import odms.medications.Interaction;

public interface MedicationInteractionsDAO {

    /**
     * Get the interactions between two medications.
     * @param drugA is a interacting medication.
     * @param drugB is another interacting medications.
     * @return the interaction.
     */
    Interaction get(String drugA, String drugB);

    /**
     * Sets the location of the cached medication interactions.
     * @param path to the location.
     */
    void setLocation(String path);

    void load();

    /**
     * Clear all cached medication interactions.
     */
    void clear();

}
