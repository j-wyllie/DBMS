package odms.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import odms.profile.Profile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MySqlProfileDAOTest {
    private MySqlProfileDAO mySqlProfileDAO;

    private Profile testProfile0 = new Profile("Joshua", "Wyllie", LocalDate.of(1997, 7, 18), "ABC1234");
    private Profile testProfile1 = new Profile("Jack", "Hay", LocalDate.of(1998, 2, 27), "CBA43211");

    @Before
    public void setUp() {
        DatabaseConnection.setConfig("/config/db_test.config");
        mySqlProfileDAO = new MySqlProfileDAO();
    }

    @Test
    public void testAddGet() {
        testProfile0.setId(42);
        mySqlProfileDAO.add(testProfile0);

        Profile outProfile = mySqlProfileDAO.get(42);
        Assert.assertEquals(testProfile0, outProfile);
    }

    @Test
    public void testGetAll() {
        mySqlProfileDAO.add(testProfile0);
        List<Profile> allInProfiles = new ArrayList<>();
        allInProfiles.add(testProfile0);
        allInProfiles.add(testProfile1);

        List<Profile> allOutProfiles = mySqlProfileDAO.getAll();
        Assert.assertEquals(allOutProfiles, allInProfiles);
    }

    @Test
    public void testRemove() {
        mySqlProfileDAO.remove(testProfile0);
        List<Profile> allProfiles = mySqlProfileDAO.getAll();
        Assert.assertEquals(allProfiles.get(0), testProfile1);
    }

    @After
    public void cleanUp() {
        DatabaseConnection.setConfig("/config/db.config");
    }
}
