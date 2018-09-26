package odms.controller.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    /**
     * Searches the transplant waiting list based on the search string entered in the search field.
     * @param receiverList list of receivers
     * @param searchString search string to compare to profile names
     * @return List of profile, organ pairs
     */
    public List<Entry<Profile, OrganEnum>> searchWaitingList(
            List<Entry<Profile, OrganEnum>> receiverList, String searchString) {
        List<Entry<Profile, OrganEnum>> receivers = new ArrayList<>();

        if (!searchString.equals("")) {
            for (Map.Entry<Profile, OrganEnum> p: receiverList) {
                if (p.getKey().getFullName().toLowerCase().contains(searchString.toLowerCase())) {
                    receivers.add(p);
                }
            }
            return receivers;
        } else {
            return receiverList;
        }
    }
}
