package odms.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import odms.enums.OrganEnum;
import odms.profile.Procedure;
import odms.profile.Profile;

public class MySqlProcedureDAO implements ProcedureDAO {

    /**
     * Get all procedures for the profile.
     * @param profile to get the conditions for.
     * @param pending procedures or false for past procedures.
     */
    @Override
    public void getAll(Profile profile, Boolean pending) {
        String query = "select * from procedures where ProfileId = ? where Pending = ?;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();

        try {
            Connection conn = connectionInstance.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setBoolean(2, pending);

            ResultSet allProcedures = stmt.executeQuery();
            conn.close();

            //todo: get affected organs at some point.
            //todo: return procedures in some kind of set/list.
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new procedure to a profile.
     * @param profile to add the procedure to.
     * @param procedure to add.
     */
    @Override
    public void add(Profile profile, Procedure procedure) {
        String query = "insert into procedures (ProfileId, Summary, Description, ProcedureDate) "
                + "values (?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, procedure.getSummary());
            stmt.setString(3, procedure.getLongDescription());
            stmt.setDate(4, Date.valueOf(procedure.getDate()));

            stmt.executeUpdate();

            //todo: insert affected organs.

            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove a procedure from a profile.
     * @param procedure to remove.
     */
    @Override
    public void remove(Procedure procedure) {
        String query = "delete from procedures where Id = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, procedure.getId());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update a procedure for the profile.
     * @param procedure to update.
     */
    @Override
    public void update(Procedure procedure) {
        String query = "update procedures set Summary = ?, Description = ?, ProcedureDate = ? "
                + "where Id = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, procedure.getSummary());
            stmt.setString(2, procedure.getLongDescription());
            stmt.setDate(3, Date.valueOf(procedure.getDate()));
            stmt.setInt(4, procedure.getId());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add an affected organ to a procedure for a profile.
     * @param procedure to add the organ to.
     * @param organ to add.
     */
    @Override
    public void addAffectedOrgan(Procedure procedure, OrganEnum organ) {
        String query = "insert into affected_organs (ProcedureId, Organ) values (?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, procedure.getId());
            stmt.setString(2, organ.getName());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove an affected organ from a procedure for a profile.
     * @param procedure to remove the organ from.
     * @param organ to remove.
     */
    @Override
    public void removeAffectedOrgan(Procedure procedure, OrganEnum organ) {
        String query = "delete from affected_organs where ProcedureId = ? and Organ = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, procedure.getId());
            stmt.setString(2, organ.getName());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
