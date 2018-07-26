package odms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import odms.enums.CountriesEnum;

public class MySqlCountryDAO implements CountryDAO {

    @Override
    public List<CountriesEnum> getAll() {
        List<CountriesEnum> countries = new ArrayList<>();
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        String query = "select * from countries;";
        try {
            Connection connection = connectionInstance.getConnection();

            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                countries.add(CountriesEnum.valueOf(result.getString("Name")));
            }

            stmt.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Please enter a valid read-only query.");
        }
        return countries;
    }

    @Override
    public List<CountriesEnum> getAll(boolean valid) {
        List<CountriesEnum> countries = new ArrayList<>();
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        String query = "select * from countries where Valid = ?;";
        try {
            Connection connection = connectionInstance.getConnection();

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setBoolean(1, valid);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                countries.add(CountriesEnum.valueOf(result.getString("Name")));
            }

            stmt.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Please enter a valid read-only query.");
        }
        return countries;
    }
}
