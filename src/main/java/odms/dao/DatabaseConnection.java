package odms.dao;

import static java.lang.System.getProperty;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {

    private static Connection connection;

    private DatabaseConnection() {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(getProperty("user.dir") + "/config/db.config"));

            String host = prop.getProperty("host");
            String database = prop.getProperty("database");
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");
            String driver = prop.getProperty("driver");

            Class.forName(driver);
            connection = DriverManager.getConnection(host + '/' + database, username, password);
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

    public static Connection getConnection() {
        return connection;
    }
}
