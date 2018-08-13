package odms.view.profile;

import static odms.controller.AlertController.profileCancelChanges;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.controller.AlertController;
import odms.controller.database.CountryDAO;
import odms.controller.database.DAOFactory;
import odms.model.enums.CountriesEnum;
import odms.model.enums.NewZealandRegionsEnum;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.view.CommonView;

public class ProfileEdit extends CommonView {
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
    private Label pictureLabel;

    @FXML
    private TextField cityOfDeathField;

    @FXML
    private Label cityLabel;

    @FXML
    private Label countryLabel;

    @FXML
    private Label regionLabel;

    @FXML
    private TextField cityField;

    @FXML
    private Button removePhotoBtn;

    private Profile currentProfile;

    private odms.controller.profile.ProfileEdit controller = new odms.controller.profile.ProfileEdit(this);
    private Boolean isOpenedByClinician;

    private File chosenFile;
    private Boolean removePhoto = false;

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
            if (controller.save()) {
                showNotification("profile", event);
            } else {
                showNotificationFailed("profile", event);
            }
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

        Display v = fxmlLoader.getController();
        v.initialize(currentProfile, isOpenedByClinician, null);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }

    /**
     * File picker to choose only supported image types.
     *
     * @throws IOException if the file cannot be read
     */
    @FXML
    private void handleChooseImageClicked() {
        Stage stage = (Stage) pictureLabel.getScene().getWindow();
        this.chosenFile = chooseImage(pictureLabel, stage);
        if (this.chosenFile != null) {
            this.pictureLabel.setVisible(true);
            this.removePhotoBtn.setVisible(false);
            this.removePhoto = false;
        }
    }

    /**
     * Enable removal of image.
     */
    @FXML
    private void handleRemoveImageClicked() {
        removePhoto = true;

        pictureLabel.setText("Current photo will be removed");
        pictureLabel.setVisible(true);
        removePhotoBtn.setVisible(false);
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
                regionField.clear();
                comboRegion.getItems().setAll(NewZealandRegionsEnum.toArrayList());
                if (currentProfile.getRegion() != null) {
                    comboRegion.setValue(currentProfile.getRegion());
                }
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
                regionOfDeathField.clear();
                comboRegionOfDeath.getItems().setAll(NewZealandRegionsEnum.toArrayList());
                if (currentProfile.getRegionOfDeath() != null) {
                    comboRegionOfDeath.setValue(currentProfile.getRegionOfDeath());
                } else {
                    if (currentProfile.getRegion() != null) {
                        comboRegionOfDeath.setValue(currentProfile.getRegion());
                    }
                }
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
        if (currentProfile.getPreferredName() != null && !currentProfile.getPreferredName().isEmpty()) {
            donorFullNameLabel.setText(currentProfile.getPreferredName());
        } else {
            donorFullNameLabel.setText(currentProfile.getFullName());
        }

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
            //todo way to set time of death
            dodDatePicker.setValue(LocalDate.from(currentProfile.getDateOfDeath()));
        }
        if (currentProfile.getHeight() != 0.0) {
            heightField.setText(String.valueOf(currentProfile.getHeight() / 100));
        }
        if (currentProfile.getWeight() != 0.0) {
            weightField.setText(String.valueOf(currentProfile.getWeight() / 100));
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
        comboGenderPref.getItems().addAll(
                "Male",
                "Female",
                "Non binary"
        ); // TODO Add database call for all preferred genders.

        if (currentProfile.getPreferredGender() != null) {
            comboGenderPref.getEditor().setText(currentProfile.getPreferredGender());
        }
        setupDeathFields();

        if (currentProfile.getPictureName() != null &&
                !currentProfile.getPictureName().isEmpty()) {

            removePhotoBtn.setVisible(true);
            pictureLabel.setVisible(false);
        }
    }

    private void setupDeathFields() {

        //city and region should be displayed same regardless
        if (currentProfile.getCity() != null) {
            cityField.setText(currentProfile.getCity());
        }
        if (currentProfile.getCountry() != null) {
            comboCountry.setValue(
                    CountriesEnum.getValidNameFromString(currentProfile.getCountry()));
        }
        if (currentProfile.getRegion() != null) {
            if (currentProfile.getCountry() != null) {
                if (currentProfile.getCountry().equals("New Zealand")) {
                    comboRegion.setDisable(false);
                    regionField.setDisable(true);
                    comboRegion.setValue(currentProfile.getRegion());
                } else {
                    comboRegion.setDisable(true);
                    regionField.setDisable(false);
                    regionField.setText(currentProfile.getRegion());
                }
            } else {
                comboRegion.setDisable(true);
                regionField.setDisable(false);
                regionField.setText(currentProfile.getRegion());
            }
        }

        comboRegion.setDisable(false);
        comboCountry.setDisable(false);
        regionField.setDisable(false);
        cityField.setDisable(false);


        //Profile is dead
        if (currentProfile.getDateOfDeath() != null) {

            //Only a clinician should be able to edit these -- not sure about this.
            if (isOpenedByClinician) {
                comboCountryOfDeath.setDisable(false);
                cityOfDeathField.setDisable(false);
                regionOfDeathField.setDisable(false);
                comboRegionOfDeath.setDisable(false);

            } else {
                comboCountryOfDeath.setDisable(true);
                regionOfDeathField.setDisable(true);
                comboRegionOfDeath.setDisable(true);
                cityOfDeathField.setDisable(true);
            }

            //country
            if (currentProfile.getCountryOfDeath() == null) {
                if (currentProfile.getCountry() != null) {
                    comboCountry.setValue(CountriesEnum
                            .getValidNameFromString(currentProfile.getCountry()));
                }
            } else {
                comboCountryOfDeath.setValue(CountriesEnum
                        .getValidNameFromString(currentProfile.getCountryOfDeath()));
            }

            //city
            if (currentProfile.getCityOfDeath() == null) {
                if (currentProfile.getCity() != null) {
                    cityField.setText(currentProfile.getCity());
                }
            } else {
                cityOfDeathField.setText(currentProfile.getCityOfDeath());
            }

            //region
            if (currentProfile.getRegionOfDeath() == null) {
                if (currentProfile.getRegion() != null) {
                    if (currentProfile.getCountry() != null) {
                        if (currentProfile.getCountry().equals("New Zealand")) {
                            comboRegionOfDeath.setValue(currentProfile.getRegionOfDeath());
                            regionOfDeathField.clear();
                        } else {
                            regionOfDeathField.setText(currentProfile.getRegionOfDeath());
                            //comboRegionOfDeath.clearSelection()
                        }
                    } else {
                        regionOfDeathField.setText(currentProfile.getRegion());
                        //comboRegionOfDeath.clearSelection()
                    }
                }
            } else {
                if (currentProfile.getCountry() != null) {
                    if (currentProfile.getCountry().equals("New Zealand")) {
                        comboRegionOfDeath.setValue(currentProfile.getRegionOfDeath());
                        regionOfDeathField.clear();
                    }
                } else {
                    regionOfDeathField.setText(currentProfile.getRegionOfDeath());
                    //comboRegionOfDeath.clearSelection()
                }
            }

        } else {
            //profile is alive

//            if (currentProfile.getCity() != null) {
//                cityField.setText(currentProfile.getCity());
//            }
//            if (currentProfile.getCountry() != null) {
//                comboCountry.setValue(
//                        CountriesEnum.getValidNameFromString(currentProfile.getCountry()));
//            }
//            if (currentProfile.getRegion() != null) {
//                if (currentProfile.getCountry() != null) {
//                    if (currentProfile.getCountry().equals("New Zealand")) {
//                        comboRegion.setDisable(false);
//                        regionField.setDisable(true);
//                        comboRegion.setValue(currentProfile.getRegion());
//                    } else {
//                        comboRegion.setDisable(true);
//                        regionField.setDisable(false);
//                        regionField.setText(currentProfile.getRegion());
//                    }
//                } else {
//                    comboRegion.setDisable(true);
//                    regionField.setDisable(false);
//                    regionField.setText(currentProfile.getRegion());
//                }
//
//            }

        }

        //Populating combo box values
        CountryDAO database = DAOFactory.getCountryDAO();
        int index = 0;
        for (String country : database.getAll(true)) {
            User.allowedCountriesIndices.add(index);
            index++;
        }

        List<String> validCountries = database.getAll(true);
        comboCountry.getItems().addAll(validCountries);
        comboCountryOfDeath.getItems().addAll(validCountries);

        refreshRegionSelection();
        if (currentProfile.getDateOfDeath() != null) {
            refreshRegionOfDeathSelection();
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
            String pattern = "^[A-HJ-NP-Z]{3}\\d{4}$";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(newValue);

            if (!m.matches() && !m.hitEnd()) {
                nhiField.setText(oldValue);
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

    public File getChosenFile() {
        return chosenFile;
    }

    public Boolean getRemovePhoto() {
        return removePhoto;
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

    public void setComboGender(Object o) {
        comboGender.setValue(o);
    }

    public void setHeightField(String s) {
        heightField.setText(s);
    }

    public void setPhoneField(String s) {
        phoneField.setText(s);
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

    public String getComboCountryOfDeath() {
        return comboCountryOfDeath.getValue().toString();
    }

    public String getRegionOfDeathField() {
        return regionOfDeathField.getText();
    }

    public ComboBox<String> getComboRegion() {
        return comboRegion;
    }

    public String getComboRegionOfDeath() {
        return comboRegionOfDeath.getValue();
    }

    public ComboBox getComboCountry() {
        return comboCountry;
    }

    public String getCityOfDeathField() {
        return cityOfDeathField.getText();
    }

    public String getCityField() {
        return cityField.getText();
    }

}
