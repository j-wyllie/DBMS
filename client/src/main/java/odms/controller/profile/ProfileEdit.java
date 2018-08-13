package odms.controller.profile;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.fxml.FXML;
import odms.commons.model.profile.Profile;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.data.AddressIO;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;

public class ProfileEdit extends CommonController {

    private Profile currentProfile;
    private odms.view.profile.ProfileEdit view;
    private Boolean isClinician;
    private static final String MAINCOUNTRY = "New Zealand";

    /**
     * Profile edit view connected to this controller.
     *
     * @param v view.
     */
    public ProfileEdit(odms.view.profile.ProfileEdit v) {
        view = v;
    }

    /**
     * Button handler to save the changes made to the fields.
     */
    @FXML
    public void save() throws IllegalArgumentException, SQLException {
        if (AlertController.saveChanges()) {
            // history Generation
//            odms.model.history.History action = new odms.model.history.History("profile",
//                    currentProfile.getId(), "update",
//                    "previous " + currentProfile.getAttributesSummary(), -1, null);

            try {
                if (view.getDodDateTimePicker() != null) {
                    saveCityOfDeath();
                    saveRegionOfDeath();
                    saveCountryOfDeath();
                } else {
                    currentProfile.setCountryOfDeath(null);
                    currentProfile.setRegionOfDeath(null);
                    currentProfile.setCityOfDeath(null);
                }
            } catch (IllegalArgumentException e) {
                //throw new IllegalArgumentException(e.getMessage());
            }
            // Required General Fields
            saveDateOfBirth();
            saveGivenNames();
            saveNhi();
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

            saveCity();
            saveCountry();
            saveRegion();

            ProfileDAO database = DAOFactory.getProfileDao();
            database.update(currentProfile);

            // TODO: update history.
//            // history Changes
//            action.setHistoryData(
//                    action.getHistoryData() + " new " + currentProfile.getAttributesSummary());
//            action.setHistoryTimestamp(LocalDateTime.now());
//            CurrentHistory.updateHistory(action);

        }
    }

    /**
     * Save Region of death field to profile.
     *
     * @throws IllegalArgumentException Thrown when an invalid region is entered.
     */
    private void saveRegionOfDeath() throws IllegalArgumentException {
        if (!view.getRegionOfDeathField().isEmpty() && AddressIO
                .checkValidRegion(
                        view.getRegionOfDeathField() + " " + view.getComboCountryOfDeath(),
                        view.getRegionOfDeathField(), view.getComboCountryOfDeath()) && view
                .getComboRegion().isDisabled()) {
            currentProfile.setRegionOfDeath(view.getRegionOfDeathField());
        } else if (!view.getRegionOfDeathField().isEmpty() && !AddressIO
                .checkValidRegion(
                        view.getRegionOfDeathField() + " " + view.getComboCountryOfDeath(),
                        view.getRegionOfDeathField(), view.getComboCountryOfDeath())) {
            throw new IllegalArgumentException("Invalid Region of Death");
        } else if (!view.getComboRegionOfDeath().isEmpty() && AddressIO
                .checkValidRegion(
                        view.getRegionOfDeathField() + " " + view.getComboCountryOfDeath(),
                        view.getComboRegionOfDeath(), view.getComboCountryOfDeath())) {
            currentProfile.setRegionOfDeath(view.getComboRegionOfDeath());
        } else {
            throw new IllegalArgumentException("Invalid Region of Death");
        }
    }

    /**
     * Save City of death field to profile.
     *
     * @throws IllegalArgumentException thrown when an invalid city is entered.
     */
    private void saveCityOfDeath() throws IllegalArgumentException {
        if (!view.getCityOfDeathField().isEmpty() && AddressIO
                .checkValidCity(view.getCityOfDeathField() + " " + view.getComboCountryOfDeath(),
                        view.getCityOfDeathField(), view.getComboCountryOfDeath())) {
            currentProfile.setCityOfDeath(view.getCityOfDeathField());
        } else if (!view.getCityOfDeathField().isEmpty() && !AddressIO
                .checkValidCity(view.getCityOfDeathField() + " " + view.getComboCountryOfDeath(),
                        view.getCityOfDeathField(), view.getComboCountryOfDeath())) {
            throw new IllegalArgumentException("Invalid city of death.");
        }
    }


    /**
     * Save Country of death field to profile.
     */
    private void saveCountryOfDeath() {
        currentProfile.setCountryOfDeath(view.getComboCountryOfDeath());
    }


