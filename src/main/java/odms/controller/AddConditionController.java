package odms.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import odms.profile.Condition;
import odms.profile.Profile;

import java.awt.*;
import java.sql.Connection;
import java.time.LocalDate;

public class AddConditionController {

    private Profile searchedDonor;
    private DonorProfileController controller;

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
    public void handleAddButtonClicked(ActionEvent actionEvent) {
        String name = nameField.getText();
        String dateDiagnosed = dateDiagnosedField.getText();
        Boolean isChronic = chronicCheckBox.isSelected();
        String dateCured = dateCuredField.getText();

        Condition condition;
        if (dateCured == "") {
            condition = new Condition(name, dateDiagnosed, isChronic);
        } else {
            condition = new Condition(name, dateDiagnosed, dateCured, isChronic);
        }
        addCondition(condition);
    }

    /**
     * Adds a new condition to the current profile
     * @param condition
     */
    public void addCondition(Condition condition) {
        searchedDonor.addCondition(condition);
        controller.refreshTable();
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("/view/DonorProfile.fxml"));
//        DonorProfileController controller = fxmlLoader.<DonorProfileController>getController();
//        controller.refreshTable();
    }

    @FXML
    public void handleCuredChecked(ActionEvent actionEvent) {
        if (curedCheckBox.isSelected()) {
            dateCuredField.setDisable(false);
        } else {
            dateCuredField.setDisable(true);
        }
    }

    public void init(DonorProfileController controller) {
        this.controller = controller;
        searchedDonor = controller.searchedDonor;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dateDiagnosedField.setText(now.format(formatter));
        dateCuredField.setDisable(true);
    }

}
