package odms.view.user;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.locations.Hospital;
import odms.controller.AlertController;

/**
 * View class for the create hospital scene.
 */
public class HospitalCreate {

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private ListView<CheckBox> programList;

    private odms.controller.user.HospitalCreate controller =
            new odms.controller.user.HospitalCreate();
    private boolean isEdit;
    private Integer hospitalId;

    /**
     * Button handler called when the add button is clicked.
     *
     * @param event button clicked event
     */
    public void handleAddButtonClicked(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        String address;
        String name;
        List<CheckBox> organPrograms = programList.getItems();

        try {
            address = addressField.getText();
            name = nameField.getText();
            if (name.length() < 1) {
                throw new IllegalArgumentException("Name not provided!");
            }
            if (address.length() < 1) {
                throw new IllegalArgumentException("Address not provided!");
            }
            if (!isEdit) {
                controller.addHospital(name, address, organPrograms);
            } else {
                controller.editHospital(name, address, organPrograms, hospitalId);
            }
            appStage.close();

        } catch (IOException | IllegalArgumentException e) {
            AlertController.invalidEntry("Please enter all fields.");
        } catch (SQLException e) {
            AlertController.invalidEntry("Database error.");
        }

    }

    /**
     * Button handler called when the cancel button is clicked.
     *
     * @param event button clicked event
     */
    public void handleCancelButtonClicked(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.close();
    }

    /**
     * Init the listView to contain radio buttons.
     */
    public void initialize() {
        isEdit = false;
        initListView();
    }

    /**
     * Init the listView to contain radio buttons.
     *
     * @param hospital The hospital object to be edited.
     */
    public void initialize(Hospital hospital) {
        hospitalId = hospital.getId();
        isEdit = true;
        initListView();
        nameField.setText(hospital.getName());
        addressField.setText(hospital.getAddress());

        int i = 0;
        for (CheckBox checkBox : programList.getItems()) {
            checkBox.setSelected(hospital.getPrograms().get(i));
            i++;
        }

    }

    /**
     * Populates the transplant program list view with all of the possible organs transplants.
     */
    private void initListView() {
        ObservableList<CheckBox> programs = FXCollections.observableArrayList();
        for (OrganEnum organEnum : OrganEnum.values()) {
            programs.add(new CheckBox(organEnum.getNamePlain()));
        }

        programList.setItems(programs);
    }

}
