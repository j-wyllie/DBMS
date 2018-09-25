package server.model.database.organ;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.ExpiredOrgan;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import server.model.database.DatabaseConnection;

@Slf4j
public class MySqlOrganDAO implements OrganDAO {

    /**
     * Gets all organs that a profile has donated in the past.
     *
     * @param profile to get the organs for.
     */
    @Override
    public Set<OrganEnum> getDonations(Profile profile) {
        return getOrgans(
                profile,
                "SELECT * FROM organs WHERE ProfileId = ? AND Donated = ?"
        );
    }

    /**
     * Gets all organs that a profile has registered to donate.
     *
     * @param profile to get the organs for.
     */
    @Override
    public Set<OrganEnum> getDonating(Profile profile) {
        return getOrgans(
                profile,
                "SELECT * FROM organs WHERE ProfileId = ? AND ToDonate = ?"
        );
    }

    /**
     * Gets all organs that a profile requires.
     *
     * @param profile to get the organs for.
     */
    @Override
    public Set<OrganEnum> getRequired(Profile profile) {
        return getOrgans(
                profile,
                "SELECT * FROM organs WHERE ProfileId = ? AND Required = ?"
        );
    }

    /**
     * Gets all organs that a profile has received in the past.
     *
     * @param profile to get the organs for.
     */
    @Override
    public Set<OrganEnum> getReceived(Profile profile) {
        return getOrgans(
                profile,
                "SELECT * FROM organs WHERE ProfileId = ? AND Received = ?"
        );
    }

