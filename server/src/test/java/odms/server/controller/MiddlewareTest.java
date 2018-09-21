package odms.server.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import odms.server.model.database.MySqlCommonTests;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.Server;
import server.controller.Middleware;
import server.model.database.DAOFactory;
import server.model.database.middleware.MiddlewareDAO;
import server.model.database.profile.ProfileDAO;
import server.model.database.user.UserDAO;
import spark.Request;

public class MiddlewareTest extends MySqlCommonTests {

    // Data access objects required.
    private ProfileDAO profileDAO = DAOFactory.getProfileDao();
    private UserDAO userDAO = DAOFactory.getUserDao();
    private MiddlewareDAO middleware = DAOFactory.getMiddlewareDAO();

    // Profile variables.
    private Profile profileA;
    private Profile profileB;
    private Profile profileC;
    private LocalDate genericDate = LocalDate.of(1998, 3, 3);

    // User variables.
    private User userA;
    private User userB;
    private User userC;

    // Request variables.
    private Request requestA;
    private Request requestB;

    // Token variables.
    int validToken = 32873;
    int invalidToken = 784834;

    // General variables.
    private int invalidId = 0;

    @Before
    public void setup() throws SQLException, UserNotFoundException {
        profileA = new Profile("Alice", "Smith",
                genericDate, "LPO7236");
        profileDAO.add(profileA);
        profileA = profileDAO.get(profileA.getNhi());

        profileB = new Profile("Ben", "Smith",
                genericDate, "YSL9939");
        profileDAO.add(profileB);
        profileB = profileDAO.get(profileB.getNhi());

        profileC = new Profile("Bob", "Marshall",
                genericDate, "GSK726");
        profileDAO.add(profileC);
        profileC = profileDAO.get(profileC.getNhi());

        userA = new User(UserType.CLINICIAN, "Brooke", "Canterbury");
        userA.setUsername("brooker");
        userDAO.add(userA);
        userA = userDAO.get(userA.getUsername());

        userB = new User(UserType.ADMIN, "Tim", "Hamblin");
        userB.setUsername("timh");
        userDAO.add(userB);
        userB = userDAO.get(userB.getUsername());

        userC = new User(UserType.CLINICIAN, "Josh", "Wyllie");
        userC.setUsername("joshw");
        userDAO.add(userC);
        userC = userDAO.get(userC.getUsername());

        requestA = mock(Request.class);
        when(requestA.headers("id")).thenReturn(profileA.getId().toString());
        when(requestA.headers("userType")).thenReturn(UserType.PROFILE.getName());

        requestB = mock(Request.class);
    }

    @Test
    public void testProfileAuthenticateValid() throws SQLException {
        int token = Middleware.authenticate(profileA.getId(), UserType.PROFILE);
        // Add token to mocked request.
        when(requestA.headers("token")).thenReturn(String.valueOf(token));
        assertTrue(Middleware.isAuthenticated(requestA));
    }

    @Test
    public void testProfileAuthenticateInvalid() throws SQLException {
        int token = Middleware.authenticate(invalidId, UserType.PROFILE);
        // Add token to mocked request.
        when(requestA.headers("token")).thenReturn(String.valueOf(token));
        assertFalse(Middleware.isAuthenticated(requestA));
    }

    @Test
    public void testUserAuthenticateValid() {

    }

    @Test
    public void testUserAuthenticateInvalid() {

    }

    @Test
    public void testIsAuthenticateValid() {

    }

    @Test
    public void testIsAuthenticateInvalid() {

    }

    @Test
    public void testIsAdminAuthenticateValid() {

    }

    @Test
    public void testIsAdminAuthenticateInvalid() {

    }

    @Test
    public void testLogoutValid() {

    }

    @Test
    public void testLogoutInvalid() {

    }

    @After
    public void tearDown() throws SQLException {
        // Profile teardown.
        profileDAO.remove(profileA);
        profileDAO.remove(profileB);
        profileDAO.remove(profileC);

        // User teardown.
        userDAO.remove(userA);
        userDAO.remove(userB);
        userDAO.remove(userC);
    }
}
