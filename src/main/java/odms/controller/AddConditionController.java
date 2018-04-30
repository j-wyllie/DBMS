package odms.controller;

import com.sun.xml.internal.ws.api.FeatureConstructor;
import java.sql.Date;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.Action;
import odms.profile.Condition;
import odms.profile.Profile;

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

            LocalDate dob = controller.getSearchedDonor().getDateOfBirth();
            if (dob.isAfter(dateDiagnoses) || dateDiagnoses.isAfter(LocalDate.now())){
                throw new IllegalArgumentException();
            }
            if (curedCheckBox.isSelected()) {
                String[] cureDates = dateCured.split("-");
                LocalDate dateCures = LocalDate.of(Integer.valueOf(cureDates[2]), Integer.valueOf(cureDates[1]), Integer.valueOf(cureDates[0]));
                if(dateCures.isAfter(LocalDate.now()) || dob.isAfter(dateCures) || dateDiagnoses.isAfter(dateCures)){
                    condition = new Condition(name, dateDiagnosed, dateCured, isChronic);

                } else {
                    throw new IllegalArgumentException();
                }
            } else {
                condition = new Condition(name, dateDiagnosed, isChronic);
            }
            addCondition(condition);

        } catch (IllegalArgumentException e) {
            warningLabel.setVisible(true);
        } catch (DateTimeException e) {
            warningLabel.setVisible(true);
        } catch (ArrayIndexOutOfBoundsException e) {
            warningLabel.setVisible(true);

        }
    }

    /**
     * Adds a new condition to the current profile
     * @param condition
     */
    public void addCondition(Condition condition) {
        searchedDonor.addCondition(condition);
        controller.refreshTable();
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("/view/DonorProfile.fxml"));
//        DonorProfileController controller = fxmlLoader.<DonorProfileController>getController();
//        controller.refreshTable();
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

    public void init(DonorProfileController controller) {
        this.controller = controller;
        searchedDonor = controller.searchedDonor;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dateDiagnosedField.setText(now.format(formatter));
        dateCuredField.setDisable(true);
    }

}
