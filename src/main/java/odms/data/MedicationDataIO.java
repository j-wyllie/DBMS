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

    /**
     * Get Map of interactions between two drugs. The values of each key is the time frame after taking both drugs in
     * which the interaction may occur. Age and gender is used to get the most relevant interactions for a profile.
     * @param drug1 Name of first drug.
     * @param drug2 Name of second drug.
     * @param gender gender of profile request is made for.
     * @param age age of profile request is made for.
     * @return Map<String, String> keys are valid interactions and values are duration of time after which an
     * interaction may occur.
     * @throws IOException
     */
    public static Map<String, String> getDrugInteractions(String drug1, String drug2, String gender, int age) throws IOException {
        Map<String, String> interactions;
        interactions = new HashMap<>();


        if (!(drug1 == null || drug1.equals("") || drug2 == null || drug2.equals(""))) {
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

        JsonObject interactionAge = results.get("age_interaction").getAsJsonObject();
        interactions = parseInteractionAgeJSON(interactions, interactionAge, age);

        JsonObject interactionDuration = results.get("duration_interaction").getAsJsonObject();
        interactions = parseInteractionDurationJSON(interactions, interactionDuration);
        return interactions;
    }

    /**
     * Parse JSON object that contains drug interactions based on male or female genders.
     * @param interactions Map of drug interactions, keys are valid interactions and values are duration of time after
     *                     which an interaction may occur.
     * @param interactionGender JSONObject that contains two JSONArrays, male or female. Arrays contain drug
     *                           interactions
     * @param gender gender of profile request is made for.
     * @return Map<String, String> keys are valid interactions and values are empty strings.
     */
    private static Map<String, String> parseGenderInteractionsJSON(Map<String, String> interactions,
                                                                   JsonObject interactionGender, String gender) {
        JsonArray genderInteractionsArray = interactionGender.get(gender).getAsJsonArray();
        for (JsonElement value : genderInteractionsArray) {
            if (!interactions.containsKey(value.toString())) {
                interactions.put(value.toString(), "");
            }
        }
        return interactions;
    }

    /**
     *
     * @param interactions Map of drug interactions, keys are valid interactions and values are duration of time after
     *                     which an interaction may occur.
     * @param interactionAge JSON object that contains arrays of interactions, each array represents the age group in
     *                       which interactions may occur.
     * @param age age of profile request is made for.
     * @return Map<String, String> keys are valid interactions and values are empty strings.
     */
    private static Map<String, String> parseInteractionAgeJSON(Map<String, String> interactions,
                                                               JsonObject interactionAge, int age) {
        String ageGroup;
        int lowerBound;
        int upperBound;
        String[] ageGroupArray;
        JsonArray interactionArray;

        for (Map.Entry<String, JsonElement> entry : interactionAge.entrySet()) {
            ageGroup = entry.getKey();
            if (ageGroup.equals("nan")) {
                return interactions;
            } else if (ageGroup.equals("60+") && age > 60) {
                interactionArray = entry.getValue().getAsJsonArray();
                for (JsonElement value : interactionArray) {
                    interactions.put(value.toString(), "");
                }
                return interactions;
            } else {
                ageGroupArray = ageGroup.split("-");
                lowerBound = Integer.parseInt(ageGroupArray[0]);
                upperBound = Integer.parseInt(ageGroupArray[1]);
                if (age >= lowerBound && age <= upperBound) {
                    interactionArray = entry.getValue().getAsJsonArray();
                    for (JsonElement value : interactionArray) {
                        interactions.put(value.toString(), "");
                    }
                    return interactions;
                }
            }
        }
        return interactions;
    }

    /**
     * Parse JSON object that contains drug interactions based on time of taking the drugs.
     * @param interactions Map of drug interactions, keys are valid interactions and values are duration of time after
     *                     which an interaction may occur.
     * @param interactionDuration JSON object that contains arrays of interactions, each array represents the time frame
     *                            in which the interaction may occur.
     * @return Map<String, String> keys are valid interactions and values are duration of time after which an
     * interaction may occur.
     */
    private static Map<String, String> parseInteractionDurationJSON(Map<String, String> interactions,
                                                                    JsonObject interactionDuration) {
        for (Map.Entry<String, JsonElement> entry : interactionDuration.entrySet()) {
            JsonArray interactionArray = entry.getValue().getAsJsonArray();
            for (JsonElement value : interactionArray) {
                if (interactions.containsKey(value.toString()) && interactions.get(value.toString()).equals("")) {
                    interactions.put(value.toString(), entry.getKey());
                }
            }
        }

        // If interaction did not have a duration and was not listed in 'not specified' JSON array, it is given the
        // value 'not specified'
        for (Map.Entry<String, String> entry : interactions.entrySet()) {
            if (entry.getValue().equals("")) {
                interactions.put(entry.getKey(), "not specified");
            }
        }
        return interactions;
    }
}
