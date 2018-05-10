package odms.controller;

import static odms.controller.AlertController.invalidUsername;
import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.LoginController.getCurrentProfile;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;
import static odms.data.MedicationDataIO.getActiveIngredients;
import static odms.data.MedicationDataIO.getSuggestionList;

import com.google.gson.Gson;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import odms.cli.CommandUtils;
import odms.data.MedicationDataIO;
import odms.data.ProfileDataIO;
import odms.medications.Drug;
import odms.profile.Condition;
import odms.profile.Organ;
import odms.profile.Procedure;
import odms.profile.Profile;
import org.controlsfx.control.table.TableFilter;

public class ProfileDisplayController extends CommonController {

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

    private Boolean isClinician = false;

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

    @FXML
    private Button buttonAddMedication;

    @FXML
    private Button buttonDeleteMedication;

    @FXML
    private Button buttonMedicationCurrentToHistoric;

    @FXML
    private Button buttonMedicationHistoricToCurrent;

    @FXML
    private TextField textFieldMedicationSearch;

    @FXML
    private TableView<Drug> tableViewCurrentMedications;

    @FXML
    private TableColumn<Drug, String> tableColumnMedicationNameCurrent;

    @FXML
    private TableView<Drug> tableViewHistoricMedications;

    @FXML
    private TableColumn<Drug, String> tableColumnMedicationNameHistoric;

    @FXML
    private Label organsRequiredLabel;

    @FXML
    private TableView<String> tableViewDrugInteractionsNames;

    @FXML
    private TableColumn<String, String> tableColumnDrugInteractions;

    @FXML
    private TableView<Map.Entry<String, String>> tableViewDrugInteractions;

    @FXML
    private TableColumn<Map.Entry<String, String>, String> tableColumnSymptoms;

    @FXML
    private TableColumn<Map.Entry<String, String>, String> tableColumnDuration;

    @FXML
    private TableView<String> tableViewActiveIngredients;

    @FXML
    private TableColumn<String, String> tableColumnActiveIngredients;

    private ObservableList<Drug> currentMedication = FXCollections.observableArrayList();

    private ObservableList<Drug> historicMedication = FXCollections.observableArrayList();

    private ObservableList<Map.Entry<String, String>> interactions;

    private ContextMenu suggestionMenu = new ContextMenu();

    @FXML
    private Button buttonShowDrugInteractions;

    @FXML
    private Button buttonViewActiveIngredients;

    /**
     * Text for showing recent edits.
     */
    @FXML
    public Text editedText;


    private ObservableList<Condition> curConditionsObservableList;
    private ObservableList<Condition> pastConditionsObservableList;


    @FXML
    private Button addNewProcedureButton;

    @FXML
    private Button deleteProcedureButton;

    @FXML
    private TableView pendingProcedureTable;

    @FXML
    private TableView previousProcedureTable;

    @FXML
    private TableColumn pendingSummaryColumn;

    @FXML
    private TableColumn previousSummaryColumn;

    @FXML
    private TableColumn pendingDateColumn;

    @FXML
    private TableColumn previousDateColumn;

    @FXML
    private TableColumn pendingAffectsColumn;

    @FXML
    private TableColumn previousAffectsColumn;

    @FXML
    private Label receiverStatusLabel;

    /**
     * Called when there has been an edit to the current profile.
     */
    public void editedTextArea() {
        editedText.setText("The profile was successfully edited.");
    }
    private ObservableList<Procedure> previousProceduresObservableList;
    private ObservableList<Procedure> pendingProceduresObservableList;

    /**
     * initializes and refreshes the current and past conditions tables
     */
    @FXML
    private void makeTable(ArrayList<Condition> curConditions, ArrayList<Condition> pastConditions){                      //TODO need a function to get all current conditions, rather than just all
        //curDiseasesTable.getSortOrder().add(curChronicColumn);}

        //curChronicColumn.setComparator(curChronicColumn.getComparator().reversed());
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

        pastConditionsTable.widthProperty().addListener((source, oldWidth, newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) pastConditionsTable.lookup("TableHeaderRow");
            header.reorderingProperty().addListener(
                    (observable, oldValue, newValue) -> header.setReordering(false));
        });

