package odms.controller.procedure;

import java.util.List;
import javafx.fxml.FXML;
import odms.model.profile.Procedure;
import odms.model.profile.Profile;
import odms.view.profile.ProfileProceduresView;

import java.time.LocalDate;
import java.util.ArrayList;

public class ProcedureController {
    ProfileProceduresView view;

    public ProcedureController(ProfileProceduresView v) {
        view = v;
    }

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
        removeProcedure(procedure, view.getProfile());
    }

    /**
     * Remove a procedure from the current profile
     *
     * @param procedure the procedure to remove
     */
    private void removeProcedure(Procedure procedure, Profile profile) {
        List<Procedure> procedures = profile.getAllProcedures();
        procedures.remove(procedure);
    }

    /**
     * Gets all the previous procedures
     *
     * @return previous procedures
     */
    public List<Procedure> getPreviousProcedures() {
        Profile profile = view.getProfile();
        ArrayList<Procedure> prevProcedures = new ArrayList<>();
        List<Procedure> procedures = profile.getAllProcedures();
        if (procedures != null) {
            for (Procedure procedure : procedures) {
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
    public List<Procedure> getPendingProcedures() {
        Profile profile = view.getProfile();
        List<Procedure> pendingProcedures = new ArrayList<>();
        List<Procedure> procedures = profile.getAllProcedures();
        if (procedures != null) {
            for (Procedure procedure : procedures) {
                if (procedure.getDate().isAfter(LocalDate.now())) {
                    pendingProcedures.add(procedure);
                }
            }
        }
        return pendingProcedures;
    }
}
