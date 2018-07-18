package odms.dao;

import static java.time.LocalDateTime.*;

import java.util.HashMap;
import java.util.Map;
import odms.medications.Interaction;

public class JsonMedicationInteractionsDAO implements MedicationInteractionsDAO {

    private Map<Integer, Interaction> interactionDb = new HashMap<>();
    private String defaultPath = "cache/medication_interactions.json";
    private String path;

    /**
     * Get the interactions between two medications.
     * @param drugA is an interacting medication.
     * @param drugB is another interacting medications.
     */
    @Override
    public Interaction get(String drugA, String drugB) {
        for (Interaction interaction : interactionDb.values()) {
            if (interaction.getDrugA().toLowerCase().equals(drugA)
                    && interaction.getDrugB().toLowerCase().equals(drugB)) {

                if (interaction.getDateTimeExpired().isBefore(now())
                    || interaction.getDateTimeExpired().isEqual(now())) {
                    interaction = add(interaction.getDrugA(), interaction.getDrugB());
                }
                return interaction;
            }
        }
        return add(drugA, drugB);
    }

    @Override
    public void load() {

    }

    /**
     * Request new interaction data between two medications from the server.
     * @param drugA is an interacting medication.
     * @param drugB is another interacting medication.
     * @return the new interaction data.
     */
    private Interaction add(String drugA, String drugB) {

    }

    /**
     * Clear all cached medication interactions.
     */
    @Override
    public void clear() {
        interactionDb.clear();
    }

    @Override
    public void setLocation(String path) { this.path = path; }
}
