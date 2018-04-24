package odms.controller;

import static odms.controller.AlertController.DonorCancelChanges;
import static odms.controller.AlertController.DonorSaveChanges;
import static odms.controller.AlertController.GuiPopup;
import static odms.controller.LoginController.getCurrentDonor;
import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
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
import odms.commandlineview.CommandUtils;
import odms.data.DonorDataIO;
import odms.donor.Donor;

public class EditDonorProfileController {

    private Donor currentDonor;
    private Donor searchedDonor;

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
        Parent parent = FXMLLoader.load(getClass().getResource("/view/EditDonorProfile.fxml"));
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
                    "Donor " + currentDonor.getId() + " updated details previous = " + currentDonor
                            .getAttributesSummary() + " new = ";
            currentDonor.setGivenNames(givenNamesField.getText());
            currentDonor.setLastNames(lastNamesField.getText());
            currentDonor.setIrdNumber(Integer.valueOf(irdField.getText()));
            //currentDonor.setDateOfBirth(Date.parse(dobField.getText()));
            //currentDonor.setDateOfDeath(LocalDate.parse(dodField.getText()));
            currentDonor.setGender(genderField.getText());
            if(heightField.getText() == null) {
                currentDonor.setHeight(Double.valueOf(heightField.getText()));
            }
            if(heightField.getText() == null) {
                currentDonor.setWeight(Double.valueOf(weightField.getText()));
            }
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
//            try {
//                Set<String> set = new HashSet<>(Arrays.asList(organField.getText().split(", ")));
//                if(set != null){
//                    currentDonor.setRegistered(true);
//                    currentDonor.addOrgans(set);
//                }
//
//                } catch (Exception e){
//                e.printStackTrace();
//                    error = true;
//                }
//
//            try {
//                Set<String> set = new HashSet<>(Arrays.asList(donationsField.getText().split(", ")));
//                if(set != null){
//                    currentDonor.setRegistered(true);
//                    currentDonor.addDonations(set);
//                }
//
//            } catch (Exception e){
//                e.printStackTrace();
//                error = true;
//            }

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
            if(error == true) {
                GuiPopup("Error. Not all fields were updated.");
            }

            DonorDataIO.saveDonors(getCurrentDatabase(), "example/example.json");

            if(getCurrentDonor() != null){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DonorProfile.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                appStage.setScene(scene);
                appStage.show();

            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DonorProfile.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                DonorProfileController controller = fxmlLoader.<DonorProfileController>getController();
                controller.setDonor(currentDonor);
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
            Parent parent = FXMLLoader.load(getClass().getResource("/view/DonorProfile.fxml"));
            Scene newScene = new Scene(parent);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(newScene);
            appStage.show();
        }
    }

    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize() {
        if(currentDonor == null){
            currentDonor = getCurrentDonor();
        }
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

    public void setDonor(Donor donor) {
        currentDonor = donor;
    }
}
