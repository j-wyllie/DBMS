package odms.dao;

import static java.time.LocalDateTime.now;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odms.data.MedicationDataIO;
import odms.medications.Interaction;

public class JsonMedicationInteractionsDAO implements MedicationInteractionsDAO {

    private Map<Integer, Interaction> interactionDb = new HashMap<>();
    private String defaultPath = "cache/medication_interactions.json";
    private String path;
    private static final String INTERACTION_URL = "https://www.ehealthme.com/api/v1/drug-interaction/%s/%s/";
    private static final String SERVER_ERROR = "1";

    /**
     * Get the interactions between two medications.
     * @param drugA is an interacting medication.
     * @param drugB is another interacting medications.
     */
    @Override
    public Interaction get(String drugA, String drugB) throws IOException {
        for (Interaction interaction : interactionDb.values()) {
            if (interaction.getDrugA().equalsIgnoreCase(drugA)
                    && interaction.getDrugB().equalsIgnoreCase(drugB)) {

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
        Gson gson = new Gson();

        String file = defaultPath;
        if (!(path == null)) {
            file = path;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            this.interactionDb = gson.fromJson(reader, Map.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        File file = new File(this.defaultPath);
        if (!(this.path == null)) {
            file = new File(this.path);
        }

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            BufferedWriter writeFile = new BufferedWriter(new FileWriter(file));

            writeFile.write(gson.toJson(interactionDb));

            writeFile.close();

            System.out.println("File exported successfully!");

        } catch (IOException e) {
            System.out.println("IO exception, please check the specified file");
            System.out.println("File requested: " + path);
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
                JsonParser parser = new JsonParser();
                JsonObject results = parser.parse(response.toString()).getAsJsonObject();

                Map<String, List<String>> ageEffects = parseListInteractions(results
                        .get("age_interaction")
                        .getAsJsonObject());

                Map<String, Integer>  coexistingConditions = parseAtomicInteractions(results
                        .get("co_existing_conditions")
                        .getAsJsonObject());

                Map<String, List<String>> durationInteractions = parseListInteractions(results
                        .get("duration_interaction")
                        .getAsJsonObject());

                Map<String, List<String>> genderInteractions = parseListInteractions(results
                        .get("gender_interaction")
                        .getAsJsonObject());

                interaction = new Interaction(drugA, drugB, ageEffects, coexistingConditions,
                        durationInteractions, genderInteractions);
            }
        }
        return interaction;
    }

    /**
     * Makes a request to the server to get the interactions between the medications.
     * @param drugA is an interacting medication.
     * @param drugB is the othe interacting medication.
     * @return the response from the server.
     * @throws IOException
     */
    private StringBuffer getResponse(String drugA, String drugB) throws IOException {
        drugA = MedicationDataIO.replaceSpace(drugA, true);
        drugB = MedicationDataIO.replaceSpace(drugB, true);
        String urlString = String.format(INTERACTION_URL, drugA, drugB);

        // Reading the response from the connection.
        StringBuffer response = MedicationDataIO.makeRequest(urlString);

        if (response == null) {
            return response;
        }
        else if (response.toString().equals(SERVER_ERROR)) {
            // Server is fussy about what order the drugs are in the url, if request fails will
            // try again with drugs in different order.
            urlString = String.format(INTERACTION_URL, drugB, drugA);
            response = MedicationDataIO.makeRequest(urlString);
            if (response == null || response.toString().equals(SERVER_ERROR)) {
                return null;
            }
        }
        return response;
    }

    /**
     * Reformats the interaction data from a JSON string to a mapping between string keys and lists
     * of the relevant data.
     * @param interactionObj the json object representing the interactions data.
     * @return a key/value mapping of the interaction data.
     */
    private Map<String, List<String>> parseListInteractions(JsonObject interactionObj) {
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
     * @param interactionObj the json object representing the interactions data.
     * @return a key/value mapping of the interaction data.
     */
    private Map<String, Integer> parseAtomicInteractions(JsonObject interactionObj) {
        Map<String, Integer> result = new HashMap<>();
        Gson gson = new Gson();

        interactionObj.keySet().forEach(key -> {
            Integer value = gson.fromJson(interactionObj.get(key), Integer.class);
            result.put(key, value);
        });
        return result;
    }

    /**
     * Clear all cached medication interactions.
     */
    @Override
    public void clear() {
        this.interactionDb.clear();
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
