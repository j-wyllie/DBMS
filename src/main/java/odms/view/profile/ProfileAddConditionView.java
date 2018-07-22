package odms.view.profile;

import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import odms.controller.condition.ConditionAddController;
import odms.model.profile.Profile;

public class ProfileAddConditionView {
    private Profile searchedDonor;
    private ProfileMedicalHistoryView parent;
    private ConditionAddController controller = new ConditionAddController(this);

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

    public void init(ProfileMedicalHistoryView view, Profile p) {
        parent = view;
        searchedDonor = p;
        LocalDate now = LocalDate.now();
        dateDiagnosedDatePicker.setValue(now);
        dateCuredDatePicker.setValue(now);
        dateCuredDatePicker.setDisable(true);
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
