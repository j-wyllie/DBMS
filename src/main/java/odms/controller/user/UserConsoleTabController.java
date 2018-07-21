package odms.controller.user;

import odms.App;
import odms.cli.CommandGUI;
import odms.cli.CommandLine;
import odms.view.user.UserConsoleTabView;

public class UserConsoleTabController {

    UserConsoleTabView view;

    public UserConsoleTabController(UserConsoleTabView v) {
        view = v;
    }

    public void setupConsole(CommandGUI commandGUI) {
        System.setIn(commandGUI.getIn());
        System.setOut(commandGUI.getOut());

        // Start the command line in an alternate thread
        CommandLine commandLine = new CommandLine(App.getProfileDb(), commandGUI.getIn(),
                commandGUI.getOut());
        commandGUI.initHistory(commandLine);
        Thread t = new Thread(commandLine);
        t.start();
    }

}
