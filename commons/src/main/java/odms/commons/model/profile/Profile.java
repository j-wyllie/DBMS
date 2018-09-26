package odms.commons.model.profile;

import odms.commons.model.enums.BloodTypeEnum;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.history.CurrentHistory;
import odms.commons.model.history.History;
import odms.commons.model.medications.Drug;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The profile model class.
 */
public class Profile implements Comparable<Profile> {
    private static final String PROFILE_STRING = "profile ";
    private static final int HEIGHT_TO_METERS = 100;
    private static final String QUOTE = "\"";
    private static final String COMMA = ",";
    private static final String HYPHEN = "-";
    private static final String NULL_STRING = "null";
    private static final String SPACE = " ";
    private static final String DOD_STRING = "dod=";
    private static final String ZERO_STRING = "0";
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private List<String> regionsNZ = Arrays
            .asList("Northland", "Auckland", "Waikato", "Bay of Plenty", "Gisborne", "Hawke's Bay",
                    "Taranaki", "Manawatu-Wanganui", "Wellington", "Tasman", "Nelson",
                    "Marlborough", "West Coast", "Canterbury", "Otago", "Southland");

    private Integer id;
    private String nhi;
    private String username;

    private Boolean donor = false;
    private Boolean receiver = false;

    private String givenNames;
    private String lastNames;
    private String preferredName;
    private LocalDate dateOfBirth;
    private LocalDateTime dateOfDeath;
    private String gender;
    private String preferredGender;
    private Double height = 0.0;
    private Double weight = 0.0;
    private String bloodType;

    private String address;

    private CountriesEnum countryOfDeath;
    private String regionOfDeath;
    private String cityOfDeath;

    private String streetNumber;
    private String streetName;
    private String neighbourhood;
    private String city;
    private String region;
    private String zipCode;
    private CountriesEnum country;
    private CountriesEnum birthCountry;

    private Boolean isSmoker;
    private String alcoholConsumption;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;

    private List<String> updateActions = new ArrayList<>();

    private List<Procedure> procedures = new ArrayList<>();

    private List<Procedure> pendingProcedures = new ArrayList<>();
    private List<Procedure> previousProcedures = new ArrayList<>();

    private Set<OrganEnum> organsDonating = new HashSet<>();
    private Set<OrganEnum> organsDonated = new HashSet<>();
    private Set<OrganEnum> organsRequired = new HashSet<>();
    private Set<OrganEnum> organsReceived = new HashSet<>();
    private Set<OrganEnum> organsExpired = new HashSet<>();
    private Set<Organ> organTimeStamps = new HashSet<>();

    private List<Condition> conditions = new ArrayList<>();

    private String phone;
    private String mobilePhone;
    private String email;
    private String pictureName;

    private LocalDateTime timeOfCreation;
    private LocalDateTime lastUpdated;
    private LocalDateTime lastBloodDonation;

    private int bloodDonationPoints = 0;

    private List<Drug> currentMedications = new ArrayList<>();
    private List<Drug> historyOfMedication = new ArrayList<>();
    private List<String> medicationTimestamps = new ArrayList<>();

    /**
     * Instantiates the profile class with data from the CLI.
     *
     * @param attributes the list of attributes in attribute="value" form
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong
     */
    public Profile(List<String> attributes) {
        setExtraAttributes(attributes);
        procedures = new ArrayList<>();

        if (getGivenNames() == null || getLastNames() == null ||
                getDateOfBirth() == null || getNhi() == null) {
            throw new IllegalArgumentException();
        }
        timeOfCreation = LocalDateTime.now();
    }

