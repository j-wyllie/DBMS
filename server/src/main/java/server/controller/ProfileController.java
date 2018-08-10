package server.controller;

import java.sql.SQLException;
import org.sonar.api.internal.google.gson.Gson;
import server.model.database.DAOFactory;
import server.model.database.profile.ProfileDAO;
import spark.Request;
import spark.Response;

public class ProfileController {

    public static String getAll(Request req, Response res) {
        ProfileDAO database = DAOFactory.getProfileDao();
        try {
            List<Profile> profiles = database.getAll();
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
        return "Unimplemented";
    }

    public static String create(Request req, Response res) {
        return "Unimplemented";
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
