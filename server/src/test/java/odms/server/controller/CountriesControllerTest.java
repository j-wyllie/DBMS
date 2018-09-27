package odms.server.controller;

import com.google.gson.Gson;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.Profile;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.controller.CountriesController;
import server.model.database.DAOFactory;
import server.model.database.country.CountryDAO;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CountriesControllerTest extends CommonTestUtils {

    // Data access object variables.
    CountryDAO countriesDAO = DAOFactory.getCountryDAO();

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
        countriesDAO.update(CountriesEnum.NZ, true);

        // Create request to set NZ validity to false.
        Map<String, String> body = new HashMap<>();
        body.put(KeyEnum.NAME.toString(), String.valueOf(CountriesEnum.NZ));
        body.put(KeyEnum.VALID.toString(), String.valueOf(false));
        when(requestB.body()).thenReturn(gson.toJson(body));

    }

    @Test
    public void testGetAll() {
        List<String> testResult = gson.fromJson(CountriesController.getAll(requestA, responseA), List.class);
        assertEquals(countriesDAO.getAll().size(), testResult.size());
    }

    @Test
    public void testGetCountriesValid() {
        when(requestA.queryParams(KeyEnum.VALID.toString())).thenReturn(String.valueOf(true));
        List<String> testResult = gson.fromJson(CountriesController.getAll(requestA, responseA), List.class);
        assertEquals(countriesDAO.getAll(true).size(), testResult.size());
    }

    @Test
    public void testGetCountriesInvalid() {
        when(requestA.queryParams(KeyEnum.VALID.toString())).thenReturn(String.valueOf(false));
        List<String> testResult = gson.fromJson(CountriesController.getAll(requestA, responseA), List.class);
        assertEquals(countriesDAO.getAll(false).size(), testResult.size());
    }

    @Test
    public void testEditValid() {
        List<String> testResult;

        // Check NZ validity is true.
        when(requestA.queryParams(KeyEnum.VALID.toString())).thenReturn(String.valueOf(true));
        testResult = gson.fromJson(CountriesController.getAll(requestA, responseA), List.class);
        assertTrue(testResult.contains(CountriesEnum.NZ.getName()));

        CountriesController.edit(requestB, responseB);
        // Check NZ validity is false.
        when(requestA.queryParams(KeyEnum.VALID.toString())).thenReturn(String.valueOf(false));
        testResult = gson.fromJson(CountriesController.getAll(requestA, responseA), List.class);
        assertTrue(testResult.contains(CountriesEnum.NZ.getName()));
    }

    @Test
    public void testEditInvalid() {
        // Required fields are missing from the body.
        String body = CountriesController.edit(requestA, responseA);
        assertEquals(ResponseMsgEnum.BAD_REQUEST.toString(), body);
    }
}
