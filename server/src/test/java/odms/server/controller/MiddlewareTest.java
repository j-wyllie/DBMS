package odms.server.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sonar.api.server.authentication.UnauthorizedException;
import server.controller.Middleware;
import server.model.database.DAOFactory;
import server.model.database.PasswordUtilities;
import server.model.database.profile.ProfileDAO;
import server.model.database.user.UserDAO;
import server.model.enums.KeyEnum;
import spark.HaltException;
import spark.Request;
import spark.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PasswordUtilities.class)
@PowerMockIgnore("javax.management.*")
public class MiddlewareTest extends CommonTestUtils {

    // Data access objects required.
    private ProfileDAO profileDAO = DAOFactory.getProfileDao();
    private UserDAO userDAO = DAOFactory.getUserDao();

    // Profile variables.
    private Profile profileA;
    private Profile profileB;
    private Profile profileC;
    private LocalDate genericDate =
            LocalDate.of(1998, 3, 3);

    // User variables.
    private User userA;
    private User userB;
    private User userC;

    // Request variables.
    private Request requestA;
    private Request requestB;
    private Request requestC;

    // Response variables.
    private Response responseA;
    private Response responseB;
    private Response responseC;

    // General variables.
    private int invalidId = -1;

    @Before
    public void setup() throws SQLException, UserNotFoundException {

        PowerMockito.stub(
                PowerMockito.method(PasswordUtilities.class, "getSaltedHash")
        ).toReturn("test");

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
        userA.setPassword("yeet");
        userDAO.add(userA);
        userA = userDAO.get(userA.getUsername());

        userB = new User(UserType.CLINICIAN, "Tim", "Hamblin");
        userB.setUsername("timh");
        userB.setPassword("yeet");
        userDAO.add(userB);
        userB = userDAO.get(userB.getUsername());

        userC = new User(UserType.CLINICIAN, "Josh", "Wyllie");
        userC.setUsername("joshw");
        userC.setPassword("yeet");
        userDAO.add(userC);
        userC = userDAO.get(userC.getUsername());

        requestA = mock(Request.class);
        when(requestA.headers(KeyEnum.ID.toString())).thenReturn(profileA.getId().toString());
        when(requestA.headers(KeyEnum.USERTYPE.toString())).thenReturn(String.valueOf(UserType.PROFILE));

        requestB = mock(Request.class);
        when(requestB.headers(KeyEnum.ID.toString())).thenReturn(userA.getStaffID().toString());
        when(requestB.headers(KeyEnum.USERTYPE.toString())).thenReturn(String.valueOf(UserType.ADMIN));

        requestC = mock(Request.class);
        when(requestC.headers(KeyEnum.ID.toString())).thenReturn(userB.getStaffID().toString());
        when(requestC.headers(KeyEnum.USERTYPE.toString())).thenReturn(String.valueOf(UserType.CLINICIAN));

        responseA = mock(Response.class);
        responseB = mock(Response.class);
        responseC = mock(Response.class);
    }

    @Test
    public void testProfileAuthenticateValid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(profileA.getId(), UserType.PROFILE);
        // Add token to mocked request.
        when(requestA.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        assertTrue(Middleware.isAuthenticated(requestA, responseA));
    }

