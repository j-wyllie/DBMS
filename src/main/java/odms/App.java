package odms;

import javafx.application.Application;
import odms.cli.CommandLine;
import odms.cli.CommandUtils;
import odms.controller.GuiMain;
import odms.controller.HistoryController;
import odms.data.ProfileDataIO;
import odms.data.ProfileDatabase;
import odms.data.UserDataIO;
import odms.data.UserDatabase;
import odms.data.UserDataIO;
import odms.data.UserDatabase;

import java.io.File;

public class App {
    private static final String DONOR_DATABASE = "example/example.json";
    private static final String USER_DATABASE = "example/users.json";

    private static ProfileDatabase profileDb = ProfileDataIO.loadData(DONOR_DATABASE);
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
            } else {
                profileDb = ProfileDataIO.loadData(DONOR_DATABASE);
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
