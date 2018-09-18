package odms.view.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import odms.commons.model.enums.OrganEnum;

import java.io.IOException;
import java.sql.SQLException;

/**
 * View class for the create hospital scene.
 */
public class HospitalCreate {

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private Label warningLabel;

    @FXML
    private Label warningServerLabel;

    @FXML
    private Label warningAddressLabel;

    @FXML
    private ListView<String> programList;

    private odms.controller.user.HospitalCreate controller = new odms.controller.user.HospitalCreate();

    /**
     * Button handler called when the add button is clicked.
     * @param event button clicked event
     */
    public void handleAddButtonClicked(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        String address;
        String name;

        try {
            address = addressField.getText();
            name = nameField.getText();
        } catch (Exception e) {
            warningLabel.setVisible(true);
            warningServerLabel.setVisible(false);
            warningAddressLabel.setVisible(false);
            return;
        }

        try {
            controller.addHospital(name, address);
        } catch (IOException e) {
            warningAddressLabel.setText(e.getMessage());
            warningAddressLabel.setVisible(true);
            warningServerLabel.setVisible(false);
            warningLabel.setVisible(false);
            return;
        } catch (SQLException e) {
            warningServerLabel.setVisible(true);
            warningAddressLabel.setVisible(false);
            warningLabel.setVisible(false);
            return;
        }
        appStage.close();
    }

    /**
     * Init the listView to contain radio buttons.
     */
    public void initialize() {
        initListView();
    }

    /**
     * Populates the transplant program list view with all of the possible organs transplants.
     */
    private void initListView() {
        ObservableList<String> programs = FXCollections.observableArrayList();
        for (OrganEnum organEnum : OrganEnum.values()) {
            programs.add(organEnum.getNamePlain());
        }

        programList.setItems(programs);
        programList.setCellFactory(param -> new RadioListCell());
    }

    /**
     * Custom class that allows radio buttons to be placed in list view elements.
     */
    private class RadioListCell extends ListCell<String> {
        @Override
        public void updateItem(String obj, boolean empty) {
            super.updateItem(obj, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                RadioButton radioButton = new RadioButton(obj);
                // Add Listeners if any
                setGraphic(radioButton);
            }
        }
    }
}
