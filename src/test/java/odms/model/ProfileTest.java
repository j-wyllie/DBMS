package odms.model;

import javafx.beans.property.SimpleStringProperty;
import odms.controller.history.HistoryController;
import odms.model.enums.OrganEnum;
import odms.model.medications.Drug;
import odms.model.profile.Condition;
import odms.model.profile.OrganConflictException;
import odms.model.profile.Procedure;
import odms.model.profile.Profile;
import org.junit.Before;
import org.junit.Test;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.Assert.*;

public class ProfileTest {

    private ArrayList<String> profileAttr;

    @Before
    public void setup() {
        profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");
    }

    /**
     * Test to create a valid user
     *
     * @throws IllegalArgumentException bad arguments
     */
    @Test
    public void testCreateBasicUser() {
        Profile testProfile = new Profile(profileAttr);

        assertNotNull(testProfile);
    }

    @Test
    public void testCreateBasicUserRawData() {
        Profile testProfile = new Profile("John", "Smithy", "17-01-1998", 123456789);

        assertNotNull(testProfile);
    }

    /**
     * Test creating a users with every attribute
     *
     * @throws IllegalArgumentException bad arguments
     */
    @Test
    public void testCreateFullUser() {
        // TODO add all missing attributes

        profileAttr.add("dod=\"6-3-2018\"");
        profileAttr.add("gender=\"male\"");
        profileAttr.add("height=\"86.0\"");
        profileAttr.add("weight=\"72.0\"");
        profileAttr.add("blood-type=\"O+\"");
        profileAttr.add("address=\"Riccarton\"");
        profileAttr.add("region=\"Christchurch\"");

        Profile testProfile = new Profile(profileAttr);

        assertNotNull(testProfile);
    }

    /**
     * Test to create an invalid user with no IRD no
     *
     * @throws IllegalArgumentException bad arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNoIRD() throws IllegalArgumentException {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("\"given-names=John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");

        new Profile(profileAttr);
    }

    /**
     * Test to create an invalid user with no DOB
     *
     * @throws IllegalArgumentException bad arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNoDOB() throws IllegalArgumentException {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("ird=\"123456879\"");

        new Profile(profileAttr);
    }

    /**
     * Test to create an invalid user with no first-name
     *
     * @throws IllegalArgumentException bad arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNoFirstName() throws IllegalArgumentException {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        new Profile(profileAttr);
    }

    /**
     * Test to create an invalid user with no last name
     *
     * @throws IllegalArgumentException bad arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNoLastName() throws IllegalArgumentException {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        new Profile(profileAttr);
    }

    /**
     * Test to create an invalid user with an incorrectly spelt attribute
     *
     * @throws IllegalArgumentException bad arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserBadAttr() throws IllegalArgumentException {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-na=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        new Profile(profileAttr);
    }

    /**
     * Test the ability to add organs to the list of donatable organs
     */
    @Test
    public void testAddDonatableOrgans() {
        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        List<String> someOrgans = new ArrayList<>();
        someOrgans.add("bone");
        someOrgans.add("heart");
        someOrgans.add("cornea");
        testProfile.addOrgansDonated(OrganEnum.stringListToOrganSet(someOrgans));
        testProfile.setId(9999);

        Set<OrganEnum> expected = new HashSet<>();
        expected.add(OrganEnum.BONE);
        expected.add(OrganEnum.HEART);
        expected.add(OrganEnum.CORNEA);

        assertEquals(expected, testProfile.getOrgansDonated());
    }

    /**
     * Test the ability to add an organ to the list of organs that the profile can donate
     *
     * @throws OrganConflictException if an organ conflict exists
     */
    @Test
    public void testAddOrgans() throws OrganConflictException {
        Profile testProfile = new Profile(profileAttr);

        testProfile.setDonor(true);
        testProfile.setId(9999);

        List<String> someOrgans = new ArrayList<>();
        someOrgans.add("bone");
        someOrgans.add("heart");
        someOrgans.add("cornea");
        testProfile.addOrgansDonating(OrganEnum.stringListToOrganSet(someOrgans));

        Set<OrganEnum> expected = new HashSet<>();
        expected.add(OrganEnum.BONE);
        expected.add(OrganEnum.HEART);
        expected.add(OrganEnum.CORNEA);

        assertEquals(expected, testProfile.getOrgansDonating());
    }

