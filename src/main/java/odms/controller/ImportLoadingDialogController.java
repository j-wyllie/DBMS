package odms.controller;

import java.io.File;
import java.io.IOException;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import odms.data.ProfileDatabase;
import odms.data.ProfileImportTask;
import odms.user.User;

public class ImportLoadingDialogController {

    private ProfileImportTask profileImportTask;

    @FXML
    private ProgressBar progressBarImport;

    @FXML
    private Label labelImportStatus;

    @FXML
    private Button buttonImportCancel;

    @FXML
    private Button buttonImportConfirm;

    @FXML
    private TableView tableStatus;

    private Stage currentStage;
    private User currentUser;
    private Thread importTask;

    /**
     * Binds the progress bar and the text property to the profile import task
     */
    private void updateProgress() {
        progressBarImport.setProgress(0);
        progressBarImport.progressProperty().unbind();
        progressBarImport.progressProperty().bind(profileImportTask.progressProperty());

        profileImportTask.messageProperty().addListener((observable, oldValue, newValue) -> {
            String[] currentResults = newValue.split(",");
            // todo
        });

        profileImportTask.progressProperty().addListener((observable, oldValue, newValue) -> {
            Float percValue = newValue.floatValue() * 100;
            String newTitle = "Importing data: " + Math.round(percValue) + "%";
            currentStage.setTitle(newTitle);
        });

        profileImportTask.exceptionProperty().addListener((observable, oldValue, newValue) ->  {
            if(newValue != null) {
                ((Stage) buttonImportConfirm.getScene().getWindow()).close();

                AlertController.guiPopup("CSV file is not formatted correctly.");
            }
        });

        importTask = new Thread(profileImportTask);
        importTask.setDaemon(true);
        importTask.start();
    }

    /**
     * Creates the profile import task, adds handlers for the buttons and calls update progress
     * @param file the file being imported
     */
    @FXML
    public void initialize(File file) {
        if (currentUser != null) {
            profileImportTask = new ProfileImportTask(file);

            profileImportTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                    event -> buttonImportConfirm.setDisable(false));

            buttonImportConfirm.setOnAction(event -> {
                ProfileDatabase db = profileImportTask.getDb();
                closeWindows(currentStage);
                    GuiMain.setCurrentDatabase(db);
            });

            buttonImportCancel.setOnAction(event -> {
                importTask.interrupt();
                ((Stage) progressBarImport.getScene().getWindow()).close();
            });

            updateProgress();
        }

    }

    private void setupTable() {
        TableColumn labels = new TableColumn("labels");

    }

    /**
     * Closes all of the open windows and re-opens an admin page
     * @param stage the current stage
     */
    private void closeWindows(Stage stage) {
        ClinicianProfileController.closeAllOpenProfiles();
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

    /**
     * Sets the current user of the program
     * @param currentUser the current user
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setCurrentStage(Stage stage) {
        this.currentStage = stage;
    }

    public void setOnCloseRequest() {
        this.currentStage.setOnCloseRequest((WindowEvent event) -> {
            importTask.interrupt();
        });
    }

}
