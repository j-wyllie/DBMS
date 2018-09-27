package odms.controller.profile;

import odms.Session;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.controller.AlertController;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;
import odms.view.LoginView;

import java.sql.SQLException;

/**
 * Controller for the password prompt window.
 */
public class PasswordPrompt {

    private odms.view.profile.PasswordPrompt view;

    /**
     * Sets the PasswordPrompt view.
     * @param v view to be set.
     */
    public PasswordPrompt(odms.view.profile.PasswordPrompt v) {
        this.view = v;
    }

    /**
     * Saves the users password by calling the profileDAO.
     */
    public void savePassword(LoginView loginView) throws SQLException {
        ProfileDAO profileDAO = DAOFactory.getProfileDao();
        Profile profile = (Profile) Session.getCurrentUser().getKey();
        profile.setPassword(view.passwordField.getText());
        Session.setCurrentUser(profile, UserType.PROFILE);
        profileDAO.savePassword(profile.getUsername(), view.passwordField.getText());

        if (loginView.isValidProfile()) {
            Profile currentProfile = loginView.loadProfile(((Profile) Session.getCurrentUser().getKey()).getUsername());
            loginView.loadProfileView(currentProfile);
        } else {
            AlertController.invalidUsernameOrPassword();
        }
    }
}
