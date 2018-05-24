package odms.controller;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import odms.data.MedicationDataIO;
import odms.data.ProfileDataIO;
import odms.history.History;
import odms.medications.Drug;
import odms.profile.Condition;
import odms.profile.Procedure;
import odms.profile.Profile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static odms.controller.AlertController.invalidUsername;
import static odms.controller.AlertController.saveChanges;
import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.data.MedicationDataIO.getActiveIngredients;
import static odms.data.MedicationDataIO.getSuggestionList;


public class ProfileDisplayController extends CommonController {

    private Boolean isOpenedByClinician = false;
    protected Profile currentProfile;
    private ObservableList<Drug> currentMedication = FXCollections.observableArrayList();
    private ObservableList<Drug> historicMedication = FXCollections.observableArrayList();
    private ObservableList<Map.Entry<String, String>> interactions;
    private ContextMenu suggestionMenu = new ContextMenu();
    private ObservableList<Condition> curConditionsObservableList;
    private ObservableList<Condition> pastConditionsObservableList;

    protected ObjectProperty<Profile> currentProfileBound = new SimpleObjectProperty<>();

    // Displays in IntelliJ as unused but is a false positive
    // The FXML includes operate this way and allow access to the instantiated controller.
    @FXML
    private AnchorPane profileOrganOverview;
    @FXML
    private ProfileOrganOverviewController profileOrganOverviewController;

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
    private TextArea historyView;

    @FXML
    private Label bmiLabel;

    @FXML
    private Label ageLabel;

    @FXML
    private Label userIdLabel;

    @FXML
    private Button logoutButton;

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
    private Button buttonSaveMedications;

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

    @FXML
    private Button buttonShowDrugInteractions;

    @FXML
    private Button buttonViewActiveIngredients;

    @FXML
    private Button buttonViewMedicationHistory;

    /**
     * Text for showing recent edits.
     */
    @FXML
    public Text editedText;

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
    private RedoController redoController= new RedoController();
    private UndoController undoController= new UndoController();

