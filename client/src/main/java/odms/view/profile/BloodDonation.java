package odms.view.profile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import odms.commons.model.profile.Profile;

import java.time.LocalDateTime;

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

    public void onBtnCancelClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();

    }

    public void onBtnDonateClicked(ActionEvent actionEvent) {
        if(plasmaCheckBox.isSelected()) {
            bloodTypePoints+= 2;
        }
        LocalDateTime timestamp = LocalDateTime.now();
        System.out.println(bloodTypePoints);
        Stage stage = (Stage) btnDonate.getScene().getWindow();
        stage.close();
    }

    public void setPoints() {
        switch (profile.getBloodType()){
            case "O+":
                bloodTypePoints = 2;
                break;
            case "O-":
                bloodTypePoints = 3;
                break;
            case "A+":
                bloodTypePoints = 2;
                break;
            case "A-":
                bloodTypePoints = 4;
                break;
            case "B+":
                bloodTypePoints = 3;
                break;
            case "B-":
                bloodTypePoints = 5;
                break;
            case "AB+":
                bloodTypePoints = 4;
                break;
            case "AB-":
                bloodTypePoints = 5;
                break;
        }
        try {
            if (LocalDateTime.now().minusDays(365).isBefore(profile.getLastBloodDonation())) {
                bloodTypePoints += 1;
            }
        } catch (NullPointerException e) {

        }
    }

    public void initialize(Profile currentProfile) {
        profile = currentProfile;
        setPoints();
        setLabel();
    }

    private void setLabel() {
        lblPoints.setText("Points Earnable: " + bloodTypePoints);
    }

    public void onCheckBoxChecked(ActionEvent actionEvent) {
        if(plasmaCheckBox.isSelected()) {
            lblPoints.setText("Points Earnable: " + (bloodTypePoints+2));
        } else {
            setLabel();
        }
    }
}
