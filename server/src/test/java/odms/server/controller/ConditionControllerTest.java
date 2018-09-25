package odms.server.controller;

import com.google.gson.Gson;
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
        assertEquals(1, ConditionController.getAll(requestA, responseA).length());
        assertEquals(200, responseA.status());
    }

    @Test
    public void testGetAllInvalid() {
        // Profile id param is missing.
        assertEquals(0, ConditionController.getAll(requestA, responseA).length());
        assertEquals(400, responseA.status());
    }

    @Test
    public void testAddValid() {
        when(requestB.params(KeyEnum.ID.toString())).thenReturn(String.valueOf(profileA.getId()));
        ConditionController.add(requestB, responseB);
        assertEquals(200, responseB.status());
    }

    @Test
    public void testAddInvalid() {
        // Profile id param is missing.
        ConditionController.add(requestB, responseB);
        assertEquals(400, responseB.status());
    }

    @Test
    public void testEditValid() {
        conditionA.setDateCured(LocalDate.now());
        when(requestA.body()).thenReturn(gson.toJson(conditionA));
        ConditionController.edit(requestA, responseA);
        assertEquals(200, responseA.status());
    }

    @Test
    public void testEditInvalid() {
        // Condition body is missing.
        ConditionController.edit(requestA, responseA);
        assertEquals(400, responseA.status());
    }

    @Test
    public void testDeleteValid() {
        when(requestA.queryParams(KeyEnum.ID.toString())).thenReturn(String.valueOf(conditionA.getId()));
        ConditionController.delete(requestA, responseA);
        assertEquals(200, responseA.status());
    }

    @Test
    public void testDeleteInvalid() {
        ConditionController.delete(requestA, responseA);
        assertEquals(400, responseA.status());
    }

    @After
    public void tearDown() throws SQLException {
        profileDAO.remove(profileA);
    }
}
