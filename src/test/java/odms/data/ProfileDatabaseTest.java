package odms.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import odms.profile.Profile;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ProfileDatabaseTest {
    private ProfileDatabase donorDB;
    private Profile profileOne;
    private Profile profileTwo;

    @Before
    public void setup() {
        // Create profile Database with basic profile
        donorDB = new ProfileDatabase();

        ArrayList<String> donorOneAttr = new ArrayList<>();
        donorOneAttr.add("given-names=\"John\"");
        donorOneAttr.add("last-names=\"Wayne\"");
        donorOneAttr.add("dob=\"17-01-1998\"");
        donorOneAttr.add("ird=\"123456879\"");

        ArrayList<String> donorTwoAttr = new ArrayList<>();
        donorTwoAttr.add("given-names=\"Sam\"");
        donorTwoAttr.add("last-names=\"Sick\"");
        donorTwoAttr.add("dob=\"17-01-1997\"");
        donorTwoAttr.add("ird=\"123456878\"");

        try {
            profileOne = new Profile(donorOneAttr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            profileTwo = new Profile(donorTwoAttr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAddDonor() {
        try {
            donorDB.addProfile(profileOne);

            assertEquals(donorDB.getProfile(0).getGivenNames(), "John");
            assertEquals(donorDB.getProfile(0).getLastNames(), "Wayne");
            donorDB.addProfile(profileTwo);

            assertEquals(donorDB.getProfile(1).getGivenNames(), "Sam");
            assertEquals(donorDB.getProfile(1).getLastNames(), "Sick");
            assertTrue("Population should be 2", donorDB.getProfilePopulation() == 2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteDonor() {
        try {
            donorDB.addProfile(profileOne);
            donorDB.addProfile(profileTwo);
            assertTrue("Population should be 2", donorDB.getProfilePopulation() == 2);

            donorDB.deleteProfile(0);
            assertNull(donorDB.getProfile(0));

            assertEquals(donorDB.getProfile(1).getGivenNames(), "Sam");
            assertEquals(donorDB.getProfile(1).getLastNames(), "Sick");
            assertTrue("Population should be 1", donorDB.getProfilePopulation() == 1);

            donorDB.deleteProfile(1);
            assertTrue("Population should be 0", donorDB.getProfilePopulation() == 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetDonorPopulation() {
        try {
            donorDB.addProfile(profileOne);
            donorDB.addProfile(profileTwo);
            assertTrue("Population should be 2", donorDB.getProfilePopulation() == 2);

            donorDB.deleteProfile(0);
            assertTrue("Population should be 1", donorDB.getProfilePopulation() == 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetDonors() {
        ArrayList<Profile> testResults;

        try {
            donorDB.addProfile(profileOne);
            donorDB.addProfile(profileTwo);
            testResults = donorDB.getProfiles(false);
            assertTrue("Should be 2 results", testResults.size() == 2);

            // Check sorting works as intended
            assertEquals(testResults.get(0).getLastNames(), "Sick");
            assertEquals(testResults.get(1).getLastNames(), "Wayne");

            testResults = donorDB.getProfiles(true);
            assertTrue("Should be 0 results", testResults.size() == 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCheckIRDNumberExists() throws IrdNumberConflictException {
        thrown.expect(IrdNumberConflictException.class);
        thrown.expectMessage("IRD number already in use");

        donorDB.addProfile(profileOne);
        donorDB.addProfile(profileOne);
    }

}
