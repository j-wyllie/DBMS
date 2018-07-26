package odms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javafx.application.Application;
import odms.cli.CommandLine;
import odms.controller.GuiMain;
import odms.controller.HistoryController;
import odms.dao.DatabaseConnection;
import odms.data.ProfileDataIO;
import odms.data.ProfileDatabase;
import odms.data.UserDataIO;
import odms.data.UserDatabase;

import java.io.File;
import odms.enums.CountriesEnum;

public class App {
    private static final String DONOR_DATABASE = "example/example.json";
    private static final String USER_DATABASE = "example/users.json";

    private static ProfileDatabase profileDb = ProfileDataIO.loadDataFromJSON(DONOR_DATABASE);
    private static UserDatabase userDb = UserDataIO.loadData(USER_DATABASE);

    public static void main(String[] args) {
        HistoryController.currentSessionHistory.add(null);
        try {

            File userDbFile = new File(USER_DATABASE);
            if (!userDbFile.isFile()) {
                userDb = new UserDatabase();
            } else {
                userDb = UserDataIO.loadData(USER_DATABASE);
            }

            File profileDbFile = new File(DONOR_DATABASE);
            if (!profileDbFile.isFile()) {
                profileDb = new ProfileDatabase();
                ProfileDataIO.saveData(profileDb, DONOR_DATABASE);
            } else {
                profileDb = ProfileDataIO.loadDataFromJSON(DONOR_DATABASE);
            }

            if (args == null || args.length == 0) {
                Application.launch(GuiMain.class);

            } else {
                switch (args[0].toLowerCase()) {
                    case "-cmd":
                        CommandLine commandLine = new CommandLine(profileDb, userDb);
                        commandLine.initialiseConsole();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ProfileDatabase getProfileDb() {
        return profileDb;
    }

    public static UserDatabase getUserDb() {
        return userDb;
    }

    public void setCurrentDatabase(ProfileDatabase profileDb) {
        profileDb = profileDb;
    }

}
