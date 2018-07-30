package odms.controller.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import odms.model.enums.CountriesEnum;

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
}
