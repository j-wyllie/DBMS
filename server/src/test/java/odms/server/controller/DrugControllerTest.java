package odms.server.controller;

import com.google.gson.Gson;
import odms.commons.model.medications.Drug;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.Profile;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.controller.DrugController;
import server.model.database.DAOFactory;
import server.model.database.medication.MedicationDAO;
import server.model.database.profile.ProfileDAO;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DrugControllerTest extends CommonTestUtils {

    // Data access object variables.
    ProfileDAO profileDAO = DAOFactory.getProfileDao();
    MedicationDAO drugDAO = DAOFactory.getMedicationDao();

    // Profile variables.
    private Profile profileA;
    private LocalDate genericDate =
            LocalDate.of(1998, 3, 3);

    // Drug variables.
    private Drug drugA;
    private Drug drugB;

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
    public void setup() throws SQLException {
        profileA = new Profile("Alice", "Smith",
                genericDate, "LPO7236");
        profileDAO.add(profileA);
        profileA.setUsername("alices");
        profileA.setPassword("12345");
        profileA = profileDAO.get(profileA.getNhi());

        drugA = new Drug("Reserpine");
        drugB = new Drug("Ibuprofen");

        drugDAO.add(drugA, profileA.getId(), true);
        drugA = drugDAO.getAll(profileA.getId(), true).get(0);

        requestA = mock(Request.class);
        when(requestA.params(KeyEnum.ID.toString())).thenReturn(profileA.getId().toString());
        when(requestA.queryParams("current")).thenReturn(String.valueOf(true));
        responseA = mock(Response.class);

        requestB = mock(Request.class);
        when(requestB.params(KeyEnum.ID.toString())).thenReturn(profileA.getId().toString());
        when(requestB.body()).thenReturn(gson.toJson(drugB));
        when(requestB.queryParams("current")).thenReturn(String.valueOf(false));
        responseB = mock(Response.class);

        requestC = mock(Request.class);
        responseC = mock(Response.class);

        requestD = mock(Request.class);
        when(requestD.body()).thenReturn(gson.toJson(drugA));
        when(requestD.queryParams("current")).thenReturn(String.valueOf(false));
        responseD = mock(Response.class);

        requestE = mock(Request.class);
        when(requestE.params(KeyEnum.ID.toString())).thenReturn(drugA.getId().toString());
        responseE = mock(Response.class);
    }

    @Test
    public void testGetAllValid() {
        List<String> testResult = gson.fromJson(DrugController.getAll(requestA, responseA), List.class);
        assertEquals(drugDAO.getAll(profileA.getId(), true).size(), testResult.size());
    }

    @Test
    public void testGetAllInvalid() {
        // Required parameters missing.
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), DrugController.getAll(requestC, responseC));
    }

    @Test
    public void testAddValid() {
        // Check drug is not stored.
        assertFalse(drugDAO.getAll(profileA.getId(), true).contains(drugB));
        assertFalse(drugDAO.getAll(profileA.getId(), false).contains(drugB));
        // Store the new drug.
        assertEquals("Drug added", DrugController.add(requestB, responseB));
        drugB = drugDAO.getAll(profileA.getId(), false).get(0);
        // Check drug is stored correctly.
        assertFalse(drugDAO.getAll(profileA.getId(), true).contains(drugB));
        assertEquals(drugDAO.getAll(profileA.getId(), false).get(0).getId(), drugB.getId());
        drugDAO.remove(drugB);
    }

    @Test
    public void testAddInvalid() {
        // Required parameters missing.
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), DrugController.add(requestC, responseC));
    }

    @Test
    public void testEditValid() {
        // Check counts before edit.
        assertEquals(1, drugDAO.getAll(profileA.getId(), true).size());
        assertEquals(0, drugDAO.getAll(profileA.getId(), false).size());
        // Edit drug.
        assertEquals("Drug updated", DrugController.edit(requestD, responseD));
        // Check counts after edit.
        assertEquals(0, drugDAO.getAll(profileA.getId(), true).size());
        assertEquals(1, drugDAO.getAll(profileA.getId(), false).size());
    }

    @Test
    public void testEditInvalid() {
        // Required parameters missing.
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), DrugController.edit(requestC, responseC));
    }

    @Test
    public void testDeleteValid() {
        // Check counts before delete.
        assertEquals(1, drugDAO.getAll(profileA.getId(), true).size());
        // Delete the drug.
        assertEquals("Drug deleted", DrugController.delete(requestE, responseE));
        // Check counts after delete.
        assertEquals(0, drugDAO.getAll(profileA.getId(), true).size());
    }

    @Test
    public void testDeleteInvalid() {
        // Required parameters missing.
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), DrugController.delete(requestC, responseC));
    }

    @After
    public void tearDown() throws SQLException {
        // Drug teardown.
        drugDAO.remove(drugA);

        // Profile teardown.
        profileDAO.remove(profileA);
    }
}
