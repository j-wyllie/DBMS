package server.controller;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Procedure;
import org.sonar.api.internal.google.gson.Gson;
import server.model.database.DAOFactory;
import server.model.database.procedure.ProcedureDAO;
import spark.Request;
import spark.Response;

import java.util.List;

public class ProcedureController {

    /**
     * Gets a list of all procedures.
     *
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String getAll(Request req, Response res) {
        ProcedureDAO database = DAOFactory.getProcedureDao();
        List<Procedure> procedures;
        int profileId;
        boolean pending;

        try {
            profileId = Integer.valueOf(req.params("id"));
            pending = Boolean.valueOf(req.queryParams("pending"));
        } catch (Exception e) {
            res.status(500);
            return "Bad Request";
        }

        try {
            procedures = database.getAll(profileId, pending);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(procedures);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }

    /**
     * Adds a procedure to a profile in storage.
     *
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String add(Request req, Response res) {
        Gson gson = new Gson();
        ProcedureDAO database = DAOFactory.getProcedureDao();
        Procedure newProcedure;
        int profileId;

        try {
            profileId = Integer.valueOf(req.params("id"));
            newProcedure = gson.fromJson(req.body(), Procedure.class);
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        try {
            database.add(profileId, newProcedure);
        } catch (Exception e) {
            res.status(500);
            return "Internal Server Error";
        }

        res.status(201);
        return "Procedure Created";
    }

    /**
     * Edits a procedure stored for a profile.
     *
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String edit(Request req, Response res) {
        Gson gson = new Gson();
        ProcedureDAO database = DAOFactory.getProcedureDao();
        Procedure newProcedure;
        boolean pending;

        try {
            newProcedure = gson.fromJson(req.body(), Procedure.class);
            pending = Boolean.valueOf(req.queryParams("pending"));
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        try {
            database.update(newProcedure, pending);
        } catch (Exception e) {
            res.status(500);
            return "Internal Server Error";
        }

        res.status(201);
        return "profile Created";
    }

    /**
     * Removes a procedure from a stored profile.
     *
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String delete(Request req, Response res) {
        Gson gson = new Gson();
        ProcedureDAO database = DAOFactory.getProcedureDao();
        Procedure newProcedure;

        try {
            newProcedure = new Procedure(Integer.parseInt(req.params("id")));
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }
        try {

            database.remove(newProcedure);
        } catch (Exception e) {
            res.status(500);
            return "Internal Server Error";
        }

        res.status(200);
        return "Procedure Deleted";
    }

    /**
     * Gets all organs affected by a procedure for a stored profile.
     * 
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String getOrgans(Request req, Response res) {
        ProcedureDAO database = DAOFactory.getProcedureDao();
        List<OrganEnum> organs;
        int id;

        try {
            id = Integer.valueOf(req.params("id"));
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        try {
            organs = database.getAffectedOrgans(id);
        } catch (Exception e) {
            res.status(500);
            return "Internal Server Error";
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(organs);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }

    /**
     * Removes an affected organ from a procedure for a stored profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String deleteOrgan(Request req, Response res) {
        ProcedureDAO database = DAOFactory.getProcedureDao();
        int id;
        String organ;

        try {
            id = Integer.valueOf(req.params("id"));
            organ = req.queryParams("name");
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        try {
            database.removeAffectedOrgan(new Procedure(id), OrganEnum.valueOf(organ));
        } catch (Exception e) {
            res.status(500);
            return "Internal Server Error";
        }

        res.status(200);
        return "Affected organ removed";
    }

    /**
     * Adds an affected organ to a procedure for a stored profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String addOrgan(Request req, Response res) {
        Gson gson = new Gson();
        ProcedureDAO database = DAOFactory.getProcedureDao();
        int id;
        OrganEnum organ;

        try {
            id = Integer.valueOf(req.params("id"));
            organ = OrganEnum.valueOf(gson.toJson(req.body(), OrganEnum.class));
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        try {
            database.addAffectedOrgan(new Procedure(id), organ);
        } catch (Exception e) {
            res.status(500);
            return "Internal Server Error";
        }

        res.status(200);
        return "Affected organ removed";
    }
}
