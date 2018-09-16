package odms.controller.user;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.locations.Hospital;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.controller.CommonController;
import odms.controller.database.DAOFactory;
import odms.controller.database.locations.HospitalDAO;
import odms.controller.database.procedure.ProcedureDAO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The controller for the scheduling a donation.
 */
public class ScheduleProcedure extends CommonController {

    private odms.view.user.ScheduleProcedure view;

    /**
     * Sets the view.
     *
     * @param view The view.
     */
    public void setView(odms.view.user.ScheduleProcedure view) {
        this.view = view;
    }

    /**
     * Gets the organs that can be donated between two users.
     * NOTE: This function assumes that the two profiles are compatible.
     *
     * @return A list of organs
     */
    public List<OrganEnum> getDonatingOrgans() {
        Profile donor = view.getDonor();
        Profile receiver = view.getReceiver();
        Set<OrganEnum> intersection = new HashSet<>(donor.getOrgansDonating());
        intersection.retainAll(receiver.getOrgansRequired());
        return new ArrayList<>(intersection);
    }

    /**
     * Schedules the organ donation between the two profiles.
     *
     * @throws IllegalArgumentException When data is incorrectly entered.
     */
    public void scheduleProcedure() {
        LocalDateTime dateTime = view.getDatePickerValue();
        OrganEnum organ = view.getSelectedOrgan();
        Hospital hospital = view.getSelectedHospital();
        Profile donor = view.getDonor();
        Profile receiver = view.getReceiver();
        ProcedureDAO procedureDAO = DAOFactory.getProcedureDao();

        if (dateTime == null || organ == null || hospital == null) {
            throw new IllegalArgumentException("All values must be entered");
        }

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Date can not be before the current time");
        }

        Procedure procedure = new Procedure(
                generateSummary(), dateTime, generateDescription(), organ);
        procedureDAO.add(donor, procedure);
        procedureDAO.add(receiver, procedure);
    }

    /**
     * Generates a summary for the procedure.
     *
     * @return the summary string
     */
    private String generateSummary() {
        OrganEnum organ = view.getSelectedOrgan();
        return organ.getName() + " donation";
    }

    /**
     * Generates a description for the procedure.
     *
     * @return the description string
     */
    private String generateDescription() {
        Profile donor = view.getDonor();
        Profile receiver = view.getReceiver();
        OrganEnum organ = view.getSelectedOrgan();

        return donor.getFullName() + " is donating their " +
            organ.getName() + " to " + receiver.getFullName() + ".";
    }


    /**
     * Gets a list of hospitals from the database.
     *
     * @return the list of hospitals
     * @throws SQLException When an SQL error occurs
     */
    public List<Hospital> getHospitals() throws SQLException {
        HospitalDAO database = DAOFactory.getHospitalDAO();
        return database.getAll();
    }
}
