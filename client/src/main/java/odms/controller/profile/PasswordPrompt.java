package odms.controller.profile;

import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;

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
    public void savePassword() {
        ProfileDAO profileDAO = DAOFactory.getProfileDao();
        profileDAO.savePassword(view.getCurrentProfile().getNhi(), view.passwordField.getText());
    }
}
