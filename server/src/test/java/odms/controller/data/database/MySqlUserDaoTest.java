package odms.controller.data.database;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.SQLException;
import odms.commons.model.enums.UserType;
import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.DAOFactory;
import server.model.database.user.UserDAO;

public class MySqlUserDaoTest extends MySqlCommonTests {

    private static UserDAO userDAO = DAOFactory.getUserDao();

    private static User testUser0;
    private static User testUser1;

    @Before
    public void setup() throws SQLException, UserNotFoundException {
        testUser0 = new User(
                UserType.ADMIN,
                "Tim Hamblin",
                "Yeetus",
                "Username",
                "test"
        );
        testUser1 = new User(
                UserType.ADMIN,
                "Brooke rasdasdk",
                "Yeetskeet",
                "Pleb",
                "test"
        );

        userDAO.add(testUser0);
        testUser0 = userDAO.get(testUser0.getUsername());
        userDAO.add(testUser1);
        testUser1 = userDAO.get(testUser1.getUsername());
    }

    @After
    public void tearDown() throws InterruptedException, UserNotFoundException {
        Thread.sleep(100);
        userDAO.remove(testUser0);
        userDAO.remove(testUser1);
    }

    @Test
    public void testGetUser() throws UserNotFoundException, SQLException {
        assertEquals(testUser0.getUsername(), userDAO.get("Username").getUsername());
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetInvalidUser() throws SQLException, UserNotFoundException {
        assertEquals(testUser0.getUsername(), userDAO.get("Yeet").getUsername());
    }

    @Test
    public void testGetAll() throws SQLException {
        assertEquals(2, userDAO.getAll().size());
    }

    @Test (expected = UserNotFoundException.class)
    public void testRemove() throws SQLException, UserNotFoundException {
        User testUser = new User(
                UserType.ADMIN,
                "Russian Hacker",
                "Kazakhstan",
                "llirik",
                "test"
        );
        userDAO.add(testUser);
        testUser = userDAO.get(testUser.getUsername());
        userDAO.remove(testUser);

        userDAO.get(testUser.getUsername());
    }

    @Test
    public void testUpdate() throws SQLException, UserNotFoundException {
        testUser0.setName("Nanny");
        userDAO.update(testUser0);

        assertEquals("Nanny", userDAO.get("username").getName());
    }

    @Test
    public void testIsUniqueUsernameTrue() throws SQLException {
        assertTrue(userDAO.isUniqueUsername("ree"));
    }

    @Test
    public void testIsUniqueUsernameFalse() throws SQLException {
        assertFalse(userDAO.isUniqueUsername("Username"));
    }
}
