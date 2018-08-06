package odms.controller.data.database;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.SQLException;
import java.time.LocalDateTime;

import odms.controller.database.DatabaseConnection;
import odms.controller.database.MySqlUserDAO;
import odms.controller.user.UserNotFoundException;
import odms.model.user.User;
import odms.model.enums.UserType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MySqlUserDaoTest {
    MySqlUserDAO mySqlUserDAO = new MySqlUserDAO();

    private User testUser0 = new User(1, "Username", "password", "Tim Hamblin", UserType.ADMIN, "69 Yeetville", "Yeetus",
            LocalDateTime.now(), LocalDateTime.now());
    private User testUser1 = new User(UserType.ADMIN, "Brooke Radsfdsa", "Wellington");

    @Before
    public void setUp() {
        DatabaseConnection.setConfig("/config/db_test.config");
    }

    @Test
    public void testGetUser() throws UserNotFoundException, SQLException {
        mySqlUserDAO.add(testUser0);

        assertEquals(testUser0.getUsername(), mySqlUserDAO.get("Username").getUsername());
    }

    @Test (expected = UserNotFoundException.class)
    public void testGetInvalidUser() throws SQLException, UserNotFoundException {
        mySqlUserDAO.add(testUser0);

        assertEquals(testUser0.getUsername(), mySqlUserDAO.get("Yeet").getUsername());
    }

    @Test
    public void testGetAll() throws SQLException {
        mySqlUserDAO.add(testUser1);

        assertEquals(1, mySqlUserDAO.getAll().size());
    }

    @Test
    public void testRemove() throws SQLException, UserNotFoundException {
        mySqlUserDAO.add(testUser0);
        testUser0 = mySqlUserDAO.get("username");
        mySqlUserDAO.remove(testUser0);
        assertEquals(0, mySqlUserDAO.getAll().size());
    }

    @Test
    public void testUpdate() throws SQLException, UserNotFoundException {
        mySqlUserDAO.add(testUser0);
        testUser0 = mySqlUserDAO.get("username");
        testUser0.setName("Nanny");
        mySqlUserDAO.update(testUser0);

        assertEquals("Nanny", mySqlUserDAO.get("username").getName());
    }

    @Test
    public void testIsUniqueUsernameFalse() throws SQLException {
        mySqlUserDAO.add(testUser0);
        assertFalse(mySqlUserDAO.isUniqueUsername("ree"));
    }

    @Test
    public void testIsUniqueUsernameTrue() throws SQLException {
        mySqlUserDAO.add(testUser0);
        assertTrue(mySqlUserDAO.isUniqueUsername("Username"));
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
