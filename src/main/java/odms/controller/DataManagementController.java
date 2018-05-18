package odms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import odms.data.ProfileDataIO;
import odms.user.User;

import java.io.File;
import java.io.IOException;

public class DataManagementController {

    public User currentUser;

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
        if (file != null) { // Check that the user actually selected a file

            if (AlertController.unsavedChangesImport()) {
                GuiMain.setCurrentDatabase(ProfileDataIO.loadData(file.getPath()));
                stage.close();

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/ClinicianProfile.fxml"));

                try {
                    Scene scene = new Scene(fxmlLoader.load());

                    ClinicianProfileController controller = fxmlLoader.getController();
                    controller.setCurrentUser(currentUser);
                    controller.initialize();

                    stage = new Stage();
                    stage.setTitle("Admin");
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
