package odms.controller;

import static odms.controller.AlertController.DonorCancelChanges;
import static odms.controller.AlertController.DonorSaveChanges;
import static odms.controller.LoginController.getCurrentProfile;
import static odms.controller.AlertController.GuiPopup;
import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

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

    private Profile currentProfile;

    @FXML
    private Label donorFullNameLabel;

    @FXML
    private Label donorStatusLabel;

    @FXML
    private TextField givenNamesField;

    @FXML
    private TextField lastNamesField;

    @FXML
    private TextField irdField;

    @FXML
    private TextField dobField;

    @FXML
    private TextField dodField;

    @FXML
    private TextField genderField;

    @FXML
    private TextField heightField;

    @FXML
    private TextField weightField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField regionField;

    @FXML
    private TextField bloodTypeField;

    @FXML
    private TextField smokerField;

    @FXML
    private TextField alcoholConsumptionField;

    @FXML
    private TextField bloodPressureField;

    @FXML
    private TextField diseaseField;

    @FXML
    private TextField organField;

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
        undo();
    }

    /**
     * Button handler to redo last undo action.
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event) throws IOException {
        redo();
    }

    /**
     * Button handler to make fields editable.
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/ProfileEdit.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

    /**
     * Button handler to save the changes made to the fields.
     * @param event clicking on the save (tick) button.
     */
    @FXML
    private void handleSaveButtonClicked(ActionEvent event) throws IOException {
        boolean saveBool = DonorSaveChanges();
        boolean error = false;
        if (saveBool) {
            String action =
                    "Profile " + currentProfile.getId() + " updated details previous = " + currentProfile
                            .getAttributesSummary() + " new = ";
            currentProfile.setGivenNames(givenNamesField.getText());
            currentProfile.setLastNames(lastNamesField.getText());
            currentProfile.setIrdNumber(Integer.valueOf(irdField.getText()));
            //currentDonor.setDateOfBirth(Date.parse(dobField.getText()));
            //currentDonor.setDateOfDeath(LocalDate.parse(dodField.getText()));
            currentProfile.setGender(genderField.getText());
            if(heightField.getText() == null) {
                currentProfile.setHeight(Double.valueOf(heightField.getText()));
            }
            if(heightField.getText() == null) {
                currentProfile.setWeight(Double.valueOf(weightField.getText()));
            }
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
            try {
                Set<String> set = new HashSet<>(Arrays.asList(organField.getText().split(", ")));
                if(set != null){
                    currentProfile.setRegistered(true);
                    currentProfile.addOrgans(set);
                }

                } catch (Exception e){
                    error = true;
                }

            try {
                Set<String> set = new HashSet<>(Arrays.asList(donationsField.getText().split(", ")));
                if(set != null){
                    currentProfile.setRegistered(true);
                    currentProfile.addDonations(set);
                }

            } catch (Exception e){
                error = true;
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
            if (error) {
                GuiPopup("Error. Not all fields were updated.");
            }

            ProfileDataIO.saveData(getCurrentDatabase(), "example/example.json");

            if (getCurrentProfile() != null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                        .getResource("/view/ProfileDisplay.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                appStage.setScene(scene);
                appStage.show();

            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                        .getResource("/view/ProfileDisplay.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                DonorProfileController controller = fxmlLoader.<DonorProfileController>getController();
                controller.setDonor(currentProfile);
                controller.initialize();

                Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                appStage.setScene(scene);
                appStage.show();
            }
        }
    }

    /**
     * Button handler to cancel the changes made to the fields.
     * @param event clicking on the cancel (x) button.
     */
    @FXML
    private void handleCancelButtonClicked(ActionEvent event) throws IOException {
        boolean cancelBool = DonorCancelChanges();

        if (cancelBool) {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/ProfileDisplay.fxml"));
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

        if(currentProfile == null){
            currentProfile = getCurrentProfile();
        }
        if (currentProfile != null) {

            try {
                donorFullNameLabel.setText(currentProfile.getFullName());

                donorStatusLabel.setText("Donor Status: Unregistered");

                if (currentProfile.getRegistered() != null && currentProfile.getRegistered()) {
                    donorStatusLabel.setText("Donor Status: Registered");
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
//            if (currentProfile.getBloodPressure() != null) {
//                bloodPressureField.setText(currentProfile.getBloodPressure());
//            }
//            diseaseField.setText(currentProfile.getChronicDiseasesAsCSV());
                organField.setText(currentProfile.getOrgansAsCSV());
                donationsField.setText(currentProfile.getDonationsAsCSV());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setDonor(Profile donor) {
        currentProfile = donor;
    }
}
