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
import java.util.Timer;
import java.util.TimerTask;

public class MedicationDataIO {

    private static String prevSubstring;
    private static boolean isDelayed;

    private static void delayRequest(String substring) {
        if (prevSubstring == null) {
            isDelayed = true;
            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            isDelayed = false;
                            prevSubstring = substring;
                            System.out.println(prevSubstring);
                        }
                    }, 5000
            );
        }
    }

    /**
     * Gets a list of suggestions for the clinician user based on a string or substring they
     * have entered into the field.
     * @param substring represents the value to send in the HTTP GET request to the API.
     * @throws IOException
     */
    public static ArrayList<String> getSuggestionList(String substring) throws IOException {
        ArrayList<String> suggestionList = new ArrayList<>();
//        delayRequest(substring);
//        if (!isDelayed) {
//            System.out.println(prevSubstring);
            if (!(substring == null || substring == "")) {
                System.out.println(substring);
                String urlString = String
                        .format("http://mapi-us.iterar.co/api/autocomplete?query=%s", substring);
                URL url = new URL(urlString);

                //Reading the response from the connection.
                StringBuffer response = makeRequest(url);

                //Parsing the list of suggestions from the response.
                suggestionList = parseJSON(response, false);
            }
//            prevSubstring = null;
//        }
        return suggestionList;
    }

    /**
     * Gets a list of active ingredients for the drug selected by the clinician user.
     *
     * @param drugName represents the value to send in the HTTP GET request to the API.
     */
    public static ArrayList<String> getActiveIngredients(String drugName) throws IOException {
        ArrayList<String> activeList = new ArrayList<>();

        if (!(drugName == null || drugName == "")) {
            String urlString = String
                    .format("http://mapi-us.iterar.co/api/%s/substances.json", drugName);
            URL url = new URL(urlString);

            //Reading the response from the connection.
            StringBuffer response = makeRequest(url);

            //Parsing the list of suggestions from the response.
            activeList = parseJSON(response, true);
        }
        return activeList;
    }

    /**
     * Makes a HTTP GET request to the url passed as a parameter and then returns the response.
     *
     * @param url represents the address the HTTP GET request is made to.
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
     * Parse the list of results from the JSON in the HTTP response.
     *
     * @param content represents the buffer holding the JSON response to parse.
     **/
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
}

