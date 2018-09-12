package odms.view;

import static odms.controller.AlertController.invalidUsername;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;
import odms.controller.database.user.UserDAO;
import odms.controller.user.UserNotFoundException;
import odms.view.profile.Display;
import odms.view.profile.PasswordPrompt;
import odms.view.user.ClinicianProfile;

/**
 * Login view.
 */
@Slf4j
public class LoginView extends CommonController {

    private User currentUser;

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
     * @param event the login button clicked event.
     */
    @FXML
    private void handleLoginButtonClicked(ActionEvent event) {
        if (!usernameField.getText().equals("")) {
            String username = usernameField.getText();

            try {
                if (CommonView.isValidNHI(usernameField.getText())) {
                    tryLoginProfile(event, username);
                } else if (checkUser()) {
                    currentUser = loadUser(username);
                    loadUserView(currentUser);
                } else {
                    AlertController.invalidUsernameOrPassword();
                }
            } catch (UserNotFoundException | SQLException | IllegalArgumentException u) {
                AlertController.invalidUsernameOrPassword();

            }
        }
    }

    /**
     * Attempts to log the profile in with their credentials.
     *
     * @param event    login button clicked event.
     * @param username Username entered.
     * @throws SQLException thrown when there is an error in the sql.
     */
    private void tryLoginProfile(ActionEvent event, String username) throws SQLException {
        Profile currentProfile = loadProfile(username);

        if (!hasPassword()) {
            try {
                showPasswordPromptWindow(currentProfile, event);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else if (checkProfile()) {
            loadProfileView(currentProfile);
        } else {
            AlertController.invalidUsernameOrPassword();
        }
    }

    /**
     * Checks the profile has a password.
     *
     * @return True if they do.
     */
    private boolean hasPassword() {
        ProfileDAO database = DAOFactory.getProfileDao();
        return database.hasPassword(usernameField.getText());
    }

    /**
     * Checks the profiles credentials.
     *
     * @return boolean if valid credentials.
     */
    private boolean checkProfile() {
        ProfileDAO database = DAOFactory.getProfileDao();
        return database.checkCredentials(usernameField.getText(), passwordField.getText());
    }

    /**
     * Checks the users credentials with the database.
     *
     * @return Boolean based on if the credentials are correct. True if valid.
     */
    private Boolean checkUser() {
        UserDAO database = DAOFactory.getUserDao();
        return database.checkCredentials(usernameField.getText(), passwordField.getText());
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
                controller.initialize(profile, false, null, null);

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
        } catch (IOException e) {
            invalidUsername();
        }
    }

    /**
     * Load a user from the database.
     *
     * @param username the username to load
     * @return a user object
     * @throws SQLException          if a SQL error occurs
     * @throws UserNotFoundException if a user cannot be found
     */
    private User loadUser(String username) throws SQLException, UserNotFoundException {
        UserDAO database = DAOFactory.getUserDao();
        this.currentUser = database.get(username);
        return currentUser;
    }

    /**
     * Load the user view.
     *
     * @param user user to be loaded.
     */
    private void loadUserView(User user) {

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
    }

    /**
     * Closes the current stage.
     */
    private void closeCurrentStage() {
        Stage currentStage = (Stage) usernameField.getScene().getWindow();
        currentStage.close();
    }

    /**
     * Scene change to create account view.
     *
     * @param event clicking on the create new account link.
     * @throws IOException thrown when the window can not be created.
     */
    @FXML
    private void handleCreateNewAccountLinkClicked(ActionEvent event) throws IOException {
        String scene = "/view/ProfileCreate.fxml";
        String title = "Create profile";
        showScene(event, scene, title, false);
    }

    /**
     * Displays a password prompt window when a user logs in and doesn't have a password set.
     *
     * @param currentProfile the current profile being logged in.
     * @param event          login button clicked mouse event.
     * @throws IOException thrown when the window can not be created.
     */
    private void showPasswordPromptWindow(Profile currentProfile, ActionEvent event)
            throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/PasswordPrompt.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        PasswordPrompt view = fxmlLoader.getController();
        view.initialize(currentProfile);

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Set up password");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setAlwaysOnTop(true);
        stage.centerOnScreen();

        stage.show();
    }

    /**
     * Handle enter button being used to login.
     *
     * @param event enter key pressed event.
     */
    @FXML
    private void onEnter(ActionEvent event) {
        handleLoginButtonClicked(event);
    }
}
