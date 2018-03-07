package odms.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import odms.donor.Donor;
import org.junit.Before;
import org.junit.Test;

public class DonorDatabaseTest {
    private DonorDatabase donorDB;
    private Donor donorOne;
    private Donor donorTwo;

    @Before
    public void setup() {
        // Create donor Database with basic donor
        donorDB = new DonorDatabase();
        donorOne = new Donor("John", "Wayne", LocalDate.now(), "");
        donorTwo = new Donor("Sam", "Sick", LocalDate.now(), "");

    }

    @Test
    public void testAddDonor() {
        donorDB.addDonor(donorOne);

        assertEquals(donorDB.getDonor(0).getGivenNames(), "John");
        assertEquals(donorDB.getDonor(0).getLastNames(), "Wayne");
        donorDB.addDonor(donorTwo);

        assertEquals(donorDB.getDonor(1).getGivenNames(), "Sam");
        assertEquals(donorDB.getDonor(1).getLastNames(), "Sick");
        assertTrue("Population should be 2", donorDB.getDonorPopulation() == 2);

    }

    @Test
    public void testDeleteDonor() {
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

    }

    @Test
    public void testgetDonorPopulation() {
        donorDB.addDonor(donorOne);
        donorDB.addDonor(donorTwo);
        assertTrue("Population should be 2", donorDB.getDonorPopulation() == 2);

        donorDB.deleteDonor(0);
        assertTrue("Population should be 1", donorDB.getDonorPopulation() == 1);

    }

}
