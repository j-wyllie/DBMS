package odms.controller;


import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
            // Order of execution for building these is required due to removing items from the
            // Available list that are present in the Required list.
            buildOrgansRequired();
            buildOrgansAvailable();
            viewOrgansAvailable.setItems(observableListOrgansAvailable);
            viewOrgansRequired.setItems(observableListOrgansRequired);
        }

        btnOrganSwitch.setOnAction(event -> handleBtnOrganSwitchClicked());
        viewOrgansAvailable.setOnMouseClicked(this::handleListOrgansAvailableClick);
        viewOrgansRequired.setOnMouseClicked(this::handleListOrgansRequiredClick);
    }

    /**
     * Populate the ListView with the organs the profile currently requires.
     */
    private void buildOrgansRequired() {
        observableListOrgansRequired = FXCollections.observableArrayList();
        if(profile.getOrgansRequired() != null) {
            for (Organ organ : profile.getOrgansRequired()) {
                observableListOrgansRequired.add(organ.getNamePlain());
            }
            Collections.sort(observableListOrgansRequired);
        }
    }

    /**
     * Populate the ListView with the organs that are available and that are not in the
     * required list.
     */
    private void buildOrgansAvailable() {
        observableListOrgansAvailable = FXCollections.observableArrayList();
        observableListOrgansAvailable.addAll(Organ.toArrayList());
        observableListOrgansAvailable.removeIf(str -> observableListOrgansRequired.contains(str));
    }

    /**
     * Button to perform moving the organ from one ListView to the other ListView
     */
    private void handleBtnOrganSwitchClicked() {
        switchOrgans();
    }

    private void handleListOrgansAvailableClick(MouseEvent event) {
        handleOrgansClick(event, viewOrgansRequired.getSelectionModel());
    }

    private void handleListOrgansRequiredClick(MouseEvent event) {
        handleOrgansClick(event, viewOrgansAvailable.getSelectionModel());
    }

    /**
     * Click Handler to handle Click actions on the ListViews.
     * - A single click will clear the selection from the opposing ListView.
     * - A double click will move the organ from the ListView to the opposing ListView.
     *
     * @param event the MouseEvent
     * @param model the SelectionModel to operate against
     */
    private void handleOrgansClick(MouseEvent event, MultipleSelectionModel<String> model) {
        if (event.getButton() == MouseButton.PRIMARY) {
            model.clearSelection();

            if (event.getClickCount() == 2) {
                model.clearSelection();
                switchOrgans();
            }
        }
    }

    /**
     * Cancel the current changes in the view and close the window.
     */
    public void onBtnCancelClicked() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    /**
     * Save the changes made in the current view and close the window.
     */
    public void onBtnSaveClicked() {
        profile.setReceiver(true);
        Set<String> set = new HashSet<>(observableListOrgansRequired);
        profile.setOrgansRequired(set);
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    private void refresh() {
        viewOrgansRequired.refresh();
        viewOrgansAvailable.refresh();
    }

    /**
     * Configure the currently selected profile
     *
     * @param profile the profile to operate against.
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    /**
     * Take the selected organ from the ListView and move it to the other ListView.
     */
    private void switchOrgans() {
        final int selectedIdxAvailable = viewOrgansAvailable.getFocusModel().getFocusedIndex();
        if (selectedIdxAvailable != -1) {
            String itemToRemove = viewOrgansAvailable.getSelectionModel().getSelectedItem();
            observableListOrgansAvailable.remove(itemToRemove);
            observableListOrgansRequired.add(itemToRemove);
        } else {
            final int selectedIdxRequired = viewOrgansRequired.getSelectionModel().getSelectedIndex();
            if(selectedIdxRequired != -1) {
                String itemToRemove = viewOrgansRequired.getSelectionModel().getSelectedItem();
                observableListOrgansRequired.remove(itemToRemove);
                observableListOrgansAvailable.add(itemToRemove);
                giveReasonForRemoval(itemToRemove);
            }
        }

        Collections.sort(observableListOrgansRequired);
        Collections.sort(observableListOrgansAvailable);
        refresh();

        viewOrgansAvailable.getSelectionModel().clearSelection();
        viewOrgansRequired.getSelectionModel().clearSelection();
    }

    private void giveReasonForRemoval(String organ) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/OrganRemoval.fxml"));

        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        OrganRemovalController controller = fxmlLoader.<OrganRemovalController>getController();
        controller.initialize(organ);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Organ removal");
        stage.show();
    }
}
