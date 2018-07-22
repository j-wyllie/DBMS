package odms.view.user;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import odms.controller.AlertController;
import odms.controller.user.UserDataManagementController;
import odms.model.user.User;

public class UserDataManagementTabView {

    private User currentUser;

    private UserDataManagementController userDataManagementController = new UserDataManagementController(this);

    @FXML
    private AnchorPane dataManagementAp;

    /**
     * Opens a file chooser and imports the selected files.
     *
     * @param actionEvent
     */
    public void handleImportSavedDataClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON file(*.json)",
                "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) dataManagementAp.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) { // Check that the user actually selected a file
            if (ClinicianProfileView
                    .checkUnsavedChanges((Stage) dataManagementAp.getScene().getWindow())) {
                if (AlertController.unsavedChangesImport()) {
                    userDataManagementController.importAndCloseWindows(stage, file, currentUser);
                }
            } else {
                userDataManagementController.importAndCloseWindows(stage, file, currentUser);
            }
        }
    }

    public void initialize(User currentUser) {
        this.currentUser = currentUser;
    }
}
