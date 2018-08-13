package odms.view.user;

import static odms.controller.user.AvailableOrgans.getExpiryLength;
import static odms.controller.user.AvailableOrgans.getTimeRemaining;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import odms.controller.database.CountryDAO;
import odms.controller.database.DAOFactory;
import odms.model.enums.NewZealandRegionsEnum;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.view.CommonView;
import org.controlsfx.control.CheckComboBox;

public class AvailableOrgans extends CommonView {

    @FXML
    private CheckComboBox organsCombobox;
    @FXML
    private CheckComboBox countriesCombobox;
    @FXML
    private CheckComboBox regionsCombobox;
    @FXML
    private TableView availableOrgansTable;
    @FXML
    private TableView<Profile> potentialOrganMatchTable;

    private boolean filtered = false;
    private OrganEnum selectedOrgan;

    private ObservableList<Map.Entry<Profile,OrganEnum>> listOfAvailableOrgans;
    private ObservableList<Map.Entry<Profile,OrganEnum>> listOfFilteredAvailableOrgans; // TODO should these two lists just be one list?
    private ObservableList<Profile> potentialOrganMatches = FXCollections.observableArrayList();

    private ClinicianProfile parentView;
    private odms.controller.user.AvailableOrgans controller = new odms.controller.user.AvailableOrgans();

    private ObservableList<String> organsStrings = FXCollections.observableArrayList();

    private Thread importTask;

