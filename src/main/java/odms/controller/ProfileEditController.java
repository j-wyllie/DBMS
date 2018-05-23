package odms.controller;

import static odms.controller.AlertController.guiPopup;
import static odms.controller.AlertController.profileCancelChanges;
import static odms.controller.AlertController.saveChanges;
import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import com.sun.media.sound.InvalidDataException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.cli.CommandUtils;
import odms.data.ProfileDataIO;
import odms.profile.Profile;

public class ProfileEditController extends CommonController {

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
    private DatePicker dobDatePicker;

    @FXML
    private DatePicker dodDatePicker;

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

    @FXML
    private TextField preferredNameField;

    private Boolean isClinician;

    @FXML
    private ComboBox comboGenderPref;

    @FXML
    private ComboBox comboGender;

    /**
     * Button handler to undo last action.
     *
     * @param event clicking on the undo button.
     */
    @FXML
    private void handleUndoButtonClicked(ActionEvent event) {
        undo();
    }

    /**
     * Button handler to redo last undo action.
     *
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event) {
        redo();
    }

    /**
     * Button handler to save the changes made to the fields.
     *
     * @param event clicking on the save (tick) button.
     */
    @FXML
    private void handleSaveButtonClicked(ActionEvent event) throws IOException {
        boolean error = false;

        if (saveChanges()) {
            if (givenNamesField.getText().isEmpty() || lastNamesField.getText().isEmpty() ||
                    irdField.getText().isEmpty() || dobDatePicker.getValue().equals(null)) {
                guiPopup("Error. Required fields were left blank.");
            } else {
                String action = "Profile " +
                    currentProfile.getId() +
                    " updated details previous = " +
                    currentProfile.getAttributesSummary() +
                    " new = ";

                currentProfile.setGivenNames(givenNamesField.getText());

                currentProfile.setLastNames(lastNamesField.getText());

                currentProfile.setPreferredName(preferredNameField.getText());

                currentProfile.setIrdNumber(Integer.valueOf(irdField.getText()));

                try {
                    LocalDate dob = dobDatePicker.getValue();
                    LocalDate dod = dodDatePicker.getValue();
                    if (!(dob == null)) {
                        if (!(dob.isAfter(LocalDate.now()))) {
                            currentProfile.setDateOfBirth(dob);
                        } else {
                            throw new InvalidDataException();
                        }
                    } else {
                        throw new InvalidDataException();
                    }
                    if (!(dod == null)) {
                        if (!(dod.isBefore(currentProfile.getDateOfBirth())
                            ||
                            dod.isAfter(LocalDate.now()))) {
                            currentProfile.setDateOfDeath(dod);
                        } else {
                            throw new InvalidDataException();
                        }
                    }
                } catch (InvalidDataException e) {
                    error = true;
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
                action = action +
                    currentProfile.getAttributesSummary() +
                    " at " +
                    LocalDateTime.now();
                if (CommandUtils.getHistory().size() != 0) {
                    if (CommandUtils.getPosition() != CommandUtils.getHistory().size() - 1) {
                        CommandUtils.currentSessionHistory.subList(CommandUtils.getPosition(),
                                CommandUtils.getHistory().size() - 1).clear();
                    }
                }
                CommandUtils.currentSessionHistory.add(action);
                CommandUtils.historyPosition = CommandUtils.currentSessionHistory.size() - 1;

                if (diseaseField.getText().contains("/")) {
                    String[] diseases = diseaseField.getText().split(", ");
                    HashSet<String> diseasesSet = new HashSet<>(Arrays.asList(diseases));
                    currentProfile.setChronicDiseases(diseasesSet);
                }
                if (comboGender.getValue() != null) {
                    currentProfile.setGender(comboGender.getValue().toString());
                }
                if (comboGenderPref.getEditor().getText().equals("")) { // If there is no preferred gender just set it to the gender
                    if (comboGender.getValue() != null) {
                        currentProfile.setPreferredGender(comboGender.getValue().toString());
                    }
                } else {
                    currentProfile.setPreferredGender(comboGenderPref.getEditor().getText());
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
        if (profileCancelChanges()) {
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
                if (currentProfile.getPreferredName() != null) {
                    preferredNameField.setText(currentProfile.getPreferredName());
                }
                if (currentProfile.getIrdNumber() != null) {
                    irdField.setText(currentProfile.getIrdNumber().toString());
                }
                if (currentProfile.getDateOfBirth() != null) {
                    dobDatePicker.setValue(currentProfile.getDateOfBirth());
                }
                if (currentProfile.getDateOfDeath() != null) {
                    dodDatePicker.setValue(currentProfile.getDateOfDeath());
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

                comboGender.setEditable(false);
                comboGender.getItems().addAll("Male", "Female");
                if (currentProfile.getGender() != null) {
                    if (currentProfile.getGender().toLowerCase().equals("male")) {
                        comboGender.getSelectionModel().selectFirst();
                    } else if (currentProfile.getGender().toLowerCase().equals("female")) {
                        comboGender.getSelectionModel().select(1);
                    } else {
                        comboGender.setValue("");
                    }
                }

                if (currentProfile.getGender() != null) {
                    comboGender.getEditor().setText(currentProfile.getGender());
                }

                comboGenderPref.setEditable(true);
                comboGenderPref.getItems().addAll("Male", "Female", "Non binary"); //TODO Add database call for all preferred genders.

                if (currentProfile.getPreferredGender() != null) {
                    comboGenderPref.getEditor().setText(currentProfile.getPreferredGender());
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
