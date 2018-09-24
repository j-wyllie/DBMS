package odms.view.user;

import java.sql.SQLException;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.locations.Hospital;
import odms.commons.model.profile.Profile;
import odms.controller.DateTimePicker;
import odms.view.CommonView;

/**
 * The view for scheduling an organ donation.
 */
@Slf4j
public class ScheduleProcedure extends CommonView {

    private odms.controller.user.ScheduleProcedure controller;
    private ObservableList<OrganEnum> organsToDonate = FXCollections.observableArrayList();
    private ObservableList<Hospital> hospitals = FXCollections.observableArrayList();

    private Profile donor;
    private Profile receiver;

    @FXML
    private Label donorNameLabel;
    @FXML
    private Label receiverNameLabel;
    @FXML
    private ChoiceBox<OrganEnum> organDropdown;
    @FXML
    private ChoiceBox<Hospital> locationDropdown;
    @FXML
    private DateTimePicker dateOfProcedurePicker;
    @FXML
    private Label errorLabel;
    @FXML
    private CheckBox receiverEmailCheck;
    @FXML
    private CheckBox donorEmailCheck;


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
        setLocationDropdown();
    }

    /**
     * Sets the name labels.
     */
    private void setLabels() {
        donorNameLabel.setText(donor.getFullName());
        receiverNameLabel.setText(receiver.getFullName());
    }

    /**
     * Populates the organ dropdown with the possible organs and selects the first one.
     */
    private void setOrganDropdown() {
        organsToDonate.addAll(controller.getDonatingOrgans());
        organDropdown.setItems(organsToDonate);
        organDropdown.setValue(organsToDonate.get(0));
    }

    /**
     * Populates the location dropdown with hospitals from the database.
     */
    private void setLocationDropdown() {
        try {
            hospitals.addAll(controller.getHospitals());
            if (!hospitals.isEmpty()) {
                locationDropdown.setItems(hospitals);
                locationDropdown.setValue(hospitals.get(0));
            } else {
                Hospital hospital = new Hospital("Unspecified", 0.0, 0.0, "");
                hospitals.add(hospital);
                locationDropdown.setItems(hospitals);
                locationDropdown.setValue(hospitals.get(0));
            }

            locationDropdown.setConverter(new StringConverter<Hospital>() {
                @Override
                public String toString(Hospital object) {
                    return object.getName();
                }

                @Override
                public Hospital fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
            log.error(e.getMessage());
        }
    }

    /**
     * Schedules the procedure and closes the window.
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

    public LocalDateTime getDatePickerValue() {
        return dateOfProcedurePicker.getDateTimeValue();
    }

    public Hospital getSelectedHospital() {
        return locationDropdown.getValue();
    }

    public OrganEnum getSelectedOrgan() {
        return organDropdown.getValue();
    }

    public Boolean getDonorCheck() {
        return donorEmailCheck.isSelected();
    }

    public Boolean getReceiverCheck() {
        return receiverEmailCheck.isSelected();
    }
}
