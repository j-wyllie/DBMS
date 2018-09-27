package server.controller;

import static spark.Spark.halt;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import server.model.enums.KeyEnum;
import spark.Request;
import odms.commons.model.enums.UserType;
import org.sonar.api.server.authentication.UnauthorizedException;
import server.model.database.DAOFactory;
import server.model.database.middleware.MiddlewareDAO;
import spark.Response;

public class Middleware {

    // Connection to database methods.
    private static MiddlewareDAO database = DAOFactory.getMiddlewareDAO();

    /**
     * Constructor - static classes can not be init.
     */
    public Middleware() {
        throw new UnsupportedOperationException();
    }

    /**
     * Authenticates a user by setting and returning a new token
     * to the user.
     * @param id of the user.
     * @param userType type of user.
     * @return a generated token for future requests.
     * @throws SQLException error.
     */
    public static Map<String, Integer> authenticate(int id, UserType userType) throws SQLException {
        int token = generateToken();
        if (userType == UserType.PROFILE || userType == UserType.DONOR) {
            database.setProfileToken(id, token);
        } else if (userType == UserType.ADMIN || userType == UserType.CLINICIAN) {
            database.setUserToken(id, token);
        }
        Map<String, Integer> response = new HashMap<>();
        response.put("id", id);
        response.put("Token", token);
        return response;
    }

    /**
     * Checks if a request has the correct general authentication.
     * @param req the request.
     * @return true if the request is authenticated, false otherwise.
     * @throws SQLException internal error.
     */
    public static boolean isAuthenticated(Request req, Response res) throws SQLException {
        System.out.println("auth");
        UserType userType;
        int id;
        int token;
        try {
            userType = UserType.valueOf(req.headers(KeyEnum.USERTYPE.toString()));
            id = Integer.valueOf(req.headers(KeyEnum.ID.toString()));
            token = Integer.valueOf(req.headers(KeyEnum.AUTH.toString()));
        } catch (Exception e) {
            halt(401, "Unauthorized");
            return false;
        }

        if (userType == UserType.PROFILE || userType == UserType.DONOR) {
            if (database.isProfileAuthenticated(id, token)) {
                return true;
            }
        } else if (userType == UserType.ADMIN || userType == UserType.CLINICIAN) {
            if (database.isUserAuthenticated(id, token)) {
                return true;
            }
        } else {
            halt(401, "Unauthorized");
            return false;
        }
        halt(401, "Unauthorized");
        return false;
    }

    /**
     * Checks if a request has the correct authentication appropriate for
     * an admin or clinician.
     * @param request the request.
     * @return true if the request is authenticated, false otherwise.
     * @throws SQLException internal error.
     */
    public static boolean isAdminAuthenticated(Request request, Response response) throws SQLException {
        System.out.println("auth2");
        int id;
        int token;
        try {
            id = Integer.valueOf(request.headers(KeyEnum.ID.toString()));
            token = Integer.valueOf(request.headers(KeyEnum.AUTH.toString()));
        } catch (NumberFormatException e) {
            halt(401, "Unauthorized");
            return false;
        }
        if (database.isUserAuthenticated(id, token)) {
            return true;
        }
        halt(401, "Unauthorized");
        return false;
    }

    /**
     * Resets the token of an authenticated user.
     * @param id of the user.
     * @param userType type of user.
     * @param token to check authentication.
     * @throws SQLException internal error.
     * @throws UnauthorizedException unauthorized error.
     */
    public static void logout(int id, UserType userType, int token)
            throws SQLException, UnauthorizedException {
        if (userType == UserType.PROFILE || userType == UserType.DONOR) {
            if (database.isProfileAuthenticated(id, token)) {
                database.deleteProfileToken(id);
            } else {
                throw new UnauthorizedException("User unauthorized.");
            }
        } else if (userType == UserType.ADMIN || userType == UserType.CLINICIAN) {
            if (database.isUserAuthenticated(id, token)) {
                database.deleteUserToken(id);
            } else {
                throw new UnauthorizedException("User unauthorized.");
            }
        }
    }

    /**
     * Generates a random number to be used as
     * a token.
     * @return the generated token.
     */
    private static int generateToken() {
        Random random = new Random();
        return random.nextInt();
    }
}
