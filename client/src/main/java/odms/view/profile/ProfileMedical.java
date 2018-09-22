package odms.view.profile;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.controller.AlertController;
import odms.view.CommonView;

import java.io.IOException;

public class ProfileMedical extends CommonView {

    @FXML
    private Label bloodTypeLabel;
    @FXML
    private Label smokerLabel;
    @FXML
    private Label alcoholConsumptionLabel;
    @FXML
    private Label bloodPressureLabel;
    @FXML
    private Label bmiLabel;
    @FXML
    private Button bloodDonationButton;
    @FXML
    private Label bloodDonationLabel;

    private Profile currentProfile;
    // init controller corresponding to this view
    private odms.controller.profile.ProfileMedical controller =
            new odms.controller.profile.ProfileMedical(this);

    private void setUpDetails() {
        if (currentProfile.getAlcoholConsumption() != null) {
            alcoholConsumptionLabel.setText(
                    alcoholConsumptionLabel.getText() +
                            currentProfile.getAlcoholConsumption()
            );
        }
        if (currentProfile.getBloodPressure() != null) {
            bloodPressureLabel.setText(
                    bloodPressureLabel.getText() + currentProfile.getBloodPressure()
            );
        }
        if (currentProfile.getBloodType() != null) {
            bloodTypeLabel.setText(bloodTypeLabel.getText() + currentProfile.getBloodType());
        }
        if (currentProfile.getHeight() != 0.0 && currentProfile.getWeight() != 0.0) {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);

            bmiLabel.setText(bmiLabel.getText() + df.format(currentProfile.getBmi()));
        }
        if (currentProfile.getIsSmoker() != null) {
            smokerLabel.setText(smokerLabel.getText() + currentProfile.getIsSmoker());
        }
        updateBloodDonationLabel();
    }

    public void initialize(Profile p, Boolean isOpenedByClinician) {
        if (isOpenedByClinician) {
            bloodDonationButton.setVisible(false);
        } else {
            bloodDonationButton.setVisible(true);
        }
        currentProfile = p;
        if (currentProfile != null) {
            setUpDetails();
        }
    }

    /**
     * Updates the blood donation label after donating blood
     * in order to display the correct number of points.
     */
    public void updateBloodDonationLabel() {
        bloodDonationLabel.setText("Blood Donation Points: " +
                currentProfile.getBloodDonationPoints());
    }

    /**
     * Opens a window for donating blood.
     */
    @FXML
    public void onBtnDonationClicked(ActionEvent actionEvent) throws IOException {
        Node source = (Node) actionEvent.getSource();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/BloodDonation.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        BloodDonation view = fxmlLoader.getController();
        try {
            view.initialize(currentProfile, this);
            Stage stage = new Stage();
            stage.setTitle("Blood Donation");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initOwner(source.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.centerOnScreen();
            stage.show();
        } catch (NullPointerException e) {
            AlertController.guiPopup("You have not set a blood type yet.");
        }


    }
}
