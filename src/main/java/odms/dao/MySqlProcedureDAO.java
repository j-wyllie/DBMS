package odms.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;
import odms.model.profile.Procedure;

public class MySqlProcedureDAO implements ProcedureDAO {

    /**
     * Get all procedures for the profile.
     *
     * @param profile to get the conditions for.
     * @param pending procedures or false for past procedures.
     */
    @Override
    public List<Procedure> getAll(Profile profile, Boolean pending) {
        String query = "select * from procedures where ProfileId = ? and Pending = ?;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        List<Procedure> result = new ArrayList<>();

        try {
            Connection conn = connectionInstance.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setBoolean(2, pending);

            ResultSet allProcedures = stmt.executeQuery();

            while (allProcedures.next()) {
                Procedure procedure = parseProcedure(allProcedures);
                result.add(procedure);
            }
            conn.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Parses the rows returned by the database into Procedure objects.
     *
     * @param procedures rows returned by the database.
     * @return a procedure object.
     * @throws SQLException error.
     */
    private Procedure parseProcedure(ResultSet procedures) throws SQLException {
        int id = procedures.getInt("Id");
        String summary = procedures.getString("Summary");
        LocalDate procedureDate = procedures.getDate("ProcedureDate").toLocalDate();
        String description = procedures.getString("Description");
        List<OrganEnum> affectedOrgans = getAffectedOrgans(id);

        return new Procedure(id, summary, procedureDate, description, affectedOrgans);
    }

    /**
     * Add a new procedure to a profile.
     *
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
            //todo: return and update procedure id.
            conn.close();
            stmt.close();

            for (OrganEnum organ : procedure.getOrgansAffected()) {
                addAffectedOrgan(procedure, organ);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove a procedure from a profile.
     *
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
            stmt.close();
        } catch (SQLException e) {
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
            stmt.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update a procedure for the profile.
     *
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
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all affected organs for a procedure.
     *
     * @param procedureId the procedure id.
     * @return a list of organs.
     */
    @Override
    public List<OrganEnum> getAffectedOrgans(int procedureId) {
        String query = "select * from affected_organs where Id = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        List<OrganEnum> organs = new ArrayList<>();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, procedureId);

            ResultSet allOrgans = stmt.executeQuery();

            while (allOrgans.next()) {
                String organName = allOrgans.getString("Organ");
                OrganEnum organ = OrganEnum.valueOf(organName.toUpperCase().replace(" ", "_"));
                organs.add(organ);
            }
            conn.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return organs;
    }

    /**
     * Add an affected organ to a procedure for a profile.
     *
     * @param procedure to add the organ to.
     * @param organ to add.
     */
    @Override
    public void addAffectedOrgan(Procedure procedure, OrganEnum organ) {
        String query = "insert into affected_organs (ProcedureId, Organ) values (?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

    }
}
