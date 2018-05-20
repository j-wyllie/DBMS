package odms;

import static java.lang.System.getProperty;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javafx.application.Application;
import odms.cli.CommandLine;
import odms.cli.CommandUtils;
import odms.controller.GuiMain;
import odms.data.ProfileDataIO;
import odms.data.ProfileDatabase;

public class App {
    private static final String DONOR_DATABASE = "example/example.json";
    private static ProfileDatabase profileDb = ProfileDataIO.loadData(DONOR_DATABASE);

    public static void main(String[] args) {
        try {
            Connection conn = createConnection();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        CommandUtils.currentSessionHistory.add("");
        try {

            if (args == null || args.length == 0) {
                Application.launch(GuiMain.class);

            } else {
                switch (args[0].toLowerCase()) {
                    case "-cmd":
                        CommandLine commandLine = new CommandLine(profileDb);
                        commandLine.initialiseConsole();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Connection createConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(getProperty("user.dir") + "/config/db.config"));

        String host = prop.getProperty("host");
        String database = prop.getProperty("database");
        String username = prop.getProperty("username");
        String password = prop.getProperty("password");
        String driver = prop.getProperty("driver");

        Class.forName(driver);
        Connection conn = DriverManager.getConnection(host + '/' + database, username, password);
        return conn;
    }
}
