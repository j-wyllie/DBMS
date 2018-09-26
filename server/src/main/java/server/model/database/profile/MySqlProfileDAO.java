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

@Slf4j
public class MySqlProfileDAO implements ProfileDAO {

    private static final String INSERT_QUERY = "INSERT INTO profiles (NHI, Username, IsDonor, " +
            "IsReceiver, GivenNames, LastNames, Dob, Dod, Gender, Height, Weight, BloodType, " +
            "IsSmoker, AlcoholConsumption, BloodPressureSystolic, BloodPressureDiastolic, " +
            "Address, StreetNo, StreetName, Neighbourhood, City, ZipCode, Region, Country, " +
            "BirthCountry, Phone, Email, Created, LastUpdated) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?);";

    /**
     * Gets all profiles FROM the database.
     *
     * @return a list of all the profiles in the db.
     */
    @Override
    public List<Profile> getAll() {
        String query = "SELECT * FROM profiles;";
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
     * Get a single profile from the database by id.
     *
     * @param profileId of the profile.
     * @return a profile.
     */
    @Override
    public Profile get(int profileId) {
        String query = "SELECT * FROM profiles WHERE ProfileId = ?;";
        Profile profile = null;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profileId);

            try (ResultSet rs = stmt.executeQuery()) {

                rs.next();
                profile = parseProfile(rs);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return profile;
    }

    /**
     * Get a single profile from the database by username.
     *
     * @param username of the profile.
     * @return a profile.
     * @throws SQLException thrown on invalid sql.
     */
    @Override
    public Profile get(String username) throws SQLException {
        String query = "SELECT * FROM profiles WHERE Username = ?;";
        Profile profile = null;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
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
     * Adds a new profile to the database.
     *
     * @param profile to add.
     */
    @Override
    public void add(Profile profile) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY);
                PreparedStatement addStmt = prepareStatement(profile, stmt)) {
            addStmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username to check.
     * @return true is the username does not already exist.
     */
    @Override
    public boolean isUniqueUsername(String username) {
        String query = "SELECT Username FROM profiles WHERE Username = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.last()) {
                    result.beforeFirst();
                    return (result.next());
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * checks that an nhi is unique in the database.
     *
     * @param nhi nhi to be checked.
     * @return 0 if not unique, 1 if unique.
     */
    @Override
    public int isUniqueNHI(String nhi) {
        int id = 0;
        String query = "SELECT * FROM profiles WHERE NHI = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nhi);

            try (ResultSet result = stmt.executeQuery()) {
                result.next();
                id = result.getInt("ProfileId");
                return id;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return id;
    }

    /**
     * Removes a profile from the database.
     *
     * @param profile to remove.
     * @throws SQLException thrown on invalid sql.
     */
    @Override
    public void remove(Profile profile) throws SQLException {
        String query = "DELETE FROM profiles WHERE ProfileId = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profile.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Updates a profiles information in the database.
     *
     * @param profile to update.
     * @throws SQLException thrown on invalid sql.
     */
    @Override
    public void update(Profile profile) throws SQLException {
        String query = "UPDATE profiles SET NHI = ?, Username = ?, IsDonor = ?, IsReceiver = ?, " +
                "GivenNames = ?, LastNames = ?, Dob = ?, Dod = ?, Gender = ?, Height = ?, " +
                "Weight = ?, BloodType = ?, IsSmoker = ?, AlcoholConsumption = ?, " +
                "BloodPressureDiastolic = ?, BloodPressureSystolic = ?, Address = ?, Region = ?, " +
                "Phone = ?, Email = ?, Country = ?, BirthCountry = ?, CountryOfDeath = ?, " +
                "RegionOfDeath = ?, CityOfDeath = ?, StreetNo = ?, StreetName = ?, " +
                "Neighbourhood = ?, Created = ?, LastUpdated = ?, City = ?, " +
                "BloodDonationPoints = ?, LastBloodDonation = ?, PreferredName = ?, " +
                "PreferredGender = ? " +
                "WHERE ProfileId = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, profile.getNhi());
            stmt.setString(2, profile.getNhi());
            stmt.setBoolean(3, profile.getDonor());
            stmt.setBoolean(4, profile.isReceiver());
            stmt.setString(5, profile.getGivenNames());
            stmt.setString(6, profile.getLastNames());
            stmt.setDate(7, Date.valueOf(profile.getDateOfBirth()));
            stmt.setTimestamp(8,
                    profile.getDateOfDeath() != null ?
                    Timestamp.valueOf(profile.getDateOfDeath()) : null
            );
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
            stmt.setInt(32, profile.getBloodDonationPoints());
            stmt.setTimestamp(33, Timestamp.valueOf(profile.getLastBloodDonation()));
            stmt.setString(34, profile.getPreferredName());
            stmt.setString(35, profile.getPreferredGender());
            stmt.setInt(36, profile.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
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
            String region, String gender, String type, Set<OrganEnum> organs) {
        List<Profile> result = new ArrayList<>();

        StringBuilder query = new StringBuilder("SELECT distinct p.* FROM profiles p");
        if (!organs.isEmpty()) {
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
                " WHERE ((p.PreferredName is not null and CONCAT(p.GivenNames, p.PreferredName, " +
                        "p.LastNames) LIKE ?) or (CONCAT(p.GivenNames, p.LastNames) LIKE ?))");
        System.out.println(region);
        if (!"".equals(region)) {
            query.append(" and " +
                        "p.Region like ?");
        }
        if (!gender.equals("any")) {
            query.append(" and p.Gender = ?");
        }
        if (ageSearchInt > 0) {
            if (ageRangeSearchInt == -999) {
                query.append(
                        " and (((floor(datediff(CURRENT_DATE, p.dob) / 365.25) = ?) and p.Dod " +
                                "IS NULL) or (floor(datediff(p.Dod, p.Dob) / 365.25) = ?))");
            } else {
                query.append(
                        " and (((floor(datediff(CURRENT_DATE, p.dob) / 365.25) >= ?) and p.Dod " +
                                "IS NULL) or (floor(datediff(p.Dod, p.Dob) / 365.25) >= ?)) and " +
                                "(((floor(datediff(CURRENT_DATE, p.dob) / 365.25) <= ?) and p.Dod IS " +
                                "NULL) or (floor(datediff(p.Dod, p.Dob) / 365.25) <= ?))");
            }
        }
        if (type.equalsIgnoreCase("donor")) {
            query.append(" and p.IsDonor = ?");
        }
        if (type.equalsIgnoreCase("receiver")) {
            query.append(" and p.IsReceiver = ?");
        }

        query.append(";");


        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        query.toString(),
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY)) {
            stmt.setString(1, "%" + searchString + "%");
            stmt.setString(2, "%" + searchString + "%");
            int index = 3;
            if (!"".equals(region)) {
                stmt.setString(index, "%" + region + "%");
                index++;
            }

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
        }
        return result;
    }

    /**
     * Gets the number of profiles in the database.
     *
     * @return the number of profiles.
     * @throws SQLException thrown on invalid sql.
     */
    @Override
    public Integer size() throws SQLException {
        String query = "SELECT count(*) FROM profiles;";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(query)) {
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return 0;
    }

    @Override
    public List<Entry<Profile, OrganEnum>> getAllReceiving() {
        String query = "SELECT * FROM organs left join profiles on organs.ProfileId = " +
                "profiles.ProfileId WHERE Required = 1;";
        return getReceivers(query);
    }

    @Override
    public List<Entry<Profile, OrganEnum>> searchReceiving(String searchString) {
        String query =
                "SELECT * FROM organs left join profiles on organs.ProfileId = profiles.ProfileId "
                        +
                        "WHERE GivenNames like '%" + searchString + "%' or LastNames like '%" +
                        searchString + "%' or Region like '%" + searchString + "%' or Organ like '%"
                        +
                        searchString + "%';";
        return getReceivers(query);
    }

    /**
     * Gets all of the dead profiles in the db.
     *
     * @return a list of all the dead profiles.
     */
    @Override
    public List<Profile> getDead() {
        String query = "SELECT DISTINCT * FROM `profiles` " +
                "JOIN organs on profiles.ProfileId=organs.ProfileId " +
                "WHERE Dod IS NOT NULL AND ToDonate = 1 AND Expired IS NULL;";

        List<Profile> result = new ArrayList<>();

        ArrayList<Integer> existingIds = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
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
        }
        return result;
    }

    /**
     * Gets all of the dead profiles in the db.
     *
     * @return a list of all the dead profiles.
     * @throws SQLException thrown on invalid sql.
     */
    @Override
    public List<Profile> getDeadFiltered(String searchString) throws SQLException {
        String query = "SELECT * FROM profiles " +
                "JOIN organs on profiles.ProfileId = organs.ProfileId " +
                "WHERE CONCAT(GivenNames, LastNames) LIKE ? AND Dod IS NOT NULL AND " +
                "ToDonate = 1 AND Expired IS NULL;";

        List<Profile> result = new ArrayList<>();

        ArrayList<Integer> existingIds = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + searchString + "%");
            try (ResultSet allProfiles = stmt.executeQuery()) {
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
            throw e;
        }
        return result;
    }

    /**
     * Get list of receivers that could be recipients of a SELECTed organ.
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
        StringBuilder bloodQuery = new StringBuilder();
        for (int i = 0; i < blood.size(); i++) {
            if (i != blood.size() - 1) {
                bloodQuery.append("?,");
            } else {
                bloodQuery.append("?");
            }
        }

        List<String> orgs = Arrays.asList(organs.split("\\s*,\\s*"));
        StringBuilder organQuery = new StringBuilder();
        for (int i = 0; i < orgs.size(); i++) {
            if (i != orgs.size() - 1) {
                organQuery.append("?,");
            } else {
                organQuery.append("?");
            }
        }

        String query = "SELECT p.* FROM profiles p WHERE p.BloodType in (" +
                bloodQuery.toString() + ") AND " +
                "FLOOR(datediff(CURRENT_DATE, p.dob) / 365.25) BETWEEN ? AND ? AND " +
                "p.IsReceiver = 1 AND (" +
                "SELECT o.Organ FROM organs o WHERE o.ProfileId = p.ProfileId AND o.Organ in (" +
                organQuery.toString() + ") AND o.Required GROUP BY o.ProfileId) in (" +
                organQuery.toString() + ")";

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
                stmt.setString(count, type.replace('-', ' '));
                count++;
            }

            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    Profile profile = parseProfile(result);
                    OrganDAO organDAO = DAOFactory.getOrganDao();
                    profile.addOrgansRequired(organDAO.getRequired(profile));
                    receivers.add(profile);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return receivers;
    }

    /**
     * Checks that a profile has a password.
     *
     * @param nhi nhi of the profile.
     * @return true if they have a profile.
     * @throws SQLException thrown when there is a server error.
     */
    @Override
    public Boolean hasPassword(String nhi) throws SQLException {
        String query = "SELECT Username FROM profiles WHERE nhi = ? AND PASSWORD != ''";
        boolean hasPassword = false;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nhi);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    hasPassword = true;
                }
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
        return hasPassword;
    }

    /**
     * Checks the credentials of the profile.
     *
     * @param username username.
     * @param password password.
     * @return boolean, true if valid.
     * @throws UserNotFoundException thrown when a profile is not found in the database.
     */
    @Override
    public Boolean checkCredentials(String username, String password) throws UserNotFoundException {
        String query = "SELECT NHI, Password FROM profiles WHERE NHI = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

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
        }
        return false;
    }

    /**
     * Saves a password to the database for a certain profile.
     *
     * @param nhi nhi of the profile.
     * @param password password to be set.
     * @return true if successfully set, false otherwise.
     * @throws UserNotFoundException thrown when a user is not found in the database.
     */
    @Override
    public Boolean savePassword(String nhi, String password)
            throws UserNotFoundException {
        String query = "UPDATE profiles SET Password = ? WHERE NHI = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, PasswordUtilities.getSaltedHash(password));
            stmt.setString(2, nhi);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new UserNotFoundException("Not found", nhi);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Support function to execute queries to find organ receivers.
     *
     * @param query the SQL query to execute.
     * @return a Profile list of organ receivers.
     */
    private List<Entry<Profile, OrganEnum>> getReceivers(String query) {
        List<Entry<Profile, OrganEnum>> receivers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(query)) {

            while (result.next()) {
                Profile profile = parseProfile(result);
                OrganEnum organ = OrganEnum
                        .valueOf(result.getString("Organ").toUpperCase().replace(" ", "_"));
                receivers.add(new SimpleEntry<>(profile, organ));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return receivers;
    }

    /**
     * Parses the profile information FROM the rows returned FROM the database.
     *
     * @param profiles the rows returned FROM the database.
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

        int bloodDonationPoints;
        try {
            bloodDonationPoints = profiles.getInt("BloodDonationPoints");
        } catch (SQLException e) {
            bloodDonationPoints = 0;
        }

        Timestamp createdTs = profiles.getTimestamp("Created");
        LocalDateTime created = createdTs != null ? createdTs.toLocalDateTime() : null;

        Timestamp updatedTs = profiles.getTimestamp("Created");
        LocalDateTime updated = updatedTs != null ? updatedTs.toLocalDateTime() : null;

        Timestamp lastBloodDonationTs = profiles.getTimestamp("LastBloodDonation");
        LocalDateTime lastBloodDonation = lastBloodDonationTs != null ?
                lastBloodDonationTs.toLocalDateTime() : null;

        Profile profile = new Profile(id, nhi, username, isDonor, isReceiver, givenNames, lastNames,
                dob, dod, gender, height, weight, bloodType, isSmoker, alcoholConsumption,
                bpSystolic, bpDiastolic, address, region, phone, email, country, city,
                countryOfDeath, regionOfDeath, cityOfDeath, created, updated,
                preferredName, preferredGender, imageName, lastBloodDonation, bloodDonationPoints);

        try {
            setOrgans(profile);
            setMedications(profile);
            setProcedures(profile);
            setConditions(profile);
        } catch (OrganConflictException e) {
            log.error(e.getMessage(), e);
        }

        return profile;
    }

    /**
     * Add Organs to the profile object.
     *
     * @param profile the profile to add to.
     * @throws OrganConflictException if a conflict occurs.
     */
    private void setOrgans(Profile profile) throws OrganConflictException {
        OrganDAO database = DAOFactory.getOrganDao();

        profile.addOrgansDonating(database.getDonating(profile));
        profile.addOrgansDonated(database.getDonations(profile));
        profile.addOrgansRequired(database.getRequired(profile));
        profile.addOrgansReceived(database.getReceived(profile));
    }

    /**
     * Add Medications to the profile object.
     *
     * @param profile the profile to add to.
     */
    private void setMedications(Profile profile) {
        MedicationDAO database = DAOFactory.getMedicationDao();

        profile.setCurrentMedications(database.getAll(profile.getId(), true));
        profile.setHistoryOfMedication(database.getAll(profile.getId(), false));
    }

    /**
     * Add Procedures to the profile object.
     *
     * @param profile the profile to add to.
     */
    private void setProcedures(Profile profile) {
        ProcedureDAO database = DAOFactory.getProcedureDao();

        profile.setPendingProcedures(database.getAll(profile.getId(), true));
        profile.setPreviousProcedures(database.getAll(profile.getId(), false));
    }

    /**
     * Add Conditions to the profile object.
     *
     * @param profile the profile to add to.
     */
    private void setConditions(Profile profile) {
        ConditionDAO database = DAOFactory.getConditionDao();

        profile.setConditions(database.getAll(profile.getId(), false));
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
}
