package odms.view.user;

import java.io.File;
import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import odms.commons.model.user.User;
import odms.controller.AlertController;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.MySqlProfileDAO;
import odms.controller.profile.ProfileImportTask;
import odms.view.CommonView;

public class ImportLoadingDialog extends CommonView {

    private ProfileImportTask profileImportTask;

    private odms.controller.user.ImportLoadingDialog controller = new odms.controller.user.ImportLoadingDialog(this);

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
    public void initialize(File file, Stage parentStage, User user) {
        if (user != null) {
            currentUser = user;
            profileImportTask = new ProfileImportTask(file);

            profileImportTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                    event -> buttonImportConfirm.setDisable(false));

            buttonImportConfirm.setOnAction(event -> {
                try {
                    MySqlProfileDAO mySqlProfileDAO = new MySqlProfileDAO();
                    mySqlProfileDAO.commitTransaction(profileImportTask.getConnection());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                closeWindows(parentStage);
            });

            buttonImportCancel.setOnAction(event -> {
                importTask.interrupt();
                    MySqlProfileDAO mySqlProfileDAO = new MySqlProfileDAO();
                    mySqlProfileDAO.rollbackTransaction(profileImportTask.getConnection());
                ((Stage) progressBarImport.getScene().getWindow()).close();
            });

            tableStatus.setSelectionModel(null);

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
        closeAllOpenProfiles();
        stage.close();
        createNewAdminWindow(currentUser);
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
        this.currentStage.setOnCloseRequest((WindowEvent event) -> {
            importTask.interrupt();
            MySqlProfileDAO mySqlProfileDAO = new MySqlProfileDAO();
            mySqlProfileDAO.rollbackTransaction(profileImportTask.getConnection());
        });
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
         * Required by javafx
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
