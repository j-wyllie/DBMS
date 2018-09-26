package server.model.database.locations;

import com.google.common.base.CaseFormat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.locations.Hospital;
import server.model.database.DatabaseConnection;

/**
 * Implements all of the HospitalDAO methods.
 */
@Slf4j
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
        List<Hospital> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            try (ResultSet allHospitals = stmt.executeQuery(query)) {
                while (allHospitals.next()) {
                    Hospital newHospital = parseHospital(allHospitals);
                    result.add(newHospital);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return result;
    }

    /**
     * Get a hospital from database.
     *
     * @param name the name of the hospital to retrieve
     * @return hospital object
     * @throws SQLException thrown when there is a server error.
     */
    @Override
    public Hospital get(String name) throws SQLException {
        Hospital result = null;
        String query = "SELECT * FROM hospitals WHERE Name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);

            try (ResultSet allHospitals = stmt.executeQuery()) {
                while (allHospitals.next()) {
                    result = parseHospital(allHospitals);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return result;
    }

    /**
     * Get a hospital from the database by id.
     *
     * @param id the id of the database
     * @return the hospital object
     * @throws SQLException thrown when there is a server error.
     */
    public Hospital get(int id) throws SQLException {
        Hospital result = null;
        String query = "SELECT * FROM hospitals WHERE Id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet allHospitals = stmt.executeQuery()) {
                while (allHospitals.next()) {
                    result = parseHospital(allHospitals);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return result;
    }

    /**
     * Creates a hospital object from a result set.
     *
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

        List<Boolean> organPrograms = new ArrayList<>();
        for (OrganEnum organ : OrganEnum.values()) {
            organPrograms.add(
                    resultSet.getBoolean(
                            CaseFormat.UPPER_UNDERSCORE.to(
                                    CaseFormat.UPPER_CAMEL, organ.toString()
                            )
                    )
            );
        }

        return new Hospital(name, latitude, longitude, address, organPrograms, id);
    }

    /**
     * Add a hospital to the database.
     *
     * @param hospital hospital object to add
     * @throws SQLException thrown when there is a server error.
     */
    public void add(Hospital hospital) throws SQLException {
        String query = "INSERT INTO hospitals (Name, Address, Latitude, Longitude, Bone," +
                "`BoneMarrow`, `ConnectiveTissue`, Cornea, Heart, Intestine, Kidney, Liver, " +
                "Lung, `MiddleEar`, Pancreas, Skin) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, hospital.getName());
            stmt.setString(2, hospital.getAddress());
            setDouble(3, stmt, hospital.getLatitude());
            setDouble(4, stmt, hospital.getLongitude());

            int i = 5;
            for (Boolean organBool : hospital.getPrograms()) {
                 stmt.setBoolean(i, organBool);
                 i++;
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private void setDouble(int index, PreparedStatement preparedStatement, Double val)
            throws SQLException {
        if (val == null) {
            preparedStatement.setNull(index, java.sql.Types.NULL);
        } else {
            preparedStatement.setDouble(index, val);
        }
    }

    /**
     * Edit a hospitals details.
     *
     * @param hospital edited hospital object
     * @throws SQLException thrown when there is a server error.
     */
    public void edit(Hospital hospital) throws SQLException {
        String query = "UPDATE hospitals SET Name = ?, Address = ?, Latitude = ?, Longitude = ?," +
                "Bone = ?, `BoneMarrow` = ?, `ConnectiveTissue` = ?, Cornea = ?, Heart = ?, " +
                "Intestine = ?, Kidney = ?, Liver = ?, Lung = ?, `MiddleEar` = ?, Pancreas = ?," +
                "Skin = ? WHERE Id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, hospital.getName());
            stmt.setString(2, hospital.getAddress());
            stmt.setDouble(3, hospital.getLatitude());
            stmt.setDouble(4, hospital.getLongitude());

            int i = 5;
            for (Boolean organBool : hospital.getPrograms()) {
                stmt.setBoolean(i, organBool);
                i++;
            }

            stmt.setInt(i, hospital.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Remove a hospital from the database.
     *
     * @param name the name of the hospital object to remove
     * @throws SQLException thrown when there is a server error.
     */
    public void remove(String name) throws SQLException {
        String query = "DELETE FROM hospitals WHERE Name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);

            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Remove a hospital from the database by ID.
     *
     * @param id the ID of the hospital to remove
     * @throws SQLException thrown when there is a server error.
     */
    public void remove(Integer id)  throws SQLException {
        String query = "DELETE FROM hospitals WHERE Id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    };
}
