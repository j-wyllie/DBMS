package odms.view.user;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Callback;
import odms.commons.model.user.User;

import java.net.URL;
import java.util.*;
import odms.controller.user.ExpandableListElement;

public class OrganMap implements Initializable, MapComponentInitializedListener{

    private odms.controller.user.OrganMap controller = new odms.controller.user.OrganMap();
    private User currentUser;

    @FXML
    private GoogleMapView mapView;
    private GoogleMap map;

    @FXML
    private ListView matchesListView;
    private ObservableList<ExpandableListElement> listViewContent;

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
        // populate matchesListView with donor and receiver columns and their initial values
        ListView<String> donorsList = new ListView<String>(controller.getDeadDonors());
        ListView<String> receiversList = new ListView<String>();    // empty as no receiver selected
        ExpandableListElement donors = new ExpandableListElement("Donors", donorsList);
        ExpandableListElement receivers = new ExpandableListElement("Receivers", receiversList);
        listViewContent = FXCollections.observableArrayList();
        listViewContent.add(donors);
        listViewContent.add(receivers);
        matchesListView.setItems(listViewContent);

        // set cell factory callback function
        matchesListView.setCellFactory(controller.getListViewCallback());
    }

    public double getMatchesListViewWidth() {
        return matchesListView.getWidth();
    }
}
