package odms.view.user;

import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import odms.controller.GuiMain;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.view.CommonView;
import javafx.scene.control.ProgressBar;

import java.sql.SQLException;
import java.util.Map;

public class AvailableOrgans extends CommonView {
    @FXML
    private TableView availableOrgansTable;
    private ObservableList<Map.Entry<Profile,OrganEnum>> listOfAvailableOrgans;
    private ClinicianProfile parentView;
    private odms.controller.user.AvailableOrgans controller = new odms.controller.user.AvailableOrgans();

    private Thread importTask;


    public void initialize(User currentUser, ClinicianProfile p) {
        populateTable();
        parentView = p;
    }

    public void populateTable()  {
        availableOrgansTable.getColumns().clear();
        TableColumn<Map.Entry<Profile, OrganEnum>, String> organCol = new TableColumn<>(
                "Organ");
        organCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getValue().getName()));

        TableColumn<Map.Entry<Profile, OrganEnum>, String> dateOfDeathNameCol = new TableColumn<>(
                "Date of Death");
        dateOfDeathNameCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getKey().getDateOfDeath().toString()));

        TableColumn<Map.Entry<Profile, OrganEnum>, String> countdownCol = new TableColumn<>(
                "Countdown");
        countdownCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getKey().getRegion()));

        TableColumn<Map.Entry<Profile, OrganEnum>, String> donorIdCol = new TableColumn<>(
                "Donor ID");
        donorIdCol.setCellValueFactory(
                cdf -> new SimpleStringProperty((cdf.getValue().getKey().getId()).toString()));

//        TableColumn<Map.Entry<Profile, OrganEnum>, String> expiryProgressBarCol = new TableColumn<>(
//                "Expiry Progress Bar");
//        expiryProgressBarCol.setCellValueFactory(
//                cdf -> new SimpleStringProperty((cdf.getValue().getValue().getDate()).toString()));


//        TableColumn<TestTask, Double> expiryProgressBarCol = new TableColumn("Expiry Progress Bar");
//        expiryProgressBarCol.setCellValueFactory(new PropertyValueFactory<>(
//                "progress"));
//        expiryProgressBarCol
//                .setCellFactory(ProgressBarTableCell.forTableColumn());

        // TODO yet to work out how to link anything up, bit confused
        TableColumn<Map.Entry<Profile, OrganEnum>, Double> expiryProgressBarCol = new TableColumn("Expiry Progress Bar");
        expiryProgressBarCol.setCellValueFactory(new PropertyValueFactory<>(
                "progress"));
        expiryProgressBarCol
                .setCellFactory(ProgressBarTableCell.forTableColumn());

        availableOrgansTable.getColumns().add(organCol);
        availableOrgansTable.getColumns().add(dateOfDeathNameCol);
        availableOrgansTable.getColumns().add(countdownCol);
        availableOrgansTable.getColumns().add(donorIdCol);
        availableOrgansTable.getColumns().add(expiryProgressBarCol);
        availableOrgansTable.getItems().clear();
        try {
            setList();
        } catch (SQLException e) {
            System.out.println("SQL ERROR");
        }
        availableOrgansTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    availableOrgansTable.getSelectionModel().getSelectedItem() != null) {
                createNewDonorWindow(((Map.Entry<Profile, OrganEnum>) availableOrgansTable.getSelectionModel()
                        .getSelectedItem()).getKey(), parentView);
            }
        });


        // Thread stuff for the multiple progress bars, not sure how else to do it
        ExecutorService executor = Executors.newFixedThreadPool(availableOrgansTable.getItems().size(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });

    }

    public void setList() throws SQLException{
        listOfAvailableOrgans = FXCollections.observableArrayList(controller.getAllOrgansAvailable());
        availableOrgansTable.setItems(listOfAvailableOrgans);
    }




    // TODO not sure how to feed an organ into this task to use the organs expiry time as the rate etc
    static class TestTask extends Task<Void> {

        private final int waitTime; // milliseconds
        private final int pauseTime; // milliseconds

        public static final int NUM_ITERATIONS = 100;

        TestTask(int waitTime, int pauseTime, OrganEnum organ) {
            this.waitTime = waitTime;
            this.pauseTime = pauseTime;
        }

        @Override
        protected Void call() throws Exception {


            this.updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
            //this.updateMessage("Waiting...");
            Thread.sleep(waitTime);
            //this.updateMessage("Running...");
            for (int i = 0; i < NUM_ITERATIONS; i++) {
                updateProgress((1.0 * i) / NUM_ITERATIONS, 1);
                Thread.sleep(pauseTime);
            }
            //this.updateMessage("Done");
            this.updateProgress(1, 1);
            return null;
        }
    }




}
