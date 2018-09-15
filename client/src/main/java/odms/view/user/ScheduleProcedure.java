package odms.view.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.view.CommonView;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 * The view for scheduling an organ donation.
 */
@Slf4j
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
    @FXML
    private DatePicker dateOfProcedurePicker;
    @FXML
    private Label errorLabel;

    /**
     * Initialize the component and use the parent view to get donor and receiver.
     *
     * @param parentView the organ map parent view.
     */
    public void initialize(OrganMap parentView) {
        controller = new odms.controller.user.ScheduleProcedure();
        controller.setView(this);
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
        organsToDonate.addAll(controller.getDonatingOrgans());
        organDropdown.setItems(organsToDonate);
        organDropdown.setValue(organsToDonate.get(0));
    }

    /**
     * Schedules the procedure and closes the window.
     *
     */
    @FXML
    private void scheduleProcedure() {
        try {
            controller.scheduleProcedure();
            Stage stage = (Stage) donorNameLabel.getScene().getWindow();
            stage.close();
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
            log.error(e.getMessage());
        }

    }

    public Profile getDonor() {
        return donor;
    }

    public Profile getReceiver() {
        return receiver;
    }

    public LocalDate getDatePickerValue() {
        return dateOfProcedurePicker.getValue();
    }
}
