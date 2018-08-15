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
import odms.view.profile.Display;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.user.UserNotFoundException;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.view.user.ClinicianProfile;
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
     */
    @FXML
    private void handleLoginButtonClicked() {
        if (!usernameField.getText().equals("")) {
            String username = usernameField.getText();

            try {
                try {
                    currentUser = loadUser(username);

                    loadUserView(currentUser);
                } catch (UserNotFoundException u) {
                    Profile currentProfile = loadProfile(username);

                    loadProfileView(currentProfile);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load a profile from the database.
     *
     * @param username the username to load
     * @return a profile object
     * @throws SQLException if a SQL error occurs
     */
    private Profile loadProfile(String username) throws SQLException {
        return DAOFactory.getProfileDao().get(username);
    }

    /**
     * Load the profile view.
     *
     * @param profile the profile object whose data will be displayed
     */
    private void loadProfileView(Profile profile) {
        try {
            if (profile != null) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(
                        getClass().getResource("/view/ProfileDisplay.fxml"));

                Scene scene = new Scene(fxmlLoader.load());
                Display controller = fxmlLoader.getController();
                controller.initialize(profile, false, null);

                Stage stage = new Stage();
                if (profile.getPreferredName() != null && !profile.getPreferredName().isEmpty()) {
                    stage.setTitle(profile.getPreferredName() + "'s profile");
                } else {
                    stage.setTitle(profile.getFullName() + "'s profile");
                }
                stage.setScene(scene);
                stage.show();

                closeCurrentStage();
            } else {
                AlertController.invalidUsername();
            }
        } catch (NumberFormatException e) {
            AlertController.invalidEntry();
        } catch (Exception e) {
            e.printStackTrace();
            invalidUsername();
        }
    }

    /**
     * Load a user from the database.
     *
     * @param username the username to load
     * @return a user object
     * @throws SQLException if a SQL error occurs
     * @throws UserNotFoundException if a user cannot be found
     */
    private User loadUser(String username) throws SQLException, UserNotFoundException {
        UserDAO database = DAOFactory.getUserDao();
        this.currentUser = database.get(username);
        return currentUser;
    }

    /**
     * Load the user view.
     */
    private void loadUserView(User user) {
        if (user.getPassword() != null && passwordField.getText().equals(user.getPassword())) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(
                        getClass().getResource("/view/ClinicianProfile.fxml")
                );

                Scene scene = new Scene(fxmlLoader.load());
                ClinicianProfile v = fxmlLoader.getController();
                v.setCurrentUser(user);
                v.initialize();

                Stage stage = new Stage();
                stage.setTitle(user.getUserType().getName());
                stage.setScene(scene);
                stage.show();
                closeCurrentStage();
            } catch (IOException e) {
                invalidUsername();
            }
        } else {
            invalidUsernameOrPassword();
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
     */
    @FXML
    private void handleCreateNewAccountLinkClicked(ActionEvent event) throws IOException {
        String scene = "/view/ProfileCreate.fxml";
        String title = "Create profile";
        showScene(event, scene, title, false);
    }

    /**
     * Handle enter button being used to login.
     */
    @FXML
    private void onEnter() {
        handleLoginButtonClicked();
    }
}
