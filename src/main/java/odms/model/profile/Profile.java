package odms.model.profile;

import java.time.Period;
import odms.controller.profile.ProfileGeneralControllerTODOContainsOldProfileMethods;
import odms.model.enums.OrganEnum;
import odms.model.medications.Drug;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;

public class Profile implements Comparable<Profile> {
    //todo tidy-up

    private Boolean donor = false;
    private Boolean receiver = false;

    private String givenNames;
    private String lastNames;
    private String preferredName;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private String gender;
    private String preferredGender;
    private Double height = 0.0;
    private Double weight = 0.0;
    private String bloodType;
    private String address;
    private String region;

    private Boolean isSmoker;
    private String alcoholConsumption;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private HashSet<String> chronicDiseases = new HashSet<>();

    private ArrayList<String> updateActions = new ArrayList<>();

    private ArrayList<Procedure> procedures = new ArrayList<>();

    private HashSet<OrganEnum> organsDonating = new HashSet<>();
    private HashSet<OrganEnum> organsDonated = new HashSet<>();
    private HashSet<OrganEnum> organsRequired = new HashSet<>();
    private HashSet<OrganEnum> organsReceived = new HashSet<>();

    private ArrayList<Condition> conditions = new ArrayList<>();

    private String phone;
    private String email;

    private Integer irdNumber;
    private LocalDateTime timeOfCreation;
    private LocalDateTime lastUpdated;

    private Integer id;

    private ArrayList<Drug> currentMedications = new ArrayList<>();
    private ArrayList<Drug> historyOfMedication = new ArrayList<>();
    private ArrayList<String> medicationTimestamps = new ArrayList<>();

    /**
     * Instantiates the profile class with data from the CLI
     *
     * @param attributes the list of attributes in attribute="value" form
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong
     */
    public Profile(ArrayList<String> attributes) throws IllegalArgumentException {
        //todo Change how setExtraAttributes works
        ProfileGeneralControllerTODOContainsOldProfileMethods.setExtraAttributes(attributes, this);
        procedures = new ArrayList<>();

        if (getGivenNames() == null || getLastNames() == null || getDateOfBirth() == null
                || getIrdNumber() == null) {
            throw new IllegalArgumentException();
        }
        timeOfCreation = LocalDateTime.now();
    }

    /**
     * Instantiates the basic profile class with a raw input of values
     *
     * @param givenNames profile's given names as String
     * @param lastNames  profile's last names as String
     * @param dob        profile's date of birth as a string
     * @param irdNumber  profile's IRD number as Integer
     */
    public Profile(String givenNames, String lastNames, String dob, Integer irdNumber) {

        // Build an ArrayList so I can reuse the
        ArrayList<String> attr = new ArrayList<>();
        attr.add("given-names=\"" + givenNames + "\"");
        attr.add("last-names=\"" + lastNames + "\"");
        attr.add("ird=\"" + irdNumber + "\"");
        attr.add("dob=\"" + dob + "\"");
        this.setReceiver(false);
        //todo Change how setExtraAttributes works
        ProfileGeneralControllerTODOContainsOldProfileMethods.setExtraAttributes(attr, this);

        if (getGivenNames() == null ||
                getLastNames() == null ||
                getDateOfBirth() == null ||
                getIrdNumber() == null) {
            throw new IllegalArgumentException();
        }
        timeOfCreation = LocalDateTime.now();
    }

    public Profile(String givenNames, String lastNames, LocalDate dob, Integer irdNumber) {
        this(
                givenNames,
                lastNames,
                dob.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                irdNumber
        );
    }

    /**
     * Gets all of the profiles procedures
     *
     * @return all procedures
     */
    public ArrayList<Procedure> getAllProcedures() {
        return procedures;
    }

