package odms.server.controller;

import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.controller.CommonController;
import server.controller.Middleware;
import server.model.database.DAOFactory;
import server.model.database.profile.ProfileDAO;
import server.model.database.user.UserDAO;
import server.model.enums.KeyEnum;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommonControllerTest extends CommonTestUtils {

    // Data access objects required.
    private ProfileDAO profileDAO = DAOFactory.getProfileDao();
    private UserDAO userDAO = DAOFactory.getUserDao();

    // Profile variables.
    private Profile profileA;
    private LocalDate genericDate =
            LocalDate.of(1998, 3, 3);

    // User variables.
    private User userA;
    private User userB;

    // Request variables.
    private Request requestA;
    private Request requestB;
    private Request requestC;
    private Request requestD;
    private Request requestE;

    // Response variables.
    private Response responseA;
    private Response responseB;
    private Response responseC;
    private Response responseD;
    private Response responseE;

    @Before
    public void setup() throws UserNotFoundException, SQLException {
        profileA = new Profile("Alice", "Smith",
                genericDate, "LPO7236");
        profileDAO.add(profileA);
        profileA.setUsername("alices");
        profileA.setPassword("12345");
        profileA = profileDAO.get(profileA.getNhi());

        // Default admin.
        userA = new User(UserType.ADMIN, "admin", "Canterbury");
        userA.setUsername("admin");
        userA.setPassword("admin");
        userA.setDefault(true);
        userDAO.add(userA);
        userA = userDAO.get(userA.getUsername());

        // Default clinician.
        userB = new User(UserType.CLINICIAN, "Default", "Canterbury");
        userB.setUsername("0");
        userB.setPassword("password");
        userB.setDefault(true);
        userDAO.add(userB);
        userB = userDAO.get(userB.getUsername());

        requestA = mock(Request.class);
        when(requestA.queryParams("username")).thenReturn(userA.getUsername());
        when(requestA.queryParams("password")).thenReturn(userA.getPassword());
        when(requestA.queryParams(KeyEnum.USERTYPE.toString())).thenReturn(String.valueOf(userA.getUserType()));

        requestB = mock(Request.class);
        when(requestB.queryParams("username")).thenReturn(profileA.getUsername());
        when(requestB.queryParams("password")).thenReturn(profileA.getPassword());
        when(requestB.queryParams(KeyEnum.USERTYPE.toString())).thenReturn(String.valueOf(UserType.PROFILE));

        requestC = mock(Request.class);

        requestD = mock(Request.class);
        when(requestD.headers(KeyEnum.ID.toString())).thenReturn(String.valueOf(userA.getStaffID()));
        when(requestD.headers(KeyEnum.USERTYPE.toString())).thenReturn(String.valueOf(userA.getUserType()));

        requestE = mock(Request.class);
        when(requestE.headers(KeyEnum.ID.toString())).thenReturn(String.valueOf(profileA.getId()));
        when(requestE.headers(KeyEnum.USERTYPE.toString())).thenReturn(String.valueOf(UserType.PROFILE));

        responseA = mock(Response.class);
        responseB = mock(Response.class);
        responseC = mock(Response.class);
        responseD = mock(Response.class);
        responseE = mock(Response.class);
    }

    @Test
    public void testSetupRequired() throws SQLException, UserNotFoundException {
        // Remove so default creation is required.
        userDAO.remove(userA);
        userDAO.remove(userB);
        CommonController.setup(requestC, responseC);
        assertEquals(userA, userDAO.get(userA.getUsername()));
        assertEquals(userB, userDAO.get(userB.getUsername()));
    }

    @Test
    public void testSetupNotRequired() throws UserNotFoundException, SQLException {
        CommonController.setup(requestC, responseC);
        assertEquals(userA, userDAO.get(userA.getUsername()));
        assertEquals(userB, userDAO.get(userB.getUsername()));
    }

    @Test
    public void testCheckCredentialsUserValid() {
        String response = CommonController.checkCredentials(requestA, responseA);
        assertEquals(200, responseA.status());
    }

    @Test
    public void testCheckCredentialsUserInvalid() {
        // Overwrite mock with invalid password.
        when(requestA.headers("password")).thenReturn("invalid");
        String response = CommonController.checkCredentials(requestA, responseA);
        assertEquals(404, responseA.status());
    }

    @Test
    public void testCheckCredentialsUserInvalidUsername() {
        // Overwrite mock with invalid username.
        when(requestA.headers("username")).thenReturn("invalid");
        String response = CommonController.checkCredentials(requestA, responseA);
        assertEquals(404, responseA.status());
    }

    @Test
    public void testCheckCredentialsProfileValid() {
        String response = CommonController.checkCredentials(requestB, responseB);
        assertEquals(200, responseB.status());
    }

    @Test
    public void testCheckCredentialsProfileInvalid() {
        // Overwrite mock with invalid password.
        when(requestB.headers("password")).thenReturn("invalid");
        String response = CommonController.checkCredentials(requestB, responseB);
        assertEquals(404, responseB.status());
    }

    @Test
    public void testCheckCredentialsProfileInvalidUsername() {
        // Overwrite mock with invalid username.
        when(requestB.headers("username")).thenReturn("invalid");
        String response = CommonController.checkCredentials(requestB, responseB);
        assertEquals(404, responseB.status());

    }

    @Test
    public void testLogoutUserValid() throws SQLException {
        // Authenticate the user.
        int token = Middleware.authenticate(userA.getStaffID(), userA.getUserType()).get("Token");
        when(requestD.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(token));
        assertTrue(Middleware.isAdminAuthenticated(requestD, responseD));

        // Logout the user.
        CommonController.logout(requestD, responseD);
        assertEquals(200, responseD.status());
    }

    @Test
    public void testLogoutUserInvalid() {
        // Logout the user - token header missing and user not logged in.
        CommonController.logout(requestD, responseD);
        assertEquals(400, responseD.status());
    }

    @Test
    public void testLogoutUserInvalidToken() throws SQLException {
        // Authenticate the user.
        int token = Middleware.authenticate(userA.getStaffID(), userA.getUserType()).get("Token");
        when(requestD.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(token));
        assertTrue(Middleware.isAdminAuthenticated(requestD, responseD));

        // Invalid token set.
        int invalidToken = -1;
        when(requestD.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(invalidToken));

        // Logout the user.
        CommonController.logout(requestD, responseD);
        assertEquals(401, responseD.status());
    }

    @Test
    public void testLogoutProfileValid() throws SQLException {
        // Authenticate the profile.
        int token = Middleware.authenticate(profileA.getId(), UserType.PROFILE).get("Token");
        when(requestE.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(token));
        assertTrue(Middleware.isAuthenticated(requestE, responseE));

        // Logout the user.
        CommonController.logout(requestE, responseE);
        assertEquals(200, responseE.status());
    }

    @Test
    public void testLogoutProfileInvalid() {
        // Logout the profile - token header missing and user not logged in.
        CommonController.logout(requestE, responseE);
        assertEquals(400, responseE.status());
    }

    @Test
    public void testLogoutProfileInvalidToken() throws SQLException {
        // Authenticate the profile.
        int token = Middleware.authenticate(profileA.getId(), UserType.PROFILE).get("Token");
        when(requestE.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(token));
        assertTrue(Middleware.isAuthenticated(requestE, responseE));

        // Invalid token set.
        int invalidToken = -1;
        when(requestE.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(invalidToken));

        // Logout the user.
        CommonController.logout(requestE, responseE);
        assertEquals(401, responseE.status());
    }

    @After
    public void tearDown() throws SQLException {
        // Profile teardown.
        profileDAO.remove(profileA);

        // User teardown.
        userDAO.remove(userA);
        userDAO.remove(userB);
    }
}
