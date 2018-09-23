package odms.controller.user;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.locations.Hospital;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.controller.CommonController;
import odms.controller.database.DAOFactory;
import odms.controller.database.locations.HospitalDAO;
import odms.controller.database.organ.OrganDAO;
import odms.controller.database.procedure.ProcedureDAO;
import odms.controller.email.Email;

/**
 * The controller for the scheduling a donation.
 */
@Slf4j
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
     * Gets the organs that can be donated between two users. NOTE: This function assumes that the
     * two profiles are compatible.
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
                generateSummary(), dateTime, generateDescription(), organ, hospital);

        if (procedureDAO.add(donor, procedure) &&
                procedureDAO.add(receiver, procedure)) {
            updateOrgans();
        }

        donor.addProcedure(procedure);
        receiver.addProcedure(procedure);
        sendEmails();
    }

    /**
     * Updates the profiles donating and required organs accordingly.
     */
    private void updateOrgans() {
        OrganEnum organ = view.getSelectedOrgan();
        Profile donor = view.getDonor();
        Profile receiver = view.getReceiver();
        OrganDAO organDAO = DAOFactory.getOrganDao();
        organDAO.removeDonating(donor, organ);
        organDAO.addDonation(donor, organ);
        organDAO.removeRequired(receiver, organ);
        organDAO.addReceived(receiver, organ);
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

        return donor.getFullName() + " is donating their " + '\n' +
                organ.getName() + " to " + receiver.getFullName() + "\n Hospital: " +
                view.getSelectedHospital().getName();
    }

    /**
     * Generates and sends an email to the people in the match.
     */
    private void sendEmails() {
        String subject = "Organ Donation Match";
        if (view.getDonorCheck()) {
            String message = generateMessage(true);
            Email.sendMessage(view.getDonor().getEmail(), message, subject);
        }
        if (view.getReceiverCheck()) {
            String message = generateMessage(false);
            Email.sendMessage(view.getReceiver().getEmail(), message, subject);
        }
    }

    /**
     * Generates an email for the specified role.
     *
     * @param donor true if the message is for a donor.
     * @return The message string.
     */
    private String generateMessage(Boolean donor) {
        String newLine = "\n";
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        String role;

        if (donor) {
            role = "receiver.";
        } else {
            role = "donor.";
        }

        return "Hi " + view.getDonor().getFullName() + newLine + newLine +
                "Congratulations you have been matched with an organ " + role + newLine + newLine +
                "Procedure Details:" + newLine +
                "\tTime: " + view.getDatePickerValue().format(format) + newLine +
                "\tLocation: " + view.getSelectedHospital().getName() + newLine +
                "\tOrgan: " + view.getSelectedOrgan() + newLine + newLine +
                "Many Thanks" + newLine +
                "The Team at Human Farm";
    }
}
