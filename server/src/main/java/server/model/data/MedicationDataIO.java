package server.model.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import commons.model.medications.Interaction;

public class MedicationDataIO {

    // Value used to identify if response was a internal server error.
    private static final String SERVERERROR = "1";

    // URL strings for the API endpoints
    private static final String INTERACTIONURL = "https://www.ehealthme.com/api/v1/drug-interaction/%s/%s/";
    private static final String INGREDIENTURL = "http://mapi-us.iterar.co/api/%s/substances.json";
    private static final String SUGGESTIONURL = "http://mapi-us.iterar.co/api/autocomplete?query=%s";

    /**
     * Gets a list of suggestions for the clinician user based on a string or substring they
     * have entered into the field.
     * @param substring represents the value to send in the HTTP GET request to the API.
     * @throws IOException IOException URL and HttpURLConnection may cause IOExceptions.
     */
    public static ArrayList<String> getSuggestionList(String substring) throws IOException {
        ArrayList<String> suggestionList = new ArrayList<>();

        if (!(substring == null || substring.equals(""))) {
            substring = replaceSpace(substring, false);
            String urlString = String
                    .format(SUGGESTIONURL, substring);

            //Reading the response from the connection.
            StringBuffer response = makeRequest(urlString);

            if (response == null || response.toString().equals(SERVERERROR)) {
                return suggestionList;
            }
            //Parsing the list of suggestions from the response.
            suggestionList = parseJSON(response, false);
        }
        return suggestionList;
    }

    /**
     * Gets a list of active ingredients for the drug selected by the clinician user.
     * @param drugName represents the value to send in the HTTP GET request to the API.
     * @throws IOException IOException URL and HttpURLConnection may cause IOExceptions.
     */
    public static ArrayList<String> getActiveIngredients(String drugName) throws IOException {
        ArrayList<String> activeList = new ArrayList<>();

        if (!(drugName == null || drugName.equals(""))) {
            drugName = replaceSpace(drugName, false);
            String urlString = String
                    .format(INGREDIENTURL, drugName);

            //Reading the response from the connection.
            StringBuffer response = makeRequest(urlString);

            if (response == null || response.toString().equals(SERVERERROR)) {
                return activeList;
            }

            //Parsing the list of suggestions from the response.
            activeList = parseJSON(response, true);
        }
        return activeList;
    }

    /**
     * Replaces space character in drug name with either UTF-8 encoding or the dash, '-', character. Depends on value of
     * isInteractions which one is used.
     * @param drug name of drug to be used in http request.
     * @param isInteraction boolean that identifies if drug will be used in drug interaction request or not.
     * @return drug name with space replaced with correct replacement.
     */
    public static String replaceSpace(String drug, Boolean isInteraction) {
        if (!isInteraction) {
            return drug.replace(" ", "%20");
        } else {
            return drug.replace(" ", "-");
        }

    }

    /**
     * Makes a HTTP GET request to the url passed as a parameter and then returns the response.
     * @param urlString represents the address the HTTP GET request is made to.
     * @throws IOException URL and HttpURLConnection may cause IOExceptions.
     */
    public static StringBuffer makeRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        StringBuffer responseContent;
        //Creating the connection to the API server.
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        if (con.getResponseCode() == 200) {
            responseContent = new StringBuffer();
            BufferedReader response = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = response.readLine();
            while (line != null) {
                responseContent.append(line);
                line = response.readLine();
            }
            response.close();
            con.disconnect();
        } else if (con.getResponseCode() < 600 && con.getResponseCode() > 499) {
            // If response was an internal server error, return SERVERERROR
            responseContent = new StringBuffer();
            return responseContent.append(SERVERERROR);
        } else {
            return null;
        }

