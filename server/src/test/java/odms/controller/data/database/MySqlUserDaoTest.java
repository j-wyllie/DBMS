package odms.controller.data.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.SQLException;
import odms.commons.model.enums.UserType;
import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import server.model.database.DAOFactory;
import server.model.database.PasswordUtilities;
import server.model.database.user.UserDAO;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PasswordUtilities.class)
@PowerMockIgnore("javax.management.*")
public class MySqlUserDaoTest extends MySqlCommonTests {
    private UserDAO userDAO = DAOFactory.getUserDao();

    private User testUser0;
    private User testUser1;

    @Before
    public void setup() {
        PowerMockito.stub(
                PowerMockito.method(PasswordUtilities.class, "getSaltedHash")
        ).toReturn("test");

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
    }

    @After
    public void tearDown() {
        try {
            userDAO.remove(testUser0);
            userDAO.remove(testUser1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        assertFalse(!userDAO.isUniqueUsername("ree"));
    }

    @Test
    public void testIsUniqueUsernameFalse() throws SQLException {
        assertFalse(userDAO.isUniqueUsername("Username"));
    }
}
