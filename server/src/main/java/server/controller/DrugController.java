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

    public static String getAll(Request req, Response res) {
        MedicationDAO database = DAOFactory.getMedicationDao();
        List<Drug> drugs;

        int profileId = Integer.valueOf(req.params("id"));
        boolean current = Boolean.valueOf(req.queryParams("current"));

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

    public static String add(Request req, Response res) {
        Gson gson = new Gson();
        MedicationDAO database = DAOFactory.getMedicationDao();
        Drug newDrug;
        int profileId;
        boolean current;

        try {
            profileId = Integer.valueOf(req.params("id"));
            newDrug = gson.fromJson(req.body(), Drug.class);
            current = Boolean.valueOf(req.queryMap("current").toString());
        } catch (Exception e) {
            res.status(500);
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

    public static String edit(Request req, Response res) {
        Gson gson = new Gson();
        MedicationDAO database = DAOFactory.getMedicationDao();
        Drug newDrug;
        boolean current;

        try {
            newDrug = gson.fromJson(req.body(), Drug.class);
            current = Boolean.valueOf(req.queryMap("current").toString());
        } catch (Exception e) {
            res.status(500);
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

    public static String delete(Request req, Response res) {
        Gson gson = new Gson();
        MedicationDAO database = DAOFactory.getMedicationDao();
        Drug newDrug;

        try {
            newDrug = gson.fromJson(req.body(), Drug.class);
        } catch (Exception e) {
            res.status(500);
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
