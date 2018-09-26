package server.controller;

import com.google.gson.JsonParser;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.Condition;
import org.sonar.api.internal.google.gson.Gson;
import server.model.database.DAOFactory;
import server.model.database.condition.ConditionDAO;
import server.model.enums.DataTypeEnum;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

@Slf4j
public class ConditionController {
    private static final String RES_CONDITION_UPDATED = "Condition Updated";

    /**
     * Prevent instantiation of static class.
     */
    private ConditionController() {
        throw new UnsupportedOperationException();
    }

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
            profileId = Integer.valueOf(req.params(KeyEnum.ID.toString()));
        } catch (Exception e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            conditions = database.getAll(profileId);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(conditions);

        res.type(DataTypeEnum.JSON.toString());
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
            profileId = Integer.valueOf(req.params(KeyEnum.ID.toString()));
            newCondition = gson.fromJson(req.body(), Condition.class);
        } catch (Exception e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            database.add(profileId, newCondition);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        res.status(201);
        return "Condition added successfully";
    }

    /**
     * Updates a condition for a stored profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String edit(Request req, Response res) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        ConditionDAO database = DAOFactory.getConditionDao();
        Condition condition;

        try {
            if (req.body() == null || parser.parse(req.body()).getAsJsonObject().size() < 1) {
                throw new IllegalArgumentException("Required fields missing.");
            }
            condition = gson.fromJson(req.body(), Condition.class);
        } catch (IllegalArgumentException e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            database.update(condition);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        res.status(200);
        return RES_CONDITION_UPDATED;
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
            id = Integer.valueOf(req.params(KeyEnum.ID.toString()));
        } catch (Exception e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            database.remove(new Condition(id));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        res.status(200);
        return "Condition Deleted";
    }
}
