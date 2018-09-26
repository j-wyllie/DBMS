package odms.controller.user;

import java.util.List;
import java.util.Map.Entry;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.controller.CommonController;
import odms.controller.database.DAOFactory;

/**
 * Controller for the transplant waiting list view.
 */
public class TransplantWaitingList extends CommonController {

    /**
     * Gets the list of organs needing to be received along with profile information.
     * @return list of organs and associated profiles
     */
    public List<Entry<Profile, OrganEnum>> getWaitingList() {
        return DAOFactory.getProfileDao().getAllReceiving();
    }

}
