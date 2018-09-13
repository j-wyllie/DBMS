package odms.view.profile;

import static odms.controller.AlertController.profileCancelChanges;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.NewZealandRegionsEnum;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.controller.AlertController;
import odms.controller.DateTimePicker;
import odms.controller.database.DAOFactory;
import odms.controller.database.country.CountryDAO;
import odms.view.CommonView;

/**
 * profile edit window.
 */
@Slf4j
public class ProfileEdit extends CommonView {

    private static final String MAINCOUNTRY = "New Zealand";

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
    private SplitPane dodPane;

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
    private Label pictureLabel;

    @FXML
    private TextField cityOfDeathField;

    @FXML
    private TextField cityField;

    @FXML
    private Button removePhotoBtn;

    private Profile currentProfile;
    private odms.controller.profile.ProfileEdit controller =
            new odms.controller.profile.ProfileEdit(
                    this
            );
    private Boolean isOpenedByClinician;

    private File chosenFile;
    private Boolean removePhoto = false;
    private User currentUser;

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
     * @throws IOException error closing windows.
     */
    @FXML
    private void handleSaveButtonClicked(ActionEvent event) throws IOException {
        try {
            if (AlertController.saveChanges()) {
                controller.save();
                showNotification("profile", event);
            } else {
                showNotificationFailed("profile", event);
            }
            closeWindow(event);
        } catch (IllegalArgumentException | SQLException e) {
            AlertController.invalidEntry(
                    e.getMessage() + "\n" + "Changes not saved."
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
        v.initialize(currentProfile, isOpenedByClinician, null, currentUser);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }

    /**
     * File picker to choose only supported image types.
     *
     * @throws IOException if the file cannot be read.
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
            if (comboCountry.getValue().toString().equals(MAINCOUNTRY)) {
                comboRegion.setVisible(true);
                regionField.setVisible(false);
                regionField.clear();
                comboRegion.getItems().setAll(NewZealandRegionsEnum.toArrayList());
                if (currentProfile.getRegion() != null && NewZealandRegionsEnum.toArrayList()
                        .contains(currentProfile.getRegion())) {
                    comboRegion.setValue(currentProfile.getRegion());
                } else {
                    comboRegion.setValue(NewZealandRegionsEnum.toArrayList().get(0));
                }
            } else {
                comboRegion.setVisible(false);
                regionField.setVisible(true);
            }
        } else {
            comboRegion.setVisible(false);
            regionField.setVisible(true);
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
                comboRegionOfDeath.setVisible(true);
                regionOfDeathField.setVisible(false);
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
                comboRegionOfDeath.setVisible(false);
                regionOfDeathField.setVisible(true);
            }
        } else {
            comboRegionOfDeath.setVisible(false);
            regionOfDeathField.setVisible(true);
        }
    }

    /**
     * Sets the current profile attributes to the labels on start up.
     *
     * @param p Current profile.
     * @param isOpenedByClinician Boolean, true if the window was opened by a clinician.
     */
    @FXML
    public void initialize(Profile p, Boolean isOpenedByClinician, User currentUser) {
        this.isOpenedByClinician = isOpenedByClinician;
        this.currentUser = currentUser;
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
                deathDetailsSetDisable(true);
                clearDodField();
            }
            try {
                if(controller.getManuallyExpiredOrgans()) {
                    disableItems();
                    dodPane.hoverProperty().addListener(observable -> {
                        if (dodPane.isHover()) {
                            dodPane.setTooltip(new Tooltip("Profile has manually expired organ(s)."));
                        }
                    });

                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Disables labels if a clinician isn't viewing the profile.
     */
    private void disableItems() {
        deathDetailsSetDisable(true);
        dodDateTimePicker.setDisable(true);
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
                "Non binary");

        if (currentProfile.getPreferredGender() != null) {
            comboGenderPref.getEditor().setText(currentProfile.getPreferredGender());
        }

        if (currentProfile.getPictureName() != null &&
                !currentProfile.getPictureName().isEmpty()) {

            removePhotoBtn.setVisible(true);
            pictureLabel.setVisible(false);
        }

        setUpLocationFields();
        controller.populateDeathFields();
    }

    /**
     * Sets up the location fields to be populated with the correct countries and regions.
     */
    private void setUpLocationFields() {
        //Populating combo box values
        CountryDAO database = DAOFactory.getCountryDAO();

        List<String> validCountries = database.getAll(true);
        comboCountry.getItems().addAll(validCountries);
        comboCountryOfDeath.getItems().addAll(validCountries);

        // City and region should be displayed same regardless
        CountryDAO countryDAO = DAOFactory.getCountryDAO();
        if (currentProfile.getCity() != null) {
            cityField.setText(currentProfile.getCity());
        }
        if (currentProfile.getCountry() != null) {
            comboCountry.setValue(
                    CountriesEnum.getValidNameFromString(currentProfile.getCountry()));
        } else if (validCountries.contains(MAINCOUNTRY)) {
            comboCountry.setValue(CountriesEnum.getValidNameFromString(MAINCOUNTRY));
        }
        if (currentProfile.getRegion() != null) {
            if (currentProfile.getCountry() != null) {
                if (currentProfile.getCountry().equals(MAINCOUNTRY)) {
                    comboRegion.setVisible(true);
                    regionField.setVisible(false);
                    comboRegion.setValue(currentProfile.getRegion());
                } else {
                    comboRegion.setVisible(false);
                    regionField.setVisible(true);
                    regionField.setText(currentProfile.getRegion());
                }
            } else {
                comboRegion.setVisible(false);
                regionField.setVisible(true);
                regionField.setText(currentProfile.getRegion());
            }
        }

        deathDetailsSetDisable(false);

        refreshRegionSelection();
    }

    /**
     * Sets the listeners for nhiField, weightField and dodDateTimePicker.
     */
    private void setListeners() {
        String anyDigit = "//d*";
        String notAnyDigit = "[^\\d]";
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

                    if (newValue == null || newValue.equals("")) {
                        deathDetailsSetDisable(true);
                        clearDodField();
                    } else {

                        try {
                            DateTimeFormatter dtFormatter = DateTimeFormatter
                                    .ofPattern("d/M/yyyy H:mm");

                            // Validate the text entry is a valid date time.
                            LocalDateTime parsedDoD = LocalDateTime.parse(
                                    dodDateTimePicker.getEditor().getText(), dtFormatter
                            );

                            if (isValidDateOfDeath(parsedDoD)) {
                                deathDetailsSetDisable(false);
                                refreshRegionOfDeathSelection();
                                controller.populateDeathFields();
                            } else {
                                AlertController.invalidEntry("Date cannot be in the future.");
                                deathDetailsSetDisable(true);
                                clearDodField();
                            }

                        } catch (DateTimeParseException e) {
                            deathDetailsSetDisable(true);
                        }
                    }
                });
    }

    private Boolean isValidDateOfDeath(LocalDateTime dateOfDeath) {
        return dateOfDeath.isAfter(LocalDateTime.of(1900, 6, 30, 12, 0)) &&
                !dateOfDeath.isAfter(LocalDateTime.now());
    }

    private void deathDetailsSetDisable(Boolean disabled) {
        comboCountryOfDeath.setDisable(disabled);
        regionOfDeathField.setDisable(disabled);
        comboRegionOfDeath.setDisable(disabled);
        cityOfDeathField.setDisable(disabled);
    }

    private void clearDodField() {
        dodDateTimePicker.clearDateTimeValue();
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

    public void setIsSmokerCheckBox(Boolean b) {
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
