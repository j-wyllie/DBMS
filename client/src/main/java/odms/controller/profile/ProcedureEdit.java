package odms.controller.profile;

import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.history.History;
import odms.view.profile.ProcedureDetailed;
import odms.controller.history.CurrentHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProcedureEdit {

    private ProcedureDetailed view;

    public ProcedureEdit(ProcedureDetailed v) {
        view = v;
    }

    public void save() throws IllegalArgumentException {
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
        CurrentHistory.updateHistory(action);
    }
}
