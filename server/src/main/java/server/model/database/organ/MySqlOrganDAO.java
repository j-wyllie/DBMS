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
import odms.commons.model.profile.ExpiredOrgan;
import server.model.database.DatabaseConnection;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;

@Slf4j
public class MySqlOrganDAO implements OrganDAO {

    /**
     * Gets all organs that a profile has donated in the past.
     *
     * @param profile to get the organs for.
     */
    @Override
    public Set<OrganEnum> getDonations(int profile) {
        return getOrgans( new Profile(profile), "select * from organs where ProfileId = ? and Donated = ?");

    }

    /**
     * Gets all organs that a profile has registered to donate.
     *
     * @param profile to get the organs for.
     */
    @Override
    public Set<OrganEnum> getDonating(int profile) {
        return getOrgans(new Profile(profile), "select * from organs where ProfileId = ? and ToDonate = ?");
    }

    /**
     * Runs the given query with the first parameter set to the profile and the second set to true
     * @param profile to get organs for.
     * @param query to execute.
     * @return the list of the returned organs
     */
    private Set<OrganEnum> getOrgans(Profile profile, String query) {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Set<OrganEnum> allOrgans = new HashSet<>();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setBoolean(2, true);
            ResultSet allOrganRows = stmt.executeQuery();

            while (allOrganRows.next()) {
                String organName = allOrganRows.getString("Organ");
                OrganEnum organ = OrganEnum.valueOf(organName.toUpperCase().replace(" ", "_"));
                try {
                    String str = allOrganRows.getString("DateRegistered");
                    DateTimeFormatter formatter = DateTimeFormatter
                            .ofPattern("yyyy-MM-dd HH:mm:ss");
                    organ.setDate(LocalDateTime.parse(str, formatter), profile);
                } catch (DateTimeParseException e) {
                    organ.setDate(LocalDate.parse(allOrganRows.getString("DateRegistered"))
                            .atStartOfDay(), profile);
                }
                allOrgans.add(organ);
            }
            conn.close();
            stmt.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return allOrgans;
    }

    /**
     * Gets all organs that a profile requires.
     *
     * @param profile to get the organs for.
     */
    @Override
    public Set<OrganEnum> getRequired(Profile profile) {
        return getOrgans(profile, "select * from organs where ProfileId = ? and Required = ?");
    }