    /**
     * Save Date of Birth field to profile.
     *
     * @throws IllegalArgumentException if the field is empty
     */
    public void saveDateOfBirth() throws IllegalArgumentException {
        if (view.getdobDatePicker() == null) {
            throw new IllegalArgumentException("Date of Birth field cannot be blank");
        } else if (view.getdobDatePicker().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of Birth cannot be in the future");
        }
        currentProfile.setDateOfBirth(view.getdobDatePicker());
    }

    /**
     * Save Given Names field to profile.
     *
     * @throws IllegalArgumentException if the field is empty
     */
    public void saveGivenNames() throws IllegalArgumentException {
        if (view.getGivenNamesField().isEmpty()) {
            throw new IllegalArgumentException("Given Names field cannot be blank");
        }
        currentProfile.setGivenNames(view.getGivenNamesField());
    }

    /**
     * Save IRD Number field to profile.
     *
     * @throws IllegalArgumentException if the field is empty
     */
    public void saveNhi() throws IllegalArgumentException {
        if (!view.getNhiField().equals(currentProfile.getNhi()) && (
                !view.getNhiField().matches("^[A-HJ-NP-Z]{3}\\d{4}$"))) {
            throw new IllegalArgumentException("NHI must be valid");
        }

        currentProfile.setNhi(view.getNhiField());
    }

    /**
     * Save Last Names field to profile.
     *
     * @throws IllegalArgumentException if the field is empty
     */
    public void saveLastNames() throws IllegalArgumentException {
        if (view.getLastNamesField().isEmpty()) {
            throw new IllegalArgumentException("Last Names field cannot be blank");
        }
        currentProfile.setLastNames(view.getLastNamesField());
    }

    /**
     * Save Address field to profile.
     */
    public void saveAddress() throws IllegalArgumentException {
        if (!view.getAddressField().isEmpty() && AddressIO
                .checkValidCountry(view.getAddressField(),
                        view.getComboCountry().getValue().toString())) {
            currentProfile.setAddress(view.getAddressField());
        } else if (!view.getAddressField().isEmpty() && !AddressIO
                .checkValidCountry(view.getAddressField(),
                        view.getComboCountry().getValue().toString())) {
            throw new IllegalArgumentException("Invalid address");
        }
    }


    /**
     * Save Date of Death field to profile.
     *
     * @throws IllegalArgumentException if date is prior to birth date
     */
    public void saveDateOfDeath() {
        if (view.getDodDateTimePicker() != null) {
            currentProfile.setDateOfDeath(LocalDateTime.from(view.getDodDateTimePicker()));
        }
    }

    /**
     * Save Email field to profile.
     */
    public void saveEmail() {
        if (!view.getEmailField().isEmpty()) {
            currentProfile.setEmail(view.getEmailField());
        }
    }

    /**
     * Save Gender field to profile.
     */
    public void saveGender() {
        if (view.getComboGender() != null) {
            currentProfile.setGender(view.getComboGender().toString());
        }
    }

    /**
     * Save Height field to profile.
     */
    public void saveHeight() {
        if (!view.getHeightField().isEmpty()) {
            currentProfile.setHeight(Double.valueOf(view.getHeightField()));
        } else {
            currentProfile.setHeight(0.0);
        }
    }

    /**
     * Save Phone field to profile.
     */
    public void savePhone() {
        if (!view.getPhoneField().isEmpty()) {
            currentProfile.setPhone(view.getPhoneField());
        }
    }

    /**
     * Save Preferred Gender value to profile.
     */
    public void savePreferredGender() {
        // If there is no preferred gender just set it to the gender
        if (view.getComboGenderPref().equals("")) {
            if (view.getComboGender() != null) {
                currentProfile.setPreferredGender(view.getComboGender().toString());
            }
        } else {
            currentProfile.setPreferredGender(view.getComboGenderPref());
        }
    }

    /**
     * Save Preferred Name field to profile.
     */
    public void savePreferredName() {
        currentProfile.setPreferredName(view.getPreferredNameField());
    }

    /**
     * Save Region field to profile.
     */
    public void saveRegion() {
        if (!view.getComboRegion().isDisabled()) {
            if (view.getComboRegion().getValue() != null) {
                currentProfile.setRegion(view.getComboRegion().getValue());
            }
        } else {
            if (view.getRegionField() != null) {
                currentProfile.setRegion(view.getRegionField());
            }
        }
    }

    /**
     * Save Weight field to profile.
     */
    public void saveWeight() {
        if (!view.getWeightField().isEmpty()) {
            currentProfile.setWeight(Double.valueOf(view.getWeightField()));
        } else {
            currentProfile.setWeight(0.0);
        }
    }

    /**
     * Save Alcohol Consumption field to profile.
     */
    public void saveAlcoholConsumption() {
        if (!view.getAlcoholConsumptionField().isEmpty()) {
            currentProfile.setAlcoholConsumption(view.getAlcoholConsumptionField());
        }
    }

