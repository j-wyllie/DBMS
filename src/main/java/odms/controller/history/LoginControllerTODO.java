package odms.controller.history;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.view.profile.ProfileDisplayControllerTODO;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.user.ClinicianProfileControllerTODO;
import odms.controller.user.UserNotFoundException;
import odms.model.data.ProfileDatabase;
import odms.model.data.UserDatabase;
import odms.model.profile.Profile;
import odms.model.user.User;

import java.io.IOException;

import static odms.controller.AlertController.invalidUsername;
import static odms.controller.AlertController.invalidUsernameOrPassword;
import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.GuiMain.getUserDatabase;

public class LoginControllerTODO extends CommonController {

    private static User currentUser;
    private ProfileDatabase currentDatabase = getCurrentDatabase();
    private UserDatabase userDatabase = getUserDatabase();
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

    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Scene change to profile profile view if log in credentials are valid.
     *
     * @param event clicking on the login button.
     */
    @FXML
    private void handleLoginButtonClicked(ActionEvent event) {
        Scene scene;

        if (!usernameField.getText().equals("")) {

            String username = usernameField.getText();
            try {
                currentUser = userDatabase.getUser(username);
                System.out.println(currentUser.getUsername());
                if (currentUser.getPassword() != null && passwordField.getText()
                        .equals(currentUser.getPassword())) {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader
                                .setLocation(getClass().getResource("/view/ClinicianProfile.fxml"));

                        scene = new Scene(fxmlLoader.load());
                        ClinicianProfileControllerTODO controller = fxmlLoader.getController();
                        controller.setCurrentUser(currentUser);
                        controller.initialize();

                        Stage stage = new Stage();
                        stage.setTitle(currentUser.getUserType().getName());
                        stage.setScene(scene);
                        stage.show();
                        closeCurrentStage();
                    } catch (IOException e) {
                        invalidUsername();
                    }
                } else {
                    invalidUsernameOrPassword();
                }
            } catch (UserNotFoundException u) {
                try {
                    int userId = Integer.valueOf(usernameField.getText());
                    if (userId == 0) {
                        currentUser = userDatabase.getUser(0);

                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader
                                .setLocation(getClass().getResource("/view/ClinicianProfile.fxml"));

                        scene = new Scene(fxmlLoader.load());
                        ClinicianProfileControllerTODO controller = fxmlLoader.getController();
                        controller.setCurrentUser(currentUser);
                        controller.initialize();

                        Stage stage = new Stage();
                        stage.setTitle(currentUser.getUserType().getName());
                        stage.setScene(scene);
                        stage.show();
                        closeCurrentStage();
                    } else {
                        Profile currentProfile = currentDatabase.getProfile(userId);

                        if (currentProfile != null) {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(
                                    getClass().getResource("/view/ProfileDisplay.fxml"));

                            scene = new Scene(fxmlLoader.load());
                            ProfileDisplayControllerTODO controller = fxmlLoader.getController();
                            controller.setProfile(currentProfile);
                            controller.initialize();

                            Stage stage = new Stage();
                            stage.setTitle(currentProfile.getFullName() + "'s profile");
                            stage.setScene(scene);
                            stage.show();

                            closeCurrentStage();
                        } else {
                            invalidUsername();
                        }
                    }
                } catch (NumberFormatException e) {
                    AlertController.invalidEntry();

                } catch (Exception e) {
                    e.printStackTrace();
                    invalidUsername();
                }
            }
        }
    }

    private void closeCurrentStage() {
        Stage currentStage = (Stage) usernameField.getScene().getWindow();
        currentStage.close();
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
        String title = "Create profile";
        showScene(event, scene, title, false);
    }

    @FXML
    private void onEnter(ActionEvent event) {
        handleLoginButtonClicked(event);
    }
}
