package odms.controller.profile;

import java.util.Set;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;

public class OrganCLIService {

    private OrganCLIService() {
        throw new UnsupportedOperationException();
    }

    public static void removeOrgansDonating(Set<OrganEnum> organs, Profile p) {
        for (OrganEnum organ : organs) {
            p.getOrgansDonating().remove(organ);
        }
    }

    /**
     * Add a set of organs to the list of organs that the profile wants to donate
     *
     * @param organs the set of organs to donate
     * @throws IllegalArgumentException if a bad argument is used
     * @throws OrganConflictException   if there is a conflicting organ
     */
    public static void addOrgansDonating(Set<OrganEnum> organs, Profile p)
            throws IllegalArgumentException, OrganConflictException {
        for (OrganEnum organ : organs) {
            if (p.getOrgansDonating().contains(organ)) {
                throw new IllegalArgumentException(
                        "Organ " + organ + " already exists in donating list"
                );
            }
            addOrganDonating(organ, p);
        }
    }

    /**
     * Add an organ to the organs donate list.
     *
     * @param organ the organ the profile wishes to donate
     */
    private static void addOrganDonating(OrganEnum organ, Profile p) throws OrganConflictException {
        if (p.getOrgansReceived().contains(organ)) {
            // A donor cannot donate an organ they've received.
            throw new OrganConflictException(
                    "profile has previously received " + organ,
                    organ
            );
        }
        p.getOrgansDonating().add(organ);
    }

    /**
     * Add a set of organsDonating to the list of organsDonating that the profile has donated
     *
     * @param organs a set of organsDonating that the profile has donated
     */
    public static void addOrgansDonated(Set<OrganEnum> organs, Profile p) {
        for (OrganEnum organ : organs) {
            p.getOrgansDonated().add(organ);
        }
    }

    /**
     * Add an organ to the organs required list.
     *
     * @param organ the organ the profile requires
     */
    private static void addOrganRequired(OrganEnum organ, Profile p) {
        p.setReceiver(true);
        p.getOrgansRequired().add(organ);
    }

    /**
     * Add a set of organs that the profile requires to the required organs set.
     *
     * @param organs the set of organs to be received
     */
    public static void addOrgansRequired(Set<OrganEnum> organs, Profile p) {
        for (OrganEnum organ : organs) {
            addOrganRequired(organ, p);
        }
    }

    /**
     * Remove a set of organs from the list of organs required.
     *
     * @param organs a set of organs to be removed
     */
    public static void removeOrgansRequired(Set<OrganEnum> organs, Profile p) {
        for (OrganEnum organ : organs) {
            p.getOrgansRequired().remove(organ);
        }
    }
}
