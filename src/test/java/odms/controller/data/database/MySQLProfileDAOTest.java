package odms.controller.data.database;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import odms.controller.database.DatabaseConnection;
import odms.controller.database.MySqlProfileDAO;
import odms.model.profile.Profile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MySQLProfileDAOTest {
    private MySqlProfileDAO mySqlProfileDAO;

    private Profile testProfile0 = new Profile("Joshua", "Wyllie", LocalDate.of(1997, 7, 18), "ABC1234");
    private Profile testProfile1 = new Profile("Jack", "Hay", LocalDate.of(1998, 2, 27), "CBA43211");

    /**
     * Sets the Database to the test database and Initialises the DBO
     */
    @Before
    public void setUp() {
        DatabaseConnection.setConfig("/config/db_test.config");
        mySqlProfileDAO = new MySqlProfileDAO();
    }

    /**
     * Tests adding and getting a profile by id
     */
    @Test
    public void testAddGet() {
        try {
            mySqlProfileDAO.add(testProfile0);
            Profile outProfile = mySqlProfileDAO.get("ABC1234");
            Assert.assertEquals(testProfile0.getNhi(), outProfile.getNhi());
        } catch (SQLException e) {
            assert(false);
        }

    }

//    /**
//     * Tests adding multiple profiles and the getAll function
//     */
//    @Test
//    public void testGetAll() {
//        mySqlProfileDAO.add(testProfile0);
//        List<Profile> allInProfiles = new ArrayList<>();
//        allInProfiles.add(testProfile0);
//        allInProfiles.add(testProfile1);
//
//        List<Profile> allOutProfiles = mySqlProfileDAO.getAll();
//        Assert.assertEquals(allOutProfiles, allInProfiles);
//    }
//
//    /**
//     * Tests removing a profile
//     */
//    @Test
//    public void testRemove() {
//        mySqlProfileDAO.remove(testProfile0);
//        List<Profile> allProfiles = mySqlProfileDAO.getAll();
//        Assert.assertEquals(allProfiles.get(0), testProfile1);
//    }

    /**
     * Sets the database back to the production database
     */
    @After
    public void cleanUp() {
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        connectionInstance.resetTestDb();
    }
}
