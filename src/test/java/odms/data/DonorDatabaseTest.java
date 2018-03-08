package odms.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import odms.donor.Donor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

}
