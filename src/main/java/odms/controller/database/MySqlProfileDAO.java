package odms.controller.database;

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
import odms.model.enums.OrganEnum;
import odms.model.medications.Drug;
import odms.model.profile.OrganConflictException;
import odms.model.profile.Procedure;
import odms.model.profile.Profile;

public class MySqlProfileDAO implements ProfileDAO {

    /**
     * Gets all profiles from the database.
     */
    @Override
    public List<Profile> getAll() throws SQLException {
        String query = "select * from profiles;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        List<Profile> result = new ArrayList<>();
        Connection conn = connectionInstance.getConnection();
        Statement stmt = conn.createStatement();
        try {

            ResultSet allProfiles = stmt.executeQuery(query);

            while (allProfiles.next()) {
                Profile newProfile  = parseProfile(allProfiles);
                result.add(newProfile);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
            stmt.close();
        }
        return result;
    }

    /**
     * Get a single profile from the database.
     * @return a profile.
     */
    @Override
    public Profile get(int profileId) throws SQLException {
        String query = "select * from profiles where ProfileId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Profile profile = null;
        Connection conn = instance.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();

            rs.next();
            profile = parseProfile(rs);

        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
            stmt.close();
        }

        return profile;
    }

    /**
     * Get a single profile from the database by username.
     * @param username of the profile.
     * @return a profile.
     */
    @Override
    public Profile get(String username) throws SQLException {
        String query = "select * from profiles where Username = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Profile profile = null;
        Connection conn = instance.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        try {
            while(rs.next()) {
                profile = parseProfile(rs);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            stmt.close();
            conn.close();
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
            dod = profiles.getDate("Dod").toLocalDate();
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
                gender, height, weight, bloodType, isSmoker, alcoholConsumption, bpSystolic, bpDiastolic,
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

        profile.setCurrentMedications(database.getAll(profile, true));
        profile.setHistoryOfMedication(database.getAll(profile, false));

        return profile;
    }

    private Profile setProcedures(Profile profile) {
        ProcedureDAO database = DAOFactory.getProcedureDao();

        profile.setPendingProcedures((ArrayList<Procedure>) database.getAll(profile, true));
        profile.setPreviousProcedures((ArrayList<Procedure>) database.getAll(profile, false));

        return profile;
    }

    private Profile setConditions(Profile profile) {
        ConditionDAO database = DAOFactory.getConditionDao();

        profile.setConditions(database.getAll(profile, false));
        return profile;
    }

    /**
     * Adds a new profile to the database.
     * @param profile to add.
     */
    @Override
    public void add(Profile profile) throws SQLException {
        String query = "insert into profiles (NHI, Username, IsDonor, IsReceiver, GivenNames,"
                + " LastNames, Dob, Dod, Gender, Height, Weight, BloodType, IsSmoker, AlcoholConsumption,"
                + " BloodPressureSystolic, BloodPressureDiastolic, Address, StreetNo, StreetName, Neighbourhood,"
                + " City, ZipCode, Region, Country, BirthCountry, Phone, Email, Created, LastUpdated) values "
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setString(1, profile.getNhi());
            stmt.setString(2, profile.getNhi());
            stmt.setBoolean(3, profile.getDonor());
            stmt.setBoolean(4, profile.getReceiver());
            stmt.setString(5, profile.getGivenNames());
            stmt.setString(6, profile.getLastNames());
            stmt.setString(7, profile.getDateOfBirth().toString());
            if (profile.getDateOfDeath() == null) {
                stmt.setString(8, null);
            }
            else {
                stmt.setString(8, profile.getDateOfDeath().toString());
            }
            stmt.setString(9, profile.getGender());
            stmt.setDouble(10, profile.getHeight());
            stmt.setDouble(11, profile.getWeight());
            stmt.setString(12, profile.getBloodType());
            if (profile.getIsSmoker() == null) {
                stmt.setBoolean(13, false);
            }
            else {
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

            stmt.executeUpdate();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            stmt.close();
            conn.close();
        }
    }

    /**
     * Checks if a username already exists in the database.
     * @param username to check.
     * @return true is the username does not already exist.
     */
    @Override
    public boolean isUniqueUsername(String username) throws SQLException {
        String query = "select * from profiles where Username = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setString(1, username);

            ResultSet result = stmt.executeQuery();

            if (result.getFetchSize() == 0) {
                return true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
            stmt.close();
        }
        return false;
    }

    /**
     * Removes a profile from the database.
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
            e.printStackTrace();
        } finally {
            conn.close();
            stmt.close();
        }
    }

    private void removeOrgans(Profile profile) {
        OrganDAO database = DAOFactory.getOrganDao();

        profile.getOrgansDonating().forEach(organ -> {
            database.removeDonating(profile, organ);
        });
        profile.getOrgansDonated().forEach(organ -> {
            database.removeDonation(profile, organ);
        });
        profile.getOrgansReceived().forEach(organ -> {
            database.removeReceived(profile, organ);
        });
        profile.getOrgansRequired().forEach(organ -> {
            database.removeRequired(profile, organ);
        });
    }

    private void removeMedications(Profile profile) {
        MedicationDAO database = DAOFactory.getMedicationDao();

        profile.getCurrentMedications().forEach(medication -> {
            database.remove(medication);
        });
        profile.getHistoryOfMedication().forEach(medication -> {
            database.remove(medication);
        });
    }

    private void removeProcedures(Profile profile) {
        ProcedureDAO database = DAOFactory.getProcedureDao();

        profile.getPendingProcedures().forEach(procedure -> {
            database.remove(procedure);
        });
        profile.getPreviousProcedures().forEach(procedure -> {
            database.remove(procedure);
        });
    }

    private void removeConditions(Profile profile) {
        ConditionDAO database = DAOFactory.getConditionDao();

        profile.getCurrentConditions().forEach(condition -> {
            database.remove(condition);
        });
        profile.getCuredConditions().forEach(condition -> {
            database.remove(condition);
        });
    }

    /**
     * Updates a profiles information in the database.
     * @param profile to update.
     */
    @Override
    public void update(Profile profile) throws SQLException {
        String query = "update profiles set NHI = ?, Username = ?, IsDonor = ?, IsReceiver = ?, "
                + "GivenNames = ?, LastNames = ?, Dob = ?, Dod = ?, Gender = ?, Height = ?, Weight = ?,"
                + "BloodType = ?, IsSmoker = ?, AlcoholConsumption = ?, BloodPressureDiastolic = ?, "
                + "BloodPressureSystolic = ?, Address = ?, "
                + "Region = ?, Phone = ?, Email = ?, Created = ?, LastUpdated = ? where ProfileId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query);
        try {

            stmt.setString(1, profile.getNhi());
            stmt.setString(2, profile.getNhi());
            stmt.setBoolean(3, profile.getDonor());
            stmt.setBoolean(4, profile.getReceiver());
            stmt.setString(5, profile.getGivenNames());
            stmt.setString(6, profile.getLastNames());
            stmt.setDate(7, Date.valueOf(profile.getDateOfBirth()));
            if (!(profile.getDateOfDeath() == null)) {
                stmt.setDate(8, Date.valueOf(profile.getDateOfDeath()));
            }
            else {
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
            stmt.setTimestamp(21, Timestamp.valueOf(profile.getTimeOfCreation()));
            stmt.setTimestamp(22, Timestamp.valueOf(profile.getLastUpdated()));
            stmt.setInt(23, profile.getId());

            stmt.executeUpdate();

        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
            stmt.close();
        }
    }

    /**
     * Searches for a sublist of profiles based on criteria.
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
        String query = "select distinct p.* from profiles p inner join organs o on p.ProfileId = o.ProfileId";
        if (organs.size() > 0) {
            int index = 0;
            for (OrganEnum organ : organs) {
                if (index > 0) {
                    query += " or o.Organ = '" + organ.getNamePlain() + "'";
                    index++;
                }
                else {
                    query += " and o.Organ = '" + organ.getNamePlain() + "'";
                    index++;
                }
            }
        }
        query += " where (p.GivenNames like ? OR p.LastNames like ?) and p.Region like ?";
        if (!gender.equals("any")) {
            query += " and p.Gender = ?";
        }
        if (ageSearchInt > 0) {
            if (ageRangeSearchInt == -999) {
                query += " and (((floor(datediff(CURRENT_DATE, p.dob) / 365.25) = ?) and p.Dod IS NULL) or (floor(datediff(p.Dod, p.Dob) / 365.25) = ?))";
            }
            else {
                query += " and (((floor(datediff(CURRENT_DATE, p.dob) / 365.25) >= ?) and p.Dod IS NULL) or (floor(datediff(p.Dod, p.Dob) / 365.25) >= ?))"
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

        Connection conn = connectionInstance.getConnection();

        PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        try {
            stmt.setString(1, "%" + searchString + "%");
            stmt.setString(2, "%" + searchString + "%");
            stmt.setString(3, "%" + region + "%");

            int index = 4;
            if (!gender.equals("any")) {
                stmt.setString(4, gender);
                index++;
            }

            if (ageSearchInt > 0) {
                if (ageRangeSearchInt == -999) {
                    stmt.setInt(index, ageSearchInt);
                    index++;
                    stmt.setInt(index, ageSearchInt);
                    index++;
                }
                else {
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

            System.out.println(stmt);

            ResultSet allProfiles = stmt.executeQuery();
            int size = 0;
            allProfiles.last();
            size = allProfiles.getRow();
            allProfiles.beforeFirst();

            if (size > 250) {
                for (int i = 0; i <250; i++) {
                    allProfiles.next();
                    Profile newProfile  = parseProfile(allProfiles);
                    result.add(newProfile);
                }
            } else {
                while (allProfiles.next()) {
                    Profile newProfile  = parseProfile(allProfiles);
                    result.add(newProfile);
                }
            }
            System.out.println(stmt);

        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
            stmt.close();

        }
        return result;
    }

    /**
     * Gets the number of profiles in the database.
     * @return the number of profiles.
     */
    @Override
    public int size() throws SQLException {
        String query = "select count(*) from profiles;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection conn = instance.getConnection();
        Statement stmt = conn.createStatement();
        try {


            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                return result.getInt(1);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
            stmt.close();
        }
        return 0;
    }

    @Override
    public List<Entry<Profile, OrganEnum>> getAllReceiving() {
        String query = "select * from organs left join profiles on organs.ProfileId = profiles.ProfileId;";
        return getReceivers(query);
    }

    @Override
    public List<Entry<Profile, OrganEnum>> searchReceiving(String searchString) {
        String query = "select * from organs left join profiles on organs.ProfileId = profiles.ProfileId "
                + "where GivenNames like '%" + searchString + "%' or LastNames like '%" + searchString
                + "%' or Region like '%" + searchString + "%' or Organ like '%" + searchString + "%';";
        return getReceivers(query);
    }

    private List<Entry<Profile, OrganEnum>> getReceivers(String query) {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        List<Entry<Profile, OrganEnum>> receivers = new ArrayList<>();

        try {
            Connection conn = instance.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                Profile profile = parseProfile(result);
                OrganEnum organ = OrganEnum.valueOf(result.getString("Organ").toUpperCase().replace(" ", "_"));
                receivers.add(new SimpleEntry<>(profile, organ));
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receivers;
    }
}
