package odms.controller.profile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.controller.database.DAOFactory;
import odms.controller.database.procedure.ProcedureDAO;

/**
 * Controller for the add procedure scene.
 */
public class ProcedureAdd {

    /**
     * Add a procedure to the current profile.
     * @param p profile object of current profile
     * @throws IllegalArgumentException thrown if procedure date is before profiles dob
     */
    public void add(Profile p, List<String> organs, Procedure procedure) throws IllegalArgumentException {
        List<OrganEnum> organEnums = new ArrayList<>();
        organs.forEach(string -> {
            OrganEnum organ = OrganEnum.valueOf(string.toUpperCase().replace(" ",  "_"));
            organEnums.add(organ);
        });
        procedure.setOrgansAffected(organEnums);
        addProcedure(procedure, p);
    }

    /**
     * Parses the procedure to create a Procedure object.
     * @return Procedure object
     */
    public Procedure parseProcedure(String summary, LocalDate dateOfProcedure, String longDescription, LocalDate dob) {
        Procedure procedure;
        // validate procedure
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
        profile.addProcedure(procedure);
        procedureDAO.add(profile, procedure);
    }



}
