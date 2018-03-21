package odms.donor;

import java.util.Collections;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    private Set<Organ> donatedOrgans = new HashSet<>();


    private Integer irdNumber;
    private LocalDateTime timeOfCreation;
    private LocalDateTime lastUpdated;

    private Integer id;


    public Donor(){

    }
    /**
     * Instantiates the Donor class
     * @param attributes the list of attributes in attribute="value" form
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong
     */
    public Donor (ArrayList<String> attributes) throws IllegalArgumentException {
        setExtraAttributes(attributes);

        if (getGivenNames() == null || getLastNames() == null || getDateOfBirth() == null || getIrdNumber() == null) {
            throw new IllegalArgumentException();
        }
        timeOfCreation = LocalDateTime.now();
    }

    /**
     * Sets the attributes that are passed into the constructor
     * @param attributes the attributes given in the constructor
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong
     */
    public void setExtraAttributes(ArrayList<String> attributes) throws IllegalArgumentException {
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
        String value = parts[1].replace("\"", ""); // get rid of the speech marks;

        if (attrName.equals(Attribute.GIVENNAMES.getText())) {
            setGivenNames(value);
        } else if (attrName.equals(Attribute.LASTNAMES.getText())) {
            setLastNames(value);
        } else if (attrName.equals(Attribute.DATEOFBIRTH.getText())) {
            String[] dates = value.split("-");
            LocalDate date = LocalDate.of(Integer.valueOf(dates[2]), Integer.valueOf(dates[1]), Integer.valueOf(dates[0]));
            setDateOfBirth(date);
        } else if (attrName.equals(Attribute.DATEOFDEATH.getText())) {
            if(value.equals("null")){
                setDateOfDeath(null);
            } else {
                String[] dates = value.split("-");
                LocalDate date = LocalDate.of(Integer.valueOf(dates[2]), Integer.valueOf(dates[1]), Integer.valueOf(dates[0]));
                setDateOfDeath(date);
            }
        } else if (attrName.equals(Attribute.GENDER.getText()) ){
            setGender(value.toLowerCase());
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
            if(value.equals("null")) {
                value = null;
            }
            setBloodType(value);
        } else if (attrName.equals(Attribute.ADDRESS.getText())) {
            setAddress(value);
        } else if (attrName.equals(Attribute.REGION.getText())) {
            setRegion(value);
        } else if (attrName.equals(Attribute.IRD.getText())) {
            try {
                setIrdNumber(Integer.valueOf(value));
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
     * View the list of donations that the donor has made
     */
    public void viewDonations() {
        String output = "Organs donated:  ";

        for (Organ org : donatedOrgans) {
            output += org.getName() + ", ";
        }

        // Did this to make the output look nicer with commas
        System.out.println(output.substring(0, output.length() - 2));
    }

    /**
     * Outputs the donor's attributes
     */
    public void viewAttributes() {
        if (irdNumber != null) {
            System.out.println("IRD: " + irdNumber);
        }

        if (givenNames != null) {
            System.out.println("Given Names: " + givenNames);
        }

        if (lastNames != null) {
            System.out.println("Last Names: " + lastNames);
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

        if (organs.size() > 0) {
            viewOrgans();
        }

        System.out.println("IRD: " + irdNumber);

        System.out.println("Last updated at: " + lastUpdated.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")));
    }

    public String getAttributesSummary() {
        String summary = "";
        summary = summary +("ird=" + irdNumber);
        summary = summary +"," +("given-names=" + givenNames);
        summary = summary +"," +("last-names=" + lastNames);
        summary = summary +"," +("dob=" + dateOfBirth.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        if(dateOfDeath==null){summary = summary +"," +("dod=" + null);}
        else{summary = summary +"," +("dod=" + dateOfDeath.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));}
        summary = summary +"," +("gender=" + gender);
        summary = summary +"," +("height=" + height);
        summary = summary +"," +("weight=" + weight);
        summary = summary +"," +("blood-type=" + bloodType);
        summary = summary +"," +("address=" + address);
        summary = summary +"," +("region=" + region);
        return summary;
    }

    /**
     * Add a set of organs to the list of organs that the donor wants to donate
     * @param organs a set of organs they want to donate
     */
    public void addOrgans(Set<String> organs) throws IllegalArgumentException {
        if (Collections.disjoint(organs, this.organs)) {
            generateUpdateInfo("organs");

            for (String org : organs) {
                String newOrgan = org.trim().toLowerCase();
                if (newOrgan.equals(Organ.BONE.getName())) {
                    this.organs.add(Organ.BONE);
                } else if (newOrgan.equals(Organ.LIVER.getName())) {
                    this.organs.add(Organ.LIVER);
                } else if (newOrgan.equals(Organ.KIDNEY.getName())) {
                    this.organs.add(Organ.KIDNEY);
                } else if (newOrgan.equals(Organ.PANCREAS.getName())) {
                    this.organs.add(Organ.PANCREAS);
                } else if (newOrgan.equals(Organ.HEART.getName())) {
                    this.organs.add(Organ.HEART);
                } else if (newOrgan.equals(Organ.LUNG.getName())) {
                    this.organs.add(Organ.LUNG);
                } else if (newOrgan.equals(Organ.INTESTINE.getName())) {
                    this.organs.add(Organ.INTESTINE);
                } else if (newOrgan.equals(Organ.CORNEA.getName())) {
                    this.organs.add(Organ.CORNEA);
                } else if (newOrgan.equals(Organ.MIDDLE_EAR.getName())) {
                    this.organs.add(Organ.MIDDLE_EAR);
                } else if (newOrgan.equals(Organ.SKIN.getName())) {
                    this.organs.add(Organ.SKIN);
                } else if (newOrgan.equals(Organ.BONE.getName())) {
                    this.organs.add(Organ.BONE);
                } else if (newOrgan.equals(Organ.BONE_MARROW.getName())) {
                    this.organs.add(Organ.BONE_MARROW);
                } else if (newOrgan.equals(Organ.CONNECTIVE_TISSUE.getName())) {
                    this.organs.add(Organ.CONNECTIVE_TISSUE);
                } else {
                    throw new IllegalArgumentException();
                }
            }
        } else {
            throw new IllegalArgumentException();
        }

    }

    /**
     * Add a set of organs to the list of organs that the donor has donated
     * @param organs a set of organs that the donor has donated
     */
    public void addDonations(Set<String> organs) {
        generateUpdateInfo("donatedOrgans");
        for (String org : organs) {
            String newOrgan = org.trim().toLowerCase();
            if (newOrgan.equals(Organ.BONE.getName())) {
                this.donatedOrgans.add(Organ.BONE);
            } else if (newOrgan.equals(Organ.LIVER.getName())) {
                this.donatedOrgans.add(Organ.LIVER);
            } else if (newOrgan.equals(Organ.KIDNEY.getName())) {
                this.donatedOrgans.add(Organ.KIDNEY);
            } else if (newOrgan.equals(Organ.PANCREAS.getName())) {
                this.donatedOrgans.add(Organ.PANCREAS);
            } else if (newOrgan.equals(Organ.HEART.getName())) {
                this.donatedOrgans.add(Organ.HEART);
            } else if (newOrgan.equals(Organ.LUNG.getName())) {
                this.donatedOrgans.add(Organ.LUNG);
            } else if (newOrgan.equals(Organ.INTESTINE.getName())) {
                this.donatedOrgans.add(Organ.INTESTINE);
            } else if (newOrgan.equals(Organ.CORNEA.getName())) {
                this.donatedOrgans.add(Organ.CORNEA);
            } else if (newOrgan.equals(Organ.MIDDLE_EAR.getName())) {
                this.donatedOrgans.add(Organ.MIDDLE_EAR);
            } else if (newOrgan.equals(Organ.SKIN.getName())) {
                this.donatedOrgans.add(Organ.SKIN);
            } else if (newOrgan.equals(Organ.BONE.getName())) {
                this.donatedOrgans.add(Organ.BONE);
            } else if (newOrgan.equals(Organ.BONE_MARROW.getName())) {
                this.donatedOrgans.add(Organ.BONE_MARROW);
            } else if (newOrgan.equals(Organ.CONNECTIVE_TISSUE.getName())) {
                this.donatedOrgans.add(Organ.CONNECTIVE_TISSUE);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    public void removeDonations(Set<String> organs) {
        generateUpdateInfo("donatedOrgans");
        for (String org : organs) {
            String newOrgan = org.trim().toLowerCase();
            if (newOrgan.equals(Organ.BONE.getName())) {
                this.donatedOrgans.remove(Organ.BONE);
            } else if (newOrgan.equals(Organ.LIVER.getName())) {
                this.donatedOrgans.remove(Organ.LIVER);
            } else if (newOrgan.equals(Organ.KIDNEY.getName())) {
                this.donatedOrgans.remove(Organ.KIDNEY);
            } else if (newOrgan.equals(Organ.PANCREAS.getName())) {
                this.donatedOrgans.remove(Organ.PANCREAS);
            } else if (newOrgan.equals(Organ.HEART.getName())) {
                this.donatedOrgans.remove(Organ.HEART);
            } else if (newOrgan.equals(Organ.LUNG.getName())) {
                this.donatedOrgans.remove(Organ.LUNG);
            } else if (newOrgan.equals(Organ.INTESTINE.getName())) {
                this.donatedOrgans.remove(Organ.INTESTINE);
            } else if (newOrgan.equals(Organ.CORNEA.getName())) {
                this.donatedOrgans.remove(Organ.CORNEA);
            } else if (newOrgan.equals(Organ.MIDDLE_EAR.getName())) {
                this.donatedOrgans.remove(Organ.MIDDLE_EAR);
            } else if (newOrgan.equals(Organ.SKIN.getName())) {
                this.donatedOrgans.remove(Organ.SKIN);
            } else if (newOrgan.equals(Organ.BONE.getName())) {
                this.donatedOrgans.remove(Organ.BONE);
            } else if (newOrgan.equals(Organ.BONE_MARROW.getName())) {
                this.donatedOrgans.remove(Organ.BONE_MARROW);
            } else if (newOrgan.equals(Organ.CONNECTIVE_TISSUE.getName())) {
                this.donatedOrgans.remove(Organ.CONNECTIVE_TISSUE);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * Remove a set of organs from the list of organs that the use wants to donate
     * @param organs a set of organs to be removed
     */
    public void removeOrgans(Set<String> organs) throws IllegalArgumentException {
        generateUpdateInfo("organs");
        for (String org : organs) {
            String newOrgan = org.trim().toLowerCase();
            if (newOrgan.equals(Organ.BONE.getName())) {
                this.organs.remove(Organ.BONE);
            } else if (newOrgan.equals(Organ.LIVER.getName())) {
                this.organs.remove(Organ.LIVER);
            } else if (newOrgan.equals(Organ.KIDNEY.getName())) {
                this.organs.remove(Organ.KIDNEY);
            } else if (newOrgan.equals(Organ.PANCREAS.getName())) {
                this.organs.remove(Organ.PANCREAS);
            } else if (newOrgan.equals(Organ.HEART.getName())) {
                this.organs.remove(Organ.HEART);
            } else if (newOrgan.equals(Organ.LUNG.getName())) {
                this.organs.remove(Organ.LUNG);
            } else if (newOrgan.equals(Organ.INTESTINE.getName())) {
                this.organs.remove(Organ.INTESTINE);
            } else if (newOrgan.equals(Organ.CORNEA.getName())) {
                this.organs.remove(Organ.CORNEA);
            } else if (newOrgan.equals(Organ.MIDDLE_EAR.getName())) {
                this.organs.remove(Organ.MIDDLE_EAR);
            } else if (newOrgan.equals(Organ.SKIN.getName())) {
                this.organs.remove(Organ.SKIN);
            } else if (newOrgan.equals(Organ.BONE.getName())) {
                this.organs.remove(Organ.BONE);
            } else if (newOrgan.equals(Organ.BONE_MARROW.getName())) {
                this.organs.remove(Organ.BONE_MARROW);
            } else if (newOrgan.equals(Organ.CONNECTIVE_TISSUE.getName())) {
                this.organs.remove(Organ.CONNECTIVE_TISSUE);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }


    /**
     * Logs which property was updated and the time it was updated
     * Also changes the last updated property
     * @param property the property that was updated
     */
    private void generateUpdateInfo(String property) {
        LocalDateTime currentTime = LocalDateTime.now();
        lastUpdated = currentTime;
        String output = property + " updated at " + currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"));
        updateActions.add(output);
    }

    public Set<Organ> getDonatedOrgans() {
        return donatedOrgans;
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

    public Integer getIrdNumber() {
        return irdNumber;
    }

    public void setIrdNumber(Integer irdNumber) {
        generateUpdateInfo("ird");
        this.irdNumber = irdNumber;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ArrayList<String> getUpdateActions() {
        return updateActions;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}