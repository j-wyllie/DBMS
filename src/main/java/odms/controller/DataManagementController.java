package odms.controller;

import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.App;
import odms.data.ProfileDataIO;
import odms.user.User;

public class DataManagementController {

    public User currentUser;

    @FXML
    private AnchorPane dataManagementAp;

    /**
     * Opens a file chooser and imports the selected files.
     * Lets the user choose from JSON and CSV files.
     * @param actionEvent
     */
    public void handleImportExistingDataClicked(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Data Files (*.json;*.csv)",
                "*.json", "*.csv"
        );
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) dataManagementAp.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        handleFile(file);
    }

    /**
     * Checks that a file is not null and if there are unsaved changes
     * Then calls the function to import the data from the file
     * @param file The file that is trying to be imported
     */
    private void handleFile(File file) throws IOException {
        if (file != null) { // Check that the user actually selected a file
            if (ClinicianProfileController.checkUnsavedChanges((Stage) dataManagementAp.getScene().getWindow())) {
                if (AlertController.unsavedChangesImport()) {
                    handleInputType(file);
                }
            } else {
                handleInputType(file);
            }
        }
    }

    /**
     * Loads the ImportLoadingDialog pane to import the data from the csv file
     * @param file the csv file to be imported
     * @throws IOException thrown if the data in the file can not be imported
     */
    private void importCSV(File file) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ImportLoadingDialog.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        ImportLoadingDialogController controller = fxmlLoader.getController();
        controller.setCurrentUser(currentUser);

        controller.initialize(file, (Stage) dataManagementAp.getScene().getWindow());

        Stage stage = new Stage();
        controller.setCurrentStage(stage);
        controller.setOnCloseRequest();
        stage.setTitle("Import data");
        stage.initOwner(dataManagementAp.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Imports new json or csv file.
     * Closes all open windows and re-initializes the admin view.
     * @param file file to be set as database
     */
    private void handleInputType(File file) throws IOException {
        if (file.getName().toLowerCase().contains(".json")) {
            GuiMain.setCurrentDatabase(ProfileDataIO.loadDataFromJSON(file.getPath()));
        } else if (file.getName().toLowerCase().contains(".csv")) {
            importCSV(file);
        }
    }

    /**
     * Sets the current user in the program
     * @param currentUser the current user
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
