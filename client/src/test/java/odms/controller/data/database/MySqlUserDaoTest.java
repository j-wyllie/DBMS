package odms.controller.data.database;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.SQLException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import odms.controller.database.DatabaseConnection;
import odms.controller.database.MySqlUserDAO;
import odms.controller.user.UserNotFoundException;
import odms.model.user.User;
import odms.model.enums.UserType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MySqlUserDaoTest extends MySqlCommonTests {
    MySqlUserDAO mySqlUserDAO = new MySqlUserDAO();

    private User testUser0 = new User(1, "Username", "password", "Tim Hamblin", UserType.ADMIN, "69 Yeetville", "Yeetus",
            LocalDateTime.now(), LocalDateTime.now());
    private User testUser1 = new User(1, "Pleb", "password", "Brooke rasdasdk", UserType.ADMIN, "68 Yeetville", "Yeetskeet",
            LocalDateTime.now(), LocalDateTime.now());

    @Before
    public void setUp() throws SQLException, UserNotFoundException {
        mySqlUserDAO.add(testUser0);
        testUser0 = mySqlUserDAO.get("username");
    }

    @Test
    public void testGetUser() throws UserNotFoundException, SQLException {
        mySqlUserDAO.add(testUser1);

        assertEquals(testUser1.getUsername(), mySqlUserDAO.get("Pleb").getUsername());
    }

    @Test (expected = UserNotFoundException.class)
    public void testGetInvalidUser() throws SQLException, UserNotFoundException {
        assertEquals(testUser0.getUsername(), mySqlUserDAO.get("Yeet").getUsername());
    }

    @Test
    public void testGetAll() throws SQLException {
        mySqlUserDAO.add(testUser1);

        assertEquals(2, mySqlUserDAO.getAll().size());
    }

    @Test
    public void testRemove() throws SQLException {
        mySqlUserDAO.remove(testUser0);
        assertEquals(0, mySqlUserDAO.getAll().size());
    }

    @Test
    public void testUpdate() throws SQLException, UserNotFoundException {
        testUser0.setName("Nanny");
        mySqlUserDAO.update(testUser0);

        assertEquals("Nanny", mySqlUserDAO.get("username").getName());
    }

    @Test
    public void testIsUniqueUsernameFalse() throws SQLException {
        assertFalse(mySqlUserDAO.isUniqueUsername("ree"));
    }

    @Test
    public void testIsUniqueUsernameTrue() throws SQLException {
        assertTrue(mySqlUserDAO.isUniqueUsername("Username"));
    }

    @After
    public void cleanup() throws SQLException {
        List<User> users = mySqlUserDAO.getAll();
        for (User user : users) {
            mySqlUserDAO.remove(user);
        }
    }
}
