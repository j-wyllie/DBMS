package odms.view.user;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.util.MarkerImageFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.view.CommonView;

/**
 * Tab containing the organ map.
 */
@Slf4j
public class OrganMap extends CommonView implements Initializable, MapComponentInitializedListener {

    private static final String RECEIVER_MARKER = "/icons/receiverMarker.png";
    private static final String DONOR_MARKER = "/icons/deadDonorMarker.png";
    private static final Integer ZOOM_LEVEL = 5;
    private static final LatLong LAT_LONG = new LatLong(-41, 172.6362);
    private static final String FULL_PREFERRED_NAME = "fullPreferredName";

    private odms.controller.user.OrganMap controller = new odms.controller.user.OrganMap();
    private User currentUser;
    private Collection<Marker> currentDonorMarkers = new ArrayList<>();
    private Collection<Marker> currentReceiverMarkers = new ArrayList<>();
    private ObservableList<Profile> donorsList;
    private ObservableList<Profile> receiversList;

    @FXML
    private GoogleMapView mapView;
    private GoogleMap map;

    @FXML
    private TableView<Profile> donorListView;
    @FXML
    private TableColumn<Object, Object> donorColumn;
    @FXML
    private TableView<Profile> receiverListView;
    @FXML
    private TableColumn<Object, Object> receiverColumn;

    private ClinicianProfile parentView;

    /**
     * Sets the current user and parent view.
     *
     * @param currentUser the current user.
     * @param parentView the parent view.
     */
    public void initialize(User currentUser, ClinicianProfile parentView) {
        this.currentUser = currentUser;
        this.parentView = parentView;
        controller.setView(this);
        initListViews();
    }

    /**
     * Initializes the map view by adding a listener.
     *
     * @param location location.
     * @param resources resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapView.addMapInializedListener(this);
    }

    /**
     * Initializes the map view. Sets the map options.
     */
    @Override
    public void mapInitialized() {

        //Set the initial properties of the map.
        MapOptions mapOptions = new MapOptions();

        mapOptions.center(LAT_LONG)
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .zoom(ZOOM_LEVEL);

        map = mapView.createMap(mapOptions);
        showAllDonors();
    }

    /**
     * Initializes the list views.
     */
    private void initListViews() {
        donorsList = controller.getDeadDonors();
        donorListView.setItems(donorsList);
        donorColumn.setCellValueFactory(new PropertyValueFactory<>(FULL_PREFERRED_NAME));

        donorListView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    donorListView.getSelectionModel().getSelectedItem() != null) {
                Profile selectedDonor = donorListView.getSelectionModel()
                        .getSelectedItem();
                addDonorMarker(selectedDonor);
                populateReceivers(selectedDonor);
            }
        });

        receiverListView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    receiverListView.getSelectionModel().getSelectedItem() != null) {
                Profile selectedReceiver = receiverListView.getSelectionModel()
                        .getSelectedItem();
                addReceiverMarker(selectedReceiver);
            }
        });
    }

    /**
     * Populates the receivers table with all the possible receivers for the donor selected.
     *
     * @param donor the donor that is donating the organ.
     */
    private void populateReceivers(Profile donor) {
        receiversList = controller.getReceivers(donor);
        receiverListView.setItems(receiversList);
        receiverColumn.setCellValueFactory(new PropertyValueFactory<>(FULL_PREFERRED_NAME));
    }

    /**
     * Adds a single donor marker to the map.
     *
     * @param profile profile to add.
     */
    private void addDonorMarker(Profile profile) {
        ArrayList<Double> latLng = controller.displayPointOnMap(profile);
        LatLong donorLocation = new LatLong(latLng.get(0), latLng.get(1));
        MarkerOptions markerOptions = new MarkerOptions();
        formatMarkerImage(DONOR_MARKER, donorLocation, markerOptions);

        Marker marker = new Marker(markerOptions);
        if (!currentDonorMarkers.isEmpty()) {
            clearDonorMarkers();
        }
        if (!currentReceiverMarkers.isEmpty()) {
            map.removeMarkers(currentReceiverMarkers);
            currentReceiverMarkers.clear();
        }
        map.addMarker(marker);

        map.addUIEventHandler(marker, UIEventType.click,
                jsObject -> createNewDonorWindow(profile, parentView, currentUser));

        currentDonorMarkers.add(marker);
    }

    /**
     * Adds a receiver marker to the map at the profiles location.
     *
     * @param profile The profile to be added to the map.
     */
    private void addReceiverMarker(Profile profile) {
        ArrayList<Double> latLng = controller.displayPointOnMap(profile);
        LatLong donorLocation = new LatLong(latLng.get(0), latLng.get(1));
        MarkerOptions markerOptions = new MarkerOptions();
        formatMarkerImage(RECEIVER_MARKER, donorLocation, markerOptions);
        Marker marker = new Marker(markerOptions);

        if (!currentReceiverMarkers.isEmpty()) {
            map.removeMarkers(currentReceiverMarkers);
            currentReceiverMarkers.clear();
        }

        map.addMarker(marker);
        map.addUIEventHandler(marker, UIEventType.click,
                jsObject -> createNewDonorWindow(profile, parentView, currentUser));

        currentReceiverMarkers.add(marker);
    }

    /**
     * Shows all the donors on the map.
     */
    public void showAllDonors() {
        map.removeMarkers(currentDonorMarkers);
        currentDonorMarkers.clear();
        showAllOnMap(donorsList, DONOR_MARKER);
    }

    /**
     * Removes all the donor markers currently on the map.
     */
    private void clearDonorMarkers() {
        map.removeMarkers(currentDonorMarkers);
        currentDonorMarkers.clear();
    }

    /**
     * Adds a marker on the map for each receiver.
     */
    public void showAllReceivers() {
        map.removeMarkers(currentReceiverMarkers);
        currentReceiverMarkers.clear();
        showAllOnMap(receiversList, RECEIVER_MARKER);

    }

    /**
     * Displays a list of profiles on the map.
     *
     * @param profileList Profiles to display.
     * @param mapMarker image to use as marker.
     */
    private void showAllOnMap(ObservableList<Profile> profileList, String mapMarker) {
        for (Profile profile : profileList) {
            ArrayList<Double> latLng = controller.displayPointOnMap(profile);
            LatLong donorLocation = new LatLong(latLng.get(0), latLng.get(1));
            MarkerOptions markerOptions = new MarkerOptions();
            formatMarkerImage(mapMarker, donorLocation, markerOptions);
            Marker marker = new Marker(markerOptions);
            currentReceiverMarkers.add(marker);

            map.addUIEventHandler(marker, UIEventType.click,
                    jsObject -> {
                        if (mapMarker.equals(DONOR_MARKER)) {
                            populateReceivers(profile);
                            showAllReceivers();
                            clearDonorMarkers();
                        } else {
                            createNewDonorWindow(profile, parentView, currentUser);
                        }
                    });
            map.addMarker(marker);
        }
    }

    /**
     * Formats the marker image.
     *
     * @param mapMarker The current map marker.
     * @param donorLocation Donors location as a LatLong.
     * @param markerOptions The markers options.
     */
    private void formatMarkerImage(String mapMarker, LatLong donorLocation,
            MarkerOptions markerOptions) {
        String markerImage = MarkerImageFactory.createMarkerImage(this.getClass()
                .getResource(mapMarker).toString(), "png");
        markerImage = markerImage.replace("(", "");
        markerImage = markerImage.replace(")", "");
        markerOptions.position(donorLocation).icon(markerImage);
    }

    public double getMatchesListViewWidth() {
        return donorListView.getWidth();
    }
}
