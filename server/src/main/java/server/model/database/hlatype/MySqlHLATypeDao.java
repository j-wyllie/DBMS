package server.model.database.hlatype;

import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.HLAType;
import server.model.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MySqlHLATypeDao implements HLATypeDAO{
    /**
     * Get the HLA type for the profile.
     * @param profile to get the HLA type for.
     */
    @Override
    public HLAType get(int profile) {
        String query = "select * from hla_type where ProfileId = ?;";
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        HLAType hlaType = new HLAType();

        try {
            Connection conn = connectionInstance.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile);
            ResultSet hlaResult = stmt.executeQuery();
            hlaType = parseHLA(hlaResult);
            conn.close();
            stmt.close();
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return hlaType;
    }

    /**
     * Parses a single row of the hla_type table and returns a hlatype object
     * @param rs rows returned by the database.
     * @return the parsed condition object
     * @throws SQLException error.
     */
    private HLAType parseHLA(ResultSet rs) throws SQLException {
        HLAType hlaType = new HLAType();
        rs.next();
        try {
            hlaType.setGroupX(parseMap(rs.getString("groupX")));
            hlaType.setGroupY(parseMap(rs.getString("groupY")));
            hlaType.setSecondaryAntigens(parseMap(rs.getString("secondary")));
            return hlaType;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Parses a string from the database and returns it as a Map<String, Integer>
     * @param string String to be parsed.
     * @return the parsed map
     */
    private Map<String, Integer> parseMap(String string) {
        Map<String, Integer> map = new HashMap<>();
        if(!string.equals("{}")) {
            string = string.replace("{", "");
            string = string.replace("}", "");
            string = string.replace(" ", "");
            String[] strings = string.split(",");
            for (int i = 0; i < strings.length; i++) {
                String[] values = strings[i].split("=");
                map.put(values[0], Integer.parseInt(values[1]));
            }
        }
        return map;
    }

    /**
     * Add a new hlatype to a profile.
     * @param profile to add the hla to.
     * @param hla to add.
     */
    @Override
    public void add(int profile, HLAType hla) {
        String query = "insert into hla_type (ProfileId, groupX, groupY, secondary"
                + ") values (?, ?, ?, ?);";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile);
            stmt.setString(2, hla.getGroupX().toString());
            stmt.setString(3, hla.getGroupY().toString());
            stmt.setString(4, hla.getSecondaryAntigens().toString());
            stmt.executeUpdate();
            conn.close();
            stmt.close();
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Remove a hlaType from a profile.
     * @param profile of hlatype to remove
     */
    @Override
    public void remove(int profile) {
        String query = "delete from hla_type where profileId = ?;";
        DatabaseConnection instance = DatabaseConnection.getInstance();
        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, profile);
            stmt.executeUpdate();
            conn.close();
            stmt.close();
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Update a hlaType for the profile.
     * @param hlaType to update.
     * @param profile of hlatype
     */
    @Override
    public void update(HLAType hlaType, int profile) {
        String query = "update hla_type set groupX = ?, groupY = ?, secondary = ?"
                + "where profileId = ?";
        DatabaseConnection instance = DatabaseConnection.getInstance();

        try {
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, hlaType.getGroupX().toString());
            stmt.setString(2, hlaType.getGroupY().toString());
            stmt.setString(3, hlaType.getSecondaryAntigens().toString());
            stmt.setInt(4, profile);
            stmt.executeUpdate();
            conn.close();
            stmt.close();
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

}
