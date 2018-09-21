package server.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.CountriesEnum;
import org.sonar.api.internal.google.gson.Gson;
import org.sonar.api.internal.google.gson.JsonObject;
import org.sonar.api.internal.google.gson.JsonParser;
import server.model.database.DAOFactory;
import server.model.database.country.CountryDAO;
import server.model.enums.DataTypeEnum;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

@Slf4j
public class CountriesController {

    /**
     * Prevent instantiation of static class.
     */
    private CountriesController() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a list of all the countries in the database
     * Takes an optional query parameter to define the validity of the country
     * @param req The request sent to the endpoint
     * @param res The response sent back
     * @return The response body as a list of Json object of countries
     */
    public static String getAll(Request req, Response res) {
        // This boolean is optional so it needs to be null
        Boolean valid = null;
        List<String> countries;

        // This query is optional so only assign it if it exists
        if (req.queryMap().hasKey(KeyEnum.VALID.toString())) {
            valid = Boolean.valueOf(req.queryParams(KeyEnum.VALID.toString()));
        }

        try {
            countries = getCountries(valid);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(countries);

        res.type(DataTypeEnum.JSON.toString());
        res.status(200);

        return responseBody;
    }

    /**
     * Updates a countries valid field to true or false
     * @param req The request sent to the endpoint
     * @param res The response sent back
     * @return The response body
     */
    public static String edit(Request req, Response res) {
        CountryDAO countryDAO = DAOFactory.getCountryDAO();
        JsonParser parser = new JsonParser();
        String name;
        boolean valid;

        try {
            JsonObject body = parser.parse(req.body()).getAsJsonObject();
            name = body.get(KeyEnum.NAME.toString()).getAsString();
            valid = body.get(KeyEnum.VALID.toString()).getAsBoolean();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }
        try {
            countryDAO.update(CountriesEnum.valueOf(name), valid);
            res.status(200);
            return "Country Updated";
        }
        catch (Exception e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }
    }

    /**
     * Gets the countries from the database
     * @param valid optional query to define the validity of the countries
     * @return A set of country names
     */
    private static List<String> getCountries(Boolean valid) {
        CountryDAO countryDAO = DAOFactory.getCountryDAO();

        if (valid == null) {
            return countryDAO.getAll();
        } else {
            return countryDAO.getAll(valid);
        }
    }
}
