package server.controller;

import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.CountriesEnum;
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
        // This boolean is optional so it needs to be null.
        Boolean valid = null;
        List<String> countries;

        // This query is optional so only assign it if it exists
        if (req.queryParams(KeyEnum.VALID.toString()) != null) {
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
        CountriesEnum name;
        boolean valid;

        try {
            JsonObject body = parser.parse(req.body()).getAsJsonObject();
            JsonElement country = body.get(KeyEnum.NAME.toString());
            JsonElement validCountry = body.get(KeyEnum.VALID.toString());
            if (country == null || validCountry == null) {
                throw new IllegalArgumentException("required fields missing from body.");
            }
            name = CountriesEnum.valueOf(country.getAsString());
            valid = validCountry.getAsBoolean();

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }
        try {
            countryDAO.update(name, valid);
            res.status(200);
            return "Country Updated";
        } catch (SQLException e) {
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
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
