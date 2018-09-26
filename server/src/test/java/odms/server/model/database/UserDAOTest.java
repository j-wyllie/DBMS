package odms.server.model.database;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.SQLException;
import java.util.List;
import odms.commons.model.enums.UserType;
import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import odms.server.CommonTestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import server.model.database.DAOFactory;
import server.model.database.user.UserDAO;

public class UserDAOTest extends CommonTestUtils {
    private static UserDAO userDAO = DAOFactory.getUserDao();

    private static User testUser0;
    private static User testUser1;

    @BeforeClass
    public static void addUser() throws SQLException, UserNotFoundException {
        testUser0 = new User(UserType.ADMIN, "Tim Hamblin","Yeetus");
        testUser0.setUsername("username");
        userDAO.add(testUser0);

        testUser1 = new User(UserType.ADMIN, "Brooke rasdasdk","Yeetskeet");
        testUser1.setUsername("pleb");
        userDAO.add(testUser1);

        testUser0 = userDAO.get("username");
        testUser1 = userDAO.get("pleb");
    }

    @Test
    public void testGetUser() throws UserNotFoundException, SQLException {
        assertEquals(testUser0.getUsername(), userDAO.get("username").getUsername());
    }

    @Test (expected = UserNotFoundException.class)
    public void testGetInvalidUser() throws SQLException, UserNotFoundException {
        assertEquals(testUser0.getUsername(), userDAO.get("Yeet").getUsername());
    }

    @Test
    public void testGetAll() throws SQLException {
        assertEquals(2, userDAO.getAll().size());
    }

    @Test
    public void testRemove() throws SQLException {
        userDAO.remove(testUser1);
        assertEquals(1, userDAO.getAll().size());
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
        assertFalse(userDAO.isUniqueUsername("username"));
    }

    @AfterClass
    public static void cleanup() throws SQLException {
        List<User> users = userDAO.getAll();
        for (User user : users) {
            userDAO.remove(user);
        }
    }
}
