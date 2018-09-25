package odms.controller.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.control.RadioButton;
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

    /**
     * Makes a request to add a new hospital to the database.
     * @param name name of hospital
     * @param address address of hospital
     * @param organPrograms list of radio button objects that represent what organ transplant
     * programs a hospital provides
     * @throws IOException thrown if there is an error validating the address
     * @throws SQLException thrown if there is a database error
     */
    public void addHospital(String name, String address, List<RadioButton> organPrograms)
            throws IOException, SQLException {
        List<Boolean> organProgramBools = new ArrayList<>();
        for (RadioButton organProgram : organPrograms) {
            organProgramBools.add(organProgram.isSelected());
        }
        List<Double> latlong = getGeoLocation(address);
        Hospital newHospital = new Hospital(name, latlong.get(0), latlong.get(1), address,
                organProgramBools);
        HospitalDAO hospitalDAO = DAOFactory.getHospitalDAO();
        hospitalDAO.add(newHospital);
    }

    /**
     * Makes a request to edit an existing hospital in the database.
     * @param name name of hospital
     * @param address address of hospital
     * @param hospitalId the id of the hospital that has been edited
     * @param organPrograms list of radio button objects that represent what organ transplant
     * programs a hospital provides
     * @throws IOException thrown if there is an error validating the address
     * @throws SQLException thrown if there is a database error
     */
    public void editHospital(String name, String address, List<RadioButton> organPrograms,
            Integer hospitalId) throws SQLException, IOException {
        List<Boolean> organProgramBools = new ArrayList<>();
        for (RadioButton organProgram : organPrograms) {
            organProgramBools.add(organProgram.isSelected());
        }
        List<Double> latlong = getGeoLocation(address);
        Hospital newHospital = new Hospital(name, latlong.get(0), latlong.get(1), address,
                organProgramBools, hospitalId);
        HospitalDAO hospitalDAO = DAOFactory.getHospitalDAO();
        hospitalDAO.edit(newHospital);
    }

    /**
     * Gets the lat and long of hospital address, this also validated the address is real.
     * @param address address to be geolocated
     * @return List that contains the lat and long of the address
     * @throws IOException thrown if there is an error validating the address
     */
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
