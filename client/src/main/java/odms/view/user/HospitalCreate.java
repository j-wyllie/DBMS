package odms.view.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import odms.commons.model.enums.OrganEnum;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class HospitalCreate {

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField longitudeField;

    @FXML
    private TextField latitudeField;

    @FXML
    private Label warningLabel;

    @FXML
    private ListView<String> programList;

    @FXML
    private Button addButton;

    private odms.controller.user.HospitalCreate controller = new odms.controller.user.HospitalCreate(this);

    public void handleAddButtonClicked() {
        String address = addressField.getText();
        String name = nameField.getText();
        Double lat = Double.parseDouble(latitudeField.getText());
        Double lon = Double.parseDouble(longitudeField.getText());

        try {
            controller.addHospital(name, address, lat, lon);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Init the listView to contain radio buttons.
     */
    public void initialize() {
        initListView();
    }

    private void initListView() {
        ObservableList<String> programs = FXCollections.observableArrayList();
        for (OrganEnum organEnum : OrganEnum.values()) {
            programs.add(organEnum.getNamePlain());
        }

        programList.setItems(programs);
        programList.setCellFactory(param -> new RadioListCell());
    }

    public String getNameField() {
        return nameField.getText();
    }

    public String getAddressField() {
        return addressField.getText();
    }

    public Double getLatitudeField() {
        return Double.parseDouble(latitudeField.getText());
    }

    public Double getLongitudeField() {
        return Double.parseDouble(longitudeField.getText());
    }

    public List<String> getProgramList() {
        return programList.getItems();
    }

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
