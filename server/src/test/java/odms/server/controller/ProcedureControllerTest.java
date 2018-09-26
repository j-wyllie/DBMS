package odms.server.controller;

import com.google.gson.Gson;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.controller.ProcedureController;
import server.model.database.DAOFactory;
import server.model.database.condition.ConditionDAO;
import server.model.database.organ.OrganDAO;
import server.model.database.procedure.ProcedureDAO;
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

public class ProcedureControllerTest extends CommonTestUtils {

    // Data access object variables.
    private ProfileDAO profileDAO = DAOFactory.getProfileDao();
    private ProcedureDAO procedureDAO = DAOFactory.getProcedureDao();
    private OrganDAO organDAO = DAOFactory.getOrganDao();

    // Profile variables.
    private Profile profileA;
    private LocalDate genericDate =
            LocalDate.of(1998, 3, 3);

    // Condition variables.
    private Procedure procedureA;
    private Procedure procedureB;
    private Procedure procedureC;
    private Procedure procedureD;

    // Request variables.
    private Request requestA;
    private Request requestB;
    private Request requestC;
    private Request requestD;

    // Response variables.
    private Response responseA;
    private Response responseB;
    private Response responseC;
    private Response responseD;

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
        organDAO.addDonating(profileA, OrganEnum.BONE);
        organDAO.addDonating(profileA, OrganEnum.BONE_MARROW);

        procedureA = new Procedure("Lung Transplant", genericDate, "");
        procedureB = new Procedure("Heart Transplant", genericDate, "");
        procedureC = new Procedure("Bone Marrow Transplant", LocalDate.now().plusDays(7), "");
        procedureC.addAffectedOrgan(profileA, OrganEnum.BONE);
        procedureD = new Procedure("Cornea Transplant", LocalDate.now().plusDays(7), "");

        procedureDAO.add(profileA.getId(), procedureA);
        procedureA = procedureDAO.getAll(profileA.getId(), false).get(0);
        procedureDAO.add(profileA.getId(), procedureB);
        procedureB = procedureDAO.getAll(profileA.getId(), false).get(1);
        procedureDAO.add(profileA.getId(), procedureC);
        procedureC = procedureDAO.getAll(profileA.getId(), true).get(0);


        requestA = mock(Request.class);
        responseA = mock(Response.class);

        requestB = mock(Request.class);
        when(requestB.params(KeyEnum.ID.toString())).thenReturn(String.valueOf(profileA.getId()));
        responseB = mock(Response.class);

        requestC = mock(Request.class);
        when(requestC.params(KeyEnum.ID.toString())).thenReturn(String.valueOf(procedureC.getId()));
        responseC = mock(Response.class);

        requestD = mock(Request.class);
        procedureB.setDate(LocalDate.now().plusDays(7));
        when(requestD.body()).thenReturn(gson.toJson(procedureB));
        when(requestD.queryParams(KeyEnum.PENDING.toString())).thenReturn(String.valueOf(true));
        responseD = mock(Response.class);
    }

    @Test
    public void testGetAllPrevious() {
        when(requestB.queryParams(KeyEnum.PENDING.toString())).thenReturn(String.valueOf(false));
        List<String> testResult = gson.fromJson(ProcedureController.getAll(requestB, responseB), List.class);
        assertEquals(2, testResult.size());
    }

    @Test
    public void testGetAllPending() {
        when(requestB.queryParams(KeyEnum.PENDING.toString())).thenReturn(String.valueOf(true));
        List<String> testResult = gson.fromJson(ProcedureController.getAll(requestB, responseB), List.class);
        assertEquals(1, testResult.size());
    }

    @Test
    public void testGetAllInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProcedureController.getAll(requestA, responseA));
    }

    @Test
    public void testAddValid() {
        when(requestB.body()).thenReturn(gson.toJson(procedureD));
        assertEquals("Procedure Created", ProcedureController.add(requestB, responseB));
    }

    @Test
    public void testAddInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProcedureController.add(requestA, responseA));
    }

    @Test
    public void testEditValid() {
        assertEquals("Procedure Updated", ProcedureController.edit(requestD, responseD));
    }

    @Test
    public void testEditInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProcedureController.edit(requestA, responseA));
    }

    @Test
    public void testDeleteValid() {
        assertEquals("Procedure Deleted", ProcedureController.delete(requestC, responseC));
    }

    @Test
    public void testDeleteInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProcedureController.delete(requestA, responseA));
    }

    @Test
    public void testGetOrgansValid() {
        List<String> testResult = gson.fromJson(ProcedureController.getOrgans(requestC, responseC), List.class);
        assertEquals(1, testResult.size());
    }

    @Test
    public void testGetOrgansInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProcedureController.getOrgans(requestA, responseA));
    }

    @Test
    public void testDeleteOrganValid() {
        when(requestC.queryParams(KeyEnum.NAME.toString())).thenReturn(String.valueOf(OrganEnum.BONE));
        assertEquals("Affected organ removed", ProcedureController.deleteOrgan(requestC, responseC));
        List<String> testResult = gson.fromJson(ProcedureController.getOrgans(requestC, responseC), List.class);
        assertEquals(0, testResult.size());
    }

    @Test
    public void testDeleteOrganInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProcedureController.deleteOrgan(requestA, responseA));
    }

    @Test
    public void testAddOrganValid() {
        when(requestC.body()).thenReturn(gson.toJson(OrganEnum.BONE_MARROW));
        assertEquals("Affected organ added", ProcedureController.addOrgan(requestC, responseC));
        List<String> testResult = gson.fromJson(ProcedureController.getOrgans(requestC, responseC), List.class);
        assertEquals(2, testResult.size());
    }

    @Test
    public void testAddOrganInvalid() {
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ProcedureController.addOrgan(requestA, responseA));
    }

    @After
    public void tearDown() throws SQLException {
        // Profile/procedure teardown.
        for (Profile profile : profileDAO.getAll()) {
            for (Procedure procedure : procedureDAO.getAll(profile.getId(), true)) {
                procedureDAO.remove(procedure);
            }
            for (Procedure procedure : procedureDAO.getAll(profile.getId(), false)) {
                procedureDAO.remove(procedure);
            }
            profileDAO.remove(profile);
        }
    }
}
