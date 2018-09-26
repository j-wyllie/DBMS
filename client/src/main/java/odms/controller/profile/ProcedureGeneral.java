package odms.controller.profile;

import java.time.LocalDate;
import java.util.List;
import javafx.fxml.FXML;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.controller.database.DAOFactory;
import odms.controller.database.procedure.ProcedureDAO;
import odms.view.profile.ProceduresDisplay;

public class ProcedureGeneral {
    ProceduresDisplay view;

    public ProcedureGeneral(ProceduresDisplay v) {
        view = v;
    }
    private ProcedureDAO server = DAOFactory.getProcedureDao();

    /**
     * Given a procedure, will return whether the procedure has past
     *
     * @param procedure the procedure to check
     * @return whether the procedure has past
     */
    public boolean isPreviousProcedure(Procedure procedure) {
        return procedure.getDate().isBefore(LocalDate.now());
    }

    /**
     * Removes the selected procedure
     */
    @FXML
    public void delete() {
        Procedure procedure = view.getSelectedPendingProcedure();
        if (procedure == null) {
            procedure = view.getSelectedPreviousProcedure();
        }
        if (procedure == null) {
            return;
        }
        removeProcedure(procedure);
    }

    /**
     * Remove a procedure from the current profile
     *
     * @param procedure the procedure to remove
     */
    public void removeProcedure(Procedure procedure) {
        server.remove(procedure);
    }

    /**
     * Gets all the previous procedures
     *
     * @return previous procedures
     */
    public List<Procedure> getPreviousProcedures(Profile profile) {
        return server.getAll(profile, false);
    }

    /**
     * Gets all the pending procedures
     *
     * @return pending procedures
     */
    public List<Procedure> getPendingProcedures(Profile profile) {
        return server.getAll(profile, true);
    }
}
