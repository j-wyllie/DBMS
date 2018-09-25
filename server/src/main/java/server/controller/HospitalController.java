package server.controller;

import lombok.extern.slf4j.Slf4j;
import odms.commons.model.locations.Hospital;
import org.sonar.api.internal.google.gson.Gson;
import org.sonar.api.internal.google.gson.JsonObject;
import org.sonar.api.internal.google.gson.JsonParser;
import server.model.database.DAOFactory;
import server.model.database.locations.HospitalDAO;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.List;

@Slf4j
public class HospitalController {

    /**
     * Gets a list of all the hospitals in the database.
     * @param req The request sent to the endpoint
     * @param res The response sent back
     * @return The response body as a list of Json object of countries
     */
    public static String getAll(Request req, Response res) {
        List<Hospital> hospitals;

        try {
            HospitalDAO hospitalDAO = DAOFactory.getHospitalDAO();
            hospitals = hospitalDAO.getAll();
        } catch (SQLException e) {
            log.error(e.getMessage());
            res.status(500);
            return "Database Error";
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(hospitals);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }

    /**
     * Gets a single hospital from database.
     *
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String get(Request req, Response res) {
        HospitalDAO database = DAOFactory.getHospitalDAO();
        Hospital hospital;

        if (req.queryMap().hasKey("name")) {
            try {
                hospital = database.get(req.queryParams("name"));
            } catch (SQLException e) {
                log.error(e.getMessage());
                res.status(500);
                return "Database Error";
            }
        } else {
            try {
                hospital = database.get(Integer.valueOf(req.queryParams("id")));
            } catch (SQLException e) {
                log.error(e.getMessage());
                res.status(500);
                return "Database Error";
            }
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(hospital);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }

    /**
     * Updates a countries valid field to true or false.
     * @param req The request sent to the endpoint
     * @param res The response sent back
     * @return The response body
     */
    public static String edit(Request req, Response res) {
        HospitalDAO hospitalDAO = DAOFactory.getHospitalDAO();
        Gson parser = new Gson();
        Hospital hospital;

        try {
            hospital = parser.fromJson(req.body(), Hospital.class);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            res.status(400);
            return "Bad Request";
        }
        try {
            hospitalDAO.edit(hospital);
            res.status(200);
            return "Hospital Updated";
        } catch (SQLException e) {
            log.error(e.getMessage());
            res.status(500);
            return "Database Error";
        }
    }

    /**
     * Removes a hospital from the database.
     *
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String delete(Request req, Response res) {
        HospitalDAO database = DAOFactory.getHospitalDAO();
        String name;

        try {
            name = req.queryParams("name");
        } catch (Exception e) {
            log.error(e.getMessage());
            res.status(400);
            return "Bad Request";
        }
        try {

            database.remove(name);
        } catch (SQLException e) {
            log.error(e.getMessage());
            res.status(500);
            return "Database Error";
        }

        res.status(200);
        return "Hospital Deleted";
    }

    /**
     * Adds a new hospital to he database.
     *
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String create(Request req, Response res) {
        Gson gson = new Gson();
        HospitalDAO database = DAOFactory.getHospitalDAO();
        Hospital newHospital;

        try {
            newHospital = gson.fromJson(req.body(), Hospital.class);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            res.status(403);
            return "Forbidden";
        }

        if (newHospital != null) {
            try {
                database.add(newHospital);
            } catch (SQLException e) {
                log.error(e.getMessage());
                res.status(500);
                return "Database Error";
            }
        }
        res.status(201);
        return "Hospital Created";
    }
}
