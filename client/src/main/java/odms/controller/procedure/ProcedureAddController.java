package odms.controller.procedure;

import java.util.List;
import javafx.collections.ObservableList;
import odms.view.profile.ProfileAddProcedureView;
import odms.view.profile.ProfileDisplayViewTODO;
import odms.model.enums.OrganEnum;
import odms.model.profile.Procedure;

import java.time.LocalDate;

public class ProcedureAddController {

    private ProfileDisplayViewTODO controller;

    private ObservableList<OrganEnum> donatedOrgans;

    private ProfileAddProcedureView view;

    public ProcedureAddController(ProfileAddProcedureView v) {
        view = v;
    }

    public void add() throws IllegalArgumentException {
        Procedure procedure = parseProcedure();
        procedure.setOrgansAffected(view.getAffectedOrgansListView());
        addProcedure(procedure);
    }

    private Procedure parseProcedure() {
        String summary = view.getSummaryField();
        LocalDate dateOfProcedure = view.getDateOfProcedureDatePicker();
        String longDescription = view.getDescriptionField();

        Procedure procedure;

        // validate procedure
        LocalDate dob = view.getSearchedDonor().getDateOfBirth();
        if (dob.isAfter(dateOfProcedure)) {
            throw new IllegalArgumentException();
        }
        if (longDescription.equals("")) {
            procedure = new Procedure(summary, dateOfProcedure);

        } else {
            procedure = new Procedure(summary, dateOfProcedure, longDescription);
        }

        return procedure;
    }

    /**
     * Add a procedure to the current profile
     *
     * @param procedure
     */
    private void addProcedure(Procedure procedure) {
        List<Procedure> procedures = view.getSearchedDonor().getAllProcedures();
        procedures.add(procedure);
    }



}
