package odms.server.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.ExpiredOrgan;
import odms.commons.model.profile.Organ;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.controller.OrganController;
import server.model.database.DAOFactory;
import server.model.database.organ.OrganDAO;
import server.model.database.profile.ProfileDAO;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

public class OrganControllerTest extends CommonTestUtils {

    // Data access object variables.
    ProfileDAO profileDAO = DAOFactory.getProfileDao();
    OrganDAO organDAO = DAOFactory.getOrganDao();

    // Profile variables.
    private Profile profileA;
    private LocalDate genericDate =
            LocalDate.of(1998, 3, 3);

    // Drug variables.
    private Organ organA;
    private Organ organB;

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
    Gson gson = new Gson();


    @Before
    public void setup() throws SQLException, OrganConflictException {
        profileA = new Profile("Alice", "Smith",
                genericDate, "LPO7236");
        profileDAO.add(profileA);
        profileA.setUsername("alices");
        profileA.setPassword("12345");
        profileA = profileDAO.get(profileA.getNhi());

        organA = new Organ(OrganEnum.HEART, LocalDateTime.now());
        organDAO.addDonating(profileA, organA.getOrganEnum());

        requestA = mock(Request.class);
        responseA = mock(Response.class);
    }

    @Test
    public void testGetAllValid() {

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
    public void testAdd() {

    }

    @Test
    public void testDelete() {

    }

    @Test
    public void testGetOrgans() {

    }

    @Test
    public void testAddOrgan() {

    }

    @Test
    public void testRemoveOrgan() {

    }

    @Test
    public void testGetExpired() {

    }

    @Test
    public void testSetExpired() {

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
    }
}