    /**
     * Adds an organ to a profiles past donations.
     *
     * @param profile to add the past donation to.
     * @param organ donated.
     */
    @Override
    public void addDonation(Profile profile, OrganEnum organ) {
        profile.addOrganDonated(organ);
        try {
            addOrganWithStatus(profile.getId(), organ, true, false, false, false);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Adds an organ to a profiles organs to donate.
     *
     * @param profile to donate.
     * @param organ to donate.
     * @throws OrganConflictException error.
     */
    @Override
    public void addDonating(Profile profile, OrganEnum organ) throws OrganConflictException {
        profile.addOrganDonating(organ);
        try {
            addOrganWithStatus(profile.getId(), organ, false, true, false, false);
        } catch (SQLException e) {
            throw new OrganConflictException();
        }
    }

    /**
     * Adds a organ to a profiles required organs.
     *
     * @param profile requiring the organ.
     * @param organ required.
     */
    @Override
    public void addRequired(Profile profile, OrganEnum organ) {
        profile.addOrganRequired(organ);
        try {
            addOrganWithStatus(profile.getId(), organ, false, false, true, false);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Adds a organ to a profiles received organs.
     *
     * @param profile receiving the organ.
     * @param organ received.
     */
    @Override
    public void addReceived(Profile profile, OrganEnum organ) {
        profile.addOrganReceived(organ);
        try {
            addOrganWithStatus(profile.getId(), organ, false, false, false, true);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Removes an organ FROM a profiles past donations.
     *
     * @param profile to remove the past donation FROM.
     * @param organ to remove.
     */
    @Override
    public void removeDonation(Profile profile, OrganEnum organ) {
        String query = "DELETE FROM organs WHERE ProfileId = ? AND Organ = ? AND Donated = ?;";
        profile.removeOrganDonated(organ);
        removeOrgan(profile.getId(), organ, query);
    }

    /**
     * Removes an organ FROM a profiles organs to donate.
     *
     * @param profile to remove the organ FROM.
     * @param organ to remove.
     */
    @Override
    public void removeDonating(Profile profile, OrganEnum organ) {
        String query = "DELETE FROM organs WHERE ProfileId = ? AND Organ = ? AND toDonate = ?;";
        profile.removeOrganDonating(organ);
        removeOrgan(profile.getId(), organ, query);
    }

    /**
     * Removes an organ FROM a profiles required organs.
     *
     * @param profile to remove the organ FROM.
     * @param organ to remove.
     */
    @Override
    public void removeRequired(Profile profile, OrganEnum organ) {
        String query = "DELETE FROM organs WHERE ProfileId = ? AND Organ = ? AND Required = ?;";
        profile.removeOrganRequired(organ);
        removeOrgan(profile.getId(), organ, query);
    }

    /**
     * Removes an organ FROM a profiles received organs.
     *
     * @param profile to remove the organ FROM.
     * @param organ to remove.
     */
    @Override
    public void removeReceived(Profile profile, OrganEnum organ) {
        String query = "DELETE FROM organs WHERE ProfileId = ? and Organ = ? AND Received = ?;";
        profile.removeOrganReceived(organ);
        removeOrgan(profile.getId(), organ, query);
    }

    /**
     * Removes an entry FROM the organs table in the database.
     *
     * @param profileId to remove the organ from.
     * @param organ to remove.
     * @param query to execute the removal.
     */
    private void removeOrgan(int profileId, OrganEnum organ, String query) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profileId);
            stmt.setString(2, organ.toString());
            stmt.setBoolean(3, true);

            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Updates organ to be expired.
     *
     * @param profile to update the organ.
     * @param organ to update.
     * @param expired expired boolean.
     * @param note Clinician's reason to update.
     * @param userId Clinician's user Id.
     */
    @Override
    public void setExpired(Profile profile, OrganEnum organ, Integer expired, String note,
            Integer userId) throws SQLException {
        String query = "UPDATE organs SET Expired = ?, UserId = ?, Note = ?, " +
                "ExpiryDate = CURRENT_TIMESTAMP WHERE ProfileId = ? AND Organ = ? " +
                "AND ToDonate = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, expired);
            stmt.setInt(2, userId);
            stmt.setString(3, note);
            stmt.setInt(4, profile.getId());
            stmt.setString(5, organ.toString());
            stmt.setInt(6, 1);

            stmt.executeUpdate();
        }
    }

    /**
     * Updates organ to be non-expired.
     *
     * @param profile to revert organ expired.
     * @param organ to revert.
     */
    @Override
    public void revertExpired(Integer profile, OrganEnum organ) throws SQLException {
        String query = "UPDATE organs SET Expired = 0 , UserId = NULL , Note = NULL " +
                "WHERE ProfileId = ? AND Organ = ? ;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profile);
            stmt.setString(2, organ.toString());

            stmt.executeUpdate();
        }
    }

    /**
     * Gets all organs that have expired from a profile.
     *
     * @param profile to get the organs for.
     */
    @Override
    public List<ExpiredOrgan> getExpired(Profile profile) {
        String query = "SELECT * FROM organs JOIN users ON organs.UserId = users.UserId " +
                "WHERE Expired = ? AND ProfileId = ? ;";
        List<ExpiredOrgan> allOrgans = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, 1);
            stmt.setInt(2, profile.getId());

            try (ResultSet allOrganRows = stmt.executeQuery()) {

                while (allOrganRows.next()) {
                    String organName = allOrganRows.getString("Organ");
                    OrganEnum organEnum = OrganEnum.valueOf(organName);
                    String note = allOrganRows.getString("Note");
                    String clinicianName = allOrganRows.getString("Name");
                    LocalDateTime date = allOrganRows.getTimestamp("ExpiryDate").toLocalDateTime();
                    ExpiredOrgan organ = new ExpiredOrgan(organEnum, note, clinicianName, date);

                    allOrgans.add(organ);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return allOrgans;
    }

    /**
     * Support function for adding Organs to the database with a donation status.
     *
     * @param profileId the Profile ID to operate against.
     * @param organ the organ to add.
     * @param donated if donated.
     * @param toDonate if to donate.
     * @param required if required.
     * @param received if received.
     */
    private void addOrganWithStatus(int profileId, OrganEnum organ,
            Boolean donated, Boolean toDonate, Boolean required, Boolean received)
            throws SQLException {
        String query = "INSERT INTO organs (ProfileId, Organ, Donated, toDonate, Required, " +
                "Received, DateRegistered) VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profileId);
            stmt.setString(2, organ.toString());
            stmt.setBoolean(3, donated);
            stmt.setBoolean(4, toDonate);
            stmt.setBoolean(5, required);
            stmt.setBoolean(6, received);
            stmt.setString(7, LocalDateTime.now().toString());

            stmt.executeUpdate();
        }
    }

    /**
     * Runs the given query with the first parameter set to the profile and the second set to true.
     *
     * @param profile to get organs for.
     * @param query to execute.
     * @return the list of the returned organs
     */
    private Set<OrganEnum> getOrgans(Profile profile, String query) {
        Set<OrganEnum> allOrgans = new HashSet<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, profile.getId());
            stmt.setBoolean(2, true);
            try (ResultSet allOrganRows = stmt.executeQuery()) {

                while (allOrganRows.next()) {
                    String organName = allOrganRows.getString("Organ");
                    allOrgans.add(
                            parseOrganDateRegistered(
                                    profile,
                                    allOrganRows,
                                    OrganEnum.valueOf(organName)
                            )
                    );
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return allOrgans;
    }

    /**
     * Parse the date registered key on a SQL result for an Organ.
     *
     * @param profile the parent profile.
     * @param allOrganRows the SQL results.
     * @param organ object reference.
     * @return the modified organ.
     * @throws SQLException if key not found.
     */
    private OrganEnum parseOrganDateRegistered(Profile profile, ResultSet allOrganRows, OrganEnum organ)
            throws SQLException {
        String dateRegKey = "DateRegistered";

        try {
            String str = allOrganRows.getString(dateRegKey);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            organ.setDate(LocalDateTime.parse(str, formatter), profile);
        } catch (DateTimeParseException e) {
            organ.setDate(
                    LocalDate.parse(allOrganRows.getString(dateRegKey)).atStartOfDay(),
                    profile
            );
        }
        return organ;
    }
}
