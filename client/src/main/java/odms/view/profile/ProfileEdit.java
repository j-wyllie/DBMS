package odms.view.profile;

import static odms.controller.AlertController.profileCancelChanges;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.NewZealandRegionsEnum;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.controller.AlertController;
import odms.controller.database.DAOFactory;
import odms.controller.database.country.CountryDAO;
import odms.view.CommonView;

/**
 * Profile edit window.
 */
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
    private DateTimePicker dodDateTimePicker;

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
    private CheckBox isSmokerCheckBox;

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

    @FXML
    private TextField cityOfDeathField;

    @FXML
    private TextField cityField;

    private Profile currentProfile;

    private static final String MAINCOUNTRY = "New Zealand";

    private odms.controller.profile.ProfileEdit controller = new odms.controller.profile.ProfileEdit(
            this);
    private Boolean isOpenedByClinician;

    /**
     * Button handler to save the changes made to the fields.
     *
     * @param event clicking on the save (tick) button.
     * @throws IOException error closing windows.
     */
    @FXML
    private void handleSaveButtonClicked(ActionEvent event) throws IOException {
        try {
            controller.save();
            showNotification("profile", event);
            closeWindow(event);
        } catch (IllegalArgumentException | SQLException e) {
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
     * @throws IOException Error closing windows.
     */
    @FXML
    private void handleCancelButtonClicked(ActionEvent event) throws IOException {
        if (profileCancelChanges()) {
            closeWindow(event);
        }
    }

    /**
     * Closes the edit window and opens the profile display.
     *
     * @param event Action event when button is pressed.
     * @throws IOException Thrown when there is an error initializing a new window.
     */
    private void closeWindow(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProfileDisplay.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Display v = fxmlLoader.getController();
        v.initialize(currentProfile, isOpenedByClinician);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }

    /**
     * File picker to choose only supported image types.
     *
     * @param event clicking on the choose file button.
     * @throws IOException thrown if an error
     */
    @FXML
    private void handleChooseImageClicked(ActionEvent event) throws IOException {
        File chosenFile = chooseImage(pictureText);
        File pictureDestination = controller.setImage(chosenFile, LOCALPATH);
        copyFileUsingStream(chosenFile, pictureDestination);

    }

    /**
     * Ensures the correct input method for region is displayed, also populates region with NZ
     * regions when NZ is selected as country
     */
    @FXML
    private void refreshRegionSelection() {
        if (comboCountry.getValue() != null) {
            if (comboCountry.getValue().toString().equals(MAINCOUNTRY)) {
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
            if (comboCountryOfDeath.getValue().toString().equals(MAINCOUNTRY)) {
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
     *
     * @param p Current profile.
     * @param isOpenedByClinician Boolean, true if the window was opened by a clinician.
     */
    @FXML
    public void initialize(Profile p, Boolean isOpenedByClinician) {
        this.isOpenedByClinician = isOpenedByClinician;
        this.currentProfile = p;
        this.controller.setCurrentProfile(currentProfile);
        this.controller.setIsClinician(isOpenedByClinician);

        // Restrict entry on these fields to numbers only.
        // Regex: \\d* matches only with digits 0 or more times.
        setListeners();

        if (currentProfile != null) {
            populateFields();
            if (!isOpenedByClinician) {
                disableItems();
            }

            if (currentProfile.getDateOfDeath() == null) {
                comboCountryOfDeath.setDisable(true);
                regionOfDeathField.setDisable(true);
                comboRegionOfDeath.setDisable(true);
                cityOfDeathField.setDisable(true);
            }
        }
    }

    /**
     * Disables labels if a clinician isn't viewing the profile.
     */
    private void disableItems() {
        comboCountryOfDeath.setDisable(true);
        regionOfDeathField.setDisable(true);
        comboRegionOfDeath.setDisable(true);
        cityOfDeathField.setDisable(true);
        dodDateTimePicker.setDisable(true);
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
            dodDateTimePicker.setDateTimeValue(LocalDateTime.from(currentProfile.getDateOfDeath()));
        } else {
            dodDateTimePicker.getEditor().clear();
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
            isSmokerCheckBox.setSelected(false);
        } else {
            isSmokerCheckBox.setSelected(true);
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
        setUpLocationFields();
        controller.populateDeathFields();
    }

    private void setUpLocationFields() {
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
                if (currentProfile.getCountry().equals(MAINCOUNTRY)) {
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

        dodDateTimePicker.getEditor().textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue.isEmpty()) {
                        comboCountryOfDeath.setDisable(true);
                        regionOfDeathField.setDisable(true);
                        comboRegionOfDeath.setDisable(true);
                        cityOfDeathField.setDisable(true);
                    } else {
                        if (isOpenedByClinician) {
                            comboCountryOfDeath.setDisable(false);
                            regionOfDeathField.setDisable(false);
                            comboRegionOfDeath.setDisable(false);
                            cityOfDeathField.setDisable(false);
                            refreshRegionOfDeathSelection();
                            controller.populateDeathFields();
                        }
                    }
                });
    }

    public void setComboCountryOfDeath(String country) {
        comboCountryOfDeath.setValue(CountriesEnum.getValidNameFromString(country));
    }

    public void setRegionOfDeathField(String region) {
        regionOfDeathField.setText(region);
    }

    public void setCityOfDeathField(String city) {
        cityOfDeathField.setText(city);
    }

    public void setComboRegionOfDeath(String region) {
        comboRegionOfDeath.setValue(region);
    }

    /**
     * Clears the region of death field.
     */
    public void clearRegionOfDeathField() {
        regionOfDeathField.clear();
    }

    public LocalDate getdobDatePicker() {
        return dobDatePicker.getValue();
    }

    public void setdobDatePicker(LocalDate date) {
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

    public LocalDateTime getDodDateTimePicker() {
        return dodDateTimePicker.getDateTimeValue();
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

    public boolean getIsSmokerCheckBox() {
        return isSmokerCheckBox.isSelected();
    }

    public void setDODDatePicker(LocalDateTime l) {
        dodDateTimePicker.setDateTimeValue(l);
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

    public void setIsSmokerCheckBox(boolean b) {
        isSmokerCheckBox.setSelected(b);
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
