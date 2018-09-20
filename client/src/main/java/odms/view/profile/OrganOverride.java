package odms.view.profile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.controller.AlertController;
import odms.view.CommonView;

import java.sql.SQLException;

/**
 * Control the organ removal view for selecting reasoning behind removing an organ from a profile.
 */
public class OrganOverride extends CommonView {

    @FXML
    private Label organLabel;

    @FXML
    private TextField reasonText;

    @FXML
    private Button confirmButton;

    private Profile currentProfile;
    private String currentOrgan;

    private odms.controller.profile.OrganOverride controller = new odms.controller.profile.OrganOverride(this);
    private User currentUser;

    /**
     * Confirms the changes made to the manual organ override and stores the reason given for this
     * change.
     *
     * @param event the ActionEvent
     */
    @FXML
    private void handleConfirmButtonAction(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            controller.confirm();
        } catch (SQLException e) {
            AlertController.invalidEntry("Failed to override organ expiry.");
        }
        appStage.close();
    }

    /**
     * Closes the window when the cancel button is clicked.
     *
     * @param event the
     */
    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.close();
    }

    /**
     * Sets the current profile and organ to be processed on start up. Also configures components of
     * the window.
     *
     * @param organ the organ being overridden.
     * @param profile the profile the organ is being removed from.
     */
    public void initialize(String organ, Profile profile, User user) {
        currentUser = user;
        currentProfile = profile;
        currentOrgan = organ;
        organLabel.setText(organLabel.getText() + organ);
        confirmButton.setDisable(true);

        reasonText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 1) {
                confirmButton.setDisable(true);
            } else {
                confirmButton.setDisable(false);
            }
        });
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public String getCurrentOrgan() {
        return currentOrgan;
    }

    public String getReasonText() {
        return reasonText.getText();
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
