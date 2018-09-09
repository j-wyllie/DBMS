package odms.view.user;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tooltip;
import odms.commons.model.locations.Hospital;
import odms.commons.model.user.User;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class HospitalMap implements Initializable, MapComponentInitializedListener {

    private odms.controller.user.HospitalMap controller = new odms.controller.user.HospitalMap();
    private User currentUser;
    private Marker currentHospitalMarker;

    @FXML
    private GoogleMapView mapView;


    private GoogleMap map;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapView.addMapInializedListener(this);
    }

    public void initialize(User currentUser) {
        this.currentUser = currentUser;
        controller.setView(this);
    }

    @Override
    public void mapInitialized() {

        //Set the initial properties of the map.
        MapOptions mapOptions = new MapOptions();

        mapOptions.center(new LatLong(-41, 172.6362))
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .zoom(5);

        map = mapView.createMap(mapOptions);
    }

    @FXML
    public void handleAddHospitalMarker(ActionEvent event) {

        // TEMPORARY, used for testing, just adds a random hospital created here to the map

        Hospital hospitalTest = new Hospital("HospitalTes", -39.07, 174.05, null, 10);
        addHospitalMarker(hospitalTest);
    }

    /**
     * Adds a given hospital to the map object, as a marker with a tooltip containing hospital details
     *
     * @param hospital hospital to add to the map
     */
    public void addHospitalMarker(Hospital hospital){

        Marker marker = controller.createHospitalMarker(hospital);
        InfoWindow infoWindow = controller.createHospitalInfoWindow(hospital);

        // TODO only show tooltip when mouse is hovered over marker or clicked on
        infoWindow.open(map, marker);
        map.addMarker(marker);
    }

    /**
     * Populates the map object with ALL hospitals in database
     */
    public void populateHospitals() {
        ArrayList<Hospital> hospitals = new ArrayList<>();

        // use to be created getAllHospitals method here

        for (Hospital hospital : hospitals) {
            addHospitalMarker(hospital);
        }
    }

    public void populateHospitalsWithinDistance(LatLong latLong, int distance) {
        ArrayList<Hospital> hospitalsNear = new ArrayList<>();

        // use to be created getAllHospitalsNear method here, populates map only with hospitals within distance kms of the given location
        controller.getHospitalsWithinDistance(latLong, distance);

        for (Hospital hospital : hospitalsNear) {
            addHospitalMarker(hospital);
        }
    }

    /**
     * Displays the distance in km and the time between two given hospitals
     *
     * @param hospital1 hospital one
     * @param hospital2 hospital two
     */
    public void showTimeDistBetweenHospitals(Hospital hospital1, Hospital hospital2) {

    }

}
