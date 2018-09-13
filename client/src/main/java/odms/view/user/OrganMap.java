package odms.view.user;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.util.MarkerImageFactory;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Callback;
import netscape.javascript.JSObject;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;

import java.net.URL;
import java.util.*;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;
import odms.controller.user.ExpandableListElement;
import odms.view.CommonView;

public class OrganMap extends CommonView implements Initializable, MapComponentInitializedListener {

    private odms.controller.user.OrganMap controller = new odms.controller.user.OrganMap();
    private User currentUser;
    private Collection<Marker> currentDonorMarkers = new ArrayList<Marker>();
    private Collection<Marker> currentReceiverMarkers = new ArrayList<Marker>();
    private ObservableList<Profile> donorsList;
    private ProfileDAO profileDAO = DAOFactory.getProfileDao();
    private ObservableList<Profile> receiversList;

    @FXML
    private GoogleMapView mapView;
    private GoogleMap map;

    @FXML
    private TableView donorListView;
    @FXML
    private TableColumn donorColumn;
    @FXML
    private Button showAllDonorsButton;
    @FXML
    private TableView receiverListView;
    @FXML
    private TableColumn receiverColumn;
    @FXML
    private Button showAllReceiversButton;

    private ClinicianProfile parentView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapView.addMapInializedListener(this);
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


    public void initialize(User currentUser, ClinicianProfile parentView) {
        this.currentUser = currentUser;
        this.parentView = parentView;
        controller.setView(this);
        initListViews();
    }

    private void initListViews() {
        donorsList = controller.getDeadDonors();
        donorListView.setItems(donorsList);
        donorColumn.setCellValueFactory(new PropertyValueFactory<>("fullPreferredName"));

        donorListView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    donorListView.getSelectionModel().getSelectedItem() != null) {
                Profile selectedDonor = (Profile) donorListView.getSelectionModel().getSelectedItem();
                addDonorMarker(selectedDonor);
                populateReceivers(selectedDonor);
            }
        });

        receiverListView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    receiverListView.getSelectionModel().getSelectedItem() != null) {
                Profile selectedReceiver = (Profile) receiverListView.getSelectionModel().getSelectedItem();
                addReceiverMarker(selectedReceiver);
            }
        });
    }

    /**
     * Populates the receivers table with all the possible receivers for the donor selected.
     * @param donor the donor that is donating the organ.
     */
    private void populateReceivers(Profile donor) {
        receiversList = controller.getReceivers(donor);
        receiverListView.setItems(receiversList);
        receiverColumn.setCellValueFactory(new PropertyValueFactory<>("fullPreferredName"));
    }

    public void addDonorMarker(Profile profile) {
        ArrayList<Double> latLng = controller.displayPointOnMap(profile);
        LatLong donorLocation = new LatLong(latLng.get(0), latLng.get(1));
        MarkerOptions markerOptions = new MarkerOptions();
        String markerImage = MarkerImageFactory.createMarkerImage(this.getClass()
                .getResource("/icons/deadDonorMarker.png").toString(), "png");
        markerImage = markerImage.replace("(", "");
        markerImage = markerImage.replace(")", "");
        markerOptions.position(donorLocation).icon(markerImage);

        Marker marker = new Marker(markerOptions);
        if(!currentDonorMarkers.isEmpty()){
            map.removeMarkers(currentDonorMarkers);
            currentDonorMarkers.clear();
        }
        if(!currentReceiverMarkers.isEmpty()){
            map.removeMarkers(currentReceiverMarkers);
            currentReceiverMarkers.clear();
        }
        map.addMarker(marker);
        map.addUIEventHandler(marker, UIEventType.click,
                jsObject -> {
                    try {
                        createNewDonorWindow(profileDAO.get(1), parentView, currentUser);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
        currentDonorMarkers.add(marker);
    }

    public void addReceiverMarker(Profile profile) {
        ArrayList<Double> latLng = controller.displayPointOnMap(profile);
        LatLong donorLocation = new LatLong(latLng.get(0), latLng.get(1));
        MarkerOptions markerOptions = new MarkerOptions();
        String markerImage = MarkerImageFactory.createMarkerImage(this.getClass()
                .getResource("/icons/mapMarker_40x40.png").toString(), "png");
        markerImage = markerImage.replace("(", "");
        markerImage = markerImage.replace(")", "");
        markerOptions.position(donorLocation).icon(markerImage);
        Marker marker = new Marker(markerOptions);


if(!currentReceiverMarkers.isEmpty()){
            map.removeMarkers(currentReceiverMarkers);
            currentReceiverMarkers.clear();
        }

        map.addMarker(marker);
        currentReceiverMarkers.add(marker);
    }    public void showAllDonors(){
        map.removeMarkers(currentDonorMarkers);
        currentDonorMarkers.clear();
        for (Profile profile : donorsList) {
            ArrayList<Double> latLng = controller.displayPointOnMap(profile);
            LatLong donorLocation = new LatLong(latLng.get(0), latLng.get(1));
            MarkerOptions markerOptions = new MarkerOptions();
            String markerImage = MarkerImageFactory.createMarkerImage(this.getClass()
                    .getResource("/icons/deadDonorMarker.png").toString(), "png");
            markerImage = markerImage.replace("(", "");
            markerImage = markerImage.replace(")", "");
            markerOptions.position(donorLocation).icon(markerImage);
            Marker marker = new Marker(markerOptions);
            currentDonorMarkers.add(marker);
            map.addMarker(marker);
        }
    }

    public void showAllReceivers() {
        for(Profile profile : receiversList) {
            ArrayList<Double> latLng = controller.displayPointOnMap(profile);
            LatLong donorLocation = new LatLong(latLng.get(0), latLng.get(1));
            MarkerOptions markerOptions = new MarkerOptions();
            String markerImage = MarkerImageFactory.createMarkerImage(this.getClass()
                    .getResource("/icons/mapMarker_40x40.png").toString(), "png");
            markerImage = markerImage.replace("(", "");
            markerImage = markerImage.replace(")", "");
            markerOptions.position(donorLocation).icon(markerImage);
            Marker marker = new Marker(markerOptions);
            currentReceiverMarkers.add(marker);
            map.addMarker(marker);
        }
    }

    public double getMatchesListViewWidth() {
        return donorListView.getWidth();
    }
}
