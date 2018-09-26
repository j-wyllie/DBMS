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

    private static final Integer PW_MIN_LENGTH = 5;

    private static final String PW_BASE_ERROR = "Invalid password: ";
    private static final String PW_NOT_MATCHING = "Passwords do not match.";
    private static final String PW_TOO_SHORT = "Password is less than 5 characters.";

    private Profile currentProfile;

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

    /**
     * Validates the password on confirm button pressed.
     */
    @FXML
    public void handleConfirmBtnPressed() {
        if (passwordField.getText().length() >= PasswordPrompt.PW_MIN_LENGTH) {
            if (passwordField.getText().equals(confirmPasswordField.getText())) {
                controller.savePassword();
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
    public void initialize(Profile currentProfile) {
        this.currentProfile = currentProfile;
    }

    /**
     * Handle enter button being used to save password.
     */
    @FXML
    private void onEnter() {
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

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public void setCurrentProfile(Profile currentProfile) {
        this.currentProfile = currentProfile;
    }
}
