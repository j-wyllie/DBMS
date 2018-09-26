package odms.server.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.ExpiredOrgan;
import odms.commons.model.profile.Organ;
import odms.commons.model.profile.OrganConflictException;
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
import server.controller.OrganController;
import server.model.database.DAOFactory;
import server.model.database.PasswordUtilities;
import server.model.database.organ.OrganDAO;
import server.model.database.profile.ProfileDAO;
import server.model.database.user.MySqlUserDAO;
import server.model.database.user.UserDAO;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PasswordUtilities.class)
@PowerMockIgnore("javax.management.*")
public class OrganControllerTest extends CommonTestUtils {

    // Data access object variables.
    ProfileDAO profileDAO = DAOFactory.getProfileDao();
    UserDAO userDAO = DAOFactory.getUserDao();
    OrganDAO organDAO = DAOFactory.getOrganDao();

    // Profile variables.
    private Profile profileA;
    private LocalDate genericDate =
            LocalDate.of(1998, 3, 3);

    // User variables.
    private User userA;

    // Drug variables.
    private Organ organA;
    private Organ organB;
    private Organ organC;
    private Organ organD;
    private Organ organE;

    // Request variables.
    private Request requestA;
    private Request requestB;
    private Request requestC;

    // Response variables.
    private Response responseA;
    private Response responseB;
    private Response responseC;

    // General variables.
    private Gson gson = new Gson();
    private static final String DONATED = "donated";
    private static final String DONATING = "donating";
    private static final String RECEIVED = "received";
    private static final String REQUIRED = "required";

    @Before
    public void setup() throws SQLException, OrganConflictException, UserNotFoundException {
        PowerMockito.stub(
                PowerMockito.method(PasswordUtilities.class, "getSaltedHash")
        ).toReturn("test");

        PowerMockito.stub(
                PowerMockito.method(MySqlUserDAO.class, "checkCredentials")
        ).toReturn(true);

        profileA = new Profile("Alice", "Smith",
                genericDate, "LPO7236");
        profileDAO.add(profileA);
        profileA.setUsername("alices");
        profileA.setPassword("test");
        profileA = profileDAO.get(profileA.getNhi());

        // Default admin.
        userA = new User(UserType.ADMIN, "admin", "Canterbury");
        userA.setUsername("admin");
        userA.setPassword("test");
        userA.setDefault(true);
        userDAO.add(userA);
        userA = userDAO.get(userA.getUsername());
        userA.setPassword("test");

        organA = new Organ(OrganEnum.LIVER, LocalDateTime.now());
        organB = new Organ(OrganEnum.CONNECTIVE_TISSUE, LocalDateTime.now());
        organC = new Organ(OrganEnum.BONE_MARROW, LocalDateTime.now());
        organD = new Organ(OrganEnum.BONE, LocalDateTime.now());
        organE = new Organ(OrganEnum.SKIN, LocalDateTime.now());

        organDAO.addDonating(profileA, organA.getOrganEnum());
        organDAO.addDonating(profileA, organC.getOrganEnum());
        organDAO.addDonating(profileA, organD.getOrganEnum());
        organDAO.addDonating(profileA, organE.getOrganEnum());

        organDAO.addDonation(profileA, organC.getOrganEnum());
        organDAO.addDonation(profileA, organD.getOrganEnum());
        organDAO.addDonation(profileA, organE.getOrganEnum());

        organDAO.addRequired(profileA, organB.getOrganEnum());
        organDAO.addRequired(profileA, organA.getOrganEnum());

        organDAO.addReceived(profileA, organB.getOrganEnum());

        requestA = mock(Request.class);
        responseA = mock(Response.class);

        requestB = mock(Request.class);
        when(requestB.params(KeyEnum.ID.toString())).thenReturn(String.valueOf(profileA.getId()));
        responseB = mock(Response.class);

        requestC = mock(Request.class);
        when(requestC.params(KeyEnum.ID.toString())).thenReturn(String.valueOf(profileA.getId()));
        when(requestC.queryParams("organ")).thenReturn(String.valueOf(organC.getOrganEnum()));
        when(requestC.queryParams("expired")).thenReturn(String.valueOf(1));
        when(requestC.queryParams("note")).thenReturn(String.valueOf(profileA.getId()));
        when(requestC.queryParams("userId")).thenReturn(String.valueOf(userA.getStaffID()));
        responseC = mock(Response.class);
    }

    @Test
    public void testGetAllDonated() {
        when(requestB.queryParams(DONATED)).thenReturn(String.valueOf(true));
        List<String> testResult = gson.fromJson(OrganController.getAll(requestB, responseB), List.class);
        assertEquals(3, testResult.size());
    }

    @Test
    public void testGetAllDonating() {
        when(requestB.queryParams(DONATING)).thenReturn(String.valueOf(true));
        List<String> testResult = gson.fromJson(OrganController.getAll(requestB, responseB), List.class);
        assertEquals(4, testResult.size());
    }

    @Test
    public void testGetAllReceived() {
        when(requestB.queryParams(RECEIVED)).thenReturn(String.valueOf(true));
        List<String> testResult = gson.fromJson(OrganController.getAll(requestB, responseB), List.class);
        assertEquals(1, testResult.size());
    }

