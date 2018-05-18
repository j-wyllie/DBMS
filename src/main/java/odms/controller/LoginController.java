package odms.controller;

import static odms.controller.AlertController.invalidEntry;
import static odms.controller.AlertController.invalidUsername;
import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.GuiMain.getUserDatabase;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/ClinicianProfile.fxml"));

                    Scene scene = new Scene(fxmlLoader.load());
                    ClinicianProfileController controller = fxmlLoader.getController();
                    controller.setCurrentUser(currentUser);
                    controller.initialize();

                    Stage stage = new Stage();
                    stage.setTitle("Clinician");
                    stage.setScene(scene);
                    stage.show();
                    closeCurrentStage();
                } else {
                    currentProfile = currentDatabase.getProfile(userId);

                    if (currentProfile != null) {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/view/ProfileDisplay.fxml"));

                        Scene scene = new Scene(fxmlLoader.load());
                        ProfileDisplayController controller = fxmlLoader.getController();
                        controller.setProfile(currentProfile);
                        controller.initialize();

                        Stage stage = new Stage();
                        stage.setTitle(currentProfile.getFullName() + "'s Profile");
                        stage.setScene(scene);
                        stage.show();

                        closeCurrentStage();
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

    private void closeCurrentStage() {
        Stage currentStage = (Stage) usernameField.getScene().getWindow();
        // do what you have to do
        currentStage.close();
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

    public static Profile getCurrentProfile() {return currentProfile;}
    public static User getCurrentUser() {return currentUser;}

    @FXML
    private void onEnter(ActionEvent event) {
        handleLoginButtonClicked(event);
    }
}
