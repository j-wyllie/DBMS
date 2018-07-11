package odms.controller.profile;

import odms.controller.history.HistoryController;
import odms.model.enums.OrganEnum;
import odms.model.history.History;
import odms.model.profile.OrganConflictException;
import odms.model.profile.Profile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ProfileOrganControllerTODO {

    /**
     * Add an organ to the organs donate list.
     *
     * @param organ the organ the profile wishes to donate
     */
    public void addOrganDonating(OrganEnum organ) throws OrganConflictException {
        if (this.organsReceived.contains(organ)) {
            // A donor cannot donate an organ they've received.
            throw new OrganConflictException(
                    "profile has previously received " + organ,
                    organ
            );
        }
        this.organsDonating.add(organ);
    }

    /**
     * Add an organ to the organs required list.
     *
     * @param organ the organ the profile requires
     */
    public void addOrganRequired(OrganEnum organ) {//TODO Error Check
        this.setReceiver(true);
        this.organsRequired.add(organ);
    }

    /**
     * Add a set of organs that the profile requires to the required organs set.
     *
     * @param organs the set of organs to be received
     */
    public void addOrgansRequired(HashSet<OrganEnum> organs) {
        generateUpdateInfo("organsRequired");

        for (OrganEnum organ : organs) {
            addOrganRequired(organ);
            LocalDateTime now = LocalDateTime.now();
            History action = new History("profile", this.getId(), "required organ",
                    "" + organ.getNamePlain(), -1, now);
            HistoryController.updateHistory(action);
        }
    }

    /**
     * Add a set of organs to the list of organs that the profile wants to donate
     *
     * @param organs the set of organs to donate
     * @throws IllegalArgumentException if a bad argument is used
     * @throws OrganConflictException   if there is a conflicting organ
     */
    public void addOrgansDonating(Set<OrganEnum> organs)
            throws IllegalArgumentException, OrganConflictException {
        generateUpdateInfo("organsDonating");

        for (OrganEnum organ : organs) {
            if (this.organsDonating.contains(organ)) {
                throw new IllegalArgumentException(
                        "Organ " + organ + " already exists in donating list"
                );
            }
            this.addOrganDonating(organ);

            History action = new History("profile ", this.getId(), "set", organ.getNamePlain(),
                    -1, LocalDateTime.now());
            HistoryController.updateHistory(action);
        }
    }

    public static HashSet<OrganEnum> getOrgansRequired(Profile profile) {
        return organsRequired;
    }

    /**
     * Add an organ to the set of received organs. If the organ exists in the receiving set, remove
     * it.
     *
     * @param organ to be added
     */
    private void addOrganReceived(OrganEnum organ) {
        if (this.organsRequired.contains(organ)) {
            this.organsRequired.remove(organ);
        }

        this.organsReceived.add(organ);
    }

    /**
     * Add a set of organs to the set of received organs.
     *
     * @param organs set to be added
     */
    public void addOrgansReceived(Set<OrganEnum> organs) {
        generateUpdateInfo("organsReceived");

        for (OrganEnum organ : organs) {
            addOrganReceived(organ);
            History action = new History("profile ", this.getId(),
                    "received", organ.getNamePlain(), -1, LocalDateTime.now());
            HistoryController.updateHistory(action);
        }
    }

    /**
     * Remove a set of organs from the list of organs that the use wants to donate
     *
     * @param organs a set of organs to be removed
     */
    public void removeOrgansDonating(Set<OrganEnum> organs) {
        generateUpdateInfo("organsDonating");

        for (OrganEnum organ : organs) {
            this.organsDonating.remove(organ);
            History action = new History(
                    "profile ",
                    this.getId(),
                    "removed",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
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
        generateUpdateInfo("organsReceiving");

        for (OrganEnum organ : organs) {
            this.organsRequired.remove(organ);
            History action = new History(
                    "profile ",
                    this.getId(),
                    "removed required",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );

            HistoryController.updateHistory(action);
        }
    }

    public void removeOrganReceived(OrganEnum organ) {
        if (this.organsReceived.contains(organ)) {
            this.organsReceived.remove(organ);
        }

        this.organsRequired.add(organ);
    }

    public void removeOrganDonated(OrganEnum organ) {
        if (this.organsDonated.contains(organ)) {
            this.organsDonated.remove(organ);
        }

        this.organsDonating.add(organ);
    }

    /**
     * Add an organ to the list of donated organsDonating. If the organ exists in the donating list,
     * remove it from the donating list.
     *
     * @param organ the organ to be added
     */
    public void addOrganDonated(OrganEnum organ) {
        if (this.organsDonating.contains(organ)) {
            this.organsDonating.remove(organ);
        }

        this.organsDonated.add(organ);
    }

    /**
     * Add a set of organsDonating to the list of organsDonating that the profile has donated
     *
     * @param organs a set of organsDonating that the profile has donated
     */
    public void addOrgansDonated(Set<OrganEnum> organs) {
        generateUpdateInfo("pastDonations");

        for (OrganEnum organ : organs) {
            this.organsDonated.add(organ);
            History action = new History(
                    "profile ",
                    this.getId(),
                    "donated",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );
            HistoryController.updateHistory(action);
        }
    }

    /**
     * Remove a set of organs from the list of organs that the profile has donated
     *
     * @param organs a set of organs to remove from the list
     */
    public void removeOrgansDonated(Set<OrganEnum> organs) {
        generateUpdateInfo("organsDonated");

        for (OrganEnum organ : organs) {
            this.organsDonated.remove(organ);
            History action = new History(
                    "profile ",
                    this.getId(),
                    "removed donated",
                    organ.getNamePlain(),
                    -1,
                    LocalDateTime.now()
            );
            HistoryController.updateHistory(action);
        }
    }
}
