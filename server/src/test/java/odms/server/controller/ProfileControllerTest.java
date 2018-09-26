package odms.server.controller;

import com.google.gson.Gson;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
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

    // Response variables.
    private Response responseA;
    private Response responseB;

    // General variables.
    private Gson gson = new Gson();
    private static final String KEY_SEARCH = "searchString";



    @Before
    public void setup() throws SQLException {
        profileA = new Profile("Alice", "Smith",
                genericDate, "LPO7236");
        profileA.setUsername("alices");
        profileA.setPassword("12345");
        profileA.setDateOfDeath(LocalDateTime.now().minusDays(1));
        profileDAO.add(profileA);
        profileA = profileDAO.get(profileA.getNhi());
        organDAO.addRequired(profileA, OrganEnum.LIVER);

        profileB = new Profile("Bob", "Smith",
                genericDate, "LPO3647");
        profileB.setUsername("bobs");
        profileB.setPassword("12345");
        profileDAO.add(profileB);
        profileB = profileDAO.get(profileB.getNhi());

        profileC = new Profile("Nick", "Currie",
                genericDate, "LPO3557");
        profileC.setUsername("nickc");
        profileC.setPassword("12345");
        profileC.setDateOfDeath(LocalDateTime.now().minusDays(1));
        profileDAO.add(profileC);
        profileC = profileDAO.get(profileC.getNhi());

        requestA = mock(Request.class);
        responseA = mock(Response.class);

        requestB = mock(Request.class);
        responseB = mock(Response.class);
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
    public void testGetAllSearch() {

    }

    @Test
    public void testGet() {

    }

    @Test
    public void testCreate() {

    }

    @Test
    public void testEdit() {

    }

    @Test
    public void testDelete() {

    }

    @Test
    public void testCount() {

    }

    @Test
    public void testHasPassword() {

    }

    @Test
    public void testCheckCredentials() {

    }

    @Test
    public void testSavePassword() {

    }

    @After
    public void tearDown() throws SQLException {
        // Profile/procedure teardown.
        for (Profile profile : profileDAO.getAll()) {
            profileDAO.remove(profile);
        }
    }
}
