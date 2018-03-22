package odms.controller;

import static odms.controller.AlertController.InvalidUsername;
import static odms.controller.LoginController.getCurrentDonor;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.donor.Donor;

public class EditDonorProfileController {

    private static Donor currentDonor = getCurrentDonor();

    /**
     * Label to display the user's full name.
     */
    @FXML
    private Label donorFullNameLabel;

    /**
     * Label to display the user's donor status.
     */
    @FXML
    private Label donorStatusLabel;

    /**
     * Field to edit the user's given names.
     */
    @FXML
    private TextField givenNamesField;

    /**
     * Field to edit the user's surnames.
     */
    @FXML
    private TextField lastNamesField;

    /**
     * Field to edit the user's ird number.
     */
    @FXML
    private TextField irdField;

    /**
     * Field to edit the user's date of birth.
     */
    @FXML
    private TextField dobField;

    /**
     * Field to edit the user's date of death.
     */
    @FXML
    private TextField dodField;

    /**
     * Field to edit the user's gender.
     */
    @FXML
    private TextField genderField;

    /**
     * Field to edit the user's height.
     */
    @FXML
    private TextField heightField;

    /**
     * Field to edit the user's weight.
     */
    @FXML
    private TextField weightField;

    /**
     * Field to edit the user's phone number.
     */
    @FXML
    private TextField phoneField;

    /**
     * Field to edit the user's email.
     */
    @FXML
    private TextField emailField;

    /**
     * Field to edit the user's address.
     */
    @FXML
    private TextField addressField;

    /**
     * Field to edit the user's region.
     */
    @FXML
    private TextField regionField;

    /**
     * Field to edit the user's blood type.
     */
    @FXML
    private TextField bloodTypeField;

    /**
     * Field to edit the user's smoker status.
     */
    @FXML
    private TextField smokerField;

    /**
     * Field to edit the user's alcohol consumption.
     */
    @FXML
    private TextField alcoholConsumptionField;

    /**
     * Field to edit the user's blood pressure.
     */
    @FXML
    private TextField bloodPressureField;

    /**
     * Field to edit the user's region.
     */
    @FXML
    private TextField chronicDiseasesField;

    /**
     * Field to edit the user's organs to donate.
     */
    @FXML
    private TextField organsField;

    /**
     * Field to edit the user's region.
     */
    @FXML
    private TextField donationsField;


    /**
     * Scene change to log in view.
     * @param event clicking on the logout button.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
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
     * Button handler to save the changes made to the fields.
     * @param event clicking on the save (tick) button.
     */
    @FXML
    private void handleSaveButtonClicked(ActionEvent event) throws IOException {

    }

    /**
     * Button handler to cancel the changes made to the fields.
     * @param event clicking on the cancel (x) button.
     */
    @FXML
    private void handleCancelButtonClicked(ActionEvent event) throws IOException {

    }

    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize() {

        try {
            donorFullNameLabel.setText(currentDonor.getGivenNames() + " " + currentDonor.getLastNames());

            if (currentDonor.getGivenNames() != null) {
                givenNamesField.setText(currentDonor.getGivenNames());
            }
            if (currentDonor.getLastNames() != null) {
                lastNamesField.setText(currentDonor.getLastNames());
            }
            if (currentDonor.getIrdNumber() != null) {
                irdField.setText(currentDonor.getIrdNumber().toString());
            }
            if (currentDonor.getDateOfBirth() != null) {
                dobField.setText(currentDonor.getDateOfBirth().toString());
            }
            if (currentDonor.getDateOfDeath() != null) {
                dodField.setText(currentDonor.getDateOfDeath().toString());
            }
            if (currentDonor.getGender() != null) {
                genderField.setText(currentDonor.getGender());
            }
            heightField.setText(String.valueOf(currentDonor.getHeight()));
            weightField.setText(String.valueOf(currentDonor.getWeight()));
            phoneField.setText(currentDonor.getPhone());
            emailField.setText(currentDonor.getEmail());

            if (currentDonor.getAddress() != null) {
                addressField.setText(currentDonor.getAddress());
            }
            if (currentDonor.getRegion() != null) {
                regionField.setText(currentDonor.getRegion());
            }
            if (currentDonor.getBloodType() != null) {
                bloodTypeField.setText(currentDonor.getBloodType());
            }
            if (currentDonor.getSmoker() != null) {
                smokerField.setText(currentDonor.getSmoker().toString());
            }
            if (currentDonor.getAlcoholConsumption() != null) {
                alcoholConsumptionField.setText(currentDonor.getAlcoholConsumption());
            }
            if (currentDonor.getBloodPressure() != null) {
                bloodPressureField.setText(currentDonor.getBloodPressure());
            }
            chronicDiseasesField.setText(currentDonor.getChronicDiseasesAsCSV());
            organsField.setText(currentDonor.getOrgansAsCSV());
            donationsField.setText(currentDonor.getDonationsAsCSV());
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
