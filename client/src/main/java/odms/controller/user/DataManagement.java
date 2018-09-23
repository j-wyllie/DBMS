package odms.controller.user;

import static odms.view.CommonView.checkUnsavedChanges;

import java.io.File;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.controller.AlertController;
import odms.view.user.ClinicianProfile;
import odms.view.user.ImportLoadingDialog;

/**
 * Handles the importing of a CSV file.
 */
public class DataManagement {

    private ClinicianProfile view;
    private Stage currentStage;

    /**
     * Sets the controllers view parent view.
     *
     * @param v Clinician Profile.
     */
    public DataManagement(ClinicianProfile v) {
        view = v;
    }

    /**
     * Loads the ImportLoadingDialog pane to import the data from the csv file.
     *
     * @param file the csv file to be imported
     * @throws IOException thrown if the data in the file can not be imported
     */
    private void importCSV(File file) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ImportLoadingDialog.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        ImportLoadingDialog controller = fxmlLoader.getController();

        controller.initialize(file, currentStage, view.getCurrentUser());

        Stage stage = new Stage();
        controller.setCurrentStage(stage);
        controller.setOnCloseRequest();
        stage.setTitle("Import data");
        stage.initOwner(currentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Checks that a file is not null and if there are unsaved changes. Then calls the function to
     * import the data from the file
     *
     * @param file The file that is trying to be imported
     * @param stage The current stage.
     * @throws IOException Thrown when the window cannot be initialized.
     */
    public void handleFile(File file, Stage stage) throws IOException {
        if (file != null) { // Check that the user actually selected a file
            currentStage = stage;
            if (checkUnsavedChanges(currentStage)) {
                if (AlertController.unsavedChangesImport()) {
                    handleInputType(file);
                }
            } else {
                handleInputType(file);
            }
        }
    }

    /**
     * Imports new json or csv file. Closes all open windows and re-initializes the admin view.
     *
     * @param file file to be set as database
     * @throws IOException Thrown when the csv file cannot be opened.
     */
    private void handleInputType(File file) throws IOException {
        if (file.getName().toLowerCase().contains(".csv")) {
            importCSV(file);
        }
    }
}
