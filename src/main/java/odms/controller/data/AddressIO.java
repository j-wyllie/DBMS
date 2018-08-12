package odms.controller.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AddressIO {
    private static final  String API_URL = "https://maps.googleapis.com/maps/api/";
    private static String key;

    private AddressIO() {
        throw new UnsupportedOperationException();
    }

    public static boolean checkValidCountry(String address, String country) {
        try {
            address = address.toLowerCase();
            address = address.replace("road","rd");
            address = address.replace("street","st");
            String jsonString = getGeocodeLocation(address,country.replace(" ","+")).toString();
            return jsonString.toLowerCase().contains(address.toLowerCase());
        } catch (Exception e) {
            System.out.println("Invalid Address");
            return false;
        }
    }

    public static boolean checkValidRegion(String address, String region, String country) {
        try {
            JsonObject jsonString = getGeocodeLocation(address, country.replace(" ","+"));
            return jsonString.getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonArray(
                    "address_components").get(0).getAsJsonObject().toString().contains(region);
        } catch (IOException | IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean checkValidCity(String address, String city, String country) {
        try {
            JsonObject jsonString = getGeocodeLocation(address, country.replace(" ","+"));
            return jsonString.getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonArray(
                    "address_components").get(0).getAsJsonObject().toString().contains("locality");
        } catch (IOException | IndexOutOfBoundsException e) {
            return false;
        }
    }

    private static JsonObject getGeocodeLocation(String address, String country) throws IOException{
        key = "AIzaSyCfq6coJWIFGQusltLJCA8tZMt9cjouzLw";

        String query = API_URL +
                "geocode/json?address=" +
                address.replace(" ","+") +
                "&components=country:"+country+"&key=" + key;
        URL url = new URL(query);
        URLConnection request = url.openConnection();
        request.connect();
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject rootobj = root.getAsJsonObject();
        return rootobj;
    }

}
