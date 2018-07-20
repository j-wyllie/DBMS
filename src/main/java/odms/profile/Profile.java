package odms.profile;

import javafx.beans.property.SimpleStringProperty;
import odms.controller.HistoryController;
import odms.enums.OrganEnum;
import odms.history.History;
import odms.medications.Drug;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import odms.controller.HistoryController;
import odms.enums.OrganEnum;
import odms.history.History;
import odms.medications.Drug;

public class Profile implements Comparable<Profile> {

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

    private String streetNumber;
    private String streetName;
    private String city;
    private String region;
    private String zipCode;
    private String country;
    private String birthCountry;


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
    private String mobilePhone;
    private String email;

    private String nhi;
    private LocalDateTime timeOfCreation;
    private LocalDateTime lastUpdated;

    private Integer id;

    private ArrayList<Drug> currentMedications = new ArrayList<>();
    private ArrayList<Drug> historyOfMedication = new ArrayList<>();
    private ArrayList<String> medicationTimestamps = new ArrayList<>();

    /**
     * Instantiates the Profile class with data from the CLI
     * @param attributes the list of attributes in attribute="value" form
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong
     */
    public Profile(ArrayList<String> attributes) throws IllegalArgumentException {
        setExtraAttributes(attributes);
        procedures = new ArrayList<>();

        if (getGivenNames() == null || getLastNames() == null || getDateOfBirth() == null || getNhi() == null) {
            throw new IllegalArgumentException();
        }
        timeOfCreation = LocalDateTime.now();
    }

    /**
     * Instantiates the basic Profile class with a raw input of values
     * @param givenNames Profile's given names as String
     * @param lastNames Profile's last names as String
     * @param dob Profile's date of birth as a string
     * @param nhi Profile's NHI number as Integer
     */
    public Profile(String givenNames, String lastNames, String dob, String nhi) {

        // Build an ArrayList so I can reuse the
        ArrayList<String> attr = new ArrayList<>();
        attr.add("given-names=\"" + givenNames + "\"");
        attr.add("last-names=\"" + lastNames + "\"");
        attr.add("nhi=\"" + nhi + "\"");
        attr.add("dob=\"" + dob + "\"");
        this.setReceiver(false);
        setExtraAttributes(attr);

        if (getGivenNames() == null ||
                getLastNames() == null ||
                getDateOfBirth() == null ||
                getNhi() == null) {
            throw new IllegalArgumentException();
        }
        timeOfCreation = LocalDateTime.now();
    }

    public Profile(String givenNames, String lastNames, LocalDate dob, String nhi) {
        this(
                givenNames,
                lastNames,
                dob.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                nhi
        );
    }

    /**
     * Compares the profile object to another profile object. Result is determined by lexicographical order of profile
     * full name.
     * @param other another profile object to compare to.
     * @return int value to show if object is equal, greater than ore less than the other profile object.
     */
    public int compareTo(Profile other) {
        return getFullName().toLowerCase().compareTo(other.getFullName().toLowerCase());
    }

    /**
     * Sets the attributes that are passed into the constructor
     * @param attributes the attributes given in the constructor
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong
     */
    public void setExtraAttributes(ArrayList<String> attributes) throws IllegalArgumentException {
        for (String val : attributes) {
            String[] parts = val.split("=");
            if (parts.length==1) {
                String[] newParts = {parts[0], ""};
                setGivenAttribute(newParts);
            } else {
                setGivenAttribute(parts);
            }
        }
    }

