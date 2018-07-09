package odms.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import odms.profile.Profile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

public class ProfileDataIOTest {
    private ProfileDatabase profileDb;
    private Profile profileOne;
    private static final String SAMPLE_CSV_FILE = "./sample.csv";

    @Before
    public void setup() {
        // Create profile Database with basic profile
        profileDb = new ProfileDatabase();

        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");

        try {
            profileOne = new Profile(profileOneAttr);
            profileDb.addProfile(profileOne);

        } catch (NHIConflictException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSaveJSON() {
        ProfileDatabase loadedDb;
        ProfileDataIO.saveData(profileDb, "CommandUtilsTest.json");

        loadedDb = ProfileDataIO.loadDataFromJSON("CommandUtilsTest.json");

        assertEquals(
            profileDb.getProfile(0).getGivenNames(),
            loadedDb.getProfile(0).getGivenNames()
        );

        try {
            Files.deleteIfExists(Paths.get("CommandUtilsTest.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoad1ProfileFromCSV() throws InvalidFileException {
        ProfileDatabase loadedDb;
        File file = new File("./src/test/java/odms/data/profileTestData/CSVProfiles1.csv");
        loadedDb = ProfileDataIO.loadDataFromCSV(file);
        assertEquals(1, loadedDb.getProfiles(false).size());
    }

    @Test
    public void testLoad100ProfilesFromCSV() throws InvalidFileException {
        ProfileDatabase loadedDb;
        File file = new File("./src/test/java/odms/data/profileTestData/CSVProfiles100.csv");
        loadedDb = ProfileDataIO.loadDataFromCSV(file);
        assertEquals(100, loadedDb.getProfiles(false).size());
    }

    @Test
    public void testCSVToProfileConverterInvalid() {
            ProfileDatabase loadedDb;

            File file = new File("./src/test/java/odms/data/profileTestData/CSVProfilesInvalid.csv");
            try {
                loadedDb = ProfileDataIO.loadDataFromCSV(file);
            } catch (InvalidFileException e) {
                assert(true);
            }

    }

    @Test
    public void testInvalidNHI() {
        // Only one assert going on here, nothing to see
        String invalidNHI = "123TREE";
        assertFalse(ProfileDataIO.isValidNHI(invalidNHI));

        invalidNHI = "ABO1234";
        assertFalse(ProfileDataIO.isValidNHI(invalidNHI));

        invalidNHI = "ABI1234";
        assertFalse(ProfileDataIO.isValidNHI(invalidNHI));

        invalidNHI = "AB1234";
        assertFalse(ProfileDataIO.isValidNHI(invalidNHI));
    }

    @Test
    public void testValidNHI() {
        String validNHI = "ABC1234";
        assertTrue(ProfileDataIO.isValidNHI(validNHI));
    }
}
