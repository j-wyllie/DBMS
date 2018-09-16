package odms.view.user;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.util.MarkerImageFactory;
import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.view.CommonView;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Tab containing the organ map.
 */
@Slf4j
public class OrganMap extends CommonView implements Initializable, MapComponentInitializedListener {

    private static final String RECEIVER_MARKER = "/icons/receiverMarker.png";
    private static final String DONOR_MARKER = "/icons/deadDonorMarker.png";
    private static final Integer ZOOM_LEVEL = 5;
    private static final String FULL_PREFERRED_NAME = "fullPreferredName";
    private static final Double LAT = -41.0;
    private static final Double LONG = 172.6362;
    private static final Double HALF_SECOND = 0.5;

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
    @FXML
    private TextField searchDonorsText;

    private ClinicianProfile parentView;
    private PopOver popOver;
    private Boolean hasClickedMarker = false;
    private Profile currentReceiver;
    private Profile currentDonor;
    private Button openProfileBtn = new Button("View Profile");
    private Button matchBtn = new Button("Match");

    /**
     * Sets the current user and parent view.
     *
     * @param currentUser the current user.
     * @param parentView  the parent view.
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
     * @param location  location.
     * @param resources resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mapView.addMapInializedListener(this);

        mapView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (popOver != null) {
                popOver.hide();
                popOver = null;
            }
            if (hasClickedMarker) {
                popOver = controller.createNewPopOver(currentReceiver);
                popOver.show(mapView.getParent(), event.getScreenX(), event.getScreenY());
                hasClickedMarker = false;
            }
        });

        openProfileBtn.setOnAction(event -> createNewDonorWindow(currentReceiver, parentView, currentUser));

        matchBtn.setOnAction(event -> {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/UserScheduleProcedure.fxml"));

            try {
                Scene scene = new Scene(fxmlLoader.load());
                ScheduleProcedure scheduleProcedure = fxmlLoader.getController();
                scheduleProcedure.initialize(this);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Schedule Procedure");
                stage.initOwner(mapView.getScene().getWindow());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.show();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

        });
    }

    /**
     * Initializes the map view. Sets the map options.
     */
    @Override
    public void mapInitialized() {

        //Set the initial properties of the map.
        MapOptions mapOptions = new MapOptions();

        mapOptions.center(new LatLong(LAT, LONG)
        )
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
        if (!searchDonorsText.getText().equals("")) {
            donorsList = controller.getDeadDonorsFiltered(searchDonorsText.getText());
        } else {
            donorsList = controller.getDeadDonors();
        }
        donorListView.setItems(donorsList);
        donorColumn.setCellValueFactory(new PropertyValueFactory<>(FULL_PREFERRED_NAME));

        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(HALF_SECOND));
        searchDonorsText.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished(ae -> initListViews());
            pauseTransition.playFromStart();
        });

        donorListView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 1 &&
                    donorListView.getSelectionModel().getSelectedItem() != null) {
                Profile selectedDonor = donorListView.getSelectionModel()
                        .getSelectedItem();
                addDonorMarker(selectedDonor);
                currentDonor = selectedDonor;
                populateReceivers(selectedDonor);
            } else if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    donorListView.getSelectionModel().getSelectedItem() != null) {
                createNewDonorWindow(donorListView.getSelectionModel()
                        .getSelectedItem(), parentView, currentUser);
            }
        });

        receiverListView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 1 &&
                    receiverListView.getSelectionModel().getSelectedItem() != null) {
                Profile selectedReceiver = receiverListView.getSelectionModel()
                        .getSelectedItem();
                addReceiverMarker(selectedReceiver);
            } else if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    receiverListView.getSelectionModel().getSelectedItem() != null) {
                createNewDonorWindow(receiverListView.getSelectionModel()
                        .getSelectedItem(), parentView, currentUser);
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
        List<Double> latLng = controller.getProfileLatLong(profile);
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
                jsObject -> {
                    populateReceivers(profile);
                    showAllReceivers();
                    clearDonorMarkers();
                });

        currentDonorMarkers.add(marker);
    }

    /**
     * Adds a receiver marker to the map at the profiles location.
     *
     * @param profile The profile to be added to the map.
     */
    private void addReceiverMarker(Profile profile) {
        List<Double> latLng = controller.getProfileLatLong(profile);
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
                jsObject -> {
                    hasClickedMarker = true;
                    currentReceiver = profile;
                });

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
     * @param mapMarker   image to use as marker.
     */
    private void showAllOnMap(ObservableList<Profile> profileList, String mapMarker) {
        for (Profile profile : profileList) {
            List<Double> latLng = controller.getProfileLatLong(profile);
            LatLong donorLocation = new LatLong(latLng.get(0), latLng.get(1));
            MarkerOptions markerOptions = new MarkerOptions();
            formatMarkerImage(mapMarker, donorLocation, markerOptions);
            Marker marker = new Marker(markerOptions);
            currentReceiverMarkers.add(marker);

            map.addUIEventHandler(marker, UIEventType.click,
                    jsObject -> {
                        if (mapMarker.equals(DONOR_MARKER)) {
                            currentDonor = profile;
                            populateReceivers(profile);
                            showAllReceivers();
                            clearDonorMarkers();
                        } else {
                            currentReceiver = profile;
                            hasClickedMarker = true;
                            currentReceiver = profile;
                        }
                    });
            map.addMarker(marker);
        }
    }

    /**
     * Formats the marker image.
     *
     * @param mapMarker     The current map marker.
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

    public Button getOpenProfileBtn() {
        return openProfileBtn;
    }

    public Button getMatchBtn() {
        return matchBtn;
    }

    public Profile getCurrentReceiver() {
        return currentReceiver;
    }

    public Profile getCurrentDonor() {
        return currentDonor;
    }
}
