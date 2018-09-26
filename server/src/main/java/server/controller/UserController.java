package server.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import odms.commons.model.enums.UserType;
import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import org.sonar.api.internal.google.gson.Gson;
import server.model.database.DAOFactory;
import server.model.database.user.UserDAO;
import server.model.enums.DataTypeEnum;
import server.model.enums.ResponseMsgEnum;
import spark.*;

public class UserController {

    /**
     * Prevent instantiation of static class.
     */
    private UserController() {
        throw new UnsupportedOperationException();
    }

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

        res.type(DataTypeEnum.JSON.toString());
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

        res.type(DataTypeEnum.JSON.toString());
        res.status(200);

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
        UserDAO database = DAOFactory.getUserDao();
        User newUser;

        try {
            newUser = new Gson().fromJson(req.body(), User.class);
            if (!(database.isUniqueUsername(newUser.getUsername()))) {
                throw new IllegalArgumentException("Username must be unique.");
            }
        } catch (SQLException e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        } catch (IllegalArgumentException e) {
            res.status(403);
            return ResponseMsgEnum.FORBIDDEN.toString();
        }

        try {
            database.add(newUser);
        } catch (SQLException e) {
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
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
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            if (database.isUniqueUsername(user.getUsername())) {
                database.update(user);
            } else {
                res.status(403);
                return ResponseMsgEnum.FORBIDDEN.toString();
            }
        } catch (SQLException e) {
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
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
        UserDAO database = DAOFactory.getUserDao();
        User user;

        try {
            // Creating a dummy user object so that the DAO can access the id
            user = new User(UserType.CLINICIAN, "dummy", "dummy");
            user.setStaffID(Integer.valueOf(req.params("id")));
        } catch (Exception e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            database.remove(user);
        } catch (SQLException e) {
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        res.status(200);
        return "user Deleted";
    }

    /**
     * Checks the credentials of a user logging in.
     * @param req request containing the username and password.
     * @param res response from the server.
     * @return String containing successful user validation.
     */
    public static String checkCredentials(Request req, Response res) {
        UserDAO database = DAOFactory.getUserDao();
        Gson gson = new Gson();
        Boolean valid;

        String username = req.queryParams("username");
        String password = req.queryParams("password");
        try {
            valid = database.checkCredentials(username, password);
        } catch (UserNotFoundException e) {
            res.status(404);
            return "User not found";
        } catch (SQLException e) {
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        if (valid) {
            try {
                User user = database.get(username);
                Map<String, Integer> body = Middleware.authenticate(
                        user.getStaffID(), user.getUserType());
                res.type(DataTypeEnum.JSON.toString());
                res.status(200);
                return gson.toJson(body);
            } catch (Exception e) {
                res.status(500);
                return e.getMessage();
            }
        } else {
            res.status(401);
            return "Unauthorized";
        }
    }
}
