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
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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

        drugA = new Drug("Reserpine");
        drugB = new Drug("Ibuprofen");

        drugDAO.add(drugA, profileA.getId(), true);
        drugA = drugDAO.getAll(profileA.getId(), true).get(0);

        requestA = mock(Request.class);
        when(requestA.params(KeyEnum.ID.toString())).thenReturn(profileA.getId().toString());
        when(requestA.queryParams("current")).thenReturn(String.valueOf(true));
        responseA = mock(Response.class);

        requestB = mock(Request.class);
        when(requestB.body()).thenReturn(gson.toJson(drugB));
        responseB = mock(Response.class);
    }

    @Test
    public void testGetAllValid() {
        System.out.println(DrugController.getAll(requestA, responseA));
        List<String> testResult = gson.fromJson(DrugController.getAll(requestA, responseA), List.class);
        assertEquals(drugDAO.getAll(profileA.getId(), true).size(), testResult.size());
    }

    @Test
    public void testGetAllInvalid() {

    }

    @Test
    public void testAddValid() {

    }

    @Test
    public void testAddInvalid() {

    }

    @Test
    public void testEditValid() {

    }

    @Test
    public void testDeleteInvalid() {

    }

    @After
    public void tearDown() throws SQLException {
        drugDAO.remove(drugA);
        profileDAO.remove(profileA);
    }
}
