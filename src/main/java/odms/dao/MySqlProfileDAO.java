package odms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import odms.data.ProfileDatabase;
import odms.profile.Profile;

public class MySqlProfileDAO implements ProfileDAO {

    @Override
    public void getAll() {
        String query = "select * from profiles;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();

        try {
            Connection conn = connectionInstance.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet allProfiles = stmt.executeQuery(query);
            conn.close();

            ProfileDatabase database = new ProfileDatabase();
            //todo: store profiles locally.
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Profile profile) {
        String query = "insert into profiles (ProfileId, Ird, Username, IsDonor, IsReceiver, GivenNames,"
                + " LastNames, Dob, Dod, Gender, Height, Weight, BloodType, IsSmoker, AlcoholConsumption,"
                + " BloodPressure, Address, Region, Phone, Email, Created, LastUpdated) values (?, ?, ?,"
                + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getId());
            stmt.setInt(2, profile.getIrdNumber());
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

    @Override
    public void update(Profile profile) {
        String query = "update profiles set Ird = ?, Username = ?, IsDonor = ?, IsReceiver = ?, "
                + "GivenNames = ?, LastNames = ?, Dob = ?, Dod = ?, Gender = ?, Height = ?, Weight = ?,"
                + "BloodType = ?, IsSmoker = ?, AlcoholConsumption = ?, BloodPressure = ?, Address = ?,"
                + "Region = ?, Phone = ?, Email = ?, Created = ?, LastUpdated = ? where UserId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile.getIrdNumber());
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
