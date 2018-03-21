package odms.donor;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import org.omg.CORBA.ORB;

import static org.junit.Assert.*;

public class DonorTest {

    /**
     * Test to create a valid user
     * @throws IllegalArgumentException
     */
    @Test
    public void testCreateBasicUser() throws IllegalArgumentException {
        Donor testDonor = null;

        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"17-01-1998\"");
        donorAttr.add("ird=\"123456879\"");

        try {
            testDonor = new Donor(donorAttr);
        } catch (IllegalArgumentException e) {
            //pass
        }

        assertTrue(testDonor != null);
    }

    @Test
    public void testCreateBasicUserRawData() throws IllegalArgumentException {
        Donor testDonor = null;

        try {
            testDonor = new Donor("John", "Smithy", "17-01-1998", 123456789);
        } catch (IllegalArgumentException e) {
            //pass
        }

        assertTrue(testDonor != null);
    }

    /**
     * Test creating a users with every attribute
     * @throws IllegalArgumentException
     */
    @Test
    public void testCreateFullUser() throws IllegalArgumentException {
        Donor testDonor = null;

        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"17-01-1998\"");
        donorAttr.add("ird=\"123456879\"");
        donorAttr.add("dod=\"6-3-2018\"");
        donorAttr.add("gender=\"male\"");
        donorAttr.add("height=\"86.0\"");
        donorAttr.add("weight=\"72.0\"");
        donorAttr.add("blood-type=\"O+\"");
        donorAttr.add("address=\"Riccarton\"");
        donorAttr.add("region=\"Christchurch\"");

        try {
            testDonor = new Donor(donorAttr);
        } catch (IllegalArgumentException e) {
            //pass
        }

        assertTrue(testDonor != null);
    }

    /**
     * Test to create an invalid user with no IRD no
     * @throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNoIRD() throws IllegalArgumentException {
        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("\"given-names=John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"17-01-1998\"");

        Donor donorOnlyAttr = new Donor(donorAttr);
    }

    /**
     * Test to create an invalid user with no DOB
     * @throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNoDOB() throws IllegalArgumentException {
        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("ird=\"123456879\"");;

        Donor donorOnlyAttr = new Donor(donorAttr);
    }

    /**
     * Test to create an invalid user with no first-name
     * @throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNoFirstName() throws IllegalArgumentException {
        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"17-01-1998\"");
        donorAttr.add("ird=\"123456879\"");

        Donor donorOnlyAttr = new Donor(donorAttr);
    }

    /**
     * Test to create an invalid user with no last name
     * @throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNoLastName() throws IllegalArgumentException {
        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("dob=\"17-01-1998\"");
        donorAttr.add("ird=\"123456879\"");

        Donor donorOnlyAttr = new Donor(donorAttr);
    }

    /**
     * Test to create an invalid user with an incorrectly spelt attribute
     * @throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserBadAttr() throws IllegalArgumentException {
        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-na=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"17-01-1998\"");
        donorAttr.add("ird=\"123456879\"");

        Donor donorOnlyAttr = new Donor(donorAttr);
    }

    /**
     * Test the ability to add organs to the list of donatable organs
     */
    @Test
    public void testAddDonatableOrgans() {
        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"17-01-1998\"");
        donorAttr.add("ird=\"123456879\"");

        Donor testDonor = null;
        try {
            testDonor = new Donor(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        Set<String> someOrgans = new HashSet<>();
        someOrgans.add("bone");
        someOrgans.add("heart");
        someOrgans.add("cornea");
        testDonor.addDonations(someOrgans);

        Set<Organ> expected = new HashSet<>();
        expected.add(Organ.BONE);
        expected.add(Organ.HEART);
        expected.add(Organ.CORNEA);

        assertEquals(expected, testDonor.getDonatedOrgans());
    }

    /**
     * Test the ability to add an organ to the list of organs that the donor can donate
     */
    @Test
    public void testAddOrgans() {
        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"17-01-1998\"");
        donorAttr.add("ird=\"123456879\"");

        Donor testDonor = null;
        try {
            testDonor = new Donor(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        Set<String> someOrgans = new HashSet<>();
        someOrgans.add("bone");
        someOrgans.add("heart");
        someOrgans.add("cornea");
        testDonor.addOrgans(someOrgans);

        Set<Organ> expected = new HashSet<>();
        expected.add(Organ.BONE);
        expected.add(Organ.HEART);
        expected.add(Organ.CORNEA);

        assertEquals(expected, testDonor.getOrgans());
    }

    /**
     * Test the ability to remove organs from the list of donatable organs
     */
    @Test
    public void testRemoveDonatableOrgans() {
        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"17-01-1998\"");
        donorAttr.add("ird=\"123456879\"");

        Donor testDonor = null;
        try {
            testDonor = new Donor(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        Set<String> someOrgans = new HashSet<>();
        someOrgans.add("bone");
        someOrgans.add("heart");
        someOrgans.add("cornea");
        testDonor.addDonations(someOrgans);

        Set<String> removedOrgans = new HashSet<>();
        removedOrgans.add("bone");
        removedOrgans.add("heart");
        testDonor.removeDonoations(removedOrgans);

        Set<Organ> expected = new HashSet<>();
        expected.add(Organ.CORNEA);

        assertEquals(testDonor.getDonatedOrgans(), expected);
    }

    /*
     * Tests the ability to remove an organ from the list of organs that the
     * donor has donated
     */
    @Test
    public void testRemoveOrgans() {
        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"17-01-1998\"");
        donorAttr.add("ird=\"123456879\"");

        Donor testDonor = null;
        try {
            testDonor = new Donor(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        Set<String> someOrgans = new HashSet<>();
        someOrgans.add("bone");
        someOrgans.add("heart");
        someOrgans.add("cornea");
        testDonor.addOrgans(someOrgans);

        Set<String> removedOrgans = new HashSet<>();
        removedOrgans.add("bone");
        removedOrgans.add("heart");
        testDonor.removeOrgans(removedOrgans);

        Set<Organ> expected = new HashSet<>();
        expected.add(Organ.CORNEA);

        assertEquals(testDonor.getOrgans(), expected);
    }

    /**
     * Tests that when an existing organ is added it does not duplicate it
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddExistingOrgan() {
        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"17-01-1998\"");
        donorAttr.add("ird=\"123456879\"");

        Donor testDonor = null;
        try {
            testDonor = new Donor(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        Set<String> someOrgans = new HashSet<>();
        someOrgans.add("bone");

        testDonor.addOrgans(someOrgans);
        testDonor.addOrgans(someOrgans);
    }

    /**
     * Check that the property changes are recorded
     */
    @Test
    public void testPropertyChangeEvent() {
        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"17-01-1998\"");
        donorAttr.add("ird=\"123456879\"");

        Donor testDonor = null;
        try {
            testDonor = new Donor(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        assertEquals(testDonor.getUpdateActions().size(), 4);
    }

    /**
     * Testing the bmi to check that it is accurate
     */
    @Test
    public void testCalculateBMI() {
        DecimalFormat df = new DecimalFormat("#.##");

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"17-01-1998\"");
        donorAttr.add("ird=\"123456879\"");
        donorAttr.add("weight=\"72.0\"");
        donorAttr.add("height=\"175.0\"");

        Donor testDonor = null;
        try {
            testDonor = new Donor(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        double bmi = testDonor.calculateBMI();
        assertEquals(df.format(bmi), "23.51");
    }

    /**
     * Tests the calculated age if the user is alive
     * Also implicitly tests the calculation after the users birthday
     * This test will depreciate in ~2000 years
     * God forbid anyone finds this in ~2000 years
     */
    @Test
    public void testCalculateAgeAlive() {
        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("ird=\"123456879\"");

        Donor testDonor = null;
        try {
            testDonor = new Donor(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        int age = testDonor.calculateAge();
        int year = LocalDate.now().getYear() - 2000;
        assertEquals(age, year);
    }


    /**
     * Tests the calculated age if the user is dead
     * Implicitly tests the calculation before the users birthday
     */
    @Test
    public void testCalculateAgeDead() {
        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"02-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Donor testDonor = null;
        try {
            testDonor = new Donor(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        int age = testDonor.calculateAge();
        assertEquals(age, 49);
    }

    /**
     * Tests the calculated age if it is the donors birthday
     */
    @Test
    public void testCalculateAgeOnBirthday() {
        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Donor testDonor = null;
        try {
            testDonor = new Donor(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        int age = testDonor.calculateAge();
        assertEquals(age, 50);
    }


    @Test
    public void testGetBloodPressure() {
        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Donor testDonor = null;
        try {
            testDonor = new Donor(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testDonor.setBloodPressureSystolic(120);
        testDonor.setBloodPressureDiastolic(80);

        String bloodPressure = testDonor.getBloodPressure();
        assertEquals(bloodPressure, "120/80");
    }
}