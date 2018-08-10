package server.model.database.country;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import server.model.database.DatabaseConnection;
import server.model.enums.CountriesEnum;

public class MySqlCountryDAO implements CountryDAO {

    @Override
    public List<String> getAll() {
        List<String> countries = new ArrayList<>();
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        String query = "select * from countries;";
        try {
            Connection connection = connectionInstance.getConnection();

            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                countries.add(CountriesEnum.valueOf(result.getString("Name")).getName());
            }

            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countries;
    }

    @Override
    public List<String> getAll(boolean valid) {
        List<String> countries = new ArrayList<>();
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        String query = "select * from countries where Valid = ?;";
        try {
            Connection connection = connectionInstance.getConnection();

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setBoolean(1, valid);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                countries.add(CountriesEnum.valueOf(result.getString("Name")).getName());
            }

            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countries;
    }

    @Override
    public void update(CountriesEnum country, boolean valid) {
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        String query = "update countries set Valid = ? where Name = ?;";
        try {
            Connection connection = connectionInstance.getConnection();

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setBoolean(1, valid);
            stmt.setString(2, country.toString());

            stmt.executeUpdate();

            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Method to be called to repopulate the countries table.
     * @throws SQLException throws sql exception
     */
    public void populateCountriesTable() throws SQLException {
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        Connection connection = connectionInstance.getConnection();
        PreparedStatement stmt = null;

        String query = "TRUNCATE TABLE countries";
        try {
            stmt = connection.prepareStatement(query);
            stmt.executeUpdate();
            stmt.close();
            connection.close();
            for (CountriesEnum country: CountriesEnum.values()) {
                connection = connectionInstance.getConnection();

                query = "insert into countries (Name) VALUES (?)";

                stmt = connection.prepareStatement(query);

                stmt.setString(1, country.toString());

                stmt.executeUpdate();

                stmt.close();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
