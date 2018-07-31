package odms.controller.user;

import static odms.view.CommonView.checkUnsavedChanges;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.controller.AlertController;
import odms.controller.GuiMain;
import odms.controller.data.ProfileDataIO;
import odms.model.user.User;
import odms.view.user.ClinicianProfileView;

import java.io.File;
import java.io.IOException;
import odms.view.user.ImportLoadingDialogView;
import odms.view.user.UserDataManagementTabView;

public class UserDataManagementController {

    UserDataManagementTabView view;

    public UserDataManagementController(UserDataManagementTabView v) {
        view = v;
    }

    private Stage currentStage;

    /**
     * Imports new json file. Closes all open windows and re-initializes the admin view.
     *
     * @param stage Stage to be close
     * @param file  file to be set as database
     */
    public void importAndCloseWindows(Stage stage, File file, User currentUser) {
        GuiMain.setCurrentDatabase(ProfileDataIO.loadData(file.getPath()));

        ClinicianProfileView.closeAllOpenProfiles();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ClinicianProfile.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());

            ClinicianProfileView v = fxmlLoader.getController();
            v.setCurrentUser(currentUser);
            v.initialize();

            stage = new Stage();
            stage.setTitle("Admin");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
        ImportLoadingDialogView controller = fxmlLoader.getController();

        controller.initialize(file, currentStage, view.currentUser);

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
     * Checks that a file is not null and if there are unsaved changes
     * Then calls the function to import the data from the file
     * @param file The file that is trying to be imported
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
     * Imports new json or csv file.
     * Closes all open windows and re-initializes the admin view.
     * @param file file to be set as database
     */
    private void handleInputType(File file) throws IOException {
        if (file.getName().toLowerCase().contains(".csv")) {
            importCSV(file);
        }
    }
}
