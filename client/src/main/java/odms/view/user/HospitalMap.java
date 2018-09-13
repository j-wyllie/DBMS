package odms.view.user;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.*;
import com.lynden.gmapsfx.shapes.Polyline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import netscape.javascript.JSObject;
import odms.commons.model.locations.Hospital;
import odms.commons.model.user.User;

import javafx.event.ActionEvent;
import odms.data.GoogleDistanceMatrix;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static odms.controller.user.AvailableOrgans.msToStandard;


public class HospitalMap implements Initializable, MapComponentInitializedListener, DirectionsServiceCallback {

    private odms.controller.user.HospitalMap controller = new odms.controller.user.HospitalMap();
    private User currentUser;

    private DirectionsService directionsService;
    private DirectionsPane directionsPane;
    private DirectionsRenderer directionsRenderer = null;

    private ArrayList<Hospital> hospitalArrayList = new ArrayList<>();

    protected StringProperty origin = new SimpleStringProperty();
    protected StringProperty destination = new SimpleStringProperty();

    private Hospital hospitalSelected1;
    private Hospital hospitalSelected2;

    private DecimalFormat decimalFormat = new DecimalFormat("####0.00");
    private Polyline helicopterRoute;

    private GoogleMap map;

    @FXML
    private GoogleMapView mapView;

    @FXML
    private TextArea travelInfo;

    @FXML
    private ComboBox travelMethod;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapView.addMapInializedListener(this);

        if (hospitalSelected1 != null && hospitalSelected2 != null) {
            createRouteBetweenHospitals(hospitalSelected1, hospitalSelected2);
        }

        ObservableList<String> travelMethods = FXCollections.observableArrayList("Car", "Helicopter");
        travelMethod.setItems(travelMethods);
        travelMethod.setValue("Car");
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

        travelInfo.setWrapText(true);

        map = mapView.createMap(mapOptions, false);

        directionsService = new DirectionsService();
        directionsPane = mapView.getDirec();
        directionsRenderer = new DirectionsRenderer(true, mapView.getMap(), directionsPane);

