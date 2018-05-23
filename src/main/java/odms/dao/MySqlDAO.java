package odms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;


public class MySqlDAO implements ReadOnlyDAO {

    @Override
    public void queryDatabase(String query) {
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();
        Connection connection = connectionInstance.getConnection();

        if (isReadOnlyQuery(query)) {
            try {
                Statement stmt = connection.createStatement();
                ResultSet result = stmt.executeQuery(query);

                outputResult(result);
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isReadOnlyQuery(String query) {
        String queryType = query.trim().substring(0, 6).trim();
        if (queryType.equalsIgnoreCase("select")) {
            return true;
        }
        else {
            System.out.println("Please enter a valid read-only query.");
            return false;
        }
    }

    private void outputResult(ResultSet result) throws SQLException{
        ResultSetMetaData data = result.getMetaData();
        String border = "+";
        String headers = "|";
        String leftAlignFormat = "|";

        for (int i = 0; i < data.getColumnCount(); i++) {
            border += StringUtils.repeat("-", data.getColumnDisplaySize(i)) + "+";


            headers += String.format("| %s", data.getColumnName(i));
            headers += StringUtils.repeat(" ", data.getColumnDisplaySize(i)
                    - data.getColumnName(i).length()) + "|";

            leftAlignFormat += "%-" + data.getColumnDisplaySize(i) + "s |";
        }

        System.out.println(border);
        System.out.println(headers);
        System.out.format(border);

        while (result.next()) {
            ArrayList<String> rowData = new ArrayList<>();
            for (int i = 0; i < data.getColumnCount(); i++) {
                rowData.add(result.getString(i));
            }
            System.out.format(leftAlignFormat, rowData);
        }
        System.out.println(border);

    }
}
