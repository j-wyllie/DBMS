package server.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Procedure;
import org.sonar.api.internal.google.gson.Gson;
import server.model.database.DAOFactory;
import server.model.database.procedure.ProcedureDAO;
import server.model.enums.DataTypeEnum;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

@Slf4j
public class ProcedureController {

    /**
     * Prevent instantiation of static class.
     */
    private ProcedureController() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a list of all procedures.
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
            profileId = Integer.valueOf(req.params(KeyEnum.ID.toString()));
            pending = Boolean.valueOf(req.queryParams(KeyEnum.PENDING.toString()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            procedures = database.getAll(profileId, pending);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(procedures);

        res.type(DataTypeEnum.JSON.toString());
        res.status(200);

        return responseBody;
    }

    /**
     * Adds a procedure to a profile in storage.
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
            profileId = Integer.valueOf(req.params(KeyEnum.ID.toString()));
            newProcedure = gson.fromJson(req.body(), Procedure.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            database.add(profileId, newProcedure);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        res.status(201);
        return "Procedure Created";
    }

    /**
     * Edits a procedure stored for a profile.
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
            pending = Boolean.valueOf(req.queryParams(KeyEnum.PENDING.toString()));
            if (newProcedure == null) {
                throw new IllegalArgumentException("Missing required fields.");
            }
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            database.update(newProcedure, pending);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        res.status(201);
        return "Procedure Updated";
    }

    /**
     * Removes a procedure from a stored profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String delete(Request req, Response res) {
        ProcedureDAO database = DAOFactory.getProcedureDao();
        Procedure newProcedure;

        try {
            newProcedure = new Procedure(Integer.parseInt(
                    req.params(KeyEnum.ID.toString()))
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            database.remove(newProcedure);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        res.status(200);
        return "Procedure Deleted";
    }

    /**
     * Gets all organs affected by a procedure for a stored profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String getOrgans(Request req, Response res) {
        ProcedureDAO database = DAOFactory.getProcedureDao();
        List<OrganEnum> organs;
        int id;

        try {
            id = Integer.valueOf(req.params(KeyEnum.ID.toString()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            organs = database.getAffectedOrgans(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(organs);

        res.type(DataTypeEnum.JSON.toString());
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
            id = Integer.valueOf(req.params(KeyEnum.ID.toString()));
            organ = req.queryParams(KeyEnum.NAME.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            database.removeAffectedOrgan(new Procedure(id), OrganEnum.valueOf(organ));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
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
            id = Integer.valueOf(req.params(KeyEnum.ID.toString()));
            organ = gson.fromJson(req.body(), OrganEnum.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            database.addAffectedOrgan(new Procedure(id), organ);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        res.status(200);
        return "Affected organ added";
    }
}
