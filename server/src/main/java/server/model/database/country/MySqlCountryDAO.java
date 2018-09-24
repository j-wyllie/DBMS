package server.model.database.country;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.CountriesEnum;
import server.model.database.DatabaseConnection;

@Slf4j
public class MySqlCountryDAO implements CountryDAO {

    @Override
    public List<String> getAll() {
        List<String> countries = new ArrayList<>();
        String query = "SELECT * FROM countries;";

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

    @Override
    public List<String> getAll(boolean valid) {
        List<String> countries = new ArrayList<>();
        String query = "SELECT * FROM countries where Valid = ?;";

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

    @Override
    public void update(CountriesEnum country, boolean valid) {
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
    public void populateCountriesTable() {
        String query = "TRUNCATE TABLE countries";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement truncStmt = connection.prepareStatement(query)) {
            truncStmt.executeUpdate();
            truncStmt.close();

            for (CountriesEnum country: CountriesEnum.values()) {
                query = "insert into countries (Name) VALUES (?)";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, country.toString());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }
}
