package odms.model.user;

import javafx.scene.image.Image;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.model.enums.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import odms.model.enums.CountriesEnum;

public class User {

    private UserType userType;
    private String name;
    private Integer staffID;
    private String workAddress;
    private String region;
    private LocalDateTime lastUpdated;
    private ArrayList<String> updateActions = new ArrayList<>();
    private LocalDateTime timeOfCreation;
    private String username;
    private String password; //not being used yet, but will be in the future.
    private boolean isDefault;
    private String pictureName;

    public static ObservableList<Integer> allowedCountriesIndices = FXCollections.observableArrayList();

    /**
     * user constructor
     *
     * @param userType  type of user
     * @param attrArray array containing users attributes
     */
    public User(UserType userType, ArrayList<String> attrArray) {
        this.userType = userType;
        setExtraAttributes(attrArray);
        timeOfCreation = LocalDateTime.now();
    }

    /**
     * user constructor.
     *
     * @param userType type of user
     * @param name     user name
     * @param region   user region
     */
    public User(UserType userType, String name, String region) {
        this.timeOfCreation = LocalDateTime.now();
        this.userType = userType;
        this.name = name;
        this.region = region;
        timeOfCreation = LocalDateTime.now();
        this.updateActions.add("Account for " + name + "created at " + LocalDateTime.now());
    }

    /**
     *
     * @param userType type of user
     * @param name name of user
     * @param region users region
     * @param username username of the user
     * @param password users password
     */
    public User(UserType userType, String name, String region, String username, String password) {
        this.userType = userType;
        this.name = name;
        this.region = region;
        this.username = username;
        this.password = password;
    }

    /**
     * Creates an administrator account
     *
     * @param userType the user type to be set
     * @param name     the users name.
     */
    public User(UserType userType, String name) {
        this.timeOfCreation = LocalDateTime.now();
        this.userType = userType;
        this.name = name;
        this.updateActions
                .add(userType + "account for " + name + "created at " + LocalDateTime.now());
    }

    /**
     * user constructor.
     * @param userType type of user.
     * @param name user name.
     * @param region user region.
     */
    public User(int userId, String username, String password, String name, UserType userType,
            String address, String region, LocalDateTime created, LocalDateTime updated,
            String imageName) {
        this.staffID = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.userType = userType;
        this.workAddress = address;
        this.region = region;
        this.timeOfCreation = created;
        this.lastUpdated = updated;
        this.updateActions.add(name + " logged on at " + LocalDateTime.now());
        this.pictureName = imageName;
    }


    /**
     * Logs which property was updated and the time it was updated Also changes the last updated
     * property
     *
     * @param property the property that was updated
     */
    private void generateUpdateInfo(String property) {
        LocalDateTime currentTime = LocalDateTime.now();
        this.lastUpdated = currentTime;
        String output = property + " updated at " + currentTime
                .format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"));
        this.updateActions.add(output);
    }

    /**
     * Sets the attributes that are passed into the constructor.
     *
     * @param attributes the attributes given in the constructor.
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong.
     */
    public void setExtraAttributes(ArrayList<String> attributes) throws IllegalArgumentException {
        for (String val : attributes) {
            String[] parts = val.split("=");
            setGivenAttribute(parts);
        }
    }

    /**
     * sets a users specific given attribute.
     *
     * @param parts a string containing the users new attribute to be set.
     * @throws IllegalArgumentException
     */
    private void setGivenAttribute(String[] parts) throws IllegalArgumentException {
        String attrName = parts[0];
        String value = parts[1].replace("\"", ""); // get rid of the speech marks;

        if (attrName.startsWith(" ")) {
            attrName = attrName.substring(1);
        }

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
            case "password":
                setPassword(value);
                break;
            case "username":
                setUsername(value);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * gets a attribute summary of the user
     *
     * @return attribute summary of the user
     */
    public String getAttributesSummary() {
        String summary = "";
        summary = summary + ("staffID=" + staffID);
        summary = summary + "," + ("name=" + name);
        summary = summary + "," + ("workAddress=" + workAddress);
        summary = summary + "," + ("region=" + region);
        return summary;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        generateUpdateInfo(name);
    }

    public Integer getStaffID() {
        return this.staffID;
    }

    public void setStaffID(Integer staffID) {
        this.staffID = staffID;
        generateUpdateInfo(staffID.toString());
    }

    public String getWorkAddress() {
        return this.workAddress;
    }

    public void setWorkAddress(String address) {
        this.workAddress = address;
        generateUpdateInfo(workAddress);
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
        generateUpdateInfo(region);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
        generateUpdateInfo(this.username);
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }
}
