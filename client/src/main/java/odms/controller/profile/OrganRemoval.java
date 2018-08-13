package odms.controller.profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import odms.commons.model.history.History;
import odms.commons.model.profile.Profile;
import odms.controller.CommonController;
import odms.controller.history.CurrentHistory;
import odms.commons.model.enums.OrganEnum;
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
                view.getCurrentProfile().setDateOfDeath(LocalDate.from(view.getDOD()));
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
            History action = new History(
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
