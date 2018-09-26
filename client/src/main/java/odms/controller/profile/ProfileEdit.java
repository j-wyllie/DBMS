package odms.controller.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.history.CurrentHistory;
import odms.commons.model.history.History;
import odms.commons.model.profile.HLAType;
import odms.commons.model.profile.Profile;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.HlaController;
import odms.controller.data.AddressIO;
import odms.controller.data.ImageDataIO;
import odms.controller.database.DAOFactory;
import odms.controller.database.hla.HttpHLADAO;
import odms.controller.database.profile.ProfileDAO;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ProfileEdit extends CommonController {

    private Profile currentProfile;
    private odms.view.profile.ProfileEdit view;
    private Boolean isClinician;
    private static final CountriesEnum MAINCOUNTRY = CountriesEnum.NZ;

    /**
     * profile edit view connected to this controller.
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
    public void save() throws SQLException, IOException, IllegalArgumentException {
        // History Generation
        History action = new History(
                "profile",
                currentProfile.getId(),
                "update",
                "previous " + currentProfile.getAttributesSummary(),
                -1,
                null
        );

        saveDeathDetails();

        // Required General Fields
        saveChosenImage();
        saveDateOfBirth();
        saveGivenNames();
        saveLastNames();
        saveNhi();

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
        saveHLAtype();

        saveCity();
        saveCountry();
        saveRegion();

        ProfileDAO server = DAOFactory.getProfileDao();
        server.update(currentProfile);

        // history Changes
        action.setHistoryData(
                action.getHistoryData() + " new " + currentProfile.getAttributesSummary());
        action.setHistoryTimestamp(LocalDateTime.now());
        CurrentHistory.updateHistory(action);
    }

    /**
     * Saves the death details of the profile.
     */
    private void saveDeathDetails() {
        try {
            if (view.getDodDateTimePicker() != null) {
                if (view.getCityOfDeathField() != null && (view.getComboRegionOfDeath() != null
                        || view.getRegionOfDeathField() != null)) {
                    String validRegion = checkValidRegionOfDeath();
                    String validCity = checkValidCityOfDeath();
                    if (validRegion != null && validCity != null) {
                        saveCityofDeath(validCity);
                        saveRegionOfDeath(validRegion);
                        saveCountryOfDeath();
                    }
                } else {
                    throw new IllegalArgumentException(
                            "Please enter all required death details"
                    );
                }

            } else {
                currentProfile.setCountryOfDeath(null);
                currentProfile.setRegionOfDeath(null);
                currentProfile.setCityOfDeath(null);
                currentProfile.setDateOfDeath(null);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Checks that the region of death is valid.
     *
     * @return The string of the valid region, null otherwise.
     */
    private String checkValidRegionOfDeath() {

        if (view.getComboCountryOfDeath().equals(MAINCOUNTRY)) {
            if (view.getComboRegionOfDeath() != null && AddressIO
                    .checkValidRegion(
                            view.getComboRegionOfDeath(), view.getComboCountryOfDeath().getName())) {
                return view.getComboRegionOfDeath();
            } else {
                throw new IllegalArgumentException(
                        "Invalid Region of Death"
                );

            }
        } else {
            if (view.getRegionOfDeathField() != null && AddressIO
                    .checkValidRegion(view.getRegionOfDeathField(),
                            view.getComboCountryOfDeath().getName())) {
                return view.getRegionOfDeathField();
            } else if (view.getRegionOfDeathField() != null && !AddressIO
                    .checkValidRegion(view.getRegionOfDeathField(),
                            view.getComboCountryOfDeath().getName())) {
                throw new IllegalArgumentException(
                        "Invalid Region of Death"
                );
            }
        }
        return null;

    }

    /**
     * Save Region of death field to profile.
     *
     * @throws IllegalArgumentException Thrown when an invalid region is entered.
     */
    private void saveRegionOfDeath(String region) throws IllegalArgumentException {
        currentProfile.setRegionOfDeath(region);
    }

    /**
     * Save City of death field to profile.
     *
     * @param city city of death string
     */
    private void saveCityofDeath(String city) {
        currentProfile.setCityOfDeath(city);
    }

    /**
     * Save City of death field to profile.
     *
     * @throws IllegalArgumentException thrown when an invalid city is entered.
     */
    private String checkValidCityOfDeath() throws IllegalArgumentException {
        if (view.getComboCountryOfDeath().equals(MAINCOUNTRY)) {
            if (view.getCityOfDeathField() != null && AddressIO
                    .checkValidCity(view.getCityOfDeathField()
                            + "," + view.getComboRegionOfDeath() + "," + view
                            .getComboCountryOfDeath(), view.getCityOfDeathField(), MAINCOUNTRY.getName())) {
                return view.getCityOfDeathField();
            } else {
                throw new IllegalArgumentException(
                        "Invalid city of death."
                );
            }
        } else if (view.getRegionOfDeathField() != null) {
            if (AddressIO.checkValidCity(view.getCityOfDeathField()
                            + "," + view.getRegionOfDeathField() + "," + view.getComboCountryOfDeath(),
                    view.getCityOfDeathField(),
                    view.getComboCountryOfDeath().getName())) {
                return view.getCityOfDeathField();
            } else {
                throw new IllegalArgumentException(
                        "Invalid city of death."
                );
            }
        }
        return null;
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
    void saveDateOfBirth() throws IllegalArgumentException {
        if (view.getdobDatePicker() == null) {
            throw new IllegalArgumentException(
                    "Date of Birth field cannot be blank"
            );
        } else if (view.getdobDatePicker().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Date of Birth cannot be in the future"
            );
        }
        currentProfile.setDateOfBirth(view.getdobDatePicker());
    }

    /**
     * Save Chosen Image.
     *
     * @throws IOException if there is a exception handling the file
     */
    void saveChosenImage() throws IOException {
        File chosenFile = view.getChosenFile();
        if (chosenFile != null) {
            currentProfile.setPictureName(
                    ImageDataIO.deleteAndSaveImage(
                            chosenFile, currentProfile.getNhi()
                    )
            );
        } else if (view.getRemovePhoto()) {
            ImageDataIO.deleteImage(currentProfile.getNhi());
            currentProfile.setPictureName(null);
        }
    }

    /**
     * Save Given Names field to profile.
     *
     * @throws IllegalArgumentException if the field is empty
     */
    void saveGivenNames() throws IllegalArgumentException {
        if (view.getGivenNamesField().isEmpty()) {
            throw new IllegalArgumentException(
                    "Given Names field cannot be blank"
            );
        }
        currentProfile.setGivenNames(view.getGivenNamesField());
    }

    /**
     * Save IRD Number field to profile.
     *
     * @throws IllegalArgumentException if the field is empty
     */
    void saveNhi() throws IllegalArgumentException, SQLException {
        if (!view.getNhiField().equals(currentProfile.getNhi())) {
            if (!view.getNhiField().matches("^[A-HJ-NP-Z]{3}\\d{4}$")) {
                throw new IllegalArgumentException(
                        "NHI must be valid"
                );
            } else if (DAOFactory.getProfileDao().isUniqueUsername(view.getNhiField())) {
                throw new IllegalArgumentException(
                        "NHI is not unique."
                );
            }
        }

        currentProfile.setNhi(view.getNhiField());
    }

    /**
     * Save Last Names field to profile.
     *
     * @throws IllegalArgumentException if the field is empty
     */
    void saveLastNames() throws IllegalArgumentException {
        if (view.getLastNamesField().isEmpty()) {
            throw new IllegalArgumentException(
                    "Last Names field cannot be blank"
            );
        }
        currentProfile.setLastNames(view.getLastNamesField());
    }

    /**
     * Save Address field to profile.
     */
    void saveAddress() throws IllegalArgumentException {
        if (!view.getAddressField().isEmpty() && AddressIO
                .checkValidCountry(view.getAddressField(),
                        view.getComboCountry().getValue().toString())) {
            currentProfile.setAddress(view.getAddressField());
        } else if (!view.getAddressField().isEmpty() && !AddressIO
                .checkValidCountry(view.getAddressField(),
                        view.getComboCountry().getValue().toString())) {
            throw new IllegalArgumentException(
                    "Invalid address"
            );
        }
    }


    /**
     * Save Date of Death field to profile.
     *
     * @throws IllegalArgumentException if date is prior to birth date
     */
    void saveDateOfDeath() {
        if (view.getDodDateTimePicker() != null) {
            currentProfile.setDateOfDeath(LocalDateTime.from(view.getDodDateTimePicker()));
            Set<OrganEnum> organsRequired = new HashSet<>();
            organsRequired.addAll(currentProfile.getOrgansRequired());
            for (OrganEnum organEnum : organsRequired) {
                DAOFactory.getOrganDao().removeRequired(currentProfile, organEnum);
            }
            currentProfile.setReceiver(false);
        }
    }

    /**
     * Save Email field to profile.
     */
    void saveEmail() {
        if (!view.getEmailField().isEmpty()) {
            currentProfile.setEmail(view.getEmailField());
        }
    }

    /**
     * Save Gender field to profile.
     */
    void saveGender() {
        if (view.getComboGender() != null) {
            currentProfile.setGender(view.getComboGender().toString());
        }
    }

    /**
     * Save Height field to profile.
     */
    void saveHeight() {
        if (!view.getHeightField().isEmpty()) {
            currentProfile.setHeight(Double.valueOf(view.getHeightField()));
        } else {
            currentProfile.setHeight(0.0);
        }
    }

    /**
     * Save Phone field to profile.
     */
    void savePhone() {
        if (!view.getPhoneField().isEmpty()) {
            currentProfile.setPhone(view.getPhoneField());
        }
    }

    /**
     * Save Preferred Gender value to profile.
     */
    void savePreferredGender() {
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
    void savePreferredName() {
        currentProfile.setPreferredName(view.getPreferredNameField());
    }

    /**
     * Save Region field to profile.
     */
    void saveRegion() {
        if (view.getComboRegion().isVisible()) {
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
    void saveWeight() {
        if (!view.getWeightField().isEmpty()) {
            currentProfile.setWeight(Double.valueOf(view.getWeightField()));
        } else {
            currentProfile.setWeight(0.0);
        }
    }

    /**
     * Save Alcohol Consumption field to profile.
     */
    void saveAlcoholConsumption() {
        if (!view.getAlcoholConsumptionField().isEmpty()) {
            currentProfile.setAlcoholConsumption(view.getAlcoholConsumptionField());
        }
    }

    /**
     * Save Blood Pressure field to profile. Must be in format of Systolic/Diastolic.
     */
    void saveBloodPressure() {
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
    void saveBloodType() {
        if (!view.getBloodTypeField().isEmpty()) {
            currentProfile.setBloodType(view.getBloodTypeField());
        }
    }

    /**
     * Save Smoker Status to profile.
     */
    void saveIsSmoker() {
        currentProfile.setIsSmoker(view.getIsSmokerCheckBox());
    }

    public void saveHLAtype() {
        try {
            // add primary HLA values
            List<String> hlas = new ArrayList<>();
            hlas.add(view.getHLAXAField());
            hlas.add(view.getHLAXBField());
            hlas.add(view.getHLAXCField());
            hlas.add(view.getHLAXDPField());
            hlas.add(view.getHLAXDQField());
            hlas.add(view.getHLAXDRField());
            hlas.add(view.getHLAYAField());
            hlas.add(view.getHLAYBField());
            hlas.add(view.getHLAYCField());
            hlas.add(view.getHLAYDPField());
            hlas.add(view.getHLAYDQField());
            hlas.add(view.getHLAYDRField());

            if (hlas.toString().isEmpty()) {
                return;
            }

            List<Integer> hlasInt = new ArrayList<>();
            for (String hla : hlas) {
                hlasInt.add(Integer.valueOf(hla));
            }
            HLAType hlaType = new HLAType(hlasInt);

            // add secondary HLA values (move to hla controller)
            for (Object secondaryHlaObject : view.getSecondaryHlaListView().getItems().toArray()) {
                String secondaryHla = String.valueOf(secondaryHlaObject);
                String gene = secondaryHla.replaceAll(HLAType.ALELLE, "");
                String allele = secondaryHla.replaceAll(HLAType.GENE, "");
                hlaType.addSecondaryAntigen(gene, Integer.valueOf(allele));
            }

            HttpHLADAO httpHLADAO = new HttpHLADAO();
            httpHLADAO.edit(currentProfile.getId(), hlaType);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("All Primary HLA Antigen values must be entered");
        }
    }

    /**
     * Save Country field to profile.
     */
    private void saveCountry() {
        if (view.getComboCountry().getValue() != null) {
            currentProfile.setCountry(CountriesEnum.getEnumByString(view.getComboCountry().getValue().toString()));
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
        return currentProfile;
    }

    /**
     * Populates the death fields.
     */
    public void populateDeathFields() {
        if (currentProfile.getCountryOfDeath() == null) {
            if (currentProfile.getCountry() != null) {
                view.setComboCountryOfDeath(currentProfile.getCountry().getName());
            }
        } else {
            view.setComboCountryOfDeath(currentProfile.getCountryOfDeath().getName());
        }

        // city
        if (currentProfile.getCityOfDeath() == null) {
            if (currentProfile.getCity() != null) {
                view.setCityOfDeathField(currentProfile.getCity());
            }
        } else {
            view.setCityOfDeathField(currentProfile.getCityOfDeath());
        }

        // region
        if (currentProfile.getRegionOfDeath() == null) {
            if (currentProfile.getRegion() != null) {
                if (currentProfile.getCountry() != null) {
                    if (currentProfile.getCountry().equals(MAINCOUNTRY)) {
                        view.setComboRegionOfDeath(currentProfile.getRegionOfDeath());
                        view.clearRegionOfDeathField();
                    } else {
                        view.setRegionOfDeathField(currentProfile.getRegionOfDeath());
                    }
                } else {
                    view.setRegionOfDeathField(currentProfile.getRegion());
                }
            }
        } else {
            if (currentProfile.getCountryOfDeath().equals(MAINCOUNTRY)) {
                view.setComboRegionOfDeath(currentProfile.getRegionOfDeath());
                view.clearRegionOfDeathField();
            } else {
                view.setRegionOfDeathField(currentProfile.getRegionOfDeath());
            }
        }
    }

    public void handelSecondaryHlaEntered() {
        String newHla = view.getSecondaryHlaField().toUpperCase();
        if (newHla.isEmpty() || !newHla.matches(HLAType.SECONDARY_TYPE)) {
            return;
        }
        view.clearSecondaryHlaField();

        ObservableList<String> secondaryHlas = view.getSecondaryHlaListView().getItems();
        secondaryHlas.add(0, newHla);
        view.getSecondaryHlaListView().setItems(secondaryHlas);
    }

    public void handelSecondaryHlaDeleted(List<String> HLAsToRemove) {
        for (String hla : HLAsToRemove) {
            view.getSecondaryHlaListView().getItems().remove(hla);
        }
    }

    public void setCurrentProfile(Profile donor) {
        currentProfile = donor;
    }

    public void setIsClinician(Boolean bool) {
        isClinician = bool;
    }

    public boolean getManuallyExpiredOrgans() throws SQLException {
        return !DAOFactory.getOrganDao().getExpired(currentProfile).isEmpty();
    }
}
