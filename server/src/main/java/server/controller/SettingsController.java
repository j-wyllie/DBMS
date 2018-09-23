package server.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.UserType;
import org.sonar.api.internal.google.gson.Gson;
import org.sonar.api.internal.google.gson.JsonObject;
import org.sonar.api.internal.google.gson.JsonParser;
import server.model.database.DAOFactory;
import server.model.database.settings.SettingsDAO;
import server.model.enums.DataTypeEnum;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

@Slf4j
public class SettingsController {

    /**
     * Prevent instantiation of static class.
     */
    private SettingsController() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a list of all the countries in the database.
     * Takes an optional query parameter to define the validity of the country.
     * @param req the request sent to the endpoint.
     * @param res the response sent back.
     * @return the response body as a list of Json object of countries.
     */
    public static String getAllCountries(Request req, Response res) {
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
     * Updates a countries valid field to true or false.
     * @param req the request sent to the endpoint.
     * @param res the response sent back.
     * @return the response body.
     */
    public static String editCountries(Request req, Response res) {
        SettingsDAO countryDAO = DAOFactory.getSettingsDAO();
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
            countryDAO.updateCountries(CountriesEnum.valueOf(name), valid);
            res.status(200);
            return "Country Updated";
        }
        catch (Exception e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }
    }

    /**
     * Gets the countries from the database.
     * @param valid optional query to define the validity of the countries.
     * @return a set of country names.
     */
    private static List<String> getCountries(Boolean valid) {
        SettingsDAO countryDAO = DAOFactory.getSettingsDAO();

        if (valid == null) {
            return countryDAO.getAllCountries();
        } else {
            return countryDAO.getAllCountries(valid);
        }
    }

    /**
     * Gets the current users session locale from the database.
     * @param req to the server.
     * @param res from the server.
     * @return the datetime and number locales for the user.
     */
    public static String getLocale(Request req, Response res) {
        SettingsDAO database = DAOFactory.getSettingsDAO();
        int id;
        UserType userType;
        Map<String, String> body = new HashMap<>();

        try {
            id = Integer.valueOf(req.queryParams(KeyEnum.ID.toString()));
            userType = UserType.valueOf(req.queryParams(KeyEnum.USERTYPE.toString()));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }
        try {
            String dateTimeFormat = database.getDateTimeFormat(id, userType);
            String numberFormat = database.getNumberFormat(id, userType);
            body.put(KeyEnum.DATETIMELOCALE.toString(), dateTimeFormat);
            body.put(KeyEnum.NUMBERLOCALE.toString(), numberFormat);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return e.getMessage();
        }
        Gson gson = new Gson();
        String responseBody = gson.toJson(body);

        res.type(DataTypeEnum.JSON.toString());
        res.status(200);
        return responseBody;
    }

    /**
     * Sets the locales for a user.
     * @param req to the server.
     * @param res from the server.
     * @return to status of the update to the locale settings.
     */
    public static String setLocale(Request req, Response res) {
        int id;
        UserType userType;
        String numberFormat;
        String dateTimeFormat;
        Map<String, String> body = new HashMap<>();
        SettingsDAO database = DAOFactory.getSettingsDAO();

        try {
            id = Integer.valueOf(req.queryParams(KeyEnum.ID.toString()));
            userType = UserType.valueOf(req.queryParams(KeyEnum.USERTYPE.toString()));
            dateTimeFormat = body.get(KeyEnum.DATETIMELOCALE.toString());
            numberFormat = body.get(KeyEnum.NUMBERLOCALE.toString());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }
        try {
            database.setDateTimeFormat(id, userType, dateTimeFormat);
            database.setNumberFormat(id, userType, numberFormat);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return e.getMessage();
        }

        res.status(200);
        return "Locale Updated";
    }
}
