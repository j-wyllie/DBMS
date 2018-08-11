package odms.controller.user;

import odms.App;
import odms.cli.CommandGUI;
import odms.cli.CommandLine;
import odms.view.user.ConsoleTab;

public class Console {

    ConsoleTab view;

    public Console(ConsoleTab view) {
        this.view = view;
    }

    public void setupConsole(CommandGUI commandGUI) {
        System.setIn(commandGUI.getIn());
        System.setOut(commandGUI.getOut());

        // Start the command line in an alternate thread
        CommandLine commandLine = new CommandLine(commandGUI.getIn(), commandGUI.getOut());
        commandGUI.initHistory(commandLine);
        Thread t = new Thread(commandLine);
        t.setDaemon(true);
        t.start();
    }

}
