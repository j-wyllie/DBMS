package odms.controller.profile;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.history.History;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.controller.database.DAOFactory;
import odms.controller.database.procedure.ProcedureDAO;
import odms.controller.history.CurrentHistory;
import odms.view.profile.ProcedureDetailed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Controller for the procedure edit scene.
 */
public class ProcedureEdit {
    private static final String COMMA = ",";
    private static final String CLOSE_BACKET = ")";

    private ProcedureDetailed view;
    private ProcedureDAO server = DAOFactory.getProcedureDao();

    /**
     * constructor for ProcedureEdit class. Sets the view component of the controller.
     * @param v the view
     */
    public ProcedureEdit(ProcedureDetailed v) {
        view = v;
    }

    /**
     * Saves the updated procedure.
     * @throws IllegalArgumentException thrown if procedure date is before profiles dob
     */
    public void save() {
        Profile profile = view.getProfile();
        Procedure procedure = view.getCurrentProcedure();

        History action = new History("profile ", profile.getId(), "EDITED",
                "",
                profile.getAllProcedures().indexOf(procedure),
                LocalDateTime.now());
        String oldValues =
                " PREVIOUS(" + procedure.getSummary() + COMMA + procedure.getDate() +
                        COMMA + procedure.getLongDescription() +
                        CLOSE_BACKET + " OLDORGANS" + procedure.getOrgansAffected();

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

        procedure.setHospital(view.getSelectedHospital());

        String newValues =
                " CURRENT(" + procedure.getSummary() + COMMA + procedure.getDate() + COMMA +
                        procedure.getLongDescription() + CLOSE_BACKET + " NEWORGANS" +
                        procedure.getOrgansAffected();
        action.setHistoryData(oldValues + newValues);
        CurrentHistory.updateHistory(action);

        server.update(profile, procedure);
    }

    public Set<OrganEnum> getDonatedOrgans() {
        return view.getProfile().getOrgansDonated();
    }
}
