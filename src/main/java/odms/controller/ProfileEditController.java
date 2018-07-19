package odms.controller;

import static odms.controller.AlertController.profileCancelChanges;
import static odms.controller.GuiMain.getCurrentDatabase;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import odms.data.ProfileDataIO;
import odms.history.History;
import odms.profile.Profile;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


public class ProfileEditController extends CommonController {

    private Profile currentProfile;
    private File pictureDestination;

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
    private TextField nhiNumberField;

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

    private Boolean isClinician;

    @FXML
    private ComboBox comboGenderPref;

    @FXML
    private ComboBox comboGender;

    @FXML
    private Text pictureText;


    /**
     * File picker to choose only supported image types.
     *
     * @param event clicking on the choose file button.
     */

    @FXML//TODO
    private String handleChooseImageClicked(ActionEvent event) throws IOException{
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Images", "jpg", "png");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {

            if (chooser.getSelectedFile().length() > 1000000) {
                pictureText.setText("Photos must be less than 1 mb! \n" + "Choose another ");
                return chooser.getSelectedFile().getName();
            }

            System.out.println("You chose to open this file: " +
                    chooser.getSelectedFile().getName());
            pictureText.setText(chooser.getSelectedFile().getName());
        }
        if (chooser.getSelectedFile() == null) {
            return null;
        } else {
            //TODO how are photos being stored - Below is the class loader which may help with uploading images to storage.

            ClassLoader classLoader = getClass().getClassLoader();
            pictureDestination = new File(new File("."),"src/main/resources/profile_images/" + currentProfile.getNhi() + ".png");


            copyFileUsingStream(chooser.getSelectedFile(), pictureDestination);

            currentProfile.setPictureName(chooser.getSelectedFile().getName());
            return chooser.getSelectedFile().getName();
        }
    }


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
        if (AlertController.saveChanges()) {
            try {
                // History Generation
                History action = new History("Profile" , currentProfile.getId() ,"update",
                        "previous "+currentProfile.getAttributesSummary(),-1,null);

                // Required General Fields
                saveDateOfBirth();
                saveGivenNames();
                saveNhiNumber();
                saveLastNames();

                // Optional General Fields
                saveAddress();
                saveDateOfDeath();
                saveEmail();
                saveGender();
                saveHeight();
                savePhone();
                savePreferredGender();
                savePreferredName();
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

                // History Changes
                action.setHistoryData(action.getHistoryData()+" new "+currentProfile.getAttributesSummary());
                action.setHistoryTimestamp(LocalDateTime.now());
                HistoryController.updateHistory(action);

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
        if (dobDatePicker.getEditor().getText().isEmpty()) {
            throw new IllegalArgumentException("Date of Birth field cannot be blank");
        }
        currentProfile.setDateOfBirth(dobDatePicker.getValue());
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
     * Save NHI Number field to profile.
     * @throws IllegalArgumentException if the field is empty
     */
    private void saveNhiNumber() throws IllegalArgumentException {
        if (nhiNumberField.getText().isEmpty()) {
            throw new IllegalArgumentException("NHI field cannot be blank");
        }
        currentProfile.setNhi(nhiNumberField.getText());
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
        if (!dodDatePicker.getEditor().getText().isEmpty()) {
            currentProfile.setDateOfDeath(dodDatePicker.getValue());
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
        if (comboGender.getValue() != null) {
            currentProfile.setGender(comboGender.getValue().toString());
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
     * Save Preferred Gender value to profile.
     */
    private void savePreferredGender() {
        // If there is no preferred gender just set it to the gender
        if (comboGenderPref.getEditor().getText().equals("")) {
            if (comboGender.getValue() != null) {
                currentProfile.setPreferredGender(comboGender.getValue().toString());
            }
        } else {
            currentProfile.setPreferredGender(comboGenderPref.getEditor().getText());
        }
    }

    /**
     * Save Preferred Name field to profile.
     */
    private void savePreferredName() {
        currentProfile.setPreferredName(preferredNameField.getText());
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
        // Restrict entry on these fields to numbers only.
        // Regex: \\d* matches only with digits 0 or more times.
        // TODO investigate abstracting copy paste listeners to common function.
        heightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                heightField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        nhiNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                nhiNumberField.setText(newValue.replaceAll("[^\\d]", ""));
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
                if (currentProfile.getNhi() != null) {
                    nhiNumberField.setText(currentProfile.getNhi());
                }
                if (currentProfile.getDateOfBirth() != null) {
                    dobDatePicker.setValue(currentProfile.getDateOfBirth());
                }
                if (currentProfile.getDateOfDeath() != null) {
                    dodDatePicker.setValue(currentProfile.getDateOfDeath());
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
