package odms.view.user;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import odms.controller.database.CountryDAO;
import odms.controller.database.DAOFactory;
import odms.model.enums.NewZealandRegionsEnum;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.view.CommonView;
import org.controlsfx.control.CheckComboBox;

import static odms.controller.user.AvailableOrgans.*;

public class AvailableOrgans extends CommonView {

    @FXML
    private CheckComboBox organsCombobox;
    @FXML
    private CheckComboBox countriesCombobox;
    @FXML
    private CheckComboBox regionsCombobox;
    @FXML
    private TableView availableOrgansTable;

    private ObservableList<Map.Entry<Profile,OrganEnum>> listOfAvailableOrgans;
    private ObservableList<Map.Entry<Profile,OrganEnum>> listOfFilteredAvailableOrgans; // TODO should these two lists just be one list?

    private ClinicianProfile parentView;
    private odms.controller.user.AvailableOrgans controller = new odms.controller.user.AvailableOrgans();

    private ObservableList<String> organsStrings = FXCollections.observableArrayList();

    private Thread importTask;

    public void populateTable()  {
        availableOrgansTable.getColumns().clear();
        TableColumn<Map.Entry<Profile, OrganEnum>, String> organCol = new TableColumn<>(
                "Organ");
        organCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getValue().getName()));

        TableColumn<Map.Entry<Profile, OrganEnum>, String> dateOfDeathNameCol = new TableColumn<>(
                "Date of Death");
        dateOfDeathNameCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(
                        cdf.getValue().getKey().getDateOfDeath().toString()));

        TableColumn<Map.Entry<Profile, OrganEnum>, String> countdownCol = new TableColumn<>(
                "Countdown");
        countdownCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(
                        (controller.getTimeToExpiryFormatted(cdf.getValue().getValue(), cdf.getValue().getKey()))));


        TableColumn<Map.Entry<Profile, OrganEnum>, String> donorIdCol = new TableColumn<>(
                "Donor ID");
        donorIdCol.setCellValueFactory(
                cdf -> new SimpleStringProperty((cdf.getValue().getKey().getId()).toString()));

//        TableColumn<TestTask, Double> expiryProgressBarCol = new TableColumn("Expiry Progress Bar");
//        expiryProgressBarCol.setCellValueFactory(new PropertyValueFactory<>(
//                "progress"));
//        expiryProgressBarCol
//                .setCellFactory(ProgressBarTableCell.forTableColumn());

        TableColumn<Map.Entry<Profile, OrganEnum>, Double> expiryProgressBarCol = new TableColumn(
                "Expiry Progress Bar");
        expiryProgressBarCol.setCellValueFactory(
                cdf -> new SimpleDoubleProperty(getTimeRemaining(cdf.getValue().getValue(), cdf.getValue().getKey()) / getExpiryLength(cdf.getValue().getValue())).asObject()
        );
        expiryProgressBarCol
                .setCellFactory(ProgressBarTableCell.forTableColumn());


        availableOrgansTable.getColumns().add(organCol);
        availableOrgansTable.getColumns().add(dateOfDeathNameCol);
        availableOrgansTable.getColumns().add(countdownCol);
        availableOrgansTable.getColumns().add(donorIdCol);
        availableOrgansTable.getColumns().add(expiryProgressBarCol);
        availableOrgansTable.getItems().clear();

        try {
            setAvailableOrgansList();
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
        //ExecutorService executor = Executors.newFixedThreadPool(availableOrgansTable.getItems().size(), new ThreadFactory() {
         //   @Override
         //   public Thread newThread(Runnable r) {
         //       Thread t = new Thread(r);
          //      t.setDaemon(true);
         //       return t;
        //    }
       // });

    }

    /**
     * Populates available organs table with ALL available organs in database
     * @throws SQLException exception thrown when accessing DB to get all available organs
     */
    public void setAvailableOrgansList() throws SQLException{
        listOfAvailableOrgans = FXCollections.observableArrayList(controller.getAllOrgansAvailable());
        SortedList<Map.Entry<Profile, OrganEnum>> sortedDonaters = new SortedList<>(listOfAvailableOrgans,
                (Map.Entry<Profile, OrganEnum> donor1, Map.Entry<Profile, OrganEnum> donor2) -> {
                    if(getTimeRemaining(donor1.getValue(), donor1.getKey()) < getTimeRemaining(donor2.getValue(), donor2.getKey())) {
                        return -1;
                    } else if(getTimeRemaining(donor2.getValue(), donor2.getKey()) < getTimeRemaining(donor1.getValue(), donor1.getKey())) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
        availableOrgansTable.setItems(sortedDonaters
        );
    }

    /**
     * Updates the available organs list according to the active filters
     */
    private void performSearchFromFilters() {
        listOfFilteredAvailableOrgans = controller.performSearch(organsCombobox.getCheckModel().getCheckedItems(),
                countriesCombobox.getCheckModel().getCheckedItems(), regionsCombobox.getCheckModel().getCheckedItems());

        updateTable();
    }

    /**
     * Clears the available organs table and updates with the filtered data according to the filters
     */
    private void updateTable() {
        availableOrgansTable.getItems().clear();
        availableOrgansTable.setItems(listOfFilteredAvailableOrgans);
    }


    public void initialize(User currentUser, ClinicianProfile p) {
        controller.setView(this);
        populateTable();
        parentView = p;
        //Populating combo box values
        CountryDAO database = DAOFactory.getCountryDAO();
        int index = 0;
        for (String country : database.getAll(true)) {
            User.allowedCountriesIndices.add(index);             // TODO is allowed countries relevant for this table?
            index++;
        }

        List<String> validCountries = database.getAll(true);
        countriesCombobox.getItems().addAll(validCountries);

        regionsCombobox.getItems().setAll(NewZealandRegionsEnum.toArrayList()); // TODO will this be populated wit ALL regions?
        //regionsCombobox.setDisable(true);  // TODO not sure how the region filter will work with multiple countries just yet

        organsStrings.clear();
        organsStrings.addAll(OrganEnum.toArrayList());
        organsCombobox.getItems().setAll(OrganEnum.values());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                availableOrgansTable.refresh();
                for(Map.Entry<Profile, OrganEnum> m : listOfAvailableOrgans) {
                    controller.checkOrganExpiredListRemoval(m.getValue(), m.getKey(), m);
                }
            }
        },0,1);

    }

    public ObservableList<Map.Entry<Profile, OrganEnum>> getListOfAvailableOrgans() {
        return listOfAvailableOrgans;
    }

    public void removeItem(Map.Entry<Profile, OrganEnum> m) {
        listOfAvailableOrgans.remove(m);
    }

    // static?
//    public class TestTask extends Task<Void> {
//
//        private final int waitTime; // milliseconds
//        private final int pauseTime; // milliseconds
//
//        public static final int NUM_ITERATIONS = 100;
//
//        TestTask(int waitTime, int pauseTime) {
//            this.waitTime = waitTime;
//            this.pauseTime = pauseTime;
//            //int test = odms.controller.user.AvailableOrgans.getExpiryTime(null, null);
//        }
//
//        @Override
//        protected Void call() throws Exception {
//
//            this.updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
//            this.updateMessage("Waiting...");
//            Thread.sleep(waitTime);
//            this.updateMessage("Running...");
//            for (int i = 0; i < NUM_ITERATIONS; i++) {
//                updateProgress((1.0 * i) / NUM_ITERATIONS, 1);
//                Thread.sleep(pauseTime);
//            }
//            this.updateMessage("Done");
//            this.updateProgress(1, 1);
//            return null;
//        }
//    }
}
