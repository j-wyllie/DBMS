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

        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("ird=\"123456879\"");

        ArrayList<String> profileTwoAttr = new ArrayList<>();
        profileTwoAttr.add("given-names=\"Sam\"");
        profileTwoAttr.add("last-names=\"Sick\"");
        profileTwoAttr.add("dob=\"17-01-1997\"");
        profileTwoAttr.add("ird=\"123456878\"");

        try {
            profileOne = new Profile(profileOneAttr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            profileTwo = new Profile(profileTwoAttr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAddProfile() {
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
    public void testDeleteProfile() {
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
    public void testGetProfilePopulation() {
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
    public void testGetProfiles() {
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

    @Test
    public void TestSearchDonors() {
        // Since the fuzzy search is implemented by an external library it will be hard to test what names match the
        // string. So tests will be based on strings that definitely should or shouldn't be matched.
        ArrayList<Profile> testResults;

        // Create some more profiles that are only needed for this test.
        ArrayList<String> profileThreeAttr = new ArrayList<>();
        profileThreeAttr.add("given-names=\"Sam\"");
        profileThreeAttr.add("last-names=\"Slavko\"");
        profileThreeAttr.add("dob=\"17-01-1997\"");
        profileThreeAttr.add("ird=\"123456877\"");

        ArrayList<String> profileFourAttr = new ArrayList<>();
        profileFourAttr.add("given-names=\"Reece\"");
        profileFourAttr.add("last-names=\"Williams\"");
        profileFourAttr.add("dob=\"17-01-1997\"");
        profileFourAttr.add("ird=\"123456876\"");

        ArrayList<String> profileFiveAttr = new ArrayList<>();
        profileFiveAttr.add("given-names=\"Zu\"");
        profileFiveAttr.add("last-names=\"Tiu\"");
        profileFiveAttr.add("dob=\"17-01-1997\"");
        profileFiveAttr.add("ird=\"123456875\"");

        Profile profileThree = new Profile(profileThreeAttr);
        Profile profileFour = new Profile(profileFourAttr);
        Profile profileFive = new Profile(profileFiveAttr);


        try {
            // No profiles in db, so no results
            testResults = profileDb.searchProfiles("Sam Slavko",  -999, -999, null, null, null, null);
            assertTrue(testResults.size() == 0);

            profileDb.addProfile(profileOne);
            profileDb.addProfile(profileTwo);
            profileDb.addProfile(profileThree);
            profileDb.addProfile(profileFour);
            profileDb.addProfile(profileFive);

            // Top result should be profile Sam Slavko, next result Sam Sick. No other results because other profile's
            // names not at all similar.
            testResults = profileDb.searchProfiles("Sam Slavko", -999, -999, null, null, null, null);
            assertTrue(testResults.size() == 2);
            assertEquals(profileThree, testResults.get(0));
            assertEquals(profileTwo, testResults.get(1));

            // Should not contain profile Zu Tiu because this name has no a or nearby letter to a. Should contain
            // all other profiles.
            testResults = profileDb.searchProfiles("a", -999, -999, null, null, null, null);
            assertTrue(testResults.size() == 4);
            assertTrue(!testResults.contains(profileFive));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
