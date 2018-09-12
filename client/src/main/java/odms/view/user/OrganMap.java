package odms.view.user;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.util.MarkerImageFactory;
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
import javafx.util.Callback;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;

import java.net.URL;
import java.util.*;
import odms.controller.user.ExpandableListElement;

public class OrganMap implements Initializable, MapComponentInitializedListener{

    private odms.controller.user.OrganMap controller = new odms.controller.user.OrganMap();
    private User currentUser;
    private Collection<Marker> currentDonorMarkers = new ArrayList<Marker>();
    private ObservableList<Profile> donorsList;

    @FXML
    private GoogleMapView mapView;
    private GoogleMap map;

    @FXML
    private TableView donorListView;
    @FXML
    private TableColumn donorColumn;
    @FXML
    private Button showAllDonorsButton;

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


    public void initialize(User currentUser) {
        this.currentUser = currentUser;
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

    public void addSingleMarker(Profile profile){
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
        if(!currentDonorMarkers.isEmpty()){
            map.removeMarkers(currentDonorMarkers);
            currentDonorMarkers.clear();
        }
        map.addMarker(marker);
        currentDonorMarkers.add(marker);
    }



    public void showAllDonors(){
        map.removeMarkers(currentDonorMarkers);
        currentDonorMarkers.clear();
        for(Profile profile : donorsList){
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
