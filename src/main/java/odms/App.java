package odms;

import javafx.application.Application;
import odms.commandlineview.CommandLine;
import odms.commandlineview.CommandUtils;
import odms.controller.GuiMain;
import odms.data.DonorDatabase;

public class App {
    private static DonorDatabase donorDb = new DonorDatabase();

    public static void main(String[] args) {
        CommandUtils.currentSessionHistory.add("");
        try {

            if (args == null || args.length == 0) {
                Application.launch(GuiMain.class);
            } else {
                switch (args[0].toLowerCase()) {
                    case "-cmd":
                        CommandLine commandLine = new CommandLine(donorDb);
                        commandLine.initialiseConsole();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
