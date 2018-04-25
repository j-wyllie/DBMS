package odms.controller;

import static odms.controller.AlertController.InvalidUsername;
import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.GuiMain.getUserDatabase;


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
import odms.data.ProfileDatabase;
import odms.data.UserDatabase;
import odms.profile.Profile;
import odms.user.User;

public class LoginController {

    private static ProfileDatabase currentDatabase = getCurrentDatabase();
    private static UserDatabase userDatabase = getUserDatabase();
    private static Profile currentProfile;
    private static User currentUser;

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

            if (userId == 0) {
                currentUser = userDatabase.getClinician(0);
                Parent parent = FXMLLoader.load(getClass().getResource("/view/ClinicianProfile.fxml"));
                Scene newScene = new Scene(parent);
                Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                appStage.setScene(newScene);
                appStage.show();
            } else {
                currentProfile = currentDatabase.getProfile(userId);
                if (currentProfile != null) {
                    Parent parent = FXMLLoader.load(getClass().getResource("/view/DonorProfile.fxml"));
                    Scene newScene = new Scene(parent);
                    Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    appStage.setScene(newScene);
                    appStage.show();
                } else {
                    InvalidUsername();
                }
            }
        } catch (Exception e) {
            InvalidUsername();
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
    public static void setCurrentDonor(Integer id) {currentProfile = currentDatabase.getProfile(id);}
}
