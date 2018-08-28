package odms.controller.profile;

import odms.controller.CommonController;
import odms.controller.database.DAOFactory;

import java.sql.SQLException;

/**
 * Controller class for the OrganOverride view class.
 */
public class OrganOverride extends CommonController {
    private odms.view.profile.OrganOverride view;

    /**
     * Sets the view variable.
     * @param view OrganOverride view instance
     */
    public OrganOverride(odms.view.profile.OrganOverride view) {
        this.view = view;
    }

    /**
     * Takes details from view fields and sends a request to the DAO to override an organ.
     * @throws SQLException thrown if there are malformed details
     */
    public void confirm() throws SQLException {
        Integer isExpired = 1;
        DAOFactory.getOrganDao().setExpired(view.getCurrentProfile(), view.getCurrentOrgan(), isExpired,
                view.getReasonText(), view.getCurrentUser().getStaffID());
    }
}