        return responseContent;
    }

    /**
     * Parse the list of results from the JSON in the HTTP response.
     * @param content represents the buffer holding the JSON response to parse.
     */
    private static ArrayList<String> parseJSON(StringBuffer content, boolean ingredients) {
        ArrayList<String> responseList = new ArrayList<>();
        JsonParser parser = new JsonParser();
        if (ingredients) {
            JsonArray results = parser.parse(content.toString()).getAsJsonArray();
            for (JsonElement value : results) {
                responseList.add(value.toString().replace("\"", ""));
            }
        } else {
            JsonObject resultsObject = parser.parse(content.toString()).getAsJsonObject();
            JsonArray results = resultsObject.getAsJsonArray("suggestions");
            for (JsonElement value : results) {
                responseList.add(value.toString().replace("\"", ""));
            }
        }
        return responseList;
    }

    /**
     * Get Map of interactions between two drugs. The values of each key is the time frame after taking both drugs in
     * which the interaction may occur. Age and gender is used to get the most relevant interactions for a profile.
     * @param interaction The interaction object that contains the symptoms needed to build the
     * valid interactions for the requested age and gender.
     * @param gender gender of profile request is made for.
     * @param age age of profile request is made for.
     * @return Map keys are symptoms and values are duration.
     */
    public static Map<String, String> getDrugInteractions(Interaction interaction, String gender, int age) {
        Map<String, String> interactions = new HashMap<>();

        if (interaction.getDrugA() == null) {
            interactions.put("error", null);
        }
        else {
            if (gender == null || !(gender.equals("male") || gender.equals("female"))) {
                interactions.putAll(parseGenderInteractionsJSON(interactions, interaction.getGenderInteractions(), "male"));
                interactions.putAll(parseGenderInteractionsJSON(interactions, interaction.getGenderInteractions(), "female"));
            } else {
                interactions.putAll(parseGenderInteractionsJSON(interactions, interaction.getGenderInteractions(), gender));
            }

            interactions = parseInteractionAgeJSON(interactions, interaction.getAgeInteractions(), age);

            interactions = parseInteractionDurationJSON(interactions, interaction.getDurationInteractions());
        }
        return interactions;
    }

    /**
     * Parse JSON object that contains drug interactions based on male or female genders.
     * @param interactions Map of drug interactions, keys are valid interactions and values are duration of time after
     *                     which an interaction may occur.
     * @param interactionGender Map that contains genders as keys and a list of symptoms as values.
     * @param gender gender of profile request is made for.
     * @return a map where keys are valid interactions and values are empty strings.
     */
    private static Map<String, String> parseGenderInteractionsJSON(Map<String, String> interactions,
            Map<String, List<String>> interactionGender, String gender) {

        for (Entry<String, List<String>> genderList : interactionGender.entrySet()) {
            if (genderList.getKey().equals(gender)) {
                for (String symptom : genderList.getValue()) {
                    interactions.put(symptom, null);
                }
            }
        }

        return interactions;
    }

    /**
     *
     * @param interactions Map of drug interactions, keys are valid interactions and values are duration of time after
     *                     which an interaction may occur.
     * @param interactionAge Map that contains age groups as keys and a list of symptoms as values.
     * @param age age of profile request is made for.
     * @return a map where keys are valid interactions and values are empty strings.
     */
    private static Map<String, String> parseInteractionAgeJSON(Map<String, String> interactions,
            Map<String, List<String>> interactionAge, int age) {
        Integer lowerBound;
        Integer upperBound;
        String[] ageGroupArray;

        for (Entry<String, List<String>> ageList : interactionAge.entrySet()) {
            if (ageList.getKey().equals("nan")) {
                continue;
            }
            if (ageList.getKey().equals("60+") && age >= 60) {
                for (String symptom : ageList.getValue()) {
                    interactions.put(symptom, null);
                }
                return interactions;
            } else if (!ageList.getKey().equals("60+")) {
                ageGroupArray = ageList.getKey().split("-");
                lowerBound = Integer.parseInt(ageGroupArray[0]);
                upperBound = Integer.parseInt(ageGroupArray[1]);
                if (age >= lowerBound && age <= upperBound) {
                    for (String symptom : ageList.getValue()) {
                        interactions.put(symptom, null);
                    }
                }
                return interactions;
            }
        }

        return interactions;
    }

    /**
     * Parse JSON object that contains drug interactions based on time of taking the drugs.
     * @param interactions Map of drug interactions, keys are valid interactions and values are duration of time after
     *                     which an interaction may occur.
     * @param interactionDuration Map that contains time frames as keys and a list of symptoms as values.
     * @return a map where keys are valid interactions and values are duration of time after which an
     * interaction may occur.
     */
    private static Map<String, String> parseInteractionDurationJSON(Map<String, String> interactions,
            Map<String, List<String>> interactionDuration) {
        SortedMap<String, List<String>> sortedDurations = new TreeMap<>(interactionDuration);
        for (Entry<String, List<String>> durationList : sortedDurations.entrySet()) {
            for (String symptom : durationList.getValue()) {
                if (interactions.containsKey(symptom) && (interactions.get(symptom) == null ||
                        interactions.get(symptom).equalsIgnoreCase("not specified"))) {
                    interactions.put(symptom, durationList.getKey());
                }
            }
        }

        // If interaction did not have a duration and was not listed in 'not specified' JSON array, it is given the
        // value 'not specified'
        for (Entry<String, String> entry : interactions.entrySet()) {
            if (entry.getValue() == null) {
                interactions.put(entry.getKey(), "not specified");
            }
        }

        return interactions;
    }
}
