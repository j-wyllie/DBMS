package odms.server.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gson.JsonParser;
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
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import server.controller.CommonController;
import server.controller.Middleware;
import server.model.database.DAOFactory;
import server.model.database.PasswordUtilities;
import server.model.database.profile.MySqlProfileDAO;
import server.model.database.profile.ProfileDAO;
import server.model.database.user.MySqlUserDAO;
import server.model.database.user.UserDAO;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PasswordUtilities.class, MySqlUserDAO.class, MySqlProfileDAO.class})
@PowerMockIgnore("javax.management.*")
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

    // General variables.
    JsonParser parser = new JsonParser();

    @Before
    public void setup() throws UserNotFoundException, SQLException {
        PowerMockito.stub(
                PowerMockito.method(PasswordUtilities.class, "getSaltedHash")
        ).toReturn("test");

        profileA = new Profile("Alice", "Smith",
                genericDate, "LPO7236");
        profileA.setUsername("alices");
        profileDAO.add(profileA);
        profileA = profileDAO.get(profileA.getNhi());
        profileA.setPassword("test");
        profileDAO.savePassword(profileA.getUsername(), profileA.getPassword());

        // Default admin.
        userA = new User(UserType.ADMIN, "admin", "Canterbury");
        userA.setUsername("admin");
        userA.setPassword("admin");
        userA.setDefault(true);
        userDAO.add(userA);
        userA = userDAO.get(userA.getUsername());
        userA.setPassword("test");

        // Default clinician.
        userB = new User(UserType.CLINICIAN, "Default", "Canterbury");
        userB.setUsername("0");
        userB.setPassword("test");
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
        assertEquals(userA.getUsername(), userDAO.get(userA.getUsername()).getUsername());
        assertEquals(userB.getUsername(), userDAO.get(userB.getUsername()).getUsername());
    }

    @Test
    public void testSetupNotRequired() throws UserNotFoundException, SQLException {
        CommonController.setup(requestC, responseC);
        assertEquals(userA.getUsername(), userDAO.get(userA.getUsername()).getUsername());
        assertEquals(userB.getUsername(), userDAO.get(userB.getUsername()).getUsername());
    }

    @Test
    public void testCheckCredentialsUserValid() {
        PowerMockito.stub(
                PowerMockito.method(MySqlUserDAO.class, "checkCredentials")
        ).toReturn(true);
        String response = CommonController.checkCredentials(requestA, responseA);
        int id = parser.parse(response).getAsJsonObject().get(KeyEnum.ID.toString()).getAsInt();
        assertEquals(userA.getStaffID().toString(), String.valueOf(id));
    }

    @Test
    public void testCheckCredentialsUserInvalid() {
        PowerMockito.stub(
                PowerMockito.method(MySqlUserDAO.class, "checkCredentials")
        ).toReturn(false);
        // Overwrite mock with invalid password.
        when(requestA.queryParams("password")).thenReturn("invalid");
        assertEquals("Unauthorized", CommonController.checkCredentials(requestA, responseA));
    }

    @Test
    public void testCheckCredentialsUserInvalidUsername() {
        PowerMockito.stub(
                PowerMockito.method(MySqlUserDAO.class, "checkCredentials")
        ).toReturn(false);
        // Overwrite mock with invalid username.
        when(requestA.queryParams("username")).thenReturn("invalid");
        assertEquals("Unauthorized", CommonController.checkCredentials(requestA, responseA));
    }

    @Test
    public void testCheckCredentialsProfileValid() {
        PowerMockito.stub(
                PowerMockito.method(MySqlProfileDAO.class, "checkCredentials")
        ).toReturn(true);
        String response = CommonController.checkCredentials(requestB, responseB);
        int id = parser.parse(response).getAsJsonObject().get(KeyEnum.ID.toString()).getAsInt();
        assertEquals(profileA.getId().toString(), String.valueOf(id));
    }

    @Test
    public void testCheckCredentialsProfileInvalid() {
        PowerMockito.stub(
                PowerMockito.method(MySqlProfileDAO.class, "checkCredentials")
        ).toReturn(false);
        // Overwrite mock with invalid password.
        when(requestB.queryParams("password")).thenReturn("invalid");
        assertEquals("Unauthorized", CommonController.checkCredentials(requestB, responseB));
    }

    @Test
    public void testCheckCredentialsProfileInvalidUsername() {
        // Overwrite mock with invalid username.
        when(requestB.queryParams("username")).thenReturn("invalid");
        assertEquals("Profile not found", CommonController.checkCredentials(requestB, responseB));
    }

    @Test
    public void testLogoutUserValid() throws SQLException {
        // Authenticate the user.
        int token = Middleware.authenticate(userA.getStaffID(), userA.getUserType()).get("Token");
        when(requestD.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(token));
        assertTrue(Middleware.isAdminAuthenticated(requestD, responseD));
        // Logout the user.
        assertEquals("User successfully logged out", CommonController.logout(requestD, responseD));
    }

    @Test
    public void testLogoutUserInvalid() {
        // Logout the user - token header missing and user not logged in.
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), CommonController.logout(requestD, responseD));
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
        assertEquals("User unauthorized.", CommonController.logout(requestD, responseD));
    }

    @Test
    public void testLogoutProfileValid() throws SQLException {
        // Authenticate the profile.
        int token = Middleware.authenticate(profileA.getId(), UserType.PROFILE).get("Token");
        when(requestE.headers(KeyEnum.AUTH.toString())).thenReturn(String.valueOf(token));
        assertTrue(Middleware.isAuthenticated(requestE, responseE));
        // Logout the user.
        assertEquals("User successfully logged out", CommonController.logout(requestE, responseE));
    }

    @Test
    public void testLogoutProfileInvalid() {
        // Logout the profile - token header missing and user not logged in.
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), CommonController.logout(requestE, responseE));
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
        assertEquals("User unauthorized.",  CommonController.logout(requestE, responseE));
    }

    @After
    public void tearDown() throws SQLException {
        // Profile teardown.
        for (Profile profile : profileDAO.getAll()) {
            profileDAO.remove(profile);
        }
        // User teardown.
        for (User user : userDAO.getAll()) {
            userDAO.remove(user);
        }
    }
}
