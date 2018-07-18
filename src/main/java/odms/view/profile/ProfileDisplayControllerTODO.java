package odms.view.profile;

import static odms.controller.AlertController.invalidUsername;

import java.io.IOException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import odms.controller.CommonController;
import odms.model.profile.Profile;

public class ProfileDisplayControllerTODO extends CommonController {

    public Profile currentProfile;
    /**
     * Text for showing recent edits.
     */
    @FXML
    public Text editedText;
    @FXML
    private Label donorFullNameLabel;
    @FXML
    private Label donorStatusLabel;
    @FXML
    private Label userIdLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private Button buttonViewMedicationHistory;
    @FXML
    private Button addNewProcedureButton;
    @FXML
    private Button deleteProcedureButton;
    @FXML
    private Label receiverStatusLabel;

    protected ObjectProperty<Profile> currentProfileBound = new SimpleObjectProperty<>();
    private Boolean isOpenedByClinician = false;
    // Displays in IntelliJ as unused but is a false positive
    // The FXML includes operate this way and allow access to the instantiated controller.
    @FXML
    private AnchorPane profileOrganOverview;
    @FXML
    private ProfileOrgansView profileOrgansView;

    @FXML
    private ProfileGeneralViewTODOReplacesDisplayController profileGeneralViewTODOReplacesDisplayController;
    @FXML
    private ProfileMedicalViewTODO profileMedicalViewTODO;
    @FXML
    private ProfileHistoryViewTODO profileHistoryViewTODO;
    @FXML
    private ProfileMedicationsView profileMedicationsView;


    /**
     * Called when there has been an edit to the current profile.
     */
    public void editedTextArea() {
        editedText.setText("The profile was successfully edited.");
    }


    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
        showLoginScene(event);
    }


    /**
     * sets all of the items in the fxml to their respective values
     *
     * @param currentProfile donors profile
     */
    @FXML
    private void setPage(Profile currentProfile) {

        try {
            donorFullNameLabel.setText(currentProfile.getFullName());
            donorStatusLabel.setText(donorStatusLabel.getText() + "Unregistered");
            receiverStatusLabel.setText(receiverStatusLabel.getText() + "Unregistered");

            if (currentProfile.getDonor() != null && currentProfile.getDonor()) {
                if (currentProfile.getOrgansDonated().size() > 0) {
                    donorStatusLabel.setText("Donor Status: Registered");
                }
            }

            if (currentProfile.getOrgansRequired().size() < 1) {
                currentProfile.setReceiver(false);
            } else {
                currentProfile.setReceiver(true);
            }

            if (currentProfile.isReceiver()) {
                receiverStatusLabel.setText("Receiver Status: Registered");
            }

            if (currentProfile.getId() != null) {
                userIdLabel
                        .setText(userIdLabel.getText() + Integer.toString(currentProfile.getId()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            invalidUsername();
        }
    }


    /**
     * Enables the relevant buttons on medications tab for how many drugs are selected
     */
    @FXML
    private void refreshPageElements() {
        hideItems();
    }


    /**
     * hides items that shouldn't be visible to either a donor or clinician
     */
    @FXML
    private void hideItems() {
        if (isOpenedByClinician) {
            //user is a clinician looking at donors profile, maximise functionality
            addNewProcedureButton.setVisible(true);
            deleteProcedureButton.setVisible(true);
            buttonViewMedicationHistory.setVisible(true);

            logoutButton.setVisible(false);
        } else {
            // user is a standard profile, limit functionality
            addNewProcedureButton.setVisible(false);
            deleteProcedureButton.setVisible(false);
            buttonViewMedicationHistory.setVisible(false);
        }
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    @FXML
    private void onTabOrgansSelected() {
        profileOrgansView.currentProfile.bind(currentProfileBound);
        profileOrgansView.populateOrganLists();
    }

    @FXML
    public void onTabGeneralSelected() {
        profileGeneralViewTODOReplacesDisplayController.currentProfile.bind(currentProfileBound);
    }

    @FXML
    public void onTabMedicalSelected() {
        profileMedicalViewTODO.currentProfile.bind(currentProfileBound);
    }

    @FXML
    public void onTabHistorySelected() {
        profileHistoryViewTODO.currentProfile.bind(currentProfileBound);
    }

    public void onTabMedicationsSelected() {
        profileMedicationsView.currentProfile.bind(currentProfileBound);
    }


    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize() {

        if (currentProfile != null) {
            currentProfileBound.set(currentProfile);
            setPage(currentProfile);
        }

        refreshPageElements();

    }


    /**
     * sets the profile if it is being opened by a clinician If opened by clinician, set appropriate
     * boolean and profile
     *
     * @param profile to be used
     */
    public void setProfileViaClinician(Profile profile) {
        isOpenedByClinician = true;
        currentProfile = profile;
    }

    /**
     * sets the donor if it was logged in by a user If logged in normally, sets profile
     *
     * @param profile to be used
     */
    public void setProfile(Profile profile) {
        currentProfile = profile;
    }


}
