package odms.controller.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class Request {

    public Response getReguest(String urlString, int token, Map<String, String> headers)
            throws IOException {
        JsonParser parser = new JsonParser();

        URL url = new URL(urlString);
        StringBuffer responseContent = null;

        //Creating the connection to the API server.
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        responseContent = new StringBuffer();
        BufferedReader response = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        String line = response.readLine();
        while (line != null) {
            responseContent.append(line);
            line = response.readLine();
        }

        int status = con.getResponseCode();
        JsonElement body = parser.parse(responseContent.toString());

        response.close();
        con.disconnect();

        return new Response(headers, token, body, status);
    }

    public Response postReguest(String url, int token, Map<String, String> headers) {
        Response response = new Response(null, 0, null, 0);
        return response;
    }

    public Response patchReguest(String url, int token, Map<String, String> headers) {
        Response response = new Response(null, 0, null, 0);
        return response;
    }

    public Response deleteReguest(String url, int token, Map<String, String> headers) {
        Response response = new Response(null, 0, null, 0);
        return response;
    }
}
