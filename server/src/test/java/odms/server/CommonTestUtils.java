package odms.server;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import server.model.database.DatabaseConnection;

public abstract class CommonTestUtils {

    @BeforeClass
    public static void configureDatabase() {
        DatabaseConnection.setTestDb();
    }

    @AfterClass
    public static void resetDb() {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        instance.resetTestDb();
        DatabaseConnection.setTestDb();
    }
}
