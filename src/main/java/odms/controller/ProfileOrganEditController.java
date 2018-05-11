package odms.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import odms.enums.OrganSelectEnum;
import odms.enums.OrganEnum;
import odms.profile.OrganConflictException;
import odms.profile.Profile;

public class ProfileOrganEditController extends ProfileOrganCommonController {

    private ObservableList<String> observableListOrgansSelected = FXCollections.observableArrayList();

    @FXML
    private ListView<String> viewOrgansAvailable;

    @FXML
    private ListView<String> viewOrgansRequired;

    @FXML
    private Button btnOrganSwitch;

    @FXML
    private Button btnSave;

    @FXML
    private Label bannerLabel;

    private static OrganSelectEnum windowType;

    public void initialize() {
        bannerLabel.setText(windowType.toString());

        if (profile != null) {
            // Order of execution for building these is required due to removing items from the
            // Available list that are present in the Required list.
            buildOrgansSelected();
            buildOrgansAvailable(observableListOrgansSelected);
            viewOrgansAvailable.setItems(observableListOrgansAvailable);
            viewOrgansRequired.setItems(observableListOrgansSelected);
        }

        btnOrganSwitch.setOnAction(event -> handleBtnOrganSwitchClicked());
        viewOrgansAvailable.setOnMouseClicked(this::handleListOrgansAvailableClick);
        viewOrgansRequired.setOnMouseClicked(this::handleListOrgansRequiredClick);
    }

    /**
     * Populate the ListView with the organs the profile currently requires.
     */
    private void buildOrgansSelected() {
        Set<OrganEnum> organs = new HashSet<>();

        switch (windowType) {
            case DONATED:
                organs = profile.getOrgansDonated();
                break;
            case DONATING:
                organs = profile.getOrgansDonating();
                break;
            case REQUIRED:
                organs = profile.getOrgansRequired();
                break;
        }

        populateOrganList(observableListOrgansSelected, organs);
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
        HashSet<OrganEnum> organs = ProfileOrganEditController.observableListStringsToOrgans(
                new HashSet<>(observableListOrgansSelected)
        );

        switch (windowType) {
            case DONATED:
                profile.addOrgansDonated(organs);
                break;
            case DONATING:
                profile.setDonor(true);

                try {
                    organs.removeAll(profile.getOrgansDonating());
                    profile.addOrgansDonating(organs);
                } catch (OrganConflictException e) {
                    AlertController.invalidOrgan(e.getOrgan());
                }
                break;
            case REQUIRED:
                profile.setReceiver(true);

                profile.addOrgansRequired(organs);
                break;
        }

        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    /**
     * Convert an HashSet of Organ Strings to a HashSet of OrganEnum.
     *
     * @param organStrings strings to convert
     * @return set of OrganEnum
     */
    private static HashSet<OrganEnum> observableListStringsToOrgans(HashSet<String> organStrings) {
        List<String> correctedOrganStrings = new ArrayList<>();

        for (String organ : organStrings) {
            correctedOrganStrings.add(organ.trim().toUpperCase().replace(" ", "_"));
        }

        return OrganEnum.stringListToOrganSet(correctedOrganStrings);
    }

    /**
     * Refresh the listViews
     */
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
            observableListOrgansSelected.add(itemToRemove);
        } else {
            final int selectedIdxRequired = viewOrgansRequired.getSelectionModel().getSelectedIndex();
            if(selectedIdxRequired != -1) {
                String itemToRemove = viewOrgansRequired.getSelectionModel().getSelectedItem();
                observableListOrgansSelected.remove(itemToRemove);
                observableListOrgansAvailable.add(itemToRemove);
            }
        }

        Collections.sort(observableListOrgansSelected);
        Collections.sort(observableListOrgansAvailable);
        refresh();

        viewOrgansAvailable.getSelectionModel().clearSelection();
        viewOrgansRequired.getSelectionModel().clearSelection();
    }

    public static void setWindowType(OrganSelectEnum type) {
        windowType = type;
    }

}
