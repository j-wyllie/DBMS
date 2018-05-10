package odms.controller;

import java.rmi.registry.LocateRegistry;
import java.sql.Date;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javax.swing.Action;
import odms.profile.Condition;
import odms.profile.Profile;

import java.sql.Connection;
import java.time.LocalDate;

public class ConditionAddController {

    private Profile searchedDonor;
    private ProfileDisplayController controller;

    @FXML
    private javafx.scene.control.TextField nameField;

    @FXML
    private DatePicker dateDiagnosedDatePicker;

    @FXML
    private DatePicker dateCuredDatePicker;

    @FXML
    private CheckBox chronicCheckBox;

    @FXML
    private CheckBox curedCheckBox;

    @FXML
    private Label warningLabel;

    @FXML
    private Button addButton;

    @FXML
    public void handleAddButtonClicked(ActionEvent actionEvent) {
        String name = nameField.getText();
        LocalDate dateDiagnosed = dateDiagnosedDatePicker.getValue();
        Boolean isChronic = chronicCheckBox.isSelected();
        LocalDate dateCured = dateCuredDatePicker.getValue();

        Condition condition;

        try {
            LocalDate dob = controller.getSearchedDonor().getDateOfBirth();
            if (dob.isAfter(dateDiagnosed) || dateDiagnosed.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException();
            }
            if (curedCheckBox.isSelected()) {
                if(dateCured.isAfter(LocalDate.now()) || dob.isAfter(dateCured) || dateDiagnosed.isAfter(dateCured)){
                    throw new IllegalArgumentException();
                } else {
                    condition = new Condition(name, dateDiagnosed, dateCured, isChronic);
                }
            } else {
                condition = new Condition(name, dateDiagnosed, isChronic);
            }
            addCondition(condition);

        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | DateTimeException e) {
            warningLabel.setVisible(true);
        }
    }

    /**
     * Adds a new condition to the current profile
     * @param condition to be added
     */
    public void addCondition(Condition condition) {
        searchedDonor.addCondition(condition);
        controller.refreshConditionTable();
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleCuredChecked(ActionEvent actionEvent) {
        if (curedCheckBox.isSelected()) {
            dateCuredDatePicker.setDisable(false);
            chronicCheckBox.setSelected(false);
        } else {
            dateCuredDatePicker.setDisable(true);
        }
    }

    @FXML
    public void handleChronicChecked(ActionEvent actionEvent) {
        if (chronicCheckBox.isSelected()) {
            dateCuredDatePicker.setDisable(true);
            curedCheckBox.setSelected(false);
        }
    }

    public void init(ProfileDisplayController controller) {
        this.controller = controller;
        searchedDonor = controller.searchedDonor;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dateCuredDatePicker.setValue(LocalDate.now());
        dateCuredDatePicker.setDisable(true);
    }

}
