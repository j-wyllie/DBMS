package server.model.database.locations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import odms.commons.model.locations.Hospital;
import server.model.database.DatabaseConnection;

/**
 * Implements all of the HospitalDAO methods.
 */
public class MySqlHospitalDAO implements HospitalDAO {

    /**
     * Get all hospitals in database.
     *
     * @return list of hospitals
     * @throws SQLException thrown when there is a server error.
     */
    @Override
    public List<Hospital> getAll() throws SQLException {
        String query = "SELECT * FROM hospitals";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        List<Hospital> result = new ArrayList<>();
        Connection conn = connectionInstance.getConnection();
        try (Statement stmt = conn.createStatement()) {

            ResultSet allHospitals = stmt.executeQuery(query);

            while (allHospitals.next()) {
                Hospital newHospital = parseHospital(allHospitals);
                result.add(newHospital);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return result;
    }

    /**
     * Get a hospital from database.
     *
     * @param id the id of the hospital to retrieve
     * @return hospital object
     * @throws SQLException thrown when there is a server error.
     */
    @Override
    public Hospital get(int id) throws SQLException {
        String query = "SELECT * FROM hospitals WHERE id = ?";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        Connection conn = connectionInstance.getConnection();
        Hospital result = null;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet allHospitals = stmt.executeQuery();

            result = parseHospital(allHospitals);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return result;
    }

    /**
     * Creates a hospital object from a result set.
     * @param resultSet query results, contains hospital data
     * @return hospital object
     * @throws SQLException thrown when there is a server error.
     */
    private Hospital parseHospital(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("Id");
        String name = resultSet.getString("Name");
        String address = resultSet.getString("Address");
        Double longitude = resultSet.getDouble("Longitude");
        Double latitude = resultSet.getDouble("Latitude");
        return new Hospital(name, latitude, longitude, address, null, id);
    }

    /**
     * Add a hospital to the database.
     *
     * @param hospital hospital object to add
     * @throws SQLException thrown when there is a server error.
     */
    @Override
    public void add(Hospital hospital) throws SQLException {
        String query = "INSERT INTO hospitals (Name, Address, Latitude, Longitude) VALUES (?,?,?,?)";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        Connection conn = connectionInstance.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, hospital.getName());
            stmt.setString(2, hospital.getAddress());
            stmt.setDouble(3, hospital.getLatitude());
            stmt.setDouble(4, hospital.getLongitude());

            stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }

    /**
     * Edit a hospitals details.
     *
     * @param hospital edited hospital object
     * @throws SQLException thrown when there is a server error.
     */
    @Override
    public void edit(Hospital hospital) throws SQLException {
        String query = "UPDATE hospitals SET Name = ?, Address = ?, Latitude = ?, Longitude = ?" +
                "WHERE Id = ?";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        Connection conn = connectionInstance.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, hospital.getName());
            stmt.setString(2, hospital.getAddress());
            stmt.setDouble(3, hospital.getLatitude());
            stmt.setDouble(4, hospital.getLongitude());
            stmt.setInt(5, hospital.getId());

            stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }

    /**
     * Remove a hospital from the database.
     *
     * @param hospital the hospital object to remove
     * @throws SQLException thrown when there is a server error.
     */
    @Override
    public void remove(Integer id) throws SQLException {
        String query = "DELETE FROM hospitals WHERE Id = ?";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        Connection conn = connectionInstance.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }
}
