package server.model.database.profile;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.UserNotFoundException;
import server.model.database.DAOFactory;
import server.model.database.DatabaseConnection;
import server.model.database.PasswordUtilities;
import server.model.database.condition.ConditionDAO;
import server.model.database.medication.MedicationDAO;
import server.model.database.organ.OrganDAO;
import server.model.database.procedure.ProcedureDAO;

@Slf4j
public class MySqlProfileDAO implements ProfileDAO {

    String insertQuery = "insert into profiles (NHI, Username, IsDonor, IsReceiver, GivenNames,"
            + " LastNames, Dob, Dod, Gender, Height, Weight, BloodType, IsSmoker, AlcoholConsumption,"
            + " BloodPressureSystolic, BloodPressureDiastolic, Address, StreetNo, StreetName, Neighbourhood,"
            + " City, ZipCode, Region, Country, BirthCountry, Phone, Email, Created, LastUpdated) values "
            + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    /**
     * Gets all profiles from the database.
     */
    @Override
    public List<Profile> getAll() throws SQLException {
        String query = "select * from profiles;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        List<Profile> result = new ArrayList<>();
        try (Connection conn = connectionInstance.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet allProfiles = stmt.executeQuery(query)) {

            while (allProfiles.next()) {
                Profile newProfile = parseProfile(allProfiles);
                result.add(newProfile);
            }

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Get a single profile from the database.
     *
     * @return a profile.
     */
    @Override
    public Profile get(int profileId) throws SQLException {
        String query = "select * from profiles where ProfileId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Profile profile = null;
        try (Connection conn = instance.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            try {

                stmt.setInt(1, profileId);
                try (ResultSet rs = stmt.executeQuery()) {

                    rs.next();
                    profile = parseProfile(rs);
                }

            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }

        return profile;
    }

    /**
     * Get a single profile from the database by username.
     *
     * @param username of the profile.
     * @return a profile.
     */
    @Override
    public Profile get(String username) throws SQLException {
        String query = "select * from profiles where Username = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Profile profile = null;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            try {
                while (rs.next()) {
                    profile = parseProfile(rs);
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);

            }
        }

        return profile;
    }

    /**
     * Parses the profile information from the rows returned from the database.
     *
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
        if ((profiles.getDate("Dob") != null)) {
            dob = profiles.getDate("Dob").toLocalDate();
        }

        LocalDateTime dod = null;
        Timestamp timestamp = profiles.getTimestamp("Dod");
        if (timestamp != null) {
            dod = profiles.getTimestamp("Dod").toLocalDateTime();
        }
        String gender = profiles.getString("Gender");
        Double height = profiles.getDouble("Height");
        Double weight = profiles.getDouble("Weight");
        String bloodType = profiles.getString("BloodType");
        Boolean isSmoker = profiles.getBoolean("IsSmoker");
        String alcoholConsumption = profiles.getString("AlcoholConsumption");
        int bpSystolic = profiles.getInt("BloodPressureSystolic");
        int bpDiastolic = profiles.getInt("BloodPressureDiastolic");
        String address = profiles.getString("Address");
        String region = profiles.getString("Region");
        String country = profiles.getString("Country");

        String phone = profiles.getString("Phone");
        String email = profiles.getString("Email");
        String preferredName = profiles.getString("PreferredName");
        String preferredGender = profiles.getString("PreferredGender");
        String imageName = profiles.getString("ImageName");

        String city = profiles.getString("City");
        String countryOfDeath = profiles.getString("CountryOfDeath");
        String regionOfDeath = profiles.getString("RegionOfDeath");
        String cityOfDeath = profiles.getString("CityOfDeath");

        LocalDateTime created = null;
        if (profiles.getTimestamp("Created") != null) {
            created = profiles.getTimestamp("Created").toLocalDateTime();
        }
        LocalDateTime updated = null;
        if (profiles.getTimestamp("Created") != null) {
            updated = profiles.getTimestamp("LastUpdated").toLocalDateTime();
        }
        Profile profile = new Profile(id, nhi, username, isDonor, isReceiver, givenNames, lastNames,
                dob, dod, gender, height, weight, bloodType, isSmoker, alcoholConsumption,
                bpSystolic, bpDiastolic, address, region, phone, email, country, city,
                countryOfDeath, regionOfDeath, cityOfDeath, created, updated,
                preferredName, preferredGender, imageName);

        try {
            profile = setOrgans(profile);
            profile = setMedications(profile);
            profile = setProcedures(profile);
            profile = setConditions(profile);
        } catch (OrganConflictException e) {
            log.error(e.getMessage(), e);
        }

        return profile;
    }

    private Profile setOrgans(Profile profile) throws OrganConflictException {
        OrganDAO database = DAOFactory.getOrganDao();

        profile.addOrgansDonating(database.getDonating(profile.getId()));
        profile.addOrgansDonated(database.getDonations(profile.getId()));
        profile.addOrgansRequired((HashSet<OrganEnum>) database.getRequired(profile));
        profile.addOrgansReceived(database.getReceived(profile.getId()));

        return profile;
    }

    private Profile setMedications(Profile profile) {
        MedicationDAO database = DAOFactory.getMedicationDao();

        profile.setCurrentMedications(database.getAll(profile.getId(), true));
        profile.setHistoryOfMedication(database.getAll(profile.getId(), false));

        return profile;
    }

    private Profile setProcedures(Profile profile) {
        ProcedureDAO database = DAOFactory.getProcedureDao();

        profile.setPendingProcedures(database.getAll(profile.getId(), true));
        profile.setPreviousProcedures(database.getAll(profile.getId(), false));

        return profile;
    }

    private Profile setConditions(Profile profile) {
        ConditionDAO database = DAOFactory.getConditionDao();

        profile.setConditions(database.getAll(profile.getId(), false));
        return profile;
    }

    /**
     * Prepares a statement for adding a profile to the database.
     *
     * @param profile profile to be added.
     * @param stmt statement with the connection to be handled.
     * @return The prepared statement.
     * @throws SQLException thrown if the statement can't be populated.
     */
    private PreparedStatement prepareStatement(Profile profile, PreparedStatement stmt)
            throws SQLException {
        stmt.setString(1, profile.getNhi());
        stmt.setString(2, profile.getNhi());
        stmt.setBoolean(3, profile.getDonor());
        stmt.setBoolean(4, profile.isReceiver());
        stmt.setString(5, profile.getGivenNames());
        stmt.setString(6, profile.getLastNames());
        stmt.setString(7, profile.getDateOfBirth().toString());
        if (profile.getDateOfDeath() == null) {
            stmt.setString(8, null);
        } else {
            stmt.setString(8, profile.getDateOfDeath().toString());
        }
        stmt.setString(9, profile.getGender());
        stmt.setDouble(10, profile.getHeight());
        stmt.setDouble(11, profile.getWeight());
        stmt.setString(12, profile.getBloodType());
        if (profile.getIsSmoker() == null) {
            stmt.setBoolean(13, false);
        } else {
            stmt.setBoolean(13, profile.getIsSmoker());
        }
        stmt.setString(14, profile.getAlcoholConsumption());
        try {
            stmt.setInt(15, profile.getBloodPressureSystolic());
        } catch (NullPointerException e) {
            stmt.setNull(15, Types.INTEGER);
        }
        try {
            stmt.setInt(16, profile.getBloodPressureDiastolic());
        } catch (NullPointerException e) {
            stmt.setNull(16, Types.INTEGER);
        }
        if (profile.getAddress() != null) {
            stmt.setString(17, profile.getAddress());
        } else {
            stmt.setNull(17, Types.VARCHAR);
        }
        if (profile.getStreetNumber() != null) {
            stmt.setString(18, (profile.getStreetNumber()));
        } else {
            stmt.setNull(18, Types.VARCHAR);
        }
        if (profile.getStreetName() != null) {
            stmt.setString(19, profile.getStreetName());
        } else {
            stmt.setNull(19, Types.VARCHAR);
        }
        if (profile.getNeighbourhood() != null) {
            stmt.setString(20, profile.getNeighbourhood());
        } else {
            stmt.setNull(20, Types.VARCHAR);
        }
        if (profile.getCity() != null) {
            stmt.setString(21, profile.getCity());
        } else {
            stmt.setNull(21, Types.VARCHAR);
        }
        if (profile.getZipCode() != null) {
            stmt.setInt(22, Integer.valueOf(profile.getZipCode()));
        } else {
            stmt.setNull(22, Types.INTEGER);
        }
        if (profile.getRegion() != null) {
            stmt.setString(23, profile.getRegion());
        } else {
            stmt.setNull(23, Types.VARCHAR);
        }
        if (profile.getCountry() != null) {
            stmt.setString(24, profile.getCountry());
        } else {
            stmt.setNull(24, Types.VARCHAR);
        }
        if (profile.getBirthCountry() != null) {
            stmt.setString(25, profile.getBirthCountry());
        } else {
            stmt.setNull(25, Types.VARCHAR);
        }
        if (profile.getPhone() != null) {
            stmt.setString(26, profile.getPhone());
        } else {
            stmt.setNull(26, Types.VARCHAR);
        }
        if (profile.getEmail() != null) {
            stmt.setString(27, profile.getEmail());
        } else {
            stmt.setNull(27, Types.VARCHAR);
        }

        stmt.setString(28, LocalDateTime.now().toString());
        stmt.setString(29, LocalDateTime.now().toString());

        return stmt;
    }

    /**
     * Adds a new profile to the database.
     *
     * @param profile to add.
     */
    @Override
    public void add(Profile profile) throws SQLException {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(insertQuery);
        try {
            stmt = prepareStatement(profile, stmt);
            stmt.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            stmt.close();
            conn.close();
        }
    }

    /**
     * Adds a profile to a transaction.
     *
     * @param conn Connection to add to.
     * @param profile profile to add.
     * @throws SQLException thrown if you can't add the profile.
     */
    public void addToTransaction(Connection conn, Profile profile) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(insertQuery);
        stmt = prepareStatement(profile, stmt);
        stmt.executeUpdate();
    }

