package odms.view.user;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.*;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
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
import odms.controller.database.DAOFactory;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;


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
     * Clears all routes from map, just initializes map again on click of clear routes button
     *
     * @param event Event of clear button being clicked
     */
    @FXML
    private void handleClearRoutesButtonClicked(ActionEvent event) {
        // TODO instead of initializing the map again just remove routes?

        if(helicopterRoute != null) {
            map.removeMapShape(helicopterRoute);
        }

        hospitalSelected1 = null;
        hospitalSelected2 = null;
        travelInfo.clear();

        mapInitialized();

    }

    /**
     * Creates route between two given hospitals and displays on given map
     *
     * @param origin hospital the route starts from
     * @param destination hospital the route ends at
     */
    private void createRouteBetweenHospitals(Hospital origin, Hospital destination) {
        final double HELICOPTER_SPEED_KMH = 222;

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

            if(helicopterRoute != null) {
                map.removeMapShape(helicopterRoute);
            }

            Double distance = controller.calcDistanceHaversine(hospitalSelected1.getLatitude(), hospitalSelected1.getLongitude(), hospitalSelected2.getLatitude(), hospitalSelected2.getLongitude());
            Double duration = distance / HELICOPTER_SPEED_KMH * 60;
            showTravelDetails(hospitalSelected1, hospitalSelected2, decimalFormat.format(distance), decimalFormat.format(duration));

            helicopterRoute = controller.createHelicopterRoute(hospitalSelected1, hospitalSelected2);

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
        String time = "";

        // TODO might have to use google matrix api to get duration
        if (directionStatus == DirectionStatus.OK) {
            System.out.println(ourRoute.getDuration().getText());
            time = String.valueOf(ourRoute.getDuration().getText());
        }

        showTravelDetails(hospitalSelected1, hospitalSelected2, ourRoute.getDistance().getText(), time);
    }

    private void showTravelDetails(Hospital hospital1, Hospital hospital2, String distance, String duration) {
        String travelMethodGiven = String.valueOf(travelMethod.getSelectionModel().getSelectedItem());

        String travel = travelMethodGiven + " journey between " + hospital1.getName() + " and " + hospital2.getName() + ":\n" +
                "Distance: " + distance + "km "+ "\n Travel Time: " + duration + " minutes.";
        travelInfo.setText(travel);
    }


    @FXML
    public void handleAddHospitalMarker(ActionEvent event) {

        // TEMPORARY, used for testing, just adds a random hospital created here to the map

        Hospital hospitalTest = new Hospital("HospitalTest1", -39.07, 174.05, null, 10);
        Hospital hospitalTest2 = new Hospital("HospitalTest2", -40.57, 175.27, null, 13);
        Hospital hospitalTest3 = new Hospital("HospitalTest3", -38.23, 177.31, null, 15);

        addHospitalMarker(hospitalTest);
        addHospitalMarker(hospitalTest2);
        addHospitalMarker(hospitalTest3);

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

            System.out.println("Hospital " + hospital.getId() + " selected");


            if (this.hospitalSelected1 == null) {
                this.hospitalSelected1 = hospital;
                if (hospitalSelected2 != null) {
                    createRouteBetweenHospitals(this.hospitalSelected1, hospitalSelected2);
                }

            } else {
                hospitalSelected2 = hospital;
                if (this.hospitalSelected1 != null) {
                    createRouteBetweenHospitals(this.hospitalSelected1, hospitalSelected2);
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
