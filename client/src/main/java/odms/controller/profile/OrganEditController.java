package odms.controller.profile;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.database.DAOFactory;
import odms.controller.database.ProfileDAO;
import odms.controller.history.HistoryController;
import odms.model.enums.OrganEnum;
import odms.model.history.History;
import odms.model.profile.OrganConflictException;
import odms.view.profile.OrganEditView;

/**
 * Control access for items around the organ lists.
 */
public class OrganEditController extends CommonController {
    private OrganEditView view;

    public OrganEditController(OrganEditView view) {
        this.view = view;
    }

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

    // todo rename to something meaningful
    public void caseDonated() {
        Set<OrganEnum> organsRemoved;
        organsRemoved = findOrgansRemoved(
                view.getCurrentProfile().getOrgansDonated(),
                view.getOrgansAdded()
        );
        addOrgansDonated(view.getOrgansAdded());
        removeOrgansDonated(organsRemoved);
    }

    /**
     * Add a set of organsDonating to the list of organsDonating that the profile has donated.
     *
     * @param organs a set of organsDonating that the profile has donated
     */
    public void addOrgansDonated(Set<OrganEnum> organs) {
        //todo fix generate update info
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("pastDonations",
                view.getCurrentProfile());

        for (OrganEnum organ : organs) {
            //todo might need to change to addOrganDonated
            if(!DAOFactory.getOrganDao().getDonations(view.getCurrentProfile()).contains(organ)) {
                DAOFactory.getOrganDao().addDonation(view.getCurrentProfile(), organ);
            }
            view.getCurrentProfile().getOrgansDonated().add(organ);
            History action = new History(
                    "profile ",
                    view.getCurrentProfile().getId(),
                    "donated",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );
            HistoryController.updateHistory(action);
        }
    }

    /**
     * Remove a set of organs from the list of organs that the profile has donated.
     *
     * @param organs a set of organs to remove from the list
     */
    public void removeOrgansDonated(Set<OrganEnum> organs) {
        //todo fix generate update info
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("organsDonated",
                view.getCurrentProfile());

        for (OrganEnum organ : organs) {
            if(DAOFactory.getOrganDao().getDonations(view.getCurrentProfile()).contains(organ)) {
                DAOFactory.getOrganDao().removeDonation(view.getCurrentProfile(), organ);
            }
            view.getCurrentProfile().getOrgansDonated().remove(organ);
            History action = new History(
                    "profile ",
                    view.getCurrentProfile().getId(),
                    "removed donated",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );
            HistoryController.updateHistory(action);
        }
    }

    // todo rename to something more meaningful
    public void caseDonating() {
        try {
            Set<OrganEnum> organsRemoved;
            view.getCurrentProfile().setDonor(true);

            organsRemoved = findOrgansRemoved(
                    view.getCurrentProfile().getOrgansDonating(),
                    view.getOrgansAdded()
            );

            view.getOrgansAdded().removeAll(view.getCurrentProfile().getOrgansDonating());
            addOrgansDonating(view.getOrgansAdded());
            removeOrgansDonating(organsRemoved);
        } catch (OrganConflictException e) {
            AlertController.invalidOrgan(e.getOrgan());
        }
        view.getCurrentProfile().updatedDonorStatus();
    }

    /**
     * Remove a set of organs from the list of organs that the use wants to donate.
     *
     * @param organs a set of organs to be removed
     */
    public void removeOrgansDonating(Set<OrganEnum> organs) {
        // todo generate update info
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo(
            "organsDonating",
                view.getCurrentProfile()
        );

        for (OrganEnum organ : organs) {
            if(DAOFactory.getOrganDao().getDonating(view.getCurrentProfile()).contains(organ)) {
                DAOFactory.getOrganDao().removeDonating(view.getCurrentProfile(), organ);
            }
            view.getCurrentProfile().getOrgansDonating().remove(organ);
            DAOFactory.getOrganDao().removeDonating(view.getCurrentProfile(), organ);
            History action = new History(
                    "profile ",
                    view.getCurrentProfile().getId(),
                    "removed",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );
            HistoryController.updateHistory(action);
        }
    }

