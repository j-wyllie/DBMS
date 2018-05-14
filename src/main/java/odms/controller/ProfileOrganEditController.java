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
import odms.enums.OrganEnum;
import odms.enums.OrganSelectEnum;
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

        if (currentProfile.get() != null) {
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
                organs = currentProfile.get().getOrgansDonated();
                break;
            case DONATING:
                organs = currentProfile.get().getOrgansDonating();
                break;
            case REQUIRED:
                organs = currentProfile.get().getOrgansRequired();
                break;
        }

        populateOrganList(observableListOrgansSelected, organs);
    }

    /**
     * Support function to detect organs removed from the selected list view.
     * @param currentOrgans the current organ list to detect against
     * @param changedOrgans changed organs list to search with
     * @return a list of organs to remove from the profile
     */
    private HashSet<OrganEnum> findOrgansRemoved(HashSet<OrganEnum> currentOrgans,
            HashSet<OrganEnum> changedOrgans) {
        HashSet<OrganEnum> organsRemoved = new HashSet<>();
        for (OrganEnum organ : currentOrgans) {
            if (!changedOrgans.contains(organ)) {
                organsRemoved.add(organ);
            }
        }
        return organsRemoved;
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
        HashSet<OrganEnum> organsAdded = ProfileOrganEditController.observableListStringsToOrgans(
                new HashSet<>(observableListOrgansSelected)
        );
        HashSet<OrganEnum> organsRemoved;

        switch (windowType) {
            case DONATED:
                organsRemoved = findOrgansRemoved(
                        currentProfile.get().getOrgansDonated(),
                        organsAdded
                );

                currentProfile.get().addOrgansDonated(organsAdded);
                currentProfile.get().removeOrgansDonated(organsRemoved);
                break;
            case DONATING:
                try {
                    currentProfile.get().setDonor(true);

                    organsRemoved = findOrgansRemoved(
                            currentProfile.get().getOrgansDonating(),
                            organsAdded
                    );

                    organsAdded.removeAll(currentProfile.get().getOrgansDonating());
                    currentProfile.get().addOrgansDonating(organsAdded);
                    currentProfile.get().removeOrgansDonating(organsRemoved);
                } catch (OrganConflictException e) {
                    AlertController.invalidOrgan(e.getOrgan());
                }
                break;
            case REQUIRED:
                currentProfile.get().setReceiver(true);

                organsRemoved = findOrgansRemoved(
                        currentProfile.get().getOrgansRequired(),
                        organsAdded
                );

                currentProfile.get().addOrgansRequired(organsAdded);
                currentProfile.get().removeOrgansRequired(organsRemoved);
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
    private void refreshListViews() {
        viewOrgansRequired.refresh();
        viewOrgansAvailable.refresh();
    }

    /**
     * Configure the currently selected profile
     *
     * @param profile the profile to operate against.
     */
    public void setCurrentProfile(Profile profile) {
        this.currentProfile.set(profile);
    }

    /**
     * Take the selected organ from the ListView and move it to the other ListView.
     */
    private void switchOrgans() {
        if (viewOrgansAvailable.getFocusModel().getFocusedIndex() != -1) {
            String item = viewOrgansAvailable.getSelectionModel().getSelectedItem();
            observableListOrgansAvailable.remove(item);
            observableListOrgansSelected.add(item);
        } else if (viewOrgansRequired.getSelectionModel().getSelectedIndex() != -1) {
            String item = viewOrgansRequired.getSelectionModel().getSelectedItem();
            observableListOrgansSelected.remove(item);
            observableListOrgansAvailable.add(item);
        }

        Collections.sort(observableListOrgansSelected);
        Collections.sort(observableListOrgansAvailable);
        refreshListViews();

        viewOrgansAvailable.getSelectionModel().clearSelection();
        viewOrgansRequired.getSelectionModel().clearSelection();
    }

    public static void setWindowType(OrganSelectEnum type) {
        windowType = type;
    }

}
