package odms.view.user;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
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
        System.out.println("init");
        // Initialize command line GUI
        commandGUI = new CommandGUI(displayTextArea);
        controller.setupConsole(commandGUI);

    }

}
