package odms.controller;

import static odms.controller.AlertController.InvalidUsername;
import static odms.controller.LoginController.getCurrentProfile;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

import com.google.gson.Gson;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import odms.cli.CommandUtils;
import odms.data.ProfileDataIO;
import odms.profile.Condition;
import odms.profile.Profile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.control.table.TableFilter;

public class DonorProfileController {

    protected Profile searchedDonor;

    @FXML
    private Label donorFullNameLabel;

    @FXML
    private Label donorStatusLabel;

    @FXML
    private Label givenNamesLabel;

    @FXML
    private Label lastNamesLabel;

    @FXML
    private Label irdLabel;

    @FXML
    private Label dobLabel;

    @FXML
    private Label dodLabel;

    @FXML
    private Label genderLabel;

    @FXML
    private Label heightLabel;

    @FXML
    private Label weightLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label regionLabel;

    @FXML
    private Label bloodTypeLabel;

    @FXML
    private Label smokerLabel;

    @FXML
    private Label alcoholConsumptionLabel;

    @FXML
    private Label bloodPressureLabel;

    @FXML
    private Label chronicConditionsLabel;

    @FXML
    private Label organsLabel;

    @FXML
    private Label donationsLabel;

    @FXML
    private TextArea historyView;

    @FXML
    private Label bmiLabel;

    @FXML
    private Label ageLabel;

    @FXML
    private Label userIdLabel;

    @FXML
    private Button logoutButton;

    Boolean isClinician = false;

    @FXML
    private TableView curConditionsTable;

    @FXML
    private TableColumn curDescriptionColumn;

    @FXML
    private TableColumn curChronicColumn;

    @FXML
    private TableColumn curDateOfDiagnosisColumn;

    @FXML
    private TableView pastConditionsTable;

    @FXML
    private TableColumn pastDescriptionColumn;

    @FXML
    private TableColumn pastDateCuredColumn;

    @FXML
    private TableColumn pastDateOfDiagnosisColumn;

    @FXML
    private Button toggleCuredButton;

    @FXML
    private Button toggleChronicButton;

    @FXML
    private Button addNewConditionButton;

    @FXML
    private Button deleteConditionButton;


    private ObservableList<Condition> curConditionsObservableList;
    private ObservableList<Condition> pastConditionsObservableList;



    /**
     * initializes and refreshes the current and past conditions tables
     */
    @FXML
    private void makeTable(ArrayList<Condition> curConditions, ArrayList<Condition> pastConditions){                      //TODO need a function to get all current conditions, rather than just all
        //curDiseasesTable.getSortOrder().add(curChronicColumn);
        Profile currentDonor;
        if (searchedDonor != null) {
            currentDonor = searchedDonor;
        } else {
            currentDonor = getCurrentProfile();
        }

        curChronicColumn.setComparator(curChronicColumn.getComparator().reversed());
        //currentDonor.setAllConditions(new ArrayList<>());                                  //remove this eventually, just to keep list small with placeholder data

        if (curConditions != null) {curConditionsObservableList = FXCollections.observableArrayList(curConditions);}
        else {curConditionsObservableList = FXCollections.observableArrayList(); }
        if (pastConditions != null) {pastConditionsObservableList = FXCollections.observableArrayList(pastConditions);}
        else {pastConditionsObservableList = FXCollections.observableArrayList(); }

        refreshConditionTable();

    }

