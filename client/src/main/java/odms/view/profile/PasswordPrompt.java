package odms.view.profile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import odms.commons.model.profile.Profile;

public class PasswordPrompt {

    public PasswordField passwordField;
    public PasswordField confirmPasswordField;
    public Label errorLabel;
    public Profile currentProfile;

    private odms.controller.profile.PasswordPrompt controller = new odms.controller.profile.PasswordPrompt(this);

    @FXML
    public void handleConfirmBtnPressed(ActionEvent actionEvent) {
        if (passwordField.getText().equals(confirmPasswordField.getText())) {
            controller.savePassword();
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
