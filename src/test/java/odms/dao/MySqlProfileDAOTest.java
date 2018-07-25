package odms.dao;

import java.util.List;
import odms.profile.Profile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MySqlProfileDAOTest {
    private MySqlProfileDAO mySqlProfileDAO;

    @Before
    public void setUp() {
        DatabaseConnection.setConfig("/config/db_test.config");
        mySqlProfileDAO = new MySqlProfileDAO();
    }

    @Test
    public void testGetAll() {
        List<Profile> allProfiles = mySqlProfileDAO.getAll();
//
//        for (Profile p : allProfiles) {
//            System.out.println(p);
//        }
    }

    @After
    public void cleanUp() {
        DatabaseConnection.setConfig("/config/db.config");
    }
}