    /**
     * Gets a connection instance.
     *
     * @return The connection.
     * @throws SQLException Thrown when connection can't be made.
     */
    public Connection getConnection() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * Commits a transaction and closes the connection.
     *
     * @param conn Connection to close with the transaction.
     * @throws SQLException Thrown when transaction can't be committed.
     */
    public void commitTransaction(Connection conn) throws SQLException {
        conn.commit();
        conn.close();
    }

    /**
     * Rolls back a transaction and closes the connection.
     *
     * @param conn connection with transaction to rollback.
     */
    public void rollbackTransaction(Connection conn) {
        try {
            conn.rollback();
            conn.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username to check.
     * @return true is the username does not already exist.
     */
    @Override
    public boolean isUniqueUsername(String username) throws SQLException {
        String query = "select Username from profiles where Username = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {
            stmt.setString(1, username);
            ResultSet result = stmt.executeQuery();
            if (result.last()) {
                result.beforeFirst();
                return (result.next());
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            conn.close();
            stmt.close();
        }
        return false;
    }


    /**
     * Checks if a nhi already exists in the database.
     *
     * @param nhi to check.
     * @return true is the nhi does not already exist.
     */
    @Override
    public int isUniqueNHI(String nhi) throws SQLException {
        int id = 0;
        String query = "select * from profiles where NHI = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setString(1, nhi);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                id = result.getInt("ProfileId");
                return id;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            conn.close();
            stmt.close();
        }
        return id;
    }

    /**
     * Removes a profile from the database.
     *
     * @param profile to remove.
     */
    @Override
    public void remove(Profile profile) throws SQLException {
        String query = "delete from profiles where ProfileId = ?;";

        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setInt(1, profile.getId());

            removeOrgans(profile);
            removeMedications(profile);
            removeProcedures(profile);
            removeConditions(profile);

            stmt.executeUpdate();

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            conn.close();
            stmt.close();
        }
    }

