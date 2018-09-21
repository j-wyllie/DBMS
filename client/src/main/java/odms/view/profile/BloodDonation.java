package odms.view.profile;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import odms.commons.model.profile.Profile;
import lombok.extern.slf4j.Slf4j;


/**
 * View for the blood donation tab.
 */
@Slf4j
public class BloodDonation {
    @FXML
    private Button btnDonate;
    @FXML
    private Button btnCancel;
    @FXML
    private CheckBox plasmaCheckBox;
    @FXML
    private Label lblPoints;

    private Profile profile;

    private int bloodTypePoints;

    private ProfileMedical parent;

    private odms.controller.profile.BloodDonation controller = new odms.controller.profile.BloodDonation(this);

    /**
     * Method that is called when cancel button is clicked to close the window.
     */
    public void onBtnCancelClicked() {

        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();

    }

    /**
     * Method that is called when donate button is clicked to increment points and close window.
     */
    public void onBtnDonateClicked() {
        controller.updatePoints(profile, getPlasmaChecked());
        parent.updateBloodDonationLabel();
        Stage stage = (Stage) btnDonate.getScene().getWindow();
        stage.close();
    }

    /**
     * Method that sets up the view.
     * @param currentProfile the profile that is currently selected.
     * @param view The parent view.
     */
    public void initialize(Profile currentProfile, ProfileMedical view) {
        profile = currentProfile;
        parent = view;
        setLabel();
    }

    /**
     * Sets the label with the number of earnable points
     */
    private void setLabel() {
        lblPoints.setText("Points Earnable: " + controller.setPoints(profile));
    }

    /**
     * Updates label with correct text if checkbox is checked.
     */
    public void onCheckBoxChecked() {
        if (plasmaCheckBox.isSelected()) {
            lblPoints.setText("Points Earnable: " + (controller.setPoints(profile) + 2));
        } else {
            setLabel();
        }
    }

    public Profile getProfile() {
        return profile;
    }

    public boolean getPlasmaChecked() {
        return plasmaCheckBox.isSelected();
    }
}
