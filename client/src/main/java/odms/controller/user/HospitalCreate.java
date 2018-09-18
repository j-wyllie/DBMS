package odms.controller.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import odms.commons.model.locations.Hospital;
import odms.controller.database.DAOFactory;
import odms.controller.database.locations.HospitalDAO;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for create hospital scene.
 */
public class HospitalCreate {

    private static final String API_URL = "https://maps.googleapis.com/maps/api/";
    private static final String KEY = "AIzaSyCfq6coJWIFGQusltLJCA8tZMt9cjouzLw";

    public void addHospital(String name, String address) throws IOException, SQLException {
        List<Double> latlong = getGeoLocation(address);
        Hospital newHospital = new Hospital(name, latlong.get(0), latlong.get(1), address);
        HospitalDAO hospitalDAO = DAOFactory.getHospitalDAO();
        hospitalDAO.add(newHospital);
    }

    private List<Double> getGeoLocation(String address) throws IOException {
        List<Double> locationList = new ArrayList<>();
        String query = API_URL + "geocode/json?address=" + address.replace(" ", "+") + "&key=" + KEY;
        URL url = new URL(query);
        URLConnection request;

        try {
            request = url.openConnection();
            request.connect();
        } catch (IOException e) {
            throw new IOException("Couldn't validate address");
        }

        try {
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonArray results = root.getAsJsonObject().getAsJsonArray("results");
            JsonObject location = results.get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject();
            locationList.add(Double.parseDouble(location.get("lat").getAsString()));
            locationList.add(Double.parseDouble(location.get("lng").getAsString()));
        } catch (Exception e) {
            throw new IOException("Invalid Address");
        }

        return locationList;
    }
}
