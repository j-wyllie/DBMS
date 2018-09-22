package odms.server.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.sql.SQLException;
import java.time.LocalDate;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.server.authentication.UnauthorizedException;
import server.controller.Middleware;
import server.model.database.DAOFactory;
import server.model.database.profile.ProfileDAO;
import server.model.database.user.UserDAO;
import spark.Request;


public class MiddlewareTest extends CommonTestUtils {

    // Data access objects required.
    private ProfileDAO profileDAO = DAOFactory.getProfileDao();
    private UserDAO userDAO = DAOFactory.getUserDao();

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
    private Request requestC;

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

        userA = new User(UserType.ADMIN, "Brooke", "Canterbury");
        userA.setUsername("brooker");
        userDAO.add(userA);
        userA = userDAO.get(userA.getUsername());

        userB = new User(UserType.CLINICIAN, "Tim", "Hamblin");
        userB.setUsername("timh");
        userDAO.add(userB);
        userB = userDAO.get(userB.getUsername());

        userC = new User(UserType.CLINICIAN, "Josh", "Wyllie");
        userC.setUsername("joshw");
        userDAO.add(userC);
        userC = userDAO.get(userC.getUsername());

        requestA = mock(Request.class);
        when(requestA.headers("id")).thenReturn(profileA.getId().toString());
        when(requestA.headers("userType")).thenReturn(String.valueOf(UserType.PROFILE));

        requestB = mock(Request.class);
        when(requestB.headers("id")).thenReturn(userA.getStaffID().toString());
        when(requestB.headers("userType")).thenReturn(String.valueOf(UserType.ADMIN));

        requestC = mock(Request.class);
        when(requestC.headers("id")).thenReturn(userB.getStaffID().toString());
        when(requestC.headers("userType")).thenReturn(String.valueOf(UserType.CLINICIAN));
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
        // Set id to invalid id.
        when(requestA.headers("id")).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAuthenticated(requestA));
    }

    @Test
    public void testAdminAuthenticateValid() throws SQLException {
        int token = Middleware.authenticate(userA.getStaffID(), UserType.ADMIN);
        // Add token to mocked request.
        when(requestB.headers("token")).thenReturn(String.valueOf(token));
        assertTrue(Middleware.isAdminAuthenticated(requestB));
    }

    @Test
    public void testAdminAuthenticateInvalid() throws SQLException {
        int token = Middleware.authenticate(invalidId, UserType.ADMIN);
        // Add token to mocked request.
        when(requestB.headers("token")).thenReturn(String.valueOf(token));
        // Set id to invalid id.
        when(requestB.headers("id")).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAdminAuthenticated(requestB));
    }

    @Test
    public void testClinicianAuthenticateValid() throws SQLException {
        int token = Middleware.authenticate(userB.getStaffID(), UserType.CLINICIAN);
        // Add token to mocked request.
        when(requestC.headers("token")).thenReturn(String.valueOf(token));
        assertTrue(Middleware.isAdminAuthenticated(requestC));
    }

    @Test
    public void testClinicianAuthenticateInvalid() throws SQLException {
        int token = Middleware.authenticate(invalidId, UserType.CLINICIAN);
        // Add token to mocked request.
        when(requestC.headers("token")).thenReturn(String.valueOf(token));
        // Set id to invalid id.
        when(requestC.headers("id")).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAdminAuthenticated(requestC));
    }

    @Test
    public void testIsAuthenticatedValid() throws SQLException {
        int token = Middleware.authenticate(profileA.getId(), UserType.PROFILE);
        // Add token to mocked request.
        when(requestA.headers("token")).thenReturn(String.valueOf(token));
        assertTrue(Middleware.isAuthenticated(requestA));
    }

    @Test
    public void testIsAuthenticatedInvalid() throws SQLException {
        int token = Middleware.authenticate(invalidId, UserType.PROFILE);
        // Add token to mocked request.
        when(requestA.headers("token")).thenReturn(String.valueOf(token));
        // Set id to invalid id.
        when(requestA.headers("id")).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAuthenticated(requestA));
    }

    @Test
    public void testIsAdminAuthenticatedValid() throws SQLException {
        int token = Middleware.authenticate(userA.getStaffID(), UserType.ADMIN);
        // Add token to mocked request.
        when(requestB.headers("token")).thenReturn(String.valueOf(token));
        assertTrue(Middleware.isAdminAuthenticated(requestB));
    }

    @Test
    public void testIsAdminAuthenticatedInvalid() throws SQLException {
        int token = Middleware.authenticate(invalidId, UserType.ADMIN);
        // Add token to mocked request.
        when(requestB.headers("token")).thenReturn(String.valueOf(token));
        // Set id to invalid id.
        when(requestB.headers("id")).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAdminAuthenticated(requestB));
    }

    @Test
    public void testLogoutProfileValid() throws SQLException {
        int token = Middleware.authenticate(profileA.getId(), UserType.PROFILE);
        // Add token to mocked request.
        when(requestA.headers("token")).thenReturn(String.valueOf(token));
        assertTrue(Middleware.isAuthenticated(requestA));

        Middleware.logout(profileA.getId(), UserType.PROFILE, token);
        assertFalse(Middleware.isAuthenticated(requestA));
    }

    @Test (expected = UnauthorizedException.class)
    public void testLogoutProfileInvalid() throws SQLException {
        int token = Middleware.authenticate(invalidId, UserType.PROFILE);
        // Add token to mocked request.
        when(requestA.headers("token")).thenReturn(String.valueOf(token));
        // Set id to invalid id.
        when(requestA.headers("id")).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAuthenticated(requestA));

        Middleware.logout(invalidId, UserType.PROFILE, token);
        assertFalse(Middleware.isAuthenticated(requestA));
    }

    @Test
    public void testLogoutAdminValid() throws SQLException {
        int token = Middleware.authenticate(userA.getStaffID(), UserType.ADMIN);
        // Add token to mocked request.
        when(requestB.headers("token")).thenReturn(String.valueOf(token));
        assertTrue(Middleware.isAuthenticated(requestB));

        Middleware.logout(userA.getStaffID(), UserType.ADMIN, token);
        assertFalse(Middleware.isAuthenticated(requestB));
    }

    @Test (expected = UnauthorizedException.class)
    public void testLogoutAdminInvalid() throws SQLException {
        int token = Middleware.authenticate(invalidId, UserType.ADMIN);
        // Add token to mocked request.
        when(requestB.headers("token")).thenReturn(String.valueOf(token));
        // Set id to invalid id.
        when(requestB.headers("id")).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAuthenticated(requestB));

        Middleware.logout(invalidId, UserType.ADMIN, token);
        assertFalse(Middleware.isAuthenticated(requestB));
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
