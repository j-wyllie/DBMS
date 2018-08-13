package server.controller;

import java.sql.SQLException;
import java.util.List;
import odms.commons.model.enums.UserType;
import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import org.sonar.api.internal.google.gson.Gson;
import server.model.database.DAOFactory;
import server.model.database.user.UserDAO;
import spark.*;

public class UserController {

    /**
     * Gets all users stored.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body, a list of all profiles.
     */
    public static String getAll(Request req, Response res) {
        UserDAO database = DAOFactory.getUserDao();
        List<User> users;

        try {
            users = database.getAll();
        } catch (SQLException e) {
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(users);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }

    /**
     * Gets a single user from storage.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String get(Request req, Response res) {
        UserDAO database = DAOFactory.getUserDao();
        User user = null;

        try {
            user = database.get(Integer.valueOf(req.params("id")));
        } catch (SQLException | UserNotFoundException e) {
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(user);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }

    /**
     * Creates and stores a new user.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String create(Request req, Response res) {
        Gson gson = new Gson();
        UserDAO database = DAOFactory.getUserDao();
        User newUser = null;

        try {
            newUser = gson.fromJson(req.body(), User.class);
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        if (!(newUser == null)) {
            try {
                database.add(newUser);
            } catch (SQLException e) {
                res.status(500);
                return "Internal Server Error";
            }
        }

        res.status(201);
        return "User Created";
    }

    /**
     * Edits a stored user.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String edit(Request req, Response res) {
        Gson gson = new Gson();
        UserDAO database = DAOFactory.getUserDao();
        User user = null;

        try {
            user = gson.fromJson(req.body(), User.class);
            user.setStaffID(Integer.valueOf(req.params("id")));
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        if (!(user == null)) {
            try {
                database.update(user);
            } catch (SQLException e) {
                res.status(500);
                return "Internal Server Error";
            }
        }

        res.status(200);
        return "User Updated";
    }

    /**
     * Deletes a user from storage.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String delete(Request req, Response res) {
        Gson gson = new Gson();
        UserDAO database = DAOFactory.getUserDao();
        User user = null;

        try {
            // Creating a dummy user object so that the DAO can access the id
            user = new User(UserType.CLINICIAN, "dummy", "dummy");
            user.setStaffID(Integer.valueOf(req.params("id")));
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        if (!(user == null)) {
            try {
                database.remove(user);
            } catch (SQLException e) {
                res.status(500);
                return "Internal Server Error";
            }
        }

        res.status(200);
        return "User Deleted";
    }

}
