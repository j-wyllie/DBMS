package odms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

import odms.user.User;
import odms.data.UserDatabase;
import odms.user.UserType;

public class MySqlUserDAO implements UserDAO {

    /**
     * Gets all users from the database.
     * @return ArrayList of all users in the database
     */
    @Override
    public ArrayList<User> getAll() {
        ArrayList<User> allUsers = new ArrayList<>();

        String query = "select * from users;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();

        try {
            Connection conn = connectionInstance.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet allUserRows = stmt.executeQuery(query);

            while (allUserRows.next()) {
                User user = parseUser(allUserRows);
                allUsers.add(user);
            }
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return allUsers;
    }

    /**
     * Gets a single user from the database by userID.
     * @return the specified user.
     */
    public User getUser(int userId) {
        String query = "select * from users where UserId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        User user = null;

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            conn.close();

            user = parseUser(rs);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * Gets a single user from the database by userID.
     * @param username of the user.
     * @return the specified user.
     */
    public User getUser(String username) {
        String query = "select * from users where Username = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        User user = null;

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            rs.next();
            user = parseUser(rs);
            conn.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
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
    public void add(User user) {
        String query = "insert into users (Username, Password, Name, UserType, Address,"
                + " Region, Created, LastUpdated, IsDefault) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getUserType().toString());
            stmt.setString(5, user.getWorkAddress());
            stmt.setString(6, user.getRegion());
            stmt.setString(7, LocalDateTime.now().toString());
            stmt.setString(8, LocalDateTime.now().toString());
            stmt.setBoolean(9, user.getDefault());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a username already exists in the database.
     * @param username to check.
     * @return true is the username does not already exist.
     */
    @Override
    public boolean isUniqueUsername(String username) {
        String query = "select * from users where Username = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet result = stmt.executeQuery();

            if (result.getFetchSize() == 0) {
                return true;
            }
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Removes a user from the database.
     * @param user to remove.
     */
    @Override
    public void remove(User user) {
        String query = "delete from users where UserId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, user.getStaffID());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a users information in the database.
     * @param user to update.
     */
    @Override
    public void update(User user) {
        String query = "update users set Username = ?, Password = ?, Name = ?, UserType = ?, "
                + "Address = ?, Region = ?, LastUpdated = ?, IsDefault = ? where"
                + "UserId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getUserType().toString());
            stmt.setString(5, user.getWorkAddress());
            stmt.setString(6, user.getRegion());
            stmt.setString(7, user.getLastUpdated().toString());
            stmt.setInt(8, user.getStaffID());
            stmt.setBoolean(9, user.getDefault());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
