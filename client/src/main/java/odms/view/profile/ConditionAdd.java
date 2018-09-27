package odms.view.profile;

import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.Profile;
import odms.view.CommonView;

@Slf4j
public class ConditionAdd extends CommonView {
    private Profile searchedDonor;
    private ProfileMedicalHistory parent;
    private odms.controller.profile.ConditionAdd controller = new odms.controller.profile.ConditionAdd();

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
            Condition condition = controller.parseCondition(getNameFieldText(), getDateDiagnosed(), getIsChronic(), getIsCured(),searchedDonor,getDateCured());
            controller.add(searchedDonor, condition);
            parent.refreshConditionTable();
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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

    private String getNameFieldText() {
        return nameField.getText();
    }

    private LocalDate getDateDiagnosed() {
        return dateDiagnosedDatePicker.getValue();
    }

    private Boolean getIsCured() {
        return curedCheckBox.isSelected();
    }

    private Boolean getIsChronic() {
        return chronicCheckBox.isSelected();
    }

    private LocalDate getDateCured() {
        return dateCuredDatePicker.getValue();
    }

}
