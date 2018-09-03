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
     *
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
     *
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String get(Request req, Response res) {
        UserDAO database = DAOFactory.getUserDao();
        User user;
        try {

            if (req.queryMap().hasKey("id")) {
                user = database.get(Integer.valueOf(req.queryParams("id")));
            } else {
                user = database.get(req.queryParams("username"));
            }
        } catch (UserNotFoundException e) {
            res.status(400);
            return e.getMessage();
        } catch (SQLException e) {
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(user);

        res.type("application/json");
        res.status(200);
        System.out.println(responseBody);
        return responseBody;
    }

    /**
     * Creates and stores a new user.
     *
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String create(Request req, Response res) {
        Gson gson = new Gson();
        UserDAO database = DAOFactory.getUserDao();
        User newUser;

        try {
            newUser = gson.fromJson(req.body(), User.class);
            if (!(database.isUniqueUsername(newUser.getUsername()))) {
                throw new IllegalArgumentException("Username must be unique.");
            }
        } catch (SQLException e) {
            res.status(400);
            return "Bad Request";
        } catch (IllegalArgumentException e) {
            res.status(403);
            return "Forbidden";
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
        return "user Created";
    }

    /**
     * Edits a stored user.
     *
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String edit(Request req, Response res) {
        Gson gson = new Gson();
        UserDAO database = DAOFactory.getUserDao();
        User user;

        try {
            user = gson.fromJson(req.body(), User.class);
            user.setStaffID(Integer.valueOf(req.params("id")));
        } catch (Exception e) {
            res.status(400);
            return "Bad Request";
        }

        try {
            if (database.isUniqueUsername(user.getUsername())) {
                database.update(user);
            } else {
                res.status(403);
                return "Forbidden";
            }
        } catch (SQLException e) {
            res.status(500);
            return "Internal Server Error";
        }

        res.status(200);
        return "user Updated";
    }

    /**
     * Deletes a user from storage.
     *
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
        return "user Deleted";
    }

}
