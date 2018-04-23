package odms.controller;

import static odms.controller.AlertController.DonorCancelChanges;
import static odms.controller.AlertController.DonorSaveChanges;
import static odms.controller.LoginController.getCurrentDonor;
import static odms.controller.GuiMain.getCurrentDatabase;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import javafx.util.Duration;
import odms.commandlineview.CommandUtils;
import odms.data.DonorDataIO;
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
    private TextField diseaseField;

    /**
     * Field to edit the user's organs to donate.
     */
    @FXML
    private TextField organField;

    /**
     * Field to edit the user's region.
     */
    @FXML
    private TextField donationsField;


    /**
     * Changes the Edit Profile title to include an asterix to indicate a value has been edited.
     * @param event Any key event within the text boxes.
     */

    @FXML
    private void editTrue(javafx.scene.input.KeyEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setTitle("Edit Profile (*)");
    }

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
        appStage.setTitle("Login");
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
        boolean saveBool = DonorSaveChanges();

        if (saveBool) {
            String action =
                    "Donor " + currentDonor.getId() + " updated details previous = " + currentDonor
                            .getAttributesSummary() + " new = ";
            currentDonor.setGivenNames(givenNamesField.getText());
            currentDonor.setLastNames(lastNamesField.getText());
            currentDonor.setIrdNumber(Integer.valueOf(irdField.getText()));
            //currentDonor.setDateOfBirth(Date.parse(dobField.getText()));
            //currentDonor.setDateOfDeath(LocalDate.parse(dodField.getText()));
            currentDonor.setGender(genderField.getText());
            currentDonor.setHeight(Double.valueOf(heightField.getText()));
            currentDonor.setWeight(Double.valueOf(weightField.getText()));
            currentDonor.setPhone(phoneField.getText());
            currentDonor.setEmail(emailField.getText());
            currentDonor.setAddress(addressField.getText());
            currentDonor.setRegion(regionField.getText());
            currentDonor.setBloodType(bloodTypeField.getText());

            if (bloodPressureField.getText().contains("/")) {
                String systolic = bloodPressureField.getText()
                        .substring(0, bloodPressureField.getText().indexOf("/")).trim();
                currentDonor.setBloodPressureSystolic(Integer.valueOf(systolic));
                String diastolic = bloodPressureField.getText()
                        .substring(bloodPressureField.getText().lastIndexOf('/') + 1).trim();
                currentDonor.setBloodPressureDiastolic(Integer.valueOf(diastolic));
            }

            currentDonor.setSmoker(Boolean.valueOf(smokerField.getText()));
            currentDonor.setAlcoholConsumption(alcoholConsumptionField.getText());
            action = action + currentDonor.getAttributesSummary() + " at " + LocalDateTime.now();
            if (CommandUtils.getHistory().size() != 0) {
                if (CommandUtils.getPosition() != CommandUtils.getHistory().size() - 1) {
                    CommandUtils.currentSessionHistory.subList(CommandUtils.getPosition(),
                            CommandUtils.getHistory().size() - 1).clear();
                }
            }
            CommandUtils.currentSessionHistory.add(action);
            CommandUtils.historyPosition = CommandUtils.currentSessionHistory.size() - 1;
            /*currentDonor.setOrgans();
            currentDonor.setDonations();*/

            if (diseaseField.getText().contains("/")) {
                String[] diseases = diseaseField.getText().split(", ");
                Set<String> diseasesSet = new HashSet<>(Arrays.asList(diseases));
                currentDonor.setChronicDiseases(diseasesSet);
            }

            DonorDataIO.saveDonors(getCurrentDatabase(), "example/example.json");

            Parent parent = FXMLLoader.load(getClass().getResource("/view/DonorProfile.fxml"));
            Scene newScene = new Scene(parent);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(newScene);
            appStage.setTitle("Donor Profile");
            appStage.show();
        }
        else {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/DonorProfile.fxml"));
            Scene newScene = new Scene(parent);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(newScene);
            appStage.setTitle("Donor Profile");
            appStage.show();

        }
    }

    /**
     * Button handler to cancel the changes made to the fields.
     * @param event clicking on the cancel (x) button.
     */
    @FXML
    private void handleCancelButtonClicked(ActionEvent event) throws IOException {
        boolean cancelBool = DonorCancelChanges();
        System.out.println(cancelBool);

        if (cancelBool) {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/DonorProfile.fxml"));
            Scene newScene = new Scene(parent);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(newScene);
            appStage.setTitle("Donor Profile");
            appStage.show();
        }
    }

    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize() {

        try {
            donorFullNameLabel.setText(currentDonor.getGivenNames() + " " + currentDonor.getLastNames());

            donorStatusLabel.setText(donorStatusLabel.getText() + "Unregistered");

            if (currentDonor.getRegistered() != null && currentDonor.getRegistered() == true) {
                donorStatusLabel.setText(donorStatusLabel.getText() + "Registered");
            }

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
            diseaseField.setText(currentDonor.getChronicDiseasesAsCSV());
            organField.setText(currentDonor.getOrgansAsCSV());
            donationsField.setText(currentDonor.getDonationsAsCSV());
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
