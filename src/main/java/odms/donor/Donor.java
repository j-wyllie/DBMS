package odms.donor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.Collections;
import odms.commandlineview.Attribute;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Attr;

public class Donor {

    private String givenNames;
    private String lastNames;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private String gender;
    private double height;
    private double weight;
    private String bloodType;
    private String address;
    private String region;

    private ArrayList<String> updateActions = new ArrayList<>();

    private Set<Organ> organs = new HashSet<>();

    private Integer IRD;
    private LocalDateTime timeOfCreation;

    private Integer id;

    /**
     * Instantiates the Donor class
     * @param attributes the list of attributes in attribute="value" form
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong
     */
    public Donor (ArrayList<String> attributes) throws IllegalArgumentException {
        setExtraAttributes(attributes);

        if (getGivenNames() == null || getLastNames() == null || getDateOfBirth() == null || getIRD() == null) {
            throw new IllegalArgumentException();
        }
        timeOfCreation = LocalDateTime.now();
    }

    /**
     * Sets the attributes that are passed into the constructor
     * @param attributes the attributes given in the constructor
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong
     */
    private void setExtraAttributes(ArrayList<String> attributes) throws IllegalArgumentException {
        for (String val : attributes) {
            String[] parts = val.split("=");
            setGivenAttribute(parts);
        }
    }

    /**
     * Calls the relevant method to set the attribute
     * @param parts a list with an attribute and value
     * @throws IllegalArgumentException thrown when an attribute that isn't valid is given
     */
    private void setGivenAttribute(String[] parts) throws IllegalArgumentException {
        String attrName = parts[0];
        String value = parts[1].substring(1, parts[1].length() - 1); // get rid of the speech marks

        if (attrName.equals(Attribute.GIVENNAMES.getText())) {
            setGivenNames(value);
        } else if (attrName.equals(Attribute.LASTNAMES.getText())) {
            setLastNames(value);
        } else if (attrName.equals(Attribute.DATEOFBIRTH.getText())) {
            String[] dates = value.split("-");
            LocalDate date = LocalDate.of(Integer.valueOf(dates[2]), Integer.valueOf(dates[1]), Integer.valueOf(dates[0]));
            setDateOfBirth(date);
        } else if (attrName.equals(Attribute.DATEOFDEATH.getText())) {
            String[] dates = value.split("-");
            LocalDate date = LocalDate.of(Integer.valueOf(dates[2]), Integer.valueOf(dates[1]), Integer.valueOf(dates[0]));
            setDateOfDeath(date);
        } else if (attrName.equals(Attribute.GENDER.getText()) ){
            setGender(value);
        } else if (attrName.equals(Attribute.HEIGHT.getText())) {
            try {
                setHeight(Double.valueOf(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException();
            }
        } else if (attrName.equals(Attribute.WEIGHT.getText())) {
            try {
                setWeight(Double.valueOf(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException();
            }
        } else if (attrName.equals(Attribute.BLOODTYPE.getText())) {
            setBloodType(value);
        } else if (attrName.equals(Attribute.ADDRESS.getText())) {
            setAddress(value);
        } else if (attrName.equals(Attribute.REGION.getText())) {
            setRegion(value);
        } else if (attrName.equals(Attribute.IRD.getText())) {
            try {
                setIRD(Integer.valueOf(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Outputs the donor's organs that they want to donate
     */
    public void viewOrgans() {
        String output = "Organs to donate: ";

        for (Organ org : organs) {
            output += org.getName() + ", ";
        }

        // Did this to make the output look nicer with commas
        System.out.println(output.substring(0, output.length() - 2));
    }

    /**
     * Outputs the donor's attributes
     */
    public void viewAttributes() {
        if (givenNames != null) {
            System.out.println("Given Names: " + givenNames);
        }

        if (lastNames != null) {
            System.out.println("Last Name: " + lastNames);
        }

        System.out.println("Date Of Birth: " + dateOfBirth.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        if (dateOfDeath != null) {
            System.out.println("Date Of Death: " + dateOfDeath.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }

        if (gender != null) {
            System.out.println("Gender: " + gender);
        }

        if (height != 0.0) {
            System.out.println("Height: " + height + "cm");
        }

        if (weight != 0.0) {
            System.out.println("Weight: " + weight);
        }

        if (bloodType != null) {
            System.out.println("Blood Type: " + bloodType);
        }

        if (address != null) {
            System.out.println("Address: " + address);
        }

        if (region != null) {
            System.out.println("Region: " + region);
        }

        System.out.println("IRD: " + IRD);
    }

    /**
     * Add a set of organs to the list of organs that the donor wants to donate
     * @param organs a set of organs they want to donate
     */
    public void addOrgans(Set<Organ> organs) throws IllegalArgumentException {
        if (Collections.disjoint(organs, this.organs)) {
            generateUpdateInfo("organs");
            this.organs.addAll(organs);
        } else {
            throw new IllegalArgumentException();
        }

    }

    /**
     * Remove a set of organs from the list of organs that the use wants to donate
     * @param organs a set of organs to be removed
     */
    public void removeOrgans(Set<Organ> organs) {
        generateUpdateInfo("organs");
        this.organs.removeAll(organs);
    }

    public Set<Organ> getOrgans() {

        return organs;
    }

    public LocalDateTime getTimeOfCreation() {
        return timeOfCreation;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        generateUpdateInfo("given-names");
        this.givenNames = givenNames;
    }

    public String getLastNames() {
        return lastNames;
    }

    public void setLastNames(String lastNames) {
        generateUpdateInfo("last-names");
        this.lastNames = lastNames;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        generateUpdateInfo("dob");
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        generateUpdateInfo("dod");
        this.dateOfDeath = dateOfDeath;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        generateUpdateInfo("gender");
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        generateUpdateInfo("height");
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        generateUpdateInfo("weight");
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        generateUpdateInfo("blood-type");
        this.bloodType = bloodType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        generateUpdateInfo("address");
        this.address = address;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        generateUpdateInfo("region");
        this.region = region;
    }

    public Integer getIRD() {
        return IRD;
    }

    public void setIRD(Integer IRD) {
        generateUpdateInfo("ird");
        this.IRD = IRD;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ArrayList<String> getUpdateActions() {
        return updateActions;
    }

    private void generateUpdateInfo(String property) {
        String output = property + " updated at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss dd-MM-yyyy"));
        updateActions.add(output);
    }
}