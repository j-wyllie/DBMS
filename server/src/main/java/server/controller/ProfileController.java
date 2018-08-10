package server.controller;

import java.sql.SQLException;
import java.util.ArrayList;
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
        return "Unimplemented";
    }

    public static String delete(Request req, Response res) {
        return "Unimplemented";
    }

    public static String count(Request req, Response res) {
        return "Unimplemented";
    }
}
