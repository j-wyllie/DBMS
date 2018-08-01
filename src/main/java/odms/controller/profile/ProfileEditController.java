package odms.controller.profile;

import static odms.App.getProfileDb;
import static odms.controller.GuiMain.getCurrentDatabase;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.fxml.FXML;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.data.ProfileDataIO;
import odms.controller.database.DAOFactory;
import odms.controller.database.ProfileDAO;
import odms.controller.history.HistoryController;
import odms.model.history.History;
import odms.model.profile.Profile;
import odms.view.profile.ProfileEditView;

public class ProfileEditController extends CommonController {

    private Profile currentProfile;
    private ProfileEditView view;


    private Boolean isClinician;

   public ProfileEditController(ProfileEditView v) {
       view = v;
   }

    /**
     * Button handler to save the changes made to the fields.
     *
     */
    @FXML
    public void save() {
        if (AlertController.saveChanges()) {
            try {
                // history Generation
                History action = new History("profile", currentProfile.getId(), "update",
                        "previous " + currentProfile.getAttributesSummary(), -1, null);

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

                ProfileDAO database = DAOFactory.getProfileDao();
                database.update(currentProfile);
                ProfileDataIO.saveData(getProfileDb());

                // history Changes
                action.setHistoryData(
                        action.getHistoryData() + " new " + currentProfile.getAttributesSummary());
                action.setHistoryTimestamp(LocalDateTime.now());
                HistoryController.updateHistory(action);

            } catch (Exception e) {
                AlertController.invalidEntry(
                        e.getMessage() + "\n" +
                                "Changes not saved."
                );
            }
        }
    }

    /**
     * Save Date of Birth field to profile.
     *
     * @throws IllegalArgumentException if the field is empty
     */
    public void saveDateOfBirth() throws IllegalArgumentException {
        if (view.getdobDatePicker() == null) {
            throw new IllegalArgumentException("Date of Birth field cannot be blank");
        } else if(view.getdobDatePicker().isAfter(LocalDate.now())) {
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
        if (view.getNhiField().isEmpty()) {
            throw new IllegalArgumentException("NHI Number field cannot be blank");
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
    public void saveAddress() {
        if (!view.getAddressField().isEmpty()) {
            currentProfile.setAddress(view.getAddressField());
        }
    }

    /**
     * Save Date of Death field to profile.
     *
     * @throws IllegalArgumentException if date is prior to birth date
     */
    public void saveDateOfDeath() throws IllegalArgumentException {
        if (view.getDODDatePicker() != null) {
            currentProfile.setDateOfDeath(view.getDODDatePicker());
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
        if (!view.getRegionField().isEmpty()) {
            currentProfile.setRegion(view.getRegionField());
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
                    0, view.getBloodPressureField().indexOf("/")
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
        currentProfile.setIsSmoker(view.getIsSmokerRadioButton());
    }

    /**
     * closes the edit donor window and reopens the donor.
     *
     */
    @FXML
    public Profile close() throws IOException {
        //todo sort out a way to check this
        return currentProfile;
        //if (isClinician) {
            //controller.setProfileViaClinician(currentProfile);
        //} else {
            //controller.setProfile(currentProfile);
        //}
    }



    public void setCurrentProfile(Profile donor) {
        currentProfile = donor;
        System.out.println(currentProfile);
    }

    public void setIsClinician(Boolean bool) {
        isClinician = bool;
    }
}
