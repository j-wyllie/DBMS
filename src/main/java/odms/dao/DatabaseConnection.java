package odms.dao;

import static java.lang.System.getProperty;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

public class DatabaseConnection {

    private static DataSource connectionSource;
    private ComboPooledDataSource source;

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
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class DatabaseConnectionHelper {
        private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    }

    public static DatabaseConnection getInstance() {
        return DatabaseConnectionHelper.INSTANCE;
    }

    public static Connection getConnection() throws SQLException {
        return connectionSource.getConnection();
    }
}
