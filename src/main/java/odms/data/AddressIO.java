package odms.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import odms.enums.CountriesEnum;

public class AddressIO {
    private static final  String API_URL = "https://maps.googleapis.com/maps/api/";
    private static String key;

    private AddressIO() {
        throw new UnsupportedOperationException();
    }

    public static boolean checkValidCountry(String address, List<CountriesEnum> validCountries) {
        // TODO either use one key or come up with a way to use a few
        key = "AIzaSyCfq6coJWIFGQusltLJCA8tZMt9cjouzLw";
        String query = API_URL +
                "geocode/json?address=" +
                address.replace(" ","+") +
                "&key=" + key;
        System.out.println(query);
        try {
            URL url = new URL(query);
            URLConnection request = url.openConnection();
            request.connect();
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject rootobj = root.getAsJsonObject();
            String jsonString = rootobj.toString();
            for(CountriesEnum c: validCountries) {
                if(jsonString.contains(c.name())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Invalid Address");
            return false;
        }

    }

}
