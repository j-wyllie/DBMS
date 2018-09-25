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
    private UserDAO userDAO = DAOFactory.getUserDao();

    private User testUser0;
    private User testUser1;

    private long testTime;

    @Before
    public void setup() {
        long start = System.currentTimeMillis();
        User testUserTim = new User(
                UserType.ADMIN,
                "Tim Hamblin",
                "Yeetus",
                "Username",
                "test"
        );
        User testUserBrooke = new User(
                UserType.ADMIN,
                "Brooke rasdasdk",
                "Yeetskeet",
                "Pleb",
                "test"
        );

        try {
            userDAO.add(testUserTim);
            testUser0 = userDAO.get(testUserTim.getUsername());
            userDAO.add(testUserBrooke);
            testUser1 = userDAO.get(testUserBrooke.getUsername());
        } catch (SQLException | UserNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Setup took: " + (System.currentTimeMillis() - start));
        testTime = System.currentTimeMillis();
    }

    @After
    public void tearDown() {
        System.out.println("Test took: " + (System.currentTimeMillis() - testTime));
        long start = System.currentTimeMillis();
        try {
            userDAO.remove(testUser0);
            userDAO.remove(testUser1);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Teardown took: " + (System.currentTimeMillis() - start));
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
