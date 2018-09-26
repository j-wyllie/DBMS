package odms.server.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import server.controller.CommonController;
import server.model.database.DAOFactory;
import server.model.database.common.CommonDAO;
import server.model.database.profile.ProfileDAO;
import server.model.database.user.UserDAO;
import spark.Request;
import spark.Response;

public class CommonControllerTest extends CommonTestUtils {

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

    // Request variables.
    private Request requestA;
    private Request requestB;
    private Request requestC;

    // Response variables.
    private Response responseA;

    // General variables.
    private int invalidId = -1;

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

        userA = new User(UserType.ADMIN, "admin", "Canterbury");
        userA.setUsername("admin");
        userDAO.add(userA);
        userA = userDAO.get(userA.getUsername());

        userB = new User(UserType.CLINICIAN, "Default Clinican", "Canterbury");
        userB.setUsername("0");
        userDAO.add(userB);
        userB = userDAO.get(userB.getUsername());

        requestA = mock(Request.class);
        responseA = mock(Response.class);

        requestC = mock(Request.class);
        when(requestC.headers("id")).thenReturn(userB.getStaffID().toString());
        when(requestC.headers("userType")).thenReturn(String.valueOf(UserType.CLINICIAN));
    }

    @Test (expected = UserNotFoundException.class)
    public void testSetupRequired() throws UserNotFoundException, SQLException {
        userDAO.remove(userA);
        userDAO.remove(userB);
        userDAO.get(userA.getUsername());
        userDAO.get(userB.getUsername());

        CommonController.setup(requestA, responseA);
        assertEquals(userA, userDAO.get(userA.getUsername()));
        assertEquals(userB, userDAO.get(userB.getUsername()));
    }

    @Test
    public void testSetupNotRequired() throws UserNotFoundException, SQLException {
        // Initial check.
        assertNotNull(userDAO.get(userA.getUsername()));
        assertNotNull(userDAO.get(userB.getUsername()));

        CommonController.setup(requestA, responseA);
        // Check after setup.
        assertNotNull(userDAO.get(userA.getUsername()));
        assertNotNull(userDAO.get(userB.getUsername()));
    }

    @Test
    public void testCheckCredentialsValidUser() {

    }

    @Test
    public void testCheckCredentialsInvalidUser() {

    }

    @Test
    public void testCheckCredentialsValidProfile() {

    }

    @Test
    public void testCheckCredentialsInvalidProfile() {

    }

    @Test
    public void testUserLogoutValidToken() {

    }


    @Test
    public void testUserLogoutInvalidToken() {

    }

    @Test
    public void testUserLogoutValidId() {

    }


    @Test
    public void testUserLogoutInvalidId() {

    }

    @Test
    public void testProfileLogoutValidToken() {

    }


    @Test
    public void testProfileLogoutInvalidToken() {

    }

    @Test
    public void testProfileLogoutValidId() {

    }


    @Test
    public void testProfileLogoutInvalidId() {

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
    }
}
