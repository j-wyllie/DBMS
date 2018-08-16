package odms.controller.profile;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.database.DAOFactory;
import odms.controller.database.ProfileDAO;
import odms.controller.history.CurrentHistory;
import odms.model.enums.OrganEnum;
import odms.model.profile.ExpiredOrgan;
import odms.model.profile.OrganConflictException;
import odms.model.profile.Profile;

/**
 * Control access for items around the organ lists.
 */
public class OrganExpired extends CommonController {
    private odms.view.profile.OrganExpired view;

    /**
     * Constructs the OrganExpired controller. Sets the view.
     * @param view The OrganExpired view
     */
    public OrganExpired(odms.view.profile.OrganExpired view) {
        this.view = view;
    }

    /**
     * Gets all manually expired organs for the given profile.
     * @param profile profile object to get expired organs for
     * @return List of expiredOrgan objects
     * @throws SQLException If there was an getting the data from the database
     */
    public List<ExpiredOrgan> getExpiredOrgans(Profile profile) throws SQLException {
        return DAOFactory.getOrganDao().getExpired(profile);
    }

    /**
     * Reverts the manual expiry override on a particular organ.
     * @param profileId The profile id, to identify the correct organ to revert
     * @param organ The name of the the organ to revert
     * @throws SQLException If there was an getting the data from the database
     */
    public void revertExpired(Integer profileId, String organ) throws SQLException {
        DAOFactory.getOrganDao().revertExpired(profileId, organ);
    }

}
