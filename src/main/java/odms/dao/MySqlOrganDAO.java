package odms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.Set;
import odms.enums.OrganEnum;
import odms.profile.Profile;

public class MySqlOrganDAO implements OrganDAO {

    /**
     * Gets all organs that a profile has donated in the past.
     * @param profile to get the organs for.
     */
    @Override
    public Set<OrganEnum> getDonations(Profile profile) {
        return getOrgans( profile, "select Organ from organs where ProfileId = ? and Donated = ?");

    }

    /**
     * Gets all organs that a profile has registered to donate.
     * @param profile to get the organs for.
     */
    @Override
    public Set<OrganEnum> getDonating(Profile profile) {
        return getOrgans(profile, "select Organ from organs where ProfileId = ? and Donating = ?");
    }

    /**
     * Runs the given query with the first parameter set to the profile and the second set to true
     * @param profile
     * @param query
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
                OrganEnum organ = OrganEnum.valueOf(organName.toUpperCase());
                allOrgans.add(organ);
            }
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return allOrgans;
    }

    /**
     * Gets all organs that a profile requires.
     * @param profile to get the organs for.
     */
    @Override
    public Set<OrganEnum> getRequired(Profile profile) {
        return getOrgans(profile, "select Organ from organs where ProfileId = ? and Required = ?");
    }

    /**
     * Gets all organs that a profile has received in the past.
     * @param profile to get the organs for.
     */
    @Override
    public Set<OrganEnum> getReceived(Profile profile) {
        return getOrgans(profile, "select Organ from organs where ProfileId = ? and Received = ?");
    }

    /**
     * Adds an organ to a profiles past donations.
     * @param profile to add the past donation to.
     * @param organ donated.
     */
    @Override
    public void addDonation(Profile profile, OrganEnum organ) {
        String query = "insert into organs (ProfileId, Organ, Donated, Donating, Required, Received) "
                + "values (?, ?, ?, ?, ?, ?);";
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

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an organ to a profiles organs to donate.
     * @param profile to donate.
     * @param organ to donate.
     */
    @Override
    public void addDonating(Profile profile, OrganEnum organ) {
        String query = "insert into organs (ProfileId, Organ, Donated, Donating, Required, Received) "
                + "values (?, ?, ?, ?, ?, ?);";
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

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a organ to a profiles required organs.
     * @param profile requiring the organ.
     * @param organ required.
     */
    @Override
    public void addRequired(Profile profile, OrganEnum organ) {
        String query = "insert into organs (ProfileId, Organ, Donated, Donating, Required, Received) "
                + "values (?, ?, ?, ?, ?, ?);";
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

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addReceived(Profile profile, OrganEnum organ) {
        String query = "insert into organs (ProfileId, Organ, Donated, Donating, Required, Received) "
                + "values (?, ?, ?, ?, ?, ?);";
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

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes an organ from a profiles past donations.
     * @param profile to remove the past donation from.
     * @param organ to remove.
     */
    @Override
    public void removeDonation(Profile profile, OrganEnum organ) {
        String query = "insert into organs (ProfileId, Organ, Donated, Donating, Required, Received) "
                + "values (?, ?, ?, ?, ?, ?);";
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

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes an organ from a profiles organs to donate.
     * @param profile to remove the organ from.
     * @param organ to remove.
     */
    @Override
    public void removeDonating(Profile profile, OrganEnum organ) {
        String query = "delete from organs where ProfileId = ? and Organ = ? and Donating = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, organ.getNamePlain());
            stmt.setBoolean(3, true);

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes an organ from a profiles required organs.
     * @param profile to remove the organ from.
     * @param organ to remove.
     */
    @Override
    public void removeRequired(Profile profile, OrganEnum organ) {
        String query = "delete from organs where ProfileId = ? and Organ = ? and Required = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, organ.getNamePlain());
            stmt.setBoolean(3, true);

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeReceived(Profile profile, OrganEnum organ) {
        String query = "delete from organs where ProfileId = ? and Organ = ? and Received = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, organ.getNamePlain());
            stmt.setBoolean(3, true);

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
