package odms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import odms.medications.Drug;

public class MySqlMedicationInteractionsDAO implements MedicationInteractionsDAO {

    /**
     * Get all interactions between two drugs stored in the database.
     * @param drugA of the interactions.
     * @param drugB of the interactions
     */
    @Override
    public void get(Drug drugA, Drug drugB) {
        String query = "select * from medication_interactions where DrugA = ?"
                + " and DrugB = ?;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();

        try {
            Connection conn = connectionInstance.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, drugA.getDrugName());
            stmt.setString(2, drugB.getDrugName());


            ResultSet allInteractions = stmt.executeQuery();
            conn.close();

            //todo: return interactions in some kind of set/list.
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new interaction between two drugs in the database.
     * @param drugA to interact.
     * @param drugB to interact.
     * @param symptom of the drug interactions.
     * @param duration of the drug interactions.
     */
    @Override
    public void add(Drug drugA, Drug drugB, String symptom, String duration) {
        String query = "insert into medication_interactions (DrugA, DrugB, Symptom, Duration)"
                + " values (?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, drugA.getDrugName());
            stmt.setString(2, drugB.getDrugName());
            stmt.setString(3, symptom);
            stmt.setString(4, duration);

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove all interactions between two drugs.
     * @param drugA of the interactions to remove.
     * @param drugB of the interactions to remove.
     */
    @Override
    public void removeAll(Drug drugA, Drug drugB) {
        String query = "delete from medication_interactions where DrugA = ? "
                + "and DrugB = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, drugA.getDrugName());
            stmt.setString(2, drugB.getDrugName());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove a particular interaction between two drugs.
     * @param drugA of the interaction to remove.
     * @param drugB of the interaction to remove.
     * @param symptom of the interaction to remove.
     * @param duration of the interaction to remove.
     */
    @Override
    public void remove(Drug drugA, Drug drugB, String symptom, String duration) {
        String query = "delete from medication_interactions where DrugA = ? "
                + "and DrugB = ? and Symptom = ? and Duration = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, drugA.getDrugName());
            stmt.setString(2, drugB.getDrugName());
            stmt.setString(3, symptom);
            stmt.setString(4, duration);

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update a particular interaction between two drugs.
     * @param drugA of the interaction to update.
     * @param drugB of the interaction to update.
     * @param symptom of the interaction to update.
     * @param duration of the interaction to update.
     */
    @Override
    public void update(Drug drugA, Drug drugB, String symptom, String duration) {
        String query = "update medication_interactions set Duration = ? where DrugA = ? "
                + "and DrugB = ? and Symptom = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, drugA.getDrugName());
            stmt.setString(2, drugB.getDrugName());
            stmt.setString(3, symptom);
            stmt.setString(4, duration);

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