    private void removeOrgans(Profile profile) {
        OrganDAO database = DAOFactory.getOrganDao();

        profile.getOrgansDonating().forEach(organ -> database.removeDonating(profile, organ));
        profile.getOrgansDonated().forEach(organ -> database.removeDonation(profile, organ));
        profile.getOrgansReceived().forEach(organ -> database.removeReceived(profile, organ));
        profile.getOrgansRequired().forEach(organ -> database.removeRequired(profile, organ));
    }

    private void removeMedications(Profile profile) {
        MedicationDAO database = DAOFactory.getMedicationDao();

        profile.getCurrentMedications().forEach(medication -> database.remove(medication));
        profile.getHistoryOfMedication().forEach(medication -> database.remove(medication));
    }

    private void removeProcedures(Profile profile) {
        ProcedureDAO database = DAOFactory.getProcedureDao();

        profile.getPendingProcedures().forEach(procedure -> database.remove(procedure));
        profile.getPreviousProcedures().forEach(procedure -> database.remove(procedure));
    }

    private void removeConditions(Profile profile) {
        ConditionDAO database = DAOFactory.getConditionDao();

        profile.getCurrentConditions().forEach(condition -> database.remove(condition));
        profile.getCuredConditions().forEach(condition -> database.remove(condition));
    }

