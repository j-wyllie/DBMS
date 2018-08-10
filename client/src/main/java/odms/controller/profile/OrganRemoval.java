package odms.controller.profile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import odms.controller.CommonController;
import odms.controller.history.CurrentHistory;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;
import odms.view.profile.OrganRemove;

public class OrganRemoval extends CommonController {
    OrganRemove view;

    public OrganRemoval(OrganRemove view) {
        this.view = view;
    }

    /**
     * Confirms the changes made to the organs required and stores the reason given for this
     * change.
     *
     */
    public void confirm(Profile p) {
        String selection = view.getSelection();
        switch (selection) {
            case "Error":
                view.removeOrgan();
                break;

            case "No longer required":
                view.removeOrgan();
                break;

            case "Patient deceased":
                view.removeAllOrgans();
                //todo way to set time of death
                view.getCurrentProfile().setDateOfDeath(LocalDateTime.from(view.getDOD()));
                Set<OrganEnum> organsRequired = new HashSet<>(
                        view.getCurrentProfile().getOrgansRequired()
                );
                removeOrgansRequired(organsRequired, p);
                break;
            default:
                // noop
        }
    }

    /**
     * Remove a set of organs from the list of organs required.
     *
     * @param organs a set of organs to be removed
     */
    public void removeOrgansRequired(Set<OrganEnum> organs, Profile p) {
        //todo fix generate update info into simpler solution
        //generateUpdateInfo("organsReceiving");
        for (OrganEnum organ : organs) {
            p.getOrgansRequired().remove(organ);
            odms.model.history.History action = new odms.model.history.History(
                    "profile ",
                    p.getId(),
                    "removed required",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );

            CurrentHistory.updateHistory(action);
        }
    }
}
