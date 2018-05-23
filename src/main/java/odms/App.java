package odms;

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
}
