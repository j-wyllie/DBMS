package odms.controller.database;

import static java.time.LocalDateTime.now;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odms.controller.data.MedicationDataIO;
import odms.model.medications.Interaction;

public class JsonMedicationInteractionsDAO implements MedicationInteractionsDAO {

    private Map<Integer, Interaction> interactionMap = new HashMap<>();
    private String defaultPath = "cache/medication_interactions.json";
    private String path;
    private static final String INTERACTION_URL = "https://www.ehealthme.com/api/v1/drug-interaction/%s/%s/";
    private static final String SERVER_ERROR = "1";

    /**
     * Get all interaction data stored in the cache.
     * @return all interactions data.
     */
    @Override
    public Map<Integer, Interaction> getAll() {
        return interactionMap;
    }

    /**
     * Get the interactions between two medications.
     * @param drugA is an interacting medication.
     * @param drugB is another interacting medications.
     */
    @Override
    public Interaction get(String drugA, String drugB) throws IOException {
        for (Object interactionKey : interactionMap.keySet()) {

            Interaction value = interactionMap.get(interactionKey);
            if (value.getDrugA().equalsIgnoreCase(drugA)
                    && value.getDrugB().equalsIgnoreCase(drugB)) {

                if (value.getDateTimeExpired().isBefore(now()) ||
                        value.getDateTimeExpired().isEqual(now())) {
                    value = add(value.getDrugA(), value.getDrugB());
                    interactionMap.replace((Integer) interactionKey, value);
                }
                return value;
            }
        }
        Interaction newInteraction = add(drugA, drugB);
        if (newInteraction != null && newInteraction.getDrugA() != null) {
            if (newInteraction.getDrugA() != null) {
                interactionMap.put(interactionMap.size(), newInteraction);
                save();
            }
        }
        return newInteraction;
    }

    /**
     * Loads the JSON interactions data from the set location.
     */
    @Override
    public void load() {
        Gson gson = new Gson();

        String file = defaultPath;
        if ((path != null)) {
            file = path;
        }
        try {
            this.interactionMap.clear();
            JsonParser parser = new JsonParser();
            JsonObject cache = parser.parse(new FileReader(file)).getAsJsonObject();

            cache.keySet().forEach(key -> {
                Interaction value = gson.fromJson(
                        cache.get(key).getAsJsonObject(),
                        Interaction.class
                );
                this.interactionMap.put(Integer.valueOf(key), value);
            });
        } catch (FileNotFoundException e) {
            System.out.println("The medication interactions JSON file could not be found.");
        } catch (Exception e) {
            System.out.println("There was an error opening the medication interactions JSON file.");
        }
    }

    /**
     * Saves the JSON interactions data to the set location.
     */
    @Override
    public boolean save() {
        File file = new File(this.defaultPath);
        if ((this.path != null)) {
            file = new File(this.path);
        }

        try (BufferedWriter writeFile = new BufferedWriter(new FileWriter(file))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            writeFile.write(gson.toJson(interactionMap));

            System.out.println("Cache exported successfully!");
            return true;

        } catch (IOException e) {
            System.out.println("IO exception, please check the specified cache location.");
            System.out.println("Cache requested: " + path);
            return false;
        }
    }

    /**
     * Request new interaction data between two medications from the server.
     * @param drugA is an interacting medication.
     * @param drugB is another interacting medication.
     * @return the new interaction data.
     */
    private Interaction add(String drugA, String drugB) throws IOException {
        Interaction interaction = null;

        if (!(drugA == null || drugA.equals("") || drugB == null || drugB.equals(""))) {
            StringBuffer response = getResponse(drugA, drugB);
            if (response != null) {
                if (response.toString().equals(SERVER_ERROR)) {
                    interaction = new Interaction(null, null, null, null, null, null);
                } else {

                    try {
                        JsonParser parser = new JsonParser();
                        JsonObject results = parser.parse(response.toString()).getAsJsonObject();

                        Map<String, List<String>> ageEffects = parseListInteractions(results
                                .get("age_interaction"));

                        Map<String, Integer> coexistingConditions = parseAtomicInteractions(results
                                .get("co_existing_conditions"));

                        Map<String, List<String>> durationInteractions = parseListInteractions(results
                                .get("duration_interaction"));

                        Map<String, List<String>> genderInteractions = parseListInteractions(results
                                .get("gender_interaction"));

                        interaction = new Interaction(drugA, drugB, ageEffects, coexistingConditions,
                                durationInteractions, genderInteractions);
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        }
        return interaction;
    }

    /**
     * Makes a request to the server to get the interactions between the medications.
     * @param drugA is an interacting medication.
     * @param drugB is the other interacting medication.
     * @return the response from the server.
     * @throws IOException error.
     */
    private StringBuffer getResponse(String drugA, String drugB) throws IOException {
        drugA = MedicationDataIO.replaceSpace(drugA, true);
        drugB = MedicationDataIO.replaceSpace(drugB, true);
        String urlString = String.format(INTERACTION_URL, drugA, drugB);

        // Reading the response from the connection.
        StringBuffer response = MedicationDataIO.makeRequest(urlString);
        if (response == null) {
            // Server is fussy about what order the drugs are in the url, if request fails will
            // try again with drugs in different order.
            // Sometimes it will return SERVER_ERROR but other times it will return null
            // So we need to check for both cases
            urlString = String.format(INTERACTION_URL, drugB, drugA);
            response = MedicationDataIO.makeRequest(urlString);
        } else if (response.toString().equals(SERVER_ERROR)) {
            urlString = String.format(INTERACTION_URL, drugB, drugA);
            response = MedicationDataIO.makeRequest(urlString);
        }
        return response;
    }

    /**
     * Reformats the interaction data from a JSON string to a mapping between string keys and lists
     * of the relevant data.
     * @param element the json element representing the interactions data.
     * @return a key/value mapping of the interaction data.
     */
    public Map<String, List<String>> parseListInteractions(JsonElement element) {
        if (element.isJsonNull()) {
            return null;
        }
        JsonObject interactionObj = element.getAsJsonObject();
        Map<String, List<String>> result = new HashMap<>();
        Gson gson = new Gson();

        interactionObj.keySet().forEach(key -> {
            ArrayList value = gson.fromJson(
                    interactionObj.get(key).getAsJsonArray(),
                    ArrayList.class
            );
            result.put(key, value);
        });
        return result;
    }

    /**
     * Reformats the interaction data from a JSON string to a mapping between string keys and the
     * integer values of the relevant data.
     * @param element the json element representing the interactions data.
     * @return a key/value mapping of the interaction data.
     */
    public Map<String, Integer> parseAtomicInteractions(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        JsonObject interactionObj = element.getAsJsonObject();
        Map<String, Integer> result = new HashMap<>();
        Gson gson = new Gson();

        interactionObj.keySet().forEach(key -> {
            Integer value = gson.fromJson(interactionObj.get(key), Integer.class);
            result.put(key, value);
        });
        return result;
    }

    /**
     * Clear all cached medication interaction data.
     */
    @Override
    public void clear() {
        this.interactionMap.clear();
    }

    /**
     * Sets the location of the cached medication interactions.
     * @param path to the location.
     */
    @Override
    public void setLocation(String path) {
        this.path = path;
    }
}
