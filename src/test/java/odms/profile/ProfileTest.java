package odms.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import odms.cli.CommandUtils;
import odms.enums.OrganEnum;
import odms.medications.Drug;
import org.junit.Test;

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

        new Profile(profileAttr);
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
        profileAttr.add("ird=\"123456879\"");

        new Profile(profileAttr);
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

        new Profile(profileAttr);
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

        new Profile(profileAttr);
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

        new Profile(profileAttr);
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

        Profile testProfile;

        try {
            testProfile = new Profile(profileAttr);

            List<String> someOrgans = new ArrayList<>();
            someOrgans.add("bone");
            someOrgans.add("heart");
            someOrgans.add("cornea");
            testProfile.addOrgansDonated(OrganEnum.stringListToOrganSet(someOrgans));

            Set<OrganEnum> expected = new HashSet<>();
            expected.add(OrganEnum.BONE);
            expected.add(OrganEnum.HEART);
            expected.add(OrganEnum.CORNEA);

            assertEquals(expected, testProfile.getOrgansDonated());
        } catch (IllegalArgumentException e) {
            // pass
        }

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

            testProfile.setDonor(true);

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

            testProfile.setDonor(true);

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

        Profile testProfile;
        try {
            testProfile = new Profile(profileAttr);

            testProfile.setDonor(true);
            testProfile.addDonationFromString("Heart, Bone, Cornea");

            Set<OrganEnum> expected = new HashSet<>();
            expected.add(OrganEnum.BONE);
            expected.add(OrganEnum.HEART);
            expected.add(OrganEnum.CORNEA);

            String expectedString = "Heart, Bone, Cornea";
            Set<String> expectedStrings = new HashSet<>(Arrays.asList(expectedString.split(", ")));
            Set<String> outputStrings = new HashSet<>(Arrays.asList(
                    OrganEnum.organSetToString(testProfile.getOrgansDonated()).split(", ")));

            assertEquals(expected, testProfile.getOrgansDonated());
            assertEquals(expectedStrings, outputStrings);
        } catch (IllegalArgumentException e) {
            // pass
        }

    }

    @Test
    public void testAddDiseases() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile;
        try {
            testProfile = new Profile(profileAttr);

            testProfile.setDonor(true);
            testProfile.addChronicDiseases("cancer, more cancer, even more cancer");

            Set<String> expected = new HashSet<>();
            expected.add("cancer");
            expected.add("more cancer");
            expected.add("even more cancer");

            String expectedString = "cancer, more cancer, even more cancer";

            Set<String> expectedStrings = new HashSet<>(Arrays.asList(expectedString.split(", ")));
            Set<String> outputStrings = testProfile.getChronicDiseases();

            assertEquals(expected, testProfile.getChronicDiseases());
            assertEquals(expectedStrings, outputStrings);
        } catch (IllegalArgumentException e) {
            // pass
        }

    }

    /**
     * Test the ability to remove organs from the list of donating organs
     */
    @Test
    public void testRemoveOrgansDonating() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile;
        try {
            testProfile = new Profile(profileAttr);

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
        } catch (IllegalArgumentException | OrganConflictException e) {
            // pass
        }

    }

    /**
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

            testProfile.setDonor(true);

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

        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    /**
     * Test that a profile cannot donate an organ they have received.
     */
    @Test(expected = OrganConflictException.class)
    public void testOrganDonateReceiveConflict() throws OrganConflictException {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"17-01-1998\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile;

        testProfile = new Profile(profileAttr);
        testProfile.setDonor(true);

        HashSet<OrganEnum> organs = new HashSet<>();
        organs.add(OrganEnum.BONE);
        organs.add(OrganEnum.INTESTINE);

        testProfile.addOrgansReceived(organs);
        testProfile.addOrgansDonating(organs);
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

            testProfile.setDonor(true);

            List<String> someOrgans = new ArrayList<>();
            someOrgans.add("bone");

            testProfile.addOrgansDonating(OrganEnum.stringListToOrganSet(someOrgans));
            testProfile.addOrgansDonating(OrganEnum.stringListToOrganSet(someOrgans));

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

        Profile testProfile;

        try {
            testProfile = new Profile(profileAttr);

            assertEquals(testProfile.getUpdateActions().size(), 4);
        } catch (IllegalArgumentException e) {
            // pass
        }

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

        Profile testProfile;

        try {
            testProfile = new Profile(profileAttr);

            double bmi = testProfile.calculateBMI();
            assertEquals("23.51", df.format(bmi));
        } catch (IllegalArgumentException e) {
            // pass
        }

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

        Profile testProfile;

        try {
            testProfile = new Profile(profileAttr);

            int age = testProfile.calculateAge();
            int year = LocalDate.now().getYear() - 2000;
            assertEquals(age, year);
        } catch (IllegalArgumentException e) {
            // pass
        }

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

        Profile testProfile;

        try {
            testProfile = new Profile(profileAttr);

            int age = testProfile.calculateAge();
            assertEquals(age, 49);
        } catch (IllegalArgumentException e) {
            // pass
        }

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

        Profile testProfile;
        try {
            testProfile = new Profile(profileAttr);

            int age = testProfile.calculateAge();
            assertEquals(age, 50);
        } catch (IllegalArgumentException e) {
            // pass
        }

    }


    @Test
    public void testGetBloodPressure() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"01-01-2000\"");
        profileAttr.add("dod=\"01-01-2050\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile;
        try {
            testProfile = new Profile(profileAttr);

            testProfile.setBloodPressureSystolic(120);
            testProfile.setBloodPressureDiastolic(80);

            String bloodPressure = testProfile.getBloodPressure();
            assertEquals(bloodPressure, "120/80");
        } catch (IllegalArgumentException e) {
            // pass
        }

    }

    /**
     * Tests adding some procedures to the profile and getting all procedures in the process
     */
    @Test
    public void testAddProcedureGetAllProcedures() {
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Photorefractive keratectomy", "4-9-2018", "Will correct the patients vision");

        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile;
        try {
            testProfile = new Profile(donorAttr);

            testProfile.addProcedure(procedure0);
            testProfile.addProcedure(procedure1);

            assertEquals(testProfile.getAllProcedures().contains(procedure0), true);
            assertEquals(testProfile.getAllProcedures().contains(procedure1), true);
        } catch (IllegalArgumentException e) {
            // pass
        }

    }

    /**
     * Tests deleting a procedure of the user
     */
    @Test
    public void testDeleteProcedure() {
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Photorefractive keratectomy", "4-9-2018", "Will correct the patients vision");

        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile;

        try {
            testProfile = new Profile(donorAttr);

            testProfile.addProcedure(procedure0);
            testProfile.addProcedure(procedure1);

            testProfile.removeProcedure(procedure0);

            assertEquals(testProfile.getAllProcedures().size(), 1);
        } catch (IllegalArgumentException e) {
            // pass
        }

    }

    /**
     * Tests getting all previous procedures
     */
    @Test
    public void testGetPreviousProcedure() {
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Aputation", "2-11-2012", "Removed all the patients limbs");
        Procedure procedure2 = new Procedure("Photorefractive keratectomy", "4-9-2018", "Will correct the patients vision");

        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile;
        try {
            testProfile = new Profile(donorAttr);

            testProfile.addProcedure(procedure0);
            testProfile.addProcedure(procedure1);
            testProfile.addProcedure(procedure2);

            assertEquals(testProfile.getPreviousProcedures().contains(procedure1), true);
            assertEquals(testProfile.getPreviousProcedures().size(), 1);
        } catch (IllegalArgumentException e) {
            // pass
        }

    }

    /**
     * Tests getting all pending procedures
     */
    @Test
    public void testGetPendingProcedure() {
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Aputation", "2-11-2012", "Removed all the patients limbs");
        Procedure procedure2 = new Procedure("Photorefractive keratectomy", "4-9-2018", "Will correct the patients vision");

        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile;
        try {
            testProfile = new Profile(donorAttr);

            testProfile.addProcedure(procedure0);
            testProfile.addProcedure(procedure1);
            testProfile.addProcedure(procedure2);

            assertEquals(testProfile.getPendingProcedures().contains(procedure0), true);
            assertEquals(testProfile.getPendingProcedures().contains(procedure2), true);
            assertEquals(testProfile.getPendingProcedures().size(), 2);
        } catch (IllegalArgumentException e) {
            // pass
        }

    }

    /**
     * Tests the isPreviousProcedure function of Profile
     */
    @Test
    public void testIsPendingProcedure() {
        Procedure procedure0 = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");
        Procedure procedure1 = new Procedure("Photorefractive keratectomy", "4-9-2012", "Will correct the patients vision");

        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile;
        try {
            testProfile = new Profile(donorAttr);

            testProfile.addProcedure(procedure0);
            testProfile.addProcedure(procedure1);

            assertEquals(testProfile.isPreviousProcedure(procedure0), false);
            assertEquals(testProfile.isPreviousProcedure(procedure1), true);
        } catch (IllegalArgumentException e) {
            // pass
        }

    }

    /**
     * Tests that affected organs can be added correctly to a procedure
     */
    @Test
    public void testAddAffectedOrgans() {
        // create a profile and add a procedure
        Procedure procedure = new Procedure("Appendix Removal", "2-11-2018", "Will remove the appendix via key hole surgery");

        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile;
        try {
            testProfile = new Profile(donorAttr);

            testProfile.addProcedure(procedure);

            testProfile.addDonationFromString("heart");
            testProfile.getAllProcedures().get(0).addAffectedOrgan(testProfile, OrganEnum.HEART);

            assertEquals(testProfile.getAllProcedures().get(0).getOrgansAffected().contains(
                    OrganEnum.HEART), true);
        } catch (IllegalArgumentException e) {
            // pass
        }

    }

    /**
     * Tests that adding an affected organ to a procedure that from a profile that does
     * not have that organ listed as donated throws the correct exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalAddAffectedOrgan() throws IllegalArgumentException {
        // create a profile and add a procedure
        Procedure procedure = new Procedure(
                "Appendix Removal",
                "2-11-2018",
                "Will remove the appendix via key hole surgery"
        );

        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile;
        try {
            testProfile = new Profile(donorAttr);

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
     */
    @Test
    public void testRemoveAffectedOrgans() {
        // create a profile and add a procedure
        Procedure procedure = new Procedure(
                "Appendix Removal",
                "2-11-2018",
                "Will remove the appendix via key hole surgery"
        );

        ArrayList<String> donorAttr = new ArrayList<>();
        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile;
        try {
            testProfile = new Profile(donorAttr);

            testProfile.addProcedure(procedure);

            // add heart and liver
            testProfile.addDonationFromString("heart, liver");
            testProfile.getAllProcedures().get(0).addAffectedOrgan(testProfile, OrganEnum.HEART);
            testProfile.getAllProcedures().get(0).addAffectedOrgan(testProfile, OrganEnum.LIVER);

            // remove the heart
            testProfile.getAllProcedures().get(0).removeAffectedOrgen(OrganEnum.HEART);

            // test that heart has been removed
            assertEquals(testProfile.getAllProcedures().get(0).getOrgansAffected().contains(
                    OrganEnum.HEART), false);
            assertEquals(testProfile.getAllProcedures().get(0).getOrgansAffected().contains(
                    OrganEnum.LIVER), true);
        } catch (IllegalArgumentException e) {
            // pass
        }

    }

    @Test
    public void testAddDrug(){
        Drug drug1 = new Drug("acetaminophen");
        Drug drug2 = new Drug("paracetamol");

        ArrayList<String> donorAttr = new ArrayList<>();
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

        ArrayList<String> donorAttr = new ArrayList<>();
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

        ArrayList<String> donorAttr = new ArrayList<>();
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

        ArrayList<String> donorAttr = new ArrayList<>();
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
        ArrayList<String> donorAttr = new ArrayList<>();

        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testProfile;
        try {
            Condition condition = new Condition("aids", "18-7-1997", "15-09-2014", false);
            testProfile = new Profile(donorAttr);
            testProfile.addCondition(condition);

            assertTrue(testProfile.getAllConditions().contains(condition));
        } catch (IllegalArgumentException e) {
            // pass
        }


    }

    /**
     * Tests deleting a condition of the user
     */
    @Test
    public void testDeleteCondition() {
        ArrayList<String> donorAttr = new ArrayList<>();

        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testDonor;
        try {
            Condition condition = new Condition("aids", "18-7-1997", "15-9-2013", false);
            testDonor = new Profile(donorAttr);
            testDonor.addCondition(condition);
            testDonor.removeCondition(condition);

            assertTrue(testDonor.getAllConditions().size() == 0);
        } catch (IllegalArgumentException e) {
            // pass
        }

    }

    /**
     * Tests getting an array list of all cured conditions from the user
     */
    @Test
    public void testGetCuredConditions() {
        ArrayList<String> donorAttr = new ArrayList<>();

        donorAttr.add("given-names=\"John\"");
        donorAttr.add("last-names=\"Smithy Smith Face\"");
        donorAttr.add("dob=\"01-01-2000\"");
        donorAttr.add("dod=\"01-01-2050\"");
        donorAttr.add("ird=\"123456879\"");

        Profile testDonor;
        try {
            Condition condition0 = new Condition("Aids", "18-7-1997", true);
            Condition condition1 = new Condition("Aids", "28-7-1994", "15-9-2013", false);

            testDonor = new Profile(donorAttr);
            testDonor.addCondition(condition0);
            testDonor.addCondition(condition1);

            assertTrue(testDonor.getCuredConditions().size() == 1);
            assertTrue(testDonor.getCuredConditions().contains(condition1));
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void testRequiredOrganHistory() {
        ArrayList<String> profileAttr = new ArrayList<>();
        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"01-01-2000\"");
        profileAttr.add("dod=\"01-01-2050\"");
        profileAttr.add("ird=\"123456879\"");

        Profile testProfile;

        try {
            testProfile = new Profile(profileAttr);

            List<String> someOrgans = new ArrayList<>();
            someOrgans.add("Heart");
            testProfile.addOrgansRequired(OrganEnum.stringListToOrganSet(someOrgans));

            assertTrue(CommandUtils.currentSessionHistory
                    .get(CommandUtils.historyPosition)
                    .contains(OrganEnum.HEART.getNamePlain()
                    )
            );
        } catch (IllegalArgumentException e) {
            // pass
        }
    }
}
