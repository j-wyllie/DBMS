package server.controller;

import java.sql.SQLException;
import java.util.List;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.OrganEnum;
import org.sonar.api.internal.google.gson.Gson;
import org.sonar.api.internal.google.gson.JsonObject;
import org.sonar.api.internal.google.gson.JsonParser;
import server.model.database.DAOFactory;
import server.model.database.country.CountryDAO;
import spark.Request;
import spark.Response;

public class CountriesController {

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
        if (req.queryMap().hasKey("valid")) {
            valid = Boolean.valueOf(req.queryParams("valid"));
        }

        try {
            countries = getCountries(valid);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(countries);

        res.type("application/json");
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
            name = body.get("name").getAsString();
            valid = body.get("valid").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            res.status(400);
            return "Bad Request";
        }
        try {
            countryDAO.update(CountriesEnum.valueOf(name), valid);
            res.status(200);
            return "Country Updated";
        }
        catch (Exception e) {
            res.status(400);
            return "Bad Request";
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
