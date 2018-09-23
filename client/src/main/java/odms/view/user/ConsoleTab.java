package odms.view.user;

import java.io.UnsupportedEncodingException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;
import odms.cli.CommandGUI;
import odms.commons.model.user.User;
import odms.controller.user.Console;

@Slf4j
public class ConsoleTab {

    protected ObjectProperty<User> currentProfile = new SimpleObjectProperty<>();
    private Console controller = new Console(this);

    private CommandGUI commandGUI;

    @FXML
    private TextArea displayTextArea;

    public void initialize() {
        // Initialize command line GUI
        try {
            commandGUI = new CommandGUI(displayTextArea);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        controller.setupConsole(commandGUI);
    }

    public void captureInput() {
        controller.setupConsole(commandGUI);
    }

    public void stopInputCapture() {
        controller.stopInputCapture();
    }
}