    // TODO abstract printing method to console tools
    public String getAttributesSummary() {
        String summary = "";
        summary = summary + ("ird=" + irdNumber);
        summary = summary + "," + ("given-names=" + givenNames);
        summary = summary + "," + ("last-names=" + lastNames);
        summary = summary + "," + ("dob=" + dateOfBirth
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        if (dateOfDeath == null) {
            summary = summary + "," + ("dod=" + null);
        } else {
            summary = summary + "," + ("dod=" + dateOfDeath
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        summary = summary + "," + ("gender=" + gender);
        summary = summary + "," + ("height=" + height);
        summary = summary + "," + ("weight=" + weight);
        summary = summary + "," + ("blood-type=" + bloodType);
        summary = summary + "," + ("address=" + address);
        summary = summary + "," + ("region=" + region);
        summary = summary + "," + ("isSmoker=" + isSmoker);
        summary = summary + "," + ("alcoholConsumption=" + alcoholConsumption);
        summary = summary + "," + ("bloodPressureSystolic=" + bloodPressureSystolic);
        summary = summary + "," + ("bloodPressureDiastolic=" + bloodPressureDiastolic);
        summary = summary + "," + ("phone=" + phone);
        summary = summary + "," + ("email=" + email);
        return summary;
    }

    public HashSet<OrganEnum> getOrgansReceived() {
        return organsReceived;
    }

    public ArrayList<Drug> getCurrentMedications() {
        return currentMedications;
    }

    public ArrayList<Drug> getHistoryOfMedication() {
        return historyOfMedication;
    }

    public ArrayList<String> getMedicationTimestamps() {
        return medicationTimestamps;
    }

    public HashSet<OrganEnum> getOrgansDonated() {
        return organsDonated;
    }

    public HashSet<OrganEnum> getOrgansDonating() {
        return organsDonating;
    }

    /**
     * Gets all the current conditions of the user
     *
     * @return the conditions of the user
     */
    public ArrayList<Condition> getAllConditions() {
        return this.conditions;
    }

    /**
     * Returns a string formatted Systolic / Diastolic.
     *
     * @return blood pressure string
     */
    public String getBloodPressure() {
        if (bloodPressureDiastolic != null && bloodPressureSystolic != null) {
            return bloodPressureSystolic.toString() + "/" + bloodPressureDiastolic.toString();
        }
        return null;
    }

    public HashSet<String> getChronicDiseases() {
        return chronicDiseases;
    }

    public LocalDateTime getTimeOfCreation() {
        return this.timeOfCreation;
    }

    public String getGivenNames() {
        return this.givenNames;
    }

    public String getFullName() {
        return givenNames + " " + lastNames;
    }

    public String getLastNames() {
        return lastNames;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }


    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }


    public String getGender() {
        return gender;
    }


    public Double getHeight() {
        return height;
    }


    public Double getWeight() {
        return weight;
    }


    public String getBloodType() {
        return bloodType;
    }


    public String getAddress() {
        return address;
    }


    public String getRegion() {
        return region;
    }


    public Integer getIrdNumber() {
        return irdNumber;
    }


    public Boolean getDonor() {
        return donor;
    }

    public Boolean getReceiver() {
        return receiver;
    }

    public void setAllConditions(ArrayList<Condition> conditions) {
        this.conditions = conditions;
    }


    public void setGivenNames(String givenNames) {
        //todo refactor generateUpdateInfo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("given-names", this);
        this.givenNames = givenNames;
    }

    // condition functions


    public void setReceiver(boolean receiver) {
        this.receiver = receiver;
    }


    public void setLastNames(String lastNames) {
        //todo refactor generateUpdateInfo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("last-names", this);
        this.lastNames = lastNames;
    }


    public void setDateOfBirth(LocalDate dateOfBirth) {
        //todo refactor generateUpdateInfo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("dob", this);
        this.dateOfBirth = dateOfBirth;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        if (dateOfDeath != null && getDateOfBirth().isAfter(dateOfDeath)) {
            throw new IllegalArgumentException(
                    "Date of death cannot be before date of birth"
            );
        }
        //todo refactor generateUpdateInfo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("dod", this);
        this.dateOfDeath = dateOfDeath;
    }

    public void setGender(String gender) {
        //todo refactor generateUpdateInfo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("gender", this);
        this.gender = gender;
    }

    public void setHeight(double height) {
        //todo refactor generateUpdateInfo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("height", this);
        this.height = height;
    }

    public void setWeight(double weight) {
        //todo refactor generateUpdateInfo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("weight", this);
        this.weight = weight;
    }

    public void setBloodType(String bloodType) {
        if (bloodType != null) {
            //todo refactor generateUpdateInfo
            ProfileGeneralControllerTODOContainsOldProfileMethods
                    .generateUpdateInfo("blood-type", this);
            this.bloodType = bloodType;
        }
    }

    public void setAddress(String address) {
        //todo refactor generateUpdateInfo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("address", this);
        this.address = address;
    }

    public void setRegion(String region) {
        //todo refactor generateUpdateInfo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("region", this);
        this.region = region;
    }


    public void setIrdNumber(Integer irdNumber) {
        //todo refactor generateUpdateInfo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("ird", this);
        this.irdNumber = irdNumber;
    }

    public void setDonor(Boolean donor) {
        this.donor = donor;
    }


    public void setReceiver(Boolean receiver) {
        this.receiver = receiver;
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

    public Boolean getIsSmoker() {
        return isSmoker;
    }

    public void setIsSmoker(Boolean smoker) {
        this.isSmoker = smoker;
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


    // TODO access to this array should be restricted, this makes it public and redundant.
    public void setChronicDiseases(HashSet<String> chronicDiseases) {
        this.chronicDiseases = chronicDiseases;
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

    public String getPreferredGender() {
        return this.preferredGender;
    }

    public void setPreferredGender(String preferredGender) {
        this.preferredGender = preferredGender;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    /**
     * Checks if a profile is donating a certain selection of organs
     *
     * @param organs organs to be checked
     * @return true if they are
     */
    public boolean isDonatingCertainOrgans(HashSet<OrganEnum> organs) {
        return organsDonating.containsAll(organs);
    }

    /**
     * Checks if a profile is receiving a certain selection of organs
     *
     * @param organs organs to be checked
     * @return true if they are
     */
    public boolean isReceivingCertainOrgans(HashSet<OrganEnum> organs) {
        return organsRequired.containsAll(organs);
    }

    public void setLastUpdated() {
        LocalDateTime currentTime = LocalDateTime.now();
        lastUpdated = currentTime;
    }

    public HashSet<OrganEnum> getOrgansRequired() {
        return organsRequired;
    }

    @Override
    public int compareTo(Profile o) {
        return 0;
    }

    /**
     * Calculate the profiles age if they are alive and their age at death if they are dead
     * If the age is calculated on the users birthday they are the age they are turning that day
     * e.g. if it's your 20th birthday you are 20
     * @return profile age
     */
    public int getAge() {
        //todo should we store this?
        if (dateOfDeath == null) {
            return Period.between(dateOfBirth, LocalDate.now()).getYears();
        } else {
            return Period.between(dateOfBirth, dateOfDeath).getYears();
        }
    }

    /**
     * Calculates and returns the profiles bmi
     *
     * @return BMI
     */
    public Double getBMI() {
        return weight / Math.pow(height, 2);
    }

}
