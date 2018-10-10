package odms.controller.profile;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.NewZealandRegionsEnum;
import odms.commons.model.profile.Attribute;
import odms.commons.model.profile.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import odms.controller.database.DAOFactory;

@Slf4j
public class ProfileGeneralControllerTODOContainsOldProfileMethods {

    private ProfileGeneralControllerTODOContainsOldProfileMethods() {
        throw new IllegalStateException();
    }

    /**
     * Sets the attributes that are passed into the constructor.
     *
     * @param attributes the attributes given in the constructor.
     * @param profile the current profile.
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong.
     */
    public static void setExtraAttributes(List<String> attributes, Profile profile) {
        try {
            for (String val : attributes) {
                String[] parts = val.split("=");
                if (parts.length == 1) {
                    String[] newParts = {parts[0], ""};
                    setGivenAttribute(newParts, profile);
                } else {
                    setGivenAttribute(parts, profile);
                }
            }
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Logs which property was updated and the time it was updated Also changes the last updated.
     * property
     *
     * @param property the property that was updated.
     */
    static void generateUpdateInfo(String property, Profile profile) {

        LocalDateTime currentTime = LocalDateTime.now();
        profile.setLastUpdated();
        String output = property + " updated at " + currentTime
                .format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"));
        profile.getUpdateActions().add(output);
    }

    /**
     * Calls the relevant method to set the attribute.
     *
     * @param parts a list with an attribute and value.
     * @param profile profile object of current profile.
     * @throws IllegalArgumentException thrown when an attribute that isn't valid is given.
     */
    private static void setGivenAttribute(String[] parts, Profile profile)
            throws IllegalArgumentException {
        String attrName = parts[0];
        String value = null;
        if (parts[1] != null) {
            value = parts[1].replace("\"", "");
        }
        if (attrName.equalsIgnoreCase(Attribute.GIVEN_NAMES.getText())) {
            profile.setGivenNames(value);

        } else if (attrName.equalsIgnoreCase(Attribute.LAST_NAMES.getText())) {
            profile.setLastNames(value);

        } else if (attrName.equalsIgnoreCase(Attribute.DATE_OF_BIRTH.getText())) {
            String[] dates = value != null ? value.split("-") : null;
            LocalDate date = dates != null ? LocalDate.of(
                    Integer.valueOf(dates[2]),
                    Integer.valueOf(dates[1]),
                    Integer.valueOf(dates[0])
            ) : null;
            if (date != null && date.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "Date of birth cannot be a future date."
                );
            }
            profile.setDateOfBirth(date);

        } else if (attrName.equalsIgnoreCase(Attribute.DATE_OF_DEATH.getText())) {
            if ("null".equalsIgnoreCase(value)) {
                profile.setDateOfDeath(null);
            } else {
                String[] dates = value != null ? value.split("-") : null;
                LocalDate date = dates != null ? LocalDate.of(
                        Integer.valueOf(dates[2]),
                        Integer.valueOf(dates[1]),
                        Integer.valueOf(dates[0])
                ) : null;
                profile.setDateOfDeath(LocalDateTime.of(date, LocalTime.MIN));
                profile.setCountryOfDeath(profile.getCountry());
                profile.setCityOfDeath(profile.getCity());
                profile.setRegionOfDeath(profile.getRegion());
            }

        } else if (attrName.equalsIgnoreCase(Attribute.GENDER.getText())) {
            profile.setGender(value != null ? value.toLowerCase() : null);

        } else if (attrName.equalsIgnoreCase(Attribute.HEIGHT.getText()) ||
                attrName.equalsIgnoreCase(Attribute.WEIGHT.getText())) {
            try {
                value = value == null || "null".equalsIgnoreCase(value) ? "0" : value;
                profile.setHeight(Double.valueOf(value));
            } catch (NumberFormatException e) {
                if (attrName.equals(Attribute.HEIGHT.getText())) {
                    throw new IllegalArgumentException("Invalid height entered.");
                } else {
                    throw new IllegalArgumentException("Invalid weight entered.");
                }
            }

        } else if (attrName.equalsIgnoreCase(Attribute.BLOOD_TYPE.getText())) {
            value = "null".equalsIgnoreCase(value) || "".equalsIgnoreCase(value) ? null : value;
            profile.setBloodType(value);

        } else if (attrName.equalsIgnoreCase(Attribute.ADDRESS.getText())) {
            profile.setAddress(value);

        } else if (attrName.equalsIgnoreCase(Attribute.COUNTRY.getText())) {
            if (!DAOFactory.getSettingsDAO().getAllCountries(true).contains(value)) {
                throw new IllegalArgumentException("Must be a valid country!");
            }
            profile.setCountry(CountriesEnum.getEnumByString(value));

        } else if (attrName.equalsIgnoreCase(Attribute.REGION.getText())) {
            if (profile.getCountry() != null && (profile.getCountry().getName().toLowerCase()
                    .equalsIgnoreCase(CountriesEnum.NZ.getName()) ||
                    profile.getCountry().getName()
                            .equalsIgnoreCase(CountriesEnum.NZ.toString())) &&
                    !NewZealandRegionsEnum.toArrayList().contains(value)) {
                throw new IllegalArgumentException("Must be a region within New Zealand.");
            }
            profile.setRegion(value);

        } else if (attrName.equalsIgnoreCase(Attribute.NHI.getText())) {
            try {
                profile.setNhi(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid NHI entered.");
            }

        } else if (attrName.equalsIgnoreCase(Attribute.IS_SMOKER.getText())) {
            profile.setIsSmoker(Boolean.valueOf(value));

        } else if (attrName.equalsIgnoreCase(Attribute.ALCOHOL_CONSUMPTION.getText())) {
            profile.setAlcoholConsumption(value);

        } else if (attrName.equalsIgnoreCase(Attribute.BLOOD_PRESSURE_SYSTOLIC.getText())) {
            value = "null".equalsIgnoreCase(value) ? null : value;
            profile.setBloodPressureSystolic(value == null ? null : Integer.valueOf(value));

        } else if (attrName.equalsIgnoreCase(Attribute.BLOOD_PRESSURE_DIASTOLIC.getText())) {
            value = "null".equalsIgnoreCase(value) ? null : value;
            profile.setBloodPressureDiastolic(value == null ? null : Integer.valueOf(value));

        } else if (attrName.equalsIgnoreCase(Attribute.PHONE.getText())) {
            profile.setPhone(value);

        } else if (attrName.equalsIgnoreCase(Attribute.EMAIL.getText())) {
            profile.setEmail(value);

        } else {
            throw new IllegalArgumentException("Invalid field '" + attrName + "'.");
        }
        try {
            DAOFactory.getProfileDao().update(profile);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Cannot update fields.");
        }
        System.out.println("profile(s) successfully updated.");
    }
}
