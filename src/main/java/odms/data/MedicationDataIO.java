package odms.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MedicationDataIO {

    public static Map<String, String> getDrugInteractions(String drug1, String drug2, String gender, int age) throws IOException {
        Map<String, String> interactions;
        interactions = new HashMap<>();


        if (!(drug1 == null || drug1 == "" || drug2 == null || drug2 == "")) {
            String urlString = String
                    .format("https://www.ehealthme.com/api/v1/drug-interaction/%s/%s/", drug1, drug2);
            URL url = new URL(urlString);

            //Reading the response from the connection.
            StringBuffer response = makeRequest(url);

            interactions = parseInteractionsJSON(response, gender, age);
        }
        return interactions;
    }

    /**
     * Makes a HTTP GET request to the url passed as a parameter and then returns the response.
     * @param url represents the address the HTTP GET request is made to.
     * @throws IOException
     */
    private static StringBuffer makeRequest(URL url) throws IOException {
        //Creating the connection to the API server.
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        BufferedReader response = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        StringBuffer responseContent = new StringBuffer();
        String line = response.readLine();
        while (line != null) {
            responseContent.append(line);
            line = response.readLine();
        }
        response.close();
        con.disconnect();

        return responseContent;
    }

    /**
     * Parse the JSON object returned from the HTTP response for drug interactions. Returns Map of valid interactions
     * for profile and the duration after which an interaction may occur.
     * @param content JSON text to be parsed.
     * @return Map<String, String> keys are valid interactions and values are duration of time after which an
     * interaction may occur.
     */
    private static Map<String, String> parseInteractionsJSON(StringBuffer content, String gender, int age) {
        Map<String, String> interactions;
        JsonParser parser = new JsonParser();
        interactions = new HashMap<>();
        JsonObject results = parser.parse(content.toString()).getAsJsonObject();
        JsonObject genderInteractions = results.get("gender_interaction").getAsJsonObject();
        if (!(gender.equals("male") || gender.equals("female"))) {
            interactions.putAll(parseGenderInteractionsJSON(interactions, genderInteractions, "male"));
            interactions.putAll(parseGenderInteractionsJSON(interactions, genderInteractions, "female"));
        } else {
            interactions.putAll(parseGenderInteractionsJSON(interactions, genderInteractions, gender));
        }
        JsonObject interactionDuration = results.get("duration_interaction").getAsJsonObject();
        interactions = parseInteractionDurationJSON(interactions, interactionDuration);
        return interactions;
    }

    /**
     * Parse JSON object that contains drug interactions based on male or female genders.
     * @param interactions Map of drug interactions, keys are valid interactions and values are duration of time after
     *                     which an interaction may occur.
     * @param genderInteractions JSONObject that contains two JSONArrays, male or female. Arrays contain drug
     *                           interactions
     * @param gender gender of current profile logged in.
     * @return Map<String, String> keys are valid interactions and values are empty strings.
     */
    private static Map<String, String> parseGenderInteractionsJSON(Map<String, String> interactions,
                                                                   JsonObject genderInteractions, String gender) {
        JsonArray genderInteractionsArray = genderInteractions.get(gender).getAsJsonArray();
        for (JsonElement value : genderInteractionsArray) {
            if (!interactions.containsKey(value.toString())) {
                interactions.put(value.toString(), "");
            }
        }
        return interactions;
    }

    /**
     * Parse JSON object that contains drug interactions based on male or female genders.
     * @param interactions Map of drug interactions, keys are valid interactions and values are duration of time after
     *                     which an interaction may occur.
     * @param interactionDuration
     * @return
     */
    private static Map<String, String> parseInteractionDurationJSON(Map<String, String> interactions,
                                                                    JsonObject interactionDuration) {
        Map<String, String> result;
        result = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : interactionDuration.entrySet()) {
            JsonArray interactionArray = entry.getValue().getAsJsonArray();
            for (JsonElement value : interactionArray) {
                if (interactions.containsKey(value.toString())) {
                    result.put(value.toString(), entry.getKey());
                }
            }
        }
        return result;
    }
}
