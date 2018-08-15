package odms.controller.profile;

import java.util.List;
import javafx.collections.ObservableList;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.controller.database.procedure.HttpProcedureDAO;
import odms.controller.database.procedure.ProcedureDAO;
import odms.view.profile.Display;
import odms.commons.model.enums.OrganEnum;
import odms.controller.database.DAOFactory;

import java.time.LocalDate;

/**
 * Controller for the add procedure scene.
 */
public class ProcedureAdd {

    private odms.view.profile.ProcedureAdd view;

    /**
     * constructor for the ProcedureAdd class. Sets the view variable.
     * @param v the view
     */
    public ProcedureAdd(odms.view.profile.ProcedureAdd v) {
        view = v;
    }

    /**
     * Add a procedure to the current profile.
     * @param p profile object of current profile
     * @throws IllegalArgumentException thrown if procedure date is before profiles dob
     */
    public void add(Profile p) throws IllegalArgumentException {
        Procedure procedure = parseProcedure();
        procedure.setOrgansAffected(view.getAffectedOrgansListView());
        addProcedure(procedure, p);
    }

    /**
     * Parses the procedure to create a Procedure object.
     * @return Procedure object
     */
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
     * Add a procedure to the current profile.
     *
     * @param procedure object containing all info about a certain procedure
     * @param profile profile object of current profile
     */
    public void addProcedure(Procedure procedure, Profile profile) {
        ProcedureDAO procedureDAO = DAOFactory.getProcedureDao();
        procedureDAO.add(profile, procedure);
    }



}
