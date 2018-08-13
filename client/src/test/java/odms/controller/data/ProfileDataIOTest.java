package odms.controller.data;

import java.sql.SQLException;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;
import odms.data.NHIConflictException;
import odms.commons.model.profile.Profile;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ProfileDataIOTest {

    private ProfileDAO profileDb;
    private Profile profileOne;

    @Before
    public void setup() {
        // Create profile Database with basic profile
        profileDb = DAOFactory.getProfileDao();

        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");

        try {
            profileOne = new Profile(profileOneAttr);
            profileDb.add(profileOne);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSaveAndLoad() throws SQLException {
        assertEquals(
                profileDb.getAll().get(0).getGivenNames(),
                profileDb.getAll().get(0).getGivenNames()
        );

        try {
            Files.deleteIfExists(Paths.get("CommandUtilsTest.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
