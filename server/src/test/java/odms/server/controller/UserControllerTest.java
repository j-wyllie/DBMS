package odms.server.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import java.sql.SQLException;
import java.util.List;
import odms.commons.model.enums.UserType;
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
import server.controller.UserController;
import server.model.database.DAOFactory;
import server.model.database.PasswordUtilities;
import server.model.database.user.UserDAO;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PasswordUtilities.class)
@PowerMockIgnore("javax.management.*")
public class UserControllerTest extends CommonTestUtils {

    // Data access objects required.
    private UserDAO userDAO = DAOFactory.getUserDao();

    // User variables.
    private User userA;
    private User userB;
    private User userC;

    // Request variables.
    private Request requestA;

    // Response variables.
    private Response responseA;

    // General variables.
    private Gson gson = new Gson();
    JsonParser parser = new JsonParser();

    @Before
    public void setup() throws SQLException, UserNotFoundException {
        PowerMockito.stub(
                PowerMockito.method(PasswordUtilities.class, "getSaltedHash")
        ).toReturn("test$test");

        // Default admin.
        userA = new User(UserType.ADMIN, "admin", "Canterbury");
        userA.setUsername("admin");
        userA.setPassword("test");
        userA.setDefault(true);
        userDAO.add(userA);
        userA = userDAO.get(userA.getUsername());

        // Default clinician.
        userB = new User(UserType.CLINICIAN, "Default", "Canterbury");
        userB.setUsername("0");
        userB.setPassword("test");
        userB.setDefault(true);
        userDAO.add(userB);
        userB = userDAO.get(userB.getUsername());

        userC = new User(UserType.CLINICIAN, "Brooke", "Rakowitz");
        userC.setUsername("brooker");
        userC.setPassword("test");

        requestA = mock(Request.class);
        responseA = mock(Response.class);
    }

    @Test
    public void testGetAll() {
        List<String> testResults = gson.fromJson(UserController.getAll(requestA, responseA), List.class);
        assertEquals(2, testResults.size());
    }

    @Test
    public void testGetValid() {
        when(requestA.queryParams("username")).thenReturn(userA.getUsername());
        assertEquals(userA.getStaffID(), gson.fromJson(UserController.get(requestA, responseA), User.class).getStaffID());
    }

    @Test
    public void testGetInvalid() {
        when(requestA.queryParams("username")).thenReturn(String.valueOf(-1));
        assertEquals("User not found", UserController.get(requestA, responseA));

    }

    @Test
    public void testCreateValid() {
        when(requestA.body()).thenReturn(gson.toJson(userC));
        assertEquals("User Created", UserController.create(requestA, responseA));
    }

    @Test
    public void testCreateInvalid() {
        userC.setUsername("admin");
        when(requestA.body()).thenReturn(gson.toJson(userC));
        assertEquals(ResponseMsgEnum.FORBIDDEN.toString(), UserController.create(requestA, responseA));
    }

    @Test
    public void testEditValid() {
        userB.setUsername("Default Updated");
        when(requestA.body()).thenReturn(gson.toJson(userB));
        when(requestA.params(KeyEnum.ID.toString())).thenReturn(String.valueOf(userB.getStaffID()));
        assertEquals("User Updated", UserController.edit(requestA, responseA));
    }

    @Test
    public void testEditInvalidId() {
        when(requestA.body()).thenReturn(gson.toJson(userB));
        when(requestA.params(KeyEnum.ID.toString())).thenReturn("invalid");
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), UserController.edit(requestA, responseA));
    }

    @Test
    public void testEditInvalidUsername() {
        userB.setUsername("admin");
        when(requestA.body()).thenReturn(gson.toJson(userB));
        when(requestA.params(KeyEnum.ID.toString())).thenReturn(String.valueOf(userB.getStaffID()));
        assertEquals(ResponseMsgEnum.FORBIDDEN.toString(), UserController.edit(requestA, responseA));
    }

    @Test
    public void testDeleteValid() {
        when(requestA.params(KeyEnum.ID.toString())).thenReturn(String.valueOf(userB.getStaffID()));
        assertEquals("User Deleted", UserController.delete(requestA, responseA));
    }

    @Test
    public void testDeleteInvalidId() {
        when(requestA.params(KeyEnum.ID.toString())).thenReturn("invalid");
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), UserController.delete(requestA, responseA));
    }

    @Test
    public void testCheckCredentialsValid() {
        stubCheck(true);
        when(requestA.queryParams("username")).thenReturn(userA.getUsername());
        when(requestA.queryParams("password")).thenReturn("admin");
        int id = parser.parse(UserController
                .checkCredentials(requestA, responseA))
                .getAsJsonObject().get(KeyEnum.ID.toString())
                .getAsInt();
        assert userA.getStaffID() == id;
    }

    @Test
    public void testCheckCredentialsInvalid() {
        assertEquals("User not found", UserController.checkCredentials(requestA, responseA));
    }

    @Test
    public void testCheckCredentialsUnauthorized() {
        stubCheck(false);
        when(requestA.queryParams("username")).thenReturn(userA.getUsername());
        when(requestA.queryParams("password")).thenReturn("invalid");
        assertEquals("Unauthorized", UserController.checkCredentials(requestA, responseA));
    }

    @After
    public void tearDown() throws SQLException {
        // User teardown.
        for (User user : userDAO.getAll()) {
            userDAO.remove(user);
        }
    }

    private void stubCheck(Boolean result) {
        PowerMockito.stub(
                PowerMockito.method(PasswordUtilities.class, "check")
        ).toReturn(result);
    }
}
