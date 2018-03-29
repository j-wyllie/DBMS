package odms.controller;

import static odms.controller.AlertController.InvalidUsername;
import static odms.controller.GuiMain.getCurrentDatabase;


import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.data.ProfileDatabase;
import odms.profile.Profile;

public class LoginController {
    private static ProfileDatabase currentDatabase = getCurrentDatabase();
    private static Profile currentProfile;

    /**
     * TextField to input username.
     */
    @FXML
    private TextField usernameField;

    /**
     * TextField to input password.
     */
    @FXML
    private TextField passwordField;

    /**
     * Scene change to profile profile view if log in credentials are valid.
     * @param event clicking on the login button.
     * @throws IOException
     */
    @FXML
    private void handleLoginButtonClicked(ActionEvent event) throws IOException {

        try {
            int userId = Integer.valueOf(usernameField.getText());
            currentProfile = currentDatabase.getDonor(userId);
        }
        catch (Exception e) {
            System.out.println(e);
            InvalidUsername();
        }
        finally {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/DonorProfile.fxml"));
            Scene newScene = new Scene(parent);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(newScene);
            appStage.show();
        }
    }

    /**
     * Scene change to create account view.
     * @param event clicking on the create new account link.
     * @throws IOException
     */
    @FXML
    private void handleCreateNewAccountLinkClicked(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/CreateProfile.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

    public static Profile getCurrentProfile() {
        return currentProfile;
    }
}
