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
            if(jsonString.toLowerCase().contains(address.toLowerCase())) {
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Invalid Address");
            return false;
        }
    }

    public static boolean checkValidRegion(String address, String region, String country) {
        try {
            String jsonString = getGeocodeLocation(address, country.replace(" ","+")).toString();
            System.out.println(region);
            if(jsonString.contains(region)) {
                System.out.println("A");
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Invalid Address");
            return false;
        }
    }

    public static boolean checkValidCity(String address, String city, String country) {
        try {
            String jsonString = getGeocodeLocation(address, country.replace(" ","+")).toString();
            if(jsonString.contains(city)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Invalid Address");
            return false;
        }
    }

    public static JsonObject getGeocodeLocation(String address, String country) throws IOException{
        // TODO either use one key or come up with a way to use a few
        key = "AIzaSyCfq6coJWIFGQusltLJCA8tZMt9cjouzLw";

        String query = API_URL +
                "geocode/json?address=" +
                address.replace(" ","+") +
                "&components=country:"+country+"&key=" + key;
        System.out.println(query);
        URL url = new URL(query);
        URLConnection request = url.openConnection();
        request.connect();
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject rootobj = root.getAsJsonObject();
        return rootobj;
    }

}