    /**
     * Save Blood Pressure field to profile. Must be in format of Systolic/Diastolic.
     */
    public void saveBloodPressure() {
        if (!view.getBloodPressureField().isEmpty() && view.getBloodPressureField().contains("/")) {
            String systolic = view.getBloodPressureField().substring(
                    0, view.getBloodPressureField().indexOf('/')
            ).trim();
            currentProfile.setBloodPressureSystolic(Integer.valueOf(systolic));

            String diastolic = view.getBloodPressureField().substring(
                    view.getBloodPressureField().lastIndexOf('/') + 1
            ).trim();
            currentProfile.setBloodPressureDiastolic(Integer.valueOf(diastolic));
        }
    }

    /**
     * Save Blood Type field to profile.
     */
    public void saveBloodType() {
        if (!view.getBloodTypeField().isEmpty()) {
            currentProfile.setBloodType(view.getBloodTypeField());
        }
    }

    /**
     * Save Smoker Status to profile.
     */
    public void saveIsSmoker() {
        // TODO this should be a checkbox and not a radio button.
        currentProfile.setIsSmoker(view.getIsSmokerCheckBox());
    }

    /**
     * Save Country field to profile.
     */
    private void saveCountry() {
        if (view.getComboCountry() != null) {
            currentProfile.setCountry(view.getComboCountry().getValue().toString());
        }
    }

    /**
     * Save City field to profile.
     */
    private void saveCity() {
        if (view.getCityField() != null) {
            currentProfile.setCity(view.getCityField());
        }
    }

    /**
     * closes the edit donor window and reopens the donor.
     */
    @FXML
    public Profile close() {
        //todo sort out a way to check this
        return currentProfile;
        //if (isClinician) {
        //controller.setProfileViaClinician(currentProfile);
        //} else {
        //controller.setProfile(currentProfile);
        //}
    }

    /**
     * Sets the image in the edit profile view.
     *
     * @param chosenFile file chosen.
     * @param LOCALPATH local path of the chosen file.
     * @return Picture destination.
     */
    public File setImage(File chosenFile, File LOCALPATH) {
        if (chosenFile != null) {
            String extension = getFileExtension(chosenFile).toLowerCase();
            File deleteFile;
            if ("jpg".equalsIgnoreCase(extension)) {
                deleteFile = new File(LOCALPATH + "\\" + currentProfile.getNhi() + ".jpg");
            } else {
                deleteFile = new File(LOCALPATH + "\\" + currentProfile.getNhi() + ".png");
            }
            if (deleteFile.delete()) {
                System.out.println("Old file deleted successfully");
            } else {
                System.out.println("Failed to delete the old file");
            }
            File pictureDestination = new File(
                    LOCALPATH + "\\" + currentProfile.getNhi() + "." + extension);
            currentProfile.setPictureName(chosenFile.getName());
            return pictureDestination;
        }
        return null;
    }

    /**
     * Populates the death fields.
     */
    public void populateDeathFields() {
        if (currentProfile.getCountryOfDeath() == null) {
            if (currentProfile.getCountry() != null) {
                view.setComboCountryOfDeath(currentProfile.getCountry());
            }
        } else {
            view.setComboCountryOfDeath(currentProfile.getCountryOfDeath());
        }

        //city
        if (currentProfile.getCityOfDeath() == null) {
            if (currentProfile.getCity() != null) {
                view.setCityOfDeathField(currentProfile.getCity());
                view.setCityOfDeathField(currentProfile.getCity());
            }
        } else {
            view.setCityOfDeathField(currentProfile.getCity());
        }

        //region
        if (currentProfile.getRegionOfDeath() == null) {
            if (currentProfile.getRegion() != null) {
                if (currentProfile.getCountry() != null) {
                    if (currentProfile.getCountry().equals(MAINCOUNTRY)) {
                        view.setComboRegionOfDeath(currentProfile.getRegionOfDeath());
                        view.clearRegionOfDeathField();
                    } else {
                        view.setRegionOfDeathField(currentProfile.getRegionOfDeath());
                        //comboRegionOfDeath.clearSelection()
                    }
                } else {
                    view.setRegionOfDeathField(currentProfile.getRegion());
                    //comboRegionOfDeath.clearSelection()
                }
            }
        } else {
            if (currentProfile.getCountry() != null) {
                if (currentProfile.getCountry().equals(MAINCOUNTRY)) {
                    view.setComboRegionOfDeath(currentProfile.getRegionOfDeath());
                    view.clearRegionOfDeathField();
                }
            } else {
                view.setRegionOfDeathField(currentProfile.getRegionOfDeath());
                //comboRegionOfDeath.clearSelection()
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
