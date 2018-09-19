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

    private Profile currentProfile;
    // init controller corresponding to this view
    private odms.controller.profile.ProfileMedical controller = new odms.controller.profile.ProfileMedical(this);
    private Boolean isOpenedByClinician;
    private User currentUser;

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
    }

    public void initialize(Profile p, Boolean isOpenedByClinician, User currentUser) {
        this.isOpenedByClinician = isOpenedByClinician;
        this.currentUser = currentUser;
        currentProfile = p;
        if (currentProfile != null) {
            setUpDetails();
        }
    }

    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {
        handleProfileEditButtonClicked(event, currentProfile, isOpenedByClinician, currentUser);
    }


    @FXML
    public void onBtnDonationClicked(ActionEvent actionEvent) throws IOException{
        Node source = (Node) actionEvent.getSource();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/BloodDonation.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        //OrganEdit view = fxmlLoader.getController();
        //view.initialize(currentProfile);

        Stage stage = new Stage();
        stage.setTitle("Blood Donation");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(source.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.centerOnScreen();
        stage.show();
    }
}
