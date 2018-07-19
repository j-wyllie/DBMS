package odms.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class ProfileImportTaskTest {
    private ProfileImportTask profileImportTask;

    @Before
    public void setup() {
        File file = new File("./src/test/java/odms/data/profileTestData/CSVProfiles1.csv");
        profileImportTask = new ProfileImportTask(file);
    }

    @Test
    public void testInvalidNHI() {
        // Only one assert going on here, nothing to see
        String invalidNHI = "123TREE";
        assertFalse(profileImportTask.isValidNHI(invalidNHI));

        invalidNHI = "ABO1234";
        assertFalse(profileImportTask.isValidNHI(invalidNHI));

        invalidNHI = "ABI1234";
        assertFalse(profileImportTask.isValidNHI(invalidNHI));

        invalidNHI = "AB1234";
        assertFalse(profileImportTask.isValidNHI(invalidNHI));
    }

    @Test
    public void testValidNHI() {
        String validNHI = "ABC1234";
        assertTrue(profileImportTask.isValidNHI(validNHI));
    }
}