    /**
     * Disables the ability for the table headers to be reordered.
     */
    @FXML
    private void disableTableHeaderReorder() {

        pastConditionsTable.widthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
            {
                TableHeaderRow header = (TableHeaderRow) pastConditionsTable.lookup("TableHeaderRow");
                header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        header.setReordering(false);
                    }
                });
            }
        });

        curConditionsTable.widthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
            {
                TableHeaderRow header = (TableHeaderRow) curConditionsTable.lookup("TableHeaderRow");
                header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        header.setReordering(false);
                    }
                });
            }
        });
    }



    public ArrayList<Condition> convertConditionObservableToArray(ObservableList<Condition> conditions) {
        ArrayList<Condition> toReturn = new ArrayList<>();
        for (int i = 0; i<conditions.size(); i++) {
            if (conditions.get(i) != null) { toReturn.add(conditions.get(i)); }
        }
        return toReturn;
    }



    /**
     * sets all of the items in the fxml to their respective values
     * Enables the relevant buttons on medications tab for how many conditions are selected
     */
    @FXML
    private void refreshPageElements() {

        ArrayList<Condition> allConditions = convertConditionObservableToArray(curConditionsTable.getSelectionModel().getSelectedItems());
        allConditions.addAll(convertConditionObservableToArray(pastConditionsTable.getSelectionModel().getSelectedItems()));

        if (allConditions.size() == 0) {
            deleteConditionButton.setDisable(true);
            toggleCuredButton.setDisable(true);
            toggleChronicButton.setDisable(true);
        } else {
            deleteConditionButton.setDisable(false);
            toggleCuredButton.setDisable(false);
            toggleChronicButton.setDisable(false);
        }

    }



    /**
     * refreshes current and past conditions table with its up to date data
     */
    @FXML
    protected void refreshConditionTable() {

        Callback<TableColumn, TableCell> cellFactory =
                new Callback<TableColumn, TableCell>() {
                    public TableCell call(TableColumn p) {
                        return new EditingConditionsCell();
                    }
                };

        Callback<TableColumn, TableCell> cellFactoryDate =
                new Callback<TableColumn, TableCell>() {
                    public TableCell call(TableColumn p) {
                        return new EditDateCell();
                    }
                };

        Profile currentDonor;
        if (searchedDonor != null) {
            currentDonor = searchedDonor;
        } else {
            currentDonor = getCurrentProfile();
        }

        try {
            if (curConditionsTable.getItems() != null) { curConditionsTable.getItems().clear(); }
            if (pastConditionsTable.getItems() != null) { pastConditionsTable.getItems().clear(); }
        } catch (NullPointerException|UnsupportedOperationException e) {
            System.out.println();
        }

        //curConditionsObservableList.clear();

        if (currentDonor.getCurrentConditions() != null) {curConditionsObservableList.addAll(currentDonor.getCurrentConditions());}
        if (currentDonor.getCuredConditions() != null) {pastConditionsObservableList.addAll(currentDonor.getCuredConditions());}

        curConditionsTable.setItems(curConditionsObservableList);
        curDescriptionColumn.setCellValueFactory(new PropertyValueFactory("name"));
        curDescriptionColumn.setCellFactory(cellFactory);
        curDescriptionColumn.setOnEditCommit(
                (EventHandler<TableColumn.CellEditEvent<Condition, String>>) t -> {
                    currentDonor.removeCondition(t.getTableView().getItems().get(
                            t.getTablePosition().getRow()));
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setName(t.getNewValue());
                    currentDonor.addCondition(t.getTableView().getItems().get(
                            t.getTablePosition().getRow()));
                });

        curChronicColumn.setCellValueFactory(new PropertyValueFactory("chronicText"));
        curChronicColumn.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableCell<ArrayList<Condition>, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            this.setTextFill(Color.RED);
                            setText(item);
                        }
                    }
                };
            }
        });


        curDateOfDiagnosisColumn.setCellValueFactory(new PropertyValueFactory("dateOfDiagnosis"));
        curDateOfDiagnosisColumn.setCellFactory(cellFactoryDate);

        curDateOfDiagnosisColumn.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<Condition, LocalDate>>) t -> {
            if(!t.getNewValue().isAfter(LocalDate.now())) {
                currentDonor.removeCondition(t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
                (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setDateOfDiagnosis(t.getNewValue());
                currentDonor.addCondition(t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
            }
            refreshConditionTable();
        });
        curConditionsTable.getColumns().setAll(curDescriptionColumn, curChronicColumn, curDateOfDiagnosisColumn);

        pastConditionsTable.setItems(pastConditionsObservableList);
        pastDescriptionColumn.setCellValueFactory(new PropertyValueFactory("name"));
        pastDescriptionColumn.setCellFactory(cellFactory);
        pastDescriptionColumn.setOnEditCommit(
                (EventHandler<CellEditEvent<Condition, String>>) t -> (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setName(t.getNewValue()));


        pastDateCuredColumn.setCellValueFactory(new PropertyValueFactory("dateCured"));
        pastDateCuredColumn.setCellFactory(cellFactoryDate);

            pastDateCuredColumn.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<Condition, LocalDate>>) t -> {
                if(t.getNewValue().isBefore(t.getTableView().getItems().get(t.getTablePosition().getRow()).getDateOfDiagnosis())
                        || t.getNewValue().isAfter(LocalDate.now())) {
                } else {
                    currentDonor.removeCondition(t.getTableView().getItems().get(
                            t.getTablePosition().getRow()));
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setDateCured(t.getNewValue());
                    currentDonor.addCondition(t.getTableView().getItems().get(
                            t.getTablePosition().getRow()));
                }
                refreshConditionTable();
            });

        pastDateOfDiagnosisColumn.setCellValueFactory(new PropertyValueFactory("dateOfDiagnosis"));
        pastDateOfDiagnosisColumn.setCellFactory(cellFactoryDate);

        pastDateOfDiagnosisColumn.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<Condition, LocalDate>>) t -> {
            if(t.getNewValue().isAfter(t.getTableView().getItems().get(t.getTablePosition().getRow()).getDateCured())
                    || t.getNewValue().isAfter(LocalDate.now())) {
            } else {
                currentDonor.removeCondition(t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
                (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setDateOfDiagnosis(t.getNewValue());
                currentDonor.addCondition(t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
            }
            refreshConditionTable();
        });
        pastConditionsTable.getColumns().setAll(pastDescriptionColumn, pastDateOfDiagnosisColumn, pastDateCuredColumn);

        forceSortOrder();
        refreshPageElements();

    }

    /**
     * forces the sort order of the current conditions table so that Chronic conditions are always at the top
     */
    @FXML
    private void forceSortOrder() {
        curConditionsTable.getSortOrder().clear();
        curConditionsTable.getSortOrder().add(curChronicColumn);
    }



    /**
     * Button handler to add condition to the current conditions for the current profile.
     * @param event clicking on the add button.
     */
    @FXML
    private void handleAddNewCondition(ActionEvent event) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/AddCondition.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            AddConditionController controller = fxmlLoader.<AddConditionController>getController();
            controller.init(this);

            Stage stage = new Stage();
            stage.setTitle("Add a Condition");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }

        refreshConditionTable();
    }


    /**
     * Button handler to handle delete button clicked, only available to clinicians
     * @param event clicking on the button.
     */
    @FXML
    private void handleDeleteCondition(ActionEvent event) throws IOException {
        Profile currentDonor;
        if (searchedDonor != null) {
            currentDonor = searchedDonor;
        } else {
            currentDonor = getCurrentProfile();
        }



        ArrayList<Condition> conditions = convertConditionObservableToArray(pastConditionsTable.getSelectionModel().getSelectedItems());
        conditions.addAll(convertConditionObservableToArray(curConditionsTable.getSelectionModel().getSelectedItems()));

        for (int i = 0; i<conditions.size(); i++) {
            if (conditions.get(i) != null) { currentDonor.removeCondition(conditions.get(i));}
        }

        refreshConditionTable();
    }


    /**
     * Button handler to handle toggle chronic button clicked, only available to clinicians
     * @param event clicking on the button.
     */
    @FXML
    private void handleToggleChronicButtonClicked(ActionEvent event) {


        ArrayList<Condition> conditions = convertConditionObservableToArray(pastConditionsTable.getSelectionModel().getSelectedItems());
        conditions.addAll(convertConditionObservableToArray(curConditionsTable.getSelectionModel().getSelectedItems()));

        for (int i = 0; i<conditions.size(); i++) {
            if (conditions.get(i) != null) {

                conditions.get(i).setIsChronic(!conditions.get(i).getChronic());
                if (conditions.get(i).getChronic()) {
                    conditions.get(i).setChronicText("CHRONIC");
                    conditions.get(i).setIsCured(false);
                }
                else {conditions.get(i).setChronicText("");}

            }
        }

        refreshConditionTable();
    }


    /**
     * Button handler to handle toggle cured button clicked, only available to clinicians
     * @param event clicking on the button.
     */
    @FXML
    private void handleToggleCuredButtonClicked(ActionEvent event) {

        ArrayList<Condition> conditions = convertConditionObservableToArray(pastConditionsTable.getSelectionModel().getSelectedItems());
        conditions.addAll(convertConditionObservableToArray(curConditionsTable.getSelectionModel().getSelectedItems()));

        for (int i = 0; i < conditions.size(); i++) {
            if (conditions.get(i) != null) {

                if (!conditions.get(i).getChronic()) {
                    conditions.get(i).setIsCured(!conditions.get(i).getCured());
                } else {
                    System.out.println("Condition must be unmarked as Chronic before being Cured!");
                }

                if (conditions.get(i).getCured()) {
                    conditions.get(i).setDateCured(LocalDate.now());
                } else {
                    conditions.get(i).setDateCured(null);
                }

            }

            refreshConditionTable();
        }
    }


    /**
     * Scene change to log in view.
     * @param event clicking on the logout button.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
        LoginController.setCurrentDonor(null); //clears current donor

        Parent parent = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

    /**
     * Button handler to undo last action.
     * @param event clicking on the undo button.
     */
    @FXML
    private void handleUndoButtonClicked(ActionEvent event) throws IOException {
        undo();
    }

    /**
     * Button handler to redo last undo action.
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event) throws IOException {
        redo();
    }

    /**
     * Button handler to make fields editable.
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {
        Profile currentDonor;
        if (searchedDonor != null) {
            currentDonor = searchedDonor;
        } else {
            currentDonor = getCurrentProfile();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/EditDonorProfile.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        EditDonorProfileController controller = fxmlLoader.<EditDonorProfileController>getController();
        controller.setDonor(searchedDonor);
        controller.setIsClinician(isClinician);
        controller.initialize();

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setScene(scene);
        appStage.show();
    }

    /**
     * sets all of the items in the fxml to their respective values
     * @param currentDonor donors profile
     */
    @FXML
    private void setPage(Profile currentDonor){
        /*
        if (searchedDonor != null) {
            currentDonor = searchedDonor;
        } else {
            currentDonor = getCurrentProfile();
        }
        */

        makeTable(currentDonor.getCurrentConditions(), currentDonor.getCuredConditions());
        refreshConditionTable();

        try {
            donorFullNameLabel
                    .setText(currentDonor.getFullName());
            donorStatusLabel.setText(donorStatusLabel.getText() + "Unregistered");

            if (currentDonor.getRegistered() != null && currentDonor.getRegistered()) {
                donorStatusLabel.setText("Donor Status: Registered");
            }
            if (currentDonor.getGivenNames() != null) {
                givenNamesLabel.setText(givenNamesLabel.getText() + currentDonor.getGivenNames());
            }
            if (currentDonor.getLastNames() != null) {
                lastNamesLabel.setText(lastNamesLabel.getText() + currentDonor.getLastNames());
            }
            if (currentDonor.getIrdNumber() != null) {
                irdLabel.setText(irdLabel.getText() + currentDonor.getIrdNumber());
            }
            if (currentDonor.getDateOfBirth() != null) {
                dobLabel.setText(dobLabel.getText() + currentDonor.getDateOfBirth()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            }
            if (currentDonor.getDateOfDeath() != null) {
                dodLabel.setText(dodLabel.getText() + currentDonor.getDateOfDeath()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            } else {
                dodLabel.setText(dodLabel.getText() + "NULL");
            }
            if (currentDonor.getGender() != null) {
                genderLabel.setText(genderLabel.getText() + currentDonor.getGender());
            }
            heightLabel.setText(heightLabel.getText() + currentDonor.getHeight());
            weightLabel.setText(weightLabel.getText() + currentDonor.getWeight());
            phoneLabel.setText(phoneLabel.getText());
            emailLabel.setText(emailLabel.getText());

            if (currentDonor.getAddress() != null) {
                addressLabel.setText(addressLabel.getText() + currentDonor.getAddress());
            }
            if (currentDonor.getRegion() != null) {
                regionLabel.setText(regionLabel.getText() + currentDonor.getRegion());
            }
            if (currentDonor.getBloodType() != null) {
                bloodTypeLabel.setText(bloodTypeLabel.getText() + currentDonor.getBloodType());
            }
            if (currentDonor.getHeight() != null && currentDonor.getWeight() != null) {
                bmiLabel.setText(bmiLabel.getText() + Math.round(currentDonor.calculateBMI() * 100.00) / 100.00);
            }
            if (currentDonor.getDateOfBirth() != null) {
                ageLabel.setText(ageLabel.getText() + Integer.toString(currentDonor.calculateAge()));
            }
            if (currentDonor.getId() != null) {
                userIdLabel.setText(userIdLabel.getText() + Integer.toString(currentDonor.getId()));
            }
            organsLabel.setText(organsLabel.getText() + currentDonor.getOrgans().toString());
            donationsLabel.setText(donationsLabel.getText() + currentDonor.getDonatedOrgans().toString());
            /*if (currentDonor.getSmoker() != null) {
                smokerLabel.setText(smokerLabel.getText() + currentDonor.getSmoker());
            }*/
            /*if (currentDonor.getAlcoholConsumption() != null) {
                alcoholConsumptionLabel.setText(alcoholConsumptionLabel.getText() + currentDonor.getAlcoholConsumption());
            }*/
            /*if (currentDonor.getBloodPressure() != null) {
                bloodPressureLabel.setText(bloodPressureLabel.getText() + currentDonor.getBloodPressure());
            }*/
            //chronic diseases.
            //organs to donate.
            //past donations.
            String history = ProfileDataIO.getHistory();
            Gson gson = new Gson();

            if (history.equals("")) {
                history = gson.toJson(CommandUtils.getHistory());
            } else {
                history = history.substring(0, history.length() - 1);
                history = history + "," + gson.toJson(CommandUtils.getHistory()).substring(1);
            }
            history = history.substring(1, history.length() - 1);
            String[] actionHistory = history.split(",");

            ArrayList<String> userHistory = new ArrayList<>();

            for (String str : actionHistory) {
                if (str.contains("Donor " + currentDonor.getId())) {
                    userHistory.add(str);
                }
            }

            historyView.setText(userHistory.toString());
            refreshConditionTable();

        } catch (Exception e) {
            e.printStackTrace();
            InvalidUsername();
        }

    }

    /**
     * hides items that shouldn't be visible to either a donor or clinician
     */
    @FXML
    private void hideItems() {
        if(isClinician){
            //User is a clinician looking at donors profile, maximise functionality
            curConditionsTable.setEditable(true);
            pastConditionsTable.setEditable(true);
            toggleChronicButton.setDisable(false);
            toggleChronicButton.setVisible(true);
            toggleCuredButton.setDisable(false);
            toggleCuredButton.setVisible(true);
            addNewConditionButton.setVisible(true);
            deleteConditionButton.setVisible(true);

            logoutButton.setVisible(false);
        } else {
            //User is a donor, limit functionality
            curConditionsTable.setEditable(false);
            pastConditionsTable.setEditable(false);
            toggleChronicButton.setDisable(true);
            toggleChronicButton.setVisible(false);
            toggleCuredButton.setDisable(true);
            toggleCuredButton.setVisible(false);
            addNewConditionButton.setVisible(false);
            deleteConditionButton.setVisible(false);
        }

    }

    public Profile getSearchedDonor() { return searchedDonor; }

    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize() {
        curConditionsTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
        pastConditionsTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );


        if(searchedDonor != null) {
            setPage(searchedDonor);

            //if(!isClinician) {
                hideItems();
            //}
            //Profile currentDonor = getCurrentProfile();
        }


        refreshPageElements();





        disableTableHeaderReorder();
        @SuppressWarnings("deprecation") TableFilter tableFilter = new TableFilter(curConditionsTable);
        @SuppressWarnings("deprecation") TableFilter tableFilter2 = new TableFilter(pastConditionsTable);

    }

    /**
     * sets the donor if it is being opened by a clinician
     * @param donor
     */
    public void setDonor(Profile donor) {
        isClinician = true;
        searchedDonor = donor;
        //hideItems();
        //setPage(searchedDonor);
    }

    /**
     * sets the donor if it was logged in by a user
     * @param donor
     */
    public void setLoggedInDonor(Profile donor) {
        isClinician = false;
        searchedDonor = donor;
        //setPage(searchedDonor);
    }

}
