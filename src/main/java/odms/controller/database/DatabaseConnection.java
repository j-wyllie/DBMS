package odms.controller.database;

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

public class DatabaseConnection {

    private static DataSource connectionSource;
    private static ComboPooledDataSource source;

    private String DEFAULT_CONFIG = "/config/db.config";
    private static String TEST_CONFIG = "/config/db_test.config";
    private static String CONFIG = null;

    private String RESET_SQL = "/config/reset.sql";
    private String RESAMPLE_SQL = "/config/resample.sql";

    private String RESET_TEST_SQL = "/config/reset_test_db.sql";

    /**
     * Constructor to create the singleton database connection class.
     */
    private DatabaseConnection() {
        try {
            source = new ComboPooledDataSource();

            if (CONFIG == null) {
                CONFIG = DEFAULT_CONFIG;
            }

            // load in config file
            Properties prop = new Properties();
            prop.load(ClassLoader.class.getResourceAsStream(CONFIG));

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
                e.printStackTrace();
            }
            source.setJdbcUrl(host + '/' + database);
            source.setUser(username);
            source.setPassword(password);
            source.setMinPoolSize(5);
            source.setAcquireIncrement(5);
            source.setMaxPoolSize(50);

            connectionSource = source;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTestDb() {
        CONFIG = TEST_CONFIG;
        source = new ComboPooledDataSource();

        // load in config file
        Properties prop = new Properties();
        try {
            prop.load(ClassLoader.class.getResourceAsStream(TEST_CONFIG));
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        source.setJdbcUrl(host + '/' + database);
        source.setUser(username);
        source.setPassword(password);
        source.setMinPoolSize(5);
        source.setAcquireIncrement(5);
        source.setMaxPoolSize(50);
        source.setMaxIdleTime(3000);

        connectionSource = source;
    }

    /**
     * Helper to hold the instance of the singleton database connection class.
     */
    private static class DatabaseConnectionHelper {
        private static final DatabaseConnection INSTANCE = new DatabaseConnection();
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
        return connectionSource.getConnection();
    }

    /**
     * Sets the config file location for the database.
     *
     * @param path to the file.
     */
    public static void setConfig(String path) {
        CONFIG = path;
    }

    /**
     * Resets the current in use database to the standard set of tables.
     */
    public void reset() {
        executeQuery(RESET_SQL);
    }

    public void resetTestDb() {
        executeQuery(RESET_TEST_SQL);
    }

    /**
     * Resamples the current in use database with the default data.
     */
    public void resample() {
        executeQuery(RESAMPLE_SQL);
    }

    /**
     * Executes the sql statements in the file at the location passed in.
     *
     * @param filePath the location of the file.
     */
    private void executeQuery(String filePath) {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        try {
            Connection conn = instance.getConnection();
            parseSql(conn, RESET_TEST_SQL).executeBatch();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses an SQL file into a statement. Used for reset and resample files.
     * @param conn Connection instance.
     * @param filepath Path of sql file.
     * @return Statement to be executed by statement.executeBatch() call.
     * @throws IOException If stream can't be added to.
     * @throws SQLException If statement cannot be created.
     */
    private Statement parseSql(Connection conn, String filepath) throws IOException, SQLException {
        InputStream inputStream = getClass().getResourceAsStream(filepath);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        Statement statement = conn.createStatement();
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

        br.close();
        return statement;
    }
}
