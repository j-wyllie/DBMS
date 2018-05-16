package odms.user;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class User {

    private UserType userType;
    private String name;
    private Integer staffID;
    private String workAddress;
    private String region;
    private LocalDateTime lastUpdated;
    private ArrayList<String> updateActions = new ArrayList<>();
    private LocalDateTime timeOfCreation;

    /**
     * Logs which property was updated and the time it was updated
     * Also changes the last updated property
     * @param property the property that was updated
     */
    private void generateUpdateInfo(String property) {
        LocalDateTime currentTime = LocalDateTime.now();
        this.lastUpdated = currentTime;
        String output = property + " updated at " + currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"));
        this.updateActions.add(output);
    }

    public User(UserType userType, ArrayList<String> attrArray){
        this.userType = userType;
        setExtraAttributes(attrArray);
        timeOfCreation = LocalDateTime.now();
    }

    public User(UserType userType, String name, String region){
        this.timeOfCreation = LocalDateTime.now();
        this.userType = userType;
        this.name = name;
        this.region = region;
        timeOfCreation = LocalDateTime.now();
        this.updateActions.add("Account for " + name + "created at " + LocalDateTime.now());
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

    private void setGivenAttribute(String[] parts) throws IllegalArgumentException {
        String attrName = parts[0];
        String value = parts[1].replace("\"", ""); // get rid of the speech marks;

        if (attrName.startsWith(" ")) {attrName = attrName.substring(1);}   //TODO why does this bug pop up

        //TODO add in 'attribute' functionality like in profile/Profile
        switch (attrName) {
            case "name":
                setName(value);
                break;
            case "workAddress":
                setWorkAddress(value);
                break;
            case "region":
                setRegion(value);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public String getAttributesSummary() {
        String summary = "";
        summary = summary +("staffID=" + staffID);
        summary = summary +"," +("name=" + name);
        summary = summary +"," +("workAddress=" + workAddress);
        summary = summary +"," +("region=" + region);
        return summary;
    }

    /**
     * Sets the name of the user
     * @param name name to be set
     */
    public void setName(String name){
        this.name = name;
        generateUpdateInfo(name);

    }

    /**
     * Gets the name of the user
     */
    public String getName(){
        return this.name;
    }

    /**
     * Sets the staff id of the user
     * @param staffID staff id to be set
     */
    public void setStaffID(Integer staffID){
        this.staffID = staffID;
        generateUpdateInfo(staffID.toString());
    }

    /**
     * Gets the staff id of the user
     */
    public Integer getStaffID(){
        return this.staffID;
    }

    /**
     * Sets the work address of the user
     * @param address address to be set
     */
    public void setWorkAddress(String address){
        this.workAddress = address;
        generateUpdateInfo(workAddress);
    }

    /**
     * Gets the work address of the user
     */
    public String getWorkAddress(){
        return this.workAddress;
    }

    /**
     * Sets the region of the user
     * @param region The region to be set
     */
    public void setRegion(String region){
        this.region = region;
        generateUpdateInfo(region);
    }

    /**
     * Gets the region of the user
     */
    public String getRegion(){
        return this.region;
    }
    /**
     * Returns the update history of the user
     */

    public ArrayList<String> getUpdateActions() {
        return updateActions;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(LocalDateTime timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }
}