    /**
     * Updates a profiles information in the database.
     *
     * @param profile to update.
     */
    @Override
    public void update(Profile profile) throws SQLException {
        String query = "update profiles set NHI = ?, Username = ?, IsDonor = ?, IsReceiver = ?, "
                + "GivenNames = ?, LastNames = ?, Dob = ?, Dod = ?, Gender = ?, Height = ?, Weight = ?,"
                + "BloodType = ?, IsSmoker = ?, AlcoholConsumption = ?, BloodPressureDiastolic = ?, "
                + "BloodPressureSystolic = ?, Address = ?, Region = ?, Phone = ?, Email = ?, "
                + "Country = ?, BirthCountry = ?, CountryOfDeath = ?, RegionOfDeath = ?, CityOfDeath = ?, "
                + "StreetNo = ?, StreetName = ?, Neighbourhood = ?, "
                + "Created = ?, LastUpdated = ?, City = ? where ProfileId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = DatabaseConnection.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setString(1, profile.getNhi());
            stmt.setString(2, profile.getNhi());
            stmt.setBoolean(3, profile.getDonor());
            stmt.setBoolean(4, profile.isReceiver());
            stmt.setString(5, profile.getGivenNames());
            stmt.setString(6, profile.getLastNames());
            stmt.setDate(7, Date.valueOf(profile.getDateOfBirth()));
            if (profile.getDateOfDeath() != null) {
                stmt.setTimestamp(8, Timestamp.valueOf(profile.getDateOfDeath()));
            } else {
                stmt.setDate(8, null);
            }
            stmt.setString(9, profile.getGender());
            stmt.setDouble(10, profile.getHeight());
            stmt.setDouble(11, profile.getWeight());
            stmt.setString(12, profile.getBloodType());
            stmt.setBoolean(13, profile.getIsSmoker());
            stmt.setString(14, profile.getAlcoholConsumption());
            stmt.setInt(15, profile.getBloodPressureDiastolic());
            stmt.setInt(16, profile.getBloodPressureSystolic());
            stmt.setString(17, profile.getAddress());
            stmt.setString(18, profile.getRegion());
            stmt.setString(19, profile.getPhone());
            stmt.setString(20, profile.getEmail());
            stmt.setString(21, profile.getCountry());
            stmt.setString(22, profile.getBirthCountry());
            stmt.setString(23, profile.getCountryOfDeath());
            stmt.setString(24, profile.getRegionOfDeath());
            stmt.setString(25, profile.getCityOfDeath());
            stmt.setString(26, profile.getStreetNumber());
            stmt.setString(27, profile.getStreetName());
            stmt.setString(28, profile.getNeighbourhood());
            stmt.setTimestamp(29, Timestamp.valueOf(profile.getTimeOfCreation()));
            stmt.setTimestamp(30, Timestamp.valueOf(profile.getLastUpdated()));
            stmt.setString(31, profile.getCity());
            stmt.setInt(32, profile.getId());
            stmt.executeUpdate();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            conn.close();
            stmt.close();
        }
    }

    /**
     * Searches for a sublist of profiles based on criteria.
     *
     * @param searchString filter based on search field.
     * @param ageSearchInt filter based on age.
     * @param ageRangeSearchInt filter based on age range.
     * @param region filter based on region.
     * @param gender filter based on gender.
     * @param type filter based on profile type.
     * @param organs filter based on organs selected.
     * @return a sublist of profiles.
     */
    @Override
    public List<Profile> search(String searchString, int ageSearchInt, int ageRangeSearchInt,
            String region, String gender, String type, Set<OrganEnum> organs) throws SQLException {
        String query = "select distinct p.* from profiles p";
        if (organs.size() > 0) {
            query += " join organs o on p.ProfileId = o.ProfileId";
            int index = 0;
            for (OrganEnum organ : organs) {
                if (index > 0) {
                    query += " or o.Organ = '" + organ.getNamePlain() + "'";
                    index++;
                } else {
                    query += " and (o.Organ = '" + organ.getNamePlain() + "'";
                    index++;
                }
            }
            if (index > 0) {
                query += ")";
            }
        }
        query +=
                " where ((p.PreferredName is not null and CONCAT(p.GivenNames, p.PreferredName, p.LastNames) LIKE ?) or "
                        + "(CONCAT(p.GivenNames, p.LastNames) LIKE ?)) and p.Region like ?";
        if (!gender.equals("any")) {
            query += " and p.Gender = ?";
        }
        if (ageSearchInt > 0) {
            if (ageRangeSearchInt == -999) {
                query += " and (((floor(datediff(CURRENT_DATE, p.dob) / 365.25) = ?) and p.Dod IS NULL) or (floor(datediff(p.Dod, p.Dob) / 365.25) = ?))";
            } else {
                query +=
                        " and (((floor(datediff(CURRENT_DATE, p.dob) / 365.25) >= ?) and p.Dod IS NULL) or (floor(datediff(p.Dod, p.Dob) / 365.25) >= ?))"
                                + " and (((floor(datediff(CURRENT_DATE, p.dob) / 365.25) <= ?) and p.Dod IS NULL) or (floor(datediff(p.Dod, p.Dob) / 365.25) <= ?))";
            }
        }
        if (type.equalsIgnoreCase("donor")) {
            query += " and p.IsDonor = ?";
        }
        if (type.equalsIgnoreCase("receiver")) {
            query += " and p.IsReceiver = ?";
        }

        query += ";";

        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        List<Profile> result = new ArrayList<>();

        Connection conn = DatabaseConnection.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        try {
            stmt.setString(1, "%" + searchString + "%");
            stmt.setString(2, "%" + searchString + "%");
            stmt.setString(3, "%" + region + "%");

            int index = 4;
            if (!gender.equals("any")) {
                stmt.setString(index, gender);
                index++;
            }

            if (ageSearchInt > 0) {
                if (ageRangeSearchInt == -999) {
                    stmt.setInt(index, ageSearchInt);
                    index++;
                    stmt.setInt(index, ageSearchInt);
                    index++;
                } else {
                    stmt.setInt(index, ageSearchInt);
                    index++;
                    stmt.setInt(index, ageSearchInt);
                    index++;
                    stmt.setInt(index, ageRangeSearchInt);
                    index++;
                    stmt.setInt(index, ageRangeSearchInt);
                    index++;
                }
            }
            if (type.equalsIgnoreCase("donor")) {
                stmt.setBoolean(index, true);
                index++;
            }
            if (type.equalsIgnoreCase("receiver")) {
                stmt.setBoolean(index, true);
                index++;
            }

            ResultSet allProfiles = stmt.executeQuery();
            int size;
            allProfiles.last();
            size = allProfiles.getRow();
            allProfiles.beforeFirst();

            if (size > 250) {
                for (int i = 0; i < 250; i++) {
                    allProfiles.next();
                    Profile newProfile = parseProfile(allProfiles);
                    result.add(newProfile);
                }
            } else {
                while (allProfiles.next()) {
                    Profile newProfile = parseProfile(allProfiles);
                    result.add(newProfile);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            conn.close();
            stmt.close();

        }
        return result;
    }

    /**
     * Gets the number of profiles in the database.
     *
     * @return the number of profiles.
     */
    @Override
    public Integer size() throws SQLException {
        String query = "select count(*) from profiles;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        try {

            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                return result.getInt(1);
            }
            conn.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            conn.close();
            stmt.close();
        }
        return 0;
    }

    @Override
    public List<Entry<Profile, OrganEnum>> getAllReceiving() {
        String query = "select * from organs left join profiles on organs.ProfileId = profiles.ProfileId where Required = 1;";
        return getReceivers(query);
    }

    @Override
    public List<Entry<Profile, OrganEnum>> searchReceiving(String searchString) {
        String query =
                "select * from organs left join profiles on organs.ProfileId = profiles.ProfileId "
                        + "where GivenNames like '%" + searchString + "%' or LastNames like '%"
                        + searchString
                        + "%' or Region like '%" + searchString + "%' or Organ like '%"
                        + searchString + "%';";
        return getReceivers(query);
    }

    private List<Entry<Profile, OrganEnum>> getReceivers(String query) {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        List<Entry<Profile, OrganEnum>> receivers = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                Profile profile = parseProfile(result);
                OrganEnum organ = OrganEnum
                        .valueOf(result.getString("Organ").toUpperCase().replace(" ", "_"));
                receivers.add(new SimpleEntry<>(profile, organ));
            }
            conn.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return receivers;
    }

    /**
     * Gets all profiles from the database where the person is dead.
     */
    @Override
    public List<Profile> getDead() throws SQLException {
        String query =
                "SELECT DISTINCT * FROM `profiles` JOIN organs on profiles.ProfileId=organs.ProfileId WHERE "
                        +
                        "Dod IS NOT NULL AND ToDonate = 1 AND Expired IS NULL";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        List<Profile> result = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ArrayList<Integer> existingIds = new ArrayList<>();
        try {
            ResultSet allProfiles = stmt.executeQuery(query);
            while (allProfiles.next()) {
                Profile newProfile = parseProfile(allProfiles);
                if (!existingIds.contains(newProfile.getId())) {
                    result.add(newProfile);
                    existingIds.add(newProfile.getId());
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            conn.close();
            stmt.close();
        }
        return result;
    }

    /**
     * Get list of receivers that could be recipients of a selected organ.
     *
     * @param organ type of organ that is being donated
     * @param bloodType blood type recipient needs to have
     * @param lowerAgeRange lowest age the recipient can have
     * @param upperAgeRange highest age the recipient can have
     * @return list of profile objects
     */
    @Override
    public List<Profile> getOrganReceivers(String organ, String bloodType,
            Integer lowerAgeRange, Integer upperAgeRange) {
        organ = organ.replace("-", " ");
        String query = "SELECT p.* FROM profiles p WHERE p.BloodType = ? AND "
                + "FLOOR(datediff(CURRENT_DATE, p.dob) / 365.25) BETWEEN ? AND ? "
                + "AND p.IsReceiver = 1 AND ("
                + "SELECT o.Organ FROM organs o WHERE o.ProfileId = p.ProfileId AND o.Organ = ? AND "
                + "o.Required) = ?;";

        DatabaseConnection instance = DatabaseConnection.getInstance();
        List<Profile> receivers = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, bloodType);
            stmt.setInt(2, lowerAgeRange);
            stmt.setInt(3, upperAgeRange);
            stmt.setString(4, organ);
            stmt.setString(5, organ);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                Profile profile = parseProfile(result);
                receivers.add(profile);
            }
            conn.close();
            stmt.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return receivers;
    }

    @Override
    public Boolean hasPassword(String nhi) throws SQLException {
        String query = "SELECT Username FROM profiles WHERE nhi = ? AND PASSWORD != ''";
        Boolean hasPassword = false;
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, nhi);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                hasPassword = true;
            }
            conn.close();
        } catch (SQLException e) {
            throw new SQLException();
        }

        return hasPassword;
    }

    @Override
    public Boolean checkCredentials(String username, String password)
            throws SQLException, UserNotFoundException {
        if (password.equals("")) {
            return false;
        }
        String query = "SELECT NHI, Password FROM profiles WHERE NHI = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            rs.next();
            String hashedPassword = rs.getString("Password");
            return PasswordUtilities.check(password, hashedPassword);

        } catch (SQLException e) {
            throw new UserNotFoundException("Not found", username);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        } finally {
            conn.close();
            stmt.close();
        }
        return false;
    }

    @Override
    public Boolean savePassword(String username, String password)
            throws SQLException, UserNotFoundException {
        String query = "UPDATE profiles SET Password = ? WHERE Username = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setString(1, PasswordUtilities.getSaltedHash(password));
            stmt.setString(2, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new UserNotFoundException("Not found", username);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        } finally {
            conn.close();
            stmt.close();
        }
        return false;
    }
}
