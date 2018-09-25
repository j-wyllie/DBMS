package odms.view.user;

import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import odms.commons.model.user.User;

public class DataManagement {

    private User currentUser;

    private odms.controller.user.DataManagement controller = new odms.controller.user.DataManagement(this);

    @FXML
    private AnchorPane dataManagementAp;

    /**
     * Opens a file chooser and imports the selected files.
     * Lets the user choose from JSON and CSV files.
     */
    public void handleImportExistingDataClicked() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Data Files (*.csv)",
                "*.csv"
        );
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) dataManagementAp.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        controller.handleFile(file, (Stage) dataManagementAp.getScene().getWindow());
    }

    public void initialize(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
