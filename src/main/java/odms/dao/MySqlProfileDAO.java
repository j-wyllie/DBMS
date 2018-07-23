package odms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import odms.data.ProfileDatabase;
import odms.profile.Profile;

public class MySqlProfileDAO implements ProfileDAO {

    /**
     * Gets all profiles from the database.
     */
    @Override
    public List<Profile> getAll() {
        String query = "select * from profiles;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        List<Profile> result = new ArrayList<>();

        try {
            Connection conn = connectionInstance.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet allProfiles = stmt.executeQuery(query);
            conn.close();

            while (allProfiles.next()) {
                Profile newProfile  = parseProfile(allProfiles);
                result.add(newProfile);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get a single profile from the database.
     * @return a profile.
     */
    @Override
    public Profile getProfile(int profileId) {
        String query = "select * from profiles when ProfileId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Profile profile = null;

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            conn.close();

            profile = parseProfile(rs);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return profile;
    }

    /**
     * Parses the profile information from the rows returned from the database.
     * @param profiles the rows returned from the database.
     * @return a profile.
     * @throws SQLException error.
     */
    private Profile parseProfile(ResultSet profiles) throws SQLException {
        int id = profiles.getInt("ProfileId");
        String nhi = profiles.getString("NHI");
        String username = profiles.getString("Username");
        Boolean isDonor = profiles.getBoolean("IsDonor");
        Boolean isReceiver = profiles.getBoolean("IsReceiver");
        String givenNames = profiles.getString("GivenNames");
        String lastNames = profiles.getString("LastNames");
        LocalDate dob = LocalDate.parse(profiles.getString("Dob"));
        LocalDate dod = LocalDate.parse(profiles.getString("Dod"));
        String gender = profiles.getString("Gender");
        Double height = profiles.getDouble("Height");
        Double weight = profiles.getDouble("Weight");
        String bloodType = profiles.getString("BloodType");
        Boolean isSmoker = profiles.getBoolean("IsSmoker");
        String alcoholConsump = profiles.getString("AlcoholConsumption");
        int bpSystolic = profiles.getInt("BloodPressureSystolic");
        int bpDiastolic = profiles.getInt("BloodPressureDiastolic");
        String address = profiles.getString("Address");
        String region = profiles.getString("Region");
        String phone = profiles.getString("Phone");
        String email = profiles.getString("Email");
        LocalDateTime created = LocalDateTime.parse(profiles.getString("Created"));
        LocalDateTime updated = LocalDateTime.parse(profiles.getString("LastUpdated"));

        return new Profile(id, nhi, username, isDonor, isReceiver, givenNames, lastNames, dob, dod,
                gender, height, weight, bloodType, isSmoker, alcoholConsump, bpSystolic, bpDiastolic,
                address, region, phone, email, created, updated);
    }

    /**
     * Adds a new profile to the database.
     * @param profile to add.
     */
    @Override
    public void add(Profile profile) {
        String query = "insert into profiles (ProfileId, NHI, Username, IsDonor, IsReceiver, GivenNames,"
                + " LastNames, Dob, Dod, Gender, Height, Weight, BloodType, IsSmoker, AlcoholConsumption,"
                + " BloodPressure, Address, Region, Phone, Email, Created, LastUpdated) values (?, ?, ?,"
                + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setString(2, profile.getNhi());
            //stmt.setString(3, profile.getUsername());
            stmt.setBoolean(4, profile.getDonor());
            stmt.setBoolean(5, profile.getReceiver());
            stmt.setString(6, profile.getGivenNames());
            stmt.setString(7, profile.getLastNames());
            stmt.setString(8, profile.getDateOfBirth().toString());
            stmt.setString(9, profile.getDateOfDeath().toString());
            stmt.setString(10, profile.getGender());
            stmt.setDouble(11, profile.getHeight());
            stmt.setDouble(12, profile.getWeight());
            stmt.setString(13, profile.getBloodType());
            stmt.setBoolean(14, profile.getIsSmoker());
            stmt.setString(15, profile.getAlcoholConsumption());
            stmt.setString(16, profile.getBloodPressure());
            stmt.setString(17, profile.getAddress());
            stmt.setString(18, profile.getRegion());
            stmt.setString(19, profile.getPhone());
            stmt.setString(20, profile.getEmail());
            stmt.setString(21, profile.getTimeOfCreation().toString());
            stmt.setString(22, profile.getLastUpdated().toString());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a profile from the database.
     * @param profile to remove.
     */
    @Override
    public void remove(Profile profile) {
        String query = "delete from profiles where ProfileId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a profiles information in the database.
     * @param profile to update.
     */
    @Override
    public void update(Profile profile) {
        String query = "update profiles set NHI = ?, Username = ?, IsDonor = ?, IsReceiver = ?, "
                + "GivenNames = ?, LastNames = ?, Dob = ?, Dod = ?, Gender = ?, Height = ?, Weight = ?,"
                + "BloodType = ?, IsSmoker = ?, AlcoholConsumption = ?, BloodPressure = ?, Address = ?,"
                + "Region = ?, Phone = ?, Email = ?, Created = ?, LastUpdated = ? where UserId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, profile.getNhi());
            //stmt.setString(2, profile.getUsername());
            stmt.setBoolean(3, profile.getDonor());
            stmt.setBoolean(4, profile.getReceiver());
            stmt.setString(5, profile.getGivenNames());
            stmt.setString(6, profile.getLastNames());
            stmt.setString(7, profile.getDateOfBirth().toString());
            stmt.setString(8, profile.getDateOfDeath().toString());
            stmt.setString(9, profile.getGender());
            stmt.setDouble(10, profile.getHeight());
            stmt.setDouble(11, profile.getWeight());
            stmt.setString(12, profile.getBloodType());
            stmt.setBoolean(13, profile.getIsSmoker());
            stmt.setString(14, profile.getAlcoholConsumption());
            stmt.setString(15, profile.getBloodPressure());
            stmt.setString(16, profile.getAddress());
            stmt.setString(17, profile.getRegion());
            stmt.setString(18, profile.getPhone());
            stmt.setString(19, profile.getEmail());
            stmt.setString(20, profile.getTimeOfCreation().toString());
            stmt.setString(21, profile.getLastUpdated().toString());
            stmt.setInt(22, profile.getId());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