    public void populateMatchesTable() {

        potentialOrganMatchTable.getColumns().clear();
        potentialOrganMatchTable.getItems().clear();

        TableColumn<Profile, String> waitTimeColumn = new TableColumn<>(
                "Wait time"
        );
        waitTimeColumn.setCellValueFactory(
               cdf -> new SimpleStringProperty(
                       controller.getWaitTime(selectedOrgan, cdf.getValue().getOrgansRequired())));

        TableColumn<Profile, String> ageColumn = new TableColumn<>(
                "Age"
        );
        ageColumn.setCellValueFactory(
                cdf -> new SimpleStringProperty(
                        String.valueOf(cdf.getValue().getAge())));

        TableColumn<Profile, String> locationColumn = new TableColumn<>(
                "Location"
        );
        locationColumn.setCellValueFactory(
                cdf -> new SimpleStringProperty(
                        cdf.getValue().getCountry()));      // TODO do we want address? The list is meant to be weighted by location

        potentialOrganMatchTable.getColumns().add(waitTimeColumn);
        potentialOrganMatchTable.getColumns().add(ageColumn);
        potentialOrganMatchTable.getColumns().add(locationColumn);

        setPotentialOrganMatchesList();


        // Sorting on wait time, need to add in distance from location of organ as a 'weighting'
        potentialOrganMatchTable.setSortPolicy(param -> {
            Comparator<Profile> comparator = (o1, o2) -> {
                if (controller.getWaitTimeRaw(selectedOrgan, o1.getOrgansRequired()) > controller.getWaitTimeRaw(selectedOrgan, o1.getOrgansRequired()))
                    return 1;
                else if (controller.getWaitTimeRaw(selectedOrgan, o1.getOrgansRequired()) == controller.getWaitTimeRaw(selectedOrgan, o1.getOrgansRequired())) {
                    return 0;
                } else {
                    return -1;
                }
            };
            FXCollections.sort(potentialOrganMatchTable.getItems(), comparator);
            return true;
        });

        potentialOrganMatchTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    potentialOrganMatchTable.getSelectionModel().getSelectedItem() != null) {
                createNewDonorWindow(potentialOrganMatchTable.getSelectionModel().getSelectedItem(), parentView);
            }
        });

    }


    public void populateOrgansTable()  {
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

        TableColumn<Map.Entry<Profile, OrganEnum>, Double> expiryProgressBarCol = new TableColumn(
                "Expiry Progress Bar");
        expiryProgressBarCol.setCellValueFactory(
                cdf -> new SimpleDoubleProperty(
                        getTimeRemaining(cdf.getValue().getValue(), cdf.getValue().getKey())
                        / getExpiryLength(cdf.getValue().getValue())).asObject()
        );

        expiryProgressBarCol.setCellFactory(TestProgressBar.forTableColumn(
                this.getClass().getResource("/styles/Common.css").toExternalForm())
        );

//        System.out.println(this.getClass().getResource("/styles/Common.css").getFile());


//        System.out.println(availableOrgansTable.getStylesheets());

//        expiryProgressBarCol.getStyleClass().clear();
//        expiryProgressBarCol.getStyleClass().add("progress-bar-test");


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
            } else if (event.isPrimaryButtonDown() && event.getClickCount() == 1 &&
                    availableOrgansTable.getSelectionModel().getSelectedItem() != null) {
                selectedOrgan = ((Map.Entry<Profile, OrganEnum>) availableOrgansTable.getSelectionModel().getSelectedItem()).getValue();
                setPotentialOrganMatchesList();
                updateMatchesTable();
            }
        });

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
        listOfFilteredAvailableOrgans = listOfAvailableOrgans;
    }

    /**
     * Populates available organs table with ALL available organs in database
     */
    public void setPotentialOrganMatchesList()   {

        try{
            OrganEnum organToMatch = selectedOrgan;
            Profile donorProfile = ((Map.Entry<Profile, OrganEnum>) availableOrgansTable.getSelectionModel().getSelectedItem()).getKey();

            potentialOrganMatches = controller.getSuitableRecipientsSorted(organToMatch, donorProfile, selectedOrgan);

        } catch (NullPointerException e) {
            // No organ selected in table
        }

    }


    /**
     * Updates the available organs list according to the active filters
     */
    private void performOrganSearchFromFilters() {
        listOfFilteredAvailableOrgans = FXCollections.observableArrayList();
        listOfFilteredAvailableOrgans.clear();
        for(Map.Entry<Profile, OrganEnum> m : listOfAvailableOrgans) {
            if(organsCombobox.getCheckModel().getCheckedItems().contains(m.getValue()) && regionsCombobox.getCheckModel().getCheckedItems().contains(m.getKey().getRegion())) {
                listOfFilteredAvailableOrgans.add(m);
            } else if(organsCombobox.getCheckModel().getCheckedItems().contains(m.getValue()) && regionsCombobox.getCheckModel().getCheckedItems().size() == 0) {
                listOfFilteredAvailableOrgans.add(m);
            }  else if(organsCombobox.getCheckModel().getCheckedItems().size() == 0 && regionsCombobox.getCheckModel().getCheckedItems().size() == 0) {
                listOfFilteredAvailableOrgans.add(m);
            }   else if(organsCombobox.getCheckModel().getCheckedItems().size() == 0 && regionsCombobox.getCheckModel().getCheckedItems().contains(m.getKey().getRegion())) {
                listOfFilteredAvailableOrgans.add(m);
            }
        }
        if(listOfFilteredAvailableOrgans.size()!= 0 || organsCombobox.getCheckModel().getCheckedItems().size() != 0 || regionsCombobox.getCheckModel().getCheckedItems().size() != 0) {
            availableOrgansTable.setItems(listOfFilteredAvailableOrgans);
        } else {
            availableOrgansTable.setItems(listOfAvailableOrgans);
        }
    }

    /**
     * Clears the available organs table and updates with the filtered data according to the filters
     */
    private void updateOrgansTable() {
        availableOrgansTable.getItems().clear();
        availableOrgansTable.setItems(listOfFilteredAvailableOrgans);
    }

    /**
     * Clears the potential organ match table and updates with the updated profiles
     */
    private void updateMatchesTable() {
        potentialOrganMatchTable.getItems().clear();
        potentialOrganMatchTable.setItems(potentialOrganMatches);
    }


    public void initialize(User currentUser, ClinicianProfile p) {
        controller.setView(this);
        populateOrgansTable();
        populateMatchesTable();
        parentView = p;

        //Populating combo box values
        CountryDAO database = DAOFactory.getCountryDAO();
        int index = 0;
        for (String country : database.getAll(true)) {
            User.allowedCountriesIndices.add(index);             // TODO is allowed countries relevant for this table?
            index++;
        }

        List<String> validCountries = database.getAll(true);
        //todo countries not a required AC
        countriesCombobox.setVisible(false);
        countriesCombobox.getItems().addAll(validCountries);
        countriesCombobox.getCheckModel().getCheckedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change c) {
                performOrganSearchFromFilters();
            }
        });

        regionsCombobox.getItems().setAll(NewZealandRegionsEnum.toArrayList()); // TODO will this be populated with ALL regions?
        //regionsCombobox.setDisable(true);  // TODO not sure how the region filter will work with multiple countries just yet
        regionsCombobox.getCheckModel().getCheckedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change c) {
                performOrganSearchFromFilters();
            }
        });

        organsStrings.clear();
        organsStrings.addAll(OrganEnum.toArrayList());
        organsCombobox.getItems().setAll(OrganEnum.values());
        organsCombobox.getCheckModel().getCheckedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change c) {
                performOrganSearchFromFilters();
            }
        });
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                List<Map.Entry<Profile, OrganEnum>> toRemove = new ArrayList<>();
                availableOrgansTable.refresh();
                // potentialOrganMatchTable.refresh();  // not really needed
                for(Map.Entry<Profile, OrganEnum> m : listOfAvailableOrgans) {
                    toRemove.add(m);
                }
                for(Map.Entry<Profile, OrganEnum> m : toRemove) {
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
}
