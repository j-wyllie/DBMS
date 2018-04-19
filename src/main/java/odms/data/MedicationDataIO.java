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
import java.util.ArrayList;

public class MedicationDataIO {

    /**
     * Gets a list of suggestions for the clinician user based on a string or substring they
     * have entered into the field.
     * @param substring represents the value to send in the HTTP GET request to the API.
     * @throws IOException
     */
    public static ArrayList<String> GetSuggestionList(String substring) throws IOException{
        ArrayList<String> suggestionList = new ArrayList<>();

        if (!(substring == null || substring == "")) {
            String urlString = String
                    .format("http://mapi-us.iterar.co/api/autocomplete?query=%s", substring);
            URL url = new URL(urlString);

            //Reading the response from the connection.
            StringBuffer response = MakeRequest(url);

            //Parsing the list of suggestions from the response.
            suggestionList = ParseJSON(response, false);
        }
        return suggestionList;
    }

    /**
     * Gets a list of active ingredients for the drug selected by the clinician user.
     * @param drugName represents the value to send in the HTTP GET request to the API.
     * @throws IOException
     */
    public static ArrayList<String> GetActiveIngredients(String drugName) throws IOException {
        ArrayList<String> activeList = new ArrayList<>();

        if (!(drugName == null || drugName == "")) {
            String urlString = String
                    .format("http://mapi-us.iterar.co/api/%s/substances.json", drugName);
            URL url = new URL(urlString);

            //Reading the response from the connection.
            StringBuffer response = MakeRequest(url);

            //Parsing the list of suggestions from the response.
            activeList = ParseJSON(response, true);
        }
        return activeList;
    }

    /**
     * Makes a HTTP GET request to the url passed as a parameter and then returns the response.
     * @param url represents the address the HTTP GET request is made to.
     * @throws IOException
     */
    private static StringBuffer MakeRequest(URL url) throws IOException{
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
     * Parse the list of results from the JSON in the HTTP response.
     * @param content represents the buffer holding the JSON response to parse.
     **/
    private static ArrayList<String> ParseJSON(StringBuffer content, boolean ingredients) {
        ArrayList<String> responseList = new ArrayList<>();
        JsonParser parser = new JsonParser();

        if (ingredients) {
            JsonArray results = parser.parse(content.toString()).getAsJsonArray();
            for (JsonElement value : results) {
                responseList.add(value.toString().replace("\"", ""));
            }
        }
        else {
            JsonObject resultsObject = parser.parse(content.toString()).getAsJsonObject();
            JsonArray results = resultsObject.getAsJsonArray("suggestions");
            for (JsonElement value : results) {
                responseList.add(value.toString().replace("\"", ""));
            }
        }
        return responseList;
    }
}
