package odms.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import odms.profile.Condition;
import odms.profile.Profile;

public class MySqlConditionsDAO implements ConditionsDAO {

    /**
     * Get all conditions for the profile.
     * @param profile to get the conditions for.
     * @param current conditions or false for past conditions.
     */
    @Override
    public void getAll(Profile profile, Boolean current) {
        String query = "select * from conditions where ProfileId = ? where Current = ?;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();

        try {
            Connection conn = connectionInstance.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setBoolean(2, current);

            ResultSet allConditions = stmt.executeQuery();
            conn.close();

            //todo: return conditions in some kind of set/list.
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new condition to a profile.
     * @param profile to add the condition to.
     * @param condition to add.
     */
    @Override
    public void add(Profile profile, Condition condition) {
        String query = "insert into conditions (ProfileId, Description, DiagnosisDate, Chronic, "
                + "Current, Past, DateCured) values (?, ?, ?, ?, ?, ?, ?);";
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
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove a condition from a profile.
     * @param profile to remove the condition from.
     * @param condition to remove.
     */
    @Override
    public void remove(Profile profile, Condition condition) {
        String query = "delete from conditions where ProfileId = ? and Description = ? "
                + "and DiagnosisDate = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, condition.getName());
            stmt.setDate(3, Date.valueOf(condition.getDateOfDiagnosis()));

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update a condition for the profile.
     * @param profile to update the condition for.
     * @param condition to update.
     */
    @Override
    public void update(Profile profile, Condition condition) {
        String query = "update conditions set Description = ?, DiagnosisDate = ?, Chronic = ?, "
                + "Current = ?, Past = ?, DateCured = ? where ProfileId = ? and "
                + "Description = ? and DiagnosisDate = ?;";
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
            stmt.setInt(7, profile.getId());
            stmt.setString(8, condition.getName());
            stmt.setDate(9, Date.valueOf(condition.getDateOfDiagnosis()));

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
