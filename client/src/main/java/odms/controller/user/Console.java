package odms.controller.user;

import java.io.PrintStream;
import odms.App;
import odms.cli.CommandGUI;
import odms.cli.CommandLine;
import odms.view.user.ConsoleTab;

public class Console {

    ConsoleTab view;
    private CommandLine commandLine;
    Thread t;
    CommandGUI commandGUI;
    PrintStream stdout = System.out;

    public Console(ConsoleTab view) {
        this.view = view;
    }

    public void setupConsole(CommandGUI cli) {
        this.commandGUI = cli;
        System.setIn(commandGUI.getIn());
        System.setOut(commandGUI.getOut());

        // Start the command line in an alternate thread
        commandLine = new CommandLine(commandGUI.getIn(), commandGUI.getOut());
        commandGUI.initHistory(commandLine);
        t = new Thread(commandLine);
        t.setDaemon(true);
        t.start();
    }

    public void stopInputCapture() {
        System.setOut(stdout);
    }
}
