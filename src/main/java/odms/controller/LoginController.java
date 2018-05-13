package odms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import odms.data.ProfileDatabase;
import odms.data.UserDatabase;
import odms.profile.Profile;
import odms.user.User;

import java.io.IOException;

import static odms.controller.AlertController.invalidEntry;
import static odms.controller.AlertController.invalidUsername;
import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.GuiMain.getUserDatabase;

public class LoginController extends CommonController {

    private static ProfileDatabase currentDatabase = getCurrentDatabase();
    private static UserDatabase userDatabase = getUserDatabase();
    private static Profile currentProfile = null;
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
     *
     * @param event clicking on the login button.
     */
    @FXML
    private void handleLoginButtonClicked(ActionEvent event) {
        String scene;
        String title;

            if (!usernameField.getText().equals("")) {

                String username = usernameField.getText();
                if (username.equals("admin")) {
                    try {
                        currentUser = userDatabase.getUser("admin");
                        scene = "/view/ClinicianProfile.fxml";
                        title = "Clinician";
                        showScene(event, scene, title, true);
                    } catch (UserNotFoundException | IOException e) {
                        invalidUsername();
                    }

                } else {
                    try {
                        int userId = Integer.valueOf(usernameField.getText());
                    if (userId == 0) {
                        currentUser = userDatabase.getUser(0);
                        scene = "/view/ClinicianProfile.fxml";
                        title = "Clinician";
                        showScene(event, scene, title, true);
                    } else {
                        currentProfile = currentDatabase.getProfile(userId);

                        if (currentProfile != null) {
                            scene = "/view/ProfileDisplay.fxml";
                            title = "Profile";
                            showScene(event, scene, title, true);
                        } else {
                            invalidUsername();
                        }
                    }
                } catch (UserNotFoundException | IOException | NumberFormatException e) {
                        invalidUsername();
                    }
            }
        }
    }

    /**
     * Scene change to create account view.
     *
     * @param event clicking on the create new account link.
     * @throws IOException
     */
    @FXML
    private void handleCreateNewAccountLinkClicked(ActionEvent event) throws IOException {
        String scene = "/view/ProfileCreate.fxml";
        String title = "Create Profile";
        showScene(event, scene, title, false);
    }

    public static Profile getCurrentProfile() {
        return currentProfile;
    }


    @FXML
    private void onEnter(ActionEvent event) {
        handleLoginButtonClicked(event);
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentDonor(Integer id) {
        currentProfile = currentDatabase.getProfile(id);
    }
}
