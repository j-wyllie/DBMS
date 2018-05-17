package odms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DataManagementController {

    @FXML
    private AnchorPane dataManagementAp;

    public void handleImportSavedDataClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        Stage stage = (Stage) dataManagementAp.getScene().getWindow();
        fileChooser.showOpenDialog(stage);

    }
}
