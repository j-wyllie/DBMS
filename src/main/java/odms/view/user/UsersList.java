package odms.view.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import odms.model.user.User;
import odms.view.CommonView;

/**
 * The users list tab view.
 */
public class UsersList extends CommonView {
    private odms.controller.user.UsersList controller = new odms.controller.user.UsersList(this);

    @FXML
    private TableView<User> viewUsersTable;

    /**
     * Opens up the create user window.
     */
    @FXML
    public void handleCreateUserBtnPressed(ActionEvent actionEvent) {
        createPopup(actionEvent, "/view/UserCreate.fxml", "Create User");
    }

    public TableView<User> getViewUsersTable() {
        return viewUsersTable;
    }

    /**
     * Calls the controller method to setup the user table.
     */
    private void setUpUsersTable() {
        controller.setUpUsersTable();
    }

    /**
     * Adds a listener so when the window regains focus the table refreshes.
     * @param parentStage The parent stage to add the listener to.
     */
    public void initialize(Stage parentStage) {
        setUpUsersTable();
        if (parentStage != null) {
            parentStage.focusedProperty().addListener(
                    (observable, oldValue, newValue) -> controller.refreshViewUsersTable());
    }
    }
}
