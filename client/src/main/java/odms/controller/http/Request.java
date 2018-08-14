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

    private String urlString;
    private int token;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String body;

    public Request(String urlString, int token, Map<String, String> headers,
            Map<String, String> queryParams, String body) {
        this.urlString = urlString;
        this.token = token;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
    }

    public Request(String urlString, int token, Map<String, String> queryParams) {
        this.urlString = urlString;
        this.token = token;
        this.queryParams = queryParams;
    }

    public Response get()
            throws IOException {
        JsonParser parser = new JsonParser();

        URL url = new URL(constructUrl(this.urlString, this.queryParams));
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

        return new Response(this.headers, this.token, body, status);
    }

    public Response post() {
        Response response = new Response(null, 0, null, 0);
        return response;
    }

    public Response patch() {
        Response response = new Response(null, 0, null, 0);
        return response;
    }

    public Response delete() {
        Response response = new Response(null, 0, null, 0);
        return response;
    }

    private String constructUrl(String urlString, Map<String, String> queryParams) {
        Object[] keys = queryParams.keySet().toArray();
        for (int i = 0; i < queryParams.size(); i++) {
            String key = (String) keys[i];
            if (i == 0) {
                urlString += ("?" + key + "=" + queryParams.get(key));
            }
            else {
                urlString += ("&" + key + "=" + queryParams.get(key));
            }
        }
        return urlString;
    }
}
