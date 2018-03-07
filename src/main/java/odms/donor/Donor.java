package odms.donor;

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

    private Set<Organ> organs = new HashSet<>();

    private String IRD; // Not being used at the moment, not sure how we want to make donor's unique
    private LocalDateTime timeOfCreation;

    private Integer id;

    /*
    public Donor(String givenNames, String lastNames, LocalDate dateOfBirth, String IRD, ArrayList<String> attributes) {
        this.givenNames = givenNames;
        this.lastNames = lastNames;
        this.dateOfBirth = dateOfBirth;
        this.IRD = IRD;

        setExtraAttributes(attributes);

        timeOfCreation = LocalDateTime.now();
    }

    public Donor(String givenNames, String lastNames, LocalDate dateOfBirth, String IRD) {
        this.givenNames = givenNames;
        this.lastNames = lastNames;
        this.dateOfBirth = dateOfBirth;
        this.IRD = IRD;

        timeOfCreation = LocalDateTime.now();
    }*/

    // This is probably the ideal way to do this
    public Donor (ArrayList<String> attributes) throws InstantiationException {
        setExtraAttributes(attributes);

        if (getGivenNames() == null || getLastNames() == null || getDateOfBirth() == null || getIRD() == null) {
            throw new InstantiationException();
        }

        timeOfCreation = LocalDateTime.now();
    }

    private void setExtraAttributes(ArrayList<String> attributes) {
        for (String val : attributes) {
            String[] parts = val.split("=");
            setGivenAttribute(parts);
        }
    }

    private void setGivenAttribute(String[] parts) {
        String attrName = parts[0];

        if (attrName.equals(Attribute.GIVENNAMES.getText())) {
            setGivenNames(parts[1]);
        } else if (attrName.equals(Attribute.LASTNAMES.getText())) {
            setLastNames(parts[1]);
        } else if (attrName.equals(Attribute.DATEOFBIRTH.getText())) {
            String[] dates = parts[1].split("-");
            LocalDate date = LocalDate.of(Integer.valueOf(dates[2]), Integer.valueOf(dates[1]), Integer.valueOf(dates[0]));
            setDateOfBirth(date);
        } else if (attrName.equals(Attribute.DATEOFDEATH.getText())) {
            String[] dates = parts[1].split("-");
            LocalDate date = LocalDate.of(Integer.valueOf(dates[2]), Integer.valueOf(dates[1]), Integer.valueOf(dates[0]));
            setDateOfDeath(date);
        } else if (attrName.equals(Attribute.GENDER.getText()) ){
            setGender(parts[1]);
        } else if (attrName.equals(Attribute.HEIGHT.getText())) {
            setHeight(Double.valueOf(parts[1]));
        } else if (attrName.equals(Attribute.WEIGHT.getText())) {
            setWeight(Double.valueOf(parts[1]));
        } else if (attrName.equals(Attribute.BLOODTYPE.getText())) {
            setBloodType(parts[1]);
        } else if (attrName.equals(Attribute.ADDRESS.getText())) {
            setAddress(parts[1]);
        } else if (attrName.equals(Attribute.REGION.getText())) {
            setRegion(parts[1]);
        } else if (attrName.equals(Attribute.IRD.getText())) {
            setIRD(parts[1]);
        }
    }

    public void viewOrgans() {
        String output = "Organs to donate: ";

        for (Organ org : organs) {
            output += org.getName() + ", ";
        }

        // Did this to make the output look nicer with commas
        System.out.println(output.substring(0, output.length() - 2));
    }

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

        System.out.println("Time of Creation: " + timeOfCreation
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
    }

    public void addOrgans(Set<Organ> organs) {
        this.organs.addAll(organs);
    }

    public void removeOrgans(Set<Organ> organs) {
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
        this.givenNames = givenNames;
    }

    public String getLastNames() {
        return lastNames;
    }

    public void setLastNames(String lastNames) {
        this.lastNames = lastNames;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getIRD() {
        return IRD;
    }

    public void setIRD(String IRD) {
        this.IRD = IRD;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}