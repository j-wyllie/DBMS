package odms.view.user;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.UIEventType;

import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.DirectionsPane;

import com.lynden.gmapsfx.service.directions.DirectionsRenderer;
import com.lynden.gmapsfx.service.directions.DirectionsRequest;
import com.lynden.gmapsfx.service.directions.DirectionsService;
import com.lynden.gmapsfx.service.directions.DirectionsServiceCallback;
import com.lynden.gmapsfx.service.directions.TravelModes;
import com.lynden.gmapsfx.service.directions.DirectionsResult;
import com.lynden.gmapsfx.service.directions.DirectionsLeg;
import com.lynden.gmapsfx.service.directions.DirectionStatus;

import com.lynden.gmapsfx.shapes.Polyline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import odms.commons.model.enums.UserType;
import odms.commons.model.locations.Hospital;
import odms.commons.model.user.User;
import odms.controller.AlertController;
import odms.data.GoogleDistanceMatrix;
import odms.view.CommonView;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static odms.controller.user.AvailableOrgans.msToStandard;

/**
 * View class for the hospital map tab.
 */
@Slf4j
public class HospitalMap extends CommonView implements Initializable,
        MapComponentInitializedListener, DirectionsServiceCallback {

    private odms.controller.user.HospitalMap controller =
            new odms.controller.user.HospitalMap(this);

    private static DecimalFormat decimalFormat = new DecimalFormat("####0.00");

    private DirectionsService directionsService;
    private DirectionsPane directionsPane;
    private DirectionsRenderer directionsRenderer;

    private List<Hospital> hospitalList;
    private Hospital customLocation;
    private Hospital hospitalSelected1;
    private Hospital hospitalSelected2;
    private Polyline helicopterRoute;

    private GoogleMap map;
    private List<Marker> markers;

    private Boolean hasConnection;

    @FXML
    private GoogleMapView mapView;

    @FXML
    private TableView<Hospital> markersTable;

    @FXML
    private TextArea travelInfo;

    @FXML
    private ComboBox<String> travelMethod;

    @FXML
    private Button findClosestHospitalBtn;

    @FXML
    private Label noInternetLabel;

    @FXML
    private Button addHospitalButton;

    @FXML
    private Button editHospitalButton;

    /**
     * Init method for the map.
     * @param location location
     * @param resources resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mapView.addMapInializedListener(this);
        hospitalList = new ArrayList<>();
        markers = new ArrayList<>();

        if (hospitalSelected1 != null && hospitalSelected2 != null) {
            createRouteBetweenLocations(
                    hospitalSelected1.getLatitude(), hospitalSelected1.getLongitude(),
                    hospitalSelected1.getName(), hospitalSelected2.getLatitude(),
                    hospitalSelected2.getLongitude(), hospitalSelected2.getName());
        }

        ObservableList<String> travelMethods =
                FXCollections.observableArrayList("Car", "Helicopter");
        travelMethod.setItems(travelMethods);
        travelMethod.setValue("Car");
    }

    /**
     * Checks if connection to Google Maps is possible, if not shows a label to user.
     * @param currentUser the current logged in user
     */
    public void initialize(User currentUser) {
        hasConnection = netIsAvailable();
        if (!hasConnection) {
            mapView.setVisible(false);
            noInternetLabel.setVisible(true);
        }
        if (!currentUser.getUserType().equals(UserType.ADMIN)) {
            addHospitalButton.setDisable(true);
            editHospitalButton.setDisable(true);
        }
    }

    /**
     * Initializes the map.
     */
    @Override
    public void mapInitialized() {
        LatLong centreNZLatLng = new LatLong(-41, 172.6362);

        if (hasConnection) {
            //Set the initial properties of the map.
            MapOptions mapOptions = new MapOptions();

            mapOptions.center(new LatLong(centreNZLatLng.getLatitude(), centreNZLatLng.getLongitude()))
                    .mapType(MapTypeIdEnum.ROADMAP)
                    .overviewMapControl(false)
                    .panControl(false)
                    .rotateControl(false)
                    .scaleControl(false)
                    .streetViewControl(false)
                    .zoomControl(false)
                    .zoom(5);

            travelInfo.setWrapText(true);

        // Creating the actual map object using specified options
        map = mapView.createMap(mapOptions, false);

        // Creates objects needed for directions/routing
        directionsService = new DirectionsService();
        directionsPane = mapView.getDirec();
        directionsRenderer = new DirectionsRenderer(true, mapView.getMap(), directionsPane);

            // Creates a hospital object for a custom location added by the user,
            // is cleared using the clear button
            map.addMouseEventHandler(UIEventType.dblclick, (GMapMouseEvent e) -> {

                // Covers edge case of creating a marker with a route still being active/displayed
                directionsRenderer.clearDirections();
                directionsRenderer = new DirectionsRenderer(true, mapView.getMap(), directionsPane);
                if (helicopterRoute != null) {
                    map.removeMapShape(helicopterRoute);
                }

                String customLocationName = "Custom marker";

                Hospital location = new Hospital(customLocationName,
                        e.getLatLong().getLatitude(), e.getLatLong().getLongitude(), null, -1);

                // Ensures there is only ever one custom marker
                if (customLocation != null) {
                    hospitalList.remove(customLocation);
                }
                customLocation = location;
                for (Marker marker : markers) {
                    if (marker.getTitle().startsWith(customLocationName)) {
                        map.removeMarker(marker);
                    }
                }

                addHospitalMarker(location);
                travelInfo.setText("Created: " + location.getName());
                hospitalList.add(location);
                setMarkersTable();
            });

            // Clears selected
            map.addMouseEventHandler(UIEventType.click, (GMapMouseEvent e) -> {

                hospitalSelected1 = null;
                hospitalSelected2 = null;

                findClosestHospitalBtn.setDisable(true);

                travelInfo.clear();
            });

            populateHospitals();
            setMarkersTable();
            findClosestHospitalBtn.setDisable(true);
        }
    }

    /**
     * Populates the markers table with the current locations available to the map.
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
                cdf ->
                        new SimpleStringProperty(
                                String.valueOf(cdf.getValue().getId())
                        )
        );

        markersTable.getColumns().clear();
        markersTable.getColumns().add(nameColumn);
        markersTable.getColumns().add(idColumn);

        // Location clicked on in list
        markersTable.setOnMousePressed(event -> {
                    if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                            markersTable.getSelectionModel().getSelectedItem() != null) {

                        Hospital selectedLocation;
                        selectedLocation = (Hospital)
                                markersTable.getSelectionModel().getSelectedItem();

                        // Center on selected marker
                        map.setCenter(new LatLong(selectedLocation.getLatitude(),
                                selectedLocation.getLongitude()));
                        map.setZoom(10);
                    }
                });

        markersTable.getItems().clear();
        markersTable.setItems(FXCollections.observableArrayList(hospitalList));
    }

    /**
     * Shows the closest hospital to a clicked marker.
     */
    @FXML
    private void handleShowClosestHospital() {

        if (hospitalSelected1 == null) {
            return;
        }

        Hospital closest = null;
        Double distance = Double.POSITIVE_INFINITY;
        Double temp;
        for (Hospital location : hospitalList) {
            if (location.getId().equals(hospitalSelected1.getId())) {
                temp = controller.calcDistanceHaversine(location.getLatitude(),
                        location.getLongitude(), hospitalSelected1.getLatitude(),
                        hospitalSelected1.getLongitude());
                if (temp < distance) {
                    distance = temp;
                    closest = location;
                }
            }
        }

        if (closest != null) {
            map.setCenter(new LatLong(closest.getLatitude(), closest.getLongitude()));
            map.setZoom(9);

            travelInfo.setText("Closest hospital to " + hospitalSelected1.getName() + ": " +
                    closest.getName() + ", " + closest.getAddress() +
                    ".\n Approximately " + decimalFormat.format(distance) + "km away.");
        }
    }

    /**
     * Create popup with help text about hospital tab.
     */
    @FXML
    private void handleShowHelp() {

        String helpText = "Welcome! \n" +
                "To route between two hospitals, click on each and a route will appear, " +
                "to toggle the travel method use the dropdown menu. \n" +
                "To add a custom marker, double click on the map where you want the marker, " +
                "this marker can be used just like a hospital. \n" +
                "To add a hospital to the map, click 'Add Hospital," +
                "and fill out the required info. \n" +
                "To find the nearest hospital to a location, click its corresponding marker, " +
                "then click the 'Find nearest hospital' button. \n" +
                "Use the 'Clear' button to remove all routes and custom markers.";
        AlertController.guiPopupInfo(helpText);
    }

    /**
     * Clears all routes from map, also 'deselects' any hospitals, accessed via button.
     */
    @FXML
    private void handleClearRoutesButtonClicked() {
        clearRoutesAndSelection();
    }

    /**
     * Clears all routes from map, also 'deselects' any hospitals.
     */
    private void clearRoutesAndSelection() {
        populateHospitals();

        hospitalSelected1 = null;
        hospitalSelected2 = null;

        if (helicopterRoute != null) {
            map.removeMapShape(helicopterRoute);
        }

        travelInfo.clear();

        setMarkersTable();

        findClosestHospitalBtn.setDisable(true);

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
    private void createRouteBetweenLocations(Double originLat, Double originLong,
            String originName, Double destinationLat, Double destinationLong,
            String destinationName) {
        final double HELICOPTER_SPEED_KMH = 262;
        final int SECONDS_IN_HOUR = 3600;

        if (hospitalSelected1.getId().equals(hospitalSelected2.getId())) {
            clearRoutesAndSelection();
            return;
        }

        boolean isCarTrip = false;
        if (travelMethod.getSelectionModel().getSelectedItem().equals("Car")) {
            isCarTrip = true;
        }

        if (isCarTrip) {

            String originLatLong = originLat + "," + originLong;
            String destinationLatLong = destinationLat + "," + destinationLong;

            DirectionsRequest directionsRequest = new DirectionsRequest(originLatLong,
                    destinationLatLong, TravelModes.DRIVING);
            directionsService.getRoute(directionsRequest, this, directionsRenderer);

        } else {

            Double distance = controller.calcDistanceHaversine(originLat, originLong,
                    destinationLat, destinationLong); // km
            Double duration = distance / HELICOPTER_SPEED_KMH * SECONDS_IN_HOUR;
            showTravelDetails(originName, destinationName, decimalFormat.format(distance) +
                    "km", decimalFormat.format(duration));

            if (helicopterRoute != null) {
                map.removeMapShape(helicopterRoute);
            }

            helicopterRoute = controller.createHelicopterRoute(originLat, originLong,
                    destinationLat, destinationLong);
            map.addMapShape(helicopterRoute);
        }
    }


    /**
     * Called when a directions result returns, displays results of the request if the
     * request was successful.
     *
     * @param directionsResult The returned direction result
     * @param directionStatus  Status of request
     */
    @Override
    public void directionsReceived(DirectionsResult directionsResult, DirectionStatus
            directionStatus) {

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
    private void showTravelDetails(String originName, String destinationName, String distance,
            String duration) {

        String travelMethodGiven = String.valueOf(
                travelMethod.getSelectionModel().getSelectedItem());
        final int MLS_IN_SECOND = 1000;

        try {
            double durationNumber = Double.parseDouble(duration) * MLS_IN_SECOND;
            duration = msToStandard((long) durationNumber);
        } catch (NumberFormatException e) {
            log.error("Failed to parse travel duration, must not be applicable");
            log.error(e.getMessage(), e);
        }

        String travel = travelMethodGiven + " journey between " + originName + ", and " +
                destinationName + ".\n" + "Distance: " + distance +
                "\n Approximate Travel Time: " + duration;

        travelInfo.setText(travel);
    }

    /**
     * Event handler, when add hospital button is clicked a new window is opened.
     * @param event button click event
     */
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
            stage.setOnHiding(ob -> mapInitialized());
            stage.show();
        } catch (IOException e) {
            log.error("Failed to populate hospitals");
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Event handler, when edit hospital button is clicked a new window is opened.
     * @param event button click event
     */
    @FXML
    public void handleEditHospital(ActionEvent event) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/HospitalCreate.fxml"));

        try {
            Hospital hospital = markersTable.getSelectionModel().getSelectedItem();
            if (hospital == null) {
                return;
            }

            Scene scene = new Scene(fxmlLoader.load());
            HospitalCreate hospitalCreate = fxmlLoader.getController();
            hospitalCreate.initialize(hospital);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Edit Hospital");
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.centerOnScreen();
            stage.setOnHiding(ob -> mapInitialized());
            stage.show();
        } catch (IOException e) {
            log.error("Failed to populate hospitals");
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Adds a given location to the map object, as a marker with a tooltip containing
     * location details.
     *
     * @param location location to be to added to the map
     */
    private void addHospitalMarker(Hospital location) {

        Marker marker = controller.createLocationMarker(location);
        InfoWindow infoWindow = controller.createLocationInfoWindow(location);
        markers.add(marker);

        // For displaying info window
        map.addUIEventHandler(marker, UIEventType.rightclick, (JSObject obj) ->
                infoWindow.open(map, marker)
        );

        map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {

            travelInfo.setText("Selected " + location.getName());

            if (hospitalSelected1 == null) {
                hospitalSelected1 = location;

                findClosestHospitalBtn.setDisable(false);

                if (hospitalSelected2 != null) {
                    createRouteBetweenLocations(
                            hospitalSelected1.getLatitude(), hospitalSelected1.getLongitude(),
                            hospitalSelected1.getName(), hospitalSelected2.getLatitude(),
                            hospitalSelected2.getLongitude(), hospitalSelected2.getName()
                    );
                }

            } else {
                hospitalSelected2 = location;

                createRouteBetweenLocations(
                        hospitalSelected1.getLatitude(), hospitalSelected1.getLongitude(),
                        hospitalSelected1.getName(), hospitalSelected2.getLatitude(),
                        hospitalSelected2.getLongitude(), hospitalSelected2.getName()
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

        for (Marker marker : markers) {
            map.removeMarker(marker);
        }
        hospitalList.clear();
        markers.clear();
        hospitalList = controller.getHospitals();

        for (Hospital hospital : hospitalList) {
            addHospitalMarker(hospital);
        }

    }

    /**
     * Changes the travel method used for routing between two markers/locations. Options are by Car
     * or Helicopter.
     */
    @FXML
    private void handleTravelMethodToggled() {

        if (hospitalSelected1 != null && hospitalSelected2 != null) {
            if (helicopterRoute != null) {
                map.removeMapShape(helicopterRoute);
            }
            directionsRenderer.clearDirections();
            directionsRenderer = new DirectionsRenderer(true, mapView.getMap(), directionsPane);

            createRouteBetweenLocations(
                    hospitalSelected1.getLatitude(), hospitalSelected1.getLongitude(),
                    hospitalSelected1.getName(), hospitalSelected2.getLatitude(),
                    hospitalSelected2.getLongitude(), hospitalSelected2.getName()
            );
        }
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }
}
