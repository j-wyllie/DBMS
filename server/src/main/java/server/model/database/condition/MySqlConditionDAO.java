package server.model.database.condition;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.Condition;
import server.model.database.DatabaseConnection;

@Slf4j
public class MySqlConditionDAO implements ConditionDAO {

    /**
     * Get all conditions for the profile.
     * @param profile to get the conditions for.
     * @param chronic true if the conditions required are chronic.
     * @return the list of conditions for the profile.
     */
    @Override
    public List<Condition> getAll(int profile, boolean chronic) {
        String query = "select * from conditions where ProfileId = ? and Chronic = ?;";
        ArrayList<Condition> allConditions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profile);
            stmt.setBoolean(2, chronic);
            try (ResultSet allConditionRows = stmt.executeQuery()) {
                while (allConditionRows.next()) {
                    Condition condition = parseCondition(allConditionRows);
                    allConditions.add(condition);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return allConditions;
    }


    /**
     * Get all conditions for the profile.
     * @param profile to get the conditions for.
     * @return the list of conditions for the profile.
     */
    @Override
    public List<Condition> getAll(int profile) {
        String query = "select * from conditions where ProfileId = ?;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        ArrayList<Condition> allConditions = new ArrayList<>();

        try {
            Connection conn = connectionInstance.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile);
            ResultSet allConditionRows = stmt.executeQuery();

            while (allConditionRows.next()) {
                Condition condition = parseCondition(allConditionRows);
                allConditions.add(condition);
            }
            conn.close();
            stmt.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return allConditions;
    }

    /**
     * Parses a single row of the condition table and returns a condition object
     * @param rs rows returned by the database.
     * @return the parsed condition object
     * @throws SQLException error.
     */
    private Condition parseCondition(ResultSet rs) throws SQLException {
        int id = rs.getInt("Id");
        String name = rs.getString("Description");
        LocalDate dateOfDiagnosis = rs.getDate("DiagnosisDate").toLocalDate();
        boolean isChronic = rs.getBoolean("Chronic");
        boolean isCured = rs.getBoolean("Past");
        LocalDate dateCured = null;
        if (rs.getDate("CuredDate") != null) {
             dateCured = rs.getDate("CuredDate").toLocalDate();
        }

        return new Condition(id, name, dateOfDiagnosis, isChronic, isCured, dateCured);
    }

    /**
     * Add a new condition to a profile.
     * @param profile to add the condition to.
     * @param condition to add.
     */
    @Override
    public void add(int profile, Condition condition) {
        String query = "INSERT INTO conditions (ProfileId, Description, DiagnosisDate, Chronic, "
                + "Current, Past, CuredDate) VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, profile);
            stmt.setString(2, condition.getName());
            stmt.setDate(3, Date.valueOf(condition.getDateOfDiagnosis()));
            stmt.setBoolean(4, condition.getChronic());
            stmt.setBoolean(5, !condition.getCured());
            stmt.setBoolean(6, condition.getCured());
            if (condition.getDateCured() == null) {
                stmt.setNull(7, Types.DATE);
            } else {
                stmt.setDate(7, Date.valueOf(condition.getDateCured()));
            }
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Remove a condition from a profile.
     * @param condition to remove.
     */
    @Override
    public void remove(Condition condition) {
        String query = "DELETE FROM conditions WHERE Id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, condition.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Update a condition for the profile.
     * @param condition to updateCountries.
     */
    @Override
    public void update(Condition condition) {
        String query = "UPDATE conditions SET Description = ?, DiagnosisDate = ?, Chronic = ?, "
                + "Current = ?, Past = ?, CuredDate = ? WHERE Id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, condition.getName());
            stmt.setDate(2, Date.valueOf(condition.getDateOfDiagnosis()));
            stmt.setBoolean(3, condition.getChronic());
            stmt.setBoolean(4, !condition.getCured());
            stmt.setBoolean(5, condition.getCured());
            if (condition.getDateCured() == null) {
                stmt.setNull(6, Types.DATE);
            } else {
                stmt.setDate(6, Date.valueOf(condition.getDateCured()));
            }
            stmt.setInt(7, condition.getId());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }
}
