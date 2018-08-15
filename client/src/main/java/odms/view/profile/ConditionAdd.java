package odms.view.profile;

import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.Profile;
import odms.controller.database.DAOFactory;
import odms.controller.database.condition.ConditionDAO;
import odms.view.CommonView;

public class ConditionAdd extends CommonView {
    private static Profile searchedDonor;
    private static ProfileMedicalHistory parent;
    private odms.controller.profile.ConditionAdd controller = new odms.controller.profile.ConditionAdd(this);

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
        try {
            controller.add();
            parent.refreshConditionTable();
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            warningLabel.setVisible(true);
        }
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

    @FXML
    public void initialize() {
        LocalDate now = LocalDate.now();
        dateDiagnosedDatePicker.setValue(now);
        dateCuredDatePicker.setValue(now);
        dateCuredDatePicker.setDisable(true);
    }

    public void setup(ProfileMedicalHistory view, Profile p) {
        parent = view;
        searchedDonor = p;
    }

    public Profile getCurrentProfile() {
        return searchedDonor;
    }

    public String getNameFieldText() {
        return nameField.getText();
    }

    public LocalDate getDateDiagnosed() {
        return dateDiagnosedDatePicker.getValue();
    }

    public Boolean getIsCured() {
        return curedCheckBox.isSelected();
    }

    public Boolean getIsChronic() {
        return chronicCheckBox.isSelected();
    }

    public LocalDate getDateCured() {
        return dateCuredDatePicker.getValue();
    }

}
