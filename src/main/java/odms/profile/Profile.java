package odms.profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import odms.cli.CommandUtils;
import odms.controller.AlertController;
import odms.medications.Drug;

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
    private HashSet<String> chronicDiseases = new HashSet<>();

    private ArrayList<String> updateActions = new ArrayList<>();

    private ArrayList<Procedure> procedures = new ArrayList<>();

    private HashSet<Organ> organsDonating = new HashSet<>();
    private HashSet<Organ> organsDonated = new HashSet<>();
    private HashSet<Organ> organsRequired = new HashSet<>();
    private HashSet<Organ> organsReceived = new HashSet<>();

    private ArrayList<Condition> conditions = new ArrayList<>();

    private String phone;
    private String email;

    private Integer irdNumber;
    private LocalDateTime timeOfCreation;
    private LocalDateTime lastUpdated;

    private Integer id;

    private ArrayList<Drug> currentMedications;
    private ArrayList<Drug> historyOfMedication;
    private ArrayList<String> medicationTimestamps;

    /**
     * Instantiates the Profile class with data from the CLI
     * @param attributes the list of attributes in attribute="value" form
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong
     */
    public Profile(ArrayList<String> attributes) throws IllegalArgumentException {
        setExtraAttributes(attributes);
        currentMedications = new ArrayList<>();
        historyOfMedication = new ArrayList<>();
        medicationTimestamps = new ArrayList<>();
        procedures = new ArrayList<>();

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
        currentMedications = new ArrayList<>();
        historyOfMedication = new ArrayList<>();
        medicationTimestamps = new ArrayList<>();

        // Build an arraylist so I can reuse the
        ArrayList<String> attr = new ArrayList<>();
        attr.add("given-names=\"" + givenNames + "\"");
        attr.add("last-names=\"" + lastNames + "\"");
        attr.add("ird=\"" + irdNumber + "\"");
        attr.add("dob=\"" + dob + "\"");
        this.setReceiver(false); // TODO decide behaviour
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
            if(parts.length==1) {
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
        if(!parts[1].equals(null)) {
            value = parts[1].replace("\"", ""); // get rid of the speech marks;
        }

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
            if(value.equals("null") || value.equals("")) {
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
            if(value.equals("null")) {setBloodPressureSystolic(null);}
            else {
                setBloodPressureSystolic(Integer.valueOf(value));
            }
        }else if (attrName.equals("bloodPressureDiastolic")) {
            if(value.equals("null")) {setBloodPressureDiastolic(null);}
            else {
                setBloodPressureDiastolic(Integer.valueOf(value));
            }
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
     * Add a procedure to the current profile
     * @param procedure
     */
    public void addProcedure(Procedure procedure) {
        if (procedures == null) {
            procedures = new ArrayList<>();
        }
        procedures.add(procedure); }

    /**
     * Remove a procedure from the current profile
     * @param procedure
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
     * @param procedure
     * @return whether the procedure has past
     */
    public boolean isPreviousProcedure(Procedure procedure) {
        if (procedure.getDate().isBefore(LocalDate.now())) {
            return true;
        } else {
            return false;
        }
    }

    // TODO abstract printing method to console tools
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
     * Add a comma delimited organ string to the Organs Donating
     * @param organString the Comma delimited organ string
     * @throws OrganConflictException if there is a conflicting organ
     */
    public void addOrgansDonatingFromString(String organString) throws OrganConflictException {
        String[] organStrings = organString.split("(,\\s+|,)");

        for (String organ : organStrings) {
            organsDonating.add(Organ.valueOf(organ));
        }

        addOrgansDonating(organsDonating);
    }

    /**
     * Adds a csv list to the list of donations
     * @param organString the organs to add as a csv
     */
    public void addDonationFromString(String organString) {
        String[] organStrings = organString.split("(,\\s+|,)");
        this.addOrgansDonated(Organ.stringListToOrganSet(Arrays.asList(organStrings)));
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
     * Add an organ to the organs donate list.
     * @param organ the organ the profile wishes to donate
     */
    public void addOrgan(Organ organ) throws OrganConflictException {
        if (this.organsRequired.contains(organ)) {
            throw new OrganConflictException(
                    "Profile is currently receiver for " + organ,
                    organ
            );
        }
        this.organsDonating.add(organ);
    }

    /**
     * Add an organ to the organs required list.
     * @param organ the organ the profile requires
     * @throws OrganConflictException if there is a conflicting organ
     */
    public void addOrganRequired(Organ organ) throws OrganConflictException {
        if (this.organsDonating.contains(organ)) {
            throw new OrganConflictException(
                    "Profile is currently donor for  " + organ,
                    organ
            );
        }
        this.organsRequired.add(organ);
    }

    /**
     * Consume a set of organs that the profile wants to receive and updates the profile to use this
     * new set.
     * @param organs the set of organs to be received
     */
    public void setOrgansRequired(HashSet<Organ> organs) {
        generateUpdateInfo("organsRequired");

        try {
            this.organsRequired.clear();
            for (Organ organ : organs) {
                addOrganRequired(organ);

                // TODO history refactor
                String action = "Profile " +
                        this.getId() +
                        " required organ " +
                        organ.getNamePlain() +
                        " at " +
                        LocalDateTime.now();
                if (CommandUtils.getHistory().size() != 0) {
                    if (CommandUtils.getPosition() != CommandUtils.getHistory().size() - 1) {
                        CommandUtils.currentSessionHistory.subList(CommandUtils.getPosition(),
                                CommandUtils.getHistory().size() - 1).clear();
                    }
                }
                CommandUtils.currentSessionHistory.add(action);
                CommandUtils.historyPosition = CommandUtils.currentSessionHistory.size() - 1;
            }
        } catch (OrganConflictException e) {
            AlertController.invalidOrgan();
        }
    }

    /**
     * Add a set of organs to the list of organs that the profile wants to donate
     * @param organs the set of organs to donate
     * @throws IllegalArgumentException if a bad argument is used
     * @throws OrganConflictException if there is a conflicting organ
     */
    public void addOrgansDonating(Set<Organ> organs)
            throws IllegalArgumentException, OrganConflictException {
        // TODO shouldn't be checking for args here
        generateUpdateInfo("organsDonated"); // TODO should this be Organs Donating

        for (Organ organ : organs) {
            if (this.organsDonating.contains(organ)) {
                throw new IllegalArgumentException(
                        "Organ " + organ + " already exists in donating list"
                );
            }
            this.addOrgan(organ);

            String action = "Profile " +
                    this.getId() +
                    " added " +
                    organ +
                    " to donate at " +
                    LocalDateTime.now();

            // TODO abstract history

            if (CommandUtils.getHistory().size() != 0) {
                if (CommandUtils.getPosition()
                        != CommandUtils.getHistory().size() - 1) {
                    CommandUtils.currentSessionHistory
                            .subList(CommandUtils.getPosition(),
                                    CommandUtils.getHistory().size() - 1).clear();
                }
            }
            CommandUtils.currentSessionHistory.add(action);
            CommandUtils.historyPosition = CommandUtils.currentSessionHistory.size() - 1;
        }
    }

    public HashSet<Organ> getOrgansRequired() {
        return organsRequired;
    }

    /**
     * Add an organ to the set of received organs.
     * If the organ exists in the receiving set, remove it.
     * @param organ to be added
     */
    public void addOrganReceived(Organ organ) {
        if (this.organsRequired.contains(organ)) {
            this.organsRequired.remove(organ);
        }

        this.organsReceived.add(organ);
    }

    /**
     * Add a set of organs to the set of received organs.
     * @param organs set to be added
     */
    public void addOrgansReceived(Set<Organ> organs) {
        generateUpdateInfo("organsReceived");

        for (Organ organ : organs) {
            addOrganReceived(organ);

            // TODO history abstraction
            String action = "Profile " +
                    this.getId() +
                    " added " +
                    organ.getNamePlain() +
                    " to received organs " +
                    LocalDateTime.now();

            if (CommandUtils.getHistory().size() != 0) {
                if (CommandUtils.getPosition() != CommandUtils.getHistory().size() - 1) {
                    CommandUtils.currentSessionHistory.subList(CommandUtils.getPosition(),
                            CommandUtils.getHistory().size() - 1).clear();
                }
            }
            CommandUtils.currentSessionHistory.add(action);
            CommandUtils.historyPosition = CommandUtils.currentSessionHistory.size() - 1;
        }
    }

    public HashSet<Organ> getOrgansReceived() {
        return organsReceived;
    }

    /**
     * Add an organ to the list of donated organsDonating.
     * If the organ exists in the donating list, remove it from the donating list.
     * @param organ the organ to be added
     */
    public void addOrganDonated(Organ organ) {
        if (this.organsDonating.contains(organ)) {
            this.organsDonating.remove(organ);
        }

        this.organsDonated.add(organ);
    }

    /**
     * Add a set of organsDonating to the list of organsDonating that the profile has donated
     * @param organs a set of organsDonating that the profile has donated
     */
    public void addOrgansDonated(Set<Organ> organs) {
        generateUpdateInfo("pastDonations");

        for (Organ organ : organs) {
            this.organsDonated.add(organ);

            // TODO history abstraction
            String action = "Profile " +
                    this.getId() +
                    " added " +
                    organ.getNamePlain() +
                    " to past donations " +
                    LocalDateTime.now();

            if (CommandUtils.getHistory().size() != 0) {
                if (CommandUtils.getPosition() != CommandUtils.getHistory().size() - 1) {
                    CommandUtils.currentSessionHistory.subList(CommandUtils.getPosition(),
                            CommandUtils.getHistory().size() - 1).clear();
                }
            }
            CommandUtils.currentSessionHistory.add(action);
            CommandUtils.historyPosition = CommandUtils.currentSessionHistory.size() - 1;
        }
    }

    /**
     * Remove a set of organs from the list of organs that the profile has donated
     * @param organs a set of organs to remove from the list
     */
    public void removeDonations(Set<String> organs) {
        generateUpdateInfo("organsDonated");
        for (String org : organs) {
            String newOrgan = org.trim().toUpperCase();
            Organ organ = Organ.valueOf(newOrgan);
            this.organsDonated.remove(organ);
        }
    }

    /**
     * Remove a set of organs from the list of organs that the use wants to donate
     * @param organs a set of organs to be removed
     */
    public void removeOrgans(Set<String> organs) throws IllegalArgumentException {
        generateUpdateInfo("organsDonating");

        HashSet<Organ> newOrgans = new HashSet<>();

        for (String org : organs) {
            String newOrgan = org.trim().toUpperCase();
            Organ organ = Organ.valueOf(newOrgan);
            newOrgans.add(organ);
        }

        if (!Collections.disjoint(newOrgans, this.organsDonating)) {
            this.organsDonating.removeAll(newOrgans);
        } else {
            throw new IllegalArgumentException();
        }
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
        String data = drug.getDrugName() + " added on " + currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
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
        if(currentMedications.contains(drug)){
            currentMedications.remove(drug);
            medicationTimestamps.add(drug.getDrugName() + " removed on " + currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            generateUpdateInfo(drug.getDrugName());
        } else if(historyOfMedication.contains(drug)){
            historyOfMedication.remove(drug);
            medicationTimestamps.add(drug.getDrugName() + " removed on " + currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            generateUpdateInfo(drug.getDrugName());
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
        if(currentMedications.contains(drug)){
            currentMedications.remove(drug);
            historyOfMedication.add(drug);
            medicationTimestamps.add(drug.getDrugName() + " stopped on " + currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
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
        if(historyOfMedication.contains(drug)){
            historyOfMedication.remove(drug);
            currentMedications.add(drug);
            medicationTimestamps.add(drug.getDrugName() + " added back to current list on " + currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
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

    public HashSet<Organ> getOrgansDonated() {
        return organsDonated;
    }

    public HashSet<Organ> getOrgansDonating() {
        return organsDonating;
    }


    // Condition functions

    /**
     * Gets all the current conditions of the user
     * @return the conditions of the user
     */
    public ArrayList<Condition> getAllConditions() { return conditions; }

    /**
     * Gets all the cured conditions of the user
     * @return the cured conditions of the user
     */
    public ArrayList<Condition> getCuredConditions() {
        ArrayList<Condition> curedConditions = new ArrayList<>();
        try {
            for (Condition condition : conditions) {
                if (condition.getCured()) {
                    curedConditions.add(condition);
                }
            }
        } catch (NullPointerException e) {
            return null;
        }
        return curedConditions;
    }

    /**
     * Gets all the current conditions of the user
     * @return the current conditions of the user
     */
    public ArrayList<Condition> getCurrentConditions() {
        ArrayList<Condition> currentConditions = new ArrayList<>();
        try {
            for (Condition condition : conditions) {
                if (!condition.getCured()) {
                    currentConditions.add(condition);
                }
            }
        } catch (NullPointerException e) {
            return null;
        }
        return currentConditions;
    }

    /**
     * adds a condition from the user
     * @param condition to be added
     */
    public void addCondition(Condition condition) {
        if (conditions == null) { conditions = new ArrayList<>(); }
        conditions.add(condition);
    }

    /**
     * removes a condition from the user
     * @param condition to be removed
     */
    public void removeCondition(Condition condition) {
        conditions.remove(condition);
    }
    // -------

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
        if(bloodType != null) {
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

    public Integer getIrdNumber() {
        return irdNumber;
    }

    public void setIrdNumber(Integer irdNumber) {
        generateUpdateInfo("ird");
        this.irdNumber = irdNumber;
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

    /**
     * Returns a string formatted Systolic / Diastolic.
     *
     * @return blood pressure string
     */
    public String getBloodPressure() {
        return bloodPressureSystolic.toString() + "/" + bloodPressureDiastolic;
    }

    public HashSet<String> getChronicDiseases() {
        return chronicDiseases;
    }

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

}