package odms.controller.profile;

import odms.controller.history.HistoryController;
import odms.model.enums.OrganEnum;
import odms.model.history.History;
import odms.model.profile.OrganConflictException;
import odms.model.profile.Profile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ProfileOrganControllerTODO {







    /**
     * Add an organ to the set of received organs. If the organ exists in the receiving set, remove
     * it.
     *
     * @param organ to be added
     */
    private void addOrganReceived(OrganEnum organ) {
        if (this.organsRequired.contains(organ)) {
            this.organsRequired.remove(organ);
        }

        this.organsReceived.add(organ);
    }

    /**
     * Add a set of organs to the set of received organs.
     *
     * @param organs set to be added
     */
    public void addOrgansReceived(Set<OrganEnum> organs) {
        generateUpdateInfo("organsReceived");

        for (OrganEnum organ : organs) {
            addOrganReceived(organ);
            History action = new History("profile ", this.getId(),
                    "received", organ.getNamePlain(), -1, LocalDateTime.now());
            HistoryController.updateHistory(action);
        }
    }







    public void removeOrganDonated(OrganEnum organ) {
        if (this.organsDonated.contains(organ)) {
            this.organsDonated.remove(organ);
        }

        this.organsDonating.add(organ);
    }






}
