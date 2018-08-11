package odms.view.profile;

import java.io.File;
import java.net.MalformedURLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import odms.controller.data.ImageDataIO;
import odms.model.profile.Profile;

import java.io.IOException;
import odms.view.CommonView;

import static odms.controller.AlertController.invalidUsername;

public class Display extends CommonView {

    private Profile currentProfile;

    /**
     * Text for showing recent edits.
     */
    @FXML
    private Text editedText;
    @FXML
    private Label donorFullNameLabel;
    @FXML
    private Label donorStatusLabel;
    @FXML
    private Label nhiLabel;
    @FXML
    private Label receiverStatusLabel;
    @FXML
    private Tab tabGeneral;
    @FXML
    private Tab tabMedical;
    @FXML
    private Tab tabMedicalHistory;
    @FXML
    private Tab tabMedications;
    @FXML
    private Tab tabOrgans;
    @FXML
    private Tab tabHistory;
    @FXML
    private Tab tabProcedures;
    @FXML
    private Button logoutButton;
    @FXML
    private ImageView profileImage;

    private Boolean isOpenedByClinician = false;

    // Displays in IntelliJ as unused but is a false positive
    // The FXML includes operate this way and allow access to the instantiated controller.

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
        currentProfile = null;
        changeScene(event, "/view/Login.fxml");
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

            if (currentProfile.getNhi() != null) {
                nhiLabel.setText("NHI : " + currentProfile.getNhi());
            }

            setProfileImage();

        } catch (Exception e) {
            e.printStackTrace();
            invalidUsername();
        }
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    @FXML
    public void onTabGeneralSelected() {
        if (currentProfile != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileGeneralTab.fxml"));
            try {
                tabGeneral.setContent(loader.load());
                ProfileGeneral profileGeneralView = loader.getController();
                profileGeneralView.initialize(currentProfile, isOpenedByClinician);
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    private void onTabOrgansSelected() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileOrganOverview.fxml"));
        try {
            tabOrgans.setContent(loader.load());
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        OrganDisplay organsView = loader.getController();
        organsView.initialize(currentProfile, isOpenedByClinician);
    }

    @FXML
    public void onTabMedicalSelected() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileMedicalTab.fxml"));
        try {
            tabMedical.setContent(loader.load());
            ProfileMedical profileMedicalViewTODO = loader.getController();
            profileMedicalViewTODO.initialize(currentProfile, isOpenedByClinician);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onTabHistorySelected() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileHistoryTab.fxml"));
        try {
            tabHistory.setContent(loader.load());
            ProfileHistory profileHistoryViewTODO = loader.getController();
            profileHistoryViewTODO.initialize(currentProfile);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onTabMedicationsSelected() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileMedicationsTab.fxml"));
        try {
            tabMedications.setContent(loader.load());
            MedicationsGeneral profileMedicationsView = loader.getController();
            profileMedicationsView.initialize(currentProfile, isOpenedByClinician);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onTabMedicalHistorySelected() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileMedicalHistoryTab.fxml"));
        try {
            tabMedicalHistory.setContent(loader.load());
            ProfileMedicalHistory profileMedicalHistoryView = loader.getController();
            profileMedicalHistoryView.initialize(currentProfile, isOpenedByClinician);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onTabProceduresSelected() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileProceduresTab.fxml"));
        try {
            tabProcedures.setContent(loader.load());
            ProceduresDisplay profileProceduresView = loader.getController();
            profileProceduresView.initialize(currentProfile, isOpenedByClinician);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    private void setProfileImage() throws MalformedURLException {
        File image = ImageDataIO.getImagePath(currentProfile.getPictureName());

        if (!image.exists()) {
            image = new File(
                    new File("."),
                    "src/main/resources/profile_images/default.png"
            );
        }

        profileImage.setImage(new Image(image.toURI().toURL().toString()));
    }

    /**
     * Sets the current donor attributes to the labels on start up.
     *
     * @param profile to be used
     * @param isOpenedByClinician boolean, if true profile has been opened by a clinician/admin
     */
    public void initialize(Profile profile, Boolean isOpenedByClinician) {
        this.isOpenedByClinician = isOpenedByClinician;
        if (isOpenedByClinician) {
            logoutButton.setVisible(false);
        }
        currentProfile = profile;
        setPage(profile);
        onTabGeneralSelected();
    }
}
