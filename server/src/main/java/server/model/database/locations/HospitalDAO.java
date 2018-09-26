package server.model.database.locations;

import java.sql.SQLException;
import java.util.List;
import odms.commons.model.locations.Hospital;

/**
 * Interface to define all of the HospitalDAO methods.
 */
public interface HospitalDAO {

    /**
     * Get all hospitals in database.
     * @return list of hospitals
     * @throws SQLException thrown when there is a server error.
     */
    List<Hospital> getAll() throws SQLException;

    /**
     * Get a hospital from database
     * @param name the name of the hospital to retrieve.
     * @return hospital object
     * @throws SQLException thrown when there is a server error.
     */
    Hospital get(String name) throws SQLException;

    /**
     * Get a hospital from the database by id.
     *
     * @param id the id of the database
     * @return the hospital object
     * @throws SQLException thrown when there is a server error.
     */
    Hospital get(int id) throws SQLException;

    /**
     * Add a hospital to the database.
     * @param hospital hospital object to add
     * @throws SQLException thrown when there is a server error.
     */
    void add(Hospital hospital) throws SQLException;

    /**
     * Edit a hospitals details.
     * @param hospital edited hospital object
     * @throws SQLException thrown when there is a server error.
     */
    void edit(Hospital hospital) throws SQLException;

    /**
     * Remove a hospital from the database by name.
     *
     * @param name the name of the hospital to remove
     * @throws SQLException thrown when there is a server error.
     */
    void remove(String name) throws SQLException;

    /**
     * Remove a hospital from the database by ID.
     *
     * @param id the ID of the hospital to remove
     * @throws SQLException thrown when there is a server error.
     */
    void remove(Integer id) throws SQLException;
}
