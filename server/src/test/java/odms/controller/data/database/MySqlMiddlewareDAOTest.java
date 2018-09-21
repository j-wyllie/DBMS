package odms.controller.data.database;

import static org.junit.Assert.assertTrue;
import java.sql.SQLException;
import java.time.LocalDate;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.DAOFactory;
import server.model.database.middleware.MiddlewareDAO;
import server.model.database.profile.ProfileDAO;
import server.model.database.user.UserDAO;

public class MySqlMiddlewareDAOTest extends MySqlCommonTests {

    // Data access objects required.
    private ProfileDAO profileDAO = DAOFactory.getProfileDao();
    private UserDAO userDAO = DAOFactory.getUserDao();
    private MiddlewareDAO middleware = DAOFactory.getMiddlewareDAO();

    // Profile variables.
    private Profile profileA;
    private Profile profileB;

    // User variables.
    private User userA;
    private User userB;

    // Token variables.
    long validToken = 32873;
    long invalidToken = 784834;

    // General variables.
    private int invalidId = 0;

    @Before
    public void setup() throws SQLException {
        profileA = new Profile("Brooke", "Rakowitz",
                LocalDate.of(1998, 3, 3), "YSK726");
        profileDAO.add(profileA);

        profileB = new Profile("Bob", "Dylan",
                LocalDate.of(1998, 3, 3), "YSL999");
        profileDAO.add(profileB);

        userA = new User(UserType.CLINICIAN, "Brooke", "Canterbury");
        userDAO.add(userA);

        userB = new User(UserType.CLINICIAN, "Tim", "Hamblin");
        userDAO.add(userB);
    }

    @Test
    public void testSetProfileTokenValid() throws SQLException {
        middleware.setProfileToken(profileA.getId(), validToken);
        assertTrue(middleware.isProfileAuthenticated(profileA.getId(), validToken));
    }

    @Test
    public void testSetUserTokenValid() throws SQLException {
        middleware.setUserToken(userA.getStaffID(), validToken);
        assertTrue(middleware.isUserAuthenticated(userA.getStaffID(), validToken));
    }

    @Test
    public void testSetProfileTokenInvalid() throws SQLException {
        middleware.setProfileToken(invalidId, validToken);
        assertFalse(middleware.isProfileAuthenticated(invalidId, validToken));
    }

    @Test
    public void testSetUserTokenInvalid() throws SQLException {
        middleware.setUserToken(invalidId, validToken);
        assertFalse(middleware.isUserAuthenticated(invalidId, validToken));
    }

    @Test
    public void testIsProfileAuthenticatedValid() throws SQLException {
        middleware.setProfileToken(profileB.getId(), validToken);
        assertTrue(middleware.isProfileAuthenticated(profileB.getId(), validToken));
    }

    @Test
    public void testIsUserAuthenticatedValid() throws SQLException {
        middleware.setUserToken(userB.getStaffID(), validToken);
        assertTrue(middleware.isUserAuthenticated(userB.getStaffID(), validToken));
    }

    @Test
    public void testIsProfileAuthenticatedInvalid() throws SQLException {
        middleware.setProfileToken(profileB.getId(), validToken);
        assertFalse(middleware.isProfileAuthenticated(profileB.getId(), invalidToken));
    }

    @Test
    public void testIsUserAuthenticatedInvalid() {
        middleware.setUserToken(userB.getStaffID(), validToken);
        assertFalse(middleware.isUserAuthenticated(userB.getStaffID(), invalidToken));
    }

    @Test
    public void testDeleteProfileTokenValid() {

    }

    @Test
    public void testDeleteUserTokenValid() {

    }

    @Test
    public void testDeleteProfileTokenInvalid() {

    }

    @Test
    public void testDeleteUserTokenInvalid() {

    }

    @After
    public void tearDown() throws SQLException {

    }

}
