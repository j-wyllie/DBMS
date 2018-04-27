package odms.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.ResourceBundle;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import odms.profile.Organ;
import odms.profile.Profile;

public class OrganRequiredController {
    private Profile profile;

    private ObservableList<String> observableListOrgansAvailable;
    private ObservableList<String> observableListOrgansRequired;

    @FXML
    private ListView<String> viewOrgansAvailable;

    @FXML
    private ListView<String> viewOrgansRequired;

    @FXML
    private Button btnOrganSwitch;

    private void buildOrgansRequired() {
        observableListOrgansRequired = FXCollections.observableArrayList();
        if(profile.getRequiredOrgans() != null) {
            for (Organ organ : profile.getRequiredOrgans()) {
                observableListOrgansRequired.add(organ.getName());
            }
        }
    }

    private void buildOrgansAvailable() {
        observableListOrgansAvailable = FXCollections.observableArrayList();
        observableListOrgansAvailable.addAll(Organ.toArrayList());
        observableListOrgansAvailable.removeIf(str -> observableListOrgansRequired.contains(str));
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    private void refresh() {

        viewOrgansRequired.refresh();
        viewOrgansAvailable.refresh();
    }

    @FXML
    private void handleBtnOrganSwitchClicked() {
        final int selectedIdxAvailable = viewOrgansAvailable.getFocusModel().getFocusedIndex();
        if (selectedIdxAvailable != -1) {
            String itemToRemove = viewOrgansAvailable.getSelectionModel().getSelectedItem();
            observableListOrgansAvailable.remove(itemToRemove);
            observableListOrgansRequired.add(itemToRemove);
            refresh();
        } else {
            final int selectedIdxRequired = viewOrgansRequired.getSelectionModel().getSelectedIndex();
            if(selectedIdxRequired != -1) {
                String itemToRemove = viewOrgansRequired.getSelectionModel().getSelectedItem();
                observableListOrgansRequired.remove(itemToRemove);
                observableListOrgansAvailable.add(itemToRemove);
                refresh();
            }
        }
    }

    @FXML
    public void initialize() {
        if (profile != null) {
            buildOrgansRequired();
            buildOrgansAvailable();
            viewOrgansAvailable.setItems(observableListOrgansAvailable);
            viewOrgansRequired.setItems(observableListOrgansRequired);
        }

        btnOrganSwitch.setOnAction(event -> handleBtnOrganSwitchClicked());

    }
}
