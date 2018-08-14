package server.controller;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import org.sonar.api.internal.google.gson.Gson;
import org.sonar.api.internal.google.gson.JsonArray;
import org.sonar.api.internal.google.gson.JsonElement;
import org.sonar.api.internal.google.gson.JsonObject;
import org.sonar.api.internal.google.gson.JsonParser;
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
        String profiles;
        try {
            if (req.queryMap().hasKey("receiving")
                && Boolean.valueOf(req.queryParams("receiving"))) {
                profiles = getReceiving(req);
            } else {
                profiles = getAll(req);
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
     * Gets all receiving profiles (possibly with search criteria).
     * @param req received.
     * @return json string of profiles.
     */
    private static String getReceiving(Request req) {
        ProfileDAO database = DAOFactory.getProfileDao();
        Gson gson = new Gson();
        String profiles;

        if (req.queryMap().hasKey("searchString")) {
            String searchString = req.queryParams("searchString");
            List<Entry<Profile, OrganEnum>> result = database.searchReceiving(searchString);
            profiles = gson.toJson(result);
        }
        else {
            profiles = gson.toJson(database.getAllReceiving());
        }
        return profiles;
    }

    /**
     /**
     * Gets all profiles (possibly with search criteria).
     * @param req received.
     * @return json string of profiles.
     * @throws SQLException error.
     */
    private static String getAll(Request req) throws SQLException {
        ProfileDAO database = DAOFactory.getProfileDao();
        Gson gson = new Gson();
        String profiles;

        if (req.queryMap().hasKey("searchString")) {
            String searchString = req.queryParams("searchString");
            int ageSearchInt = Integer.valueOf(req.queryParams("ageSearchInt"));
            int ageRangeSearchInt = Integer.valueOf(req.queryParams("ageRangeSearchInt"));
            String region = req.queryParams("region");
            String gender = req.queryParams("gender");
            String type = req.queryParams("type");

            Set<OrganEnum> organs = new HashSet<>();
            List<String> organArray = gson.fromJson(req.queryParams("organs"), List.class);
            for (String organ : organArray) {
                organs.add(OrganEnum.valueOf(organ));
            }
            List<Profile> result = database.search(searchString, ageSearchInt,
                    ageRangeSearchInt, region, gender, type, organs);
            profiles = gson.toJson(result);
        }
        else {
            profiles = gson.toJson(database.getAll());
        }
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
