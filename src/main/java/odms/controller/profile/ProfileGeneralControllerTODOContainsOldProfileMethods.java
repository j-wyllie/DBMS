package odms.controller.profile;

import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import odms.model.profile.Attribute;
import odms.model.profile.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ProfileGeneralControllerTODOContainsOldProfileMethods {

    /**
     * Sets the attributes that are passed into the constructor
     *
     * @param attributes the attributes given in the constructor
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong
     */
    public static void setExtraAttributes(List<String> attributes, Profile profile)
            throws IllegalArgumentException {
        //todo Change how setExtraAttributes works
        for (String val : attributes) {
            String[] parts = val.split("=");
            if (parts.length == 1) {
                String[] newParts = {parts[0], ""};
                setGivenAttribute(newParts, profile);
            } else {
                setGivenAttribute(parts, profile);
            }
        }
    }

    /**
     * Returns the string value to populate the Donor/Receiver column in the clinician search
     * table.
     *
     * @return a string depicting whether to profile is a donor, receiver, or both.
     */
    public SimpleStringProperty donorReceiverProperty(Profile profile) {
        SimpleStringProperty result = new SimpleStringProperty();
        if ((profile.getDonor() != null) && profile.getDonor()) {
            if ((profile.getReceiver() != null) && profile.getReceiver()) {
                result.setValue("Donor/Receiver");
            } else {
                result.setValue("Donor");
            }
        } else if ((profile.getReceiver() != null) && profile.getReceiver()) {
            result.setValue("Receiver");

        }
        return result;
    }

    /**
     * Logs which property was updated and the time it was updated Also changes the last updated
     * property
     *
     * @param property the property that was updated
     */
    public static void generateUpdateInfo(String property, Profile profile) {

        LocalDateTime currentTime = LocalDateTime.now();
        profile.setLastUpdated();
        String output = property + " updated at " + currentTime
                .format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"));
        profile.getUpdateActions().add(output);
    }

    /**
     * Calculates and returns the profiles bmi
     *
     * @return BMI
     */
    public static Double calculateBMI(Profile profile) {
        return profile.getWeight() / ((profile.getHeight()) * (profile.getHeight()));
    }

    /**
     * Compares the profile object to another profile object. Result is determined by
     * lexicographical order of profile full name.
     *
     * @param other another profile object to compare to.
     * @return int value to show if object is equal, greater than ore less than the other profile
     * object.
     */
    public int compareTo(Profile other, Profile profile) {
        return profile.getFullName().toLowerCase().compareTo(other.getFullName().toLowerCase());
    }

    /**
     * Calls the relevant method to set the attribute
     *
     * @param parts a list with an attribute and value
     * @throws IllegalArgumentException thrown when an attribute that isn't valid is given
     */
    private static void setGivenAttribute(String[] parts, Profile profile) throws IllegalArgumentException {
        //todo Change how setExtraAttributes works
        String attrName = parts[0];
        String value = null;
        if (!parts[1].equals(null)) {
            value = parts[1].replace("\"", ""); // get rid of the speech marks;
        }

        if (attrName.equals(Attribute.GIVENNAMES.getText())) {
            profile.setGivenNames(value);
        } else if (attrName.equals(Attribute.LASTNAMES.getText())) {
            profile.setLastNames(value);
        } else if (attrName.equals(Attribute.DATEOFBIRTH.getText())) {
            String[] dates = value.split("-");
            LocalDate date = LocalDate.of(
                    Integer.valueOf(dates[2]),
                    Integer.valueOf(dates[1]),
                    Integer.valueOf(dates[0])
            );
            if (date.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "Date of birth cannot be a future date"
                );
            }
            profile.setDateOfBirth(date);
        } else if (attrName.equals(Attribute.DATEOFDEATH.getText())) {
            if (value.equals("null")) {
                profile.setDateOfDeath(null);
            } else {
                String[] dates = value.split("-");
                LocalDate date = LocalDate.of(
                        Integer.valueOf(dates[2]),
                        Integer.valueOf(dates[1]),
                        Integer.valueOf(dates[0])
                );
                profile.setDateOfDeath(date);
            }
        } else if (attrName.equals(Attribute.GENDER.getText())) {
            profile.setGender(value.toLowerCase());
        } else if (attrName.equals(Attribute.HEIGHT.getText())) {
            try {
                if (value.equals("null")) {
                    value = "0";
                }
                profile.setHeight(Double.valueOf(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid height entered");
            }
        } else if (attrName.equals(Attribute.WEIGHT.getText())) {
            try {
                if (value.equals("null")) {
                    value = "0";
                }
                profile.setWeight(Double.valueOf(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid weight entered");
            }
        } else if (attrName.equals(Attribute.BLOODTYPE.getText())) {
            if (value.equals("null") || value.equals("")) {
                value = null;
            }
            profile.setBloodType(value);
        } else if (attrName.equals(Attribute.ADDRESS.getText())) {
            profile.setAddress(value);
        } else if (attrName.equals(Attribute.REGION.getText())) {
            profile.setRegion(value);
        } else if (attrName.equals(Attribute.IRD.getText())) {
            try {
                profile.setIrdNumber(Integer.valueOf(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid IRD number entered");
            }
        } else if (attrName.equals("isSmoker")) {
            profile.setIsSmoker(Boolean.valueOf(value));
        } else if (attrName.equals("alcoholConsumption")) {
            profile.setAlcoholConsumption(value);
        } else if (attrName.equals("bloodPressureSystolic")) {
            if (value.equals("null")) {
                profile.setBloodPressureSystolic(null);
            } else {
                profile.setBloodPressureSystolic(Integer.valueOf(value));
            }
        } else if (attrName.equals("bloodPressureDiastolic")) {
            if (value.equals("null")) {
                profile.setBloodPressureDiastolic(null);
            } else {
                profile.setBloodPressureDiastolic(Integer.valueOf(value));
            }
        } else if (attrName.equals("phone")) {
            profile.setPhone(value);
        } else if (attrName.equals("email")) {
            profile.setEmail(value);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
