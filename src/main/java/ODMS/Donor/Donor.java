package ODMS.Donor;

import com.sun.org.apache.xpath.internal.operations.Or;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    private Set<Organ> organs = new HashSet<Organ>();

    private int donorID; // Not being used at the moment, not sure how we want to make donor's unique
    private LocalDateTime timeOfCreation;

    public Donor(String givenNames, String lastName, LocalDate dateOfBirth) {
        this.givenNames = givenNames;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;

        timeOfCreation = LocalDateTime.now();
    }

    public void viewAttributes() {
        if (givenNames != null) {
            System.out.println("Given Names: " + givenNames);
        }

        if (lastName != null) {
            System.out.println("Last Name: " + lastName);
        }

        System.out.println("Date Of Birth: " + dateOfBirth.format(DateTimeFormatter.ISO_DATE));

        if (dateOfDeath != null) {
            System.out.println("Date Of Death: " + dateOfDeath.format(DateTimeFormatter.ISO_DATE));
        }

        if (gender != null) {
            System.out.println("Gender: " + gender);
        }

        if (height != 0.0) {
            System.out.println("Height: " + height);
        }

        if (weight != 0.0) {
            System.out.println("Weight: " + weight);
        }

        if (bloodType != null) {
            System.out.println("Blood Type: " + bloodType);
        }

        if (address != null) {
            System.out.println("Address: " + address);
        }

        if (region != null) {
            System.out.println("Region: " + region);
        }

        System.out.println("Time of Creation: " + timeOfCreation
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
    }

    public void addOrgans(Set<Organ> organs) {
        this.organs.addAll(organs);
    }

    public void removeOrgans(Set<Organ> organs) {
        this.organs.removeAll(organs);
    }

    public Set<Organ> getOrgans() {
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