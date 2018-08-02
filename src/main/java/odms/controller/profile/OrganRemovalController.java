package odms.controller.profile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import odms.controller.CommonController;
import odms.controller.history.HistoryController;
import odms.model.enums.OrganEnum;
import odms.model.history.History;
import odms.view.profile.OrganRemovalView;

public class OrganRemovalController extends CommonController {
    OrganRemovalView view;

    public OrganRemovalController(OrganRemovalView view) {
        this.view = view;
    }

    /**
     * Confirms the changes made to the organs required and stores the reason given for this
     * change.
     *
     */
    public void confirm() {
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
                view.getCurrentProfile().setDateOfDeath(view.getDOD());
                Set<OrganEnum> organsRequired = new HashSet<>(
                        view.getCurrentProfile().getOrgansRequired()
                );
                removeOrgansRequired(organsRequired);
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
    public void removeOrgansRequired(Set<OrganEnum> organs) {
        //todo fix generate update info into simpler solution
        //generateUpdateInfo("organsReceiving");
        for (OrganEnum organ : organs) {
            view.getCurrentProfile().getOrgansRequired().remove(organ);
            History action = new History(
                    "profile ",
                    view.getCurrentProfile().getId(),
                    "removed required",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );

            HistoryController.updateHistory(action);
        }
    }
}