    /**
     * Tests adding organs donating to profile from organ string.
     *
     * @throws OrganConflictException if an organ conflict exists
     */
    @Test
    public void testAddOrgansDonatingFromString() throws OrganConflictException {
        Profile testProfile = new Profile(profileAttr);

        testProfile.setDonor(true);
        testProfile.setId(9999);

        List<String> someOrgans = new ArrayList<>();
        someOrgans.add("bone");
        someOrgans.add("heart");
        someOrgans.add("cornea");
        testProfile.addOrgansDonating(OrganEnum.stringListToOrganSet(someOrgans));

        Set<OrganEnum> expected = new HashSet<>();
        expected.add(OrganEnum.BONE);
        expected.add(OrganEnum.HEART);
        expected.add(OrganEnum.CORNEA);

        Set<String> expectedStrings = new HashSet<>(Arrays.asList(
                "Heart, Bone, Cornea".split(", "))
        );
        Set<String> outputStrings = new HashSet<>(Arrays.asList(
                OrganEnum.organSetToString(
                        testProfile.getOrgansDonating()).split(", "))
        );

        assertEquals(expected, testProfile.getOrgansDonating());
        assertEquals(expectedStrings, outputStrings);
    }

    /**
     * Tests adding diseases to a profile.
     */
    @Test
    public void testAddDiseases() {
        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        testProfile.setDonor(true);
        testProfile.setChronicDiseases(
                new HashSet<>(
                        Arrays.asList("cancer, more cancer, even more cancer".split(", "))
                )
        );

        Set<String> expected = new HashSet<>();
        expected.add("cancer");
        expected.add("more cancer");
        expected.add("even more cancer");

        String expectedString = "cancer, more cancer, even more cancer";

        Set<String> expectedStrings = new HashSet<>(Arrays.asList(expectedString.split(", ")));
        Set<String> outputStrings = testProfile.getChronicDiseases();

        assertEquals(expected, testProfile.getChronicDiseases());
        assertEquals(expectedStrings, outputStrings);
    }

    /**
     * Test the ability to remove organs from the list of donating organs
     *
     * @throws OrganConflictException if an organ conflict exists
     */
    @Test
    public void testRemoveOrgansDonating() throws OrganConflictException {
        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        List<String> someOrgans = new ArrayList<>();
        someOrgans.add("bone");
        someOrgans.add("heart");
        someOrgans.add("cornea");
        testProfile.addOrgansDonating(OrganEnum.stringListToOrganSet(someOrgans));

        List<String> removedOrgans = new ArrayList<>();
        removedOrgans.add("bone");
        removedOrgans.add("heart");
        testProfile.removeOrgansDonating(OrganEnum.stringListToOrganSet(removedOrgans));

        Set<OrganEnum> expected = new HashSet<>();
        expected.add(OrganEnum.CORNEA);

        assertEquals(expected, testProfile.getOrgansDonating());
    }

    /**
     * Tests the ability to remove an organ from the list of organs that the profile has donated
     */
    @Test
    public void testRemoveOrgans() {
        Profile testProfile = new Profile(profileAttr);

        testProfile.setDonor(true);
        testProfile.setId(9999);

        List<String> addOrgans = new ArrayList<>();
        addOrgans.add("bone");
        addOrgans.add("heart");
        addOrgans.add("cornea");
        testProfile.addOrgansDonated(OrganEnum.stringListToOrganSet(addOrgans));

        List<String> removedOrgans = new ArrayList<>();
        removedOrgans.add("bone");
        removedOrgans.add("heart");
        testProfile.removeOrgansDonated(OrganEnum.stringListToOrganSet(removedOrgans));

        Set<OrganEnum> expected = new HashSet<>();
        expected.add(OrganEnum.CORNEA);

        assertEquals(expected, testProfile.getOrgansDonated());
    }

    /**
     * Test that a profile cannot donate an organ they have received.
     *
     * @throws OrganConflictException if an organ conflict exists
     */
    @Test(expected = OrganConflictException.class)
    public void testOrganDonateReceiveConflict() throws OrganConflictException {
        Profile testProfile;

        testProfile = new Profile(profileAttr);
        testProfile.setDonor(true);
        testProfile.setId(9999);

        HashSet<OrganEnum> organs = new HashSet<>();
        organs.add(OrganEnum.BONE);
        organs.add(OrganEnum.INTESTINE);

        testProfile.addOrgansReceived(organs);
        testProfile.addOrgansDonating(organs);
    }

