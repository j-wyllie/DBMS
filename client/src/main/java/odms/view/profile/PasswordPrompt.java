package odms.view.profile;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import odms.commons.model.profile.Profile;
import odms.view.LoginView;

import java.sql.SQLException;

/**
 * The password prompt window view.
 */
public class PasswordPrompt {

    private static Integer PW_MIN_LENGTH = 5;

    private static String PW_BASE_ERROR = "Invalid password: ";
    private static String PW_NOT_MATCHING = "Passwords do not match.";
    private static String PW_TOO_SHORT = "Password is less than 5 characters.";

    public Profile currentProfile;

    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField confirmPasswordField;
    @FXML
    public Label errorLabel;
    @FXML
    public Label stdLabel;

    private odms.controller.profile.PasswordPrompt controller =
            new odms.controller.profile.PasswordPrompt(this);

    private LoginView loginView;

    /**
     * Validates the password on confirm button pressed.
     */
    @FXML
    public void handleConfirmBtnPressed() throws SQLException {
        if (passwordField.getText().length() >= PasswordPrompt.PW_MIN_LENGTH) {
            if (passwordField.getText().equals(confirmPasswordField.getText())) {
                controller.savePassword(this.loginView);
                Stage stage = (Stage) confirmPasswordField.getScene().getWindow();
                stage.close();
            } else {
                this.setErrorLabel(PasswordPrompt.PW_NOT_MATCHING);
            }
        } else {
            this.setErrorLabel(PasswordPrompt.PW_TOO_SHORT);
        }
    }

    /**
     * initializes view. Sets the current profile.
     * @param currentProfile the current profile.
     */
    public void initialize(Profile currentProfile, LoginView loginView) {
        this.currentProfile = currentProfile;
        this.loginView = loginView;
    }

    /**
     * Handle enter button being used to save password.
     */
    @FXML
    private void onEnter() throws SQLException {
        handleConfirmBtnPressed();
    }

    /**
     * Support function to update the error label text and set visible.
     * @param error the error text to use.
     */
    private void setErrorLabel(String error) {
        stdLabel.setVisible(false);

        errorLabel.setText(PasswordPrompt.PW_BASE_ERROR + error);
        errorLabel.setVisible(true);
    }
}
