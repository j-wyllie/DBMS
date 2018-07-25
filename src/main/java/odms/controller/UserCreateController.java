package odms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.user.User;
import odms.user.UserType;
import org.apache.commons.lang3.StringUtils;

public class UserCreateController extends CommonController {

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

    @FXML
    public void handleUserCreateAccountButtonClicked(ActionEvent event) {
        if (checkValidEntries()) {
            if (checkUniqueUsername()) {
                User user = new User(userTypeBox.getValue(), userNameField.getText(), userRegionField.getText());
                user.setUsername(userUsernameField.getText());
                user.setPassword(userPasswordField.getText());
                GuiMain.getUserDatabase().addUser(user);
                Stage stage = (Stage) userCreateAccountButton.getScene().getWindow();
                stage.close();

                editTrueAction(event, true);
            } else {
                AlertController.uniqueUsername();
            }
        } else {
            AlertController.invalidEntry();
        }
    }

    /**
     * checks that all fields have been filled out
     * @return a boolean signalling that all fields were filled it.
     */
    private boolean checkUniqueUsername() {
        return GuiMain.getUserDatabase().checkUniqueUsername(userUsernameField.getText());
    }

    /**
     * checks that the username entered is unique
     * @return a boolean signalling that the username is or isn't unique
     */
    private boolean checkValidEntries() {
        if (userNameField.getText().equals("") || userUsernameField.getText().equals("") ||
                userRegionField.getText().equals("") || userPasswordField.getText().equals("") ||
                StringUtils.isNumeric(userUsernameField.getText())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * adds the UserTypes to the choice box
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
