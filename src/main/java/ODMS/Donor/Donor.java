package ODMS.Donor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

public class Donor {
    private String givenNames;
    private String lastName;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private String gender;
    private double height;
    private double weight;
    private String bloodType;
    private String address;
    private String region;

    private ArrayList<Organ> organs = new ArrayList<Organ>();

    private int donorID; // Not being used at the moment, not sure how we want to make donor's unique
    private LocalDateTime timeOfCreation;

    public Donor(String givenNames, String lastName, LocalDate dateOfBirth) {
        this.givenNames = givenNames;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;

        timeOfCreation = LocalDateTime.now();
    }

    public void viewAttributes() {
        System.out.println("Given Names: " + givenNames);
        System.out.println("Last Name: " + lastName);
        System.out.println("Date Of Birth: " + dateOfBirth.format(DateTimeFormatter.ISO_DATE));
        if (dateOfDeath != null) {
            System.out.println("Date Of Death: " + dateOfDeath.format(DateTimeFormatter.ISO_DATE));
        } else {
            System.out.println("Date Of Death: null");
        }
        System.out.println("Gender: " + gender);
        System.out.println("Height: " + height);
        System.out.println("Weight: " + weight);
        System.out.println("Blood Type: " + bloodType);
        System.out.println("Address: " + address);
        System.out.println("Region: " + region);
        System.out.println("Time of Creation: " + timeOfCreation.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
    }

    public void addOrgans(ArrayList<Organ> organs) {
        this.organs.addAll(organs);
    }

    public ArrayList<Organ> getOrgans() {
        return organs;
    }

    public LocalDateTime getTimeOfCreation() {
        return timeOfCreation;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getDonorID() {
        return donorID;
    }

    public void setDonorID(int donorID) {
        this.donorID = donorID;
    }
}