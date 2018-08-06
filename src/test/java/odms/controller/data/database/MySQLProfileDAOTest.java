package odms.controller.data.database;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private Profile testProfileLong0 = new Profile(1, "DSF5422", "JackONZ", true, false,
    "Jack", "Hay", LocalDate.of(1997, 11, 14), null, "male",
    180d, 81d, "O-", true, null, 133, 80, "123 fake street", "Canterbury", "314324134",
    "jha56@uclive.ac.nz", LocalDateTime.now(), LocalDateTime.now());
    private Profile testProfileLong1 = new Profile(1, "HSD7892", "FaZe_Josh", true, true,
            "Josh", "Wyllie", LocalDate.of(1997, 7, 18), null, "male",
            182d, 79d, "O+", false, null, 144, 85, "321 imaginary place", "Canterbury", "314352341",
            "jwy31@uclive.ac.nz", LocalDateTime.now(), LocalDateTime.now());
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
        mySqlProfileDAO.add(testProfileLong0);
        Profile outProfile = mySqlProfileDAO.get("DSF5422");
        Assert.assertEquals(testProfileLong0.getNhi(), outProfile.getNhi());
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

    @Test
    public void isUniqueUsername() throws SQLException {
        mySqlProfileDAO.add(testProfileLong0);
        System.out.println(testProfileLong0.getUsername());
        boolean isUnique = mySqlProfileDAO.isUniqueUsername(testProfileLong0.getUsername());
        Assert.assertEquals(false, isUnique);
    }

    /**
     * Sets the database back to the production database
     */
//    @After
//    public void cleanUp() {
//        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
//        connectionInstance.resetTestDb();
//    }
}
