package odms.controller.profile;

import java.util.List;
import javafx.collections.ObservableList;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.controller.database.procedure.HttpProcedureDAO;
import odms.view.profile.Display;
import odms.commons.model.enums.OrganEnum;

import java.time.LocalDate;

public class ProcedureAdd {

    private Display controller;

    private ObservableList<OrganEnum> donatedOrgans;

    private odms.view.profile.ProcedureAdd view;

    public ProcedureAdd(odms.view.profile.ProcedureAdd v) {
        view = v;
    }

    public void add(Profile p) throws IllegalArgumentException {
        Procedure procedure = parseProcedure();
        procedure.setOrgansAffected(view.getAffectedOrgansListView());
        addProcedure(procedure, p);
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
    public void addProcedure(Procedure procedure, Profile profile) {
        HttpProcedureDAO httpProcedureDAO = new HttpProcedureDAO();
        httpProcedureDAO.add(profile, procedure);
    }



}
