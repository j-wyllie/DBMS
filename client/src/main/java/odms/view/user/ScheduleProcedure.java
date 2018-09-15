package odms.view.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.view.CommonView;

/**
 * The view for scheduling an organ donation.
 */
public class ScheduleProcedure extends CommonView {

    private odms.controller.user.ScheduleProcedure controller;
    private ObservableList<OrganEnum> organsToDonate = FXCollections.observableArrayList();

    private Profile donor;
    private Profile receiver;

    @FXML
    private Label donorNameLabel;
    @FXML
    private Label receiverNameLabel;
    @FXML
    private ChoiceBox<OrganEnum> organDropdown;

    /**
     * Initialize the component and use the parent view to get donor and receiver.
     *
     * @param parentView the organ map parent view.
     */
    public void initialize(OrganMap parentView) {
        controller = new odms.controller.user.ScheduleProcedure();
        this.donor = parentView.getCurrentDonor();
        this.receiver = parentView.getCurrentReceiver();
        setLabels();
        setOrganDropdown();
    }

    /**
     * Sets the name labels.
     *
     */
    private void setLabels() {
        donorNameLabel.setText(donor.getFullName());
        receiverNameLabel.setText(receiver.getFullName());
    }

    /**
     * Populates the organ dropdown with the possible organs and selects the first one.
     *
     */
    private void setOrganDropdown() {
        organsToDonate.addAll(controller.getDonatingOrgans(donor, receiver));
        organDropdown.setItems(organsToDonate);
        organDropdown.setValue(organsToDonate.get(0));
    }
}
