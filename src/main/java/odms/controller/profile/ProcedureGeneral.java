package odms.controller.profile;

import java.util.List;
import javafx.fxml.FXML;
import odms.model.profile.Profile;
import odms.view.profile.ProceduresDisplay;

import java.time.LocalDate;
import java.util.ArrayList;

public class ProcedureGeneral {
    ProceduresDisplay view;

    public ProcedureGeneral(ProceduresDisplay v) {
        view = v;
    }

    /**
     * Given a procedure, will return whether the procedure has past
     *
     * @param procedure the procedure to check
     * @return whether the procedure has past
     */
    public boolean isPreviousProcedure(odms.model.profile.Procedure procedure) {
        return procedure.getDate().isBefore(LocalDate.now());
    }

    /**
     * Removes the selected procedure
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
     * Remove a procedure from the current profile
     *
     * @param procedure the procedure to remove
     */
    private void removeProcedure(odms.model.profile.Procedure procedure, Profile profile) {
        List<odms.model.profile.Procedure> procedures = profile.getAllProcedures();
        procedures.remove(procedure);
    }

    /**
     * Gets all the previous procedures
     *
     * @return previous procedures
     */
    public List<odms.model.profile.Procedure> getPreviousProcedures() {
        Profile profile = view.getProfile();
        ArrayList<odms.model.profile.Procedure> prevProcedures = new ArrayList<>();
        List<odms.model.profile.Procedure> procedures = profile.getAllProcedures();
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
     * Gets all the pending procedures
     *
     * @return pending procedures
     */
    public List<odms.model.profile.Procedure> getPendingProcedures() {
        Profile profile = view.getProfile();
        List<odms.model.profile.Procedure> pendingProcedures = new ArrayList<>();
        List<odms.model.profile.Procedure> procedures = profile.getAllProcedures();
        if (procedures != null) {
            for (odms.model.profile.Procedure procedure : procedures) {
                if (procedure.getDate().isAfter(LocalDate.now())) {
                    pendingProcedures.add(procedure);
                }
            }
        }
        return pendingProcedures;
    }
}
