package odms.controller.data.database;

import java.sql.SQLException;
import java.time.LocalDate;
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

    @Test
    public void testAddGet() throws SQLException {
        mySqlProfileDAO.add(testProfile0);
        Profile outProfile = mySqlProfileDAO.get("ABC1234");
        Assert.assertEquals(testProfile0.getNhi(), outProfile.getNhi());
    }

    @Test
    public void testGetAll() throws SQLException {
        mySqlProfileDAO.add(testProfile0);
        mySqlProfileDAO.add(testProfile1);

        List<Profile> allOutProfiles = mySqlProfileDAO.getAll();
        Assert.assertEquals(allOutProfiles.size(), 2);
    }

    @Test
    public void testRemove() throws SQLException {
        mySqlProfileDAO.add(testProfile0);
        Profile testProfile0 = mySqlProfileDAO.get("ABC1234");
        mySqlProfileDAO.remove(testProfile0);
        List<Profile> allProfiles = mySqlProfileDAO.getAll();
        Assert.assertEquals(allProfiles.size(), 0);
    }

    /**
     * Sets the database back to the production database
     */
    @After
    public void cleanUp() {
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        connectionInstance.resetTestDb();
    }
}
