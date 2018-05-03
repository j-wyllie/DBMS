package odms.profile;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import odms.cli.CommandUtils;
import odms.medications.Drug;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ProfileTest {

    /**
     * Test to create a valid user
     * @throws IllegalArgumentException bad arguments
     */
    @Test
    public void testCreateBasicUser() throws IllegalArgumentException {
        Profile testProfile = null;

        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            //pass
        }

        assertNotNull(testProfile);
    }

    @Test
    public void testCreateBasicUserRawData() throws IllegalArgumentException {
        Profile testProfile = null;

        try {
            testProfile = new Profile("John", "Smithy", "17-01-1998", 123456789);
        } catch (IllegalArgumentException e) {
            //pass
        }

        assertNotNull(testProfile);
    }

    /**
     * Test creating a users with every attribute
     * @throws IllegalArgumentException bad arguments
     */
    @Test
    public void testCreateFullUser() throws IllegalArgumentException {
        Profile testProfile = null;

        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");
        profileAttr.add("dod=\"6-3-2018\"");
        profileAttr.add("gender=\"male\"");
        profileAttr.add("height=\"86.0\"");
        profileAttr.add("weight=\"72.0\"");
        profileAttr.add("blood-type=\"O+\"");
        profileAttr.add("address=\"Riccarton\"");
        profileAttr.add("region=\"Christchurch\"");

        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            //pass
        }

        assertNotNull(testProfile);
    }

    /**
     * Test to create an invalid user with no IRD no
     * @throws IllegalArgumentException bad arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNoIRD() throws IllegalArgumentException {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("\"given-names=John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");

        Profile profileOnlyAttr = new Profile(profileAttr);
    }

    /**
     * Test to create an invalid user with no DOB
     * @throws IllegalArgumentException bad arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNoDOB() throws IllegalArgumentException {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("ird=\"123456879\"");;

        Profile profileOnlyAttr = new Profile(profileAttr);
    }

    /**
     * Test to create an invalid user with no first-name
     * @throws IllegalArgumentException bad arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNoFirstName() throws IllegalArgumentException {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile profileOnlyAttr = new Profile(profileAttr);
    }

    /**
     * Test to create an invalid user with no last name
     * @throws IllegalArgumentException bad arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNoLastName() throws IllegalArgumentException {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile profileOnlyAttr = new Profile(profileAttr);
    }

    /**
     * Test to create an invalid user with an incorrectly spelt attribute
     * @throws IllegalArgumentException bad arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserBadAttr() throws IllegalArgumentException {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-na=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile profileOnlyAttr = new Profile(profileAttr);
    }

    /**
     * Test the ability to add organs to the list of donatable organs
     */
    @Test
    public void testAddDonatableOrgans() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        Set<String> someOrgans = new HashSet<>();
        someOrgans.add("bone");
        someOrgans.add("heart");
        someOrgans.add("cornea");
        testProfile.addDonations(someOrgans);

        Set<Organ> expected = new HashSet<>();
        expected.add(Organ.BONE);
        expected.add(Organ.HEART);
        expected.add(Organ.CORNEA);

        assertEquals(expected, testProfile.getDonatedOrgans());
    }

    /**
     * Test the ability to add an organ to the list of organs that the profile can donate
     */
    @Test
    public void testAddOrgans() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile;

        try {
            testProfile = new Profile(profileAttr);

            testProfile.setRegistered(true);

            Set<String> someOrgans = new HashSet<>();
            someOrgans.add("bone");
            someOrgans.add("heart");
            someOrgans.add("cornea");
            testProfile.addOrgansDonate(someOrgans);

            Set<Organ> expected = new HashSet<>();
            expected.add(Organ.BONE);
            expected.add(Organ.HEART);
            expected.add(Organ.CORNEA);

            assertEquals(expected, testProfile.getOrgans());

        } catch (IllegalArgumentException | OrganConflictException e) {
            // pass
        }
    }

    @Test
    public void testAddOrgansFromString() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile;

        try {
            testProfile = new Profile(profileAttr);

            testProfile.setRegistered(true);
            testProfile.addOrgansFromString("bone, heart, cornea");

            Set<Organ> expected = new HashSet<>();
            expected.add(Organ.BONE);
            expected.add(Organ.HEART);
            expected.add(Organ.CORNEA);

            String expectedString = "heart, bone, cornea";
            Set<String> expectedStrings = new HashSet<>(Arrays.asList(expectedString.split(", ")));
            Set<String> outputStrings = new HashSet<>(
                    Arrays.asList(testProfile.getOrgansAsCSV().split(", ")));

            assertEquals(expected, testProfile.getOrgans());
            assertEquals(expectedStrings, outputStrings);
        } catch (IllegalArgumentException | OrganConflictException e) {
            // pass
        }
    }


    @Test
    public void testAddDonationsFromString() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testProfile.setRegistered(true);
        testProfile.addDonationFromString("bone, heart, cornea");

        Set<Organ> expected = new HashSet<>();
        expected.add(Organ.BONE);
        expected.add(Organ.HEART);
        expected.add(Organ.CORNEA);

        String expectedString = "heart, bone, cornea";
        Set<String> expectedStrings = new HashSet<>(Arrays.asList(expectedString.split(", ")));
        Set<String> outputStrings = new HashSet<>(Arrays.asList(testProfile.getDonationsAsCSV().split(", ")));

        assertEquals(expected, testProfile.getDonatedOrgans());
        assertEquals(expectedStrings, outputStrings);
    }

    @Test
    public void testAddDiseases() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testProfile.setRegistered(true);
        testProfile.addChronicDiseases("cancer, more cancer, even more cancer");

        Set<String> expected = new HashSet<>();
        expected.add("cancer");
        expected.add("more cancer");
        expected.add("even more cancer");

        String expectedString = "cancer, more cancer, even more cancer";
        Set<String> expectedStrings = new HashSet<>(Arrays.asList(expectedString.split(", ")));
        Set<String> outputStrings = new HashSet<>(Arrays.asList(testProfile.getChronicDiseasesAsCSV().split(", ")));

        assertEquals(expected, testProfile.getChronicDiseases());
        assertEquals(expectedStrings, outputStrings);
    }

    /**
     * Test the ability to remove organs from the list of donatable organs
     */
    @Test
    public void testRemoveDonatableOrgans() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        Set<String> someOrgans = new HashSet<>();
        someOrgans.add("bone");
        someOrgans.add("heart");
        someOrgans.add("cornea");
        testProfile.addDonations(someOrgans);

        Set<String> removedOrgans = new HashSet<>();
        removedOrgans.add("bone");
        removedOrgans.add("heart");
        testProfile.removeDonations(removedOrgans);

        Set<Organ> expected = new HashSet<>();
        expected.add(Organ.CORNEA);

        assertEquals(testProfile.getDonatedOrgans(), expected);
    }

    /*
     * Tests the ability to remove an organ from the list of organs that the
     * profile has donated
     */
    @Test
    public void testRemoveOrgans() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile;

        try {
            testProfile = new Profile(profileAttr);

            testProfile.setRegistered(true);

            Set<String> someOrgans = new HashSet<>();
            someOrgans.add("bone");
            someOrgans.add("heart");
            someOrgans.add("cornea");
            testProfile.addOrgansDonate(someOrgans);

            Set<String> removedOrgans = new HashSet<>();
            removedOrgans.add("bone");
            removedOrgans.add("heart");
            testProfile.removeOrgans(removedOrgans);

            Set<Organ> expected = new HashSet<>();
            expected.add(Organ.CORNEA);

            assertEquals(testProfile.getOrgans(), expected);

        } catch (IllegalArgumentException | OrganConflictException e) {
            // pass
        }
    }

    /**
     * Tests that when an existing organ is added it does not duplicate it
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddExistingOrgan() throws IllegalArgumentException {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile;

        try {
            testProfile = new Profile(profileAttr);

            testProfile.setRegistered(true);

            Set<String> someOrgans = new HashSet<>();
            someOrgans.add("bone");

            testProfile.addOrgansDonate(someOrgans);
            testProfile.addOrgansDonate(someOrgans);

        } catch (OrganConflictException e) {
            // pass
        }
    }

    /**
     * Check that the property changes are recorded
     */
    @Test
    public void testPropertyChangeEvent() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        assertEquals(testProfile.getUpdateActions().size(), 4);
    }

    /**
     * Testing the bmi to check that it is accurate
     */
    @Test
    public void testCalculateBMI() {
        DecimalFormat df = new DecimalFormat("#.##");

        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");
        profileAttr.add("weight=\"72.0\"");
        profileAttr.add("height=\"1.75\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        double bmi = testProfile.calculateBMI();
        assertEquals("23.51", df.format(bmi));
    }

    /**
     * Tests the calculated age if the user is alive
     * Also implicitly tests the calculation after the users birthday
     * This CommandUtilsTest will depreciate in ~2000 years
     * God forbid anyone finds this in ~2000 years
     */
    @Test
    public void testCalculateAgeAlive() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"01-01-2000\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        int age = testProfile.calculateAge();
        int year = LocalDate.now().getYear() - 2000;
        assertEquals(age, year);
    }


    /**
     * Tests the calculated age if the user is dead
     * Implicitly tests the calculation before the users birthday
     */
    @Test
    public void testCalculateAgeDead() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"02-01-2000\"");
        profileAttr.add("dod=\"01-01-2050\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        int age = testProfile.calculateAge();
        assertEquals(age, 49);
    }

    /**
     * Tests the calculated age if it is the profiles birthday
     */
    @Test
    public void testCalculateAgeOnBirthday() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"01-01-2000\"");
        profileAttr.add("dod=\"01-01-2050\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        int age = testProfile.calculateAge();
        assertEquals(age, 50);
    }


    @Test
    public void testGetBloodPressure() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"01-01-2000\"");
        profileAttr.add("dod=\"01-01-2050\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testProfile.setBloodPressureSystolic(120);
        testProfile.setBloodPressureDiastolic(80);

        String bloodPressure = testProfile.getBloodPressure();
        assertEquals(bloodPressure, "120/80");
    }

    /**
     * Tests adding some procedures to the profile and getting all procedures in the process
     */
    @Test
    public void testAddProcedureGetAllProcedures() {
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Photorefractive keratectomy", "4-9-2018", "Will correct the patients vision");

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testProfile.addProcedure(procedure0);
        testProfile.addProcedure(procedure1);

        assertEquals(testProfile.getAllProcedures().contains(procedure0), true);
        assertEquals(testProfile.getAllProcedures().contains(procedure1), true);
    }

    /**
     * Tests deleting a procedure of the user
     */
    @Test
    public void testDeleteProcedure() {
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Photorefractive keratectomy", "4-9-2018", "Will correct the patients vision");

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testProfile.addProcedure(procedure0);
        testProfile.addProcedure(procedure1);

        testProfile.removeProcedure(procedure0);

        assertEquals(testProfile.getAllProcedures().size(), 1);
    }

    /**
     * Tests getting all previous procedures
     */
    @Test
    public void testGetPreviousProcedure() {
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Aputation", "2-11-2012", "Removed all the patients limbs");
        Procedure procedure2 = new Procedure("Photorefractive keratectomy", "4-9-2018", "Will correct the patients vision");

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testProfile.addProcedure(procedure0);
        testProfile.addProcedure(procedure1);
        testProfile.addProcedure(procedure2);

        assertEquals(testProfile.getPreviousProcedures().contains(procedure1), true);
        assertEquals(testProfile.getPreviousProcedures().size(), 1);
    }

    /**
     * Tests getting all pending procedures
     */
    @Test
    public void testGetPendingProcedure() {
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Aputation", "2-11-2012", "Removed all the patients limbs");
        Procedure procedure2 = new Procedure("Photorefractive keratectomy", "4-9-2018", "Will correct the patients vision");

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testProfile.addProcedure(procedure0);
        testProfile.addProcedure(procedure1);
        testProfile.addProcedure(procedure2);

        assertEquals(testProfile.getPendingProcedures().contains(procedure0), true);
        assertEquals(testProfile.getPendingProcedures().contains(procedure2), true);
        assertEquals(testProfile.getPendingProcedures().size(), 2);
    }

    /**
     * Tests the isPreviousProcedure function of Profile
     */
    @Test
    public void testIsPendingProcedure() {
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Photorefractive keratectomy", "4-9-2012", "Will correct the patients vision");

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testProfile.addProcedure(procedure0);
        testProfile.addProcedure(procedure1);

        assertEquals(testProfile.isPreviousProcedure(procedure0), false);
        assertEquals(testProfile.isPreviousProcedure(procedure1), true);
    }

    /**
     * Tests that affected organs can be added correctly to a procedure
     */
    @Test
    public void testAddAffectedOrgans() {
        // create a profile and add a procedure
        Procedure procedure = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testProfile.addProcedure(procedure);

        testProfile.addDonationFromString("heart");
        testProfile.getAllProcedures().get(0).addAffectedOrgan(testProfile, Organ.HEART);

        assertEquals(testProfile.getAllProcedures().get(0).getOrgansAffected().contains(Organ.HEART), true);
    }

    /**
     * Tests that adding an affected organ to a procedure that from a profile that does not have that organ listed as donated throws the correct exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalAddAffectedOrgan() {
        // create a profile and add a procedure
        Procedure procedure = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testProfile.addProcedure(procedure);

        Organ testOrgan = Organ.HEART;
        testProfile.getAllProcedures().get(0).addAffectedOrgan(testProfile, testOrgan);

        testProfile.getAllProcedures().get(0).getOrgansAffected().contains(Organ.HEART);
    }

    /**
     * Tests that affected organs can be added removed from a procedure
     */
    @Test
    public void testRemoveAffectedOrgans() {
        // create a profile and add a procedure
        Procedure procedure = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testProfile.addProcedure(procedure);

        // add heart and liver
        testProfile.addDonationFromString("heart, liver");
        testProfile.getAllProcedures().get(0).addAffectedOrgan(testProfile, Organ.HEART);
        testProfile.getAllProcedures().get(0).addAffectedOrgan(testProfile, Organ.LIVER);

        // remove the heart
        testProfile.getAllProcedures().get(0).removeAffectedOrgen(Organ.HEART);

        // test that heart has been removed
        assertEquals(testProfile.getAllProcedures().get(0).getOrgansAffected().contains(Organ.HEART), false);
        assertEquals(testProfile.getAllProcedures().get(0).getOrgansAffected().contains(Organ.LIVER), true);
    }





    @Test
    public void testAddDrug(){
        Drug drug1 = new Drug("acetaminophen");
        Drug drug2 = new Drug("paracetamol");

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");
        Profile donor1 = new Profile(donorAttr);


        LocalDateTime currentTime = LocalDateTime.now();

        //Simple to test as drugs can have any name at the moment.

        try {
            donor1.addDrug(drug1);
            assertEquals(donor1.getCurrentMedications().get(0).getDrugName(), "acetaminophen");
            assertEquals(donor1.getMedicationTimestamps().get(0), ("acetaminophen added on " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));

            donor1.addDrug(drug2);
            assertEquals(donor1.getCurrentMedications().get(1).getDrugName(), "paracetamol");
            assertEquals(donor1.getMedicationTimestamps().get(1), ("paracetamol added on " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
            assertEquals(donor1.getLastUpdated().format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")),
                    currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")));

        } catch (IllegalArgumentException e){
            fail();
        }
    }

    @Test
    public void testDeleteDrug(){
        Drug drug1 = new Drug("acetaminophen");
        Drug drug2 = new Drug("paracetamol");

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");
        Profile donor1 = new Profile(donorAttr);

        LocalDateTime currentTime = LocalDateTime.now();

        donor1.addDrug(drug1);
        donor1.addDrug(drug2);

        try{
            assertEquals(donor1.getCurrentMedications().size(), 2);
            donor1.deleteDrug(drug1);
            assertEquals(donor1.getCurrentMedications().get(0).getDrugName(), "paracetamol");
            assertEquals(donor1.getCurrentMedications().size(), 1);
            assertEquals(donor1.getMedicationTimestamps().get(2), "acetaminophen removed on " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

            assertEquals(donor1.getCurrentMedications().size(), 1);
            donor1.deleteDrug(drug1);
            assertEquals(donor1.getCurrentMedications().size(), 1);

            donor1.deleteDrug(drug2);
            assertEquals(donor1.getCurrentMedications().size(), 0);
            assertEquals(donor1.getMedicationTimestamps().get(3), "paracetamol removed on " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            assertEquals(donor1.getLastUpdated().format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")),
                    currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void testMoveDrugToHistory(){
        Drug drug1 = new Drug("acetaminophen");
        Drug drug2 = new Drug("paracetamol");

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");
        Profile donor1 = new Profile(donorAttr);

        LocalDateTime currentTime = LocalDateTime.now();

        donor1.addDrug(drug1);
        donor1.addDrug(drug2);

        try {
            assertEquals(donor1.getCurrentMedications().size(), 2);
            donor1.moveDrugToHistory(drug1);
            assertEquals(donor1.getCurrentMedications().size(), 1);
            assertEquals(donor1.getMedicationTimestamps().get(2), "acetaminophen stopped on " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            assertEquals(donor1.getLastUpdated().format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")),
                    currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")));

            donor1.moveDrugToHistory(drug2);
            assertEquals(donor1.getCurrentMedications().size(), 0);
            assertEquals(donor1.getMedicationTimestamps().get(3), "paracetamol stopped on " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            assertEquals(donor1.getLastUpdated().format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")),
                    currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")));

            donor1.moveDrugToHistory(drug1);
            assertEquals(donor1.getMedicationTimestamps().size(), 4);

        } catch (Exception e){
            fail();
        }

    }

    @Test
    public  void TestMoveDrugToCurrent() {
        Drug drug1 = new Drug("acetaminophen");
        Drug drug2 = new Drug("paracetamol");

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");
        Profile donor1 = new Profile(donorAttr);

        LocalDateTime currentTime = LocalDateTime.now();

        donor1.addDrug(drug1);
        donor1.addDrug(drug2);

        try {
            assertEquals(donor1.getCurrentMedications().size(), 2);
            donor1.moveDrugToHistory(drug1);
            donor1.moveDrugToHistory(drug2);
            assertEquals(donor1.getCurrentMedications().size(), 0);
            assertEquals(donor1.getHistoryOfMedication().size(), 2);

            donor1.moveDrugToCurrent(drug1);
            assertEquals(donor1.getCurrentMedications().size(), 1);
            assertEquals(donor1.getHistoryOfMedication().size(), 1);
            assertEquals(donor1.getMedicationTimestamps().get(4), "acetaminophen added back to current list on " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            assertEquals(donor1.getLastUpdated().format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")),
                    currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")));

            donor1.moveDrugToCurrent(drug2);
            assertEquals(donor1.getCurrentMedications().size(), 2);
            assertEquals(donor1.getHistoryOfMedication().size(), 0);
            assertEquals(donor1.getMedicationTimestamps().get(5), "paracetamol added back to current list on " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            assertEquals(donor1.getLastUpdated().format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")),
                    currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")));

            donor1.moveDrugToCurrent(drug1);
            assertEquals(donor1.getMedicationTimestamps().size(), 6);

        } catch (Exception e){
            fail();
        }
    }


    /**
     * Tests adding a condition to the user and getting all conditions in the process
     */
    @Test
    public void testAddConditionandGetAllConditions() {
        Condition condition = new Condition("aids", "18-7-1997", "15-09-2014", false);
        ArrayList<String> donorAttr = new ArrayList<String>();

        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testProfile.addCondition(condition);
        assert testProfile.getAllConditions().contains(condition);
    }

    /**
     * Tests deleting a condition of the user
     */
    @Test
    public void testDeleteCondition() {
        Condition condition = new Condition("aids", "18-7-1997", "15-9-2013", false);
        ArrayList<String> donorAttr = new ArrayList<String>();

        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testDonor = null;
        try {
            testDonor = new Profile(donorAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }


        testDonor.addCondition(condition);
        testDonor.removeCondition(condition);

        assert testDonor.getAllConditions().size() == 0;
    }

    /**
     * Tests getting an array list of all cured conditions from the user
     */
    @Test
    public void testGetCuredConditions() {
        Condition condition0 = new Condition("aids", "18-7-1997", true);
        ArrayList<String> donorAttr0 = new ArrayList<String>();

        donorAttr0.add("given-names=\"John\"");
        donorAttr0.add("last-names=\"Smithy Smith Face\"");
        donorAttr0.add("dob=\"01-01-2000\"");
        donorAttr0.add("dod=\"01-01-2050\"");
        donorAttr0.add("ird=\"123456879\"");

        Profile testDonor = null;
        try {
            testDonor = new Profile(donorAttr0);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testDonor.addCondition(condition0);

        Condition condition1 = new Condition("aids", "28-7-1994", "15-9-2013", false);
        ArrayList<String> donorAttr1 = new ArrayList<String>();

        donorAttr1.add("given-names=\"John\"");
        donorAttr1.add("last-names=\"Smithy Smith Face\"");
        donorAttr1.add("dob=\"01-01-2000\"");
        donorAttr1.add("dod=\"01-01-2050\"");
        donorAttr1.add("ird=\"123456879\"");

        testDonor.addCondition(condition1);

        assert testDonor.getCuredConditions().size() == 1 && testDonor.getCuredConditions().contains(condition1);
    }

    @Test
    public void testRequiredOrganHistory() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"01-01-2000\"");
        profileAttr.add("dod=\"01-01-2050\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }
        Set<String> testSet = new HashSet<String>();
        testSet.add("Heart");
        testProfile.setOrgansRequired(testSet);
        assertTrue(CommandUtils.currentSessionHistory.get(CommandUtils.historyPosition).contains("HEART"));
    }
}
