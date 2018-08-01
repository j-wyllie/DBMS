package odms.view;

import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.controller.database.DAOFactory;
import odms.controller.database.ProfileDAO;
import odms.controller.database.UserDAO;
import odms.view.profile.ProfileDisplayViewTODO;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.user.UserNotFoundException;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.view.user.ClinicianProfileView;
import java.io.IOException;
import static odms.controller.AlertController.invalidUsername;
import static odms.controller.AlertController.invalidUsernameOrPassword;

public class LoginView extends CommonController {

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
                UserDAO database = DAOFactory.getUserDao();
                currentUser = database.get(username);
                if (currentUser.getPassword() != null && passwordField.getText()
                        .equals(currentUser.getPassword())) {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader
                                .setLocation(getClass().getResource("/view/ClinicianProfile.fxml"));

                        scene = new Scene(fxmlLoader.load());
                        ClinicianProfileView v = fxmlLoader.getController();
                        v.setCurrentUser(currentUser);
                        v.initialize();

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
                        ProfileDAO database = DAOFactory.getProfileDao();
                        Profile currentProfile = database.get(username);

                        if (currentProfile != null) {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(
                                    getClass().getResource("/view/ProfileDisplay.fxml"));

                            scene = new Scene(fxmlLoader.load());
                            ProfileDisplayViewTODO controller = fxmlLoader.getController();
                            controller.setProfile(currentProfile);
                            controller.initialize(currentProfile);

                            Stage stage = new Stage();
                            stage.setTitle(currentProfile.getFullName() + "'s profile");
                            stage.setScene(scene);
                            stage.show();

                            closeCurrentStage();
                    }
                } catch (NumberFormatException e) {
                    AlertController.invalidEntry();

                } catch (Exception e) {
                    e.printStackTrace();
                    invalidUsername();
                }
            } catch (SQLException e) {
                e.printStackTrace();
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
