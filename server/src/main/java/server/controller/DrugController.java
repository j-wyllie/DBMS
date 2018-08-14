package server.controller;

import java.util.List;
import odms.commons.model.medications.Drug;
import odms.commons.model.profile.Profile;
import org.sonar.api.internal.google.gson.Gson;
import server.model.database.DAOFactory;
import server.model.database.medication.MedicationDAO;
import spark.Request;
import spark.Response;

public class DrugController {

    /**
     * Gets all drugs for a profile in persistent storage.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return a list of all drugs for the profile stored.
     */
    public static String getAll(Request req, Response res) {
        MedicationDAO database = DAOFactory.getMedicationDao();
        List<Drug> drugs;
        int profileId;
        boolean current;

        try {
            profileId = Integer.valueOf(req.params("id"));
            current = Boolean.valueOf(req.queryParams("current"));
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        try {
            drugs = database.getAll(new Profile(profileId), current);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(drugs);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }

    /**
     * Adds a new drug to a profile to persistent storage.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String add(Request req, Response res) {
        Gson gson = new Gson();
        MedicationDAO database = DAOFactory.getMedicationDao();
        Drug newDrug;
        int profileId;
        boolean current;

        try {
            profileId = Integer.valueOf(req.params("id"));
            newDrug = gson.fromJson(req.body(), Drug.class);
            current = Boolean.valueOf(req.queryParams("current"));
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }
        try {
            database.add(newDrug, new Profile(profileId), current);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        res.status(201);
        return "Drug added";
    }

    /**
     * Edits a currently stored drug for a profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String edit(Request req, Response res) {
        Gson gson = new Gson();
        MedicationDAO database = DAOFactory.getMedicationDao();
        Drug newDrug;
        boolean current;

        try {
            newDrug = gson.fromJson(req.body(), Drug.class);
            current = Boolean.valueOf(req.queryParams("current"));
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        try {
            database.update(newDrug, current);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        res.status(200);
        return "Drug updated";
    }

    /**
     * Deletes a drug for a profile from persistent storage.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String delete(Request req, Response res) {
        Gson gson = new Gson();
        MedicationDAO database = DAOFactory.getMedicationDao();
        Drug newDrug;

        try {
            newDrug = new Drug(Integer.valueOf(req.params("id")), null);
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        try {
            database.remove(newDrug);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        res.status(200);
        return "Drug deleted";
    }
}
