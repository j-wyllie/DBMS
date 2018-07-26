package odms.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import odms.dao.DAOFactory;
import odms.dao.ProfileDAO;
import odms.data.ProfileDatabase;
import odms.data.ProfileImportTask;
import odms.profile.Profile;
import odms.user.User;

public class ImportLoadingDialogController {

    private ProfileImportTask profileImportTask;

    @FXML
    private ProgressBar progressBarImport;

    @FXML
    private Button buttonImportCancel;

    @FXML
    private Button buttonImportConfirm;

    @FXML
    private TableView<ImportResult> tableStatus;

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
            updateTable(currentResults);
        });

        profileImportTask.progressProperty().addListener((observable, oldValue, newValue) -> {
            Float percValue = newValue.floatValue() * 100;
            String newTitle = "Importing data: " + Math.round(percValue) + "%";
            currentStage.setTitle(newTitle);
        });

        profileImportTask.exceptionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
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
     *
     * @param file the file being imported
     */
    @FXML
    public void initialize(File file, Stage parentStage) {
        if (currentUser != null) {
            profileImportTask = new ProfileImportTask(file);

            profileImportTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                event -> buttonImportConfirm.setDisable(false));

            buttonImportConfirm.setOnAction(event -> {
                List<Profile> profiles = profileImportTask.getDb();
                ProfileDAO database = DAOFactory.getProfileDao();

                profiles.forEach(profile -> {
                    try {
                        database.add(profile);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                closeWindows(parentStage);
            });

            buttonImportCancel.setOnAction(event -> {
                importTask.interrupt();
                ((Stage) progressBarImport.getScene().getWindow()).close();
            });

            setupTable();
            updateProgress();
        }

    }

    private void setupTable() {
        TableColumn<ImportResult, String> tcLabels = new TableColumn<>("labels");
        TableColumn<ImportResult, String> tcValues = new TableColumn<>("values");

        tcLabels.setCellValueFactory(new PropertyValueFactory<>("result"));

        tcValues.setCellValueFactory(new PropertyValueFactory<>("value"));

        tableStatus.getColumns().addAll(tcLabels, tcValues);

        tableStatus.getItems().addAll(
            new ImportResult("Success", "0"),
            new ImportResult("Failure", "0"),
            new ImportResult("Total", "0")
        );

        tableStatus.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            Pane header = (Pane) tableStatus.lookup("TableHeaderRow");
            if (header.isVisible()) {
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });
    }

    private void updateTable(String[] results) {
        for (Integer i = 0; i < results.length; i++) {
            tableStatus.getItems().get(i).setValue(results[i]);
        }
    }

    /**
     * Closes all of the open windows and re-opens an admin page
     *
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
     *
     * @param currentUser the current user
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setCurrentStage(Stage stage) {
        this.currentStage = stage;
    }

    public void setOnCloseRequest() {
        this.currentStage.setOnCloseRequest((WindowEvent event) -> importTask.interrupt());
    }

    public class ImportResult {

        private SimpleStringProperty result;
        private SimpleStringProperty value;

        ImportResult(String result, String value) {
            this.result = new SimpleStringProperty(result);
            this.value = new SimpleStringProperty(value);
        }

        /**
         * Required by javafx
         *
         * @return result property
         */
        public SimpleStringProperty resultProperty() {
            return this.result;
        }

        /**
         * Rquired by javafx
         *
         * @return value property
         */
        public SimpleStringProperty valueProperty() {
            return this.value;
        }

        public String getResult() {
            return result.get();
        }

        public String getValue() {
            return value.get();
        }

        public void setResult(String result) {
            this.result.set(result);
        }

        public void setValue(String value) {
            this.value.set(value);
        }
    }

}
