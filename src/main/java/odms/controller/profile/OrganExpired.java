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

    public OrganExpired(odms.view.profile.OrganExpired view) {
        this.view = view;
    }


    public List<ExpiredOrgan> getExpiredOrgans(Profile profile) {
        return DAOFactory.getOrganDao().getExpired(profile);
    }

    public void revertExpired(Integer profileId, String organ) {
        DAOFactory.getOrganDao().revertExpired(profileId, organ);
    }

}
