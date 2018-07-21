package odms.dao;

import java.io.IOException;
import odms.medications.Interaction;

public interface MedicationInteractionsDAO {

    /**
     * Get the interactions between two medications.
     * @param drugA is a interacting medication.
     * @param drugB is another interacting medications.
     * @return the interaction.
     */
    Interaction get(String drugA, String drugB) throws IOException;

    void load();

    void save();

    /**
     * Clear all cached medication interactions.
     */
    void clear();

    /**
     * Sets the location of the cached medication interactions.
     * @param path to the location.
     */
    void setLocation(String path);
}
