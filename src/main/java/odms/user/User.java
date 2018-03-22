package odms.user;


import java.time.LocalDate;
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
    private ArrayList<String> updateActions;
    private LocalDateTime timeOfCreation;


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

    public User(UserType userType){
        this.userType = userType;
    }

    public User(UserType userType, String name, String region){
        LocalDateTime currentTime = LocalDateTime.now();
        this.userType = userType;
        this.name = name;
        this.region = region;
        this.timeOfCreation = currentTime.now();
        updateActions.add("Account for " + name + "created at " + currentTime);
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
     * @param staffId staff id to be set
     */
    public void setStaffId(Integer staffId){
        this.staffId = staffId;
        generateUpdateInfo(staffId.toString());
    }

    /**
     * Gets the staff id of the user
     */
    public Integer getStaffId(){
        return this.staffId;
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
}