    /**
     * Instantiates the basic Profile class with a raw input of values.
     *
     * @param givenNames Profile's given names as String
     * @param lastNames Profile's last names as String
     * @param dob Profile's date of birth as a string
     * @param nhi Profile's NHI number as Integer
     */
    public Profile(String givenNames, String lastNames, String dob, String nhi) {

        // Build an ArrayList so I can reuse the
        ArrayList<String> attr = new ArrayList<>();
        attr.add("given-names=\"" + givenNames + QUOTE);
        attr.add("last-names=\"" + lastNames + QUOTE);
        attr.add("nhi=\"" + nhi + QUOTE);
        attr.add("dob=\"" + dob + QUOTE);
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

    /**
     * Instantiates a basic profile object.
     * @param givenNames the given name
     * @param lastNames the last name
     * @param dob the date of birth
     * @param nhi their nhi
     */
    public Profile(String givenNames, String lastNames, LocalDate dob, String nhi) {
        this(
                null, // id
                nhi, // nhi
                nhi, // username
                false, // isDonor
                false, // isReceiver
                givenNames, // givenNames
                lastNames, // lastNames
                dob, // dob
                null, // dod
                null, // gender
                0.0, // height
                0.0, // weight
                null, // bloodType
                null, // isSmoker
                null, // alcoholConsumption
                0, // bpSystolic
                0, // bpDiastolic
                null, // address
                null, // region
                null, // phone
                null, // email
                null, // country
                null, // city
                null, // countryOfDeath
                null, // regionOfDeath
                null, // cityOfDeath
                null, // created
                null, // updated
                null, // preferredName
                null, // preferredGender
                null, // imageName
                LocalDateTime.now().minusYears(100), // LastBloodDonation
                0 //bloodDonationPoints
        );
    }

    /**
     * Instantiates the profile object with every field.
     * @param id the id
     * @param nhi the nhi
     * @param username their username
     * @param isDonor true if they are a donor
     * @param isReceiver true if they are a receiver
     * @param givenNames their given name
     * @param lastNames their last name
     * @param dob their date of birth
     * @param dod their date of death if applicable
     * @param gender their gender
     * @param height height in meters
     * @param weight weight in kgs
     * @param bloodType blood type
     * @param isSmoker true if they are a smoker
     * @param alcoholConsumption rate of alcohol consumption
     * @param bpSystolic systolic blood pressure
     * @param bpDiastolic diastolic blood pressure
     * @param address physical address
     * @param region region
     * @param phone phone number
     * @param email their email
     * @param country country they are living in
     * @param city city they are living in
     * @param countryOfDeath country of death
     * @param regionOfDeath region of death
     * @param cityOfDeath city of death
     * @param created date created
     * @param updated time last updated
     * @param preferredName preferred name
     * @param preferredGender preferred gender
     * @param imageName name of their profile image
     */
    public Profile(Integer id, String nhi, String username, Boolean isDonor, Boolean isReceiver,
            String givenNames, String lastNames, LocalDate dob, LocalDateTime dod, String gender,
            Double height, Double weight, String bloodType, Boolean isSmoker,
            String alcoholConsumption, Integer bpSystolic, Integer bpDiastolic, String address,
            String region, String phone, String email, CountriesEnum country, String city,
            CountriesEnum countryOfDeath, String regionOfDeath, String cityOfDeath,
            LocalDateTime created, LocalDateTime updated, String preferredName,
            String preferredGender, String imageName, LocalDateTime lastBloodDonation,
            int bloodDonationPoints) {
        this.id = id;
        this.nhi = nhi;
        this.username = username;
        this.donor = isDonor;
        this.receiver = isReceiver;
        this.givenNames = givenNames;
        this.lastNames = lastNames;
        this.dateOfBirth = dob;
        this.dateOfDeath = dod;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.isSmoker = isSmoker;
        this.alcoholConsumption = alcoholConsumption;
        this.bloodPressureSystolic = bpSystolic;
        this.bloodPressureDiastolic = bpDiastolic;
        this.address = address;
        this.region = region;
        this.phone = phone;
        this.email = email;
        this.country = country;
        this.city = city;
        this.countryOfDeath = countryOfDeath;
        this.cityOfDeath = cityOfDeath;
        this.regionOfDeath = regionOfDeath;
        this.timeOfCreation = created;
        this.lastUpdated = updated;
        this.preferredName = preferredName;
        this.preferredGender = preferredGender;
        this.pictureName = imageName;
        this.bloodDonationPoints = bloodDonationPoints;
        this.lastBloodDonation = lastBloodDonation;
    }

    /**
     * Instantiates a profile with only the id for deleting from the db.
     * @param profileId the id
     */
    public Profile(int profileId) {
        this.id = profileId;
    }

    public Profile(String Nhi) {
        this.nhi = Nhi;
    }

    /**
     * Compares the profile object to another profile object. Result is determined by
     * lexicographical order of profile full name.
     *
     * @param other another profile object to compare to.
     * @return int value to show if object is equal, greater than ore less than the other profile
     * object.
     */
    public int compareTo(Profile other) {
        return getFullName().toLowerCase().compareTo(other.getFullName().toLowerCase());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Profile)) {
            return false;
        }
        Profile profile = (Profile) o;
        return profile.getNhi() == this.getNhi();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getFullName().hashCode();
        return result;
    }

    /**
     * Sets the attributes that are passed into the constructor.
     *
     * @param attributes the attributes given in the constructor
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong
     */
    private void setExtraAttributes(List<String> attributes) {
        for (String val : attributes) {
            String[] parts = val.split("=");
            if (parts.length == 1) {
                String[] newParts = {parts[0], ""};
                setGivenAttribute(newParts);
            } else {
                setGivenAttribute(parts);
            }
        }
    }

    /**
     * Calls the relevant method to set the attribute.
     *
     * @param parts a list with an attribute and value
     * @throws IllegalArgumentException thrown when an attribute that isn't valid is given
     */
    private void setGivenAttribute(String[] parts) {
        String attrName = parts[0];
        String value = null;

        if (null != parts[1]) {
            value = parts[1].replace(QUOTE, "");
        }

        if (value == null) {
            throw new IllegalArgumentException("No values given.");
        }

        if (attrName.equals(Attribute.GIVEN_NAMES.getText())) {
            setGivenNames(value);
        } else if (attrName.equals(Attribute.LAST_NAMES.getText())) {
            setLastNames(value);
        } else if (attrName.equals(Attribute.DATE_OF_BIRTH.getText())) {
            String[] dates = value.split(HYPHEN);
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
        } else if (attrName.equals(Attribute.DATE_OF_DEATH.getText())) {
            if (NULL_STRING.equals(value)) {
                setDateOfDeath(null);
            } else {
                String[] dates = value.split(HYPHEN);
                LocalDateTime date = LocalDateTime.of(
                        Integer.valueOf(dates[2]),
                        Integer.valueOf(dates[1]),
                        Integer.valueOf(dates[0]), 0, 0
                );
                setDateOfDeath(date);
                setCountryOfDeath(getCountry());
                setCityOfDeath(getCity());
                setRegionOfDeath(getRegion());
            }
        } else if (attrName.equals(Attribute.GENDER.getText())) {
            setGender(value.toLowerCase());
        } else if (attrName.equals(Attribute.HEIGHT.getText())) {
            try {
                if (NULL_STRING.equals(value)) {
                    value = ZERO_STRING;
                }
                setHeight(Double.valueOf(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid height entered");
            }
        } else if (attrName.equals(Attribute.WEIGHT.getText())) {
            try {
                if (NULL_STRING.equals(value)) {
                    value = ZERO_STRING;
                }
                setWeight(Double.valueOf(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid weight entered");
            }
        } else if (attrName.equals(Attribute.BLOOD_TYPE.getText())) {
            if (NULL_STRING.equals(value) || "".equals(value)) {
                value = null;
            }
            setBloodType(value);
        } else if (attrName.equals(Attribute.ADDRESS.getText())) {
            setAddress(value);
        } else if (attrName.equals(Attribute.COUNTRY.getText())) {
            if (!CountriesEnum.toArrayList().contains(value)) {
                throw new IllegalArgumentException("Must be a valid country!");
            }
            setCountry(CountriesEnum.getEnumByString(value));
        } else if (attrName.equals(Attribute.REGION.getText())) {
            if (getCountry() != null && !regionsNZ.contains(value) &&
                    (getCountry().getName().equalsIgnoreCase(CountriesEnum.NZ.getName()) ||
                            getCountry().getName()
                                    .equalsIgnoreCase(CountriesEnum.NZ.toString()))) {

                throw new IllegalArgumentException("Must be a region within New Zealand");
            }
            setRegion(value);
        } else if (attrName.equals(Attribute.NHI.getText())) {
            try {
                setNhi(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid NHI number entered");
            }
        } else if ("isSmoker".equals(attrName)) {
            setIsSmoker(Boolean.valueOf(value));
        } else if ("alcoholConsumption".equals(attrName)) {
            setAlcoholConsumption(value);
        } else if ("bloodPressureSystolic".equals(attrName)) {
            if (NULL_STRING.equals(value)) {
                setBloodPressureSystolic(null);
            } else {
                setBloodPressureSystolic(Integer.valueOf(value));
            }
        } else if ("bloodPressureDiastolic".equals(attrName)) {
            if (NULL_STRING.equals(value)) {
                setBloodPressureDiastolic(null);
            } else {
                setBloodPressureDiastolic(Integer.valueOf(value));
            }
        } else if ("phone".equals(attrName)) {
            setPhone(value);
        } else if ("email".equals(attrName)) {
            setEmail(value);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Gets all of the profiles procedures.
     *
     * @return all procedures
     */
    public List<Procedure> getAllProcedures() {
        return procedures;
    }

    /**
     * Gets all the pending procedures.
     *
     * @return pending procedures
     */
    public List<Procedure> getPendingProcedures() {
        return this.pendingProcedures;
    }

    public void setPendingProcedures(List<Procedure> pendingProcedures) {
        this.pendingProcedures = pendingProcedures;
    }

    public List<Procedure> getPreviousProcedures() {
        return this.previousProcedures;
    }

    public void setPreviousProcedures(List<Procedure> previous) {
        this.previousProcedures = previous;
    }

    public String getAttributesSummary() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String summary = "";
        summary = summary + ("nhi=" + nhi);
        summary = summary + COMMA + ("given-names=" + givenNames);
        summary = summary + COMMA + ("last-names=" + lastNames);
        summary = summary + COMMA + ("dob=" + dateOfBirth
                .format(format));
        if (dateOfDeath == null) {
            summary = summary + COMMA + (DOD_STRING + null);
        } else {
            summary = summary + COMMA + (DOD_STRING + dateOfDeath
                    .format(format));
        }
        summary = summary + COMMA + ("gender=" + gender);
        summary = summary + COMMA + ("height=" + height);
        summary = summary + COMMA + ("weight=" + weight);
        summary = summary + COMMA + ("blood-type=" + bloodType);
        summary = summary + COMMA + ("address=" + address);
        summary = summary + COMMA + ("region=" + region);
        summary = summary + COMMA + ("country=" + country);
        summary = summary + COMMA + ("isSmoker=" + isSmoker);
        summary = summary + COMMA + ("alcoholConsumption=" + alcoholConsumption);
        summary = summary + COMMA + ("bloodPressureSystolic=" + bloodPressureSystolic);
        summary = summary + COMMA + ("bloodPressureDiastolic=" + bloodPressureDiastolic);
        summary = summary + COMMA + ("phone=" + phone);
        summary = summary + COMMA + ("email=" + email);
        return summary;
    }

    /**
     * Add an organ to the organs donate list.
     *
     * @param organ the organ the profile wishes to donate
     * @throws OrganConflictException thrown if organ is already donating.
     */
    public void addOrganDonating(OrganEnum organ) throws OrganConflictException {
        if (this.organsDonating.contains(organ)) {
            // A donor cannot donate an organ they've received.
            throw new OrganConflictException(
                    "profile has previously received " + organ,
                    organ
            );
        }
        this.organsDonating.add(organ);
    }

    /**
     * Add a set of organs to the list of organs that the profile wants to donate.
     *
     * @param organs the set of organs to donate
     * @throws IllegalArgumentException if a bad argument is used
     * @throws OrganConflictException if there is a conflicting organ
     */
    public void addOrgansDonating(Set<OrganEnum> organs)
            throws OrganConflictException {
        generateUpdateInfo("organsDonating");

        for (OrganEnum organ : organs) {
            if (this.organsDonating.contains(organ)) {
                throw new IllegalArgumentException(
                        "Organ " + organ + " already exists in donating list"
                );
            }
            this.addOrganDonating(organ);

            History action = new History(PROFILE_STRING, this.getId(), "set", organ.getNamePlain(),
                    -1, LocalDateTime.now());
            CurrentHistory.updateHistory(action);
        }
    }

    /**
     * Add an organ to the organs required list.
     *
     * @param organ the organ the profile requires
     */
    public void addOrganRequired(OrganEnum organ) {
        this.setReceiver(true);
        this.organsRequired.add(organ);
    }

    /**
     * Add a set of organs that the profile requires to the required organs set.
     *
     * @param organs the set of organs to be received
     */
    public void addOrgansRequired(Set<OrganEnum> organs) {
        generateUpdateInfo("organsRequired");

        for (OrganEnum organ : organs) {
            addOrganRequired(organ);
            LocalDateTime now = LocalDateTime.now();
            History action = new History("profile", this.getId(), "required organ",
                    "" + organ.getNamePlain(), -1, now);
            CurrentHistory.updateHistory(action);
        }
    }

    public Set<OrganEnum> getOrgansRequired() {
        return organsRequired;
    }

    /**
     * Add an organ to the set of received organs. If the organ exists in the receiving set, remove
     * it.
     *
     * @param organ to be added
     */
    public void addOrganReceived(OrganEnum organ) {
        this.organsRequired.remove(organ);
        this.organsReceived.add(organ);
    }

    /**
     * Add a set of organs to the set of received organs.
     *
     * @param organs set to be added
     */
    public void addOrgansReceived(Set<OrganEnum> organs) {
        generateUpdateInfo("organsReceived");

        for (OrganEnum organ : organs) {
            addOrganReceived(organ);
            History action = new History(PROFILE_STRING,
                    this.getId(),
                    "received", organ.getNamePlain(), -1, LocalDateTime.now());
            CurrentHistory.updateHistory(action);
        }
    }

    public Set<OrganEnum> getOrgansReceived() {
        return organsReceived;
    }

    /**
     * Add an organ to the list of donated organsDonating. If the organ exists in the donating list,
     * remove it from the donating list.
     *
     * @param organ the organ to be added
     */
    public void addOrganDonated(OrganEnum organ) {
        this.organsDonating.remove(organ);
        this.organsDonated.add(organ);
    }

    /**
     * Add a set of organsDonating to the list of organsDonating that the profile has donated.
     *
     * @param organs a set of organsDonating that the profile has donated
     */
    public void addOrgansDonated(Set<OrganEnum> organs) {
        generateUpdateInfo("pastDonations");

        for (OrganEnum organ : organs) {
            this.organsDonated.add(organ);
            History action = new History(
                    PROFILE_STRING,
                    this.getId(),
                    "donated",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );
            CurrentHistory.updateHistory(action);
        }
    }

    /**
     * Removes an organ from the received organs.
     * @param organ the organ to remove
     */
    public void removeOrganReceived(OrganEnum organ) {
        this.organsReceived.remove(organ);
    }

    /**
     * Removes an organ from the required organs.
     * @param organ the organ to be removed
     */
    public void removeOrganRequired(OrganEnum organ) {
        this.organsRequired.remove(organ);
    }

    /**
     * Removes an organ from the donated organs.
     * @param organ the organ to be removed
     */
    public void removeOrganDonated(OrganEnum organ) {
        this.organsDonated.remove(organ);
    }

    /**
     * Removes an organ from the donating organs.
     * @param organ the organ to be removed
     */
    public void removeOrganDonating(OrganEnum organ) {
        this.organsDonating.remove(organ);
    }

    public void setReceiver(boolean receiver) {
        this.receiver = receiver;
    }

    /**
     * Returns true if profile is a receiver.
     * @return boolean
     */
    public boolean isReceiver() {
        return receiver;
    }

    /**
     * Calculate the profiles age if they are alive and their age at death if they are dead If the
     * age is calculated on the users birthday they are the age they are turning that day e.g. if
     * it's your 20th birthday you are 20
     *
     * @return profile age
     */
    private int calculateAge() {
        if (dateOfDeath == null) {
            return Period.between(dateOfBirth, LocalDate.now()).getYears();
        } else {
            return Period.between(dateOfBirth, dateOfDeath.toLocalDate()).getYears();
        }
    }

    public int getAge() {
        return calculateAge();
    }

    /**
     * Logs which property was updated and the time it was updated.
     * Also changes the last updated property.
     *
     * @param property the property that was updated
     */
    private void generateUpdateInfo(String property) {
        LocalDateTime currentTime = LocalDateTime.now();
        lastUpdated = currentTime;
        String output = property + " updated at " + currentTime
                .format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"));
        updateActions.add(output);
    }

    public List<Drug> getCurrentMedications() {
        return currentMedications;
    }

    public List<Drug> getHistoryOfMedication() {
        return historyOfMedication;
    }

    public List<String> getMedicationTimestamps() {
        return medicationTimestamps;
    }

    public Set<OrganEnum> getOrgansDonated() {
        return organsDonated;
    }

    public Set<OrganEnum> getOrgansDonating() {
        return organsDonating;
    }

    public Set<OrganEnum> getOrgansDonatingNotExpired() {
        return getOrgansDonating();
    }

    public Set<OrganEnum> getOrgansExpired() {
        return organsExpired;
    }

    /**
     * Gets all the current conditions of the user.
     *
     * @return the conditions of the user
     */
    public List<Condition> getAllConditions() {
        return this.conditions;
    }

    /**
     * Gets all the cured conditions of the user.
     *
     * @return the cured conditions of the user
     */
    public List<Condition> getCuredConditions() {
        List<Condition> curedConditions = new ArrayList<>();
        for (Condition condition : this.conditions) {
            if (condition.getCured()) {
                curedConditions.add(condition);
            }
        }

        return curedConditions;
    }

    /**
     * Gets all the current conditions of the user.
     *
     * @return the current conditions of the user
     */
    public List<Condition> getCurrentConditions() {
        List<Condition> currentConditions = new ArrayList<>();
        for (Condition condition : this.conditions) {
            if (!condition.getCured()) {
                currentConditions.add(condition);
            }
        }
        return currentConditions;
    }

    /**
     * Checks if a profile is donating a certain selection of organs.
     *
     * @param organs organs to be checked
     * @return true if they are
     */
    public boolean isDonatingCertainOrgans(Set<OrganEnum> organs) {
        return organsDonating.containsAll(organs);
    }

    /**
     * Checks if a profile is receiving a certain selection of organs.
     *
     * @param organs organs to be checked
     * @return true if they are
     */
    public boolean isReceivingCertainOrgans(Set<OrganEnum> organs) {
        return organsRequired.containsAll(organs);
    }

    public LocalDateTime getTimeOfCreation() {
        return this.timeOfCreation;
    }

    public String getGivenNames() {
        return this.givenNames;
    }

    public String getFullName() {
        return givenNames + SPACE + lastNames;
    }

    /**
     * This method is used to populate a column in the search table, intelliJ thinks it is un-used.
     * @return the full preferred name
     */
    public String getFullPreferredName() {
        if (preferredName == null || "".equals(preferredName)) {
            return givenNames + SPACE + lastNames;
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

    public LocalDateTime getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDateTime dateOfDeath) {
        if (dateOfDeath != null && getDateOfBirth().isAfter(dateOfDeath.toLocalDate())) {
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
        String newGender = gender.toLowerCase().trim();
        if ("male".equals(newGender) || "female".equals(newGender)) {
            generateUpdateInfo("gender");
            this.gender = newGender;
        } else {
            throw new IllegalArgumentException();
        }
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
        if (bloodType != null && BloodTypeEnum.toArrayList().contains(bloodType)) {
            generateUpdateInfo("blood-type");
            this.bloodType = bloodType;
        } else {
            throw new IllegalArgumentException(
                    "Invalid blood type selected.\n" +
                            bloodType + " is not a valid blood type."
            );
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

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getUpdateActions() {
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
        if (bloodPressureDiastolic != null && bloodPressureSystolic != null &&
                bloodPressureDiastolic != 0 && bloodPressureSystolic != 0) {
            return bloodPressureSystolic.toString() + "/" + bloodPressureDiastolic.toString();
        }
        return null;
    }

    public void setProcedures(List<Procedure> procedures) {
        this.procedures = procedures;
    }


    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public void setCurrentMedications(List<Drug> currentMedications) {
        this.currentMedications = currentMedications;
    }

    public void setHistoryOfMedication(List<Drug> historyOfMedication) {
        this.historyOfMedication = historyOfMedication;
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
        if (validate(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Validates an email address string.
     * @param emailStr the string to be validated
     * @return true if valid
     */
    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
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

    public CountriesEnum getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(CountriesEnum birthCountry) {
        this.birthCountry = birthCountry;
    }

    public CountriesEnum getCountry() {
        return country;
    }

    public void setCountry(CountriesEnum country) {
        this.country = country;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public CountriesEnum getCountryOfDeath() {
        return countryOfDeath;
    }

    public void setCountryOfDeath(CountriesEnum countryOfDeath) {
        this.countryOfDeath = countryOfDeath;
    }

    public String getRegionOfDeath() {
        return regionOfDeath;
    }

    public void setRegionOfDeath(String regionOfDeath) {
        this.regionOfDeath = regionOfDeath;
    }

    public String getCityOfDeath() {
        return cityOfDeath;
    }

    public void setCityOfDeath(String cityOfDeath) {
        this.cityOfDeath = cityOfDeath;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public String getUsername() {
        return username;
    }

    public int getBloodPressureSystolic() {
        return this.bloodPressureSystolic;
    }

    public int getBloodPressureDiastolic() {
        return this.bloodPressureDiastolic;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    /**
     * Calculates and returns the profiles bmi.
     *
     * @return BMI
     */
    public Double getBmi() {
        return weight / Math.pow(height / HEIGHT_TO_METERS, 2);
    }

    public void setLastUpdated() {
        lastUpdated = LocalDateTime.now();
    }

    /**
     * Updates the donor status.
     */
    public void updatedDonorStatus() {
        if (!organsDonating.isEmpty()) {
            this.setDonor(true);
        } else {
            this.setDonor(false);
        }
    }

    /**
     * Updates the receiver status.
     */
    public void updatedReceiverStatus() {
        if (!organsRequired.isEmpty()) {
            this.setReceiver(true);
        } else {
            this.setReceiver(false);
        }
    }

    public LocalDateTime getOrganDate(String name) {
        for (Organ o : organTimeStamps) {
            if (o.getOrganEnum().getName().equals(name)) {
                return o.getDate();
            }
        }
        return null;
    }

    public void setOrganDate(String organDate, LocalDateTime date) {
        organTimeStamps.add(
                new Organ(OrganEnum.valueOf(
                        organDate.toUpperCase().replace(HYPHEN, "_")),
                        date)
        );
    }

    public void setLastBloodDonation(LocalDateTime date) {
        lastBloodDonation = date;
    }

    public LocalDateTime getLastBloodDonation(){
        return lastBloodDonation;
    }

    public void addBloodDonationPoints(int points) {
        bloodDonationPoints += points;
    }

    public int getBloodDonationPoints() {
        return bloodDonationPoints;
    }

    public void addProcedure(Procedure procedure) {
        if (procedure.getDateTime() != null) {
            // This case only occurs when adding a matched organ donation
            pendingProcedures.add(procedure);
        } else {
            if (LocalDate.now().isBefore(procedure.getDate())) {
                previousProcedures.add(procedure);
            } else {
                pendingProcedures.add(procedure);
            }
        }
        procedures.add(procedure);
    }
}
