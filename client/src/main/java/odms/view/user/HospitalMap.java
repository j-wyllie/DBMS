package odms.view.user;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.*;
import com.lynden.gmapsfx.shapes.Polyline;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import odms.commons.model.locations.Hospital;
import odms.commons.model.user.User;
import odms.controller.AlertController;
import odms.data.GoogleDistanceMatrix;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static odms.controller.user.AvailableOrgans.msToStandard;

@Slf4j
public class HospitalMap implements Initializable, MapComponentInitializedListener, DirectionsServiceCallback {

    private odms.controller.user.HospitalMap controller = new odms.controller.user.HospitalMap();
    private User currentUser;

    private DirectionsService directionsService;
    private DirectionsPane directionsPane;
    private DirectionsRenderer directionsRenderer = null;

    private List<Hospital> hospitalList;

    protected StringProperty origin = new SimpleStringProperty();
    protected StringProperty destination = new SimpleStringProperty();

    private int numCustomMarkers = 0;

    private Location userLocation;
    private Marker userLocationMarker;

    private Hospital hospitalSelected1;
    private Hospital hospitalSelected2;

    private DecimalFormat decimalFormat = new DecimalFormat("####0.00");
    private Polyline helicopterRoute;

    private GoogleMap map;
    private List<Marker> markers;

    @FXML
    private GoogleMapView mapView;

    @FXML
    private TableView markersTable;

    @FXML
    private TextArea travelInfo;

    @FXML
    private ComboBox travelMethod;

    @FXML
    private Button findClosestHospitalBtn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mapView.addMapInializedListener(this);
        hospitalList = new ArrayList<>();
        markers = new ArrayList<>();

        if (hospitalSelected1 != null && hospitalSelected2 != null) {
            createRouteBetweenLocations(
                    hospitalSelected1.getLatitude(), hospitalSelected1.getLongitude(), hospitalSelected1.getName(),
                    hospitalSelected2.getLatitude(), hospitalSelected2.getLongitude(), hospitalSelected2.getName());
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

        // Creates a hospital object for a custom location added by the user, is cleared using the clear button
        // TODO do we limit it to only one custom marker allowed on the map?
        map.addMouseEventHandler(UIEventType.dblclick, (GMapMouseEvent e) -> {
            numCustomMarkers += 1;

            Hospital customLocation = new Hospital("custom marker " + (numCustomMarkers) +
                    " (" + Double.valueOf(decimalFormat.format(e.getLatLong().getLatitude())) + ", " +
                    Double.valueOf(decimalFormat.format(e.getLatLong().getLongitude())) + ")",
                    e.getLatLong().getLatitude(), e.getLatLong().getLongitude(), null, - numCustomMarkers);
            addHospitalMarker(customLocation);
            travelInfo.setText("Created: " + customLocation.getName());
            hospitalList.add(customLocation);
            setMarkersTable();
        });

        setUsersLocation();
        populateHospitals();
        setMarkersTable();
        map.addMarker(userLocationMarker);

    }

    private void setUsersLocation() {

        // Get the users IP address
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
            System.out.println("IP Address:- " + inetAddress.getHostAddress());
            System.out.println("Host Name:- " + inetAddress.getHostName());
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }

