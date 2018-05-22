package odms.controller;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.History.History;
import odms.profile.Condition;
import odms.profile.Profile;

import java.time.LocalDate;

public class ConditionAddController {

    private Profile searchedDonor;
    private ProfileDisplayController controller;

    @FXML
    private javafx.scene.control.TextField nameField;

    @FXML
    private javafx.scene.control.TextField dateDiagnosedField;

    @FXML
    private TextField dateCuredField;

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
        String dateDiagnosed = dateDiagnosedField.getText();
        Boolean isChronic = chronicCheckBox.isSelected();
        String dateCured = dateCuredField.getText();

        Condition condition;

        try {
            String[] diagDates = dateDiagnosed.split("-");
            LocalDate dateDiagnoses = LocalDate.of(Integer.valueOf(diagDates[2]), Integer.valueOf(diagDates[1]), Integer.valueOf(diagDates[0]));

            if (name.equals("")) {
                throw new IllegalArgumentException();
            }

            LocalDate dob = controller.getCurrentProfile().getDateOfBirth();
            if (dob.isAfter(dateDiagnoses) || dateDiagnoses.isAfter(LocalDate.now())){
                throw new IllegalArgumentException();
            }
            if (curedCheckBox.isSelected()) {
                String[] cureDates = dateCured.split("-");
                LocalDate dateCures = LocalDate.of(Integer.valueOf(cureDates[2]), Integer.valueOf(cureDates[1]), Integer.valueOf(cureDates[0]));
                if(dateCures.isAfter(LocalDate.now()) || dob.isAfter(dateCures) || dateDiagnoses.isAfter(dateCures)){
                    throw new IllegalArgumentException();
                } else {
                    condition = new Condition(name, dateDiagnosed, dateCured, isChronic);
                }
            } else {
                condition = new Condition(name, dateDiagnosed, isChronic);
            }
            addCondition(condition);
            LocalDateTime currentTime = LocalDateTime.now();
            History action = new History("Profile",searchedDonor.getId(),"added condition","("  + name+","+dateDiagnosed+","+isChronic+ ")",searchedDonor.getCurrentConditions().indexOf(condition),currentTime);
            HistoryController.updateHistory(action);
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
            dateCuredField.setDisable(false);
            chronicCheckBox.setSelected(false);
        } else {
            dateCuredField.setDisable(true);
        }
    }

    @FXML
    public void handleChronicChecked(ActionEvent actionEvent) {
        if (chronicCheckBox.isSelected()) {
            dateCuredField.setDisable(true);
            curedCheckBox.setSelected(false);
        }
    }

    public void init(ProfileDisplayController controller) {
        this.controller = controller;
        searchedDonor = controller.currentProfile;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dateDiagnosedField.setText(now.format(formatter));
        dateCuredField.setDisable(true);
    }

}
