package server.model.database.medication;

import java.io.IOException;
import java.util.Map;
import server.model.medications.Interaction;

public interface MedicationInteractionsDAO {

    /**
     * Get all interaction data stored in the cache.
     * @return all interactions data.
     */
    Map<Integer, Interaction> getAll();
    /**
     * Get the interactions between two medications.
     * @param drugA is a interacting medication.
     * @param drugB is another interacting medications.
     * @return the interaction.
     */
    Interaction get(String drugA, String drugB) throws IOException;

    /**
     * Loads the JSON interactions data the set location.
     */
    void load();

    /**
     * Saves the JSON interactions data to the set location.
     */
    boolean save();

    /**
     * Clear all cached medication interaction data.
     */
    void clear();

    /**
     * Sets the location of the cached medication interactions.
     * @param path to the location.
     */
    void setLocation(String path);
}
