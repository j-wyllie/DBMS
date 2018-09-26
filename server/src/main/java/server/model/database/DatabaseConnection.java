package server.model.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains methods to obtain a connection the database, as well as setting db config.
 */
@Slf4j
public final class DatabaseConnection {

    private static final String DEFAULT_CONFIG = "/config/db.config";
    private static final String TEST_CONFIG = "/config/db_test.config";
    private static final String RESET_TEST_SQL = "/config/reset_test_db.sql";

    private static String config = null;
    private static DataSource connectionSource;
    private static ComboPooledDataSource source;

    /**
     * Constructor to create the singleton database connection class.
     */
    private DatabaseConnection() {
        try {
            source = new ComboPooledDataSource();

            if (config == null) {
                config = DEFAULT_CONFIG;
            }

            // load in config file
            Properties prop = new Properties();
            prop.load(ClassLoader.class.getResourceAsStream(config));

            // set config string
            String host = prop.getProperty("host");
            String database = prop.getProperty("database");
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");
            String driver = prop.getProperty("driver");

            // init
            try {
                source.setDriverClass(driver);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            source.setJdbcUrl(host + '/' + database);
            source.setUser(username);
            source.setPassword(password);
            source.setMinPoolSize(3);
            source.setAcquireIncrement(5);
            source.setMaxPoolSize(50);

            connectionSource = source;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void setTestDb() {
        config = TEST_CONFIG;
        source = new ComboPooledDataSource();

        // load in config file
        Properties prop = new Properties();
        try {
            prop.load(ClassLoader.class.getResourceAsStream(TEST_CONFIG));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        // set config string
        String host = prop.getProperty("host");
        String database = prop.getProperty("database");
        String username = prop.getProperty("username");
        String password = prop.getProperty("password");
        String driver = prop.getProperty("driver");

        // init
        try {
            source.setDriverClass(driver);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        source.setJdbcUrl(host + '/' + database);
        source.setUser(username);
        source.setPassword(password);
        source.setMinPoolSize(3);
        source.setAcquireIncrement(5);
        source.setMaxPoolSize(50);
        source.setMaxIdleTime(3000);

        connectionSource = source;
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
    public static Connection getConnection() throws SQLException {
        return getInstance().connectionSource.getConnection();
    }

    /**
     * Sets the config file location for the database.
     *
     * @param path to the file.
     */
    public static void setConfig(String path) {
        config = path;
    }

    /**
     * Resets the current in use database to the standard set of tables.
     */
    public void reset() {
        executeQuery();
    }

    /**
     * Resets the test database to the standard set of tables.
     */
    public void resetTestDb() {
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
        try (Connection conn = getConnection()) {
            parseSqlAndExecute(conn);

        } catch (SQLException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Parses an SQL file into a statement and execute. Used for reset and resample files.
     *
     * @param conn Connection instance.
     * @throws IOException If stream can't be added to.
     * @throws SQLException If statement cannot be created.
     */
    private void parseSqlAndExecute(Connection conn) throws IOException, SQLException {

        try (InputStream inputStream = getClass().getResourceAsStream(RESET_TEST_SQL);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                Statement statement = conn.createStatement()) {
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                if ((line.length() != 0 && !line.startsWith("--"))) {
                    sb.append(line);
                }
                if (line.trim().endsWith(";")) {
                    statement.addBatch(sb.toString());
                    sb = new StringBuilder();
                }
            }
            statement.executeBatch();
        }
    }

    /**
     * Helper to hold the instance of the singleton database connection class.
     */
    private static class DatabaseConnectionHelper {
        private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    }
}
