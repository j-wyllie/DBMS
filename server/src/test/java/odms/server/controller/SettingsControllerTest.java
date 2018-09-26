package odms.server.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odms.commons.model.enums.CountriesEnum;
import odms.server.CommonTestUtils;
import org.junit.Before;
import org.junit.Test;
import server.controller.SettingsController;
import server.model.database.DAOFactory;
import server.model.database.settings.SettingsDAO;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

public class SettingsControllerTest extends CommonTestUtils {

    // Data access object variables.
    SettingsDAO settingsDAO = DAOFactory.getSettingsDAO();

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
        requestA = mock(Request.class);
        when(requestA.body()).thenReturn(gson.toJson(new HashMap<>()));
        responseA = mock(Response.class);

        requestB = mock(Request.class);
        responseB = mock(Response.class);

        // Set NZ validity to true.
        settingsDAO.updateCountries(CountriesEnum.NZ, true);

        // Create request to set NZ validity to false.
        Map<String, String> body = new HashMap<>();
        body.put(KeyEnum.NAME.toString(), String.valueOf(CountriesEnum.NZ));
        body.put(KeyEnum.VALID.toString(), String.valueOf(false));
        when(requestB.body()).thenReturn(gson.toJson(body));

    }

    @Test
    public void testGetAll() {
        List<String> testResult = gson.fromJson(SettingsController.getAllCountries(requestA, responseA), List.class);
        assertEquals(settingsDAO.getAllCountries().size(), testResult.size());
    }

    @Test
    public void testGetCountriesValid() {
        when(requestA.queryParams(KeyEnum.VALID.toString())).thenReturn(String.valueOf(true));
        List<String> testResult = gson.fromJson(SettingsController.getAllCountries(requestA, responseA), List.class);
        assertEquals(settingsDAO.getAllCountries(true).size(), testResult.size());
    }

    @Test
    public void testGetCountriesInvalid() {
        when(requestA.queryParams(KeyEnum.VALID.toString())).thenReturn(String.valueOf(false));
        List<String> testResult = gson.fromJson(SettingsController.getAllCountries(requestA, responseA), List.class);
        assertEquals(settingsDAO.getAllCountries(false).size(), testResult.size());
    }

    @Test
    public void testEditValid() {
        List<String> testResult;

        // Check NZ validity is true.
        when(requestA.queryParams(KeyEnum.VALID.toString())).thenReturn(String.valueOf(true));
        testResult = gson.fromJson(SettingsController.getAllCountries(requestA, responseA), List.class);
        assertTrue(testResult.contains(CountriesEnum.NZ.getName()));

        SettingsController.editCountries(requestB, responseB);
        // Check NZ validity is false.
        when(requestA.queryParams(KeyEnum.VALID.toString())).thenReturn(String.valueOf(false));
        testResult = gson.fromJson(SettingsController.getAllCountries(requestA, responseA), List.class);
        assertTrue(testResult.contains(CountriesEnum.NZ.getName()));
    }

    @Test
    public void testEditInvalid() {
        // Required fields are missing from the body.
        String body = SettingsController.editCountries(requestA, responseA);
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), body);
    }
}
