package odms.data;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import odms.donor.Donor;
import org.junit.Before;
import org.junit.Test;

public class DonorDataIOTest {
    private DonorDatabase donorDB;
    private Donor donorOne;

    @Before
    public void setup() {
        // Create donor Database with basic donor
        donorDB = new DonorDatabase();

        ArrayList<String> donorOneAttr = new ArrayList<>();
        donorOneAttr.add("given-names=\"John\"");
        donorOneAttr.add("last-names=\"Wayne\"");
        donorOneAttr.add("dob=\"17-01-1998\"");
        donorOneAttr.add("ird=\"123456879\"");

        try {
            donorOne = new Donor(donorOneAttr);
            donorDB.addDonor(donorOne);

        } catch (IrdNumberConflictException e) {
            e.printStackTrace();
        }

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

        try {
            Files.deleteIfExists(Paths.get("test.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
