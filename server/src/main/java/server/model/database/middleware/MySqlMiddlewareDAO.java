package server.model.database.middleware;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import server.model.database.DatabaseConnection;

public class MySqlMiddlewareDAO implements MiddlewareDAO {

    /**
     * Sets a token to a profile for later authentication
     * of requests.
     * @param profileId of the profile.
     * @param token to set to the profile.
     * @throws SQLException error.
     */
    @Override
    public void setProfileToken(int profileId, int token) throws SQLException {
        String query = "update profiles set Token = ? where ProfileId = ?;";
        setToken(query, profileId, token);
    }

    /**
     * Sets a token to a user for later authentication
     * of requests.
     * @param userId of the user.
     * @param token to set to the user.
     * @throws SQLException error.
     */
    @Override
    public void setUserToken(int userId, int token) throws SQLException {
        String query = "update users set Token = ? where UserId = ?;";
        setToken(query, userId, token);
    }

    /**
     * Checks if the token is correct meaning the profile
     * is authenticated for further requests.
     * @param profileId of the profile.
     * @param token that should be matched.
     * @return true if the token matches, false otherwise.
     * @throws SQLException error.
     */
    @Override
    public boolean isProfileAuthenticated(int profileId, int token) throws SQLException {
        String query = "select * from profiles where ProfileId = ? and Token = ?;";
        return isAuthenticated(query, profileId, token);
    }

    /**
     * Checks if the token is correct meaning the user
     * is authenticated for further requests.
     * @param userId of the user.
     * @param token that should be matched.
     * @return true if the token matches, false otherwise.
     * @throws SQLException error.
     */
    @Override
    public boolean isUserAuthenticated(int userId, int token) throws SQLException {
        String query = "select * from users where UserId = ? and Token = ?;";
        return isAuthenticated(query, userId, token);
    }

    /**
     * Resets the profiles token to stop further requests.
     * @param profileId of the profile.
     * @throws SQLException internal error.
     */
    @Override
    public void deleteProfileToken(int profileId) throws SQLException {
        String query = "update profiles set Token = ? where ProfileId = ?;";
        deleteToken(query, profileId);
    }

    /**
     * Resets the users token to stop further requests.
     * @param userId of the user.
     * @throws SQLException internal error.
     */
    @Override
    public void deleteUserToken(int userId) throws SQLException {
        String query = "update users set Token = ? where UserId = ?;";
        deleteToken(query, userId);
    }

    /**
     * Executes a query checking for a valid combination
     * for authentication.
     * @param query executed to the database.
     * @param id of the user/profile.
     * @param token to be matched.
     * @return true if it is a valid combination, false otherwise.
     * @throws SQLException internal error.
     */
    private boolean isAuthenticated(String query, int id, int token) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1,  id);
            stmt.setInt(2,  token);

            try (ResultSet set = stmt.executeQuery()) {
                return set.next();
            }

        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    /**
     * Executes a query setting a new token to a user/profile.
     * @param query to be executed.
     * @param id of the user/profile.
     * @param token to be set to.
     * @throws SQLException internal error.
     */
    private void setToken(String query, int id, int token) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1,  token);
            stmt.setInt(2,  id);
            stmt.execute();
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    /**
     * Executes a query to reset a token set to a user/profile.
     * @param query to be executed.
     * @param id of the user/profile.
     * @throws SQLException internal error.
     */
    private void deleteToken(String query, int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setNull(1, Types.INTEGER);
            stmt.setInt(2,  id);
            stmt.execute();
        } catch (SQLException e) {
            throw new SQLException();
        }
    }
}
