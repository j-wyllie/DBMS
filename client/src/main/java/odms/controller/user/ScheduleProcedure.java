package odms.controller.user;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.controller.CommonController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The controller for the scheduling a donation.
 */
public class ScheduleProcedure extends CommonController {

    /**
     * Gets the organs that can be donated between two users.
     * NOTE: This function assumes that the two profiles are compatible.
     *
     * @param donor the donor
     * @param receiver the receiver
     * @return A list of organs
     */
    public List<OrganEnum> getDonatingOrgans(Profile donor, Profile receiver) {
        Set<OrganEnum> intersection = new HashSet<>(donor.getOrgansDonating());
        intersection.retainAll(receiver.getOrgansRequired());
        return new ArrayList<>(intersection);
    }
}
