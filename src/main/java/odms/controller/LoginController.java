package odms.controller;

import static odms.controller.AlertController.invalidEntry;
import static odms.controller.AlertController.invalidUsername;
import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.GuiMain.getUserDatabase;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import odms.data.ProfileDatabase;
import odms.data.UserDatabase;
import odms.profile.Profile;
import odms.user.User;

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
     * @param event clicking on the login button.
     */
    @FXML
    private void handleLoginButtonClicked(ActionEvent event) {
        try {
            if (!usernameField.getText().equals("")) {
                int userId = Integer.valueOf(usernameField.getText());

                if (userId == 0) {
                    currentUser = userDatabase.getClinician(0);
                    String scene = "/view/ClinicianProfile.fxml";
                    String title = "Clinician";
                    showScene(event, scene, title, true);
                } else {
                    currentProfile = currentDatabase.getProfile(userId);

                    if (currentProfile != null) {
                        String scene = "/view/ProfileDisplay.fxml";
                        String title = "Profile";
                        showScene(event, scene, title, true);
                    } else {
                        invalidUsername();
                    }
                }
            }
        } catch (NumberFormatException e) {

            invalidEntry();

        } catch (Exception e) {
            e.printStackTrace();

            invalidUsername();
        }
    }

    /**
     * Scene change to create account view.
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

    public static void setCurrentProfile(Integer id) {
        currentProfile = currentDatabase.getProfile(id);
    }
}
