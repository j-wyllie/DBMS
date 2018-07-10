package odms.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class ProfileImportTaskTest {
    private ProfileImportTask pit;


    @Before
    public void setup() {
        File file = new File("./src/test/java/odms/data/profileTestData/CSVProfiles1.csv");
        pit = new ProfileImportTask(file);
    }

//    @Test
//    public void testLoad1ProfileFromCSV() throws InvalidFileException {
//        ProfileDatabase loadedDb;
//        File file = new File("./src/test/java/odms/data/profileTestData/CSVProfiles1.csv");
//        ProfileImportTask pit = new ProfileImportTask(file);
//        loadedDb = pit.loadDataFromCSV(file);
//        assertEquals(1, loadedDb.getProfiles(false).size());
//    }

//    @Test
//    public void testLoad100ProfilesFromCSV() throws InvalidFileException {
//        ProfileDatabase loadedDb;
//        File file = new File("./src/test/java/odms/data/profileTestData/CSVProfiles100.csv");
//        loadedDb = ProfileDataIO.loadDataFromCSV(file);
//        assertEquals(100, loadedDb.getProfiles(false).size());
//    }
//
//    @Test
//    public void testCSVToProfileConverterInvalid() {
//        ProfileDatabase loadedDb;
//
//        File file = new File("./src/test/java/odms/data/profileTestData/CSVProfilesInvalid.csv");
//        try {
//            loadedDb = ProfileDataIO.loadDataFromCSV(file);
//        } catch (InvalidFileException e) {
//            assert(true);
//        }
//    }
//
    @Test
    public void testInvalidNHI() {
        // Only one assert going on here, nothing to see
        String invalidNHI = "123TREE";
        assertFalse(pit.isValidNHI(invalidNHI));

        invalidNHI = "ABO1234";
        assertFalse(pit.isValidNHI(invalidNHI));

        invalidNHI = "ABI1234";
        assertFalse(pit.isValidNHI(invalidNHI));

        invalidNHI = "AB1234";
        assertFalse(pit.isValidNHI(invalidNHI));
    }

    @Test
    public void testValidNHI() {
        String validNHI = "ABC1234";
        assertTrue(pit.isValidNHI(validNHI));
    }
}
