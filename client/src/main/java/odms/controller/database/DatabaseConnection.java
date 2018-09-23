package odms.controller.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains methods to obtain a connection the database, as well as setting db config.
 */
@Slf4j
public final class DatabaseConnection {

    private static ComboPooledDataSource source = new ComboPooledDataSource();

    private static final String DEFAULT_CONFIG = "/config/db.config";
    private static final String RESET_TEST_SQL = "/config/reset_test_db.sql";

    /**
     * Constructor to create the singleton database connection class.
     */
    private DatabaseConnection() {
        try {

            // load in config file
            Properties prop = new Properties();
            prop.load(ClassLoader.class.getResourceAsStream(DEFAULT_CONFIG));

            // set config string
            String host = prop.getProperty("host");
            String database = prop.getProperty("database");
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");
            String driver = prop.getProperty("driver");

            source.setDriverClass(driver);
            source.setJdbcUrl(host + '/' + database);
            source.setUser(username);
            source.setPassword(password);
            source.setMinPoolSize(5);
            source.setAcquireIncrement(5);
            source.setMaxPoolSize(50);

        } catch (PropertyVetoException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Supplys the instance of the singleton database connection class.
     *
     * @return the instance of the class.
     */
    public static DatabaseConnection getInstance() {
        return DatabaseConnectionHelper.INSTANCE;
    }

    /**
     * Returns a connection from the database connection pool.
     *
     * @return a connection.
     * @throws SQLException error.
     */
    private static Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    /**
     * Resets the current in use database to the standard set of tables.
     */
    public void reset() {
        executeQuery();
    }

    /**
     * Resamples the current in use database with the default data.
     */
    public void resample() {
        executeQuery();
    }

    /**
     * Executes the sql statements in the file at the location passed in.
     */
    private void executeQuery() {
        try {
            Connection conn = getConnection();
            parseSql(conn, RESET_TEST_SQL).executeBatch();

        } catch (SQLException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Parses an SQL file into a statement. Used for reset and resample files.
     *
     * @param conn Connection instance.
     * @param filepath Path of sql file.
     * @return Statement to be executed by statement.executeBatch() call.
     * @throws IOException If stream can't be added to.
     * @throws SQLException If statement cannot be created.
     */
    private Statement parseSql(Connection conn, String filepath) throws IOException, SQLException {
        InputStream inputStream = getClass().getResourceAsStream(filepath);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        try (Statement statement = conn.createStatement()) {
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                if (line.length() != 0 && !line.startsWith("--")) {
                    sb.append(line);
                }
                if (line.trim().endsWith(";")) {
                    statement.addBatch(sb.toString());
                    sb = new StringBuilder();
                }
            }

            br.close();
            return statement;
        }
    }

    /**
     * Helper to hold the instance of the singleton database connection class.
     */
    private static class DatabaseConnectionHelper {

        private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    }
}
