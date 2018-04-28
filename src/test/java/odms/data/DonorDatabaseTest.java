package odms.data;

import java.util.ArrayList;
import odms.donor.Donor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class DonorDatabaseTest {
    private DonorDatabase donorDB;
    private Donor donorOne;
    private Donor donorTwo;

    @Before
    public void setup() {
        // Create donor Database with basic donor
        donorDB = new DonorDatabase();

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
            donorOne = new Donor(donorOneAttr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            donorTwo = new Donor(donorTwoAttr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAddDonor() {
        try {
            donorDB.addDonor(donorOne);

            assertEquals(donorDB.getDonor(0).getGivenNames(), "John");
            assertEquals(donorDB.getDonor(0).getLastNames(), "Wayne");
            donorDB.addDonor(donorTwo);

            assertEquals(donorDB.getDonor(1).getGivenNames(), "Sam");
            assertEquals(donorDB.getDonor(1).getLastNames(), "Sick");
            assertTrue("Population should be 2", donorDB.getDonorPopulation() == 2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteDonor() {
        try {
            donorDB.addDonor(donorOne);
            donorDB.addDonor(donorTwo);
            assertTrue("Population should be 2", donorDB.getDonorPopulation() == 2);

            donorDB.deleteDonor(0);
            assertNull(donorDB.getDonor(0));

            assertEquals(donorDB.getDonor(1).getGivenNames(), "Sam");
            assertEquals(donorDB.getDonor(1).getLastNames(), "Sick");
            assertTrue("Population should be 1", donorDB.getDonorPopulation() == 1);

            donorDB.deleteDonor(1);
            assertTrue("Population should be 0", donorDB.getDonorPopulation() == 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetDonorPopulation() {
        try {
            donorDB.addDonor(donorOne);
            donorDB.addDonor(donorTwo);
            assertTrue("Population should be 2", donorDB.getDonorPopulation() == 2);

            donorDB.deleteDonor(0);
            assertTrue("Population should be 1", donorDB.getDonorPopulation() == 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetDonors() {
        ArrayList<Donor> testResults;

        try {
            donorDB.addDonor(donorOne);
            donorDB.addDonor(donorTwo);
            testResults = donorDB.getDonors(false);
            assertTrue("Should be 2 results", testResults.size() == 2);

            // Check sorting works as intended
            assertEquals(testResults.get(0).getLastNames(), "Sick");
            assertEquals(testResults.get(1).getLastNames(), "Wayne");

            testResults = donorDB.getDonors(true);
            assertTrue("Should be 0 results", testResults.size() == 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCheckIRDNumberExists() throws IrdNumberConflictException {
        thrown.expect(IrdNumberConflictException.class);
        thrown.expectMessage("IRD number already in use");

        donorDB.addDonor(donorOne);
        donorDB.addDonor(donorOne);
    }

    @Test
    public void TestSearchDonors() {
        // Since the fuzzy search is implemented by an external library it will be hard to test what names match the
        // string. So tests will be based on strings that definitely should or shouldn't be matched.
        ArrayList<Donor> testResults;

        // Create some more profiles that are only needed for this test.
        ArrayList<String> donorThreeAttr = new ArrayList<>();
        donorThreeAttr.add("given-names=\"Sam\"");
        donorThreeAttr.add("last-names=\"Slavko\"");
        donorThreeAttr.add("dob=\"17-01-1997\"");
        donorThreeAttr.add("ird=\"123456877\"");

        ArrayList<String> donorFourAttr = new ArrayList<>();
        donorFourAttr.add("given-names=\"Reece\"");
        donorFourAttr.add("last-names=\"Williams\"");
        donorFourAttr.add("dob=\"17-01-1997\"");
        donorFourAttr.add("ird=\"123456876\"");

        ArrayList<String> donorFiveAttr = new ArrayList<>();
        donorFiveAttr.add("given-names=\"Zu\"");
        donorFiveAttr.add("last-names=\"Tiu\"");
        donorFiveAttr.add("dob=\"17-01-1997\"");
        donorFiveAttr.add("ird=\"123456875\"");

        Donor donorThree = new Donor(donorThreeAttr);
        Donor donorFour = new Donor(donorFourAttr);
        Donor donorFive = new Donor(donorFiveAttr);


        try {
            // No profiles in db, so no results
            testResults = donorDB.searchDonors("Sam Slavko");
            assertTrue(testResults.size() == 0);

            donorDB.addDonor(donorOne);
            donorDB.addDonor(donorTwo);
            donorDB.addDonor(donorThree);
            donorDB.addDonor(donorFour);
            donorDB.addDonor(donorFive);

            // Top result should be profile Sam Slavko, next result Sam Sick. No other results because other profile's
            // names not at all similar.
            testResults = donorDB.searchDonors("Sam Slavko");
            assertTrue(testResults.size() == 2);
            assertEquals(donorThree, testResults.get(0));
            assertEquals(donorTwo, testResults.get(1));

            // Should not contain profile Zu Tiu because this name has no a or nearby letter to a. Should contain
            // all other profiles.
            testResults = donorDB.searchDonors("a");
            assertTrue(testResults.size() == 4);
            assertTrue(!testResults.contains(donorFive));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