        curConditionsTable.widthProperty().addListener((source, oldWidth, newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) curConditionsTable.lookup("TableHeaderRow");
            header.reorderingProperty().addListener(
                    (observable, oldValue, newValue) -> header.setReordering(false));
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
     * refreshes current and past conditions table with its up to date data
     */
    @FXML
    protected void refreshConditionTable() {

        Callback<TableColumn, TableCell> cellFactory = p -> new EditingConditionsCell();

        Callback<TableColumn, TableCell> cellFactoryDate = p -> new EditDateCell();

//
//        try {
//            if (curConditionsTable.getItems() != null) { curConditionsTable.getItems().clear(); }
//            if (pastConditionsTable.getItems() != null) { pastConditionsTable.getItems().clear(); }
//        } catch (NullPointerException|UnsupportedOperationException e) {
//            System.out.println();
//        }
        if (curConditionsObservableList != null) {
            curConditionsObservableList = FXCollections.observableArrayList();
        }
        if (pastConditionsObservableList != null) {
            pastConditionsObservableList = FXCollections.observableArrayList();
        }

        if (searchedDonor.getCurrentConditions() != null) {curConditionsObservableList.addAll(searchedDonor.getCurrentConditions());}
        if (searchedDonor.getCuredConditions() != null) {pastConditionsObservableList.addAll(searchedDonor.getCuredConditions());}

        curConditionsTable.setItems(curConditionsObservableList);

        curDescriptionColumn.setCellValueFactory(new PropertyValueFactory("name"));
        curDescriptionColumn.setCellFactory(cellFactory);
        curDescriptionColumn.setOnEditCommit(
                (EventHandler<TableColumn.CellEditEvent<Condition, String>>) t -> {
                    searchedDonor.removeCondition(t.getTableView().getItems().get(
                            t.getTablePosition().getRow()));
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setName(t.getNewValue());
                    searchedDonor.addCondition(t.getTableView().getItems().get(
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
                searchedDonor.removeCondition(t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
                (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setDateOfDiagnosis(t.getNewValue());
                searchedDonor.addCondition(t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
                System.out.println(t.getTableView().getItems().get(t.getTablePosition().getRow()));
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
                searchedDonor.removeCondition(t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
                (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setDateCured(t.getNewValue());
                searchedDonor.addCondition(t.getTableView().getItems().get(
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
                searchedDonor.removeCondition(t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
                (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setDateOfDiagnosis(t.getNewValue());
                searchedDonor.addCondition(t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
            }
            refreshConditionTable();
        });
        pastConditionsTable.getColumns().setAll(pastDescriptionColumn, pastDateOfDiagnosisColumn, pastDateCuredColumn);

        curConditionsTable.sortPolicyProperty().set(new Callback<TableView<Condition>, Boolean>() {
            @Override
            public Boolean call(TableView<Condition> param) {
                Comparator<Condition> comparator = new Comparator<Condition>() {
                    @Override
                    public int compare(Condition o1, Condition o2) {
                        if (o1.getChronic() && o2.getChronic()) {
                            if (param.getComparator() == null) {
                                return 0;
                            } else {
                                return param.getComparator().compare(o1,o2);
                            }
                        } else if (o1.getChronic()) {
                            return -1;
                        } else if (o2.getChronic()) {
                            return 1;
                        } else if (param.getComparator() == null) {
                            return 0;
                        } else {
                            return param.getComparator().compare(o1,o2);
                        }
                    }
                };
                if (curConditionsTable.getItems().size() > 1) { // Can't sort one item
                    FXCollections.sort(curConditionsTable.getItems(), comparator);
                }
                return true;
            }
        });

        forceConditionSortOrder();
        refreshPageElements();

    }

    /**
     * forces the sort order of the conditions tables to default to the diagnoses date in Descending order
     */
    @FXML
    private void forceConditionSortOrder() {
        curConditionsTable.getSortOrder().clear();
        curConditionsTable.getSortOrder().add(curDateOfDiagnosisColumn);
        curDateOfDiagnosisColumn.setSortType(TableColumn.SortType.DESCENDING);
        pastConditionsTable.getSortOrder().clear();
        pastConditionsTable.getSortOrder().add(pastDateOfDiagnosisColumn);
        pastDateOfDiagnosisColumn.setSortType(TableColumn.SortType.DESCENDING);
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
            ConditionAddController controller = fxmlLoader.<ConditionAddController>getController();
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
//        Profile currentDonor;
//        if (searchedDonor != null) {
//            currentDonor = searchedDonor;
//        } else {
//            currentDonor = getCurrentProfile();
//        }



        ArrayList<Condition> conditions = convertConditionObservableToArray(pastConditionsTable.getSelectionModel().getSelectedItems());
        conditions.addAll(convertConditionObservableToArray(curConditionsTable.getSelectionModel().getSelectedItems()));

        for (int i = 0; i<conditions.size(); i++) {
            if (conditions.get(i) != null) { searchedDonor.removeCondition(conditions.get(i));}
        }

        refreshConditionTable();
    }

    @FXML
    public void handleAddProcedureButtonClicked(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/ProcedureAdd.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            ProcedureAddController controller = fxmlLoader.getController();
            controller.init(this);

            Stage stage = new Stage();
            stage.setTitle("Add a Procedure");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }

    }

    /**
     * Removes the selected procedure and refreshes the table
     * @param actionEvent
     */
    @FXML
    public void handleDeleteProcedureButtonClicked(ActionEvent actionEvent) {
        Profile currentDonor;
        if (searchedDonor != null) {
            currentDonor = searchedDonor;
        } else {
            currentDonor = getCurrentProfile();
        }

        Procedure procedure = (Procedure) pendingProcedureTable.getSelectionModel().getSelectedItem();
        if (procedure == null) { procedure = (Procedure) previousProcedureTable.getSelectionModel().getSelectedItem(); }
        if (procedure == null) { return; }

        currentDonor.removeProcedure(procedure);

        refreshProcedureTable();

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

        showLoginScene(event);
    }

    /**
     * Button handler to undo last action.
     * @param event clicking on the undo button.
     */
    @FXML
    private void handleUndoButtonClicked(ActionEvent event) {
        undo();
    }

    /**
     * Button handler to redo last undo action.
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event) {
        redo();
    }

    /**
     * Button handler to make fields editable.
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {
//        Profile currentDonor;
//        if (searchedDonor != null) {
//            currentDonor = searchedDonor;
//        } else {
//            currentDonor = getCurrentProfile();
//        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProfileEdit.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ProfileEditController controller = fxmlLoader.<ProfileEditController>getController();
        controller.setDonor(searchedDonor);
        controller.setIsClinician(isClinician);
        controller.initialize();

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setScene(scene);
        appStage.show();
    }

    /**
     * Button handler to view a drugs active ingredients
     * @param event clicking on the active ingredients button
     */
    @FXML
    private  void handleViewActiveIngredients(ActionEvent event) {

        Drug drug = tableViewHistoricMedications.getSelectionModel().getSelectedItem();
        if (drug == null) { drug = tableViewCurrentMedications.getSelectionModel().getSelectedItem(); }
        if (drug == null) { return; }

        ArrayList<String> activeIngredients = null;
        try {
            activeIngredients = getActiveIngredients(drug.getDrugName());
        } catch (IOException e) {
            e.printStackTrace();
            tableViewActiveIngredients.setPlaceholder(new Label("There was an error getting active ingredient data"));
        }

        tableViewActiveIngredients.getItems().clear();

        if (activeIngredients == null || activeIngredients.isEmpty()) {
            tableViewActiveIngredients.setPlaceholder(new Label("There is no active ingredient for this drug"));
        } else {
            ObservableList<String> activeIngredientsList = FXCollections.observableArrayList();
            activeIngredientsList.add("Active ingredients for " + drug.getDrugName() + ":");
            activeIngredientsList.addAll(activeIngredients);

            tableViewActiveIngredients.setItems(activeIngredientsList);
            tableColumnActiveIngredients.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
            tableViewActiveIngredients.getColumns().setAll(tableColumnActiveIngredients);
        }
    }

    /**
     * Button handler to add medications to the current medications for the current profile.
     * @param event clicking on the add button.
     */
    @FXML
    private void handleAddNewMedications(ActionEvent event)  {

        if (!textFieldMedicationSearch.getText().isEmpty()) {
            String medicationName = textFieldMedicationSearch.getText();

            searchedDonor.addDrug(new Drug(medicationName));

            refreshMedicationsTable();
        }
    }

    /**
     * Converts ObservableList of drugs to ArrayList of drugs.
     * @param drugs ObservableList of drugs.
     * @return ArrayList of drugs.
     */
    public ArrayList<Drug> convertObservableToArray(ObservableList<Drug> drugs) {
        ArrayList<Drug> toReturn = new ArrayList<>();
        for (int i = 0; i<drugs.size(); i++) {
            if (drugs.get(i) != null) { toReturn.add(drugs.get(i)); }
        }
        return toReturn;
    }

    /**
     * Button handler to get and display drug interactions on TableView tableViewDrugInteractionsName and
     * tableViewDrugInteractions.
     * @param event clicking on the show interactions button.
     */
    @FXML
    private void handleShowInteractions(ActionEvent event) {
        ArrayList<Drug> drugs = convertObservableToArray(tableViewCurrentMedications.getSelectionModel().getSelectedItems());

        Map<String, String> interactionsRaw;

        if (drugs.size() != 2) {
            if (drugs.size() == 1) {
                Drug toAdd = tableViewHistoricMedications.getSelectionModel().getSelectedItem();
                if (toAdd == null) {
                    tableViewDrugInteractionsNames.setPlaceholder(new Label("Please select two drugs"));
                    return;
                }
                drugs.add(toAdd);
            }
            else if (drugs.size() == 0) {
                drugs = convertObservableToArray(tableViewHistoricMedications.getSelectionModel().getSelectedItems());
            }

            if (drugs.size() != 2) {
                tableViewDrugInteractionsNames.setPlaceholder(new Label("Please select two drugs"));
                return; }
        }

        try {
            interactionsRaw = MedicationDataIO
                    .getDrugInteractions(drugs.get(0).getDrugName(), drugs.get(1).getDrugName(), searchedDonor.getGender(), searchedDonor.getAge());

            if (interactionsRaw.isEmpty()) {
                tableViewDrugInteractions.setPlaceholder(new Label("There are no interactions for these drugs"));
            }

            ObservableList<String> drugsList = FXCollections.observableArrayList();
            drugsList.add("Interactions between:");
            drugsList.add(drugs.get(0).getDrugName());
            drugsList.add(drugs.get(1).getDrugName());

            interactions = FXCollections.observableArrayList(interactionsRaw.entrySet());

            tableViewDrugInteractionsNames.getItems().clear();
            tableViewDrugInteractions.getItems().clear();

            tableViewDrugInteractionsNames.setItems(drugsList);
            tableColumnDrugInteractions.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
            tableViewDrugInteractionsNames.getColumns().setAll(tableColumnDrugInteractions);

            tableViewDrugInteractions.setItems(interactions);
            tableColumnSymptoms.setCellValueFactory((TableColumn.CellDataFeatures<Map.Entry<String, String>, String> param) -> new SimpleStringProperty(param.getValue().getKey()));
            tableColumnDuration.setCellValueFactory((TableColumn.CellDataFeatures<Map.Entry<String, String>, String> param) -> new SimpleStringProperty(param.getValue().getValue()));
            tableViewDrugInteractions.getColumns().setAll(tableColumnSymptoms, tableColumnDuration);
        } catch (IOException e) {
            tableViewDrugInteractions.setPlaceholder(new Label("There was an error getting interaction data"));
        }
    }

    /**
     * Button handler to remove medications from the current medications and move them to historic.
     * @param event clicking on the add button.
     */
    @FXML
    private void handleMoveMedicationToHistoric(ActionEvent event)  {

        ArrayList<Drug> drugs = convertObservableToArray(tableViewCurrentMedications.getSelectionModel().getSelectedItems());

        for (int i = 0; i<drugs.size(); i++) {
            if (drugs.get(i) != null) { searchedDonor.moveDrugToHistory(drugs.get(i));}
        }


        refreshMedicationsTable();
    }

    /**
     * Button handler to remove medications from the historic list and add them back to the current list of drugs.
     * @param event clicking on the add button.
     */
    @FXML
    private void handleMoveMedicationToCurrent(ActionEvent event)   {

        ArrayList<Drug> drugs = convertObservableToArray(tableViewHistoricMedications.getSelectionModel().getSelectedItems());

        for (int i = 0; i<drugs.size(); i++) {
            if (drugs.get(i) != null) { searchedDonor.moveDrugToCurrent(drugs.get(i));}
        }

        refreshMedicationsTable();
    }

    /**
     * Button handler to delete medications
     * @param event clicking on the delete button.
     */
    @FXML
    private void handleDelete(ActionEvent event)  {

        ArrayList<Drug> drugs = convertObservableToArray(tableViewCurrentMedications.getSelectionModel().getSelectedItems());
        drugs.addAll(convertObservableToArray(tableViewHistoricMedications.getSelectionModel().getSelectedItems()));

        for (int i = 0; i<drugs.size(); i++) {
            if (drugs.get(i) != null) { searchedDonor.deleteDrug(drugs.get(i));}
        }

        refreshMedicationsTable();
    }


    private void delayedRequest(String substring) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<String> suggestions = getSuggestionList(substring);
                            ArrayList<MenuItem> menuItems = new ArrayList<>();
                            for (String drug : suggestions) {
                                menuItems.add(new Menu(drug));
                            }
                            final ContextMenu suggestionMenu = new ContextMenu();
                            suggestionMenu.getItems().setAll(menuItems);
                            textFieldMedicationSearch.setContextMenu(suggestionMenu);
                            suggestionMenu.show(textFieldMedicationSearch,
                                    Side.BOTTOM, 0, 0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000
        );
    }


    /**
     * Set the listener for the change of value in the medication search field. Also binds the
     * suggestion list to the field, and a listener for the enter key to add the drug to the current
     * medication table.
     */
    @FXML
    private void setMedicationSearchFieldListener() {
        textFieldMedicationSearch.textProperty().addListener((observable, oldValue, newValue) ->  {
            if (!oldValue.equals(newValue)) {
//                delayedRequest(newValue);
                try {
                    ArrayList<String> suggestions = getSuggestionList(newValue);
                    ArrayList<MenuItem> menuItems = new ArrayList<>();
                    for (String drug : suggestions) {
                        MenuItem temp = new MenuItem(drug);
                        temp.setOnAction(event -> {
                            MenuItem eventItem = (MenuItem)event.getTarget();
                            textFieldMedicationSearch.setText(eventItem.getText());
                            suggestionMenu.hide();
                        });
                        menuItems.add(temp);
                    }
                    suggestionMenu.getItems().setAll(menuItems);
                    textFieldMedicationSearch.setContextMenu(suggestionMenu);
                    suggestionMenu.show(textFieldMedicationSearch, Side.BOTTOM, 0, 0);
                    menuItems.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        textFieldMedicationSearch.setOnKeyPressed(event -> {

            if (event.getCode() == KeyCode.ENTER) {

                String medicationName = textFieldMedicationSearch.getText();
                if (!medicationName.equals("")) {
                    searchedDonor.addDrug(new Drug(medicationName));
                }
                textFieldMedicationSearch.clear();
            }
            refreshMedicationsTable();
        });
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
        makeProcedureTable(currentDonor.getPreviousProcedures(), currentDonor.getPendingProcedures());

        refreshProcedureTable();

        makeTable(currentDonor.getCurrentConditions(), currentDonor.getCuredConditions());
        refreshConditionTable();

        try {
            donorFullNameLabel
                    .setText(currentDonor.getFullName());
            donorStatusLabel.setText(donorStatusLabel.getText() + "Unregistered");
            receiverStatusLabel.setText(receiverStatusLabel.getText() + "Unregistered");
            organsRequiredLabel.setText("");

            if (currentDonor.getDonor() != null && currentDonor.getDonor()) {
                if (currentDonor.getOrgansDonated().size() > 0) {
                    donorStatusLabel.setText("Donor Status: Registered");
                }
            }

            if (currentDonor.getOrgansRequired().size() < 1) {
                currentDonor.setReceiver(false);
            } else {
                currentDonor.setReceiver(true);
            }

            if (currentDonor.isReceiver()) {
                receiverStatusLabel.setText("Receiver Status: Registered");
                organsRequiredLabel.setText("Organs Required : " + Organ.organSetToString(currentDonor.getOrgansRequired()));
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
            if (currentDonor.getHeight() != null) {
                heightLabel.setText(heightLabel.getText() + currentDonor.getHeight() + "m");
            }
            if (currentDonor.getWeight() != null) {
                weightLabel.setText(weightLabel.getText() + currentDonor.getWeight() + "kg");
            }
            if (currentDonor.getPhone() != null) {
                phoneLabel.setText(phoneLabel.getText() + currentDonor.getPhone());
            }
            if (currentDonor.getEmail() != null) {
                emailLabel.setText(emailLabel.getText() + currentDonor.getEmail());
            }

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
                bmiLabel.setText(bmiLabel.getText() + currentDonor.calculateBMI());
            }
            if (currentDonor.getDateOfBirth() != null) {
                ageLabel.setText(ageLabel.getText() + Integer.toString(currentDonor.calculateAge()));
            }
            if (currentDonor.getId() != null) {
                userIdLabel.setText(userIdLabel.getText() + Integer.toString(currentDonor.getId()));
            }

            organsLabel.setText(organsLabel.getText() + Organ.organSetToString(currentDonor.getOrgansDonating()));

            donationsLabel.setText(donationsLabel.getText() + Organ.organSetToString(currentDonor.getOrgansDonated()));

            if (currentDonor.getSmoker() != null) {
                smokerLabel.setText(smokerLabel.getText() + currentDonor.getSmoker());
            }
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
            setMedicationSearchFieldListener();

            refreshConditionTable();

        } catch (Exception e) {
            e.printStackTrace();
            invalidUsername();
        }

    }

    /**
     * Refresh the current and historic medication tables with the most up to date data
     */
    @FXML
    private void refreshMedicationsTable() {

        tableViewCurrentMedications.getItems().clear();
        if (searchedDonor.getCurrentMedications() != null) {currentMedication.addAll(searchedDonor.getCurrentMedications());}
        tableViewHistoricMedications.getItems().clear();
        if (searchedDonor.getHistoryOfMedication() != null) {historicMedication.addAll(searchedDonor.getHistoryOfMedication());}

        tableViewCurrentMedications.setItems(currentMedication);
        tableColumnMedicationNameCurrent.setCellValueFactory(new PropertyValueFactory("drugName"));
        tableViewCurrentMedications.getColumns().setAll(tableColumnMedicationNameCurrent);

        tableViewHistoricMedications.setItems(historicMedication);
        tableColumnMedicationNameHistoric.setCellValueFactory(new PropertyValueFactory("drugName"));
        tableViewHistoricMedications.getColumns().setAll(tableColumnMedicationNameHistoric);

        ProfileDataIO.saveData(getCurrentDatabase(), "example/example.json");
        refreshPageElements();

    }


    /**
     * Enables the relevant buttons on medications tab for how many drugs are selected
     */
    @FXML
    private void refreshPageElements() {

        ArrayList<Drug> drugs = convertObservableToArray(tableViewCurrentMedications.getSelectionModel().getSelectedItems());
        ArrayList<Drug> allDrugs = convertObservableToArray(tableViewCurrentMedications.getSelectionModel().getSelectedItems());
        allDrugs.addAll(convertObservableToArray(tableViewCurrentMedications.getSelectionModel().getSelectedItems()));

        if (allDrugs.size() == 0) {
            buttonMedicationHistoricToCurrent.setDisable(true);
            buttonMedicationCurrentToHistoric.setDisable(true);
            buttonDeleteMedication.setDisable(true);
        } else {
            buttonMedicationHistoricToCurrent.setDisable(false);
            buttonMedicationCurrentToHistoric.setDisable(false);
            buttonDeleteMedication.setDisable(false);
        }
        buttonMedicationHistoricToCurrent.setDisable(false);
        buttonMedicationCurrentToHistoric.setDisable(false);
        buttonDeleteMedication.setDisable(false);
        buttonShowDrugInteractions.setDisable(false);


        if (drugs.size() != 2) {
            if (drugs.size() == 1) {
                Drug toAdd = tableViewHistoricMedications.getSelectionModel().getSelectedItem();
                if (toAdd != null) { drugs.add(toAdd); }
            } else if (drugs.size() == 0) {
                drugs = convertObservableToArray(tableViewHistoricMedications.getSelectionModel().getSelectedItems());
            }

            if (drugs.size() != 2) {
                buttonShowDrugInteractions.setDisable(true);
                return;
            }
        } else {
            buttonShowDrugInteractions.setDisable(false);
        }



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

    /**
     * Initializes and refreshes the previous and pending procedure tables
     */
    @FXML
    private void makeProcedureTable(ArrayList<Procedure> previousProcedures, ArrayList<Procedure> pendingProcedures) {
        //curDiseasesTable.getSortOrder().add(curChronicColumn);
        Profile currentDonor;
        if (searchedDonor != null) {
            currentDonor = searchedDonor;
        } else {
            currentDonor = getCurrentProfile();
        }

        pendingDateColumn.setComparator(pendingDateColumn.getComparator().reversed());

        if (previousProcedures != null) {
            previousProceduresObservableList = FXCollections.observableArrayList(previousProcedures);}
        else {
            previousProceduresObservableList = FXCollections.observableArrayList(); }
        if (pendingProcedures != null) {
            pendingProceduresObservableList = FXCollections.observableArrayList(pendingProcedures);}
        else {
            pendingProceduresObservableList = FXCollections.observableArrayList(); }

        if (previousProcedures != null) {
            refreshProcedureTable();
        }
        pendingProcedureTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    pendingProcedureTable.getSelectionModel().getSelectedItem() != null) {
                try {
                    createNewProcedureWindow((Procedure) pendingProcedureTable.getSelectionModel().getSelectedItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        previousProcedureTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    previousProcedureTable.getSelectionModel().getSelectedItem() != null) {
                try {
                    createNewProcedureWindow((Procedure) previousProcedureTable.getSelectionModel().getSelectedItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Creates a new edit procedure window
     */
    @FXML
    public void createNewProcedureWindow(Procedure selectedProcedure) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/ProcedureEdit.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            ProcedureEditController controller = fxmlLoader.<ProcedureEditController>getController();
            controller.initialize(selectedProcedure, this);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Refreshes the procedure table, updating it with the current values
     * pendingProcedureTable;
     */
    @FXML
    public void refreshProcedureTable() {
        Profile currentDonor;
        if (searchedDonor != null) {
            currentDonor = searchedDonor;
        } else {
            currentDonor = getCurrentProfile();
        }

        if (previousProceduresObservableList == null) {
            previousProceduresObservableList = FXCollections.observableArrayList();
        }
        if (pendingProceduresObservableList == null) {
            pendingProceduresObservableList = FXCollections.observableArrayList();
        }

        // update all procedures
        if (currentDonor.getAllProcedures() != null) {
            for (Procedure procedure : currentDonor.getAllProcedures()) {
                procedure.update();
            }
        }

        pendingProcedureTable.getItems().clear();
        previousProcedureTable.getItems().clear();
        if (currentDonor.getPendingProcedures() != null) {
            pendingProceduresObservableList.addAll(currentDonor.getPendingProcedures());}
        if (currentDonor.getPreviousProcedures() != null) {
            previousProceduresObservableList.addAll(currentDonor.getPreviousProcedures());
        } else {
            return;
        }

        previousProcedureTable.setItems(previousProceduresObservableList);
        previousSummaryColumn.setCellValueFactory(new PropertyValueFactory("summary"));
        previousDateColumn.setCellValueFactory(new PropertyValueFactory("date"));
        previousAffectsColumn.setCellValueFactory(new PropertyValueFactory("affectsOrgansText"));
        previousProcedureTable.getColumns().setAll(previousSummaryColumn, previousDateColumn, previousAffectsColumn);

        pendingProcedureTable.setItems(pendingProceduresObservableList);
        pendingSummaryColumn.setCellValueFactory(new PropertyValueFactory("summary"));
        pendingDateColumn.setCellValueFactory(new PropertyValueFactory("date"));
        pendingAffectsColumn.setCellValueFactory(new PropertyValueFactory("affectsOrgansText"));
        pendingProcedureTable.getColumns().setAll(pendingSummaryColumn, pendingDateColumn, pendingAffectsColumn);

        forceSortProcedureOrder();
    }

    /**
     * Forces the sort order of the procedure table so that most recent procedures are always at the top
     */
    @FXML
    private void forceSortProcedureOrder() {
        previousProcedureTable.getSortOrder().clear();
        previousProcedureTable.getSortOrder().add(previousDateColumn);
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

        tableViewCurrentMedications.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
        tableViewHistoricMedications.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        curChronicColumn.setSortable(false);


        if(searchedDonor != null) {
            setPage(searchedDonor);

            //if(!isClinician) {
                hideItems();
            //}
            //Profile currentDonor = getCurrentProfile();
        }

        if (searchedDonor != null) {
            refreshMedicationsTable();
            makeProcedureTable(searchedDonor.getPreviousProcedures(), searchedDonor.getPendingProcedures());

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
     * @param profile
     */
    public void setLoggedInProfile(Profile profile) {
        isClinician = false;
        searchedDonor = profile;
        //setPage(searchedDonor);
    }

}
