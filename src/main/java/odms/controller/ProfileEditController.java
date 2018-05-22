package odms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.controller.RedoController;
import odms.controller.UndoController;
import odms.data.ProfileDataIO;
import odms.history.History;
import odms.profile.Profile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;

import static odms.controller.AlertController.*;
import static odms.controller.GuiMain.getCurrentDatabase;

public class ProfileEditController extends CommonController {

    private Profile currentProfile;

    private RedoController redoController= new RedoController();
    private UndoController undoController= new UndoController();
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
    private RadioButton isSmokerRadioButton;

    private Boolean isClinician;

    /**
     * Button handler to undo last action.
     *
     * @param event clicking on the undo button.
     */
    @FXML
    private void handleUndoButtonClicked(ActionEvent event) {
        undoController.undo(GuiMain.getCurrentDatabase());
    }

    /**
     * Button handler to redo last undo action.
     *
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event) {
        redoController.redo(GuiMain.getCurrentDatabase());
    }

    /**
     * Button handler to save the changes made to the fields.
     *
     * @param event clicking on the save (tick) button.
     */
    @FXML
    private void handleSaveButtonClicked(ActionEvent event) throws IOException {
        boolean saveBool = profileSaveChanges();
        boolean error = false;

        if (saveBool) {
            if (givenNamesField.getText().isEmpty() || lastNamesField.getText().isEmpty() ||
                    irdField.getText().isEmpty() || dobField.getText().isEmpty()) {
                guiPopup("Error. Required fields were left blank.");
            } else {
                History action = new History("Profile" , currentProfile.getId() ,"update",
                        "previous "+currentProfile.getAttributesSummary(),-1,null);

                currentProfile.setGivenNames(givenNamesField.getText());

                currentProfile.setLastNames(lastNamesField.getText());

                currentProfile.setIrdNumber(Integer.valueOf(irdField.getText()));

                try {
                    currentProfile.setDateOfBirth(
                        LocalDate.parse(dobField.getText(),
                            DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    );
                    if (!dodField.getText().isEmpty()) {
                        if(!(
                            LocalDate.parse(dodField.getText(),
                                DateTimeFormatter.ofPattern("dd-MM-yyyy")
                            ).isBefore(currentProfile.getDateOfBirth())
                            ||
                            LocalDate.parse((dodField.getText()),
                                DateTimeFormatter.ofPattern("dd-MM-yyyy")
                            ).isAfter(LocalDate.now()))) {

                            currentProfile.setDateOfDeath(LocalDate.parse(
                                    dodField.getText(),
                                    DateTimeFormatter.ofPattern("dd-MM-yyyy")
                            ));

                        } else {
                            error = true;
                        }
                    }
                } catch (DateTimeParseException e) {
                    error = true;
                }
                if (!genderField.getText().isEmpty()) {
                    currentProfile.setGender(genderField.getText());
                }

                try {
                    if (!weightField.getText().isEmpty()) {
                        currentProfile.setWeight(Double.valueOf(weightField.getText()));
                    }
                    if (!heightField.getText().isEmpty()) {
                        currentProfile.setHeight(Double.valueOf(heightField.getText()));
                    }
                } catch(NumberFormatException e) {
                    error = true;
                }
                currentProfile.setPhone(phoneField.getText());
                currentProfile.setEmail(emailField.getText());
                currentProfile.setAddress(addressField.getText());
                currentProfile.setRegion(regionField.getText());
                currentProfile.setBloodType(bloodTypeField.getText());

                if (bloodPressureField.getText().contains("/")) {
                    String systolic = bloodPressureField.getText().substring(
                        0, bloodPressureField.getText().indexOf("/")).trim();
                    currentProfile.setBloodPressureSystolic(Integer.valueOf(systolic));
                    String diastolic = bloodPressureField.getText().substring(
                        bloodPressureField.getText().lastIndexOf('/') + 1).trim();
                    currentProfile.setBloodPressureDiastolic(Integer.valueOf(diastolic));
                }

                currentProfile.setSmoker(isSmokerRadioButton.isSelected());
                currentProfile.setAlcoholConsumption(alcoholConsumptionField.getText());
                action.setHistoryData(action.getHistoryData()+" new "+currentProfile.getAttributesSummary());
                action.setHistoryTimestamp(LocalDateTime.now());
                HistoryController.updateHistory(action);

                if (diseaseField.getText().contains("/")) {
                    String[] diseases = diseaseField.getText().split(", ");
                    HashSet<String> diseasesSet = new HashSet<>(Arrays.asList(diseases));
                    currentProfile.setChronicDiseases(diseasesSet);
                }
                if (error) {
                    guiPopup("Error. Not all fields were updated.");
                } else {
                    ProfileDataIO.saveData(getCurrentDatabase());
                    showNotification("Profile", event);
                    closeEditWindow(event);
                }
            }
        }
    }

    /**
     * Button handler to cancel the changes made to the fields.
     *
     * @param event clicking on the cancel (x) button.
     */
    @FXML
    private void handleCancelButtonClicked(ActionEvent event) throws IOException {
        boolean cancelBool = profileCancelChanges();

        if (cancelBool) {
            closeEditWindow(event);
        }
    }

    /**
     * closes the edit donor window and reopens the donor.
     *
     * @param event either the cancel button event or the save button event
     */
    @FXML
    private void closeEditWindow(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProfileDisplay.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ProfileDisplayController controller = fxmlLoader.getController();
        if (isClinician) {
            controller.setProfileViaClinician(currentProfile);
        } else {
            controller.setProfile(currentProfile);
        }
        controller.initialize();

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setScene(scene);
        appStage.setTitle("Profile");
        appStage.show();
    }

    /**
     * Sets the current profile attributes to the labels on start up.
     */
    @FXML
    public void initialize() {

        if (currentProfile != null) {
            try {
                donorFullNameLabel.setText(currentProfile.getFullName());

                donorStatusLabel.setText("Donor Status: Unregistered");

                if (currentProfile.getDonor() != null && currentProfile.getDonor()) {
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
                    dobField.setText(currentProfile.getDateOfBirth().format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    );
                }
                if (currentProfile.getDateOfDeath() != null) {
                    dodField.setText(currentProfile.getDateOfDeath().format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    );
                }
                if (currentProfile.getGender() != null) {
                    genderField.setText(currentProfile.getGender());
                }
                if (currentProfile.getHeight() != null){
                    heightField.setText(String.valueOf(currentProfile.getHeight()));

                }
                if (currentProfile.getWeight() != null) {
                    weightField.setText(String.valueOf(currentProfile.getWeight()));
                }
                if (currentProfile.getPhone() != null) {
                    phoneField.setText(currentProfile.getPhone());
                }
                if (currentProfile.getEmail() != null) {
                    emailField.setText(currentProfile.getEmail());
                }

                if (currentProfile.getAddress() != null) {
                    addressField.setText(currentProfile.getAddress());
                }
                if (currentProfile.getRegion() != null) {
                    regionField.setText(currentProfile.getRegion());
                }
                if (currentProfile.getBloodType() != null) {
                    bloodTypeField.setText(currentProfile.getBloodType());
                }
                if (currentProfile.getSmoker() == null || !currentProfile.getSmoker()) {
                    isSmokerRadioButton.setSelected(false);
                } else {
                    isSmokerRadioButton.setSelected(true);
                }
                if (currentProfile.getAlcoholConsumption() != null) {
                    alcoholConsumptionField.setText(currentProfile.getAlcoholConsumption());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCurrentProfile(Profile donor) {
        currentProfile = donor;
    }

    public void setIsClinician(Boolean bool) {
        isClinician = bool;
    }
}
