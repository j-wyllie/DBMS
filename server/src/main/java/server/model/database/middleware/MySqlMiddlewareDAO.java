package server.model.database.middleware;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import server.model.database.DatabaseConnection;

public class MySqlMiddlewareDAO implements MiddlewareDAO {

    @Override
    public void setProfileToken(int profileId, long token) throws SQLException {
        String query = "update profiles set Token = ? where ProfileId = ?;";
        setToken(query, profileId, token);
    }

    @Override
    public void setUserToken(int userId, long token) throws SQLException {
        String query = "update profiles set Token = ? where ProfileId = ?;";
        setToken(query, userId, token);
    }

    @Override
    public boolean isProfileAuthenticated(int profileId, long token) throws SQLException {
        return false;
    }

    @Override
    public boolean isUserAuthenticated(int userId, long token) throws SQLException {
        return false;
    }

    @Override
    public void deleteProfileToken(int profileId) throws SQLException {
        String query = "update profiles set Token = ? where ProfileId = ?;";
        deleteToken(query, profileId);
    }


    @Override
    public void deleteUserToken(int userId) throws SQLException {
        String query = "update users set Token = ? where ProfileId = ?;";
        deleteToken(query, userId);
    }

    private void setToken(String query, int id, long token) throws SQLException {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {
            stmt.setLong(1,  token);
            stmt.setInt(2,  id);
            stmt.execute();
        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            conn.close();
            stmt.close();
        }
    }

    private void deleteToken(String query, int id) throws SQLException {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {
            stmt.setNull(1, Types.INTEGER);
            stmt.setInt(2,  id);
            stmt.execute();
        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            conn.close();
            stmt.close();
        }
    }
}
