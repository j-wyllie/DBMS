package odms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.StringUtils;


public class MySqlDAO implements ReadOnlyDAO {

    @Override
    public void queryDatabase(String query) {
        DatabaseConnection connectionInstance = DatabaseConnection.getInstance();

        if (isReadOnlyQuery(query)) {
            try {
                Connection connection = connectionInstance.getConnection();

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

    private void outputResult(ResultSet result) throws SQLException {

        //System.out.println("resultset:  "+result);

        ResultSetMetaData rsmd = result.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (result.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = result.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));
            }
            System.out.println("");
        }
//        ResultSetMetaData data = result.getMetaData();
//        String border = "+";
//        String headers = "|";
//        String leftAlignFormat = "|";
//
//        System.out.println(result.getFetchSize());
//        for (int i = 0; i < data.getColumnCount(); i++) {
//            int multiple = data.getColumnDisplaySize(i) + 6;
//            if (data.getColumnDisplaySize(i) < data.getColumnName(i).length()) {
//                multiple = data.getColumnName(i).length() + 5;
//            }
//            border += StringUtils.repeat("-", multiple) + "+";
//
//
//            headers += String.format(" %s", data.getColumnName(i));
//            headers += StringUtils.repeat(" ", multiple - data.getColumnName(i).length() - 1) + "|";
//
//            leftAlignFormat += "%-" + multiple + "s |";
//        }
//
//        System.out.println(border);
//        System.out.println(headers);
//        System.out.format(border);
//
//        while (result.next()) {
//            ArrayList<String> rowData = new ArrayList<>();
//            for (int i = 0; i < data.getColumnCount(); i++) {
//                rowData.add(result.getString(i));
//            }
//            if (rowData.size() > 0) {
//                System.out.format(leftAlignFormat, rowData);
//                System.out.println(border);
//            }
//        }
//
//        System.out.println();
    }
}
