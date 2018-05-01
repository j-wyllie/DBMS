package odms.controller;

import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

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

    @FXML
    private void handleConfirmButtonAction(ActionEvent event) {
        //TODO
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        //TODO
    }

    @FXML
    private void handleReasonSelectionAction(ActionEvent event) {
        String reason = reasonSelector.getSelectionModel().getSelectedItem().toString();
        if (reason == "No longer required") {
            dynamicLabel.setText("Cured : ");
            //create cured checkbox.
            CheckBox curedCheck = new CheckBox();
            GridPane.setConstraints(curedCheck, 1, 3, 1, 1);
            GridPane.setMargin(curedCheck, new Insets(0, 30, 0, 0));
        }
        else if (reason == "Patient deceased") {
            dynamicLabel.setText("Date of death : ");
            //create date picker for dod.
            DatePicker dodPicker = new DatePicker();
            dodPicker.setOnAction(actionEvent -> {
                LocalDate dodDate = dodPicker.getValue();
            });
        }
    }

    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize() {
        //Set organ label (getCurrentOrgan()).
        organLabel.setText(organLabel.getText());
        reasonSelector.getItems().addAll("Error", "No longer required", "Patient deceased");
    }
}
