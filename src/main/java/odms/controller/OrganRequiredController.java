package odms.controller;


import java.util.HashSet;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
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

    @FXML
    private Button btnSave;

    @FXML
    public void initialize() {
        if (profile != null) {
            buildOrgansRequired();
            buildOrgansAvailable();
            viewOrgansAvailable.setItems(observableListOrgansAvailable);
            viewOrgansRequired.setItems(observableListOrgansRequired);
        }

        btnOrganSwitch.setOnAction(event -> handleBtnOrganSwitchClicked());
        viewOrgansAvailable.setOnMouseClicked(this::handleListOrgansAvailableClick);
        viewOrgansRequired.setOnMouseClicked(this::handleListOrgansRequiredClick);
    }



    private void buildOrgansRequired() {
        observableListOrgansRequired = FXCollections.observableArrayList();
        if(profile.getOrgansRequired() != null) {
            for (Organ organ : profile.getOrgansRequired()) {
                observableListOrgansRequired.add(organ.getName());
            }
        }
    }

    private void buildOrgansAvailable() {
        observableListOrgansAvailable = FXCollections.observableArrayList();
        observableListOrgansAvailable.addAll(Organ.toArrayList());
        observableListOrgansAvailable.removeIf(str -> observableListOrgansRequired.contains(str));
    }

    private void handleBtnOrganSwitchClicked() {
        switchOrgans();
    }

    private void handleListOrgansAvailableClick(MouseEvent event) {
        handleOrgansClick(event, viewOrgansRequired.getSelectionModel());
    }

    private void handleListOrgansRequiredClick(MouseEvent event) {
        handleOrgansClick(event, viewOrgansAvailable.getSelectionModel());
    }

    private void handleOrgansClick(MouseEvent event,
            MultipleSelectionModel<String> selectionModel) {
        if (event.getButton() == MouseButton.PRIMARY) {
            selectionModel.clearSelection();

            if (event.getClickCount() == 2) {
                selectionModel.clearSelection();
                switchOrgans();
            }
        }
    }

    private void refresh() {
        viewOrgansRequired.refresh();
        viewOrgansAvailable.refresh();
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    private void switchOrgans() {
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

        viewOrgansAvailable.getSelectionModel().clearSelection();
        viewOrgansRequired.getSelectionModel().clearSelection();
    }

    public void onBtnSaveClicked() {
        profile.setReceiver(true);
        Set<String> set = new HashSet<>(observableListOrgansRequired);
        profile.addOrgansRequired(set);
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }
}
