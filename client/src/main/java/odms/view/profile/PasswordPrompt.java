package odms.view.profile;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import odms.commons.model.profile.Profile;

/**
 * The password prompt window view.
 */
public class PasswordPrompt {

    public PasswordField passwordField;
    public PasswordField confirmPasswordField;
    public Label errorLabel;
    public Profile currentProfile;

    private odms.controller.profile.PasswordPrompt controller =
            new odms.controller.profile.PasswordPrompt(this);

    /**
     * Validates the password on confirm button pressed.
     */
    @FXML
    public void handleConfirmBtnPressed() {
        if (passwordField.getText().length() >= 5 &&
                passwordField.getText().equals(confirmPasswordField.getText())) {
            controller.savePassword();
            Stage stage = (Stage) confirmPasswordField.getScene().getWindow();
            stage.close();
        } else {
            errorLabel.setVisible(true);
        }
    }

    /**
     * initializes view. Sets the current profile.
     * @param currentProfile the current profile.
     */
    public void initialize(Profile currentProfile) {
        this.currentProfile = currentProfile;
    }
}
