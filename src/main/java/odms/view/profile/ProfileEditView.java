package odms.view.profile;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.text.Text;
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
            if(extension == "jpg") {
                deleteFile = new File(LOCALPATH + "\\" + currentProfile.getNhi() + ".jpg");
            } else {
                deleteFile = new File(LOCALPATH + "\\" + currentProfile.getNhi() + ".png");
            }
            if(deleteFile.delete())
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
    public void initialize(Profile p) {
        // Restrict entry on these fields to numbers only.
        // Regex: \\d* matches only with digits 0 or more times.

        // TODO investigate abstracting copy paste listeners to common function.
        heightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                heightField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        nhiField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                nhiField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        weightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                weightField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        if (p != null) {
            try {
                currentProfile = p;
                donorFullNameLabel.setText(p.getFullName());

                donorStatusLabel.setText("Donor Status: Unregistered");

                if (p.getDonor() != null && p.getDonor()) {
                    donorStatusLabel.setText("Donor Status: Registered");
                }
                if (p.getGivenNames() != null) {
                    givenNamesField.setText(p.getGivenNames());
                }
                if (p.getLastNames() != null) {
                    lastNamesField.setText(p.getLastNames());
                }
                if (p.getPreferredName() != null) {
                    preferredNameField.setText(p.getPreferredName());
                }
                if (p.getNhi() != null) {
                    nhiField.setText(p.getNhi());
                }
                if (p.getDateOfBirth() != null) {
                    dobDatePicker.setValue(p.getDateOfBirth());
                }
                if (p.getDateOfDeath() != null) {
                    dodDatePicker.setValue(p.getDateOfDeath());
                }
                if (p.getHeight() != 0.0) {
                    heightField.setText(String.valueOf(p.getHeight()));
                }
                if (p.getWeight() != 0.0) {
                    weightField.setText(String.valueOf(p.getWeight()));
                }
                if (p.getPhone() != null) {
                    phoneField.setText(p.getPhone());
                }
                if (p.getEmail() != null) {
                    emailField.setText(p.getEmail());
                }
                if (p.getAddress() != null) {
                    addressField.setText(p.getAddress());
                }
                if (p.getRegion() != null) {
                    regionField.setText(p.getRegion());
                }
                if (p.getBloodPressure() != null) {
                    bloodPressureField.setText(p.getBloodPressure());
                }
                if (p.getBloodType() != null) {
                    bloodTypeField.setText(p.getBloodType());
                }
                if (p.getIsSmoker() == null || !p.getIsSmoker()) {
                    isSmokerRadioButton.setSelected(false);
                } else {
                    isSmokerRadioButton.setSelected(true);
                }
                if (p.getAlcoholConsumption() != null) {
                    alcoholConsumptionField.setText(p.getAlcoholConsumption());
                }

                comboGender.setEditable(false);
                comboGender.getItems().addAll("Male", "Female");
                if (p.getGender() != null) {
                    if (p.getGender().toLowerCase().equals("male")) {
                        comboGender.getSelectionModel().selectFirst();
                    } else if (p.getGender().toLowerCase().equals("female")) {
                        comboGender.getSelectionModel().select(1);
                    } else {
                        comboGender.setValue("");
                    }
                }

                if (p.getGender() != null) {
                    comboGender.getEditor().setText(p.getGender());
                }

                comboGenderPref.setEditable(true);
                comboGenderPref.getItems().addAll("Male", "Female",
                        "Non binary"); //TODO Add database call for all preferred genders.

                if (p.getPreferredGender() != null) {
                    comboGenderPref.getEditor().setText(p.getPreferredGender());
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