    /**
     * Tests that when an existing organ is added it does not duplicate it
     *
     * @throws IllegalArgumentException if a bad argument is used
     * @throws OrganConflictException   if an organ conflict exists
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddExistingOrgan() throws IllegalArgumentException, OrganConflictException {
        Profile testProfile = new Profile(profileAttr);

        testProfile.setId(9999);
        testProfile.setDonor(true);

        List<String> someOrgans = new ArrayList<>();
        someOrgans.add("bone");

        testProfile.addOrgansDonating(OrganEnum.stringListToOrganSet(someOrgans));
        testProfile.addOrgansDonating(OrganEnum.stringListToOrganSet(someOrgans));
    }

    /**
     * Check that the property changes are recorded
     */
    @Test
    public void testPropertyChangeEvent() {
        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        assertEquals(testProfile.getUpdateActions().size(), 4);
    }

    /**
     * Testing the bmi to check that it is accurate
     */
    @Test
    public void testCalculateBMI() {
        DecimalFormat df = new DecimalFormat("#.##");

        profileAttr.add("weight=\"72.0\"");
        profileAttr.add("height=\"1.75\"");

        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        double bmi = testProfile.calculateBMI();
        assertEquals("23.51", df.format(bmi));
    }

    /**
     * Tests the calculated age if the user is alive Also implicitly tests the calculation after the
     * users birthday This test will depreciate in ~2000 years God forbid anyone finds this in ~2000
     * years
     */
    @Test
    public void testCalculateAgeAlive() {
        Integer birthYear = 1998;
        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        Integer age = testProfile.calculateAge();
        Integer year = LocalDate.now().getYear() - birthYear;
        assertEquals(age, year);
    }

    /**
     * Tests the calculated age if the user is dead Implicitly tests the calculation before the
     * users birthday
     */
    @Test
    public void testCalculateAgeDead() {
        profileAttr.add("dod=\"01-01-2050\"");
        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        int age = testProfile.calculateAge();
        assertEquals(age, 51);
    }

