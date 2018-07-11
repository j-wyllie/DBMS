package odms.controller.procedure;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import odms.model.profile.Profile;
import odms.view.profile.ProfileDetailedProcedureView;
import odms.view.profile.ProfileDisplayController;
import odms.controller.GuiMain;
import odms.controller.data.ProfileDataIO;
import odms.controller.history.HistoryController;
import odms.controller.history.RedoController;
import odms.controller.history.UndoController;
import odms.model.enums.OrganEnum;
import odms.model.history.History;
import odms.model.profile.Procedure;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static odms.controller.GuiMain.getCurrentDatabase;

public class ProcedureEditController {

    private ProfileDetailedProcedureView view;

    public ProcedureEditController(ProfileDetailedProcedureView v) {
        view = v;
    }

    public void save() throws IllegalArgumentException{
        Profile profile = view.getProfile();
        Procedure procedure = view.getCurrentProcedure();
        History action = new History("profile ", profile.getId(), "EDITED",
                "",
                profile.getAllProcedures().indexOf(procedure),
                LocalDateTime.now());
        String oldValues =
                " PREVIOUS(" + procedure.getSummary() + "," + procedure.getDate()
                        + "," +
                        procedure.getLongDescription() + ")" + " OLDORGANS"
                        + procedure.getOrgansAffected();
        procedure.setLongDescription(view.getDescEntry());
        procedure.setSummary(view.getSummaryEntry());

        // date validation
        LocalDate dateOfProcedure = view.getDateOfProcedure();
        LocalDate dob = profile.getDateOfBirth();
        if (dob.isAfter(dateOfProcedure)) {
            throw new IllegalArgumentException();
        } else {
            procedure.setDate(dateOfProcedure);
        }

        procedure.setOrgansAffected(view.getAffectedOrgansListView());
        String newValues =
                " CURRENT(" + procedure.getSummary() + "," + procedure.getDate() + ","
                        +
                        procedure.getLongDescription() + ")" + " NEWORGANS"
                        + procedure.getOrgansAffected();
        action.setHistoryData(oldValues + newValues);
        HistoryController.updateHistory(action);
    }
}
