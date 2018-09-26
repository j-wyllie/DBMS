package server.model.database.procedure;

import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Procedure;
import server.model.database.DAOFactory;
import server.model.database.DatabaseConnection;
import server.model.database.locations.HospitalDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for procedures.
 */
@Slf4j
public class MySqlProcedureDAO implements ProcedureDAO {

    /**
     * Get all procedures for the profile.
     *
     * @param profile to get the conditions for.
     * @param pending procedures or false for past procedures.
     */
    @Override
    public List<Procedure> getAll(int profile, Boolean pending) {
        String query = "SELECT Id, ProfileId, Summary, Description, ProcedureDate, Pending, " +
                "Previous, Hospital FROM procedures WHERE ProfileId = ? AND Pending = ?;";
        List<Procedure> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profile);
            stmt.setBoolean(2, pending);

            try (ResultSet allProcedures = stmt.executeQuery()) {

                while (allProcedures.next()) {
                    Procedure procedure = parseProcedure(allProcedures);
                    result.add(procedure);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Add a new procedure to a profile.
     *
     * @param profile to add the procedure to.
     * @param procedure to add.
     */
    @Override
    public void add(int profile, Procedure procedure) {
        String query =
                "insert into procedures (ProfileId, Summary, Description, ProcedureDate, " +
                        "Pending, Hospital) values (?, ?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, profile);
            stmt.setString(2, procedure.getSummary());
            stmt.setString(3, procedure.getLongDescription());

            if (procedure.getDateTime() != null) {
                // Date time is only used in scheduling a procedure so it must be pending
                stmt.setTimestamp(4, Timestamp.valueOf(procedure.getDateTime()));
                stmt.setInt(5, 1);
            } else {
                stmt.setDate(4, Date.valueOf(procedure.getDate()));
                if (LocalDate.now().isBefore(procedure.getDate())) {
                    stmt.setInt(5, 1);
                } else {
                    stmt.setInt(5, 0);
                }
            }

            if (procedure.getHospital() != null) {
                if (procedure.getHospital().getId() != null) {
                    stmt.setInt(6, procedure.getHospital().getId());
                } else {
                    stmt.setNull(6, Types.INTEGER);
                }
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                while (keys.next()) {
                    procedure.setId(keys.getInt(1));
                }
            }
            for (OrganEnum organ : procedure.getOrgansAffected()) {
                addAffectedOrgan(procedure, organ);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Remove a procedure FROM a profile.
     *
     * @param procedure to remove.
     */
    @Override
    public void remove(Procedure procedure) {
        String idQuery = "DELETE FROM procedures WHERE Id = ?;";
        String procedureIdQuery = "DELETE FROM affected_organs WHERE ProcedureId = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement procStmt = conn.prepareStatement(procedureIdQuery)) {
                procStmt.setInt(1, procedure.getId());
                procStmt.executeUpdate();
            }

            try (PreparedStatement idStmt = conn.prepareStatement(idQuery)) {
                idStmt.setInt(1, procedure.getId());
                idStmt.executeUpdate();
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Remove an affected organ FROM a procedure for a profile.
     *
     * @param procedure to remove the organ FROM.
     * @param organ to remove.
     */
    @Override
    public void removeAffectedOrgan(Procedure procedure, OrganEnum organ) {
        String query = "DELETE FROM affected_organs WHERE ProcedureId = ? AND Organ = ?;";
        queryEffectedOrgan(query, procedure, organ);
    }

    /**
     * Update a procedure for the profile.
     *
     * @param procedure to update.
     */
    @Override
    public void update(Procedure procedure, boolean pending) {
        String query =
                "UPDATE procedures SET Summary = ?, Description = ?, ProcedureDate = ?, " +
                        "Pending = ?, Hospital = ? WHERE Id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, procedure.getSummary());
            stmt.setString(2, procedure.getLongDescription());
            stmt.setDate(3, Date.valueOf(procedure.getDate()));
            stmt.setBoolean(4, pending);
            if (procedure.getHospital() == null || procedure.getHospital().getId() == -1) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, procedure.getHospital().getId());
            }
            stmt.setInt(6, procedure.getId());

            stmt.executeUpdate();

            /* The user could have completely changed the organs for the procedure so
            we need to remove them all and add the current organs. */
            List<OrganEnum> currentOrgans = getAffectedOrgans(procedure.getId());
            for (OrganEnum organ : currentOrgans) {
                removeAffectedOrgan(procedure, organ);
            }

            for (OrganEnum organ : procedure.getOrgansAffected()) {
                addAffectedOrgan(procedure, organ);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
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
        String query = "SELECT * FROM affected_organs WHERE ProcedureId = ?;";
        List<OrganEnum> organs = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, procedureId);

            try (ResultSet allOrgans = stmt.executeQuery()) {
                while (allOrgans.next()) {
                    String organName = allOrgans.getString("Organ");
                    OrganEnum organ = OrganEnum.valueOf(organName.toUpperCase().replace("-", "_"));
                    organs.add(organ);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
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
        String query = "INSERT INTO affected_organs (ProcedureId, Organ) values (?, ?);";
        queryEffectedOrgan(query, procedure, organ);
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
        Integer hospitalId = procedures.getInt("Hospital");
        Procedure procedure = new Procedure(
            id, summary, procedureDate, description, affectedOrgans);

        if (hospitalId != null) {
            HospitalDAO hospitalDAO = DAOFactory.getHospitalDAO();
            procedure.setHospital(hospitalDAO.get(hospitalId));
        }
        return procedure;
    }

    /**
     * Support function to encapsulate queries for adding or removing an effected organ.
     *
     * @param query the SQL query.
     * @param procedure the procedure.
     * @param organ the effected organ.
     */
    private void queryEffectedOrgan(String query, Procedure procedure, OrganEnum organ) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, procedure.getId());
            stmt.setString(2, organ.getName());

            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }
}
