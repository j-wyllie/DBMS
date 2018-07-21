package odms.view.profile;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import odms.controller.CommonController;
import odms.model.profile.Profile;

import java.io.IOException;

import static odms.controller.AlertController.invalidUsername;

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
    private Label receiverStatusLabel;

    protected ObjectProperty<Profile> currentProfileBound = new SimpleObjectProperty<>();
    private Boolean isOpenedByClinician = false;
    // Displays in IntelliJ as unused but is a false positive
    // The FXML includes operate this way and allow access to the instantiated controller.

    private ProfileGeneralView profileGeneralView
             = new ProfileGeneralView();
    private ProfileOrgansView profileOrgansView = new ProfileOrgansView();
    private ProfileMedicalViewTODO profileMedicalViewTODO = new ProfileMedicalViewTODO();
    private ProfileHistoryViewTODO profileHistoryViewTODO = new ProfileHistoryViewTODO();
    private ProfileMedicationsView profileMedicationsView = new ProfileMedicationsView();
    private ProfileMedicalHistoryView profileMedicalHistoryView = new ProfileMedicalHistoryView();
    private ProfileProceduresView profileProceduresView = new ProfileProceduresView();


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
        //todo showLoginScene(event);
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

            if (currentProfile.getReceiver()) {
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



    public Profile getCurrentProfile() {
        return currentProfile;
    }

    @FXML
    private void onTabOrgansSelected() {
        profileOrgansView = new ProfileOrgansView();
        profileOrgansView.currentProfile.bind(currentProfileBound);
        profileOrgansView.populateOrganLists();
    }

    @FXML
    public void onTabGeneralSelected() {
        if (currentProfileBound.get() != null) {
            profileGeneralView.currentProfile
                    .bind(currentProfileBound);
            profileGeneralView.setUpDetails();
        }
    }

    @FXML
    public void onTabMedicalSelected() {
        profileMedicalViewTODO.currentProfile.bind(currentProfileBound);
    }

    @FXML
    public void onTabHistorySelected() {
        profileHistoryViewTODO.currentProfile.bind(currentProfileBound);
    }

    @FXML
    public void onTabMedicationsSelected() {
        profileMedicationsView.currentProfile.bind(currentProfileBound);
    }

    @FXML
    public void onTabMedicalHistorySelected() {
        profileMedicalHistoryView.currentProfile.bind(currentProfileBound);
    }

    @FXML
    public void onTabProceduresSelected() {
        profileProceduresView.currentProfile.bind(currentProfileBound);
    }


    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize(Profile profile) {
        if (currentProfile != null) {
            currentProfileBound.set(profile);
            setPage(profile);
        }
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
