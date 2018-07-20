package odms.controller.profile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.history.HistoryController;
import odms.controller.profile.ProfileOrganRemovalController;
import odms.model.enums.OrganEnum;
import odms.model.enums.OrganSelectEnum;
import odms.model.history.History;
import odms.model.profile.OrganConflictException;
import odms.model.profile.Profile;
import odms.view.profile.ProfileOrganEditView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class ProfileOrganEditController extends CommonController {
    ProfileOrganEditView view;
    OrganSelectEnum windowType;
    public ProfileOrganEditController(ProfileOrganEditView v) {
        view = v;
    }

    /**
     * Support function to detect organs removed from the selected list view.
     *
     * @param currentOrgans the current organ list to detect against
     * @param changedOrgans changed organs list to search with
     * @return a list of organs to remove from the profile
     */
    private HashSet<OrganEnum> findOrgansRemoved(HashSet<OrganEnum> currentOrgans,
            HashSet<OrganEnum> changedOrgans) {
        HashSet<OrganEnum> organsRemoved = new HashSet<>();
        for (OrganEnum organ : currentOrgans) {
            if (!changedOrgans.contains(organ)) {
                organsRemoved.add(organ);
            }
        }
        return organsRemoved;
    }

    public void caseDonated() {
        HashSet<OrganEnum> organsRemoved;
        Profile currentProfile = view.getCurrentProfile();
        organsRemoved = findOrgansRemoved(
                currentProfile.getOrgansDonated(),
                view.getOrgansAdded()
        );
        addOrgansDonated(view.getOrgansAdded());
        removeOrgansDonated(organsRemoved);
    }
    /**
     * Add a set of organsDonating to the list of organsDonating that the profile has donated
     *
     * @param organs a set of organsDonating that the profile has donated
     */
    public void addOrgansDonated(Set<OrganEnum> organs) {
        //todo fix generate update info
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("pastDonations",view.getCurrentProfile());

        for (OrganEnum organ : organs) {
            //todo might need to change to addOrganDonated
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
     * Remove a set of organs from the list of organs that the profile has donated
     *
     * @param organs a set of organs to remove from the list
     */
    public void removeOrgansDonated(Set<OrganEnum> organs) {
        //todo fix generate update info
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("organsDonated",view.getCurrentProfile());

        for (OrganEnum organ : organs) {
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

    public void caseDonating() {
        try {
            HashSet<OrganEnum> organsRemoved;
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
    }

    /**
     * Remove a set of organs from the list of organs that the use wants to donate
     *
     * @param organs a set of organs to be removed
     */
    public void removeOrgansDonating(Set<OrganEnum> organs) {
        //todo generateupdateinfo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("organsDonating",view.getCurrentProfile());

        for (OrganEnum organ : organs) {
            view.getCurrentProfile().getOrgansDonating().remove(organ);
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
     * Add a set of organs to the list of organs that the profile wants to donate
     *
     * @param organs the set of organs to donate
     * @throws IllegalArgumentException if a bad argument is used
     * @throws OrganConflictException   if there is a conflicting organ
     */
    public void addOrgansDonating(Set<OrganEnum> organs)
            throws IllegalArgumentException, OrganConflictException {
        //todo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("organsDonating",view.getCurrentProfile());

        for (OrganEnum organ : organs) {
            if (view.getCurrentProfile().getOrgansDonating().contains(organ)) {
                throw new IllegalArgumentException(
                        "Organ " + organ + " already exists in donating list"
                );
            }
            addOrganDonating(organ);

            History action = new History("profile ", view.getCurrentProfile().getId(), "set", organ.getNamePlain(),
                    -1, LocalDateTime.now());
            HistoryController.updateHistory(action);
        }
    }

    /**
     * Add an organ to the organs donate list.
     *
     * @param organ the organ the profile wishes to donate
     */
    public void addOrganDonating(OrganEnum organ) throws OrganConflictException {
        if (view.getCurrentProfile().getOrgansReceived().contains(organ)) {
            // A donor cannot donate an organ they've received.
            throw new OrganConflictException(
                    "profile has previously received " + organ,
                    organ
            );
        }
        view.getCurrentProfile().getOrgansDonating().add(organ);
    }

    public void caseRequired() {
        HashSet<OrganEnum> organsRemoved;
        view.getCurrentProfile().setReceiver(true);

        organsRemoved = findOrgansRemoved(
                view.getCurrentProfile().getOrgansRequired(),
                view.getOrgansAdded()
        );
        addOrgansRequired(view.getOrgansAdded());
        removeOrgansRequired(organsRemoved);
    }

    /**
     * Add an organ to the organs required list.
     *
     * @param organ the organ the profile requires
     */
    public void addOrganRequired(OrganEnum organ) {//TODO Error Check
        view.getCurrentProfile().setReceiver(true);
        view.getCurrentProfile().getOrgansRequired().add(organ);
    }

    /**
     * Add a set of organs that the profile requires to the required organs set.
     *
     * @param organs the set of organs to be received
     */
    public void addOrgansRequired(HashSet<OrganEnum> organs) {
        //todo
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("organsRequired",view.getCurrentProfile());

        for (OrganEnum organ : organs) {
            addOrganRequired(organ);
            LocalDateTime now = LocalDateTime.now();
            History action = new History("profile", view.getCurrentProfile().getId(), "required organ",
                    "" + organ.getNamePlain(), -1, now);
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
        ProfileGeneralControllerTODOContainsOldProfileMethods.generateUpdateInfo("organsReceiving",view.getCurrentProfile());
        for (OrganEnum organ : organs) {
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

    public void setWindowType(OrganSelectEnum windowType) {
        this.windowType = windowType;
    }

    public OrganSelectEnum getWindowType() {
        return windowType;
    }
}
