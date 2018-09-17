package odms;

import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;
import odms.cli.CommandLine;
import odms.controller.GuiMain;
import odms.controller.history.CurrentHistory;

@Slf4j
public class App {

    public static void main(String[] args) {

        CurrentHistory.getCurrentSessionHistory() .add(null);
        try {
            if (args == null || args.length == 0) {
                Application.launch(GuiMain.class);

            } else {
                switch (args[0].toLowerCase()) {
                    case "-cmd":
                        CommandLine commandLine = new CommandLine();
                        commandLine.initialiseConsole();
                        break;
                    default:
                        // noop
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
