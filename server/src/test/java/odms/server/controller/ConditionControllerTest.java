package odms.server.controller;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.Profile;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.controller.ConditionController;
import server.model.database.DAOFactory;
import server.model.database.condition.ConditionDAO;
import server.model.database.profile.ProfileDAO;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConditionControllerTest extends CommonTestUtils {

    // Data access object variables.
    ProfileDAO profileDAO = DAOFactory.getProfileDao();
    ConditionDAO conditionDAO = DAOFactory.getConditionDao();

    // Profile variables.
    private Profile profileA;
    private LocalDate genericDate =
            LocalDate.of(1998, 3, 3);

    // Condition variables.
    private Condition conditionA;
    private Condition conditionB;

    // Request variables.
    private Request requestA;
    private Request requestB;

    // Response variables.
    private Response responseA;
    private Response responseB;

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

        conditionA = new Condition("Collapsed Lung", LocalDate.now(), false);
        conditionB = new Condition("Aids", LocalDate.now(), true);

        conditionDAO.add(profileA.getId(), conditionA);
        conditionA = conditionDAO.getAll(profileA.getId(), false).get(0);

        requestA = mock(Request.class);
        responseA = mock(Response.class);

        requestB = mock(Request.class);
        when(requestB.body()).thenReturn(gson.toJson(conditionB));
        responseB = mock(Response.class);
    }

    @Test
    public void testGetAllValid() {
        when(requestA.params(KeyEnum.ID.toString())).thenReturn(String.valueOf(profileA.getId()));
        List<Condition> expected = conditionDAO
                .getAll(profileA.getId());
        List<String> testResults = gson.fromJson(
                ConditionController.getAll(requestA, responseA), List.class);
        assertEquals(expected.size(), testResults.size());
    }

    @Test
    public void testGetAllInvalid() {
        // Profile id param is missing.
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ConditionController.getAll(requestA, responseA));
    }

    @Test
    public void testAddValid() {
        // Check condition count.
        assertEquals(0, conditionDAO.getAll(profileA.getId(), true).size());
        // Add new chronic condition.
        when(requestB.params(KeyEnum.ID.toString())).thenReturn(String.valueOf(profileA.getId()));
        assertEquals("Condition added successfully", ConditionController.add(requestB, responseB));
        conditionB = conditionDAO.getAll(profileA.getId(), true).get(0);
        // Check new condition count.
        assertEquals(1, conditionDAO.getAll(profileA.getId(), true).size());
        // Clean up.
        conditionDAO.remove(conditionB);
    }

    @Test
    public void testAddInvalid() {
        // Profile id param is missing.
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ConditionController.add(requestB, responseB));
    }

    @Test
    public void testEditValid() {
        conditionA.setDateCured(LocalDate.now());
        when(requestA.body()).thenReturn(gson.toJson(conditionA));
        assertEquals("Condition Updated", ConditionController.edit(requestA, responseA));
    }

    @Test
    public void testEditInvalid() {
        // Condition body is missing.
        when(requestA.body()).thenReturn(gson.toJson(new HashMap<>()));
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ConditionController.edit(requestA, responseA));
    }

    @Test
    public void testDeleteValid() {
        // Check condition count before deletion.
        assertEquals(1, conditionDAO.getAll(profileA.getId(), false).size());
        // Delete the condition.
        when(requestA.params(KeyEnum.ID.toString())).thenReturn(String.valueOf(conditionA.getId()));
        assertEquals("Condition Deleted", ConditionController.delete(requestA, responseA));
        // Check condition count after deletion.
        assertEquals(0, conditionDAO.getAll(profileA.getId(), false).size());
    }

    @Test
    public void testDeleteInvalid() {
        // Required attributes missing.
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), ConditionController.delete(requestA, responseA));
    }

    @After
    public void tearDown() throws SQLException {
        // Condition teardown.
        conditionDAO.remove(conditionA);
        // Profile teardown.
        profileDAO.remove(profileA);
    }
}
