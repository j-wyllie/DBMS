package odms.controller.profile;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.history.History;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;
import odms.controller.history.CurrentHistory;
import odms.commons.model.enums.OrganEnum;

/**
 * Control access for items around the organ lists.
 */
@Slf4j
public class OrganEdit extends CommonController {

    /**
     * Support function to detect organs removed from the selected list view.
     *
     * @param currentOrgans the current organ list to detect against
     * @param changedOrgans changed organs list to search with
     * @return a list of organs to remove from the profile
     */
    private Set<OrganEnum> findOrgansRemoved(Set<OrganEnum> currentOrgans,
            Set<OrganEnum> changedOrgans) {
        Set<OrganEnum> organsRemoved = new HashSet<>();
        for (OrganEnum organ : currentOrgans) {
            if (!changedOrgans.contains(organ)) {
                organsRemoved.add(organ);
            }
        }
        return organsRemoved;
    }

    public void caseDonated(Profile p, Set<OrganEnum> organsAdded) {
        Set<OrganEnum> organsRemoved;
        organsRemoved = findOrgansRemoved(
                p.getOrgansDonated(),
                organsAdded
        );
        addOrgansDonated(organsAdded,p);
        removeOrgansDonated(organsRemoved, p);
    }

    /**
     * Add a set of organsDonating to the list of organsDonating that the profile has donated.
     *
     * @param organs a set of organsDonating that the profile has donated
     */
    void addOrgansDonated(Set<OrganEnum> organs, Profile p) {
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("pastDonations",
                p);

        for (OrganEnum organ : organs) {
            if(!DAOFactory.getOrganDao().getDonations(p).contains(organ)) {
                DAOFactory.getOrganDao().addDonation(p, organ);
            }
            p.getOrgansDonated().add(organ);
            History action = new History(
                    "profile ",
                    p.getId(),
                    "donated",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );
            CurrentHistory.updateHistory(action);
        }
    }

    /**
     * Remove a set of organs from the list of organs that the profile has donated.
     *
     * @param organs a set of organs to remove from the list
     */
    public void removeOrgansDonated(Set<OrganEnum> organs, Profile p) {
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("organsDonated",
                p);

        for (OrganEnum organ : organs) {
            if(DAOFactory.getOrganDao().getDonations(p).contains(organ)) {
                DAOFactory.getOrganDao().removeDonation(p, organ);
            }
            p.getOrgansDonated().remove(organ);
            History action = new History(
                    "profile ",
                    p.getId(),
                    "removed donated",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );
            CurrentHistory.updateHistory(action);
        }
    }

    public void caseDonating(Profile p, Set<OrganEnum> organsAdded) {
        try {
            Set<OrganEnum> organsRemoved;
            p.setDonor(true);

            organsRemoved = findOrgansRemoved(
                    p.getOrgansDonating(),
                    organsAdded
            );

            addOrgansDonating(organsAdded, p);
            removeOrgansDonating(organsRemoved, p);
        } catch (OrganConflictException e) {
            AlertController.invalidOrgan(e.getOrgan());
        }
        p.updatedDonorStatus();
    }

    /**
     * Remove a set of organs from the list of organs that the use wants to donate.
     *
     * @param organs a set of organs to be removed
     */
    public void removeOrgansDonating(Set<OrganEnum> organs, Profile p) {
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo(
            "organsDonating",
                p
        );

        for (OrganEnum organ : organs) {
            if(DAOFactory.getOrganDao().getDonating(p).contains(organ)) {
                DAOFactory.getOrganDao().removeDonating(p, organ);
            }
            p.getOrgansDonating().remove(organ);
            DAOFactory.getOrganDao().removeDonating(p, organ);
            History action = new History(
                    "profile ",
                    p.getId(),
                    "removed",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );
            CurrentHistory.updateHistory(action);
        }
    }

    /**
     * Add a set of organs to the list of organs that the profile wants to donate.
     *
     * @param organs the set of organs to donate
     * @throws IllegalArgumentException if a bad argument is used
     * @throws OrganConflictException   if there is a conflicting organ
     */
    public void addOrgansDonating(Set<OrganEnum> organs, Profile p)
            throws IllegalArgumentException, OrganConflictException {
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo(
            "organsDonating",
                p
        );

        for (OrganEnum organ : organs) {
            if (!DAOFactory.getOrganDao().getDonating(p).contains(organ)) {
                if (DAOFactory.getOrganDao().getReceived(p).contains(organ)) {
                    // A donor cannot donate an organ they've received.
                    throw new OrganConflictException(
                            "profile has previously received " + organ,
                            organ
                    );
                }
                p.addOrganDonating(organ);
                DAOFactory.getOrganDao().addDonating(p, organ);
            }

            History action = new History(
                    "profile ",
                    p.getId(),
                    "set",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now());
            CurrentHistory.updateHistory(action);
        }
    }

    public void caseRequired(Profile p, Set<OrganEnum> organsAdded) {
        Set<OrganEnum> organsRemoved;
        p.setReceiver(true);

        organsRemoved = findOrgansRemoved(
                p.getOrgansRequired(),
                organsAdded
        );
        addOrgansRequired(organsAdded, p);
        removeOrgansRequired(organsRemoved, p);
        p.updatedReceiverStatus();
    }

    /**
     * Add a set of organs that the profile requires to the required organs set.
     *
     * @param organs the set of organs to be received
     */
    void addOrgansRequired(Set<OrganEnum> organs, Profile p) {
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo(
            "organsRequired",
                p
        );

        p.setReceiver(true);

        for (OrganEnum organ : organs) {
            p.getOrgansRequired().add(organ);
            if (!DAOFactory.getOrganDao().getRequired(p).contains(organ)) {
                DAOFactory.getOrganDao().addRequired(p, organ);
            }

            History action = new History(
                    "profile",
                    p.getId(),
                    "required organ",
                    "" + organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );
            CurrentHistory.updateHistory(action);
        }
    }

    /**
     * Remove a set of organs from the list of organs required.
     *
     * @param organs a set of organs to be removed
     */
    public void removeOrgansRequired(Set<OrganEnum> organs, Profile p) {
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo(
            "organsReceiving",
                p
        );

        for (OrganEnum organ : organs) {
            if(DAOFactory.getOrganDao().getRequired(p).contains(organ)) {
                DAOFactory.getOrganDao().removeRequired(p, organ);
            }
            p.getOrgansRequired().remove(organ);
            History action = new History(
                    "profile ",
                    p.getId(),
                    "removed required",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );

            CurrentHistory.updateHistory(action);
        }
    }

    public void saveOrgans(Profile p) {
        ProfileDAO database = DAOFactory.getProfileDao();
        try {
            database.update(p);

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

}