    /**
     * initializes and refreshes the current and past conditions tables
     */
    @FXML
    private void makeTable(ArrayList<Condition> curConditions, ArrayList<Condition> pastConditions){                      //TODO need a function to get all current conditions, rather than just all
        //curDiseasesTable.getSortOrder().add(curChronicColumn);}

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

        if (curConditionsObservableList != null) {
            curConditionsObservableList = FXCollections.observableArrayList();
        }
        if (pastConditionsObservableList != null) {
            pastConditionsObservableList = FXCollections.observableArrayList();
        }

        if (currentProfile.getCurrentConditions() != null) {curConditionsObservableList.addAll(currentProfile.getCurrentConditions());}
        if (currentProfile.getCuredConditions() != null) {pastConditionsObservableList.addAll(currentProfile.getCuredConditions());}

        curConditionsTable.setItems(curConditionsObservableList);

        curDescriptionColumn.setCellValueFactory(new PropertyValueFactory("name"));
        curDescriptionColumn.setCellFactory(cellFactory);
        curDescriptionColumn.setOnEditCommit(
                (EventHandler<TableColumn.CellEditEvent<Condition, String>>) t -> {
                    currentProfile.removeCondition(t.getTableView().getItems().get(
                            t.getTablePosition().getRow()));
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setName(t.getNewValue());
                    currentProfile.addCondition(t.getTableView().getItems().get(
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

        curDateOfDiagnosisColumn.setOnEditCommit(
                (EventHandler<TableColumn.CellEditEvent<Condition, LocalDate>>) t -> {
                    if (!t.getNewValue().isAfter(LocalDate.now())) {
                        currentProfile.removeCondition(t.getTableView().getItems().get(
                                t.getTablePosition().getRow()));
                        (t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setDateOfDiagnosis(t.getNewValue());
                        currentProfile.addCondition(t.getTableView().getItems().get(
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

        pastDateCuredColumn.setOnEditCommit(
                (EventHandler<TableColumn.CellEditEvent<Condition, LocalDate>>) t -> {
                    if (t.getNewValue().isBefore(
                            t.getTableView().getItems().get(t.getTablePosition().getRow())
                                    .getDateOfDiagnosis())
                            || t.getNewValue().isAfter(LocalDate.now())) {
                    } else {
                        currentProfile.removeCondition(t.getTableView().getItems().get(
                                t.getTablePosition().getRow()));
                        (t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setDateCured(t.getNewValue());
                        currentProfile.addCondition(t.getTableView().getItems().get(
                                t.getTablePosition().getRow()));
                    }
                    refreshConditionTable();
                });

        pastDateOfDiagnosisColumn.setCellValueFactory(new PropertyValueFactory("dateOfDiagnosis"));
        pastDateOfDiagnosisColumn.setCellFactory(cellFactoryDate);

        pastDateOfDiagnosisColumn.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<Condition, LocalDate>>) t -> {
            if (!(t.getNewValue().isAfter(
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).getDateCured())
                    || t.getNewValue().isAfter(LocalDate.now()))) {
                currentProfile.removeCondition(t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
                (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setDateOfDiagnosis(t.getNewValue());
                currentProfile.addCondition(t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
            }
            refreshConditionTable();
        });
        pastConditionsTable.getColumns().setAll(pastDescriptionColumn, pastDateOfDiagnosisColumn, pastDateCuredColumn);

        curConditionsTable.sortPolicyProperty().set(
                (Callback<TableView<Condition>, Boolean>) param -> {
                    Comparator<Condition> comparator = (o1, o2) -> {
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
                    };
                    FXCollections.sort(curConditionsTable.getItems(), comparator);
                    return true;
                });

        refreshPageElements();

    }

    /**
     * Button handler to add condition to the current conditions for the current profile.
     * @param event clicking on the add button.
     */
    @FXML
    private void handleAddNewCondition(ActionEvent event) throws IOException {
        try {
            Node source = (Node) event.getSource();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/AddCondition.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            ConditionAddController controller = fxmlLoader.<ConditionAddController>getController();
            controller.init(this);

            Stage stage = new Stage();
            stage.setTitle("Add a Condition");
            stage.initOwner(source.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setResizable(false);
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
        ArrayList<Condition> conditions = convertConditionObservableToArray(
                pastConditionsTable.getSelectionModel().getSelectedItems()
        );
        conditions.addAll(convertConditionObservableToArray(
                curConditionsTable.getSelectionModel().getSelectedItems())
        );
        for (Condition condition : conditions) {
            if (condition != null) {
                currentProfile.removeCondition(condition);
                LocalDateTime currentTime = LocalDateTime.now();
                History action = new History("Profile",currentProfile.getId(),
                        " removed condition","("  +condition.getName()+
                        ","+condition.getDateOfDiagnosis()+","+condition.getChronic()+","+
                        condition.getDateCuredString()+ ")",currentProfile.getCurrentConditions().indexOf(condition),currentTime);
                HistoryController.updateHistory(action);
            }
        }

        refreshConditionTable();
    }

    @FXML
    public void handleAddProcedureButtonClicked(ActionEvent actionEvent) {
        try {
            Node source = (Node) actionEvent.getSource();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/ProcedureAdd.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            ProcedureAddController controller = fxmlLoader.getController();
            controller.init(this);

            Stage stage = new Stage();
            stage.setTitle("Add a Procedure");
            stage.initOwner(source.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.setResizable(false);
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

        Procedure procedure = (Procedure) pendingProcedureTable.getSelectionModel().getSelectedItem();
        if (procedure == null) {
            procedure = (Procedure) previousProcedureTable.getSelectionModel().getSelectedItem();
        }

        if (procedure == null) {
            return;
        }

        currentProfile.removeProcedure(procedure);
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

        for (Condition condition : conditions) {
            if (condition != null) {

                condition.setIsChronic(!condition.getChronic());
                if (condition.getChronic()) {
                    condition.setChronicText("CHRONIC");
                    condition.setIsCured(false);
                } else {
                    condition.setChronicText("");
                }

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

        for (Condition condition : conditions) {
            if (condition != null) {

                if (!condition.getChronic()) {
                    condition.setIsCured(!condition.getCured());
                } else {
                    System.out.println("Condition must be unmarked as Chronic before being Cured!");
                }

                if (condition.getCured()) {
                    condition.setDateCured(LocalDate.now());
                } else {
                    condition.setDateCured(null);
                }

            }

            refreshConditionTable();
        }
    }


    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
        showLoginScene(event);
    }

    /**
     * Button handler to undo last action.
     * @param event clicking on the undo button.
     */
    @FXML
    private void handleUndoButtonClicked(ActionEvent event) {
        undoController.undo(GuiMain.getCurrentDatabase());
    }

    /**
     * Button handler to redo last undo action.
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event) {
        redoController.redo(GuiMain.getCurrentDatabase());
    }

    /**
     * Button handler to make fields editable.
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProfileEdit.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ProfileEditController controller = fxmlLoader.<ProfileEditController>getController();
        controller.setCurrentProfile(currentProfile);
        controller.setIsClinician(isOpenedByClinician);
        controller.initialize();

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setScene(scene);
        appStage.show();
    }

    /**
     * Button handler to save changes made on medications tab. If a save is made else where in the program changes to
     * the medications will also be saved.
     * @param event clicking on the save button
     */
    @FXML
    private void handleSaveMedications(ActionEvent event) throws IOException {
        if (saveChanges()) {
            showNotification("Medications Tab", event);
            ProfileDataIO.saveData(getCurrentDatabase());
        }
    }

    /**
     * Button handler to view a drugs active ingredients
     * @param event clicking on the active ingredients button
     */
    @FXML
    private  void handleViewActiveIngredients(ActionEvent event) {

        Drug drug = tableViewHistoricMedications.getSelectionModel().getSelectedItem();
        if (drug == null) {
            drug = tableViewCurrentMedications.getSelectionModel().getSelectedItem();
        }

        if (drug == null) {
            return;
        }

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

            currentProfile.addDrug(new Drug(medicationName));
            String data = currentProfile.getMedicationTimestamps().get(currentProfile.getMedicationTimestamps().size()-1);
            History history = new History("Profile",currentProfile.getId(), "added drug",
                    medicationName,Integer.parseInt(data.substring(data.indexOf("index of")+8,data.indexOf(" at"))),LocalDateTime.now());
            HistoryController.updateHistory(history);

            refreshMedicationsTable();
        }
    }

    /**
     * Converts ObservableList of drugs to ArrayList of drugs.
     * @param drugs ObservableList of drugs.
     * @return ArrayList of drugs.
     */
    private ArrayList<Drug> convertObservableToArray(ObservableList<Drug> drugs) {
        ArrayList<Drug> toReturn = new ArrayList<>();
        for (Drug drug : drugs) {
            if (drug != null) {
                toReturn.add(drug);
            }
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
        ArrayList<Drug> drugs;

        Map<String, String> interactionsRaw;

        if (convertObservableToArray(tableViewCurrentMedications.getSelectionModel().getSelectedItems()).size() == 2) {
            drugs = convertObservableToArray(tableViewCurrentMedications.getSelectionModel().getSelectedItems());
        } else {
            if (tableViewHistoricMedications.getSelectionModel().getSelectedItems().size() == 2) {
                drugs = convertObservableToArray(tableViewHistoricMedications.getSelectionModel().getSelectedItems());
            } else {
                drugs = convertObservableToArray(tableViewCurrentMedications.getSelectionModel().getSelectedItems());
                drugs.add(tableViewHistoricMedications.getSelectionModel().getSelectedItem());
            }
        }

        try {
            interactionsRaw = MedicationDataIO.getDrugInteractions(
                    drugs.get(0).getDrugName(),
                    drugs.get(1).getDrugName(),
                    currentProfile.getGender(),
                    currentProfile.getAge()
            );

            tableViewDrugInteractionsNames.getItems().clear();
            tableViewDrugInteractions.getItems().clear();
            ObservableList<String> drugsList = FXCollections.observableArrayList();
            drugsList.add("Interactions between:");
            drugsList.add(drugs.get(0).getDrugName());
            drugsList.add(drugs.get(1).getDrugName());
            tableViewDrugInteractionsNames.setItems(drugsList);
            tableColumnDrugInteractions.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
            tableViewDrugInteractionsNames.getColumns().setAll(tableColumnDrugInteractions);

            if (interactionsRaw.isEmpty()) {
                tableViewDrugInteractions.setPlaceholder(new Label("There are no interactions for these drugs"));
            } else if (interactionsRaw.containsKey("error")) {
                tableViewDrugInteractions.setPlaceholder(new Label("There was an error getting interaction data"));
            } else {
                interactions = FXCollections.observableArrayList(interactionsRaw.entrySet());
                tableViewDrugInteractions.setItems(interactions);
                tableColumnSymptoms.setCellValueFactory((TableColumn.CellDataFeatures
                                                                 <Map.Entry<String, String>, String> param) ->
                        new SimpleStringProperty(param.getValue().getKey()));
                tableColumnDuration.setCellValueFactory((TableColumn.CellDataFeatures
                                                                 <Map.Entry<String, String>, String> param) ->
                        new SimpleStringProperty(param.getValue().getValue()));
                tableViewDrugInteractions.getColumns().setAll(tableColumnSymptoms, tableColumnDuration);
            }
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

        for (Drug drug: drugs) {
            if (drug != null) {
                currentProfile.moveDrugToHistory(drug);
                String data = currentProfile.getMedicationTimestamps().
                        get(currentProfile.getMedicationTimestamps().size()-1);
                History history = new History("Profile",currentProfile.getId(),
                        "stopped",drug.getDrugName(),
                        Integer.parseInt(data.substring(data.indexOf("index of")+8,
                                data.indexOf(" at"))),LocalDateTime.now());
                HistoryController.updateHistory(history);
            }
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

        for (Drug drug: drugs) {
            if (drug != null) {
                currentProfile.moveDrugToCurrent(drug);
                String data = currentProfile.getMedicationTimestamps().get(currentProfile.getMedicationTimestamps().size()-1);
                History history = new History("Profile",currentProfile.getId(),
                        "started",drug.getDrugName(),Integer.parseInt(data.substring
                        (data.indexOf("index of")+8,data.indexOf(" at"))),LocalDateTime.now());
                HistoryController.updateHistory(history);
            }
        }

        refreshMedicationsTable();
    }

    /**
     * Button handler to delete medications
     * @param event clicking on the delete button.
     */
    @FXML
    private void handleDeleteMedication(ActionEvent event)  {
        ArrayList<Drug> drugs = convertObservableToArray(tableViewCurrentMedications.getSelectionModel().getSelectedItems());
        drugs.addAll(convertObservableToArray(tableViewHistoricMedications.getSelectionModel().getSelectedItems()));

        for (Drug drug: drugs) {
            if (drug != null) {
                currentProfile.deleteDrug(drug);
                String data = currentProfile.getMedicationTimestamps().get(currentProfile.getMedicationTimestamps().size()-1);
                History history;
                if(data.contains("history")) {
                    history = new History("Profile",currentProfile.getId(), "removed drug",
                            drug.getDrugName(),
                            Integer.parseInt(data.substring(data.indexOf("index of")+8,data.indexOf(" at"))),LocalDateTime.now());
                } else {
                    history = new History("Profile",currentProfile.getId(), "removed drug from history",
                            drug.getDrugName(),
                            Integer.parseInt(data.substring(data.indexOf("index of")+8,data.indexOf(" at"))),LocalDateTime.now());
                }
                HistoryController.updateHistory(history);
            }
        }

        refreshMedicationsTable();
    }

    /**
     * Button handler to open medicationHistory scene
     * @param event clicking on delete button.
     * @throws IOException If MedicationHistory fxml is not found.
     */
    @FXML
    private void handleViewMedicationHistory(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/MedicationHistory.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        MedicationHistory controller = fxmlLoader.getController();
        controller.setProfile(currentProfile);
        controller.initialize();
        Stage stage = new Stage();
        stage.setTitle("Medication history");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();
    }

    /**
     * Set the listener for the change of value in the medication search field. Also binds the
     * suggestion list to the field, and a listener for the enter key to add the drug to the current
     * medication table.
     */
    @FXML
    private void setMedicationSearchFieldListener() {
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.5));
        textFieldMedicationSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished(ae -> {
                try {
                    ArrayList<String> suggestions = getSuggestionList(newValue);
                    ArrayList<MenuItem> menuItems = new ArrayList<>();
                    for (String drug : suggestions) {
                        MenuItem temp = new MenuItem(drug);
                        temp.setOnAction(event -> {
                            MenuItem eventItem = (MenuItem) event.getTarget();
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
            });
            pauseTransition.playFromStart();
        });

        textFieldMedicationSearch.setOnKeyPressed(event -> {

            if (event.getCode() == KeyCode.ENTER) {

                String medicationName = textFieldMedicationSearch.getText();
                if (!medicationName.equals("")) {
                    currentProfile.addDrug(new Drug(medicationName));
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
        makeProcedureTable(currentDonor.getPreviousProcedures(), currentDonor.getPendingProcedures());

        refreshProcedureTable();

        makeTable(currentDonor.getCurrentConditions(), currentDonor.getCuredConditions());
        refreshConditionTable();

        try {
            donorFullNameLabel.setText(currentDonor.getFullName());
            donorStatusLabel.setText(donorStatusLabel.getText() + "Unregistered");
            receiverStatusLabel.setText(receiverStatusLabel.getText() + "Unregistered");

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
            if (currentDonor.getSmoker() != null) {
                smokerLabel.setText(smokerLabel.getText() + currentDonor.getSmoker());
            }

            String history = ProfileDataIO.getHistory();
            history = history.replace(",", " ").replace("]", "").
                    replace("[", "").replace("\\u003d", "=");
            String[] histories = history.split("\"");
            String historyDisplay = "";
            for (String h : histories) {
                if (!h.equals("") && h.contains("Profile "+currentProfile.getId()+" ")) {
                    historyDisplay += h + "\n";
                }
            }

            historyView.setText(historyDisplay);
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
        if (currentProfile.getCurrentMedications() != null) {
            currentMedication.addAll(currentProfile.getCurrentMedications());
        }
        tableViewHistoricMedications.getItems().clear();
        if (currentProfile.getHistoryOfMedication() != null) {
            historicMedication.addAll(currentProfile.getHistoryOfMedication());
        }

        tableViewCurrentMedications.setItems(currentMedication);
        tableColumnMedicationNameCurrent.setCellValueFactory(new PropertyValueFactory("drugName"));
        tableViewCurrentMedications.getColumns().setAll(tableColumnMedicationNameCurrent);

        tableViewHistoricMedications.setItems(historicMedication);
        tableColumnMedicationNameHistoric.setCellValueFactory(new PropertyValueFactory("drugName"));
        tableViewHistoricMedications.getColumns().setAll(tableColumnMedicationNameHistoric);

        refreshPageElements();
    }

    /**
     * Enables the relevant buttons on medications tab for how many drugs are selected
     */
    @FXML
    private void refreshPageElements() {
        ArrayList<Drug> drugs = convertObservableToArray(
                tableViewCurrentMedications.getSelectionModel().getSelectedItems()
        );
        ArrayList<Drug> allDrugs = convertObservableToArray(
                tableViewCurrentMedications.getSelectionModel().getSelectedItems()
        );
        allDrugs.addAll(convertObservableToArray(
                tableViewCurrentMedications.getSelectionModel().getSelectedItems())
        );

        disableButtonsIfNoItems(allDrugs);

        buttonMedicationHistoricToCurrent.setDisable(false);
        buttonMedicationCurrentToHistoric.setDisable(false);
        buttonDeleteMedication.setDisable(false);
        buttonShowDrugInteractions.setDisable(false);

        if (drugs.size() != 2) {
            if (drugs.size() == 1) {
                Drug toAdd = tableViewHistoricMedications.getSelectionModel().getSelectedItem();
                if (toAdd != null) {
                    drugs.add(toAdd);
                }
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

        ArrayList<Condition> allConditions = convertConditionObservableToArray(
                curConditionsTable.getSelectionModel().getSelectedItems());
        allConditions.addAll(convertConditionObservableToArray(
                pastConditionsTable.getSelectionModel().getSelectedItems()));
        disableButtonsIfNoItems(allConditions);
    }

    /**
     * Support function to disable medications buttons if no items present
     * @param items to check
     */
    private void disableButtonsIfNoItems(List<?> items) {
        if (items.size() == 0) {
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
        if (isOpenedByClinician) {
            //User is a clinician looking at donors profile, maximise functionality
            curConditionsTable.setEditable(true);
            pastConditionsTable.setEditable(true);
            toggleChronicButton.setDisable(false);
            toggleChronicButton.setVisible(true);
            toggleCuredButton.setDisable(false);
            toggleCuredButton.setVisible(true);
            addNewConditionButton.setVisible(true);
            deleteConditionButton.setVisible(true);
            addNewProcedureButton.setVisible(true);
            deleteProcedureButton.setVisible(true);
            buttonSaveMedications.setVisible(true);
            buttonDeleteMedication.setVisible(true);
            buttonShowDrugInteractions.setVisible(true);
            buttonViewActiveIngredients.setVisible(true);
            buttonAddMedication.setVisible(true);
            buttonMedicationCurrentToHistoric.setVisible(true);
            buttonMedicationHistoricToCurrent.setVisible(true);
            textFieldMedicationSearch.setVisible(true);
            tableViewActiveIngredients.setVisible(true);
            tableViewDrugInteractionsNames.setVisible(true);
            tableViewDrugInteractions.setVisible(true);
            buttonViewMedicationHistory.setVisible(true);

            logoutButton.setVisible(false);
        } else {
            // User is a standard profile, limit functionality
            curConditionsTable.setEditable(false);
            pastConditionsTable.setEditable(false);
            toggleChronicButton.setDisable(true);
            toggleChronicButton.setVisible(false);
            toggleCuredButton.setDisable(true);
            toggleCuredButton.setVisible(false);
            addNewConditionButton.setVisible(false);
            deleteConditionButton.setVisible(false);
            addNewProcedureButton.setVisible(false);
            deleteProcedureButton.setVisible(false);
            buttonSaveMedications.setVisible(false);
            buttonDeleteMedication.setVisible(false);
            buttonShowDrugInteractions.setVisible(false);
            buttonViewActiveIngredients.setVisible(false);
            buttonAddMedication.setVisible(false);
            buttonMedicationCurrentToHistoric.setVisible(false);
            buttonMedicationHistoricToCurrent.setVisible(false);
            textFieldMedicationSearch.setVisible(false);
            tableViewActiveIngredients.setVisible(false);
            tableViewActiveIngredients.setVisible(false);
            tableViewDrugInteractionsNames.setVisible(false);
            tableViewDrugInteractions.setVisible(false);
            buttonViewMedicationHistory.setVisible(false);
        }
    }

    /**
     * Initializes and refreshes the previous and pending procedure tables
     */
    @FXML
    private void makeProcedureTable(ArrayList<Procedure> previousProcedures, ArrayList<Procedure> pendingProcedures) {
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
        if (previousProceduresObservableList == null) {
            previousProceduresObservableList = FXCollections.observableArrayList();
        }
        if (pendingProceduresObservableList == null) {
            pendingProceduresObservableList = FXCollections.observableArrayList();
        }

        // update all procedures
        if (currentProfile.getAllProcedures() != null) {
            for (Procedure procedure : currentProfile.getAllProcedures()) {
                procedure.update();
            }
        }

        pendingProcedureTable.getItems().clear();
        previousProcedureTable.getItems().clear();
        if (currentProfile.getPendingProcedures() != null) {
            pendingProceduresObservableList.addAll(currentProfile.getPendingProcedures());}
        if (currentProfile.getPreviousProcedures() != null) {
            previousProceduresObservableList.addAll(currentProfile.getPreviousProcedures());
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

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    @FXML
    private void onTabOrgansSelected() {
        profileOrganOverviewController.currentProfile.bind(currentProfileBound);
        profileOrganOverviewController.populateOrganLists();
    }

    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize() {

        if (currentProfile != null) {
            currentProfileBound.set(currentProfile);

            setPage(currentProfile);

            refreshMedicationsTable();
            makeProcedureTable(
                    currentProfile.getPreviousProcedures(),
                    currentProfile.getPendingProcedures()
            );
        }

        curConditionsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        pastConditionsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tableViewCurrentMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewHistoricMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        curChronicColumn.setSortable(false);

        hideItems();
        refreshPageElements();
        disableTableHeaderReorder();
    }

    /**
     * sets the profile if it is being opened by a clinician
     * If opened by clinician, set appropriate boolean and profile
     * @param profile to be used
     */
    public void setProfileViaClinician(Profile profile) {
        isOpenedByClinician = true;
        currentProfile = profile;
    }

    /**
     * sets the donor if it was logged in by a user
     * If logged in normally, sets profile
     * @param profile to be used
     */
    public void setProfile(Profile profile) {
        currentProfile = profile;
    }

}
