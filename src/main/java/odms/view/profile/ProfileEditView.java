package odms.view.profile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import odms.controller.AlertController;
import odms.controller.profile.ProfileEditController;
import odms.model.profile.Profile;
import odms.view.CommonView;

import java.io.IOException;
import java.time.LocalDate;

import static odms.controller.AlertController.profileCancelChanges;

public class ProfileEditView extends CommonView {
    @FXML
    private Label donorFullNameLabel;

    @FXML
    private Label donorStatusLabel;

    @FXML
    private TextField givenNamesField;

    @FXML
    private TextField lastNamesField;

    @FXML
    private TextField irdNumberField;

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
    private TextField alcoholConsumptionField;

    @FXML
    private TextField bloodPressureField;

    @FXML
    private RadioButton isSmokerRadioButton;

    @FXML
    private TextField preferredNameField;

    @FXML
    private ComboBox comboGenderPref;

    @FXML
    private ComboBox comboGender;

    private ProfileEditController controller = new ProfileEditController(this);

    /**
     * Button handler to undo last action.
     *
     * @param event clicking on the undo button.
     */
    @FXML
    private void handleUndoButtonClicked(ActionEvent event) {
        controller.undo();
    }

    /**
     * Button handler to redo last undo action.
     *
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event) {
        controller.redo();
    }

    /**
     * Button handler to save the changes made to the fields.
     *
     * @param event clicking on the save (tick) button.
     */
    @FXML
    private void handleSaveButtonClicked(ActionEvent event) throws IOException {
        if (AlertController.saveChanges()) {
            try {
                controller.save();
                showNotification("profile", event);
                closeEditWindow(event);
            } catch (IllegalArgumentException e) {
                AlertController.invalidEntry(
                        e.getMessage() + "\n" +
                                "Changes not saved."
                );
            }
        }
    }

    /**
     * closes the edit donor window and reopens the donor.
     *
     * @param event either the cancel button event or the save button event
     */
    @FXML
    private void closeEditWindow(ActionEvent event) throws IOException {
        closeWindow(event);
    }

    /**
     * Button handler to cancel the changes made to the fields.
     *
     * @param event clicking on the cancel (x) button.
     */
    @FXML
    private void handleCancelButtonClicked(ActionEvent event) throws IOException {
        if (profileCancelChanges())
            closeWindow(event);
    }

    private void closeWindow(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(
                getClass().getResource("/view/ProfileDisplay.fxml"));
        ProfileDisplayControllerTODO v = fxmlLoader.getController();
        v.initialize(controller.close());
        changeScene(event, "/view/ProfileDisplay.fxml");
    }

    /**
     * Sets the current profile attributes to the labels on start up.
     */
    @FXML
    public void initialize(Profile currentProfile) {
        // Restrict entry on these fields to numbers only.
        // Regex: \\d* matches only with digits 0 or more times.
        // TODO investigate abstracting copy paste listeners to common function.
        heightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                heightField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        irdNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                irdNumberField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        weightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                weightField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

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
                    irdNumberField.setText(currentProfile.getIrdNumber().toString());
                }
                if (currentProfile.getDateOfBirth() != null) {
                    dobDatePicker.setValue(currentProfile.getDateOfBirth());
                }
                if (currentProfile.getDateOfDeath() != null) {
                    dodDatePicker.setValue(currentProfile.getDateOfDeath());
                }
                if (currentProfile.getHeight() != 0.0) {
                    heightField.setText(String.valueOf(currentProfile.getHeight()));
                }
                if (currentProfile.getWeight() != 0.0) {
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
                if (currentProfile.getBloodPressure() != null) {
                    bloodPressureField.setText(currentProfile.getBloodPressure());
                }
                if (currentProfile.getBloodType() != null) {
                    bloodTypeField.setText(currentProfile.getBloodType());
                }
                if (currentProfile.getIsSmoker() == null || !currentProfile.getIsSmoker()) {
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
                comboGenderPref.getItems().addAll("Male", "Female",
                        "Non binary"); //TODO Add database call for all preferred genders.

                if (currentProfile.getPreferredGender() != null) {
                    comboGenderPref.getEditor().setText(currentProfile.getPreferredGender());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public LocalDate getdobDatePicker(){
        return dobDatePicker.getValue();
    }

    public void setdobDatePicker(LocalDate date){
        dobDatePicker.setValue(date);
    }

    public String getGivenNamesField() {
        return givenNamesField.getText();
    }

    public String getIRDNumberField() {
        return irdNumberField.getText();
    }

    public String getLastNamesField() {
        return lastNamesField.getText();
    }

    public String getAddressField() {
        return addressField.getText();
    }

    public LocalDate getDODDatePicker() {
        return dodDatePicker.getValue();
    }

    public String getEmailField() {
        return emailField.getText();
    }

    public Object getComboGender() {
        return comboGender.getValue();
    }

    public String getHeightField() {
        return heightField.getText();
    }

    public String getPhoneField() {
        return phoneField.getText();
    }

    public String getComboGenderPref() {
        return comboGenderPref.getEditor().getText();
    }

    public String getPreferredNameField() {
        return preferredNameField.getText();
    }

    public String getRegionField() {
        return regionField.getText();
    }

    public String getWeightField() {
        return weightField.getText();
    }

    public String getAlcoholConsumptionField() {
        return alcoholConsumptionField.getText();
    }

    public String getBloodPressureField() {
        return bloodPressureField.getText();
    }

    public String getBloodTypeField() {
        return bloodTypeField.getText();
    }

    public boolean getIsSmokerRadioButton() {
        return isSmokerRadioButton.isSelected();
    }


}