    @Test (expected = HaltException.class)
    public void testProfileAuthenticateInvalid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(invalidId, UserType.PROFILE);
        // Add token to mocked request.
        when(requestA.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        // Set id to invalid id.
        when(requestA.headers(KeyEnum.ID.toString())).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAuthenticated(requestA, responseA));
    }

    @Test
    public void testAdminAuthenticateValid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(userA.getStaffID(), UserType.ADMIN);
        // Add token to mocked request.
        when(requestB.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        assertTrue(Middleware.isAdminAuthenticated(requestB, responseB));
    }

    @Test (expected = HaltException.class)
    public void testAdminAuthenticateInvalid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(invalidId, UserType.ADMIN);
        // Add token to mocked request.
        when(requestB.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        // Set id to invalid id.
        when(requestB.headers(KeyEnum.ID.toString())).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAdminAuthenticated(requestB, responseB));
    }

    @Test
    public void testClinicianAuthenticateValid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(userB.getStaffID(), UserType.CLINICIAN);
        // Add token to mocked request.
        when(requestC.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        assertTrue(Middleware.isAdminAuthenticated(requestC, responseC));
    }

    @Test (expected = HaltException.class)
    public void testClinicianAuthenticateInvalid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(invalidId, UserType.CLINICIAN);
        // Add token to mocked request.
        when(requestC.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        // Set id to invalid id.
        when(requestC.headers(KeyEnum.ID.toString())).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAdminAuthenticated(requestC, responseC));
    }

    @Test
    public void testIsAuthenticatedValid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(profileA.getId(), UserType.PROFILE);
        // Add token to mocked request.
        when(requestA.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        assertTrue(Middleware.isAuthenticated(requestA, responseA));
    }

    @Test (expected = HaltException.class)
    public void testIsAuthenticatedInvalid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(invalidId, UserType.PROFILE);
        // Add token to mocked request.
        when(requestA.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        // Set id to invalid id.
        when(requestA.headers(KeyEnum.ID.toString())).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAuthenticated(requestA, responseA));
    }

    @Test
    public void testIsAdminAuthenticatedValid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(userA.getStaffID(), UserType.ADMIN);
        // Add token to mocked request.
        when(requestB.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        assertTrue(Middleware.isAdminAuthenticated(requestB, responseB));
    }

    @Test (expected = HaltException.class)
    public void testIsAdminAuthenticatedInvalid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(invalidId, UserType.ADMIN);
        // Add token to mocked request.
        when(requestB.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        // Set id to invalid id.
        when(requestB.headers(KeyEnum.ID.toString())).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAdminAuthenticated(requestB, responseB));
    }

    @Test (expected = HaltException.class)
    public void testLogoutProfileValid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(profileA.getId(), UserType.PROFILE);
        // Add token to mocked request.
        when(requestA.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        assertTrue(Middleware.isAuthenticated(requestA, responseA));

        Middleware.logout(profileA.getId(), UserType.PROFILE, auth.get("Token"));
        assertFalse(Middleware.isAuthenticated(requestA, responseA));
    }

    @Test (expected = HaltException.class)
    public void testLogoutProfileInvalid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(invalidId, UserType.PROFILE);
        // Add token to mocked request.
        when(requestA.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        // Set id to invalid id.
        when(requestA.headers(KeyEnum.ID.toString())).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAuthenticated(requestA, responseA));

        Middleware.logout(invalidId, UserType.PROFILE, auth.get("Token"));
        assertFalse(Middleware.isAuthenticated(requestA, responseA));
    }

    @Test (expected = HaltException.class)
    public void testLogoutAdminValid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(userA.getStaffID(), UserType.ADMIN);
        // Add token to mocked request.
        when(requestB.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        assertTrue(Middleware.isAdminAuthenticated(requestB, responseB));

        Middleware.logout(userA.getStaffID(), UserType.ADMIN, auth.get("Token"));
        assertFalse(Middleware.isAdminAuthenticated(requestB, responseB));
    }

    @Test (expected = HaltException.class)
    public void testLogoutAdminInvalid() throws SQLException {
        Map<String, Integer> auth = Middleware.authenticate(invalidId, UserType.ADMIN);
        // Add token to mocked request.
        when(requestB.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(auth.get("Token")));
        // Set id to invalid id.
        when(requestB.headers(KeyEnum.ID.toString())).thenReturn(String.valueOf(invalidId));
        assertFalse(Middleware.isAdminAuthenticated(requestB, responseB));

        Middleware.logout(invalidId, UserType.ADMIN, auth.get("Token"));
        assertFalse(Middleware.isAdminAuthenticated(requestB, responseB));
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
