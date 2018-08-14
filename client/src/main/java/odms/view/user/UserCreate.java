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
import odms.controller.database.MySqlUserDAO;
import odms.commons.model.enums.UserType;
import odms.commons.model.user.User;
import odms.controller.database.DAOFactory;
import odms.view.CommonView;

/**
 * Class to handle the user create popup.
 */
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

    private MySqlUserDAO mySqlUserDAO = new MySqlUserDAO();

    /**
     * Checks that all fields have valid inputs.
     * Adds the user to the database.
     *
     * @param event ActionEvent when the button is pressed.
     */
    @FXML
    public void handleUserCreateAccountButtonClicked(ActionEvent event) throws SQLException {
        if (checkValidEntries()) {
                User user = new User(userTypeBox.getValue(),
                        userNameField.getText(),
                        userRegionField.getText()
                );
                user.setUsername(userUsernameField.getText());
                user.setPassword(userPasswordField.getText());
                try {
                    mySqlUserDAO.add(user);
                    Stage stage = (Stage) userCreateAccountButton.getScene().getWindow();
                    stage.close();
                    editTrueAction(event, true);
                } catch (SQLException e) {
                    AlertController.invalidEntry("User already exists with username " +
                            userNameField.getText());
                }

        } else {
            AlertController.invalidEntry();
        }
    }

    /**
     * Checks that the username entered is unique.
     *
     * @return a boolean signalling that the username is or isn't unique.
     */
    private boolean checkValidEntries() {
        return !userNameField.getText().equals("") && !userUsernameField.getText().equals("") &&
                !userRegionField.getText().equals("") && !userPasswordField.getText().equals("");
    }

    /**
     * Adds the UserTypes to the choice box.
     */
    private void populateUserTypeBox() {
        userTypeBox.getItems().clear();
        userTypeBox.getItems().addAll(UserType.CLINICIAN, UserType.ADMIN);
        userTypeBox.getSelectionModel().selectFirst();
    }

    /**
     * Calls the method to populate the usertype box on window initialization.
     */
    public void initialize() {
        populateUserTypeBox();
    }
}
