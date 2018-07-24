package odms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import odms.enums.OrganEnum;
import odms.medications.Drug;
import odms.profile.OrganConflictException;
import odms.profile.Procedure;
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

            while (allProfiles.next()) {
                Profile newProfile  = parseProfile(allProfiles);
                result.add(newProfile);
            }
            conn.close();
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
    public Profile get(int profileId) {
        String query = "select * from profiles where ProfileId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Profile profile = null;

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();

            rs.next();
            profile = parseProfile(rs);

            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return profile;
    }

    /**
     * Get a single profile from the database by username.
     * @param username of the profile.
     * @return a profile.
     */
    @Override
    public Profile get(String username) {
        String query = "select * from profiles where Username = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Profile profile = null;

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            rs.next();
            profile = parseProfile(rs);
            conn.close();
        }
        catch (Exception e) {
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
        LocalDate dob = null;
        if (!(profiles.getDate("Dob") == null)) {
            dob = profiles.getDate("Dob").toLocalDate();
        }
        LocalDate dod = null;
        if (!(profiles.getDate("Dod") == null)) {
            dob = profiles.getDate("Dod").toLocalDate();
        }
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
        LocalDateTime created = null;
        if (!(profiles.getTimestamp("Created") == null)) {
            created = profiles.getTimestamp("Created").toLocalDateTime();
        }
        LocalDateTime updated = null;
        if (!(profiles.getTimestamp("Created") == null)) {
            updated = profiles.getTimestamp("LastUpdated").toLocalDateTime();
        }
        Profile profile = new Profile(id, nhi, username, isDonor, isReceiver, givenNames, lastNames, dob, dod,
                gender, height, weight, bloodType, isSmoker, alcoholConsump, bpSystolic, bpDiastolic,
                address, region, phone, email, created, updated);

        try {
            profile = setOrgans(profile);
            profile = setMedications(profile);
            profile = setProcedures(profile);
            profile = setConditions(profile);
        } catch (OrganConflictException e) {
            e.printStackTrace();
        }

        return profile;
    }

    private Profile setOrgans(Profile profile) throws OrganConflictException {
        OrganDAO database = DAOFactory.getOrganDao();

        profile.addOrgansDonating(database.getDonating(profile));
        profile.addOrgansDonated(database.getDonations(profile));
        profile.addOrgansRequired((HashSet<OrganEnum>) database.getRequired(profile));
        profile.addOrgansReceived(database.getReceived(profile));

        return profile;
    }

    private Profile setMedications(Profile profile) {
        MedicationDAO database = DAOFactory.getMedicationDao();

        profile.setCurrentMedications((ArrayList<Drug>) database.getAll(profile, true));
        profile.setHistoryOfMedication((ArrayList<Drug>) database.getAll(profile, false));

        return profile;
    }

    private Profile setProcedures(Profile profile) {
        ProcedureDAO database = DAOFactory.getProcedureDao();

        profile.setPendingProcedures((ArrayList<Procedure>) database.getAll(profile, false));
        profile.setCurrentProcedures((ArrayList<Procedure>) database.getAll(profile, true));

        return profile;
    }

    private Profile setConditions(Profile profile) {
        ConditionDAO database = DAOFactory.getConditionDao();

        profile.setConditions(database.getAll(profile, true));
        return profile;
    }

    /**
     * Adds a new profile to the database.
     * @param profile to add.
     */
    @Override
    public void add(Profile profile) {
        String query = "insert into profiles (NHI, Username, IsDonor, IsReceiver, GivenNames,"
                + " LastNames, Dob, Dod, Gender, Height, Weight, BloodType, IsSmoker, AlcoholConsumption,"
                + " BloodPressure, Address, Region, Phone, Email, Created, LastUpdated) values (?, ?, ?,"
                + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, profile.getNhi());
            stmt.setString(2, profile.getUsername());
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
            stmt.setString(20, LocalDateTime.now().toString());
            stmt.setString(21, LocalDateTime.now().toString());

            stmt.executeUpdate();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a username already exists in the database.
     * @param username to check.
     * @return true is the username does not already exist.
     */
    @Override
    public boolean isUniqueUsername(String username) {
        String query = "select * from profiles where Username = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet result = stmt.executeQuery();

            if (result.getFetchSize() == 0) {
                return true;
            }
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
        } catch (SQLException e) {
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
                + "Region = ?, Phone = ?, Email = ?, Created = ?, LastUpdated = ? where ProfileId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, profile.getNhi());
            stmt.setString(2, profile.getUsername());
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
