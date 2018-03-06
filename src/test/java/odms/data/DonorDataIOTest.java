package odms.data;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import odms.Donor.Donor;
import org.junit.Before;
import org.junit.Test;

public class DonorDataIOTest {
    private DonorDatabase donorDB;
    private Donor donorOne;

    @Before
    public void setup() {
        // Create Donor Database with basic Donor
        donorDB = new DonorDatabase();
        donorOne = new Donor("John", "Wayne", LocalDate.now(), "");
        donorDB.addDonor(donorOne);

    }

    @Test
    public void testSaveAndLoad() {
        DonorDatabase loadedDb;
        DonorDataIO.saveDonors(donorDB, "test.json");

        loadedDb = DonorDataIO.loadData("test.json");

        assertEquals(
            donorDB.getDonor(0).getGivenNames(),
            loadedDb.getDonor(0).getGivenNames()
        );
    }

}
