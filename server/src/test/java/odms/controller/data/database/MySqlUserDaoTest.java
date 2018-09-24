package odms.controller.data.database;

import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import server.model.database.user.MySqlUserDAO;

import java.sql.SQLException;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MySqlUserDaoTest extends MySqlCommonTests {

    private static MySqlUserDAO mySqlUserDAO = new MySqlUserDAO();

    private static User testUser0;
    private static User testUser1;

    @BeforeClass
    public static void setup() throws SQLException, UserNotFoundException {
        testUser0 = mySqlUserDAO.get("username");
        testUser1 = mySqlUserDAO.get("Pleb");
    }

    @Test
    public void testGetUser() throws UserNotFoundException, SQLException {

        assertEquals(testUser0.getUsername(), mySqlUserDAO.get("Username").getUsername());
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetInvalidUser() throws SQLException, UserNotFoundException {
        assertEquals(testUser0.getUsername(), mySqlUserDAO.get("Yeet").getUsername());
    }

    @Test
    public void testGetAll() throws SQLException {
        assertEquals(2, mySqlUserDAO.getAll().size());
    }

    @Test
    public void testRemove() throws SQLException {
        mySqlUserDAO.remove(testUser1);
        assertEquals(1, mySqlUserDAO.getAll().size());
    }

    @Test
    public void testUpdate() throws SQLException, UserNotFoundException {
        testUser0.setName("Nanny");
        mySqlUserDAO.update(testUser0);

        assertEquals("Nanny", mySqlUserDAO.get("username").getName());
    }

    @Test
    public void testIsUniqueUsernameTrue() throws SQLException {
        assertTrue(mySqlUserDAO.isUniqueUsername("ree"));
    }

    @Test
    public void testIsUniqueUsernameFalse() throws SQLException {
        assertFalse(mySqlUserDAO.isUniqueUsername("Username"));
    }

    @AfterClass
    public static void cleanup() throws SQLException {
        List<User> users = mySqlUserDAO.getAll();
        for (User user : users) {
            mySqlUserDAO.remove(user);
        }
    }
}
