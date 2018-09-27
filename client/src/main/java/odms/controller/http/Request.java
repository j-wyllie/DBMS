package odms.controller.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import odms.Session;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;

@Slf4j
public class Request {
    private static final Integer PORT = 6969;
    private static final String DOMAIN = "localhost";
    private static final String URL = "http://" + DOMAIN + ":" + PORT + "/api/v1/";

    private static final String TYPE_CONTENT = "Content-Type";
    private static final String TYPE_JSON = "application/json";

    // Base request variables.
    private String urlString;
    private Map<String, Object> queryParams;
    private String body;

    /**
     * Constructor for a request.
     * @param urlString of the request to be made.
     * @param queryParams to be attached to the url.
     * @param body of the request.
     */
    public Request(String urlString, Map<String, Object> queryParams, String body) {
        this.urlString = urlString;
        this.queryParams = queryParams;
        this.body = body;
    }

    /**
     * Constructor for a request that does not require a body.
     * @param urlString of the request to be made.
     * @param queryParams to be attached to the url.
     */
    public Request(String urlString, Map<String, Object> queryParams) {
        this.urlString = urlString;
        this.queryParams = queryParams;
    }

    /**
     * Executes a GET request to the server.
     * @return the response from the server.
     * @throws IOException error.
     */
    public Response get() throws IOException {
        URL url = new URL(constructUrl(this.urlString, this.queryParams));

        //Creating the connection to the server.
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        setHeaders(con);

        String body = execute(con);
        int status = con.getResponseCode();

        con.disconnect();
        return new Response(body, status);
    }

    /**
     * Executes a POST request to the server.
     * @return the response from the server.
     * @throws IOException error.
     */
    public Response post() throws IOException {
        URL url = new URL(constructUrl(this.urlString, this.queryParams));

        //Creating the connection to the server.
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        setHeaders(con);
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        OutputStream output = con.getOutputStream();
        output.write(this.body.getBytes(StandardCharsets.UTF_8));

        String body = execute(con);
        int status = con.getResponseCode();

        con.disconnect();
        return new Response(body, status);
    }

    /**
     * Executes a PATCH request to the server.
     * @return the response from the server.
     * @throws IOException error.
     */
    public Response patch() throws IOException {
        URL url = new URL(constructUrl(this.urlString, this.queryParams));

        //Creating the connection to the server.
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        con.setRequestMethod("POST");
        setHeaders(con);
        con.setDoOutput(true);

        OutputStream output = con.getOutputStream();
        output.write(this.body.getBytes(StandardCharsets.UTF_8));

        String body = execute(con);
        int status = con.getResponseCode();

        con.disconnect();
        return new Response(body, status);
    }

    /**
     * Executes a DELETE request to the server.
     * @return the response from the server.
     * @throws IOException error.
     */
    public Response delete() throws IOException {
        URL url = new URL(constructUrl(this.urlString, this.queryParams));
        //Creating the connection to the server.
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        setHeaders(con);

        String body = execute(con);
        int status = con.getResponseCode();

        con.disconnect();
        return new Response(body, status);
    }

    /**
     * Executes a particular request.
     * @param con the connection to the server.
     * @return the response from the server.
     * @throws IOException error.
     */
    private String execute(HttpURLConnection con) throws IOException {
        con.setConnectTimeout(60000);
        con.setReadTimeout(60000);

        StringBuilder responseContent = new StringBuilder();
        BufferedReader response;
        try {
            response = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        } catch (Exception e) {
            response = new BufferedReader(
                    new InputStreamReader(con.getErrorStream()));
        }
        String line = response.readLine();
        while (line != null) {
            responseContent.append(line);
            line = response.readLine();
        }
        String body = responseContent.toString();

        response.close();
        return body;
    }

    /**
     * Constructs the url string with the query parameters supplied.
     * @param urlString base url.
     * @param queryParams to add to the base url.
     * @return the constructed url.
     */
    private String constructUrl(String urlString, Map<String, Object> queryParams) {
        Object[] keys = queryParams.keySet().toArray();
        for (int i = 0; i < queryParams.size(); i++) {
            String key = (String) keys[i];
            if (i == 0) {
                urlString += ("?" + key + '=' + queryParams.get(key));
            } else {
                urlString += ("&" + key + '=' + queryParams.get(key));
            }
        }
        log.info(urlString);
        return urlString;
    }

    /**
     * Sets the generically required headers for each request made to the server.
     * @param con the connection to the server.
     */
    private void setHeaders(HttpURLConnection con) {
        System.out.println(this.urlString);
        if (!(this.urlString.contains("setup") || this.urlString.contains("login") || this.urlString.contains("create"))) {
            int id = -1;
            UserType userType = Session.getCurrentUser().getValue();
            if (userType == UserType.ADMIN || userType == UserType.CLINICIAN) {
                User user = (User) Session.getCurrentUser().getKey();
                id = user.getId() == null ? 0 : user.getId();
            } else if (userType == UserType.PROFILE || userType == UserType.DONOR) {
                Profile profile = (Profile) Session.getCurrentUser().getKey();
                id = profile.getId();
            }
            con.setRequestProperty("id", String.valueOf(id));
            con.setRequestProperty("UserType", userType.toString());
            con.setRequestProperty("Authorization", String.valueOf(Session.getToken()));
        }
        con.setRequestProperty(TYPE_CONTENT, TYPE_JSON);
    }

    /**
     * Gets the url of the particular request.
     * @return the url string.
     */
    public static String getUrl() {
        return URL;
    }
}