        populateHospitals();
    }


    /**
     * Clears all routes from map, also 'deselects' any hospitals, accessed via button
     *
     * @param event Event of clear button being clicked
     */
    @FXML
    private void handleClearRoutesButtonClicked(ActionEvent event) {
        clearRoutesAndSelection();
    }

    /**
     * Clears all routes from map, also 'deselects' any hospitals
     */
    private void clearRoutesAndSelection() {
        hospitalSelected1 = null;
        hospitalSelected2 = null;

        if(helicopterRoute != null) {
            map.removeMapShape(helicopterRoute);
        }

        travelInfo.clear();

        directionsRenderer.clearDirections();
        directionsRenderer = new DirectionsRenderer(true, mapView.getMap(), directionsPane);
    }

    /**
     * Creates route between two given hospitals and displays on given map
     *
     * @param origin hospital the route starts from
     * @param destination hospital the route ends at
     */
    private void createRouteBetweenHospitals(Hospital origin, Hospital destination) {
        final double HELICOPTER_SPEED_KMH = 222;

        // clearRoutesAndSelection();

        boolean isCarTrip = false;
        if (travelMethod.getSelectionModel().getSelectedItem().equals("Car")) {
            isCarTrip = true;
        }

        if(isCarTrip) {

            String originLatLong = origin.getLatitude().toString() + "," + origin.getLongitude().toString();
            String destinationLatLong = destination.getLatitude().toString() + "," + destination.getLongitude().toString();

            DirectionsRequest directionsRequest = new DirectionsRequest(originLatLong, destinationLatLong, TravelModes.DRIVING);
            directionsService.getRoute(directionsRequest, this, directionsRenderer);

        } else {

            Double distance = controller.calcDistanceHaversine(origin.getLatitude(), origin.getLongitude(), destination.getLatitude(), destination.getLongitude()); // km
            Double duration = distance / HELICOPTER_SPEED_KMH * 3600; // seconds
            showTravelDetails(origin, destination, decimalFormat.format(distance) + "km", decimalFormat.format(duration));

            helicopterRoute = controller.createHelicopterRoute(origin, destination);

            map.addMapShape(helicopterRoute);
        }
    }


    /**
     * Called when a directions result returns, displays results of the request if the request was successful
     *
     * @param directionsResult The returned direction result
     * @param directionStatus  Status of request
     */
    @Override
    public void directionsReceived(DirectionsResult directionsResult, DirectionStatus directionStatus) {

        DirectionsLeg ourRoute = directionsResult.getRoutes().get(0).getLegs().get(0);

        String time;
        try {
            // Using the google distance matrix API
            time = decimalFormat.format(new GoogleDistanceMatrix().getDuration(hospitalSelected1, hospitalSelected2));
        } catch (IOException e) {
            e.printStackTrace();
            time = "NA";
        }

        showTravelDetails(hospitalSelected1, hospitalSelected2, ourRoute.getDistance().getText(), time);
    }

    /**
     * Displays information about a given route/journey
     *
     * @param hospital1 The hospital at the start of the journey
     * @param hospital2 The hospital at the end of the journey
     * @param distance The distance between the hospitals on the given route in km
     * @param duration The time between the two hospitals on the given route in seconds
     */
    private void showTravelDetails(Hospital hospital1, Hospital hospital2, String distance, String duration) {
        String travelMethodGiven = String.valueOf(travelMethod.getSelectionModel().getSelectedItem());

        try {
            double durationNumber = (Double.parseDouble(duration) * 1000);
            duration = msToStandard((long) durationNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String travel = travelMethodGiven + " journey between " + hospital1.getName() + " and " + hospital2.getName() + ":\n" +
                "Distance: " + distance + "\n Travel Time: " + duration;
        travelInfo.setText(travel);
    }

    @FXML
    public void handleAddHospitalMarker(ActionEvent event) {

        // TEMPORARY, used for testing, just adds a random hospital created here to the map

        Hospital hospitalTest = new Hospital("HospitalTest1", -39.07, 174.05, null, 10);
        Hospital hospitalTest2 = new Hospital("HospitalTest2", -40.57, 175.27, null, 13);
        Hospital hospitalTest3 = new Hospital("HospitalTest3", -38.23, 177.31, null, 15);
        Hospital hospitalTest4 = new Hospital("HospitalTest4", -38.20, 177.31, null, 16);

        addHospitalMarker(hospitalTest);
        addHospitalMarker(hospitalTest2);
        addHospitalMarker(hospitalTest3);
        addHospitalMarker(hospitalTest4);

    }

    /**
     * Adds a given hospital to the map object, as a marker with a tooltip containing hospital details
     *
     * @param hospital hospital to add to the map
     */
    public void addHospitalMarker(Hospital hospital){

        Marker marker = controller.createHospitalMarker(hospital);
        InfoWindow infoWindow = controller.createHospitalInfoWindow(hospital);

        map.addUIEventHandler(marker, UIEventType.rightclick, (JSObject obj) -> {
            infoWindow.open(map, marker);
        });


        map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {

            travelInfo.setText("Hospital " + hospital.getId() + " selected");

            if (hospitalSelected1 == null) {
                hospitalSelected1 = hospital;
                if (hospitalSelected2 != null) {
                    createRouteBetweenHospitals(hospitalSelected1, hospitalSelected2);
                }

            } else {
                hospitalSelected2 = hospital;
                if (hospitalSelected1 != null) {
                    createRouteBetweenHospitals(hospitalSelected1, hospitalSelected2);
                }
            }

            System.out.println("HospitalSelectedOne: " + hospitalSelected1.getId());
            System.out.println("HospitalSelectedTwo: " + hospitalSelected2.getId());

        });

        map.addMarker(marker);
    }

    /**
     * Populates the map object with all hospitals in database
     */
    public void populateHospitals() {
        ArrayList<Hospital> hospitals = new ArrayList<>();

        // use to be created getAllHospitals method here TODO

        for (Hospital hospital : hospitals) {
            addHospitalMarker(hospital);
        }
    }

}
