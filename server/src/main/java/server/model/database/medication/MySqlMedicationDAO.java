package server.model.database.medication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.medications.Drug;
import server.model.database.DatabaseConnection;

@Slf4j
public class MySqlMedicationDAO implements MedicationDAO {

    /**
     * Gets all the current and past drugs FROM the database for a single profile.
     * @param profile to get the drugs FROM.
     * @param current true if the current drugs are required for that profile.
     * @return a list of current or past drugs.
     */
    @Override
    public List<Drug> getAll(int profile, Boolean current) {
        String query = "SELECT * FROM drugs WHERE ProfileId = ? AND Current = ?;";
        List<Drug> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, profile);
            stmt.setBoolean(2, current);

            try (ResultSet allDrugs = stmt.executeQuery()) {
                while (allDrugs.next()) {
                    Drug drug = parseDrug(allDrugs);
                    result.add(drug);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    /**
     * Parse the current row into a drug object.
     * @param drugs rows returned FROM the database.
     * @return a drug object.
     * @throws SQLException error.
     */
    private Drug parseDrug(ResultSet drugs) throws SQLException {
        int id = drugs.getInt("Id");
        String name = drugs.getString("Drug");
        return new Drug(id, name);
    }

    /**
     * Adds a new drug for a profile stored in the database.
     * @param drug to add.
     * @param profile to add the drug to.
     * @param current is true if the profile is currently taking the drug.
     */
    @Override
    public void add(Drug drug, int profile, Boolean current) {
        String query = "INSERT INTO drugs (ProfileId, Drug, Current) VALUES (?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, profile);
            stmt.setString(2, drug.getDrugName());
            stmt.setBoolean(3, current);

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Removes a drug FROM a profile stored in the database.
     * @param drug to remove.
     */
    @Override
    public void remove(Drug drug) {
        String query = "DELETE FROM drugs WHERE Id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, drug.getId());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Updates drug information for a profile in the database.
     * @param drug to update.
     * @param current is true if the profile is currently taking the drug.
     */
    @Override
    public void update(Drug drug, Boolean current) {
        String query = "UPDATE drugs SET Drug = ?, Current = ? WHERE Id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, drug.getDrugName());
            stmt.setBoolean(2, current);
            stmt.setInt(3, drug.getId());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }
}
