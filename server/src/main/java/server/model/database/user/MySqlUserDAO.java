package server.model.database.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.UserType;
import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import server.model.database.DatabaseConnection;
import server.model.database.PasswordUtilities;

/**
 * Handles all of the database queries to do with the /users endpoint.
 */
@Slf4j
public class MySqlUserDAO implements UserDAO {

    private static final String NOT_FOUND = "Not found";

    /**
     * Gets all users from the database.
     *
     * @return ArrayList of all users in the database
     */
    @Override
    public List<User> getAll() {
        ArrayList<User> allUsers = new ArrayList<>();
        String query = "SELECT * FROM users;";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            try (ResultSet allUserRows = stmt.executeQuery(query)) {
                while (allUserRows.next()) {
                    User user = parseUser(allUserRows);
                    allUsers.add(user);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return allUsers;
    }

    /**
     * Gets a single user from the database by id.
     *
     * @param userId of the user.
     * @return the specified user.
     * @throws UserNotFoundException error.
     */
    public User get(int userId) throws UserNotFoundException {
        String query = "SELECT * FROM users WHERE UserId = ?;";
        User user;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {

                rs.next();
                user = parseUser(rs);
            }
        } catch (SQLException e) {
            throw new UserNotFoundException(NOT_FOUND, userId);
        }
        return user;
    }

    /**
     * Gets a single user from the database by userID.
     *
     * @param username of the user.
     * @return the specified user.
     * @throws UserNotFoundException error.
     */
    public User get(String username) throws UserNotFoundException {
        String query = "SELECT * FROM users WHERE Username = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return parseUser(rs);
            }
        } catch (SQLException e) {
            throw new UserNotFoundException(NOT_FOUND, username);
        }
    }

    @Override
    public Boolean checkCredentials(String username, String password) throws UserNotFoundException {
        String query = "SELECT Username, Password FROM users WHERE Username = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return PasswordUtilities.check(password, rs.getString("Password"));
            }

        } catch (SQLException e) {
            throw new UserNotFoundException(NOT_FOUND, username);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Parses a single row of the user table and converts it to a user object.
     *
     * @param rs the result set.
     * @return parsed user.
     * @throws SQLException error.
     */
    private User parseUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("UserId");
        String username = rs.getString("Username");
        String password = rs.getString("Password");
        String name = rs.getString("Name");
        UserType userType = UserType.valueOf(rs.getString("UserType"));
        String address = rs.getString("Address");
        String region = rs.getString("Region");
        boolean defaultBool = rs.getBoolean("IsDefault");
        LocalDateTime created = rs.getTimestamp("Created").toLocalDateTime();
        LocalDateTime updated = rs.getTimestamp("LastUpdated").toLocalDateTime();
        String imageName = rs.getString("ImageName");
        User user = new User(id,
                username,
                password,
                name,
                userType,
                address,
                region,
                created,
                updated,
                imageName
        );
        user.setDefault(defaultBool);
        return user;
    }

    /**
     * Adds a new user to the database.
     *
     * @param user to add.
     */
    @Override
    public void add(User user) throws SQLException {
        String query = "INSERT INTO users (Username, Password, Name, UserType, Address," +
                " Region, Created, LastUpdated, IsDefault, ImageName) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            System.out.println("Adding: " + 1);
            stmt.setString(1, user.getUsername());
            System.out.println("Adding: " + 2);
            stmt.setString(2, PasswordUtilities.getSaltedHash(user.getPassword()));
            System.out.println("Adding: " + 3);
            stmt.setString(3, user.getName());
            System.out.println("Adding: " + 4);
            stmt.setString(4, user.getUserType().toString());
            System.out.println("Adding: " + 5);
            stmt.setString(5, user.getWorkAddress());
            System.out.println("Adding: " + 6);
            stmt.setString(6, user.getRegion());
            System.out.println("Adding: " + 7);
            stmt.setString(7, LocalDateTime.now().toString());
            System.out.println("Adding: " + 8);
            stmt.setString(8, LocalDateTime.now().toString());
            System.out.println("Adding: " + 9);
            stmt.setBoolean(9, user.getDefault());
            System.out.println("Adding: " + 10);
            stmt.setString(10, user.getPictureName());
            stmt.execute();
        } catch (SQLException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new SQLException();
        }
    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username to check.
     * @return true if the username does not already exist.
     */
    @Override
    public boolean isUniqueUsername(String username) {
        String query = "SELECT count(Username) as total FROM users WHERE Username = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.getInt("total") == 0;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    /**
     * Removes a user from the database.
     *
     * @param user to remove.
     */
    @Override
    public void remove(User user) {
        String query = "DELETE FROM users WHERE UserId = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user.getStaffID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Updates a users information in the database.
     *
     * @param user to update.
     */
    @Override
    public void update(User user) {
        String query = "UPDATE users SET Username = ?, Password = ?, Name = ?, UserType = ?, "
                + "Address = ?, Region = ?, LastUpdated = ?, IsDefault = ?, ImageName = ? "
                + "WHERE UserId = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getUserType().toString());
            stmt.setString(5, user.getWorkAddress());
            stmt.setString(6, user.getRegion());
            stmt.setString(7, user.getLastUpdated().toString());
            stmt.setBoolean(8, user.getDefault());
            stmt.setString(9, user.getPictureName());
            stmt.setString(10, user.getPictureName());
            stmt.setInt(10, user.getStaffID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }
}
