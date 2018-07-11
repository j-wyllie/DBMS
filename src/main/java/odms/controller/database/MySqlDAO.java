package odms.controller.database;

import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;


public class MySqlDAO implements ReadOnlyDAO {

    /**
     * Establishes a connection with the database and executes the supplied query, before outputing
     * the result set returned.
     *
     * @param query
     */
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
                System.out.println("Please enter a valid read-only query.");
            }
        }
    }

    /**
     * Returns true if the query supplied is a valid read-only query.
     *
     * @param query
     * @return whether the query is a valid read-only query or not.
     */
    private boolean isReadOnlyQuery(String query) {
        String queryType = query.trim().substring(0, 6).trim();
        if (queryType.equalsIgnoreCase("select")) {
            return true;
        } else {
            System.out.println("Please enter a valid read-only query.");
            return false;
        }
    }

    /**
     * Formats the output of the result set supplied.
     *
     * @param result
     * @throws SQLException
     */
    private void outputResult(ResultSet result) throws SQLException {
        ResultSetMetaData data = result.getMetaData();
        String border = "+";
        String headers = "|";
        ArrayList<Integer> multipleArray = new ArrayList<>();

        for (int i = 1; i <= data.getColumnCount(); i++) {
            int multiple = data.getColumnDisplaySize(i) + 6;
            if (data.getColumnDisplaySize(i) < data.getColumnName(i).length()) {
                multiple = data.getColumnName(i).length() + 5;
            }
            border += StringUtils.repeat("-", multiple) + "+";

            headers += String.format(" %s", data.getColumnName(i));
            headers += StringUtils.repeat(" ", multiple - data.getColumnName(i).length() - 1) + "|";

            multipleArray.add(multiple);
        }

        System.out.println(border);
        System.out.println(headers);
        System.out.format(border);
        System.out.println();

        while (result.next()) {
            for (int i = 1; i <= data.getColumnCount(); i++) {
                String current = result.getString(i);
                if (current == null) {
                    current = "null";
                }
                System.out.print("| " + current + StringUtils
                        .repeat(" ", multipleArray.get(i - 1) - current.length() - 1));
            }

            System.out.println("|");
            System.out.println(border);

        }

        System.out.println();
    }
}
