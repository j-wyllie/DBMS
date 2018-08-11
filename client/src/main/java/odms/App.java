package odms;

import javafx.application.Application;
import odms.cli.CommandLine;
import odms.controller.GuiMain;
import odms.controller.data.ProfileDataIO;
import odms.controller.data.UserDataIO;
import odms.controller.history.CurrentHistory;
import odms.data.ProfileDatabase;
import odms.data.UserDatabase;

import java.io.File;

public class App {

    public static void main(String[] args) {

        CurrentHistory.currentSessionHistory.add(null);
        try {
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
}
