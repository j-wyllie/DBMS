package server.model.database.middleware;

import java.sql.SQLException;

public interface MiddlewareDAO {

    /**
     * Sets a token to a profile for later authentication
     * of requests.
     * @param profileId of the profile.
     * @param token to set to the profile.
     * @throws SQLException error.
     */
    void setProfileToken(int profileId, int token) throws SQLException;

    /**
     * Sets a token to a user for later authentication
     * of requests.
     * @param userId of the user.
     * @param token to set to the user.
     * @throws SQLException error.
     */
    void setUserToken(int userId, int token) throws SQLException;

    /**
     * Checks if the token is correct meaning the profile
     * is authenticated for further requests.
     * @param profileId of the profile.
     * @param token that should be matched.
     * @return true if the token matches, false otherwise.
     * @throws SQLException error.
     */
    boolean isProfileAuthenticated(int profileId, int token) throws SQLException;

    /**
     * Checks if the token is correct meaning the user
     * is authenticated for further requests.
     * @param userId of the user.
     * @param token that should be matched.
     * @return true if the token matches, false otherwise.
     * @throws SQLException error.
     */
    boolean isUserAuthenticated(int userId, int token) throws SQLException;

    /**
     * Resets the profiles token to stop further requests.
     * @param profileId of the profile.
     * @throws SQLException internal error.
     */
    void deleteProfileToken(int profileId) throws SQLException;

    /**
     * Resets the users token to stop further requests.
     * @param userId of the user.
     * @throws SQLException internal error.
     */
    void deleteUserToken(int userId) throws SQLException;
}
