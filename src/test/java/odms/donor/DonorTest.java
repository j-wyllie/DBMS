package odms.donor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DonorTest {

    /**
     * Test to create a valid user
     * @throws InstantiationError
     */
    @Test
    public void testCreateUser() throws IllegalArgumentException {
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

        Set<Organ> someOrgans = new HashSet<>();
        someOrgans.add(Organ.BONE);
        someOrgans.add(Organ.HEART);
        someOrgans.add(Organ.CORNEA);
        testDonor.addOrgans(someOrgans);

        assertEquals(someOrgans, testDonor.getOrgans());
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

        Set<Organ> someOrgans = new HashSet<>();
        someOrgans.add(Organ.BONE);
        someOrgans.add(Organ.HEART);
        someOrgans.add(Organ.CORNEA);
        testDonor.addOrgans(someOrgans);

        Set<Organ> removeOrgans = new HashSet<>();
        removeOrgans.add(Organ.BONE);
        removeOrgans.add(Organ.HEART);
        testDonor.removeOrgans(removeOrgans);

        assertEquals(testDonor.getOrgans(), new HashSet<>(Arrays.asList(Organ.CORNEA)));
    }

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

        Set<Organ> someOrgans = new HashSet<>();
        someOrgans.add(Organ.BONE);

        testDonor.addOrgans(someOrgans);
        testDonor.addOrgans(someOrgans);

    }

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
}