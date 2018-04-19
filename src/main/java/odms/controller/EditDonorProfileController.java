package odms.controller;

import static odms.controller.AlertController.DonorCancelChanges;
import static odms.controller.AlertController.DonorSaveChanges;
import static odms.controller.LoginController.getCurrentProfile;
import static odms.controller.GuiMain.getCurrentDatabase;

import java.io.IOException;
import java.time.LocalDateTime;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.cli.CommandUtils;
import odms.data.ProfileDataIO;
import odms.profile.Profile;

public class EditDonorProfileController {

    private static Profile currentProfile = getCurrentProfile();

    /**
     * Label to display the user's full name.
     */
    @FXML
    private Label donorFullNameLabel;

    /**
     * Label to display the user's profile status.
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
        boolean saveBool = DonorSaveChanges();

        if (saveBool) {
            String action =
                    "Profile " + currentProfile.getId() + " updated details previous = " + currentProfile
                            .getAttributesSummary() + " new = ";
            currentProfile.setGivenNames(givenNamesField.getText());
            currentProfile.setLastNames(lastNamesField.getText());
            currentProfile.setIrdNumber(Integer.valueOf(irdField.getText()));
            //currentProfile.setDateOfBirth(Date.parse(dobField.getText()));
            //currentProfile.setDateOfDeath(LocalDate.parse(dodField.getText()));
            currentProfile.setGender(genderField.getText());
            currentProfile.setHeight(Double.valueOf(heightField.getText()));
            currentProfile.setWeight(Double.valueOf(weightField.getText()));
            currentProfile.setPhone(phoneField.getText());
            currentProfile.setEmail(emailField.getText());
            currentProfile.setAddress(addressField.getText());
            currentProfile.setRegion(regionField.getText());
            currentProfile.setBloodType(bloodTypeField.getText());

            if (bloodPressureField.getText().contains("/")) {
                String systolic = bloodPressureField.getText()
                        .substring(0, bloodPressureField.getText().indexOf("/")).trim();
                currentProfile.setBloodPressureSystolic(Integer.valueOf(systolic));
                String diastolic = bloodPressureField.getText()
                        .substring(bloodPressureField.getText().lastIndexOf('/') + 1).trim();
                currentProfile.setBloodPressureDiastolic(Integer.valueOf(diastolic));
            }

            currentProfile.setSmoker(Boolean.valueOf(smokerField.getText()));
            currentProfile.setAlcoholConsumption(alcoholConsumptionField.getText());
            action = action + currentProfile.getAttributesSummary() + " at " + LocalDateTime.now();
            if (CommandUtils.getHistory().size() != 0) {
                if (CommandUtils.getPosition() != CommandUtils.getHistory().size() - 1) {
                    CommandUtils.currentSessionHistory.subList(CommandUtils.getPosition(),
                            CommandUtils.getHistory().size() - 1).clear();
                }
            }
            CommandUtils.currentSessionHistory.add(action);
            CommandUtils.historyPosition = CommandUtils.currentSessionHistory.size() - 1;
            /*currentProfile.setOrgans();
            currentProfile.setDonations();*/

            if (diseaseField.getText().contains("/")) {
                String[] diseases = diseaseField.getText().split(", ");
                Set<String> diseasesSet = new HashSet<>(Arrays.asList(diseases));
                currentProfile.setChronicDiseases(diseasesSet);
            }

            ProfileDataIO.saveData(getCurrentDatabase(), "example/example.json");

            Parent parent = FXMLLoader.load(getClass().getResource("/view/DonorProfile.fxml"));
            Scene newScene = new Scene(parent);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(newScene);
            appStage.show();
        }
        else {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/DonorProfile.fxml"));
            Scene newScene = new Scene(parent);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(newScene);
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
            appStage.show();
        }
    }

    /**
     * Sets the current profile attributes to the labels on start up.
     */
    @FXML
    public void initialize() {

        try {
            donorFullNameLabel.setText(currentProfile.getGivenNames() + " " + currentProfile.getLastNames());

            donorStatusLabel.setText(donorStatusLabel.getText() + "Unregistered");

            if (currentProfile.getRegistered() != null && currentProfile.getRegistered()) {
                donorStatusLabel.setText(donorStatusLabel.getText() + "Registered");
            }

            if (currentProfile.getGivenNames() != null) {
                givenNamesField.setText(currentProfile.getGivenNames());
            }
            if (currentProfile.getLastNames() != null) {
                lastNamesField.setText(currentProfile.getLastNames());
            }
            if (currentProfile.getIrdNumber() != null) {
                irdField.setText(currentProfile.getIrdNumber().toString());
            }
            if (currentProfile.getDateOfBirth() != null) {
                dobField.setText(currentProfile.getDateOfBirth().toString());
            }
            if (currentProfile.getDateOfDeath() != null) {
                dodField.setText(currentProfile.getDateOfDeath().toString());
            }
            if (currentProfile.getGender() != null) {
                genderField.setText(currentProfile.getGender());
            }
            heightField.setText(String.valueOf(currentProfile.getHeight()));
            weightField.setText(String.valueOf(currentProfile.getWeight()));
            phoneField.setText(currentProfile.getPhone());
            emailField.setText(currentProfile.getEmail());

            if (currentProfile.getAddress() != null) {
                addressField.setText(currentProfile.getAddress());
            }
            if (currentProfile.getRegion() != null) {
                regionField.setText(currentProfile.getRegion());
            }
            if (currentProfile.getBloodType() != null) {
                bloodTypeField.setText(currentProfile.getBloodType());
            }
            if (currentProfile.getSmoker() != null) {
                smokerField.setText(currentProfile.getSmoker().toString());
            }
            if (currentProfile.getAlcoholConsumption() != null) {
                alcoholConsumptionField.setText(currentProfile.getAlcoholConsumption());
            }
            if (currentProfile.getBloodPressure() != null) {
                bloodPressureField.setText(currentProfile.getBloodPressure());
            }
            diseaseField.setText(currentProfile.getChronicDiseasesAsCSV());
            organField.setText(currentProfile.getOrgansAsCSV());
            donationsField.setText(currentProfile.getDonationsAsCSV());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
