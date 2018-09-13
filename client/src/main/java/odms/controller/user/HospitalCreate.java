package odms.controller.user;

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

/**
 * Controller class for create hospital scene.
 */
public class HospitalCreate {

    private static final String API_URL = "https://maps.googleapis.com/maps/api/";
    private static String key;

    private final odms.view.user.HospitalCreate view;

    public HospitalCreate(odms.view.user.HospitalCreate view) {
        this.view = view;
    }

    public void addHospital(String name, String address, Double lat, Double lon) throws IOException, SQLException {
        getGeoLocation(address, lat, lon);

        Hospital newHospital = new Hospital(name, lat, lon, address);

        HospitalDAO hospitalDAO = DAOFactory.getHospitalDAO();
        hospitalDAO.add(newHospital);
    }

    private void getGeoLocation(String address, Double lat, Double lon) throws IOException {


        String query = API_URL + buildGMapsURL(address, lat, lon);
        URL url = new URL(query);
        URLConnection request = url.openConnection();

        System.out.println(query);

        request.connect();
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject rootobj = root.getAsJsonObject();
    }

    private String buildGMapsURL(String address, Double lat, Double lon) {
        key = "AIzaSyCfq6coJWIFGQusltLJCA8tZMt9cjouzLw";

        if (address == null) {
            return API_URL + "geocode/json?latlng=" + lat + "," + lon + "&key=" + key;
        } else {
            return API_URL + "geocode/json?address=" + address + "&key=" + key;
        }
    }
}
