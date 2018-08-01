package odms.view.profile;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import odms.controller.AlertController;
import odms.controller.profile.ProfileEditController;
import odms.model.enums.NewZealandRegionsEnum;
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
    private TextField nhiField;

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
    private ComboBox<String> comboGenderPref;

    @FXML
    private ComboBox comboGender;

    @FXML
    private ComboBox comboCountryOfDeath;

    @FXML
    private TextField regionOfDeathField;

    @FXML
    private ComboBox<String> comboRegion;

    @FXML
    private ComboBox<String> comboRegionOfDeath;

    @FXML
    private ComboBox comboCountry;

    @FXML
    private Text pictureText;


    private Profile currentProfile;


    private ProfileEditController controller = new ProfileEditController(this);
    private Boolean isOpenedByClinician;

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
            try {
                controller.save();
                showNotification("profile", event);
                closeWindow(event);
            } catch (IllegalArgumentException e) {
                AlertController.invalidEntry(
                        e.getMessage() + "\n" +
                                "Changes not saved."
                );
        }
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProfileDisplay.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        ProfileDisplayViewTODO v = fxmlLoader.getController();
        v.initialize(currentProfile);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }

    /**
     * File picker to choose only supported image types.
     *
     * @param event clicking on the choose file button.
     */
    @FXML
    private void handleChooseImageClicked(ActionEvent event) throws IOException{
        File chosenFile = chooseImage(pictureText);
        if (chosenFile != null) {
            String extension = getFileExtension(chosenFile).toLowerCase();
            File deleteFile;
            if ("jpg".equalsIgnoreCase(extension)) {
                deleteFile = new File(LOCALPATH + "\\" + currentProfile.getNhi() + ".jpg");
            } else {
                deleteFile = new File(LOCALPATH + "\\" + currentProfile.getNhi() + ".png");
            }
            if (deleteFile.delete())
            {
                System.out.println("Old file deleted successfully");
            }
            else
            {
                System.out.println("Failed to delete the old file");
            }
            File pictureDestination = new File(LOCALPATH + "\\" + currentProfile.getNhi() + "." + extension);
            copyFileUsingStream(chosenFile, pictureDestination);
            currentProfile.setPictureName(chosenFile.getName());
        }
    }

    /**
     * Ensures the correct input method for region is displayed, also populates region with NZ
     * regions when NZ is selected as country
     */
    @FXML
    private void refreshRegionSelection() {
        if (comboCountry.getValue() != null) {
            if (comboCountry.getValue().toString().equals("New Zealand")) {
                comboRegion.setDisable(false);
                regionField.setDisable(true);
                regionField.setText("");
                comboRegion.getItems().setAll(NewZealandRegionsEnum.toArrayList());
                comboRegion.setValue(currentProfile.getRegion());
            } else {
                comboRegion.setDisable(true);
                regionField.setDisable(false);
            }
        } else {
            comboRegion.setDisable(true);
            regionField.setDisable(false);
        }
    }

    /**
     * Ensures the correct input method for region is displayed, also populates region with NZ
     * regions when NZ is selected as country
     */
    @FXML
    private void refreshRegionOfDeathSelection() {
        if (comboCountryOfDeath.getValue() != null) {
            if (comboCountryOfDeath.getValue().toString().equals("New Zealand")) {
                comboRegionOfDeath.setDisable(false);
                regionOfDeathField.setDisable(true);
                regionOfDeathField.setText("");
                comboRegionOfDeath.getItems().setAll(NewZealandRegionsEnum.toArrayList());
                comboRegionOfDeath.setValue(currentProfile.getRegion());
            } else {
                comboRegionOfDeath.setDisable(true);
                regionOfDeathField.setDisable(false);
            }
        } else {
            comboRegionOfDeath.setDisable(true);
            regionOfDeathField.setDisable(false);
        }
    }


    /**
     * Sets the current profile attributes to the labels on start up.
     */
    @FXML
    public void initialize(Profile p, Boolean isOpenedByClinician) {
        this.isOpenedByClinician = isOpenedByClinician;
        this.currentProfile = p;
        this.controller.setCurrentProfile(currentProfile);

        // Restrict entry on these fields to numbers only.
        // Regex: \\d* matches only with digits 0 or more times.
        setListeners();

        if (currentProfile != null) {
            try {
                populateFields();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Populates all the fields in the window.
     */
    private void populateFields() {
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
            nhiField.setText(currentProfile.getNhi());
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
            if (currentProfile.getGender().equalsIgnoreCase("male")) {
                comboGender.getSelectionModel().selectFirst();
            } else if (currentProfile.getGender().equalsIgnoreCase("female")) {
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
    }

    private void setListeners() {
        String anyDigit = "//d*";
        String notAnyDigit = "[^\\d]";
        // TODO investigate abstracting copy paste listeners to common function.
        heightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(anyDigit)) {
                heightField.setText(newValue.replaceAll(notAnyDigit, ""));
            }
        });

        nhiField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(anyDigit)) {
                nhiField.setText(newValue.replaceAll(notAnyDigit, ""));
            }
        });

        weightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(anyDigit)) {
                weightField.setText(newValue.replaceAll(notAnyDigit, ""));
            }
        });
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

    public void setGivenNamesField(String s) {
        givenNamesField.setText(s);
    }

    public String getNhiField() {
        return nhiField.getText();
    }

    public void setNhiField(String s) {
        nhiField.setText(s);
    }

    public String getLastNamesField() {
        return lastNamesField.getText();
    }

    public void setLastNamesField(String s) {
        lastNamesField.setText(s);
    }

    public String getAddressField() {
        return addressField.getText();
    }

    public void setAddressField(String s) {
        addressField.setText(s);
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

    public void setDODDatePicker(LocalDate l) {
        dodDatePicker.setValue(l);
    }

    public void setEmailField(String s) {
        emailField.setText(s);
    }

    public void setComboGender(Object o) {
        comboGender.setValue(o);
    }

    public void setHeightField(String s) {
        heightField.setText(s);
    }

    public void setPhoneField(String s) {
        phoneField.setText(s);
    }

    public void setComboGenderPref(String s) {
        comboGenderPref.getEditor().setText(s);
    }

    public void setPreferredNameField(String s) {
        preferredNameField.setText(s);
    }

    public void setRegionField(String s) {
        regionField.setText(s);
    }

    public void setWeightField(String s) {
        weightField.setText(s);
    }

    public void setAlcoholConsumptionField(String s) {
        alcoholConsumptionField.setText(s);
    }

    public void setBloodPressureField(String s) {
        bloodPressureField.setText(s);
    }

    public void setBloodTypeField(String s) {
        bloodTypeField.setText(s);
    }

    public void setIsSmokerRadioButton(boolean b) {
        isSmokerRadioButton.setSelected(b);
    }

}
