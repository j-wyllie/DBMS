package odms.server.controller;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.UserNotFoundException;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import server.controller.ProfileController;
import server.model.database.DAOFactory;
import server.model.database.organ.OrganDAO;
import server.model.database.profile.ProfileDAO;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProfileControllerTest extends CommonTestUtils {

    // Data access object variables.
    private ProfileDAO profileDAO = DAOFactory.getProfileDao();
    private OrganDAO organDAO = DAOFactory.getOrganDao();

    // Profile variables.
    private Profile profileA;
    private Profile profileB;
    private Profile profileC;
    private LocalDate genericDate =
            LocalDate.of(1998, 3, 3);

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
    JsonParser parser = new JsonParser();
    private static final String KEY_SEARCH = "searchString";



    @Before
    public void setup() throws SQLException, UserNotFoundException, OrganConflictException {
        profileA = new Profile("Alice", "Smith",
                genericDate, "LPO7236");
        profileA.setUsername("LPO7236");
        profileDAO.add(profileA);
        profileA = profileDAO.get(profileA.getNhi());
        organDAO.addRequired(profileA, OrganEnum.LIVER);

        profileB = new Profile("Bob", "Smith",
                genericDate, "LPO3647");
        profileB.setUsername("bobs");
        profileB.setPassword("12345");
        profileB.setDateOfDeath(LocalDateTime.now().minusDays(1));
        profileDAO.add(profileB);
        profileB = profileDAO.get(profileB.getNhi());
        organDAO.addDonating(profileB, OrganEnum.LIVER);

        profileC = new Profile("Nick", "Currie",
                genericDate, "LPO3557");
        profileC.setUsername("LPO3557");
        profileC.setPassword("12345");
        profileC.setDateOfDeath(LocalDateTime.now().minusDays(1));
        profileDAO.add(profileC);
        profileDAO.savePassword(profileC.getNhi(), profileC.getPassword());
        profileC = profileDAO.get(profileC.getNhi());
        organDAO.addDonating(profileC, OrganEnum.LIVER);

        requestA = mock(Request.class);
        responseA = mock(Response.class);

        requestB = mock(Request.class);
        when(requestB.queryParams(KeyEnum.ID.toString())).thenReturn(String.valueOf(profileC.getId()));
        responseB = mock(Response.class);

        requestC = mock(Request.class);
        when(requestC.queryParams("username")).thenReturn(profileC.getUsername());
        responseC = mock(Response.class);
    }

    @Test
    public void testGetAllValid() {
        List<String> testResult = gson.fromJson(ProfileController.getAll(requestA, responseA), List.class);
        assertEquals(3, testResult.size());
    }

    @Test
    public void testGetAllInvalid() {
        when(requestA.queryParams(KEY_SEARCH)).thenReturn("Alice");
        when(requestA.queryParams("ageSearchInt")).thenReturn("invalidInt");
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProfileController.getAll(requestA, responseA));
    }

    @Test
    public void testGetReceivingValid() {
        List<String> testResult = gson.fromJson(ProfileController.getReceiving(requestA, responseA), List.class);
        assertEquals(1, testResult.size());
    }

    @Test
    public void testGetReceivingInvalid() {
        when(requestA.queryParams("organs")).thenReturn(String.valueOf(OrganEnum.LIVER));
        when(requestA.queryParams("lowerAgeRange")).thenReturn("invalidInt");
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProfileController.getReceiving(requestA, responseA));
    }

    @Test
    public void testGetAllDead() {
        List<String> testResult = gson.fromJson(ProfileController.getDead(requestA, responseA), List.class);
        assertEquals(2, testResult.size());
    }

    @Test
    public void testGetFilteredDead() {
        when(requestA.queryParams(KEY_SEARCH)).thenReturn("Bob");
        List<String> testResult = gson.fromJson(ProfileController.getDead(requestA, responseA), List.class);
        assertEquals(1, testResult.size());
    }

    @Test
    public void testGetAllSearch() {
        when(requestA.queryParams(KEY_SEARCH)).thenReturn("Alice");
        List<String> testResult = gson.fromJson(ProfileController.getAll(requestA, responseA), List.class);
        assertEquals(1, testResult.size());
    }

    @Test
    public void testGetValid() {
        assertEquals(profileC.getId(), gson.fromJson(ProfileController.get(requestB, responseB), Profile.class).getId());
    }

    @Test
    public void testGetInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProfileController.get(requestA, responseA));
    }

    @Test
    public void testCreateValid() {

    }

    @Test
    public void testCreateInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProfileController.create(requestA, responseA));
    }

    @Test
    public void testEditValid() {

    }

    @Test
    public void testEditInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProfileController.edit(requestA, responseA));
    }

    @Test
    public void testDeleteValid() {

    }

    @Test
    public void testDeleteInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProfileController.delete(requestA, responseA));
    }

    @Test
    public void testCount() {
        assert Integer.valueOf(ProfileController.count(requestA, responseA)) == 3;
    }

    @Test
    public void testHasPasswordValid() {
        when(requestA.queryParams("nhi")).thenReturn(String.valueOf(profileA.getNhi()));
        assertTrue(Boolean.valueOf(ProfileController.hasPassword(requestA, responseA)));
    }

    @Test
    public void testHasPasswordInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProfileController.hasPassword(requestA, responseA));
    }

    @Test
    public void testCheckCredentialsValid() {
        when(requestC.queryParams("password")).thenReturn("12345");
        int id = parser.parse(ProfileController
                .checkCredentials(requestC, responseC))
                .getAsJsonObject().get(KeyEnum.ID.toString())
                .getAsInt();
        assertEquals(String.valueOf(profileC.getId()), String.valueOf(id));
    }

    @Test
    public void testCheckCredentialsInvalidRequest() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProfileController.checkCredentials(requestA, responseA));
    }

    @Test
    public void testCheckCredentialsInvalidUsername() {
        when(requestA.queryParams("username")).thenReturn("nick");
        when(requestA.queryParams("password")).thenReturn("12345");
        assertEquals("Profile not found", ProfileController.checkCredentials(requestA, responseA));
    }

    @Test
    public void testCheckCredentialsUnauthorized() {
        when(requestC.queryParams("password")).thenReturn("invalid");
        assertEquals("Unauthorized", ProfileController.checkCredentials(requestC, responseC));
    }

    @Test
    public void testSavePasswordValid() {
        when(requestA.queryParams("username")).thenReturn(profileA.getNhi());
        when(requestA.queryParams("password")).thenReturn("valid");
        assertEquals("Password Set", ProfileController.savePassword(requestA, responseA));
    }

    @Test
    public void testSavePasswordInvalidRequest() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProfileController.savePassword(requestA, responseA));
    }

    @Test
    public void testSavePasswordInvalidUsername() {
        when(requestA.queryParams("username")).thenReturn("nick");
        when(requestA.queryParams("password")).thenReturn("valid");
        assertEquals("Profile not found", ProfileController.savePassword(requestA, responseA));
    }

    @After
    public void tearDown() throws SQLException {
        // Profile/procedure teardown.
        for (Profile profile : profileDAO.getAll()) {
            profileDAO.remove(profile);
        }
    }
}
