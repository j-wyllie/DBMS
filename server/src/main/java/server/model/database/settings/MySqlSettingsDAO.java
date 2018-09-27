package server.model.database.settings;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.UserType;
import server.model.database.DatabaseConnection;
import server.model.enums.KeyEnum;

@Slf4j
public class MySqlSettingsDAO implements SettingsDAO {

    /**
     * Get all available country options and their setting.
     *
     * @return the list of countries.
     */
    @Override
    public List<String> getAllCountries() {
        List<String> countries = new ArrayList<>();
        String query = "SELECT * FROM countries;";
        try (Connection connection = DatabaseConnection.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet result = stmt.executeQuery(query)) {
            while (result.next()) {
                String countryName = result.getString(KeyEnum.NAME.toString());
                if (!countryName.equals("")) {
                    CountriesEnum country = CountriesEnum.valueOf(countryName);
                    countries.add(country.getName());
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return countries;
    }

    /**
     * Gets all invalid or valid countries.
     *
     * @param valid true if valid countries are required.
     * @return the list of countries.
     */
    @Override
    public List<String> getAllCountries(boolean valid) {
        List<String> countries = new ArrayList<>();
        String query = "SELECT * FROM countries WHERE Valid = ?;";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, valid);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    countries.add(
                            CountriesEnum.valueOf(result.getString("Name")).getName()
                    );
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return countries;
    }

    /**
     * Updates the setting for a specific country.
     *
     * @param country to update.
     * @param valid state to update the country to.
     */
    @Override
    public void updateCountries(CountriesEnum country, boolean valid) {
        String query = "UPDATE countries SET Valid = ? WHERE Name = ?;";
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
     */
    @Override
    public void populateCountriesTable() {
        String query = "TRUNCATE TABLE countries";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.executeUpdate();

            for (CountriesEnum country : CountriesEnum.values()) {
                query = "INSERT INTO countries (Name) VALUES (?)";

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
     *
     * @param id of the user.
     * @param userType type of user.
     * @return the locale setting.
     */
    @Override
    public String getDateTimeFormat(int id, UserType userType) {
        String query = null;
        if (userType.equals(UserType.PROFILE) || userType.equals(UserType.DONOR)) {
            query = "SELECT DateTimeFormat FROM locale WHERE ProfileId = ?;";
        } else if (userType.equals(UserType.ADMIN) || userType.equals(UserType.CLINICIAN)) {
            query = "SELECT DateTimeFormat FROM locale WHERE UserId = ?;";
        }
        return getLocale(query, id);
    }

    /**
     * Gets the locale setting for a user for number formatting.
     *
     * @param id of the user.
     * @param userType type of user.
     * @return the locale setting.
     */
    @Override
    public String getNumberFormat(int id, UserType userType) {
        String query = null;
        if (userType.equals(UserType.PROFILE) || userType.equals(UserType.DONOR)) {
            query = "SELECT NumberFormat FROM locale WHERE ProfileId = ?;";
        } else if (userType.equals(UserType.ADMIN) || userType.equals(UserType.CLINICIAN)) {
            query = "SELECT NumberFormat FROM locale WHERE UserId = ?;";
        }
        return getLocale(query, id);
    }

    /**
     * Executes a query to get the locale setting for a specific user.
     *
     * @param query to execute.
     * @param id of the user.
     * @return the locale returned by the query.
     */
    private String getLocale(String query, int id) {
        return executeSELECTById(query, id);
    }

    /**
     * Set the DateTimeFormat.
     *
     * @param id the user or profile ID.
     * @param userType user or profile.
     * @param locale the locale settings.
     */
    @Override
    public void setDateTimeFormat(int id, UserType userType, String locale) throws SQLException {
        String query = null;
        if (userType.equals(UserType.PROFILE) || userType.equals(UserType.DONOR)) {
            if (hasCustomLocale(id, userType)) {
                query = "update locale set DateTimeFormat = ? WHERE ProfileId = ?;";
            } else {
                query = "insert into locale (DateTimeFormat, ProfileId) values (?, ?);";
            }
        } else if (userType.equals(UserType.ADMIN) || userType.equals(UserType.CLINICIAN)) {
            if (hasCustomLocale(id, userType)) {
                query = "update locale set DateTimeFormat = ? WHERE UserId = ?;";
            } else {
                query = "insert into locale (DateTimeFormat, UserId) values (?, ?);";
            }
        }
        setFormat(query, id, locale);
    }

    /**
     * Set the NumberFormat.
     *
     * @param id the user or profile ID.
     * @param userType user or profile.
     * @param locale the locale settings.
     */
    @Override
    public void setNumberFormat(int id, UserType userType, String locale) throws SQLException {
        String query = null;
        if (userType.equals(UserType.PROFILE) || userType.equals(UserType.DONOR)) {
            if (hasCustomLocale(id, userType)) {
                query = "update locale set NumberFormat = ? WHERE ProfileId = ?;";
            } else {
                query = "insert into locale (NumberFormat, ProfileId) values (?, ?);";
            }
        } else if (userType.equals(UserType.ADMIN) || userType.equals(UserType.CLINICIAN)) {
            if (hasCustomLocale(id, userType)) {
                query = "update locale set NumberFormat = ? WHERE UserId = ?;";
            } else {
                query = "insert into locale (NumberFormat, UserId) values (?, ?);";
            }
        }
        setFormat(query, id, locale);
    }

    /**
     * Deletes the locale settings for a user.
     *
     * @param id the user or profile ID.
     * @param userType user or profile.
     */
    @Override
    public void deleteLocale(int id, UserType userType) {
        String query = null;
        if (userType.equals(UserType.PROFILE) || userType.equals(UserType.DONOR)) {
            query = "delete FROM locale WHERE ProfileId = ?;";
        } else if (userType.equals(UserType.ADMIN) || userType.equals(UserType.CLINICIAN)) {
            query = "delete FROM locale WHERE UserId = ?;";
        }
        executeDeleteById(query, id);
    }

    /**
     * Check if locale settings exist for ID.
     *
     * @param id the user or profile ID.
     * @param userType user or profile.
     * @return boolean if exists.
     */
    private boolean hasCustomLocale(int id, UserType userType) {
        boolean result = false;
        String query = null;

        if (userType.equals(UserType.PROFILE) || userType.equals(UserType.DONOR)) {
            query = "SELECT * FROM locale WHERE ProfileId = ?;";
        } else if (userType.equals(UserType.ADMIN) || userType.equals(UserType.CLINICIAN)) {
            query = "SELECT * FROM locale WHERE UserId = ?;";
        }
        if (executeSELECTById(query, id) != null) {
            result = true;
        }

        return result;
    }

    /**
     * Executes a SELECT query filtering rows by a user id.
     *
     * @param query to execute.
     * @param id to filter rows by.
     * @return the first column's value.
     */
    private String executeSELECTById(String query, int id) {
        String result = null;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet set = stmt.executeQuery()) {
                if (set.next()) {
                    result = set.getString(1);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Executes a delete query filtering rows by a user id.
     *
     * @param query to execute.
     * @param id to filter rows by.
     */
    private void executeDeleteById(String query, int id) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.execute();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Executes update or insert query on the locale table.
     *
     * @param query the SQL query.
     * @param id the user or profile ID.
     * @param locale the locale settings.
     */
    private void setFormat(String query, int id, String locale) throws SQLException {
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            stmt.setString(1, locale);
            stmt.setInt(2, id);

            stmt.executeUpdate();
        }
    }
}
