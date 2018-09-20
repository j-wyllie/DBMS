package server.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.Condition;
import org.sonar.api.internal.google.gson.Gson;
import server.model.database.DAOFactory;
import server.model.database.condition.ConditionDAO;
import spark.Request;
import spark.Response;

@Slf4j
public class ConditionController {

    /**
     * Gets a list of all conditions fo a profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String getAll(Request req, Response res) {
        ConditionDAO database = DAOFactory.getConditionDao();
        List<Condition> conditions;
        int profileId;

        try {
            profileId = Integer.valueOf(req.params("id"));
        } catch (Exception e) {
            res.status(500);
            return "Bad Request";
        }

        try {
            conditions = database.getAll(profileId, true);
            conditions.addAll(database.getAll(profileId, false));
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(conditions);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }

    /**
     * Adds a condition to a stored profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String add(Request req, Response res) {
        Gson gson = new Gson();
        ConditionDAO database = DAOFactory.getConditionDao();
        Condition newCondition;
        int profileId;

        try {
            profileId = Integer.valueOf(req.params("id"));
            newCondition = gson.fromJson(req.body(), Condition.class);
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        try {
            database.add(profileId, newCondition);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return "Internal Server Error";
        }

        res.status(201);
        return "Condition Created";
    }

    /**
     * Updates a condition for a stored profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String edit(Request req, Response res) {
        Gson gson = new Gson();
        ConditionDAO database = DAOFactory.getConditionDao();
        Condition condition;

        try {
            condition = gson.fromJson(req.body(), Condition.class);
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        try {
            database.update(condition);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return "Internal Server Error";
        }

        res.status(200);
        return "Condition Updated";
    }

    /**
     * Deletes a condition from a stored profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String delete(Request req, Response res) {
        ConditionDAO database = DAOFactory.getConditionDao();
        int id;

        try {
            id = Integer.valueOf(req.params("id"));
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        try {
            database.remove(new Condition(id));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return "Internal Server Error";
        }

        res.status(200);
        return "Condition Deleted";
    }
}
