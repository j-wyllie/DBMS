package odms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import odms.data.ProfileDataIO;

import java.io.File;

public class DataManagementController {

    @FXML
    private AnchorPane dataManagementAp;

    public void handleImportSavedDataClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON file(*.json)",
                "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) dataManagementAp.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        GuiMain.setCurrentDatabase(ProfileDataIO.loadData(file.getPath()));
    }
}
