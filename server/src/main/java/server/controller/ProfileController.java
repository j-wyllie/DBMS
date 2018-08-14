package server.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import org.sonar.api.internal.google.gson.Gson;
import server.model.database.DAOFactory;
import server.model.database.profile.ProfileDAO;
import spark.Request;
import spark.Response;

public class ProfileController {

    /**
     * Gets all profiles stored.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body, a list of all profiles.
     */
    public static String getAll(Request req, Response res) {
        Gson gson = new Gson();
        ProfileDAO database = DAOFactory.getProfileDao();
        String profiles;

        try {
            if (req.queryMap().hasKey("receiving")
                && Boolean.valueOf(req.queryParams("receiving"))) {

                profiles = gson.toJson(database.getAllReceiving());
            } else {
                profiles = gson.toJson(database.getAll());
            }
        } catch (SQLException e) {
            res.status(500);
            return e.getMessage();
        }
        res.type("application/json");
        res.status(200);

        return profiles;
    }

    /**
     * Gets a single profile from storage.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String get(Request req, Response res) {
        ProfileDAO database = DAOFactory.getProfileDao();
        Profile profile = null;

        try {
            if (req.queryMap().hasKey("id")) {
                profile = database.get(Integer.valueOf(req.queryParams("id")));
            }
            else {
                profile = database.get(req.queryParams("username"));
            }
        } catch (SQLException e) {
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(profile);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }

    /**
     * Creates and stores a new profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String create(Request req, Response res) {
        Gson gson = new Gson();
        ProfileDAO database = DAOFactory.getProfileDao();
        Profile newProfile = null;

        try {
            newProfile = gson.fromJson(req.body(), Profile.class);
        } catch (Exception e) {
            System.out.println("YEEET");
            res.status(400);
            return "Bad Request";
        }

        if ((newProfile != null)) {
            try {
                System.out.println(newProfile.getNhi());
                if (database.isUniqueNHI(newProfile.getNhi()) == 0
                        && !database.isUniqueUsername(newProfile.getUsername())) {
                    database.add(newProfile);
                }
                else {
                    System.out.println("YOTE");
                    res.status(400);
                    return "Bad Request";
                }
            } catch (SQLException e) {
                res.status(500);
                return "Internal Server Error";
            }
        }

        res.status(201);
        return "Profile Created";
    }

    /**
     * Edits a stored profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String edit(Request req, Response res) {
        Gson gson = new Gson();
        ProfileDAO database = DAOFactory.getProfileDao();
        Profile profile = null;

        try {
            profile = gson.fromJson(req.body(), Profile.class);
            profile.setId(Integer.valueOf(req.params("id")));
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        if (!(profile == null)) {
            try {
                database.update(profile);
            } catch (SQLException e) {
                res.status(500);
                return "Internal Server Error";
            }
        }

        res.status(200);
        return "Profile Updated";
    }

    /**
     * Deletes a profile from storage.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String delete(Request req, Response res) {
        Gson gson = new Gson();
        ProfileDAO database = DAOFactory.getProfileDao();
        Profile profile = null;

        try {
            profile = gson.fromJson(req.body(), Profile.class);
            profile.setId(Integer.valueOf(req.params("id")));
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        if (!(profile == null)) {
            try {
                database.remove(profile);
            } catch (SQLException e) {
                res.status(500);
                return "Internal Server Error";
            }
        }

        res.status(200);
        return "Profile Deleted";
    }

    /**
     * Gets a count of all stored profiles.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String count(Request req, Response res) {
        ProfileDAO database = DAOFactory.getProfileDao();
        int count = 0;

        try {
            count = database.size();
        } catch (SQLException e) {
            res.status(500);
            return "Internal Server Error";
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(count);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }
}
