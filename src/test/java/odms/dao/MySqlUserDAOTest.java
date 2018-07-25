package odms.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import odms.controller.UserNotFoundException;
import odms.profile.Profile;
import odms.user.User;
import odms.user.UserType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MySqlUserDAOTest {
    private MySqlUserDAO MySqlUserDAO;

    private User testUser0 = new User(UserType.ADMIN, "Tim Hamblin", "Auckland");
    private User testUser1 = new User(UserType.ADMIN, "Brooke Radsfdsa", "Wellington");

    /**
     * Sets the Database to the test database and Initialises the DBO
     */
    @Before
    public void setUp() {
        DatabaseConnection.setConfig("/src/config/db_test.config");

        MySqlUserDAO = new MySqlUserDAO();
    }

    /**
     * Tests adding and getting a user by id
     */
    @Test
    public void testAddGet() throws UserNotFoundException {
        testUser0.setStaffID(40);
        MySqlUserDAO.add(testUser1);

        User outUser = MySqlUserDAO.get(40);
        Assert.assertEquals(testUser0, outUser);
    }

    /**
     * Tests adding multiple users and the getAll function
     */
    @Test
    public void testGetAll() {
        MySqlUserDAO.add(testUser0);
        List<User> allInUsers = new ArrayList<>();
        allInUsers.add(testUser0);
        allInUsers.add(testUser1);

        List<User> allOutProfiles = MySqlUserDAO.getAll();
        Assert.assertEquals(allOutProfiles, allInUsers);
    }

    /**
     * Tests removing a user
     */
    @Test
    public void testRemove() {
        MySqlUserDAO.remove(testUser0);
        List<User> allProfiles = MySqlUserDAO.getAll();
        Assert.assertEquals(allProfiles.get(0), testUser1);
    }

    /**
     * Sets the database back to the production database
     */
    @After
    public void cleanUp() {
        DatabaseConnection.setConfig("/src/config/db.config");
    }
}
