package odms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import odms.medications.Drug;
import odms.profile.Profile;

public class MySqlMedicationDAO implements MedicationDAO {

    /**
     * Gets all drugs from the database for a single profile.
     * @param profile to get the drugs from.
     */
    @Override
    public void getAll(Profile profile) {
        String query = "select * from drugs where ProfileId = ?;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();

        try {
            Connection conn = connectionInstance.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());

            ResultSet allDrugs = stmt.executeQuery();
            conn.close();

            //todo: return drugs in some kind of set/list.
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new drug for a profile stored in the database.
     * @param drug to add.
     * @param profile to add the drug to.
     * @param current is true if the profile is currently taking the drug.
     */
    @Override
    public void add(Drug drug, Profile profile, Boolean current) {
        String query = "insert into drugs (ProfileId, Drug, Current, Past) values (?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, drug.getDrugName());
            stmt.setBoolean(3, current);
            stmt.setBoolean(4, !current);

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a drug from a profile stored in the database.
     * @param drug to remove.
     * @param profile to remove the drug from.
     */
    @Override
    public void remove(Drug drug, Profile profile) {
        String query = "delete from drugs where ProfileId = ? and Drug = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, drug.getDrugName());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates drug information for a profile in the database.
     * @param drug to update.
     * @param profile to update the drug for.
     * @param current is true if the profile is currently taking the drug.
     */
    @Override
    public void update(Drug drug, Profile profile, Boolean current) {
        String query = "update drugs set Drug = ?, Current = ?, Past = ? where ProfileId = ? and "
                + "Drug = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, drug.getDrugName());
            stmt.setBoolean(2, current);
            stmt.setBoolean(3, !current);
            stmt.setInt(4, profile.getId());
            stmt.setString(5, drug.getDrugName());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
