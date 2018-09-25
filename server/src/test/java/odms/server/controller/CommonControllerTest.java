package odms.server.controller;

import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.DAOFactory;
import server.model.database.profile.ProfileDAO;
import server.model.database.user.UserDAO;
import server.model.enums.KeyEnum;
import spark.Request;
import spark.Response;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void setup() {

    }

    @Test
    public void testSetupRequired() {

    }

    @Test
    public void testSetupNotRequired() {

    }

    @Test
    public void testCheckCredentialsUserValid() {

    }

    @Test
    public void testCheckCredentialsUserInvalid() {

    }

    @Test
    public void testCheckCredentialsProfileValid() {

    }

    @Test
    public void testCheckCredentialsProfileInvalid() {

    }

    @Test
    public void testLogoutUserValid() {

    }

    @Test
    public void testLogoutUserInvalid() {

    }

    @Test
    public void testLogoutProfileValid() {

    }

    @Test
    public void testLogoutProfileInvalid() {

    }

    @After
    public void tearDown() {

    }
}
