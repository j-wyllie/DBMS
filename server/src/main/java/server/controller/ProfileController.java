package server.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import odms.commons.model.profile.Profile;
import org.sonar.api.internal.google.gson.Gson;
import server.model.database.DAOFactory;
import server.model.database.profile.ProfileDAO;
import spark.Request;
import spark.Response;

public class ProfileController {

    public static String getAll(Request req, Response res) {
        ProfileDAO database = DAOFactory.getProfileDao();
        List<Profile> profiles = new ArrayList<>();

        try {
            profiles = database.getAll();
        } catch (SQLException e) {
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(profiles);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }

    public static String get(Request req, Response res) {
        ProfileDAO database = DAOFactory.getProfileDao();
        Profile profile = null;

        try {
            profile = database.get(Integer.valueOf(req.params("id")));
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

        if (!(newProfile == null)) {
            try {
                database.add(newProfile);
            } catch (SQLException e) {
                res.status(500);
                return "Internal Server Error";
            }
        }

        res.status(201);
        return "Profile Created";
    }

    public static String edit(Request req, Response res) {
        Gson gson = new Gson();
        ProfileDAO database = DAOFactory.getProfileDao();
        Profile profile = null;

        try {
            profile = gson.fromJson(req.body(), Profile.class);
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

    public static String delete(Request req, Response res) {
        Gson gson = new Gson();
        ProfileDAO database = DAOFactory.getProfileDao();
        Profile profile = null;

        try {
            profile = gson.fromJson(req.body(), Profile.class);
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
