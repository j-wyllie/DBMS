package odms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odms.medications.Drug;
import odms.profile.Profile;

public class MySqlMedicationDAO implements MedicationDAO {

    /**
     * Gets all the current and past drugs from the database for a single profile.
     * @param profile to get the drugs from.
     * @return a map of current and past drugs.
     */
    @Override
    public Map<String, List<Drug>> getAll(Profile profile) {
        String query = "select * from drugs where ProfileId = ?;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        Map<String, List<Drug>>  result = new HashMap<>();
        List<Drug> currentDrugs = new ArrayList<>();
        List<Drug> pastDrugs = new ArrayList<>();

        try {
            Connection conn = connectionInstance.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());

            ResultSet allDrugs = stmt.executeQuery();
            conn.close();

            while (allDrugs.next()) {
                Drug drug = parseDrug(allDrugs);

                if (allDrugs.getBoolean("Current")) {
                    currentDrugs.add(drug);
                }
                else {
                    pastDrugs.add(drug);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        result.put("Current", currentDrugs);
        result.put("Past", pastDrugs);

        return result;
    }

    /**
     * Parse the current row into a drug object.
     * @param drugs rows returned from the database.
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
     */
    @Override
    public void remove(Drug drug) {
        String query = "delete from drugs where Id = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, drug.getId());

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
     * @param current is true if the profile is currently taking the drug.
     */
    @Override
    public void update(Drug drug, Boolean current) {
        String query = "update drugs set Drug = ?, Current = ?, Past = ? where Id = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, drug.getDrugName());
            stmt.setBoolean(2, current);
            stmt.setBoolean(3, !current);
            stmt.setInt(4, drug.getId());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
