package server.controller;

import java.sql.SQLException;
import java.util.Random;
import server.model.enums.KeyEnum;
import spark.Request;
import odms.commons.model.enums.UserType;
import org.sonar.api.server.authentication.UnauthorizedException;
import server.model.database.DAOFactory;
import server.model.database.middleware.MiddlewareDAO;

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
    public static int authenticate(int id, UserType userType) throws SQLException {
        int token = generateToken();
        if (userType == UserType.PROFILE || userType == UserType.DONOR) {
            database.setProfileToken(id, token);
        } else if (userType == UserType.ADMIN || userType == UserType.CLINICIAN) {
            database.setUserToken(id, token);
        }
        return token;
    }

    /**
     * Checks if a request has the correct general authentication.
     * @param req the request.
     * @return true if the request is authenticated, false otherwise.
     * @throws SQLException internal error.
     */
    public static boolean isAuthenticated(Request req) throws SQLException {
        UserType userType = UserType.valueOf(req.headers("userType"));
        int id = Integer.parseInt(req.headers(KeyEnum.ID.toString()));
        int token = Integer.parseInt(req.headers("token"));

        if (userType == UserType.PROFILE || userType == UserType.DONOR) {
            return database.isProfileAuthenticated(id, token);
        } else if (userType == UserType.ADMIN || userType == UserType.CLINICIAN) {
            return database.isUserAuthenticated(id, token);
        } else {
            return false;
        }
    }

    /**
     * Checks if a request has the correct authentication appropriate for
     * an admin or clinician.
     * @param req the request.
     * @return true if the request is authenticated, false otherwise.
     * @throws SQLException internal error.
     */
    public static boolean isAdminAuthenticated(Request req) throws SQLException {
        int id = Integer.parseInt(req.headers(KeyEnum.ID.toString()));
        int token = Integer.parseInt(req.headers("token"));

        return database.isUserAuthenticated(id, token);
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
