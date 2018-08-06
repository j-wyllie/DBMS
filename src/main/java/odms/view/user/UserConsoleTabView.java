package odms.view.user;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import odms.cli.CommandGUI;
import odms.controller.user.UserConsoleTabController;
import odms.model.user.User;

public class UserConsoleTabView {

    protected ObjectProperty<User> currentProfile = new SimpleObjectProperty<>();
    private UserConsoleTabController controller = new UserConsoleTabController(this);

    private CommandGUI commandGUI;

    @FXML
    private TextArea displayTextArea;

    public void initialize() {
        // Initialize command line GUI
        commandGUI = new CommandGUI(displayTextArea);
        controller.setupConsole(commandGUI);

    }

}
