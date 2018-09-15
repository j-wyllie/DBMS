package odms.controller.user;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.controller.CommonController;

import java.time.LocalDate;
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
     *
     * Schedules the organ donation between the two profiles.
     * @throws IllegalArgumentException When data is incorrectly entered.
     */
    public void scheduleProcedure() {
        LocalDate date = view.getDatePickerValue();

        if (date == null) {
            throw new IllegalArgumentException("A valid date must be entered");
        }

        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Date can not be before today");
        }
    }
}
