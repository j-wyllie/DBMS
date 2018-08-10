package server.model.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import server.model.profile.Condition;
import server.model.profile.Profile;

public class MySqlConditionDAO implements ConditionDAO {

    /**
     * Get all conditions for the profile.
     * @param profile to get the conditions for.
     * @param chronic true if the conditions required are chronic.
     */
    @Override
    public ArrayList<Condition> getAll(Profile profile, boolean chronic) {
        String query = "select * from conditions where ProfileId = ?;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        ArrayList<Condition> allConditions = new ArrayList<>();

        try {
            Connection conn = connectionInstance.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            ResultSet allConditionRows = stmt.executeQuery();

            while (allConditionRows.next()) {
                Condition condition = parseCondition(allConditionRows);
                allConditions.add(condition);
            }
            conn.close();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
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
        if (!(rs.getDate("CuredDate") == null)) {
             dateCured = rs.getDate("CuredDate").toLocalDate();
        }

        Condition condition = new Condition(id, name, dateOfDiagnosis, isChronic, isCured, dateCured);
        return condition;
    }

    /**
     * Add a new condition to a profile.
     * @param profile to add the condition to.
     * @param condition to add.
     */
    @Override
    public void add(Profile profile, Condition condition) {
        String query = "insert into conditions (ProfileId, Description, DiagnosisDate, Chronic, "
                + "Current, Past, CuredDate) values (?, ?, ?, ?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, condition.getName());
            stmt.setDate(3, Date.valueOf(condition.getDateOfDiagnosis()));
            stmt.setBoolean(4, condition.getChronic());
            stmt.setBoolean(5, !condition.getCured());
            stmt.setBoolean(6, condition.getCured());
            stmt.setDate(7, Date.valueOf(condition.getDateCured()));

            stmt.executeUpdate();
            conn.close();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove a condition from a profile.
     * @param condition to remove.
     */
    @Override
    public void remove(Condition condition) {
        String query = "delete from conditions where Id = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, condition.getId());

            stmt.executeUpdate();
            conn.close();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update a condition for the profile.
     * @param condition to update.
     */
    @Override
    public void update(Condition condition) {
        String query = "update conditions set Description = ?, DiagnosisDate = ?, Chronic = ?, "
                + "Current = ?, Past = ?, CuredDate = ? where Id = ?";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, condition.getName());
            stmt.setDate(2, Date.valueOf(condition.getDateOfDiagnosis()));
            stmt.setBoolean(3, condition.getChronic());
            stmt.setBoolean(4, !condition.getCured());
            stmt.setBoolean(5, condition.getCured());
            stmt.setDate(6, Date.valueOf(condition.getDateCured()));
            stmt.setInt(7, condition.getId());

            stmt.executeUpdate();
            conn.close();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
