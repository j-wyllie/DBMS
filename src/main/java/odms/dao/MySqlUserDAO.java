package odms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
            conn.close();

            while (allUserRows.next()) {
                User user = parseUser(allUserRows);
                allUsers.add(user);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return allUsers;
    }

    /**
     * Gets a single user from the database by userID.
     * @return the specified user
     */
    public User getUser(int userId) {
        String query = "select * from user when UserId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        User user = null;

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            conn.close();

            UserType userType = convertStringToUserType(rs.getString("UserType"));
            String name = rs.getString("Name");
            String region = rs.getString("Region");
            user = new User(userType, name, region);
            user.setStaffID(rs.getInt("UserId"));
            user.setWorkAddress(rs.getString("Address"));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * Parses a single row of the user table and converts it to a user object
     * @param rs
     * @return parsed user
     * @throws SQLException
     */
    private User parseUser(ResultSet rs) throws SQLException {
        UserType userType = convertStringToUserType(rs.getString("UserType"));
        String name = rs.getString("Name");
        String region = rs.getString("Region");
        User user = new User(userType, name, region);
        user.setStaffID(rs.getInt("UserId"));
        user.setWorkAddress(rs.getString("Address"));

        return user;
    }

    // ToDo: implement this with proper enum usage
    private UserType convertStringToUserType(String name) {
        switch (name) {
            case "administrator":
                return UserType.ADMIN;
            case "clinician":
                return UserType.CLINICIAN;
            case "profile":
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
        String query = "insert into user (UserId, Username, Password, Name, UserType, Address,"
                + " Region, Created, LastUpdated, IsDefault) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, user.getStaffID());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getName());
            stmt.setString(5, user.getUserType().toString());
            stmt.setString(6, user.getWorkAddress());
            stmt.setString(7, user.getRegion());
            stmt.setString(8, user.getTimeOfCreation().toString());
            stmt.setString(9, user.getLastUpdated().toString());
            stmt.setBoolean(10, user.getDefault());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a user from the database.
     * @param user to remove.
     */
    @Override
    public void remove(User user) {
        String query = "delete from user where UserId = ?;";
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
        String query = "update user set Username = ?, Password = ?, Name = ?, UserType = ?, "
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