    /**
     * Tests the calculated age if the deceased profiles DOD falls on their birthday.
     */
    @Test
    public void testCalculateAgeOnBirthday() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"01-01-2000\"");
        profileAttr.add("dod=\"01-01-2050\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        int age = testProfile.calculateAge();
        assertEquals(age, 50);
    }

    /**
     * Tests if the profiles blood pressure is returned with the correct values in the expected
     * format.
     */
    @Test
    public void testGetBloodPressure() {
        Profile testProfile = new Profile(profileAttr);

        testProfile.setBloodPressureSystolic(120);
        testProfile.setBloodPressureDiastolic(80);

        String bloodPressure = testProfile.getBloodPressure();
        assertEquals(bloodPressure, "120/80");
    }

    /**
     * Tests adding some procedures to the profile and getting all procedures in the process
     *
     * @throws IllegalArgumentException if a bad attribute is used.
     */
    @Test
    public void testAddProcedureGetAllProcedures() {
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018",
                "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Photorefractive keratectomy", "4-9-2018",
                "Will correct the patients vision");

        Profile testProfile = new Profile(profileAttr);

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
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018",
                "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Photorefractive keratectomy", "4-9-2018",
                "Will correct the patients vision");

        Profile testProfile = new Profile(profileAttr);

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
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018",
                "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Aputation", "2-11-2012",
                "Removed all the patients limbs");
        Procedure procedure2 = new Procedure("Photorefractive keratectomy", "4-9-2018",
                "Will correct the patients vision");

        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

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
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018",
                "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Aputation", "2-11-2012",
                "Removed all the patients limbs");
        Procedure procedure2 = new Procedure("Photorefractive keratectomy", "4-9-2018",
                "Will correct the patients vision");

        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        testProfile.addProcedure(procedure0);
        testProfile.addProcedure(procedure1);
        testProfile.addProcedure(procedure2);

        assertEquals(testProfile.getPendingProcedures().contains(procedure0), true);
        assertEquals(testProfile.getPendingProcedures().contains(procedure2), true);
        assertEquals(testProfile.getPendingProcedures().size(), 2);
    }

    /**
     * Tests the isPreviousProcedure function of profile
     */
    @Test
    public void testIsPendingProcedure() {
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018",
                "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Photorefractive keratectomy", "4-9-2012",
                "Will correct the patients vision");

        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        testProfile.addProcedure(procedure0);
        testProfile.addProcedure(procedure1);

        assertEquals(testProfile.isPreviousProcedure(procedure0), false);
        assertEquals(testProfile.isPreviousProcedure(procedure1), true);
    }

    /**
     * Tests that affected organs can be added correctly to a procedure
     *
     * @throws OrganConflictException if an organ conflict exists
     */
    @Test
    public void testAddAffectedOrgans() throws OrganConflictException {
        // create a profile and add a procedure
        Procedure procedure = new Procedure("Appendix Removal", "2-11-2018",
                "Will remove the appendix via key hole surgery");

        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        testProfile.addProcedure(procedure);

        testProfile.addOrgansDonating(new HashSet<>(
                Arrays.asList(OrganEnum.HEART, OrganEnum.LIVER)
        ));
        testProfile.getAllProcedures().get(0).addAffectedOrgan(testProfile, OrganEnum.HEART);

        assertTrue(
                testProfile.getAllProcedures().get(0).getOrgansAffected().contains(OrganEnum.HEART)
        );
    }

    /**
     * Tests that adding an affected organ to a procedure that from a profile that does not have
     * that organ listed as donated throws the correct exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalAddAffectedOrgan() throws IllegalArgumentException {
        // Create a profile and add a procedure
        Procedure procedure = new Procedure(
                "Appendix Removal",
                "2-11-2018",
                "Will remove the appendix via key hole surgery"
        );

        try {
            Profile testProfile = new Profile(profileAttr);
            testProfile.setId(9999);

            testProfile.addProcedure(procedure);

            OrganEnum testOrgan = OrganEnum.HEART;
            testProfile.getAllProcedures().get(0).addAffectedOrgan(testProfile, testOrgan);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Not an organ with donor status on this profile");
            throw e;
        }
    }

    /**
     * Tests that affected organs can be added removed from a procedure
     *
     * @throws OrganConflictException if an organ conflict exists
     */
    @Test
    public void testRemoveAffectedOrgans() throws OrganConflictException {
        // Create a profile and add a procedure
        Procedure procedure = new Procedure(
                "Appendix Removal",
                "2-11-2018",
                "Will remove the appendix via key hole surgery"
        );

        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        testProfile.addProcedure(procedure);

        // Add heart and liver
        testProfile.addOrgansDonating(new HashSet<>(
                Arrays.asList(OrganEnum.HEART, OrganEnum.LIVER)
        ));
        testProfile.getAllProcedures().get(0).addAffectedOrgan(testProfile, OrganEnum.HEART);
        testProfile.getAllProcedures().get(0).addAffectedOrgan(testProfile, OrganEnum.LIVER);

        // Remove the heart
        testProfile.getAllProcedures().get(0).removeAffectedOrgan(OrganEnum.HEART);

        // Test that heart has been removed
        assertFalse(
                testProfile.getAllProcedures().get(0).getOrgansAffected().contains(OrganEnum.HEART)
        );
        assertTrue(
                testProfile.getAllProcedures().get(0).getOrgansAffected().contains(OrganEnum.LIVER)
        );

    }

    /**
     * Test adding drugs to a profile
     */
    @Test
    public void testAddDrug() {
        Drug drugOne = new Drug("acetaminophen");
        Drug drugTwo = new Drug("paracetamol");

        LocalDateTime currentTime = LocalDateTime.now();
        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        testProfile.addDrug(drugOne);
        assertEquals(
                testProfile.getCurrentMedications().get(0).getDrugName(),
                "acetaminophen"
        );
        assertEquals("profile " + testProfile.getId() +
                        " added drug acetaminophen index of 0 at " +
                        currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                testProfile.getMedicationTimestamps().get(0)
        );

        testProfile.addDrug(drugTwo);
        assertEquals(
                testProfile.getCurrentMedications().get(1).getDrugName(),
                "paracetamol"
        );
        assertEquals("profile " + testProfile.getId() +
                        " added drug paracetamol index of 1 at " +
                        currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                testProfile.getMedicationTimestamps().get(1)
        );
        assertEquals(currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")),
                testProfile.getLastUpdated().format(
                        DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"))
        );
    }

    /**
     * Test to delete drugs from a profile.
     */
    @Test
    public void testDeleteDrug() {
        Drug drugOne = new Drug("acetaminophen");
        Drug drugTwo = new Drug("paracetamol");

        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        LocalDateTime currentTime = LocalDateTime.now();

        testProfile.addDrug(drugOne);
        testProfile.addDrug(drugTwo);

        assertEquals(testProfile.getCurrentMedications().size(), 2);
        testProfile.deleteDrug(drugOne);
        assertEquals(
                testProfile.getCurrentMedications().get(0).getDrugName(),
                "paracetamol"
        );
        assertEquals(testProfile.getCurrentMedications().size(), 1);
        assertEquals("profile " + testProfile.getId() +
                        " removed drug acetaminophen index of 0 at " +
                        currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                testProfile.getMedicationTimestamps().get(2)
        );

        assertEquals(testProfile.getCurrentMedications().size(), 1);
        testProfile.deleteDrug(drugOne);
        assertEquals(testProfile.getCurrentMedications().size(), 1);

        testProfile.deleteDrug(drugTwo);
        assertEquals(testProfile.getCurrentMedications().size(), 0);
        assertEquals("profile " + testProfile.getId() +
                        " removed drug paracetamol index of 0 at " +
                        currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                testProfile.getMedicationTimestamps().get(3)
        );
        assertEquals(
                testProfile.getLastUpdated().format(
                        DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")
                ),
                currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"))
        );
    }

    @Test
    public void testMoveDrugToHistory() {
        Drug drugOne = new Drug("acetaminophen");
        Drug drugTwo = new Drug("paracetamol");

        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        LocalDateTime currentTime = LocalDateTime.now();

        testProfile.addDrug(drugOne);
        testProfile.addDrug(drugTwo);

        assertEquals(testProfile.getCurrentMedications().size(), 2);
        testProfile.moveDrugToHistory(drugOne);
        assertEquals(testProfile.getCurrentMedications().size(), 1);
        assertEquals(
                testProfile.getMedicationTimestamps().get(2),
                "profile " +
                        testProfile.getId() +
                        " stopped acetaminophen index of 0 at "
                        + currentTime.format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                )
        );
        assertEquals(
                testProfile.getLastUpdated().format(
                        DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")
                ),
                currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"))
        );

        testProfile.moveDrugToHistory(drugTwo);
        assertEquals(testProfile.getCurrentMedications().size(), 0);
        assertEquals("profile " + testProfile.getId() +
                        " stopped paracetamol index of 1 at " +
                        currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                testProfile.getMedicationTimestamps().get(3));
        assertEquals(
                testProfile.getLastUpdated().format(
                        DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")
                ),
                currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"))
        );

        testProfile.moveDrugToHistory(drugOne);
        assertEquals(testProfile.getMedicationTimestamps().size(), 4);
    }

    /**
     * Tests moving drugs from history back to current.
     */
    @Test
    public void testMoveDrugToCurrent() {
        Drug drugOne = new Drug("acetaminophen");
        Drug drugTwo = new Drug("paracetamol");

        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        LocalDateTime currentTime = LocalDateTime.now();

        testProfile.addDrug(drugOne);
        testProfile.addDrug(drugTwo);

        assertEquals(testProfile.getCurrentMedications().size(), 2);
        testProfile.moveDrugToHistory(drugOne);
        testProfile.moveDrugToHistory(drugTwo);
        assertEquals(testProfile.getCurrentMedications().size(), 0);
        assertEquals(testProfile.getHistoryOfMedication().size(), 2);

        testProfile.moveDrugToCurrent(drugOne);
        assertEquals(testProfile.getCurrentMedications().size(), 1);
        assertEquals(testProfile.getHistoryOfMedication().size(), 1);
        assertEquals("profile " + testProfile.getId() +
                        " started using acetaminophen index of 0 again at " +
                        currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                testProfile.getMedicationTimestamps().get(4)
        );
        assertEquals(
                testProfile.getLastUpdated().format(
                        DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")
                ),
                currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"))
        );

        testProfile.moveDrugToCurrent(drugTwo);
        assertEquals(testProfile.getCurrentMedications().size(), 2);
        assertEquals(testProfile.getHistoryOfMedication().size(), 0);
        assertEquals("profile " + testProfile.getId() +
                        " started using paracetamol index of 1 again at " +
                        currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        ),
                testProfile.getMedicationTimestamps().get(5)
        );
        assertEquals(
                testProfile.getLastUpdated().format(
                        DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")
                ),
                currentTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"))
        );

        testProfile.moveDrugToCurrent(drugOne);
        assertEquals(testProfile.getMedicationTimestamps().size(), 6);
    }

    /**
     * Tests adding a condition to the user and getting all conditions in the process
     */
    @Test
    public void testAddConditionAndGetAllConditions() {
        Condition condition = new Condition("aids", "18-7-1997", "15-09-2014", false);
        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        testProfile.addCondition(condition);

        assertTrue(testProfile.getAllConditions().contains(condition));
    }

    /**
     * Tests deleting a condition of the user
     */
    @Test
    public void testDeleteCondition() {
        Condition condition = new Condition("aids", "18-7-1997", "15-9-2013", false);
        Profile testDonor = new Profile(profileAttr);
        testDonor.addCondition(condition);
        testDonor.removeCondition(condition);

        assertEquals(testDonor.getAllConditions().size(), 0);
    }

    /**
     * Tests getting an array list of all cured conditions from the user
     */
    @Test
    public void testGetCuredConditions() {
        Condition conditionOne = new Condition("Aids", "18-7-1997", true);
        Condition conditionTwo = new Condition("Aids", "28-7-1994", "15-9-2013", false);

        Profile testDonor = new Profile(profileAttr);
        testDonor.addCondition(conditionOne);
        testDonor.addCondition(conditionTwo);

        assertEquals(testDonor.getCuredConditions().size(), 1);
        assertTrue(testDonor.getCuredConditions().contains(conditionTwo));
    }

    /**
     * Tests not being able to create a profile with a future DOB
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFutureDOB() throws IllegalArgumentException {
        ArrayList<String> profileAttr = new ArrayList<>();

        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"01-01-2020\"");
        profileAttr.add("ird=\"123456879\"");

        try {
            new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            assertEquals("Date of birth cannot be a future date", e.getMessage());
            throw e;
        }
    }

    /**
     * Tests not being able to create a profile with a DOD prior to the DOB
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDODBeforeDOB() throws IllegalArgumentException {
        ArrayList<String> profileAttr = new ArrayList<>();

        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"01-01-2000\"");
        profileAttr.add("dod=\"01-01-1950\"");
        profileAttr.add("ird=\"123456879\"");

        try {
            new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            assertEquals("Date of death cannot be before date of birth", e.getMessage());
            throw e;
        }
    }

    /**
     * Test profiles required organ update shows in history.
     */
    @Test
    public void testRequiredOrganHistory() {
        Profile testProfile = new Profile(profileAttr);
        testProfile.setId(9999);

        List<String> someOrgans = new ArrayList<>();
        someOrgans.add("Heart");
        testProfile.addOrgansRequired(OrganEnum.stringListToOrganSet(someOrgans));

        assertTrue(HistoryController.currentSessionHistory
                .get(HistoryController.historyPosition).getHistoryData()
                .contains(OrganEnum.HEART.getNamePlain())
        );
    }

    @Test
    public void testDonorReceiverProperty() {
        ArrayList<String> profileAttr = new ArrayList<>();

        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"01-01-2000\"");
        profileAttr.add("dod=\"01-01-2050\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile = null;
        SimpleStringProperty expected = new SimpleStringProperty();
        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }

        testProfile.setDonor(true);
        testProfile.setReceiver(true);
        expected.setValue("Donor/Receiver");
        assertEquals(expected.toString(), testProfile.donorReceiverProperty().toString());

        testProfile.setDonor(true);
        testProfile.setReceiver(false);
        expected.setValue("Donor");
        assertEquals(expected.toString(), testProfile.donorReceiverProperty().toString());

        testProfile.setDonor(false);
        testProfile.setReceiver(true);
        expected.setValue("Receiver");
        assertEquals(expected.toString(), testProfile.donorReceiverProperty().toString());

        testProfile.setDonor(false);
        testProfile.setReceiver(false);
        expected.setValue(null);
        assertEquals(expected.toString(), testProfile.donorReceiverProperty().toString());
    }
}
