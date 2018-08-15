package odms.controller.profile;

import java.util.List;
import odms.controller.database.DAOFactory;
import odms.controller.database.ProcedureDAO;
import odms.model.profile.Profile;
import odms.model.profile.Procedure;

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
     * @param p profile object of current profile
     */
    public void addProcedure(Procedure procedure, Profile p) {
        List<Procedure> procedures = p.getAllProcedures();
        procedures.add(procedure);

        ProcedureDAO procedureDAO = DAOFactory.getProcedureDao();
        procedureDAO.add(p, procedure);
    }



}
