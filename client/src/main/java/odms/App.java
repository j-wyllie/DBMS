package odms;

import javafx.application.Application;
import odms.cli.CommandLine;
import odms.controller.GuiMain;
import odms.controller.history.CurrentHistory;

public class App {

    public static void main(String[] args) {

        CurrentHistory.currentSessionHistory.add(null);
        try {
            if (args == null || args.length == 0) {
                Application.launch(GuiMain.class);

            } else {
                String s = args[0].toLowerCase();
                if ("-cmd".equals(s)) {
                    CommandLine commandLine = new CommandLine();
                    commandLine.initialiseConsole();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
