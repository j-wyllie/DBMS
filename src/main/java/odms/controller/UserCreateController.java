package odms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import odms.user.User;
import odms.user.UserType;

public class UserCreateController {

    @FXML
    private TextField userUsernameField;

    @FXML
    private TextField userNameField;

    @FXML
    private TextField userRegionField;

    @FXML
    private ChoiceBox<UserType> userTypeBox;

    @FXML
    public void handleUserCreateAccountButtonClicked() {
        if (!checkValidEntries()) {
            if (checkUniqueUsername()) {
                User user = new User(userTypeBox.getValue(), userNameField.getText(), userUsernameField.getText());
                user.setUsername(userUsernameField.getText());
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
                userRegionField.getText().equals("")) {
            return false;
        } else {
            return true;
        }
    }
}
