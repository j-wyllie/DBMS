package odms.controller;

import static odms.controller.AlertController.profileCancelChanges;
import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
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
    private TextField irdNumberField;

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
    private TextField alcoholConsumptionField;

    @FXML
    private TextField bloodPressureField;

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
        if (AlertController.saveChanges()) {
            try {
                // Required General Fields
                saveDateOfBirth();
                saveGivenNames();
                saveIrdNumber();
                saveLastNames();

                // Optional General Fields
                saveAddress();
                saveDateOfDeath();
                saveEmail();
                saveGender();
                saveHeight();
                savePhone();
                saveRegion();
                saveWeight();

                // Medical Fields
                saveAlcoholConsumption();
                saveBloodPressure();
                saveBloodType();
                saveIsSmoker();

                ProfileDataIO.saveData(getCurrentDatabase());
                showNotification("Profile", event);
                closeEditWindow(event);

                // History Item
                String action = "Profile " +
                        currentProfile.getId() +
                        " updated details previous = " +
                        currentProfile.getAttributesSummary() +
                        " new = " +
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
            } catch (IllegalArgumentException e) {
                AlertController.invalidEntry(
                        e.getMessage() + "\n" +
                        "Changes not saved."
                );
            }
        }
    }

    /**
     * Save Date of Birth field to profile.
     * @throws IllegalArgumentException if the field is empty
     */
    private void saveDateOfBirth() throws IllegalArgumentException {
        if (dobField.getText().isEmpty()) {
            throw new IllegalArgumentException("Date of Birth field cannot be blank");
        }
        currentProfile.setDateOfBirth(LocalDate.parse(
                dobField.getText(),
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
        ));
    }

    /**
     * Save Given Names field to profile.
     * @throws IllegalArgumentException if the field is empty
     */
    private void saveGivenNames() throws IllegalArgumentException {
        if (givenNamesField.getText().isEmpty()) {
            throw new IllegalArgumentException("Given Names field cannot be blank");
        }
        currentProfile.setGivenNames(givenNamesField.getText());
    }

    /**
     * Save IRD Number field to profile.
     * @throws IllegalArgumentException if the field is empty
     */
    private void saveIrdNumber() throws IllegalArgumentException {
        if (irdNumberField.getText().isEmpty()) {
            throw new IllegalArgumentException("IRD Number field cannot be blank");
        }
        currentProfile.setIrdNumber(Integer.valueOf(irdNumberField.getText()));
    }

    /**
     * Save Last Names field to profile.
     * @throws IllegalArgumentException if the field is empty
     */
    private void saveLastNames() throws IllegalArgumentException {
        if (lastNamesField.getText().isEmpty()) {
            throw new IllegalArgumentException("Last Names field cannot be blank");
        }
        currentProfile.setLastNames(lastNamesField.getText());
    }

    /**
     * Save Address field to profile.
     */
    private void saveAddress() {
        if (!addressField.getText().isEmpty()) {
            currentProfile.setAddress(addressField.getText());
        }
    }

    /**
     * Save Date of Death field to profile.
     * @throws IllegalArgumentException if date is prior to birth date
     */
    private void saveDateOfDeath() throws IllegalArgumentException {
        if (!dodField.getText().isEmpty()) {
            currentProfile.setDateOfDeath(LocalDate.parse(
                    dodField.getText(),
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")
            ));
        }
    }

    /**
     * Save Email field to profile.
     */
    private void saveEmail() {
        if (!emailField.getText().isEmpty()) {
            currentProfile.setEmail(emailField.getText());
        }
    }

    /**
     * Save Gender field to profile.
     */
    private void saveGender() {
        if (!genderField.getText().isEmpty()) {
            currentProfile.setGender(genderField.getText());
        }
    }

    /**
     * Save Height field to profile.
     */
    private void saveHeight() {
        if (!heightField.getText().isEmpty()) {
            currentProfile.setHeight(Double.valueOf(heightField.getText()));
        } else {
            currentProfile.setHeight(0.0);
        }
    }

    /**
     * Save Phone field to profile.
     */
    private void savePhone() {
        if (!phoneField.getText().isEmpty()) {
            currentProfile.setPhone(phoneField.getText());
        }
    }

    /**
     * Save Region field to profile.
     */
    private void saveRegion() {
        if (!regionField.getText().isEmpty()) {
            currentProfile.setRegion(regionField.getText());
        }
    }

    /**
     * Save Weight field to profile.
     */
    private void saveWeight() {
        if (!weightField.getText().isEmpty()) {
            currentProfile.setWeight(Double.valueOf(weightField.getText()));
        } else {
            currentProfile.setWeight(0.0);
        }
    }

    /**
     * Save Alcohol Consumption field to profile.
     */
    private void saveAlcoholConsumption() {
        if (!alcoholConsumptionField.getText().isEmpty()) {
            currentProfile.setAlcoholConsumption(alcoholConsumptionField.getText());
        }
    }

    /**
     * Save Blood Pressure field to profile.
     * Must be in format of Systolic/Diastolic.
     */
    private void saveBloodPressure() {
        if (!bloodPressureField.getText().isEmpty() && bloodPressureField.getText().contains("/")) {
            String systolic = bloodPressureField.getText().substring(
                    0, bloodPressureField.getText().indexOf("/")
            ).trim();
            currentProfile.setBloodPressureSystolic(Integer.valueOf(systolic));

            String diastolic = bloodPressureField.getText().substring(
                    bloodPressureField.getText().lastIndexOf('/') + 1
            ).trim();
            currentProfile.setBloodPressureDiastolic(Integer.valueOf(diastolic));
        }
    }

    /**
     * Save Blood Type field to profile.
     */
    private void saveBloodType() {
        if (!bloodTypeField.getText().isEmpty()) {
            currentProfile.setBloodType(bloodTypeField.getText());
        }
    }

    /**
     * Save Smoker Status to profile.
     */
    private void saveIsSmoker() {
        // TODO this should be a checkbox and not a radio button.
        currentProfile.setIsSmoker(isSmokerRadioButton.isSelected());
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
        // Restrict entry on these fields to numbers only.
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
                if (currentProfile.getIrdNumber() != null) {
                    irdNumberField.setText(currentProfile.getIrdNumber().toString());
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
                if (currentProfile.getHeight() != 0.0){
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
