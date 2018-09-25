package server.model.database.hla;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import server.model.database.DatabaseConnection;

/**
 * HLA Database Access Object Implementation.
 */
@Slf4j
public class MySqlHLADao implements HLADAO {

    private static final String SELECT = "SELECT AlphaValue, NumericValue, GroupX, GroupY ";

    /**
     * Select the Group X HLA's from a profile.
     *
     * @param profileId the profile ID to operate against.
     * @return a collection of HLA's.
     */
    @Override
    public List<HLA> getGroupX(Integer profileId) {
        String query = SELECT +
                "FROM hla_type WHERE ProfileId = ? AND GroupX = true AND GroupY = false;";

        return executeSelectQuery(query, profileId);
    }

    /**
     * Select the Group Y HLA's from a profile.
     *
     * @param profileId the profile ID to operate against.
     * @return a collection of HLA's.
     */
    @Override
    public List<HLA> getGroupY(Integer profileId) {
        String query = SELECT +
                "FROM hla_type WHERE ProfileId = ? AND GroupX = false AND GroupY = true;";

        return executeSelectQuery(query, profileId);
    }

    /**
     * Select the non-primary HLA's from a profile.
     *
     * @param profileId the profile ID to operate against.
     * @return a collection of HLA's.
     */
    @Override
    public List<HLA> getNonPrimary(Integer profileId) {
        String query = SELECT +
                "FROM hla_type WHERE ProfileId = ? AND GroupX = false AND GroupY = false;";

        return executeSelectQuery(query, profileId);
    }

    /**
     * Add a new HLA to a profile.
     *
     * @param profileId to add the HLA to.
     * @param hla to add.
     */
    @Override
    public void add(Integer profileId, HLA hla) {
        String query = "INSERT INTO hla_type " +
                "(ProfileId, AlphaValue, NumericValue, GroupX, GroupY) " +
                "VALUES (?, ?, ?, ?, ?)";

        executeAddRemoveQuery(query, profileId, hla);
    }

    /**
     * Remove a HLA from a profile.
     *
     * @param profileId of HLA to remove.
     */
    @Override
    public void remove(Integer profileId, HLA hla) {
        String query = "DELETE FROM hla_type " +
                "WHERE ProfileId = ? AND AlphaValue = ? AND NumericValue = ? " +
                "AND GroupX = ? AND GroupY = ?;";

        executeAddRemoveQuery(query, profileId, hla);
    }

    /**
     * Remove all HLAs from a profile.
     *
     * @param profileId of HLA to remove.
     */
    @Override
    public void removeAll(Integer profileId) {
        String query = "DELETE FROM hla_type " +
                "WHERE ProfileId = ?";

        executeRemoveAllQuery(query, profileId);
    }

    /**
     * Support method to execute queries where all HLA's are removed.
     *
     * @param query the SQL query.
     * @param profileId the profile ID to operate against.
     */
    private void executeRemoveAllQuery(String query, Integer profileId) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profileId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Support method to execute queries where HLA's are added or removed.
     *
     * @param query the SQL query.
     * @param profileId the profile ID to operate against.
     * @param hla the HLA being consumed.
     */
    private void executeAddRemoveQuery(String query, Integer profileId, HLA hla) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, profileId);
            stmt.setString(2, hla.getAlphaValue());
            stmt.setInt(3, hla.getNumericValue());
            stmt.setBoolean(4, hla.getGroupX());
            stmt.setBoolean(5, hla.getGroupY());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Support method to execute queries where HLA's are selected from a profile.
     *
     * @param query the SQL query.
     * @param profileId the profile ID to operate against.
     * @return a collection of HLA's.
     */
    private List<HLA> executeSelectQuery(String query, Integer profileId) {
        List<HLA> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, profileId);
            try (ResultSet queryResult = stmt.executeQuery()) {
                while (queryResult.next()) {
                    results.add(parseHLAQueryResult(queryResult));
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return results;
    }

    /**
     * Support function to convert a SQL result to an HLA object.
     *
     * @param result the SQL result.
     * @return a HLA instance.
     * @throws SQLException if a SQL error occurs.
     */
    private HLA parseHLAQueryResult(ResultSet result) throws SQLException {
        String alphaValue = result.getString("AlphaValue");
        Integer numericValue = result.getInt("NumericValue");
        boolean groupX = result.getBoolean("GroupX");
        boolean groupY = result.getBoolean("GroupY");

        return new HLA(
                alphaValue,
                numericValue,
                groupX,
                groupY
        );
    }
}
