package odms.controller.procedure;

import javafx.fxml.FXML;
import odms.model.profile.Procedure;
import odms.model.profile.Profile;
import odms.view.profile.ProfileProceduresViewTODO;

import java.time.LocalDate;
import java.util.ArrayList;

public class ProcedureController {
    ProfileProceduresViewTODO view;

    public ProcedureController(ProfileProceduresViewTODO v) {
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
    public void removeProcedure(Procedure procedure, Profile profile) {
        ArrayList<Procedure> procedures = profile.getAllProcedures();
        procedures.remove(procedure);
    }

    /**
     * Gets all the previous procedures
     *
     * @return previous procedures
     */
    public ArrayList<Procedure> getPreviousProcedures() {
        Profile profile = view.getProfile();
        ArrayList<Procedure> prevProcedures = new ArrayList<>();
        ArrayList<Procedure> procedures = profile.getAllProcedures();
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
    public ArrayList<Procedure> getPendingProcedures() {
        Profile profile = view.getProfile();
        ArrayList<Procedure> pendingProcedures = new ArrayList<>();
        ArrayList<Procedure> procedures = profile.getAllProcedures();
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
