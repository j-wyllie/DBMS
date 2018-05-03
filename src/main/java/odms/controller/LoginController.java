package odms.controller;

import static odms.controller.AlertController.invalidEntry;
import static odms.controller.AlertController.invalidUsername;
import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.GuiMain.getUserDatabase;


import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.data.ProfileDatabase;
import odms.profile.Profile;
import odms.data.UserDatabase;
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

                    showScene(event, scene);
                } else {
                    currentProfile = currentDatabase.getProfile(userId);

                    if (currentProfile != null) {

                        FXMLLoader fxmlLoader = new FXMLLoader(
                                getClass().getResource("/view/ProfileDisplay.fxml"));
                        Scene scene = new Scene(fxmlLoader.load());
                        ProfileDisplayController controller = fxmlLoader.getController();

                        controller.setLoggedInProfile(currentProfile);
                        controller.initialize();
                        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                        appStage.setScene(scene);
                        appStage.show();
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
        showScene(event, "/view/ProfileCreate.fxml", true);
    }

    public static Profile getCurrentProfile() {
        return currentProfile;
    }

    @FXML
    private void onEnter(ActionEvent event) {
        handleLoginButtonClicked(event);
    }

    public static void setCurrentDonor(Integer id) {
        currentProfile = currentDatabase.getProfile(id);
    }
}
