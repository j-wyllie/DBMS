package odms.view.user;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import odms.cli.CommandGUI;
import odms.controller.user.Console;
import odms.commons.model.user.User;

public class ConsoleTab {

    protected ObjectProperty<User> currentProfile = new SimpleObjectProperty<>();
    private Console controller = new Console(this);

    private CommandGUI commandGUI;

    @FXML
    private TextArea displayTextArea;

    public void initialize() {
        // Initialize command line GUI
        commandGUI = new CommandGUI(displayTextArea);
        controller.setupConsole(commandGUI);
    }

    public void captureInput() {
        controller.setupConsole(commandGUI);
    }

    public void stopInputCapture() {
        controller.stopInputCapture();
    }
}
