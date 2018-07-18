package odms.controller.profile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import odms.controller.CommonController;
import odms.controller.history.HistoryController;
import odms.model.enums.OrganEnum;
import odms.model.history.History;
import odms.model.profile.Profile;
import odms.view.profile.ProfileOrganEditView;
import odms.view.profile.ProfileOrganRemovalView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ProfileOrganRemovalController extends CommonController{
    ProfileOrganRemovalView view;

    public ProfileOrganRemovalController(ProfileOrganRemovalView v) {
        view = v;
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
