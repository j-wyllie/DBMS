package odms.controller;

import static odms.controller.AlertController.DonorCancelChanges;
import static odms.controller.AlertController.DonorSaveChanges;
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
import odms.commandlineview.CommandUtils;
import odms.data.DonorDataIO;
import odms.donor.Donor;

public class EditSearchedDonorProfileController {

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
     * Button handler to save the changes made to the fields.
     * @param event clicking on the save (tick) button.
     */
    @FXML
    private void handleSaveButtonClicked(ActionEvent event) throws IOException {
        boolean saveBool = DonorSaveChanges();
        boolean error = false;
        if (saveBool) {
            String action =
                    "Donor " + searchedDonor.getId() + " updated details previous = " + searchedDonor
                            .getAttributesSummary() + " new = ";
            searchedDonor.setGivenNames(givenNamesField.getText());
            searchedDonor.setLastNames(lastNamesField.getText());
            searchedDonor.setIrdNumber(Integer.valueOf(irdField.getText()));
            //selectedDonor.setDateOfBirth(Date.parse(dobField.getText()));
            //selectedDonor.setDateOfDeath(LocalDate.parse(dodField.getText()));
            searchedDonor.setGender(genderField.getText());
            if(heightField.getText() == null) {
                searchedDonor.setHeight(Double.valueOf(heightField.getText()));
            }
            if(heightField.getText() == null) {
                searchedDonor.setWeight(Double.valueOf(weightField.getText()));
            }
            searchedDonor.setPhone(phoneField.getText());
            searchedDonor.setEmail(emailField.getText());
            searchedDonor.setAddress(addressField.getText());
            searchedDonor.setRegion(regionField.getText());
            searchedDonor.setBloodType(bloodTypeField.getText());

            if (bloodPressureField.getText().contains("/")) {
                String systolic = bloodPressureField.getText()
                        .substring(0, bloodPressureField.getText().indexOf("/")).trim();
                searchedDonor.setBloodPressureSystolic(Integer.valueOf(systolic));
                String diastolic = bloodPressureField.getText()
                        .substring(bloodPressureField.getText().lastIndexOf('/') + 1).trim();
                searchedDonor.setBloodPressureDiastolic(Integer.valueOf(diastolic));
            }
            try {
                Set<String> set = new HashSet<>(Arrays.asList(organField.getText().split(", ")));
                if(set.size() != 0){
                    searchedDonor.setRegistered(true);
                    searchedDonor.addOrgans(set);
                }

            } catch (Exception e){
                error = true;
            }

            try {
                Set<String> set = new HashSet<>(Arrays.asList(donationsField.getText().split(", ")));
                if(set.size() != 0){
                    searchedDonor.setRegistered(true);
                    searchedDonor.addDonations(set);
                }

            } catch (Exception e){
                error = true;
            }

            searchedDonor.setSmoker(Boolean.valueOf(smokerField.getText()));
            searchedDonor.setAlcoholConsumption(alcoholConsumptionField.getText());
            action = action + searchedDonor.getAttributesSummary() + " at " + LocalDateTime.now();
            if (CommandUtils.getHistory().size() != 0) {
                if (CommandUtils.getPosition() != CommandUtils.getHistory().size() - 1) {
                    CommandUtils.currentSessionHistory.subList(CommandUtils.getPosition(),
                            CommandUtils.getHistory().size() - 1).clear();
                }
            }
            CommandUtils.currentSessionHistory.add(action);
            CommandUtils.historyPosition = CommandUtils.currentSessionHistory.size() - 1;
            /*selectedDonor.setOrgans();
            selectedDonor.setDonations();*/

            if (diseaseField.getText().contains("/")) {
                String[] diseases = diseaseField.getText().split(", ");
                Set<String> diseasesSet = new HashSet<>(Arrays.asList(diseases));
                searchedDonor.setChronicDiseases(diseasesSet);
            }
            if(error == true) {
                GuiPopup("Error. Not all fields were updated.");
            }

            DonorDataIO.saveDonors(getCurrentDatabase(), "example/example.json");

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SearchedDonorProfile.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            SearchedDonorProfileController controller = fxmlLoader.<SearchedDonorProfileController>getController();
            controller.setSearchedDonor(searchedDonor);
            controller.initialize();

            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            appStage.setScene(scene);
            appStage.show();
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SearchedDonorProfile.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            SearchedDonorProfileController controller = fxmlLoader.<SearchedDonorProfileController>getController();
            controller.setSearchedDonor(searchedDonor);
            controller.initialize();

            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            appStage.setScene(scene);
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
        if(searchedDonor != null) {
            try {
                donorFullNameLabel.setText(searchedDonor.getGivenNames() + " " + searchedDonor.getLastNames());

                donorStatusLabel.setText(donorStatusLabel.getText() + "Unregistered");

                if (searchedDonor.getRegistered() != null && searchedDonor.getRegistered() == true) {
                    donorStatusLabel.setText(donorStatusLabel.getText() + "Registered");
                }

                if (searchedDonor.getGivenNames() != null) {
                    givenNamesField.setText(searchedDonor.getGivenNames());
                }
                if (searchedDonor.getLastNames() != null) {
                    lastNamesField.setText(searchedDonor.getLastNames());
                }
                if (searchedDonor.getIrdNumber() != null) {
                    irdField.setText(searchedDonor.getIrdNumber().toString());
                }
                if (searchedDonor.getDateOfBirth() != null) {
                    dobField.setText(searchedDonor.getDateOfBirth().toString());
                }
                if (searchedDonor.getDateOfDeath() != null) {
                    dodField.setText(searchedDonor.getDateOfDeath().toString());
                }
                if (searchedDonor.getGender() != null) {
                    genderField.setText(searchedDonor.getGender());
                }
                heightField.setText(String.valueOf(searchedDonor.getHeight()));
                weightField.setText(String.valueOf(searchedDonor.getWeight()));
                phoneField.setText(searchedDonor.getPhone());
                emailField.setText(searchedDonor.getEmail());

                if (searchedDonor.getAddress() != null) {
                    addressField.setText(searchedDonor.getAddress());
                }
                if (searchedDonor.getRegion() != null) {
                    regionField.setText(searchedDonor.getRegion());
                }
                if (searchedDonor.getBloodType() != null) {
                    bloodTypeField.setText(searchedDonor.getBloodType());
                }
                if (searchedDonor.getSmoker() != null) {
                    smokerField.setText(searchedDonor.getSmoker().toString());
                }
                if (searchedDonor.getAlcoholConsumption() != null) {
                    alcoholConsumptionField.setText(searchedDonor.getAlcoholConsumption());
                }
                if (searchedDonor.getBloodPressure() != null) {
                    bloodPressureField.setText(searchedDonor.getBloodPressure());
                }
                diseaseField.setText(searchedDonor.getChronicDiseasesAsCSV());
                organField.setText(searchedDonor.getOrgansAsCSV());
                donationsField.setText(searchedDonor.getDonationsAsCSV());
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void setSearchedDonor(Donor donor) {
        searchedDonor = donor;
    }
}
