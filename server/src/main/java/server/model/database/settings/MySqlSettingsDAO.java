package server.model.database.settings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.UserType;
import server.model.database.DatabaseConnection;

@Slf4j
public class MySqlSettingsDAO implements SettingsDAO {

    /**
     * Get all available country options and their setting.
     * @return the list of countries.
     */
    @Override
    public List<String> getAllCountries() {
        List<String> countries = new ArrayList<>();
        String query = "select * from countries;";
        try (Connection connection = DatabaseConnection.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery(query)) {
            while (result.next()) {
                countries.add(CountriesEnum.valueOf(result.getString("Name")).getName());
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return countries;
    }

    /**
     * Gets all invalid or valid countries.
     * @param valid true if valid countries are required.
     * @return the list of countries.
     */
    @Override
    public List<String> getAllCountries(boolean valid) {
        List<String> countries = new ArrayList<>();
        String query = "select * from countries where Valid = ?;";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, valid);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    countries.add(CountriesEnum.valueOf(result.getString("Name")).getName());
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return countries;
    }

    /**
     * Updates the setting for a specific country.
     * @param country to update.
     * @param valid state to update the country to.
     */
    @Override
    public void updateCountries(CountriesEnum country, boolean valid) {
        String query = "update countries set Valid = ? where Name = ?;";
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, valid);
            stmt.setString(2, country.toString());

            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Method to be called to repopulate the countries table.
     * @throws SQLException throws sql exception
     */
    @Override
    public void populateCountriesTable() {
        String query = "TRUNCATE TABLE countries";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.executeUpdate();

            for (CountriesEnum country: CountriesEnum.values()) {
                query = "insert into countries (Name) VALUES (?)";

                try (PreparedStatement countryStmt = connection.prepareStatement(query)) {

                    countryStmt.setString(1, country.toString());

                    countryStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Gets the locale setting for a user for date time formatting.
     * @param id of the user.
     * @param userType type of user.
     * @return the locale setting.
     */
    @Override
    public String getDateTimeFormat(int id, UserType userType) throws SQLException {
        String query = null;
        if (userType.equals(UserType.PROFILE) || userType.equals(UserType.DONOR)) {
            query = "select DateTimeFormat from locale where ProfileId = ?;";
        } else if (userType.equals(UserType.ADMIN) || userType.equals(UserType.CLINICIAN)) {
            query = "select DateTimeFormat from locale where UserId = ?;";
        }
        return getLocale(query, id);
    }

    /**
     * Gets the locale setting for a user for number formatting.
     * @param id of the user.
     * @param userType type of user.
     * @return the locale setting.
     */
    @Override
    public String getNumberFormat(int id, UserType userType) throws SQLException {
        String query = null;
        if (userType.equals(UserType.PROFILE) || userType.equals(UserType.DONOR)) {
            query = "select NumberFormat from locale where ProfileId = ?;";
        } else if (userType.equals(UserType.ADMIN) || userType.equals(UserType.CLINICIAN)) {
            query = "select NumberFormat from locale where UserId = ?;";
        }
        return getLocale(query, id);
    }

    /**
     * Executes a query to get the locale setting for a specific user.
     * @param query to execute.
     * @param id of the user.
     * @return the locale returned by the query.
     */
    private String getLocale(String query, int id) throws SQLException {
        ResultSet set = executeSelectById(query, id);
        set.next();
        String result = set.getString(1);
        set.close();
        return result;
    }

    /**
     * Set the DateTimeFormat.
     * @param id the user or profile ID.
     * @param userType user or profile.
     * @param locale the locale settings.
     */
    @Override
    public void setDateTimeFormat(int id, UserType userType, String locale) throws SQLException {
        String query = null;
        if (userType.equals(UserType.PROFILE) || userType.equals(UserType.DONOR)) {
            if (hasCustomLocale(id, userType)) {
                query = "update locale set DateTimeFormat = ? where ProfileId = ?;";
            } else {
                query = "insert into locale (DateTimeFormat, ProfileId) values (?, ?);";
            }
        } else if (userType.equals(UserType.ADMIN) || userType.equals(UserType.CLINICIAN)) {
            if (hasCustomLocale(id, userType)) {
                query = "update locale set DateTimeFormat = ? where UserId = ?;";
            } else {
                query = "insert into locale (DateTimeFormat, UserId) values (?, ?);";
            }
        }
        setFormat(query, id, locale);
    }

    /**
     * Set the NumberFormat.
     * @param id the user or profile ID.
     * @param userType user or profile.
     * @param locale the locale settings.
     */
    @Override
    public void setNumberFormat(int id, UserType userType, String locale) throws SQLException {
        String query = null;
        if (userType.equals(UserType.PROFILE) || userType.equals(UserType.DONOR)) {
            if (hasCustomLocale(id, userType)) {
                query = "update locale set NumberFormat = ? where ProfileId = ?;";
            } else {
                query = "insert into locale (NumberFormat, ProfileId) values (?, ?);";
            }
        } else if (userType.equals(UserType.ADMIN) || userType.equals(UserType.CLINICIAN)) {
            if (hasCustomLocale(id, userType)) {
                query = "update locale set NumberFormat = ? where UserId = ?;";
            } else {
                query = "insert into locale (NumberFormat, UserId) values (?, ?);";
            }
        }
        setFormat(query, id, locale);
    }

    /**
     * Check if locale settings exist for ID.
     * @param id the user or profile ID.
     * @param userType user or profile.
     * @return boolean if exists.
     */
    private boolean hasCustomLocale(int id, UserType userType) throws SQLException {
        boolean result;
        String query = null;

        if (userType.equals(UserType.PROFILE) || userType.equals(UserType.DONOR)) {
            query = "select * from locale where ProfileId = ?;";
        } else if (userType.equals(UserType.ADMIN) || userType.equals(UserType.CLINICIAN)) {
            query = "select * from locale where UserId = ?;";
        }
        ResultSet set =  executeSelectById(query, id);
        result = set.next();
        set.close();

        return result;
    }

    /**
     * Executes a select query filtering rows by a user id.
     * @param query to execute.
     * @param id to filter rows by.
     * @return the result set.
     */
    private ResultSet executeSelectById(String query, int id) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                return stmt.executeQuery();
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Executes update or insert query on the locale table.
     * @param query the SQL query.
     * @param id the user or profile ID.
     * @param locale the locale settings.
     */
    private void setFormat(String query, int id, String locale) {
        try {
            Connection connection = DatabaseConnection.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setString(2, locale);

                stmt.executeUpdate();
            }
            connection.close();

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }
}
