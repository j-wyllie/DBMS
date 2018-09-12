package odms.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import odms.commons.model.locations.Hospital;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Queries Google's Distance Matrix API to get distance and duration between two given locations
 */
public class GoogleDistanceMatrix {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&";

    public double getDuration(Hospital origin, Hospital destination) throws IOException {

        String apiKey = "AIzaSyCfq6coJWIFGQusltLJCA8tZMt9cjouzLw";

        URL url = new URL(BASE_URL + "origins=" + origin.getLatitude() + "," + origin.getLongitude() + "&destinations=" +
                destination.getLatitude() + "," + destination.getLongitude() + "&key=" + apiKey);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.setConnectTimeout(2000);
        request.setReadTimeout(2000);
        request.connect();

        JsonParser jp = new JsonParser();
        JsonObject jsonElement = jp.parse(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();

        return jsonElement.get("rows").getAsJsonArray().get(0)
                .getAsJsonObject().get("elements")
                .getAsJsonArray().get(0)
                .getAsJsonObject().get("duration")
                .getAsJsonObject().get("value")
                .getAsDouble();
    }
}