    /**
     * Calls the relevant method to set the attribute
     * @param parts a list with an attribute and value
     * @throws IllegalArgumentException thrown when an attribute that isn't valid is given
     */
    private void setGivenAttribute(String[] parts) throws IllegalArgumentException {
        String attrName = parts[0];
        String value = null;
        if (!parts[1].equals(null)) {
            value = parts[1].replace("\"", ""); // get rid of the speech marks;
        }

        if (attrName.equals(Attribute.GIVENNAMES.getText())) {
            setGivenNames(value);
        } else if (attrName.equals(Attribute.LASTNAMES.getText())) {
            setLastNames(value);
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
            setDateOfBirth(date);
        } else if (attrName.equals(Attribute.DATEOFDEATH.getText())) {
            if (value.equals("null")) {
                setDateOfDeath(null);
            } else {
                String[] dates = value.split("-");
                LocalDate date = LocalDate.of(
                        Integer.valueOf(dates[2]),
                        Integer.valueOf(dates[1]),
                        Integer.valueOf(dates[0])
                );
                setDateOfDeath(date);
            }
        } else if (attrName.equals(Attribute.GENDER.getText())) {
            setGender(value.toLowerCase());
        } else if (attrName.equals(Attribute.HEIGHT.getText())) {
            try {
                if (value.equals("null")) {
                    value = "0";
                }
                setHeight(Double.valueOf(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid height entered");
            }
        } else if (attrName.equals(Attribute.WEIGHT.getText())) {
            try {
                if (value.equals("null")) {
                    value = "0";
                }
                setWeight(Double.valueOf(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid weight entered");
            }
        } else if (attrName.equals(Attribute.BLOODTYPE.getText())) {
            if (value.equals("null") || value.equals("")) {
                value = null;
            }
            setBloodType(value);
        } else if (attrName.equals(Attribute.ADDRESS.getText())) {
            setAddress(value);
        } else if (attrName.equals(Attribute.REGION.getText())) {
            setRegion(value);
        } else if (attrName.equals(Attribute.NHI.getText())) {
            try {
                setNhi(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid NHI number entered");
            }
        } else if (attrName.equals("isSmoker")) {
            setIsSmoker(Boolean.valueOf(value));
        } else if (attrName.equals("alcoholConsumption")) {
            setAlcoholConsumption(value);
        } else if (attrName.equals("bloodPressureSystolic")) {
            if (value.equals("null")) {
                setBloodPressureSystolic(null);
            } else {
                setBloodPressureSystolic(Integer.valueOf(value));
            }
        } else if (attrName.equals("bloodPressureDiastolic")) {
            if (value.equals("null")) {
                setBloodPressureDiastolic(null);
            } else {
                setBloodPressureDiastolic(Integer.valueOf(value));
            }
        } else if (attrName.equals("phone")) {
            setPhone(value);
        } else if (attrName.equals("email")) {
            setEmail(value);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Add a procedure to the current profile
     * @param procedure the procedure to add
     */
    public void addProcedure(Procedure procedure) {
        if (procedures == null) {
            procedures = new ArrayList<>();
        }
        procedures.add(procedure); }

    /**
     * Remove a procedure from the current profile
     * @param procedure the procedure to remove
     */
    public void removeProcedure(Procedure procedure) { procedures.remove(procedure); }

    /**
     * Gets all of the profiles procedures
     * @return all procedures
     */
    public ArrayList<Procedure> getAllProcedures() { return procedures; }

    /**
     * Gets all the previous procedures
     * @return previous procedures
     */
    public ArrayList<Procedure> getPreviousProcedures() {
        ArrayList<Procedure> prevProcedures = new ArrayList<>();
        if (procedures != null) {
            for (Procedure procedure : procedures) {
                if (procedure.getDate().isBefore(LocalDate.now())) {
                    prevProcedures.add(procedure);
                }
            }
        }
        return prevProcedures;
    }

    /**
     * Gets all the pending procedures
     * @return pending procedures
     */
    public ArrayList<Procedure> getPendingProcedures() {
        ArrayList<Procedure> pendingProcedures = new ArrayList<>();
        if (procedures != null) {
            for (Procedure procedure : procedures) {
                if (procedure.getDate().isAfter(LocalDate.now())) {
                    pendingProcedures.add(procedure);
                }
            }
        }
        return pendingProcedures;
    }

    /**
     * Given a procedure, will return whether the procedure has past
     * @param procedure the procedure to check
     * @return whether the procedure has past
     */
    public boolean isPreviousProcedure(Procedure procedure) {
        return procedure.getDate().isBefore(LocalDate.now());
    }

    // TODO abstract printing method to console tools
    public String getAttributesSummary() {
        String summary = "";
        summary = summary +("nhi=" + nhi);
        summary = summary +"," +("given-names=" + givenNames);
        summary = summary +"," +("last-names=" + lastNames);
        summary = summary +"," +("dob=" + dateOfBirth.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        if (dateOfDeath==null) { summary = summary +"," +("dod=" + null); }
        else{summary = summary +"," +("dod=" + dateOfDeath.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));}
        summary = summary +"," +("gender=" + gender);
        summary = summary +"," +("height=" + height);
        summary = summary +"," +("weight=" + weight);
        summary = summary +"," +("blood-type=" + bloodType);
        summary = summary +"," +("address=" + address);
        summary = summary +"," +("region=" + region);
        summary = summary +"," +("isSmoker=" + isSmoker);
        summary = summary +"," +("alcoholConsumption=" + alcoholConsumption);
        summary = summary +"," +("bloodPressureSystolic=" + bloodPressureSystolic);
        summary = summary +"," +("bloodPressureDiastolic=" + bloodPressureDiastolic);
        summary = summary +"," +("phone=" + phone);
        summary = summary +"," +("email=" + email);
        return summary;
    }

    /**
     * Add an organ to the organs donate list.
     * @param organ the organ the profile wishes to donate
     */
    public void addOrganDonating(OrganEnum organ) throws OrganConflictException {
        if (this.organsReceived.contains(organ)) {
            // A donor cannot donate an organ they've received.
            throw new OrganConflictException(
                    "Profile has previously received " + organ,
                    organ
            );
        }
        this.organsDonating.add(organ);
    }

    /**
     * Add an organ to the organs required list.
     * @param organ the organ the profile requires
     */
    public void addOrganRequired(OrganEnum organ) {//TODO Error Check
        this.setReceiver(true);
        this.organsRequired.add(organ);
    }

    /**
     * Add a set of organs that the profile requires to the required organs set.
     * @param organs the set of organs to be received
     */
    public void addOrgansRequired(HashSet<OrganEnum> organs) {
        generateUpdateInfo("organsRequired");

        for (OrganEnum organ : organs) {
            addOrganRequired(organ);
            LocalDateTime now = LocalDateTime.now();
            History action = new History("Profile", this.getId(),"required organ",
                    ""+organ.getNamePlain(),-1,now);
            HistoryController.updateHistory(action);
            }
    }

    /**
     * Add a set of organs to the list of organs that the profile wants to donate
     * @param organs the set of organs to donate
     * @throws IllegalArgumentException if a bad argument is used
     * @throws OrganConflictException if there is a conflicting organ
     */
    public void addOrgansDonating(Set<OrganEnum> organs)
            throws IllegalArgumentException, OrganConflictException {
        generateUpdateInfo("organsDonating");

        for (OrganEnum organ : organs) {
            if (this.organsDonating.contains(organ)) {
                throw new IllegalArgumentException(
                        "Organ " + organ + " already exists in donating list"
                );
            }
            this.addOrganDonating(organ);

            History action = new History("Profile ", this.getId(),"set",organ.getNamePlain(),
                    -1,LocalDateTime.now());
            HistoryController.updateHistory(action);
        }
    }

    public HashSet<OrganEnum> getOrgansRequired() {
        return organsRequired;
    }

    /**
     * Add an organ to the set of received organs.
     * If the organ exists in the receiving set, remove it.
     * @param organ to be added
     */
    private void addOrganReceived(OrganEnum organ) {
        if (this.organsRequired.contains(organ)) {
            this.organsRequired.remove(organ);
        }

        this.organsReceived.add(organ);
    }

    /**
     * Add a set of organs to the set of received organs.
     * @param organs set to be added
     */
    public void addOrgansReceived(Set<OrganEnum> organs) {
        generateUpdateInfo("organsReceived");

        for (OrganEnum organ : organs) {
            addOrganReceived(organ);History action = new History("Profile ", this.getId(),
                    "received",organ.getNamePlain(),-1,LocalDateTime.now());
            HistoryController.updateHistory(action);
        }
    }

    public HashSet<OrganEnum> getOrgansReceived() {
        return organsReceived;
    }

    /**
     * Add an organ to the list of donated organsDonating.
     * If the organ exists in the donating list, remove it from the donating list.
     * @param organ the organ to be added
     */
    public void addOrganDonated(OrganEnum organ) {
        if (this.organsDonating.contains(organ)) {
            this.organsDonating.remove(organ);
        }

        this.organsDonated.add(organ);
    }

    /**
     * Add a set of organsDonating to the list of organsDonating that the profile has donated
     * @param organs a set of organsDonating that the profile has donated
     */
    public void addOrgansDonated(Set<OrganEnum> organs) {
        generateUpdateInfo("pastDonations");

        for (OrganEnum organ : organs) {
            this.organsDonated.add(organ);
            History action = new History(
                    "Profile ",
                    this.getId(),
                    "donated",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );
            HistoryController.updateHistory(action);
        }
    }

    /**
     * Remove a set of organs from the list of organs that the profile has donated
     * @param organs a set of organs to remove from the list
     */
    public void removeOrgansDonated(Set<OrganEnum> organs) {
        generateUpdateInfo("organsDonated");

        for (OrganEnum organ : organs) {
            this.organsDonated.remove(organ);
            History action = new History(
                    "Profile ",
                    this.getId(),
                    "removed donated",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );
            HistoryController.updateHistory(action);
        }
    }

    /**
     * Remove a set of organs from the list of organs that the use wants to donate
     * @param organs a set of organs to be removed
     */
    public void removeOrgansDonating(Set<OrganEnum> organs) {
        generateUpdateInfo("organsDonating");

        for (OrganEnum organ : organs) {
            this.organsDonating.remove(organ);
            History action = new History(
                    "Profile ",
                    this.getId(),
                    "removed",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );
            HistoryController.updateHistory(action);
        }
    }

    /**
     * Remove a set of organs from the list of organs required.
     * @param organs a set of organs to be removed
     */
    public void removeOrgansRequired(Set<OrganEnum> organs) {
        generateUpdateInfo("organsReceiving");

        for (OrganEnum organ : organs) {
            this.organsRequired.remove(organ);
            History action = new History(
                    "Profile ",
                    this.getId(),
                    "removed required",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );

            HistoryController.updateHistory(action);
        }
    }

    public void removeOrganReceived(OrganEnum organ) {
        if (this.organsReceived.contains(organ)) {
            this.organsReceived.remove(organ);
        }

        this.organsRequired.add(organ);
    }

    public void removeOrganDonated(OrganEnum organ) {
        if (this.organsDonated.contains(organ)) {
            this.organsDonated.remove(organ);
        }

        this.organsDonating.add(organ);
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

    /**
     * adds a drug to the list of current medications a donor is on.
     * @param drug the drug to be added
     */
    public void addDrug(Drug drug){
        if (currentMedications == null) { currentMedications = new ArrayList<>(); }
        if (medicationTimestamps == null) { medicationTimestamps = new ArrayList<>(); }
        if (historyOfMedication == null) { historyOfMedication = new ArrayList<>(); }

        LocalDateTime currentTime = LocalDateTime.now();
        currentMedications.add(drug);
        String data ="Profile " +
                this.getId() +
                " added drug " +
                drug.getDrugName() +
                " index of " +
                currentMedications.indexOf(drug) +
                " at " +
                currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        medicationTimestamps.add(data);
        generateUpdateInfo(drug.getDrugName());
    }

    /**
     * deletes a drug from the list of current medications if it was added by accident.
     * @param drug the drug to be deleted.
     */
    public void deleteDrug(Drug drug) {
        if (currentMedications == null) { currentMedications = new ArrayList<>(); }
        if (medicationTimestamps == null) { medicationTimestamps = new ArrayList<>(); }
        if (historyOfMedication == null) { historyOfMedication = new ArrayList<>(); }

        LocalDateTime currentTime = LocalDateTime.now();
        String data = "Profile " +
                this.getId() +
                " removed drug " +
                drug.getDrugName() +
                " index of "+
                currentMedications.indexOf(drug) +
                " at " +
                currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        if (currentMedications.contains(drug)) {
            currentMedications.remove(drug);
            medicationTimestamps.add(data);
            generateUpdateInfo(drug.getDrugName());
        } else if (historyOfMedication.contains(drug)) {
            historyOfMedication.remove(drug);
            data = "Profile " +
                    this.getId() +
                    " removed drug from history"  +
                    " index of " +
                    currentMedications.indexOf(drug) +
                    " at " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            medicationTimestamps.add(data);
        }
    }

    /**
     * Moves the drug to the history of drugs the donor has taken.
     * @param drug the drug to be moved to the history
     */
    public void moveDrugToHistory(Drug drug){
        if (currentMedications == null) { currentMedications = new ArrayList<>(); }
        if (medicationTimestamps == null) { medicationTimestamps = new ArrayList<>(); }
        if (historyOfMedication == null) { historyOfMedication = new ArrayList<>(); }

        LocalDateTime currentTime = LocalDateTime.now();
        if (currentMedications.contains(drug)) {
            currentMedications.remove(drug);
            historyOfMedication.add(drug);
            String data = "Profile " +
                    this.getId() +
                    " stopped " +
                    drug.getDrugName() +
                    " index of "+
                    historyOfMedication.indexOf(drug) +
                    " at " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            medicationTimestamps.add(data);
            generateUpdateInfo(drug.getDrugName());
        }



    }

    /**
     * Moves the drug to the list of current drugs the donor is taking.
     * @param drug the drug to be moved to the current drug list
     */
    public void moveDrugToCurrent(Drug drug){
        if (currentMedications == null) { currentMedications = new ArrayList<>(); }
        if (medicationTimestamps == null) { medicationTimestamps = new ArrayList<>(); }
        if (historyOfMedication == null) { historyOfMedication = new ArrayList<>(); }

        LocalDateTime currentTime = LocalDateTime.now();
        if (historyOfMedication.contains(drug)) {
            historyOfMedication.remove(drug);
            currentMedications.add(drug);
            String data = "Profile " +
                    this.getId()  +
                    " started using " +
                    drug.getDrugName() +
                    " index of " +
                    currentMedications.indexOf(drug) +
                    " again at " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            medicationTimestamps.add(data);
            generateUpdateInfo(drug.getDrugName());
        }

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

    // Condition functions

    /**
     * Gets all the current conditions of the user
     * @return the conditions of the user
     */
    public ArrayList<Condition> getAllConditions() {
        return this.conditions;
    }

    /**
     * Gets all the cured conditions of the user
     * @return the cured conditions of the user
     */
    public ArrayList<Condition> getCuredConditions() {
        ArrayList<Condition> curedConditions = new ArrayList<>();
        for (Condition condition : this.conditions) {
            if (condition.getCured()) {
                curedConditions.add(condition);
            }
        }

        return curedConditions;
    }

    /**
     * Gets all the current conditions of the user
     * @return the current conditions of the user
     */
    public ArrayList<Condition> getCurrentConditions() {
        ArrayList<Condition> currentConditions = new ArrayList<>();
        for (Condition condition : this.conditions) {
            if (!condition.getCured()) {
                currentConditions.add(condition);
            }
        }
        return currentConditions;
    }

    /**
     * Checks if a profile is donating a certain selection of organs
     * @param organs organs to be checked
     * @return true if they are
     */
    public boolean isDonatingCertainOrgans(HashSet<OrganEnum> organs) {
        return organsDonating.containsAll(organs);
    }

    /**
     * Checks if a profile is receiving a certain selection of organs
     * @param organs organs to be checked
     * @return true if they are
     */
    public boolean isReceivingCertainOrgans(HashSet<OrganEnum> organs) {
        return organsRequired.containsAll(organs);
    }

    /**
     * adds a condition from the user
     * @param condition to be added
     */
    public void addCondition(Condition condition) {
        this.conditions.add(condition);
    }

    /**
     * removes a condition from the user
     * @param condition to be removed
     */
    public void removeCondition(Condition condition) {
        this.conditions.remove(condition);
    }

    /**
     * Returns the string value to populate the Donor/Receiver column in the clinician search table.
     * @return a string depicting whether to profile is a donor, receiver, or both.
     */
    public SimpleStringProperty donorReceiverProperty() {
        SimpleStringProperty result = new SimpleStringProperty();
        if (!(donor == null) && donor) {
            if (!(receiver == null) && receiver) {
                result.setValue("Donor/Receiver");
            }
            else {
                result.setValue("Donor");
            }
        }
        else if (!(receiver == null) && receiver) {
            result.setValue("Receiver");

        }
        return result;
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

    // This method is used to populate a column in the search table
    public String getFullPreferredName() {
        if (preferredName == null || preferredName.equals("")) {
            return givenNames + " " + lastNames;
        } else {
            return givenNames + " \"" + preferredName + "\" " + lastNames;
        }
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
        if (dateOfDeath != null && getDateOfBirth().isAfter(dateOfDeath)) {
            throw new IllegalArgumentException(
                "Date of death cannot be before date of birth"
            );
        }
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
        if (bloodType != null) {
            generateUpdateInfo("blood-type");
            this.bloodType = bloodType;
        }
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

    public Boolean getDonor() {
        return donor;
    }

    public void setDonor(Boolean donor) {
        this.donor = donor;
    }

    public Boolean getReceiver() {
        return receiver;
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

    public void setAllConditions(ArrayList<Condition> conditions) {
        this.conditions = conditions;
    }

    public void setPreferredGender(String preferredGender) {
        this.preferredGender = preferredGender;
    }

    public String getPreferredGender() {
        return this.preferredGender;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getNhi() {
        return nhi;
    }

    public void setNhi(String nhi) {
        generateUpdateInfo("nhi");
        this.nhi = nhi;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
}
