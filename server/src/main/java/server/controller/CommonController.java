package server.controller;

import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.UserType;
import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import org.sonar.api.server.authentication.UnauthorizedException;
import server.model.database.DAOFactory;
import server.model.database.DatabaseConnection;
import server.model.database.middleware.MiddlewareDAO;
import server.model.database.profile.ProfileDAO;
import server.model.database.user.UserDAO;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;

import java.sql.SQLException;

@Slf4j
public class CommonController {

    private static final String ADMIN = "admin";
    private static final String CLINICIAN = "0";

    /**
     * Checks if default admin and clinician profiles exist in the database,
     * creates them if they don't.
     * @param req sent to the server.
     * @param res sent from the server to the client.
     * @return the response body.
     */
    public static String setup(Request req, Response res) {
        try {
            try {
                DAOFactory.getUserDao().get(ADMIN);
            } catch (UserNotFoundException e) {
                createDefaultAdmin();
            }
            try {
                DAOFactory.getUserDao().get(CLINICIAN);
            } catch (UserNotFoundException e) {
                createDefaultClinician();
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        }

        res.status(201);
        log.info("Default users created on startup.");
        return "Default users created.";
    }

    /**
     * Creates a default admin profile in the database.
     */
    private static void createDefaultAdmin() throws SQLException {
        User admin = new User(UserType.ADMIN, ADMIN);
        admin.setUsername(ADMIN);
        admin.setPassword(ADMIN);
        admin.setDefault(true);
        DAOFactory.getUserDao().add(admin);
    }

    /**
     * Creates a default clinician profile in the database.
     */
    private static void createDefaultClinician() throws SQLException {
        User clinician = new User(UserType.CLINICIAN, "Doc");
        clinician.setUsername(CLINICIAN);
        clinician.setPassword("password");
        clinician.setDefault(true);
        DAOFactory.getUserDao().add(clinician);

    }

    /**
     * Checks the credentials for a particular user type attempting to
     * log on to the application.
     * @param request from the client.
     * @param response to be returned to the client.
     * @return the response from the server.
     */
    public static String checkCredentials(Request request, Response response) {
        if (request.queryParams(KeyEnum.USERTYPE.toString()).equals(UserType.ADMIN.toString())) {
            return UserController.checkCredentials(request, response);
        } else if (request.queryParams(KeyEnum.USERTYPE.toString()).equals(UserType.PROFILE.toString())) {
            return ProfileController.checkCredentials(request, response);
        }
        response.status(500);
        return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
    }

    /**
     * Controller to logout a user.
     * @param request to logout from the client.
     * @param response to the client.
     * @return from the server.
     */
    public static String logout(Request request, Response response) {
        int id;
        UserType userType;
        int token;
        try {
            id = Integer.valueOf(request.headers(KeyEnum.ID.toString()));
            userType = UserType.valueOf(request.headers(KeyEnum.USERTYPE.toString()));
            token = Integer.valueOf(request.headers(KeyEnum.AUTH.toString()));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            response.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }

        try {
            Middleware.logout(id, userType, token);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            response.status(500);
            return ResponseMsgEnum.INTERNAL_SERVER_ERROR.toString();
        } catch (UnauthorizedException u) {
            log.error(u.getMessage(), u);
            response.status(401);
            return u.getMessage();
        }

        response.status(200);
        return "User successfully logged out";
    }
}
