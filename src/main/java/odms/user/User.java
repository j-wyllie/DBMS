package odms.user;

import odms.donor.Donor;

public class User extends Donor {

    private UserType userType;
    private String name;
    private Integer staffId;
    private String workAddress;
    private String region;

    public User(UserType userType){
        this.userType = userType;
    }

    public User(UserType userType, String name, String region){
        this.userType = userType;
        this.name = name;
        this.region = region;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setStaffId(Integer staffId){
        this.staffId = staffId;
    }

    public Integer getStaffId(){
        return this.staffId;
    }

    public void setWorkAddress(String address){
        this.workAddress = address;
    }

    public String getWorkAddress(){
        return this.workAddress;
    }

    public void setRegion(String region){
        this.region = region;
    }

    public String getRegion(){
        return this.region;
    }
}
