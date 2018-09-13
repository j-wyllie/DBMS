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
    private ObservableList<Profile> donorsList;
    private ProfileDAO profileDAO = DAOFactory.getProfileDao();

    @FXML
    private GoogleMapView mapView;
    private GoogleMap map;

    @FXML
    private TableView donorListView;
    @FXML
    private TableColumn donorColumn;
    @FXML
    private Button showAllDonorsButton;

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
        initListView();
    }

    private void initListView() {
        donorsList = controller.getDeadDonors();
        donorListView.setItems(donorsList);
        donorColumn.setCellValueFactory(new PropertyValueFactory<>("fullPreferredName"));

        donorListView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    donorListView.getSelectionModel().getSelectedItem() != null) {
                addSingleMarker((Profile) donorListView.getSelectionModel().getSelectedItem());
            }
        });
    }

    public void addSingleMarker(Profile profile) {
        ArrayList<Double> latLng = controller.displayPointOnMap(profile);
        LatLong donorLocation = new LatLong(latLng.get(0), latLng.get(1));
        MarkerOptions markerOptions = new MarkerOptions();
        String markerImage = MarkerImageFactory.createMarkerImage(this.getClass()
                .getResource("/icons/mapMarker.png").toString(), "png");
        markerImage = markerImage.replace("(", "");
        markerImage = markerImage.replace(")", "");
        markerOptions.position(donorLocation).icon(markerImage);

        Marker marker = new Marker(markerOptions);
        System.out.println(currentDonorMarkers.isEmpty());
        if (!currentDonorMarkers.isEmpty()) {
            map.removeMarkers(currentDonorMarkers);
            currentDonorMarkers.clear();
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


    public void showAllDonors() {
        map.removeMarkers(currentDonorMarkers);
        currentDonorMarkers.clear();
        for (Profile profile : donorsList) {
            ArrayList<Double> latLng = controller.displayPointOnMap(profile);
            LatLong donorLocation = new LatLong(latLng.get(0), latLng.get(1));
            MarkerOptions markerOptions = new MarkerOptions();
            String markerImage = MarkerImageFactory.createMarkerImage(this.getClass()
                    .getResource("/icons/mapMarker.png").toString(), "png");
            markerImage = markerImage.replace("(", "");
            markerImage = markerImage.replace(")", "");
            markerOptions.position(donorLocation).icon(markerImage);
            Marker marker = new Marker(markerOptions);
            currentDonorMarkers.add(marker);
            map.addMarker(marker);
        }
    }

    public double getMatchesListViewWidth() {
        return donorListView.getWidth();
    }
}
