package odms.controller.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import odms.controller.user.UserNotFoundException;
import odms.model.user.User;
import odms.model.user.UserType;

public class MySqlUserDAO implements UserDAO {

    /**
     * Gets all users from the database.
     * @return ArrayList of all users in the database
     */
    @Override
    public ArrayList<User> getAll() throws SQLException {
        ArrayList<User> allUsers = new ArrayList<>();

        String query = "select * from users;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        Connection conn = connectionInstance.getConnection();
        Statement stmt = conn.createStatement();
        try {

            ResultSet allUserRows = stmt.executeQuery(query);

            while (allUserRows.next()) {
                User user = parseUser(allUserRows);
                allUsers.add(user);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
            stmt.close();
        }

        return allUsers;
    }

    /**
     * Gets a single user from the database by id.
     * @param userId of the user.
     * @return the specified user.
     * @throws UserNotFoundException error.
     */
    public User get(int userId) throws UserNotFoundException, SQLException {
        String query = "select * from users where UserId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        User user = null;
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            user = parseUser(rs);
        }
        catch (SQLException e) {
            throw new UserNotFoundException("Not found", userId);
        } finally {
            conn.close();
            stmt.close();
        }

        return user;
    }

    /**
     * Gets a single user from the database by userID.
     * @param username of the user.
     * @return the specified user.
     * @throws UserNotFoundException error.
     */
    public User get(String username) throws UserNotFoundException, SQLException {
        String query = "select * from users where Username = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        User user;
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            rs.next();
            user = parseUser(rs);

        }
        catch (SQLException e) {
            throw new UserNotFoundException("Not found", username);
        } finally {
            conn.close();
            stmt.close();
        }

        return user;
    }

    /**
     * Parses a single row of the user table and converts it to a user object.
     * @param rs the result set.
     * @return parsed user.
     * @throws SQLException error.
     */
    private User parseUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("UserId");
        String username = rs.getString("Username");
        String password = rs.getString("Password");
        String name = rs.getString("Name");
        UserType userType = convertStringToUserType(rs.getString("UserType"));
        String address = rs.getString("Address");
        String region = rs.getString("Region");
        LocalDateTime created = rs.getTimestamp("Created").toLocalDateTime();
        LocalDateTime updated = rs.getTimestamp("LastUpdated").toLocalDateTime();

        User user = new User(id, username, password, name, userType, address, region, created, updated);

        return user;
    }

    // ToDo: implement this with proper enum usage
    private UserType convertStringToUserType(String name) {
        switch (name) {
            case "ADMIN":
                return UserType.ADMIN;
            case "CLINICIAN":
                return UserType.CLINICIAN;
            case "PROFILE":
                return UserType.PROFILE;
            default:
                return UserType.PROFILE;
        }
    }

    /**
     * Adds a new user to the database.
     * @param user to add.
     */
    @Override
    public void add(User user) throws SQLException {
        String query = "insert into users (Username, Password, Name, UserType, Address,"
                + " Region, Created, LastUpdated, IsDefault) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getUserType().toString());
            stmt.setString(5, user.getWorkAddress());
            stmt.setString(6, user.getRegion());
            stmt.setString(7, LocalDateTime.now().toString());
            stmt.setString(8, LocalDateTime.now().toString());
            stmt.setBoolean(9, user.getDefault()); }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
            stmt.close();
        }
    }

    /**
     * Checks if a username already exists in the database.
     * @param username to check.
     * @return true is the username does not already exist.
     */
    @Override
    public boolean isUniqueUsername(String username) throws SQLException {
        String query = "select * from users where Username = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setString(1, username);

            ResultSet result = stmt.executeQuery();

            if (result.getFetchSize() == 0) {
                return true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
            stmt.close();
        }
        return false;
    }

    /**
     * Removes a user from the database.
     * @param user to remove.
     */
    @Override
    public void remove(User user) throws SQLException {
        String query = "delete from users where UserId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setInt(1, user.getStaffID());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
            stmt.close();
        }
    }

    /**
     * Updates a users information in the database.
     * @param user to update.
     */
    @Override
    public void update(User user) throws SQLException {
        String query = "update users set Username = ?, Password = ?, Name = ?, UserType = ?, "
                + "Address = ?, Region = ?, LastUpdated = ?, IsDefault = ? where "
                + "UserId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getUserType().toString());
            stmt.setString(5, user.getWorkAddress());
            stmt.setString(6, user.getRegion());
            stmt.setString(7, user.getLastUpdated().toString());
            stmt.setBoolean(8, user.getDefault());
            stmt.setInt(9, user.getStaffID());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
            stmt.close();
        }
    }
}
