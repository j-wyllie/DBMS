package odms.controller;

import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class OrganRemovalController {

    @FXML
    private Label dynamicLabel;

    @FXML
    private Label organLabel;

    @FXML
    private ComboBox reasonSelector;

    @FXML
    private GridPane windowGrid;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    private CheckBox curedCheck = new CheckBox();

    private DatePicker dodPicker = new DatePicker(LocalDate.now());

    @FXML
    private void handleConfirmButtonAction(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.close();
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.close();
    }

    @FXML
    private void handleReasonSelectionAction(ActionEvent event) {
        String reason = reasonSelector.getSelectionModel().getSelectedItem().toString();

        if (reason == "No longer required") {
            dynamicLabel.setText("Cured : ");
            //create cured checkbox.
            dodPicker.setVisible(false);
            curedCheck.setVisible(true);
        }
        else if (reason == "Patient deceased") {
            dynamicLabel.setText("Date of death : ");
            //create date picker for dod.
            dodPicker.setOnAction(actionEvent -> {
                LocalDate dodDate = dodPicker.getValue();
            });
            dodPicker.setVisible(true);
            curedCheck.setVisible(false);
        }
        else {
            dynamicLabel.setText("");
            dodPicker.setVisible(false);
            curedCheck.setVisible(false);
        }
    }

    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize(String organ) {
        organLabel.setText(organLabel.getText() + organ);
        reasonSelector.getItems().addAll("Error", "No longer required", "Patient deceased");
        reasonSelector.setValue(reasonSelector.getItems().get(0));
        windowGrid.add(dodPicker, 1,3, 2, 1);
        dodPicker.setVisible(false);
        windowGrid.add(curedCheck, 1,3, 2, 1);
        curedCheck.setVisible(false);
    }
}
