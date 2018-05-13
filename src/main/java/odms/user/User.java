package odms.user;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class User {

    private UserType userType;
    private String name;
    private Integer staffId;
    private String workAddress;
    private String region;
    private LocalDateTime lastUpdated;
    private ArrayList<String> updateActions = new ArrayList<>();
    private LocalDateTime timeOfCreation;
    private String username;
    private String password; //not being used yet, but will be in the future.

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

    public User(UserType userType){
        this.userType = userType;
    }

    public User(UserType userType, String name, String region) {
        this.timeOfCreation = LocalDateTime.now();
        this.userType = userType;
        this.name = name;
        this.region = region;
        this.updateActions.add("Account for " + name + "created at " + LocalDateTime.now());
    }

    /**
     * Creates an administrator account
     * @param userType the user type to be set
     * @param name the users name.
     */
    public User(UserType userType, String name) {
        this.timeOfCreation = LocalDateTime.now();
        this.userType = userType;
        this.name = name;
        this.updateActions.add(userType + "account for " + name + "created at " + LocalDateTime.now());
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

        switch (attrName) {
            case "name":
                setName(value);
                break;
            case "workAddress":
                setWorkAddress(value);
                break;
            case "staffId":
                setStaffId(Integer.parseInt(value));
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
        summary = summary +("staffId=" + staffId);
        summary = summary +"," +("name=" + name);
        summary = summary +"," +("workAddress=" + workAddress);
        summary = summary +"," +("region=" + region);
        return summary;
    }

    public void setName(String name){
        this.name = name;
        generateUpdateInfo(name);
    }

    public String getName(){
        return this.name;
    }

    public void setStaffId(Integer staffId){
        this.staffId = staffId;
        generateUpdateInfo(staffId.toString());
    }

    public Integer getStaffId(){
        return this.staffId;
    }

    public void setWorkAddress(String address){
        this.workAddress = address;
        generateUpdateInfo(workAddress);
    }

    public String getWorkAddress(){
        return this.workAddress;
    }

    public void setRegion(String region){
        this.region = region;
        generateUpdateInfo(region);
    }

    public String getRegion(){
        return this.region;
    }

    public void setUsername(String username) {
        this.username = username;
        generateUpdateInfo(this.username);
    }

    public String getUsername() {
        return this.username;
    }

    public ArrayList<String> getUpdateActions() {
        return updateActions;
    }
}