    /**
     * Add a set of organs to the list of organs that the profile wants to donate.
     *
     * @param organs the set of organs to donate
     * @throws IllegalArgumentException if a bad argument is used
     * @throws OrganConflictException   if there is a conflicting organ
     */
    public void addOrgansDonating(Set<OrganEnum> organs)
            throws IllegalArgumentException, OrganConflictException {
        //todo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo(
            "organsDonating",
                view.getCurrentProfile()
        );

        for (OrganEnum organ : organs) {
            if(!DAOFactory.getOrganDao().getDonating(view.getCurrentProfile()).contains(organ)) {
                if(DAOFactory.getOrganDao().getReceived(view.getCurrentProfile()).contains(organ)){
                    // A donor cannot donate an organ they've received.
                    throw new OrganConflictException(
                            "profile has previously received " + organ,
                            organ
                    );
                }
                DAOFactory.getOrganDao().addDonating(view.getCurrentProfile(), organ);
            }

            History action = new History("profile ", view.getCurrentProfile().getId(), "set",
                    organ.getNamePlain(),
                    -1, LocalDateTime.now());
            HistoryController.updateHistory(action);
        }
    }

    // TODO what does this even mean??
    public void caseRequired() {
        Set<OrganEnum> organsRemoved;
        view.getCurrentProfile().setReceiver(true);

        organsRemoved = findOrgansRemoved(
                view.getCurrentProfile().getOrgansRequired(),
                view.getOrgansAdded()
        );
        addOrgansRequired(view.getOrgansAdded());
        removeOrgansRequired(organsRemoved);
        view.getCurrentProfile().updatedReceiverStatus();
    }

    /**
     * Add an organ to the organs required list.
     *
     * @param organ the organ the profile requires
     */
    public void addOrganRequired(OrganEnum organ) {
        view.getCurrentProfile().setReceiver(true);
        view.getCurrentProfile().getOrgansRequired().add(organ);
        if(!DAOFactory.getOrganDao().getRequired(view.getCurrentProfile()).contains(organ)){
            DAOFactory.getOrganDao().addRequired(view.getCurrentProfile(), organ);
        }

    }

    /**
     * Add a set of organs that the profile requires to the required organs set.
     *
     * @param organs the set of organs to be received
     */
    public void addOrgansRequired(Set<OrganEnum> organs) {
        //todo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo(
            "organsRequired",
                view.getCurrentProfile()
        );

        for (OrganEnum organ : organs) {
            if(!DAOFactory.getOrganDao().getRequired(view.getCurrentProfile()).contains(organ)){
                DAOFactory.getOrganDao().addRequired(view.getCurrentProfile(), organ);
            }
            //addOrganRequired(organ);
            LocalDateTime now = LocalDateTime.now();
            History action = new History(
                "profile",
                    view.getCurrentProfile().getId(),
                "required organ",
                "" + organ.getNamePlain(),
                -1,
                now
            );
            HistoryController.updateHistory(action);
        }
    }

    /**
     * Remove a set of organs from the list of organs required.
     *
     * @param organs a set of organs to be removed
     */
    public void removeOrgansRequired(Set<OrganEnum> organs) {
        //todo fix generate update info into simpler solution
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo(
            "organsReceiving",
                view.getCurrentProfile()
        );

        for (OrganEnum organ : organs) {
            if(DAOFactory.getOrganDao().getRequired(view.getCurrentProfile()).contains(organ)) {
                DAOFactory.getOrganDao().removeRequired(view.getCurrentProfile(), organ);
            }
            view.getCurrentProfile().getOrgansRequired().remove(organ);
            History action = new History(
                    "profile ",
                    view.getCurrentProfile().getId(),
                    "removed required",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );

            HistoryController.updateHistory(action);
        }
    }

    public void saveOrgans() {
        ProfileDAO database = DAOFactory.getProfileDao();
        try {
            database.update(view.getCurrentProfile());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}