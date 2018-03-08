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

    /**
     * Test to add check that an existing organ can't be added
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

        Set<Organ> someOrgans = new HashSet<>();
        someOrgans.add(Organ.BONE);

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
}