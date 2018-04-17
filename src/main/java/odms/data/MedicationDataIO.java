package odms.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MedicationDataIO {

    public static List<String> GetSuggestionList(String substring) {
        List<String> suggestionList = Arrays.asList();
        try {
            String urlString = String.format("mapi-us.iterar.co/api/autocomplete?query=%s", substring);
            URL url = new URL(urlString);
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
            suggestionList = ParseJSON(responseContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return suggestionList;
    }

    private static List<String> ParseJSON(StringBuffer content) {
        List<String> suggestionList = Arrays.asList();
        JsonObject jsonContent = new JsonObject();
        jsonContent.getAsJsonObject(content.toString());

        JsonArray suggestions = jsonContent.getAsJsonArray("suggestions");
        for (JsonElement value : suggestions) {
            suggestionList.add(value.toString());
        }
        return suggestionList;
    }
}
