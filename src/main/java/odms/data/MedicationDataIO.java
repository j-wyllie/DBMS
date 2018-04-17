package odms.data;

import com.google.gson.Gson;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import netscape.javascript.JSObject;

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

            //Creating the connection to the API server.
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            //Reading the response from the connection.
            StringBuffer responseContent = GetResponse(con);

            //Parsing the list of suggestions from the response.
            suggestionList = ParseJSON(responseContent);
        }
        return suggestionList;
    }


    private static StringBuffer GetResponse(HttpURLConnection con) throws IOException{
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
     * Parse the list of suggestions from the JSON in the HTTP response.
     * @param content represents the buffer holding the JSON response to parse.
     */
    private static ArrayList<String> ParseJSON(StringBuffer content) {
        ArrayList<String> suggestionList = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonObject suggestionObject = parser.parse(content.toString()).getAsJsonObject();

        JsonArray suggestions = suggestionObject.getAsJsonArray("suggestions");
        for (JsonElement value : suggestions) {
            suggestionList.add(value.toString().replace("\"", ""));
        }
        return suggestionList;
    }
}
