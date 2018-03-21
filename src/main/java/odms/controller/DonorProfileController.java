package odms.controller;

import static odms.controller.AlertController.InvalidUsername;
import static odms.controller.LoginController.getCurrentDonor;
import static odms.controller.LoginController.getCurrentDonor;

import java.io.Console;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import odms.donor.Donor;

public class DonorProfileController {

    private static Donor currentDonor = getCurrentDonor();

    /**
     * Label to display the user's full name.
     */
    @FXML
    private Label donorFullNameLabel;

    /**
     * Label to display the user's given names.
     */
    @FXML
    private Label givenNamesLabel;

    /**
     * Label to display the user's surnames.
     */
    @FXML
    private Label lastNamesLabel;

    /**
     * Label to display the user's ird number.
     */
    @FXML
    private Label irdLabel;

    /**
     * Label to display the user's date of birth.
     */
    @FXML
    private Label dobLabel;

    /**
     * Label to display the user's date of death.
     */
    @FXML
    private Label dodLabel;

    /**
     * Label to display the user's gender.
     */
    @FXML
    private Label genderLabel;

    /**
     * Label to display the user's height.
     */
    @FXML
    private Label heightLabel;

    /**
     * Label to display the user's weight.
     */
    @FXML
    private Label weightLabel;

    /**
     * Label to display the user's phone number.
     */
    @FXML
    private Label phoneLabel;

    /**
     * Label to display the user's email.
     */
    @FXML
    private Label emailLabel;

    /**
     * Label to display the user's address.
     */
    @FXML
    private Label addressLabel;

    /**
     * Label to display the user's region.
     */
    @FXML
    private Label regionLabel;

    /**
     * Label to display the user's blood type.
     */
    @FXML
    private Label bloodTypeLabel;

    /**
     * Label to display the user's smoker status.
     */
    @FXML
    private Label smokerLabel;

    /**
     * Label to display the user's alcohol consumption.
     */
    @FXML
    private Label alcoholConsumptionLabel;

    /**
     * Label to display the user's blood pressure.
     */
    @FXML
    private Label bloodPressureLabel;

    /**
     * Label to display the user's region.
     */
    @FXML
    private Label chronicDiseasesLabel;

    /**
     * Label to display the user's organs to donate.
     */
    @FXML
    private Label organsLabel;

    /**
     * Label to display the user's region.
     */
    @FXML
    private Label donationsLabel;


    /**
     * Scene change to log in view.
     * @param event clicking on the logout button.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("view/Login.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();

    }

    /**
     * Button handler to undo last action.
     * @param event clicking on the undo button.
     */
    @FXML
    private void handleUndoButtonClicked(ActionEvent event) throws IOException {
        //TODO
        //refresh scene.
    }

    /**
     * Button handler to redo last undo action.
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event) throws IOException {
        //TODO
        //refresh scene.
    }

    /**
     * Button handler to make fields editable.
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {
        //TODO
        //refresh scene.
    }

    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize() {

        try {
            donorFullNameLabel.setText(currentDonor.getGivenNames() + " " + currentDonor.getLastNames());

            if (currentDonor.getGivenNames() != null) {
                givenNamesLabel.setText(givenNamesLabel.getText() + currentDonor.getGivenNames());
            }
            if (currentDonor.getLastNames() != null) {
                lastNamesLabel.setText(lastNamesLabel.getText() + currentDonor.getLastNames());
            }
            if (currentDonor.getIrdNumber() != null) {
                irdLabel.setText(irdLabel.getText() + currentDonor.getIrdNumber());
            }
            if (currentDonor.getDateOfBirth() != null) {
                dobLabel.setText(dobLabel.getText() + currentDonor.getDateOfBirth());
            }
            if (currentDonor.getDateOfDeath() != null) {
                dodLabel.setText(dodLabel.getText() + currentDonor.getDateOfDeath());
            }
            if (currentDonor.getGender() != null) {
                genderLabel.setText(genderLabel.getText() + currentDonor.getGender());
            }
            heightLabel.setText(heightLabel.getText() + currentDonor.getHeight());
            weightLabel.setText(weightLabel.getText() + currentDonor.getWeight());
            phoneLabel.setText(phoneLabel.getText());
            emailLabel.setText(emailLabel.getText());

            if (currentDonor.getAddress() != null) {
                addressLabel.setText(addressLabel.getText() + currentDonor.getAddress());
            }
            if (currentDonor.getRegion() != null) {
                regionLabel.setText(regionLabel.getText() + currentDonor.getRegion());
            }
            if (currentDonor.getBloodType() != null) {
                bloodTypeLabel.setText(bloodTypeLabel.getText() + currentDonor.getBloodType());
            }
            /*if (currentDonor.getSmoker() != null) {
                smokerLabel.setText(smokerLabel.getText() + currentDonor.getSmoker());
            }*/
            /*if (currentDonor.getAlcoholConsumption() != null) {
                alcoholConsumptionLabel.setText(alcoholConsumptionLabel.getText() + currentDonor.getAlcoholConsumption());
            }*/
            /*if (currentDonor.getBloodPressure() != null) {
                bloodPressureLabel.setText(bloodPressureLabel.getText() + currentDonor.getBloodPressure());
            }*/
            if (currentDonor.getBloodPressure() != null) {
                bloodPressureLabel.setText(bloodPressureLabel.getText() + currentDonor.getBloodPressure());
            }
        }
        catch (Exception e) {
            InvalidUsername();
        }
    }
}
