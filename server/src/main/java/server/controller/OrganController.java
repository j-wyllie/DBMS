package server.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.ExpiredOrgan;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import org.sonar.api.internal.google.gson.Gson;
import org.sonar.api.internal.google.gson.JsonObject;
import org.sonar.api.internal.google.gson.JsonParser;
import server.model.database.DAOFactory;
import server.model.database.organ.OrganDAO;
import spark.Request;
import spark.Response;

@Slf4j
public class OrganController {

    /**
     * Gets a list of all organs for a profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String getAll(Request req, Response res) {
        Set<OrganEnum> organs;
        int profileId = Integer.valueOf(req.params("id"));

        try {
            organs = getOrgans(new Profile(profileId), req);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(organs);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }

    /**
     * Adds an organ to a profile in persistent storage.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String add(Request req, Response res) {
        int profileId;
        JsonObject body;
        JsonParser parser = new JsonParser();

        try {
            profileId = Integer.valueOf(req.params("id"));
            body = parser.parse(req.body()).getAsJsonObject();
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        try {
            addOrgan(new Profile(profileId), body);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        res.status(201);
        return "Organ added";
    }

    /**
     * Removes an organ from a profile in persistent storage.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String delete(Request req, Response res) {
        int profileId;
        String organ;
        try {
            profileId = Integer.valueOf(req.params("id"));
            organ = String.valueOf(req.queryParams("name"));
        } catch (Exception e) {
            res.status(500);
            return "Bad Request";
        }

        try {
            removeOrgan(new Profile(profileId), organ, req);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        res.status(200);
        return "Organ removed";
    }

    /**
     * Gets the organs from the database bases on organ usage.
     * @param profile to get the organs for.
     * @param req that was received.
     * @return the set of organs based on the criteria.
     */
    private static Set<OrganEnum> getOrgans(Profile profile, Request req) {
        OrganDAO database = DAOFactory.getOrganDao();

        if (req.queryMap().hasKey("donated")) {
            return database.getDonations(profile.getId());
        }
        if (req.queryMap().hasKey("donating")) {
            return database.getDonating(profile.getId());
        }
        if (req.queryMap().hasKey("required")) {
            return database.getRequired(profile);
        }
        if (req.queryMap().hasKey("received")) {
            return database.getReceived(profile.getId());
        }
        return new HashSet<>();
    }

    /**
     * Add an organ to a profile in persistent storage.
     * @param profile to add the organ to.
     * @param body that was received.
     * @throws OrganConflictException error.
     */
    private static void addOrgan(Profile profile, JsonObject body) throws OrganConflictException {
        OrganEnum organEnum = OrganEnum.valueOf(body.get("name").getAsString());
        OrganDAO database = DAOFactory.getOrganDao();

        if (body.keySet().contains("donated")) {
            database.addDonation(profile, organEnum);
        }
        if (body.keySet().contains("donating")) {
            database.addDonating(profile, organEnum);
        }
        if (body.keySet().contains("required")) {
            database.addRequired(profile, organEnum);
        }
        if (body.keySet().contains("received")) {
            database.addReceived(profile, organEnum);
        }
    }

    /**
     * Removes an organ from a profile in persistent storage.
     * @param profile to add the organ to.
     * @param organ to add.
     * @param req that was received.
     */
    private static void removeOrgan(Profile profile, String organ, Request req) {
        OrganEnum organEnum = OrganEnum.valueOf(organ);
        OrganDAO database = DAOFactory.getOrganDao();

        if (req.queryMap().hasKey("donated")) {
            database.removeDonation(profile, organEnum);
        }
        if (req.queryMap().hasKey("donating")) {
            database.removeDonating(profile, organEnum);
        }
        if (req.queryMap().hasKey("required")) {
            database.removeRequired(profile, organEnum);
        }
        if (req.queryMap().hasKey("received")) {
            database.removeReceived(profile, organEnum);
        }
    }

    public static String getExpired(Request req, Response res) {
        OrganDAO database = DAOFactory.getOrganDao();
        int profileId = Integer.valueOf(req.params("id"));
        List<ExpiredOrgan> organs;

        try {
            organs = database.getExpired(new Profile(profileId));
        } catch (SQLException e) {
            res.status(500);
            return "Internal Server Error";
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(organs);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }


    public static String setExpired(Request req, Response res) {
        OrganDAO database = DAOFactory.getOrganDao();
        int profileId = Integer.valueOf(req.params("id"));
        String organ = req.queryParams("organ").toLowerCase().replace("_", " ");

        try {
            if (Integer.valueOf(req.queryParams("expired")) == 1) {
                String note = req.queryParams("note");
                int userId = Integer.valueOf(req.queryParams("userId"));
                database.setExpired(new Profile(profileId), organ, 1, note, userId);
            }
            else {
                database.revertExpired(profileId, organ);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return "Internal Server Error";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(400);
            return "Bad Request";
        }

        res.status(200);
        return "Expiry set";
    }
}
