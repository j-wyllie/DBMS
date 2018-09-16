package server.model.database.middleware;

import java.sql.SQLException;

public interface MiddlewareDAO {

    void setProfileToken(int profileId, long token) throws SQLException;

    void setUserToken(int userId, long token) throws SQLException;

    boolean isProfileAuthenticated(int profileId, long token) throws SQLException;

    boolean isUserAuthenticated(int userId, long token) throws SQLException;

    void deleteProfileToken(int profileId) throws SQLException;

    void deleteUserToken(int userId) throws SQLException;
}
