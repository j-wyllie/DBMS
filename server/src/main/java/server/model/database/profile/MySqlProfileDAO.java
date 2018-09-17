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
import java.util.Arrays;
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

/**
 * Handles all of the Profile queries.
 */
@Slf4j
public class MySqlProfileDAO implements ProfileDAO {

    private String insertQuery = "insert into profiles (NHI, Username, IsDonor, IsReceiver, " +
            "GivenNames, LastNames, Dob, Dod, Gender, Height, Weight, BloodType, IsSmoker, " +
            "AlcoholConsumption, BloodPressureSystolic, BloodPressureDiastolic, Address, " +
            "StreetNo, StreetName, Neighbourhood, City, ZipCode, Region, Country, BirthCountry, " +
            "Phone, Email, Created, LastUpdated) values " +
            "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
            " ?, ?, ?, ?, ?, ?);";

    /**
     * Gets all profiles from the database.
     */
    @Override
    public List<Profile> getAll() {
        String query = "select * from profiles;";
        List<Profile> result = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
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
        Profile profile = null;
        try (Connection conn = DatabaseConnection.getConnection();
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
        Profile profile = null;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {

                try {
                    while (rs.next()) {
                        profile = parseProfile(rs);
                    }
                } catch (SQLException e) {
                    log.error(e.getMessage(), e);

                }
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
        final String createdCol = "created";
        int id = profiles.getInt("ProfileId");
        String nhi = profiles.getString("NHI");
        String username = profiles.getString("Username");
        Boolean isDonor = profiles.getBoolean("IsDonor");
        Boolean isReceiver = profiles.getBoolean("IsReceiver");
        String givenNames = profiles.getString("GivenNames");
        String lastNames = profiles.getString("LastNames");
        LocalDate dob = null;
        if (profiles.getDate("Dob") != null) {
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
        if (profiles.getTimestamp(createdCol) != null) {
            created = profiles.getTimestamp(createdCol).toLocalDateTime();
        }
        LocalDateTime updated = null;
        if (profiles.getTimestamp(createdCol) != null) {
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
        profile.addOrgansRequired(database.getRequired(profile));
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
     * Adds a new profile to the database.
     *
     * @param profile to add.
     */
    @Override
    public void add(Profile profile) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        PreparedStatement stmt = conn.prepareStatement(insertQuery);
        try {
            stmt.executeUpdate();
        } catch (SQLException e) {
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
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.executeUpdate();
        }
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
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            try {
                stmt.setString(1, username);
                try (ResultSet result = stmt.executeQuery()) {
                    if (result.last()) {
                        result.beforeFirst();
                        return result.next();
                    }
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            } finally {
                conn.close();
            }
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
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            try {

                stmt.setString(1, nhi);

                try (ResultSet result = stmt.executeQuery()) {

                    if (result.next()) {
                        id = result.getInt("ProfileId");
                        return id;
                    }
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            } finally {
                conn.close();
            }
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

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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
            }
        }
    }

    /**
     * Removes all organs from a profile.
     *
     * @param profile profile having organs removed.
     */
    private void removeOrgans(Profile profile) {
        OrganDAO database = DAOFactory.getOrganDao();

        profile.getOrgansDonating().forEach(organ -> database.removeDonating(profile, organ));
        profile.getOrgansDonated().forEach(organ -> database.removeDonation(profile, organ));
        profile.getOrgansReceived().forEach(organ -> database.removeReceived(profile, organ));
        profile.getOrgansRequired().forEach(organ -> database.removeRequired(profile, organ));
    }

    /**
     * Removes all medications from a profile.
     *
     * @param profile profile having medications removed.
     */
    private void removeMedications(Profile profile) {
        MedicationDAO database = DAOFactory.getMedicationDao();

        profile.getCurrentMedications().forEach(database::remove);
        profile.getHistoryOfMedication().forEach(database::remove);
    }

    /**
     * Removes all procedures from a profile.
     *
     * @param profile profile having procedures removed.
     */
    private void removeProcedures(Profile profile) {
        ProcedureDAO database = DAOFactory.getProcedureDao();

        profile.getPendingProcedures().forEach(database::remove);
        profile.getPreviousProcedures().forEach(database::remove);
    }

    /**
     * Removes all conditions from a profile.
     *
     * @param profile profile having conditions removed.
     */
    private void removeConditions(Profile profile) {
        ConditionDAO database = DAOFactory.getConditionDao();

        profile.getCurrentConditions().forEach(database::remove);
        profile.getCuredConditions().forEach(database::remove);
    }

    /**
     * Updates a profiles information in the database.
     *
     * @param profile to update.
     */
    @Override
    public void update(Profile profile) throws SQLException {
        String query = "update profiles set NHI = ?, Username = ?, IsDonor = ?, IsReceiver = ?, " +
                "GivenNames = ?, LastNames = ?, Dob = ?, Dod = ?, Gender = ?, Height = ?, Weight " +
                "= ?, BloodType = ?, IsSmoker = ?, AlcoholConsumption = ?, BloodPressureDiastolic" +
                " = ?, BloodPressureSystolic = ?, Address = ?, Region = ?, Phone = ?, Email = ?, " +
                "Country = ?, BirthCountry = ?, CountryOfDeath = ?, RegionOfDeath = ?, " +
                "CityOfDeath = ?, StreetNo = ?, StreetName = ?, Neighbourhood = ?, " +
                "Created = ?, LastUpdated = ?, City = ? where ProfileId = ?;";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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
            }
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
        StringBuilder query = new StringBuilder("select distinct p.* from profiles p");
        if (organs.isEmpty()) {
            query.append(" join organs o on p.ProfileId = o.ProfileId");
            int index = 0;
            for (OrganEnum organ : organs) {
                if (index > 0) {
                    query.append(" or o.Organ = '").append(organ.getNamePlain()).append("'");
                    index++;
                } else {
                    query.append(" and (o.Organ = '").append(organ.getNamePlain()).append("'");
                    index++;
                }
            }
            if (index > 0) {
                query.append(")");
            }
        }
        query.append(
                " where ((p.PreferredName is not null and CONCAT(p.GivenNames, p.PreferredName, "
                        + "p.LastNames) LIKE ?) or (CONCAT(p.GivenNames, p.LastNames) LIKE ?)) "
                        + "and p.Region like ?");
        if (!gender.equals("any")) {
            query.append(" and p.Gender = ?");
        }
        if (ageSearchInt > 0) {
            if (ageRangeSearchInt == -999) {
                query.append(" and (((floor(datediff(CURRENT_DATE, p.dob) / 365.25) = ?) and "
                        + "p.Dod IS NULL) or (floor(datediff(p.Dod, p.Dob) / 365.25) = ?))");
            } else {
                query.append(
                        " and (((floor(datediff(CURRENT_DATE, p.dob) / 365.25) >= ?) and p.Dod IS NULL) or (floor(datediff(p.Dod, p.Dob) / 365.25) >= ?))"
                                + " and (((floor(datediff(CURRENT_DATE, p.dob) / 365.25) <= ?) and p.Dod IS NULL) or (floor(datediff(p.Dod, p.Dob) / 365.25) <= ?))");
            }
        }
        if (type.equalsIgnoreCase("donor")) {
            query.append(" and p.IsDonor = ?");
        }
        if (type.equalsIgnoreCase("receiver")) {
            query.append(" and p.IsReceiver = ?");
        }

        query.append(";");

        List<Profile> result = new ArrayList<>();

        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn
                .prepareStatement(query.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY)) {

            try {
                stmt.setString(1, "%" + searchString + "%");
                stmt.setString(2, "%" + searchString + "%");
                stmt.setString(3, "%" + region + "%");

                int index = 4;
                if (!"any".equals(gender)) {
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
                if ("donor".equalsIgnoreCase(type)) {
                    stmt.setBoolean(index, true);
                    index++;
                }
                if ("receiver".equalsIgnoreCase(type)) {
                    stmt.setBoolean(index, true);
                    index++;
                }

                try (ResultSet allProfiles = stmt.executeQuery()) {
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
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                conn.close();
            }
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
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            try {

                try (ResultSet result = stmt.executeQuery(query)) {

                    if (result.next()) {
                        return result.getInt(1);
                    }
                }
                conn.close();
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            } finally {
                conn.close();
            }
        }
        return 0;
    }

    @Override
    public List<Entry<Profile, OrganEnum>> getAllReceiving() {
        String query = "select * from organs left join profiles on organs.ProfileId = " +
                "profiles.ProfileId where Required = 1;";
        return getReceivers(query);
    }

    @Override
    public List<Entry<Profile, OrganEnum>> searchReceiving(String searchString) {
        String query =
                "select * from organs left join profiles on organs.ProfileId = profiles.ProfileId "
                        +
                        "where GivenNames like '%" + searchString + "%' or LastNames like '%" +
                        searchString + "%' or Region like '%" + searchString + "%' or Organ like '%"
                        +
                        searchString + "%';";
        return getReceivers(query);
    }

    private List<Entry<Profile, OrganEnum>> getReceivers(String query) {
        List<Entry<Profile, OrganEnum>> receivers = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet result;
            try (Statement stmt = conn.createStatement()) {

                result = stmt.executeQuery(query);
            }

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
                        "Dod IS NOT NULL AND ToDonate = 1 AND Expired IS NULL;";
        List<Profile> result = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            ArrayList<Integer> existingIds = new ArrayList<>();
            try {
                try (ResultSet allProfiles = stmt.executeQuery(query)) {
                    while (allProfiles.next()) {
                        Profile newProfile = parseProfile(allProfiles);
                        if (!existingIds.contains(newProfile.getId())) {
                            result.add(newProfile);
                            existingIds.add(newProfile.getId());
                        }
                    }
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            } finally {
                conn.close();
            }
        }
        return result;
    }

    /**
     * Gets all profiles from the database where the person is dead and matches the given search
     * string.
     */
    @Override
    public List<Profile> getDeadFiltered(String searchString) throws SQLException {

        String query =
                "SELECT * FROM profiles JOIN organs on profiles.ProfileId=organs.ProfileId WHERE " +
                        "CONCAT(GivenNames, LastNames) LIKE ? AND Dod IS NOT NULL AND ToDonate = 1 AND Expired IS NULL;";
        List<Profile> result = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchString + "%");

            ArrayList<Integer> existingIds = new ArrayList<>();
            try {
                try (ResultSet allProfiles = stmt.executeQuery()) {
                    while (allProfiles.next()) {
                        Profile newProfile = parseProfile(allProfiles);
                        if (!existingIds.contains(newProfile.getId())) {
                            result.add(newProfile);
                            existingIds.add(newProfile.getId());
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                conn.close();
            }
        }
        return result;
    }

    /**
     * Get list of receivers that could be recipients of a selected organ.
     *
     * @param organs type of organ that is being donated
     * @param bloodTypes blood type recipient needs to have
     * @param lowerAgeRange lowest age the recipient can have
     * @param upperAgeRange highest age the recipient can have
     * @return list of profile objects
     */
    @Override
    public List<Profile> getOrganReceivers(String organs, String bloodTypes,
            Integer lowerAgeRange, Integer upperAgeRange) {
        List<String> blood = Arrays.asList(bloodTypes.split("\\s*,\\s*"));
        StringBuilder bloodQuery = new StringBuilder("");
        for (int i = 0; i < blood.size(); i++) {
            if (i != blood.size() - 1) {
                bloodQuery.append("?,");
            } else {
                bloodQuery.append("?");
            }
        }

        List<String> orgs = Arrays.asList(organs.split("\\s*,\\s*"));
        StringBuilder organQuery = new StringBuilder("");
        for (int i = 0; i < orgs.size(); i++) {
            if (i != orgs.size() - 1) {
                organQuery.append("?,");
            } else {
                organQuery.append("?");
            }
        }

        String query = "SELECT p.* FROM profiles p WHERE p.BloodType in (" + bloodQuery.toString()
                + ") AND "
                + "FLOOR(datediff(CURRENT_DATE, p.dob) / 365.25) BETWEEN ? AND ? "
                + "AND p.IsReceiver = 1 AND ("
                + "SELECT o.Organ FROM organs o WHERE o.ProfileId = p.ProfileId AND o.Organ in (" +
                organQuery.toString() + ") AND o.Required GROUP BY o.ProfileId) in (" + organQuery
                .toString() + ")";

        List<Profile> receivers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            int count = 1;
            for (String type : blood) {
                stmt.setString(count, type);
                count++;
            }
            stmt.setInt(count, lowerAgeRange);
            count++;
            stmt.setInt(count, upperAgeRange);
            count++;

            for (String type : orgs) {
                stmt.setString(count, type);
                count++;
            }

            for (String type : orgs) {
                stmt.setString(count, type);
                count++;
            }

            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    Profile profile = parseProfile(result);
                    receivers.add(profile);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return receivers;
    }

    @Override
    public Boolean hasPassword(String nhi) throws SQLException {
        String query = "SELECT Username FROM profiles WHERE nhi = ? AND PASSWORD != ''";
        Boolean hasPassword = false;

        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet result;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, nhi);
                result = stmt.executeQuery();
            }
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
        String query = "SELECT NHI, Password FROM profiles WHERE NHI = ?;";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            try {

                stmt.setString(1, username);
                String hashedPassword;
                try (ResultSet rs = stmt.executeQuery()) {

                    rs.next();
                    hashedPassword = rs.getString("Password");
                }
                return PasswordUtilities.check(password, hashedPassword);

            } catch (SQLException e) {
                throw new UserNotFoundException("Not found", username);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                log.error(e.getMessage(), e);
            } finally {
                conn.close();
            }
        }
        return false;
    }

    @Override
    public Boolean savePassword(String nhi, String password)
            throws SQLException, UserNotFoundException {
        String query = "UPDATE profiles SET Password = ? WHERE NHI = ?;";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            try {

                stmt.setString(1, PasswordUtilities.getSaltedHash(password));
                stmt.setString(2, nhi);
                stmt.executeUpdate();

            } catch (SQLException e) {
                throw new UserNotFoundException("Not found", nhi);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                log.error(e.getMessage(), e);
            } finally {
                conn.close();
            }
        }
        return false;
    }
}
