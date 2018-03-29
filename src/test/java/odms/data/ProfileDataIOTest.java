package odms.data;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import odms.profile.Profile;
import org.junit.Before;
import org.junit.Test;

public class ProfileDataIOTest {
    private ProfileDatabase donorDB;
    private Profile profileOne;

    @Before
    public void setup() {
        // Create profile Database with basic profile
        donorDB = new ProfileDatabase();

        ArrayList<String> donorOneAttr = new ArrayList<>();
        donorOneAttr.add("given-names=\"John\"");
        donorOneAttr.add("last-names=\"Wayne\"");
        donorOneAttr.add("dob=\"17-01-1998\"");
        donorOneAttr.add("ird=\"123456879\"");

        try {
            profileOne = new Profile(donorOneAttr);
            donorDB.addDonor(profileOne);

        } catch (IrdNumberConflictException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSaveAndLoad() {
        ProfileDatabase loadedDb;
        ProfileDataIO.saveDonors(donorDB, "CommandUtilsTest.json");

        loadedDb = ProfileDataIO.loadData("CommandUtilsTest.json");

        assertEquals(
            donorDB.getDonor(0).getGivenNames(),
            loadedDb.getDonor(0).getGivenNames()
        );

        try {
            Files.deleteIfExists(Paths.get("CommandUtilsTest.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