    @Test
    public void testGetAllRequired() {
        when(requestB.queryParams(REQUIRED)).thenReturn(String.valueOf(true));
        List<String> testResult = gson.fromJson(OrganController.getAll(requestB, responseB), List.class);
        assertEquals(2, testResult.size());
    }

    @Test
    public void testGetAllInvalidId() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), OrganController.getAll(requestA, responseA));
    }

    @Test
    public void testGetAllInvalidOrganCategory() {
        when(requestA.params(KeyEnum.ID.toString())).thenReturn(String.valueOf(profileA.getId()));
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), OrganController.getAll(requestA, responseA));
    }

    @Test
    public void testAddDonated() {
        // Generate request body.
        when(requestB.body()).thenReturn(generateBody(DONATED));
        // Add the organ.
        assertEquals("Organ added", OrganController.add(requestB, responseB));
    }

    @Test
    public void testAddDonating() {
        // Generate request body.
        when(requestB.body()).thenReturn(generateBody(DONATING));
        // Add the organ.
        assertEquals("Organ added", OrganController.add(requestB, responseB));
    }

    @Test
    public void testAddReceived() {
        // Generate request body.
        when(requestB.body()).thenReturn(generateBody(RECEIVED));
        // Add the organ.
        assertEquals("Organ added", OrganController.add(requestB, responseB));
    }

    @Test
    public void testAddRequired() {
        // Generate request body.
        when(requestB.body()).thenReturn(generateBody(REQUIRED));
        // Add the organ.
        assertEquals("Organ added", OrganController.add(requestB, responseB));
    }

    @Test
    public void testAddInvalidId() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), OrganController.add(requestA, responseA));
    }

    @Test
    public void testDeleteDonating() {
        when(requestB.queryParams(DONATING)).thenReturn(String.valueOf(true));
        when(requestB.queryParams(KeyEnum.NAME.toString())).thenReturn(String.valueOf(organA.getOrganEnum()));
        assertEquals("Organ removed", OrganController.delete(requestB, responseB));
    }

    @Test
    public void testDeleteDonated() {
        when(requestB.queryParams(DONATED)).thenReturn(String.valueOf(true));
        when(requestB.queryParams(KeyEnum.NAME.toString())).thenReturn(String.valueOf(organC.getOrganEnum()));
        assertEquals("Organ removed", OrganController.delete(requestB, responseB));
    }

    @Test
    public void testDeleteReceived() {
        when(requestB.queryParams(RECEIVED)).thenReturn(String.valueOf(true));
        when(requestB.queryParams(KeyEnum.NAME.toString())).thenReturn(String.valueOf(organB.getOrganEnum()));
        assertEquals("Organ removed", OrganController.delete(requestB, responseB));
    }

    @Test
    public void testDeleteRequired() {
        when(requestB.queryParams(REQUIRED)).thenReturn(String.valueOf(true));
        when(requestB.queryParams(KeyEnum.NAME.toString())).thenReturn(String.valueOf(organB.getOrganEnum()));
        assertEquals("Organ removed", OrganController.delete(requestB, responseB));
    }

    @Test
    public void testGetExpiredValid() throws SQLException {
        organDAO.setExpired(profileA, organC.getOrganEnum(), 1, "Test", userA.getStaffID());
        List<String> testResult = gson.fromJson(OrganController.getExpired(requestB, responseB), List.class);
        assertEquals(1, testResult.size());
    }

    @Test
    public void testGetExpiredInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), OrganController.getExpired(requestA, responseA));
    }

    @Test
    public void testSetExpiredValid() {
        assertEquals("Expiry set", OrganController.setExpired(requestC, responseC));
    }

    @Test
    public void testSetExpiredInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), OrganController.setExpired(requestA, responseA));
    }

    @After
    public void tearDown() throws SQLException {
        // Profile/Organ teardown.
        for (Profile profile : profileDAO.getAll()) {
            for (ExpiredOrgan organ : organDAO.getExpired(profile)) {
                organDAO.revertExpired(profile.getId(), organ.getOrgan());
            }
            for (OrganEnum organ : organDAO.getReceived(profile)) {
                organDAO.removeReceived(profile, organ);
            }
            for (OrganEnum organ : organDAO.getRequired(profile)) {
                organDAO.removeRequired(profile, organ);
            }
            for (OrganEnum organ : organDAO.getDonating(profile)) {
                organDAO.removeDonating(profile, organ);
            }
            for (OrganEnum organ : organDAO.getDonations(profile)) {
                organDAO.removeDonation(profile, organ);
            }
            profileDAO.remove(profile);
        }
        // User teardown.
        for (User user : userDAO.getAll()) {
            userDAO.remove(user);
        }
    }

    /**
     * Generates the request body for add organ tests.
     * @param category to add the organ to.
     * @return the body of the request.
     */
    private String generateBody(String category) {
        Map<String, String> body = new HashMap<>();
        body.put(KeyEnum.NAME.toString(), String.valueOf(OrganEnum.HEART));
        body.put(category, String.valueOf(true));
        return gson.toJson(body);
    }
}
