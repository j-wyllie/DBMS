package odms.view.profile;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class PasswordPrompt {

    public PasswordField passwordField;
    public PasswordField confirmPasswordField;
    public Label errorLabel;

    private odms.controller.profile.PasswordPrompt controller = new odms.controller.profile.PasswordPrompt(this);


    public void handleConfirmBtnPressed(ActionEvent actionEvent) {
        if (passwordField.getText().equals(confirmPasswordField.getText())) {

        } else {
            errorLabel.setVisible(true);
        }
    }
}
