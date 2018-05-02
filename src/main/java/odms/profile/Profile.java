package odms.profile;

import odms.cli.CommandUtils;

import java.time.Period;
import java.util.Arrays;
import java.util.Collections;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Profile {

    private Boolean donor;
    private Boolean receiver;

    private String givenNames;
    private String lastNames;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private String gender;
    private Double height;
    private Double weight;
    private String bloodType;
    private String address;
    private String region;

    private Boolean smoker;
    private String alcoholConsumption;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Set<String> chronicDiseases = new HashSet<>();

    private ArrayList<String> updateActions = new ArrayList<>();

    private Set<Organ> organs = new HashSet<>();
    private Set<Organ> donatedOrgans = new HashSet<>();
    private Set<Organ> organsRequired = new HashSet<>();

    private String phone;
    private String email;

    private Integer irdNumber;
    private LocalDateTime timeOfCreation;
    private LocalDateTime lastUpdated;

    private Integer id;

    /**
     * Instantiates the Profile class with data from the CLI
     * @param attributes the list of attributes in attribute="value" form
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong
     */
    public Profile(ArrayList<String> attributes) throws IllegalArgumentException {
        setExtraAttributes(attributes);

        if (getGivenNames() == null || getLastNames() == null || getDateOfBirth() == null || getIrdNumber() == null) {
            throw new IllegalArgumentException();
        }
        timeOfCreation = LocalDateTime.now();
    }

    /**
     * Instantiates the basic Profile class with a raw input of values
     * @param givenNames Profile's given names as String
     * @param lastNames Profile's last names as String
     * @param dob Profile's date of birth as a string
     * @param irdNumber Profile's IRD number as Integer
     */
    public Profile(String givenNames, String lastNames, String dob, Integer irdNumber) {
        // Build an arraylist so I can reuse the
        ArrayList<String> attr = new ArrayList<>();
        attr.add("given-names=\"" + givenNames + "\"");
        attr.add("last-names=\"" + lastNames + "\"");
        attr.add("ird=\"" + irdNumber + "\"");
        attr.add("dob=\"" + dob + "\"");

        setExtraAttributes(attr);

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
                if (value.equals("null")) {
                    value = "0";
                }
                setHeight(Double.valueOf(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException();
            }
        } else if (attrName.equals(Attribute.WEIGHT.getText())) {
            try {
                if (value.equals("null")) {
                    value = "0";
                }
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
        } else if (attrName.equals("smoker")) {
            try {
                setSmoker(Boolean.valueOf(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException();
            }
        } else if (attrName.equals("alcoholConsumption")) {
            setAlcoholConsumption(value);
        } else if (attrName.equals("bloodPressureSystolic")) {
            if(value.equals("null")) {
                value = "0";
            }
            setBloodPressureSystolic(Integer.valueOf(value));
        }else if (attrName.equals("bloodPressureDiastolic")) {
            if(value.equals("null")) {
                value = "0";
            }
            setBloodPressureDiastolic(Integer.valueOf(value));
        }else if (attrName.equals("phone")) {
            setPhone(value);
        }else if (attrName.equals("email")) {
            setEmail(value);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Outputs the profile's organs that they want to donate
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
     * View the list of donations that the profile has made
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
     * Outputs the profile's attributes
     */
    public void viewAttributes() {
        if (irdNumber != null) {
            System.out.println("IRD: " + irdNumber);
        }

        System.out.println("ODMS ID: " + id);

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
        summary = summary +"," +("smoker=" + smoker);
        summary = summary +"," +("alcoholConsumption=" + alcoholConsumption);
        summary = summary +"," +("bloodPressureSystolic=" + bloodPressureSystolic);
        summary = summary +"," +("bloodPressureDiastolic=" + bloodPressureDiastolic);
        summary = summary +"," +("phone=" + phone);
        summary = summary +"," +("email=" + email);
        return summary;
    }

    /**
     * This will add a csv list to the list of organs
     * @param organs the organs to add as a csv
     */
    public void addOrgansFromString(String organs) {
        String[] org = organs.split(",");
        addOrgansDonate(new HashSet<>(Arrays.asList(org)));
    }

    /**
     * Adds a csv list to the list of donations
     * @param organs the organs to add as a csv
     */
    public void addDonationFromString(String organs) {
        String[] org = organs.split(",");
        addDonations(new HashSet<>(Arrays.asList(org)));
    }

    /**
     * Adds a csv list of diseases to the list of donations
     * @param diseases the list of donations to add
     */
    public void addChronicDiseases(String diseases) {
        String[] allDiseases = diseases.split(",");
        for (String dis : allDiseases) {
            String newDis = dis.trim();
            chronicDiseases.add(newDis);
        }
    }

    /**
     * Returns the organs as a csv string
     * @return string of the organs
     */
    public String getOrgansAsCSV() {
        String out = "";
        int count = 0;
        int len = organs.size();

        for (Organ org : organs) {
            count++;
            if (count == len) {
                out += org.getName();
            } else {
                out += org.getName() + ", ";
            }
        }
        return out;
    }

    /**
     * returns the chronic diseases as a csv string
     * @return list of chronic dieseases
     */
    public String getChronicDiseasesAsCSV() {
        String out = "";
        int count = 0;
        int len = chronicDiseases.size();

        for (String disease : chronicDiseases) {
            count++;
            if (count == len) {
                out += disease;
            } else {
                out += disease + ", ";
            }
        }
        return out;
    }

    /**
     * returns the donations as a csv
     * @return String of donations as csv
     */
    public String getDonationsAsCSV() {
        String out = "";
        int count = 0;
        int len = donatedOrgans.size();

        for (Organ org : donatedOrgans) {
            count++;
            if (count == len) {
                out += org.getName();
            } else {
                out += org.getName() + ", ";
            }
        }
        return out;
    }

    /**
     * Add an organ to the organs donate list.
     * @param organ the organ the profile wishes to donate
     */
    public void addOrgan(Organ organ) {
        this.organs.add(organ);
    }

    /**
     * Consume a set of organs that the profile wants to receive and updates the profile to use this
     * new set.
     * @param organs the set of organs to be received
     */
    public void setOrgansRequired(Set<String> organs) {
        generateUpdateInfo("organsRequired");

        Set<Organ> newOrgans = new HashSet<>();

        for (String org : organs) {
            String newOrgan = org.trim().toUpperCase().replace(" ", "_");
            Organ organ = Organ.valueOf(newOrgan);
            newOrgans.add(organ);
            String action = "Profile " + this.getId() + " required organ " + organ + " at " + LocalDateTime.now();
            if (CommandUtils.getHistory().size() != 0) {
                if (CommandUtils.getPosition() != CommandUtils.getHistory().size() - 1) {
                    CommandUtils.currentSessionHistory.subList(CommandUtils.getPosition(),
                            CommandUtils.getHistory().size() - 1).clear();
                }
            }
            CommandUtils.currentSessionHistory.add(action);
            CommandUtils.historyPosition = CommandUtils.currentSessionHistory.size() - 1;
        }

        this.organsRequired = newOrgans;
    }

    /**
     * Add a set of organs to the list of organs that the profile wants to donate
     * @param organs the set of organs to donate
     */
    public void addOrgansDonate(Set<String> organs) throws IllegalArgumentException {
        generateUpdateInfo("donatedOrgans");

        Set<Organ> newOrgans = new HashSet<>();

        for (String org : organs) {
            String newOrgan = org.trim().toUpperCase();
            Organ organ = Organ.valueOf(newOrgan);
            newOrgans.add(organ);
        }

        if (Collections.disjoint(newOrgans, this.organs) && donor) {
            this.organs.addAll(newOrgans);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Set<Organ> getOrgansRequired() {
        return organsRequired;
    }

    /**
     * Add an organ to the list of donated organs.
     * @param organ the organ to be added
     */
    public void addDonation(Organ organ) {
        this.donatedOrgans.add(organ);
    }

    /**
     * Add a set of organs to the list of organs that the profile has donated
     * @param organs a set of organs that the profile has donated
     */
    public void addDonations(Set<String> organs) {
        generateUpdateInfo("donatedOrgans");
        for (String org : organs) {
            String newOrgan = org.trim().toUpperCase();
            Organ organ = Organ.valueOf(newOrgan);
            this.addDonation(organ);
        }
    }

    /**
     * Remove a set of organs from the list of organs that the profile has donated
     * @param organs a set of organs to remove from the list
     */
    public void removeDonations(Set<String> organs) {
        generateUpdateInfo("donatedOrgans");
        for (String org : organs) {
            String newOrgan = org.trim().toUpperCase();
            Organ organ = Organ.valueOf(newOrgan);
            this.donatedOrgans.remove(organ);
        }
    }

    /**
     * Remove a set of organs from the list of organs that the use wants to donate
     * @param organs a set of organs to be removed
     */
    public void removeOrgans(Set<String> organs) throws IllegalArgumentException {
        generateUpdateInfo("organs");

        Set<Organ> newOrgans = new HashSet<>();

        for (String org : organs) {
            String newOrgan = org.trim().toUpperCase();
            Organ organ = Organ.valueOf(newOrgan);
            newOrgans.add(organ);
        }

        if (!Collections.disjoint(newOrgans, this.organs)) {
            this.organs.removeAll(newOrgans);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setDonor(boolean donor) {
        this.donor = donor;
    }

    public boolean isDonor() {
        return donor;
    }

    public void setReceiver(boolean receiver) {
        this.receiver = receiver;
    }

    public boolean isReceiver() {
        return receiver;
    }

    /**
     * Calculates and returns the profiles bmi
     * @return BMI
     */
    public Double calculateBMI() {
        return this.weight / ((this.height) * (this.height));
    }

    /**
     * Calculate the profiles age if they are alive and their age at death if they are dead
     * If the age is calculated on the users birthday they are the age they are turning that day
     * e.g. if it's your 20th birthday you are 20
     * @return profile age
     */
    public int calculateAge() {
        if (dateOfDeath == null) {
            return Period.between(dateOfBirth, LocalDate.now()).getYears();
        } else {
            return Period.between(dateOfBirth, dateOfDeath).getYears();
        }
    }

    public int getAge(){
        return calculateAge();
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

    public String getFullName() { return givenNames + " " + lastNames; }

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

    public Double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        generateUpdateInfo("height");
        this.height = height;
    }

    public Double getWeight() {
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

    public Boolean getSmoker() {
        return smoker;
    }

    public void setSmoker(Boolean smoker) {
        this.smoker = smoker;
    }

    public String getAlcoholConsumption() {
        return alcoholConsumption;
    }

    public void setAlcoholConsumption(String alcoholConsumption) {
        this.alcoholConsumption = alcoholConsumption;
    }

    public void setBloodPressureSystolic(Integer bloodPressureSystolic) {
        this.bloodPressureSystolic = bloodPressureSystolic;
    }

    public void setBloodPressureDiastolic(Integer bloodPressureDiastolic) {
        this.bloodPressureDiastolic = bloodPressureDiastolic;
    }

    public String getBloodPressure() {
        return bloodPressureSystolic.toString() + "/" + bloodPressureDiastolic;
    }

    public Set<String> getChronicDiseases() {
        return chronicDiseases;
    }


    public void setChronicDiseases(Set<String> chronicDiseases) {
        this.chronicDiseases = chronicDiseases;
    }

    public void setDonor(Boolean donor) {
        this.donor = donor;
    }

    public Boolean getDonor() {
        return donor;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}