    /**
     * Gets all organs that a profile has received in the past.
     *
     * @param profile to get the organs for.
     */
    @Override
    public Set<OrganEnum> getReceived(int profile) {
        return getOrgans(new Profile(profile), "select * from organs where ProfileId = ? and Received = ?");
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
        String query =
                "INSERT INTO organs (ProfileId, Organ, Donated, toDonate, Required, Received, DateRegistered) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, organ.getNamePlain());
            stmt.setBoolean(3, true);
            stmt.setBoolean(4, false);
            stmt.setBoolean(5, false);
            stmt.setBoolean(6, false);
            stmt.setString(7, LocalDateTime.now().toString());

            stmt.executeUpdate();
            conn.close();
            stmt.close();
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
        String query =
                "INSERT INTO organs (ProfileId, Organ, Donated, toDonate, Required, Received, DateRegistered) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, organ.getNamePlain());
            stmt.setBoolean(3, false);
            stmt.setBoolean(4, true);
            stmt.setBoolean(5, false);
            stmt.setBoolean(6, false);
            stmt.setString(7, LocalDateTime.now().toString());

            stmt.executeUpdate();
            conn.close();
            stmt.close();
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
        String query =
                "INSERT INTO organs (ProfileId, Organ, Donated, toDonate, Required, Received, DateRegistered) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, organ.getNamePlain());
            stmt.setBoolean(3, false);
            stmt.setBoolean(4, false);
            stmt.setBoolean(5, true);
            stmt.setBoolean(6, false);
            stmt.setString(7, LocalDateTime.now().toString());

            stmt.executeUpdate();
            conn.close();
            stmt.close();
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
        String query =
                "INSERT INTO organs (ProfileId, Organ, Donated, toDonate, Required, Received, DateRegistered) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, organ.getNamePlain());
            stmt.setBoolean(3, false);
            stmt.setBoolean(4, false);
            stmt.setBoolean(5, false);
            stmt.setBoolean(6, true);
            stmt.setString(7, LocalDateTime.now().toString());

            stmt.executeUpdate();
            conn.close();
            stmt.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Removes an organ from a profiles past donations.
     *
     * @param profile to remove the past donation from.
     * @param organ to remove.
     */
    @Override
    public void removeDonation(Profile profile, OrganEnum organ) {
        String query = "delete from organs where ProfileId = ? and Organ = ? and Donated = ?;";
        profile.removeOrganDonated(organ);
        removeOrgan(profile, organ, query);
    }

    /**
     * Removes an organ from a profiles organs to donate.
     *
     * @param profile to remove the organ from.
     * @param organ to remove.
     */
    @Override
    public void removeDonating(Profile profile, OrganEnum organ) {
        String query = "delete from organs where ProfileId = ? and Organ = ? and ToDonate = ?;";
        profile.removeOrganDonating(organ);
        removeOrgan(profile, organ, query);
    }

    /**
     * Removes an organ from a profiles required organs.
     *
     * @param profile to remove the organ from.
     * @param organ to remove.
     */
    @Override
    public void removeRequired(Profile profile, OrganEnum organ) {
        String query = "delete from organs where ProfileId = ? and Organ = ? and Required = ?;";
        profile.removeOrganRequired(organ);
        removeOrgan(profile, organ, query);
    }

    /**
     * Removes an organ from a profiles received organs.
     *
     * @param profile to remove the organ from.
     * @param organ to remove.
     */
    @Override
    public void removeReceived(Profile profile, OrganEnum organ) {
        String query = "delete from organs where ProfileId = ? and Organ = ? and Received = ?;";
        profile.removeOrganReceived(organ);
        removeOrgan(profile, organ, query);
    }

    /**
     * Removes an entry from the organs table in the database.
     *
     * @param profile to remove the organ from.
     * @param organ to remove.
     * @param query to execute the removal.
     */
    private void removeOrgan(Profile profile, OrganEnum organ, String query) {
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, organ.getNamePlain());
            stmt.setBoolean(3, true);

            System.out.println(stmt);

            stmt.executeUpdate();
            conn.close();
            stmt.close();
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
    public void setExpired(Profile profile, String organ, Integer expired, String note,
            Integer userId) throws SQLException {
        String query =
                "UPDATE organs SET Expired = ?, UserId = ?, Note = ?, ExpiryDate = CURRENT_TIMESTAMP "
                        +
                        "WHERE ProfileId = ? AND Organ = ? AND ToDonate = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        Connection conn = instance.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, expired);
        stmt.setInt(2, userId);
        stmt.setString(3, note);
        stmt.setInt(4, profile.getId());
        stmt.setString(5, organ);
        stmt.setInt(6, 1);

        stmt.executeUpdate();
        conn.close();
        stmt.close();
    }

    /**
     * Updates organ to be non-expired.
     *
     * @param profile to revert organ expired.
     * @param organ to revert.
     */
    @Override
    public void revertExpired(Integer profile, String organ) throws SQLException {
        String query = "UPDATE organs SET Expired = 0 , UserId = NULL , Note = NULL WHERE ProfileId = ? AND Organ = ? ;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        Connection conn = instance.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, profile);
        stmt.setString(2, organ);

        stmt.executeUpdate();
        conn.close();
        stmt.close();
    }

    @Override
    public List<ExpiredOrgan> getExpired(Profile profile) throws SQLException {
        String query = "SELECT * FROM organs JOIN users ON organs.UserId = users.UserId WHERE Expired = ? AND ProfileId = ? ;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        List<ExpiredOrgan> allOrgans = new ArrayList<>();

        try {
            Connection conn = instance.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, 1);
            stmt.setInt(2, profile.getId());

            ResultSet allOrganRows = stmt.executeQuery();

            while (allOrganRows.next()) {
                String organName = allOrganRows.getString("Organ");
                OrganEnum organEnum = OrganEnum.valueOf(organName.toUpperCase().replace(" ", "_"));
                String note = allOrganRows.getString("Note");
                String clinicianName = allOrganRows.getString("Name");
                LocalDateTime date = allOrganRows.getTimestamp("ExpiryDate").toLocalDateTime();
                ExpiredOrgan organ = new ExpiredOrgan(organEnum, note, clinicianName, date);

                allOrgans.add(organ);
            }
            conn.close();
            stmt.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return allOrgans;

    }
}
