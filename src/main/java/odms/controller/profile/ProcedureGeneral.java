package odms.controller.profile;

import java.util.List;
import javafx.fxml.FXML;
import odms.controller.database.DAOFactory;
import odms.controller.database.ProcedureDAO;
import odms.model.profile.Profile;
import odms.view.profile.ProceduresDisplay;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Controller for procedure tab in profile view.
 */
public class ProcedureGeneral {
    private ProceduresDisplay view;

    /**
     * constructor for Procedure general. Sets the view variable.
     * @param v the view component of the controller
     */
    public ProcedureGeneral(ProceduresDisplay v) {
        view = v;
    }

    /**
     * Given a procedure, will return whether the procedure has past.
     *
     * @param procedure the procedure to check
     * @return whether the procedure has past
     */
    public boolean isPreviousProcedure(odms.model.profile.Procedure procedure) {
        return procedure.getDate().isBefore(LocalDate.now());
    }

    /**
     * Removes the selected procedure.
     */
    @FXML
    public void delete() {

        odms.model.profile.Procedure procedure = view.getSelectedPendingProcedure();
        if (procedure == null) {
            procedure = view.getSelectedPreviousProcedure();
        }
        if (procedure == null) {
            return;
        }
        removeProcedure(procedure, view.getProfile());
    }

    /**
     * Remove a procedure from the current profile.
     *
     * @param procedure the procedure to remove
     * @param profile Profile object of the current profile
     */
    public void removeProcedure(odms.model.profile.Procedure procedure, Profile profile) {
        List<odms.model.profile.Procedure> procedures = profile.getAllProcedures();
        procedures.remove(procedure);

        ProcedureDAO procedureDAO = DAOFactory.getProcedureDao();
        procedureDAO.remove(procedure);
    }

    /**
     * Gets all the previous procedures.
     *
     * @param profile Profile object of the current profile
     * @return previous procedures
     */
    public List<odms.model.profile.Procedure> getPreviousProcedures(Profile profile) {
        ArrayList<odms.model.profile.Procedure> prevProcedures = new ArrayList<>();
        List<odms.model.profile.Procedure> procedures = profile.getPreviousProcedures();
        if (procedures != null) {
            for (odms.model.profile.Procedure procedure : procedures) {
                if (procedure.getDate().isBefore(LocalDate.now())) {
                    prevProcedures.add(procedure);
                }
            }
        }
        return prevProcedures;
    }

    /**
     * Gets all the pending procedures.
     *
     * @param profile Profile object of the current profile
     * @return pending procedures
     */
    public List<odms.model.profile.Procedure> getPendingProcedures(Profile profile) {
        List<odms.model.profile.Procedure> pendingProcedures = new ArrayList<>();
        List<odms.model.profile.Procedure> procedures = profile.getPendingProcedures();
        if (procedures != null) {
            for (odms.model.profile.Procedure procedure : procedures) {
                if (procedure.getDate().isAfter(LocalDate.now()) ||
                        procedure.getDate().isEqual(LocalDate.now())) {
                    pendingProcedures.add(procedure);
                }
            }
        }
        return pendingProcedures;
    }
}
