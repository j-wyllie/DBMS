package server.controller;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.UserNotFoundException;
import org.sonar.api.internal.google.gson.Gson;
import server.model.database.DAOFactory;
import server.model.database.profile.ProfileDAO;
import server.model.enums.DataTypeEnum;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

/**
 * The profile server controller.
 */
@Slf4j
public class ProfileController {
    private static final String KEY_SEARCH = "searchString";

    /**
     * Prevent instantiation of static class.
     */
    private ProfileController() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets all profiles stored.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body, a list of all profiles.
     */
    public static String getAll(Request req, Response res) {
        String profiles;
        try {
            profiles = getAll(req);
        } catch (SQLException e) {
            res.status(500);
            return e.getMessage();
        }
        res.type(DataTypeEnum.JSON.toString());
        res.status(200);

        return profiles;
    }

    /**
     * Gets all receiving profiles (possibly with search criteria).
     * @param req received.
     * @return json string of profiles.
     */
    public static String getReceiving(Request req,  Response res) {
        ProfileDAO database = DAOFactory.getProfileDao();
        Gson gson = new Gson();
        String profiles;
        try {
            if (req.queryMap().hasKey(KEY_SEARCH)) {
                String searchString = req.queryParams(KEY_SEARCH);
                List<Entry<Profile, OrganEnum>> result = database.searchReceiving(searchString);
                profiles = gson.toJson(result);
            } else if (req.queryMap().hasKey("organ")) {
                String organ = req.queryParams("organ");
                String bloodType = req.queryParams("bloodType");
                Integer lowerAgeRange = Integer.valueOf(req.queryParams("lowerAgeRange"));
                Integer upperAgeRange = Integer.valueOf(req.queryParams("upperAgeRange"));
                List<Profile> result = database.getOrganReceivers(organ, bloodType,
                        lowerAgeRange, upperAgeRange);
                profiles = gson.toJson(result);
            } else {
                profiles = gson.toJson(database.getAllReceiving());
            }
        } catch (NumberFormatException e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        } catch (Exception e) {
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }
        res.type(DataTypeEnum.JSON.toString());
        res.status(200);

        return profiles;
    }

    /**
     * Gets all profiles stored.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body, a list of all profiles.
     */
    public static String getDead(Request req, Response res) {
        ProfileDAO database = DAOFactory.getProfileDao();
        Gson gson = new Gson();
        String profiles;
        try {
            profiles = gson.toJson(database.getDead());
        } catch (SQLException e) {
            res.status(500);
            return e.getMessage();
        }
        res.type(DataTypeEnum.JSON.toString());
        res.status(200);

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

        if (req.queryMap().hasKey(KEY_SEARCH)) {
            String searchString = req.queryParams(KEY_SEARCH);
            int ageSearchInt = Integer.parseInt(req.queryParams("ageSearchInt"));
            int ageRangeSearchInt = Integer.parseInt(req.queryParams("ageRangeSearchInt"));
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
        } else {
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
        Profile profile;

        try {
            if (req.queryMap().hasKey(KeyEnum.ID.toString())) {
                profile = database.get(Integer.valueOf(req.queryParams(KeyEnum.ID.toString())));
            } else {
                profile = database.get(req.queryParams("username"));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(profile);

        res.type(DataTypeEnum.JSON.toString());
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
        Profile newProfile;

        try {
            newProfile = gson.fromJson(req.body(), Profile.class);
        } catch (Exception e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        if (newProfile != null) {
            try {
                if (database.isUniqueNHI(newProfile.getNhi()) == 0
                        && !database.isUniqueUsername(newProfile.getUsername())) {
                    database.add(newProfile);
                } else {
                    res.status(400);
                    return ResponseMsgEnum.BAD_REQUEST.toString();
                }
            } catch (SQLException e) {
                res.status(500);
                return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
            }
        }

        res.status(201);
        return "profile Created";
    }

    /**
     * Edits a stored profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String edit(Request req, Response res) {
        ProfileDAO database = DAOFactory.getProfileDao();
        Profile profile;

        try {
            profile = new Gson().fromJson(req.body(), Profile.class);
            profile.setId(Integer.valueOf(req.params(KeyEnum.ID.toString())));
        } catch (Exception e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            database.update(profile);
        } catch (SQLException e) {
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        res.status(200);
        return "profile Updated";
    }

    /**
     * Deletes a profile from storage.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String delete(Request req, Response res) {
        ProfileDAO database = DAOFactory.getProfileDao();
        Profile profile;

        try {
            profile = new Gson().fromJson(req.body(), Profile.class);
            profile.setId(Integer.valueOf(req.params(KeyEnum.ID.toString())));
        } catch (Exception e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            database.remove(profile);
        } catch (SQLException e) {
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        res.status(200);
        return "profile Deleted";
    }

    /**
     * Gets a count of all stored profiles.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public static String count(Request req, Response res) {
        ProfileDAO database = DAOFactory.getProfileDao();
        int count;

        try {
            count = database.size();
        } catch (SQLException e) {
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(count);

        res.type(DataTypeEnum.JSON.toString());
        res.status(200);

        return responseBody;
    }

    /**
     * Checks that a profile has a password.
     * @param req the request fields.
     * @param res the response from the server.
     * @return The response body.
     */
    public static String hasPassword(Request req, Response res) {
        ProfileDAO database = DAOFactory.getProfileDao();
        Boolean hasPassword = false;
        try {
            if (req.queryMap().hasKey("nhi")) {
                hasPassword = database.hasPassword(req.queryParams("nhi"));
            }
        } catch (SQLException e) {
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        res.status(200);
        System.out.println(hasPassword.toString());
        return hasPassword.toString();
    }

    /**
     * Checks the credentials of a profile logging in,
     * @param request request containg password and username.
     * @param response response from the server.
     * @return String displaying success of validation.
     */
    public static String checkCredentials(Request request, Response response) {
        ProfileDAO database = DAOFactory.getProfileDao();
        Gson gson = new Gson();
        Boolean valid;

        String username = request.queryParams("username");
        String password = request.queryParams("password");
        try {
            valid = database.checkCredentials(username, password);
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        } catch (UserNotFoundException e) {
            response.status(404);
            return "Profile not found.";
        }

        if (valid) {
            try {
                Profile profile = database.get(username);
                Map<String, Integer> body = Middleware.authenticate(profile.getId(), UserType.PROFILE);
                response.type(DataTypeEnum.JSON.toString());
                response.status(200);
                return gson.toJson(body);
            } catch (SQLException e) {
                response.status(500);
                return e.getMessage();
            }
        } else {
            response.status(404);
            return "Error.";
        }
    }

    /**
     * Saves the profiles password.
     * @param request request being sent with url and password.
     * @param response the server response.
     * @return String confirming success.
     */
    public static String savePassword(Request request, Response response) {
        ProfileDAO profileDAO = DAOFactory.getProfileDao();
        Boolean valid;
        try {
            valid = profileDAO.savePassword(request.queryParams("username"),
                    request.queryParams("password"));
        } catch (SQLException | UserNotFoundException e) {
            response.status(500);
            return e.getMessage();
        }
        if (valid) {
            response.status(200);
            return "Password Set";
        } else {
            response.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }
    }
}
