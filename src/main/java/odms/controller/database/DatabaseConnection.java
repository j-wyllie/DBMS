package odms.controller.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static java.lang.System.getProperty;

public final class DatabaseConnection {

    private static DataSource connectionSource;
    private ComboPooledDataSource source;

    /**
     * Constructor to create the singleton database connection class.
     */
    private DatabaseConnection() {
        try {
            source = new ComboPooledDataSource();

            // load in config file
            Properties prop = new Properties();
            prop.load(new FileInputStream(getProperty("user.dir") + "/config/db.config"));

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
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return connectionSource.getConnection();
    }

    /**
     * Helper to hold the instance of the singleton database connection class.
     */
    private static class DatabaseConnectionHelper {

        private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    }
}
