package server.controller;

import java.sql.SQLException;
import java.util.Random;
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
    public static long authenticate(int id, UserType userType) throws SQLException {
        long token = generateToken();
        if (userType == UserType.PROFILE || userType == UserType.DONOR) {
            database.setProfileToken(id, token);
        }
        else if (userType == UserType.ADMIN || userType == UserType.CLINICIAN) {
            database.setUserToken(id, token);
        }
        return token;
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
        }
        else if (userType == UserType.ADMIN || userType == UserType.CLINICIAN) {
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
    private static long generateToken() {
        Random random = new Random();
        return random.nextLong();
    }
}
