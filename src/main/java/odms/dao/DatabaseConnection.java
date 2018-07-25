package odms.dao;

import static java.lang.System.getProperty;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

public class DatabaseConnection {

    private static DataSource connectionSource;
    private ComboPooledDataSource source;

    private String DEFAULT_CONFIG = "/config/db.config";
    private static String CONFIG;

    private String RESET_SQL = "/config/reset.sql";
    private String RESAMPLE_SQL = "/config/resample.sql";

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
            prop.load(new FileInputStream(getProperty("user.dir") + CONFIG));

            // set config string
            String host = prop.getProperty("host");
            String database = prop.getProperty("database");
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");
            String driver = prop.getProperty("driver");

            // init
            try {
                source.setDriverClass(driver);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            source.setJdbcUrl(host + '/' + database);
            source.setUser(username);
            source.setPassword(password);
            source.setMinPoolSize(5);
            source.setAcquireIncrement(5);
            source.setMaxPoolSize(50);

            connectionSource = source;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper to hold the instance of the singleton database
     * connection class.
     */
    private static class DatabaseConnectionHelper {
        private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    }

    /**
     * Supplys the instance of the singleton database connection class.
     * @return the instance of the class.
     */
    public static DatabaseConnection getInstance() {
        return DatabaseConnectionHelper.INSTANCE;
    }

    /**
     * Returns a connection from the database connection pool.
     * @return a connection.
     * @throws SQLException error.
     */
    public static Connection getConnection() throws SQLException {
        return connectionSource.getConnection();
    }

    /**
     * Sets the config file location for the database.
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

    /**
     * Resamples the current in use database with the default data.
     */
    public void resample() {
        executeQuery(RESAMPLE_SQL);
    }

    /**
     * Executes the sql statements in the file at the location passed in.
     * @param filePath the location of the file.
     */
    private void executeQuery(String filePath) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(getProperty("user.dir") + filePath));
            StringBuffer buffer = new StringBuffer();

            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            in.close();

            DatabaseConnection instance = DatabaseConnection.getInstance();
            Connection conn = instance.getConnection();

            PreparedStatement stmt = conn.prepareStatement(buffer.toString());

            stmt.executeUpdate();
            conn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
