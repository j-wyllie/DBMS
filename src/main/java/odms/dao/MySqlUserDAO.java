package odms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import odms.user.User;
import odms.data.UserDatabase;

public class MySqlUserDAO implements UserDAO {

    /**
     * Gets all users from the database.
     */
    @Override
    public void getAllUsers() {
        String query = "select * from users;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();

        try {
            Connection conn = connectionInstance.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet allUsers = stmt.executeQuery(query);
            conn.close();

            UserDatabase userDb = new UserDatabase();
            //todo: store users locally.
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new user to the database.
     * @param user to add.
     */
    @Override
    public void addUser(User user) {
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
    public void removeUser(User user) {
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
    public void updateUser(User user) {
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