        // Get the location related to the IP address
        LookupService cl = null;
        try {
            cl = new LookupService("client/src/main/resources/geolite/GeoLiteCity.dat",
                    LookupService.GEOIP_MEMORY_CACHE | LookupService.GEOIP_CHECK_CACHE);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        userLocation = cl.getLocation("27.127.223.255");
//        userLocation = cl.getLocation(inetAddress.getHostAddress());   // TODO this is the proper way to do it, just sometimes cant get the location

        if (userLocation == null) {
            // Couldn't find a location for that IP address
            findClosestHospitalBtn.setDisable(true);
        } else {
            findClosestHospitalBtn.setDisable(false);

            System.out.println(userLocation);
            System.out.println(userLocation.countryName);
            System.out.println(userLocation.longitude);
            System.out.println(userLocation.latitude);
            System.out.println(userLocation.city);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLong(userLocation.latitude, userLocation.longitude));
            markerOptions.label("You");

            userLocationMarker = new Marker(markerOptions);

            travelInfo.setText("Your approximate location: " + userLocation.latitude + ", " + userLocation.longitude);
        }
    }

    /**
     * Populates the markers table with the current locations available to the map
     */
    private void setMarkersTable() {

        TableColumn<Hospital, String> nameColumn = new TableColumn<>(
                "Location"
        );
        nameColumn.setCellValueFactory(
                cdf -> new SimpleStringProperty(
                        cdf.getValue().getName()
                )
        );
        nameColumn.setMaxWidth(150);

        TableColumn<Hospital, String> idColumn = new TableColumn<>(
                "ID"
        );
        idColumn.setCellValueFactory(
                cdf -> new SimpleStringProperty(
                        String.valueOf(cdf.getValue().getId())
                )
        );


//        TableColumn<Map.Entry<Marker, Hospital>, String> nameColumn = new TableColumn<>(
//                "Name"
//        );
//        nameColumn.setCellValueFactory(
//                cdf -> new SimpleStringProperty(
//                        cdf.getValue().getValue().getName()
//                )
//        );
//
//
//        TableColumn<Map.Entry<Marker, Hospital>, String> idColumn = new TableColumn<>(
//                "ID"
//        );
//        idColumn.setCellValueFactory(
//                cdf -> new SimpleStringProperty(
//                        String.valueOf(cdf.getValue().getValue().getId())
//                )
//        );

        markersTable.getColumns().clear();
        markersTable.getColumns().add(nameColumn);
        markersTable.getColumns().add(idColumn);

        markersTable.setOnMousePressed(event -> {
                    if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                            markersTable.getSelectionModel().getSelectedItem() != null) {

//                         Marker selectedMarker;
                        Hospital selectedLocation;

//                        selectedMarker = (Marker) markersTable.getSelectionModel().getSelectedItem();
                        selectedLocation = (Hospital) markersTable.getSelectionModel().getSelectedItem();

//                        MarkerOptions markerOptions = new MarkerOptions();
//                        markerOptions.colour();
//                        selectedMarker.setOptions();

                        // Center on selected marker
                        map.setCenter(new LatLong(selectedLocation.getLatitude(), selectedLocation.getLongitude()));
                        map.setZoom(10);

                    }
                });


        markersTable.getItems().clear();
        markersTable.setItems(FXCollections.observableArrayList(hospitalList));

    }

    @FXML
    private void handleShowClosestHospital(ActionEvent event) {

        Hospital closest = null;
        Double distance = Double.POSITIVE_INFINITY;
        Double temp;
        for (Hospital location : hospitalList) {
            temp = controller.calcDistanceHaversine(location.getLatitude(), location.getLongitude(), userLocation.latitude, userLocation.longitude);
            if (temp < distance) {
                distance = temp;
                closest = location;
            }
        }

        if (closest != null) {
            map.setCenter(new LatLong(closest.getLatitude(), closest.getLongitude()));
            map.setZoom(8);

//            travelInfo.setText("Closest hospital to " + userLocation.countryName + ", " +  userLocation.city + ", " +
//                    userLocation.region + ": " + closest.getName() + ", " + closest.getAddress() +
//                    ".\n Approximately " + decimalFormat.format(distance) + "km away.");

            travelInfo.setText("Closest hospital to you: " + closest.getName() + ", " + closest.getAddress() +
                    ".\n Approximately " + decimalFormat.format(distance) + "km away.");

            }


    }


    @FXML
    private void handleShowHelp(ActionEvent event) {

        String helpText = "Welcome! \n" +
                "To route between two hospitals, click on each and a route will appear, " +
                "to change the travel method use the dropdown menu. \n" +
                "To add a custom marker, double click on the map where you want the marker, " +
                "this marker can be used just like a hospital. \n" +
                "To add a hospital to the map, click 'Add Hospital," +
                "and fill out the required info'";
        AlertController.guiPopupInfo(helpText);
    }

    /**
     * Clears all routes from map, also 'deselects' any hospitals, accessed via button.
     *
     * @param event Event of clear button being clicked
     */
    @FXML
    private void handleClearRoutesButtonClicked(ActionEvent event) {
        clearRoutesAndSelection();
    }

    /**
     * Clears all routes from map, also 'deselects' any hospitals.
     */
    private void clearRoutesAndSelection() {
        populateHospitals();

        hospitalSelected1 = null;
        hospitalSelected2 = null;

        if(helicopterRoute != null) {
            map.removeMapShape(helicopterRoute);
        }

        travelInfo.clear();

        setMarkersTable();

        setUsersLocation();

        directionsRenderer.clearDirections();
        directionsRenderer = new DirectionsRenderer(true, mapView.getMap(), directionsPane);
    }

    /**
     * Creates route between two given locations and displays on given map.
     *
     * @param originLat latitude the route starts from
     * @param originLong latitude the route starts from
     * @param originName name of the location the route starts from
     * @param destinationLat latitude the route ends at
     * @param destinationLong longitude the route ends at
     * @param destinationName name of the location the route ends at
     */
    private void createRouteBetweenLocations(Double originLat, Double originLong, String originName, Double destinationLat, Double destinationLong, String destinationName) {
        final double HELICOPTER_SPEED_KMH = 222;

        if (hospitalSelected1.getId() == hospitalSelected2.getId()) {
            clearRoutesAndSelection();
            return;
        }

        boolean isCarTrip = false;
        if (travelMethod.getSelectionModel().getSelectedItem().equals("Car")) {
            isCarTrip = true;
        }

        if(isCarTrip) {

            String originLatLong = originLat + "," + originLong;
            String destinationLatLong = destinationLat + "," + destinationLong;

            DirectionsRequest directionsRequest = new DirectionsRequest(originLatLong, destinationLatLong, TravelModes.DRIVING);
            directionsService.getRoute(directionsRequest, this, directionsRenderer);

        } else {

            Double distance = controller.calcDistanceHaversine(originLat, originLong, destinationLat, destinationLong); // km
            Double duration = distance / HELICOPTER_SPEED_KMH * 3600; // seconds
            showTravelDetails(originName, destinationName, decimalFormat.format(distance) + "km", decimalFormat.format(duration));

            if(helicopterRoute != null) {
                map.removeMapShape(helicopterRoute);
            }

            helicopterRoute = controller.createHelicopterRoute(originLat, originLong, destinationLat, destinationLong);
            map.addMapShape(helicopterRoute);
        }
    }


    /**
     * Called when a directions result returns, displays results of the request if the request was successful.
     *
     * @param directionsResult The returned direction result
     * @param directionStatus  Status of request
     */
    @Override
    public void directionsReceived(DirectionsResult directionsResult, DirectionStatus directionStatus) {

        DirectionsLeg ourRoute = directionsResult.getRoutes().get(0).getLegs().get(0);

        String originName = hospitalSelected1.getName();
        String destinationName = hospitalSelected2.getName();

        Double orignLat = hospitalSelected1.getLatitude();
        Double orignLong = hospitalSelected1.getLongitude();
        Double destinationLat = hospitalSelected2.getLatitude();
        Double destinationLong = hospitalSelected2.getLongitude();

        String time;
        try {
            // Using the google distance matrix API
            time = decimalFormat.format(new GoogleDistanceMatrix().getDuration(
                    orignLat, orignLong, destinationLat, destinationLong));
        } catch (IOException e) {
            log.error("Invalid duration for travel, duration set to string: 'NA' ");
            log.error(e.getMessage(), e);
            time = "NA";
        }

        showTravelDetails(originName, destinationName, ourRoute.getDistance().getText(), time);
    }

    /**
     * Displays information about a given route/journey.
     *
     * @param originName The name of the location at the start of the journey
     * @param destinationName The name of the location at the end of the journey
     * @param distance The distance between the hospitals on the given route in km
     * @param duration The time between the two hospitals on the given route in seconds
     */
    private void showTravelDetails(String originName, String destinationName, String distance, String duration) {
        String travelMethodGiven = String.valueOf(travelMethod.getSelectionModel().getSelectedItem());

        try {
            double durationNumber = (Double.parseDouble(duration) * 1000);
            duration = msToStandard((long) durationNumber);
        } catch (Exception e) {
            log.error("Failed to parse travel duration, must not be applicable");
            log.error(e.getMessage(), e);
        }

        String travel = travelMethodGiven + " journey between " + originName + ", and " + destinationName + ".\n" +
                "Distance: " + distance + "\n Travel Time: " + duration;

        travelInfo.setText(travel);
    }

    @FXML
    public void handleAddHospital(ActionEvent event) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/HospitalCreate.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());
            HospitalCreate hospitalCreate = fxmlLoader.getController();
            hospitalCreate.initialize();

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Add new Hospital");
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.centerOnScreen();
            stage.setOnHiding(ob -> populateHospitals());
            stage.show();
        } catch (IOException e) {
            log.error("Failed to populate hospitals");
            log.error(e.getMessage(), e);
        }

    }

    /**
     * Adds a given location to the map object, as a marker with a tooltip containing location details.
     *
     * @param location location to be to added to the map
     */
    public void addHospitalMarker(Hospital location) {

        Marker marker = controller.createHospitalMarker(location);
        markers.add(marker);
        InfoWindow infoWindow = controller.createHospitalInfoWindow(location);

        // For displaying info window
        map.addUIEventHandler(marker, UIEventType.rightclick, (JSObject obj) ->
                infoWindow.open(map, marker)
        );

        map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {

            travelInfo.setText("Selected " + location.getName());

            if (hospitalSelected1 == null) {
                hospitalSelected1 = location;
                if (hospitalSelected2 != null) {
                    createRouteBetweenLocations(
                            hospitalSelected1.getLatitude(), hospitalSelected1.getLongitude(), hospitalSelected1.getName(),
                            hospitalSelected2.getLatitude(), hospitalSelected2.getLongitude(), hospitalSelected2.getName()
                    );
                }

            } else {
                hospitalSelected2 = location;
                createRouteBetweenLocations(
                        hospitalSelected1.getLatitude(), hospitalSelected1.getLongitude(), hospitalSelected1.getName(),
                        hospitalSelected2.getLatitude(), hospitalSelected2.getLongitude(), hospitalSelected2.getName()
                );
            }
        });

        map.addMarker(marker);
    }

    /**
     * Populates the map object with all hospitals in database, removes all existing hospitals
     * and markers.
     */
    private void populateHospitals() {

        for(Marker marker : markers) {
            map.removeMarker(marker);
        }
        hospitalList.clear();
        markers.clear();
        hospitalList = controller.getHospitals();

        // TODO
        // TEMPORARY, used for testing, just adds random hospitals created here to the map, until we have more hospitals in database
        Hospital hospitalTest = new Hospital("HospitalTest1", -39.07, 174.05, null, 10);
        Hospital hospitalTest2 = new Hospital("HospitalTest2", -40.57, 175.27, null, 13);
        Hospital hospitalTest3 = new Hospital("HospitalTest3", -38.23, 177.31, null, 15);
        Hospital hospitalTest4 = new Hospital("HospitalTest4", -38.20, 177.31, null, 16);
        hospitalList.add(hospitalTest);
        hospitalList.add(hospitalTest2);
        hospitalList.add(hospitalTest3);
        hospitalList.add(hospitalTest4);
        // ------------------------------------------------------------------------------------------------------------

        for (Hospital hospital : hospitalList) {
            addHospitalMarker(hospital);
        }

    }

    @FXML
    private void handleTravelMethodToggled(ActionEvent event) {

        if (hospitalSelected1 != null && hospitalSelected2 != null) {
            if (helicopterRoute != null) {
                map.removeMapShape(helicopterRoute);
            }
            directionsRenderer.clearDirections();
            directionsRenderer = new DirectionsRenderer(true, mapView.getMap(), directionsPane);

            createRouteBetweenLocations(
                    hospitalSelected1.getLatitude(), hospitalSelected1.getLongitude(), hospitalSelected1.getName(),
                    hospitalSelected2.getLatitude(), hospitalSelected2.getLongitude(), hospitalSelected2.getName()
            );
        }
    }
}
