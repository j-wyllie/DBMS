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
    private ProfileDatabase profileDb;
    private Profile profileOne;
    private Profile profileTwo;

    @Before
    public void setup() {
        // Create profile Database with basic profile
        profileDb = new ProfileDatabase();

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
            profileDb.addProfile(profileOne);

            assertEquals(profileDb.getProfile(0).getGivenNames(), "John");
            assertEquals(profileDb.getProfile(0).getLastNames(), "Wayne");
            profileDb.addProfile(profileTwo);

            assertEquals(profileDb.getProfile(1).getGivenNames(), "Sam");
            assertEquals(profileDb.getProfile(1).getLastNames(), "Sick");
            assertTrue("Population should be 2", profileDb.getProfilePopulation() == 2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteDonor() {
        try {
            profileDb.addProfile(profileOne);
            profileDb.addProfile(profileTwo);
            assertTrue("Population should be 2", profileDb.getProfilePopulation() == 2);

            profileDb.deleteProfile(0);
            assertNull(profileDb.getProfile(0));

            assertEquals(profileDb.getProfile(1).getGivenNames(), "Sam");
            assertEquals(profileDb.getProfile(1).getLastNames(), "Sick");
            assertTrue("Population should be 1", profileDb.getProfilePopulation() == 1);

            profileDb.deleteProfile(1);
            assertTrue("Population should be 0", profileDb.getProfilePopulation() == 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetDonorPopulation() {
        try {
            profileDb.addProfile(profileOne);
            profileDb.addProfile(profileTwo);
            assertTrue("Population should be 2", profileDb.getProfilePopulation() == 2);

            profileDb.deleteProfile(0);
            assertTrue("Population should be 1", profileDb.getProfilePopulation() == 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetDonors() {
        ArrayList<Profile> testResults;

        try {
            profileDb.addProfile(profileOne);
            profileDb.addProfile(profileTwo);
            testResults = profileDb.getProfiles(false);
            assertTrue("Should be 2 results", testResults.size() == 2);

            // Check sorting works as intended
            assertEquals(testResults.get(0).getLastNames(), "Sick");
            assertEquals(testResults.get(1).getLastNames(), "Wayne");

            testResults = profileDb.getProfiles(true);
            assertTrue("Should be 0 results", testResults.size() == 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCheckIRDNumberExists() throws IrdNumberConflictException {
        thrown.expect(IrdNumberConflictException.class);
        thrown.expectMessage("IRD number already in use");

        profileDb.addProfile(profileOne);
        profileDb.addProfile(profileOne);
    }

}
