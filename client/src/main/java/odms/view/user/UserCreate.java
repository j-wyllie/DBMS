package odms.view.user;

import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.controller.AlertController;
import odms.controller.GuiMain;
import odms.commons.model.user.User;
import odms.commons.model.enums.UserType;
import odms.controller.database.DAOFactory;
import odms.view.CommonView;

public class UserCreate extends CommonView {

    @FXML
    private TextField userUsernameField;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField userRegionField;
    @FXML
    private PasswordField userPasswordField;
    @FXML
    private ChoiceBox<UserType> userTypeBox;
    @FXML
    private Button userCreateAccountButton;

    /**
     * Handles the creation of new user accounts on button clicked event.
     * @param event of create account button clicked.
     * @throws SQLException error.
     */
    @FXML
    public void handleUserCreateAccountButtonClicked(ActionEvent event) throws SQLException {
        if (checkValidEntries()) {
            try {
                User user = new User(userTypeBox.getValue(),
                        userNameField.getText(),
                        userRegionField.getText()
                );
                user.setUsername(userUsernameField.getText());
                user.setPassword(userPasswordField.getText());
                DAOFactory.getUserDao().add(user);
                Stage stage = (Stage) userCreateAccountButton.getScene().getWindow();
                stage.close();
                editTrueAction(event, true);
            } catch (IllegalArgumentException e) {
                AlertController.uniqueUsername();
            }
        } else {
            AlertController.invalidEntry();
        }
    }

    /**
     * Checks that the entries are valid.
     * @return a boolean signalling whether the entries are valid.
     */
    private boolean checkValidEntries() {
        if (userNameField.getText().equals("") || userUsernameField.getText().equals("") ||
                userRegionField.getText().equals("") || userPasswordField.getText().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Adds the UserTypes to the choice box.
     */
    private void populateUserTypeBox() {
        userTypeBox.getItems().clear();
        userTypeBox.getItems().addAll(UserType.CLINICIAN, UserType.ADMIN);
        userTypeBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void initialize() {
        populateUserTypeBox();
    }
}
