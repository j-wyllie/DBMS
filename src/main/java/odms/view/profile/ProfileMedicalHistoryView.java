package odms.view.profile;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import odms.controller.EditDateCell;
import odms.controller.EditingConditionsCell;
import odms.controller.condition.ConditionAddController;
import odms.controller.profile.ProfileConditionController;
import odms.model.profile.Condition;
import odms.model.profile.Profile;
import odms.view.CommonView;

public class ProfileMedicalHistoryView extends CommonView {

    private Profile currentProfile;
    private ProfileConditionController controller = new ProfileConditionController(this);
    private Boolean isOpenedByClinician;

    private ObservableList<Condition> curConditionsObservableList;
    private ObservableList<Condition> pastConditionsObservableList;
    @FXML
    private Label chronicConditionsLabel;
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


        /**
     * initializes and refreshes the current and past conditions tables
     */
    @FXML
    private void makeTable(ArrayList<Condition> curConditions,
            ArrayList<Condition> pastConditions) {
        if (curConditions != null) {
            curConditionsObservableList = FXCollections.observableArrayList(curConditions);
        } else {
            curConditionsObservableList = FXCollections.observableArrayList();
        }
        if (pastConditions != null) {
            pastConditionsObservableList = FXCollections.observableArrayList(pastConditions);
        } else {
            pastConditionsObservableList = FXCollections.observableArrayList();
        }
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

    /**
     * Converts the conditions observable array to an arraylist.
     * @param conditions observable list of conditions
     * @return arraylist of conditions
     */
    private ArrayList<Condition> convertConditionObservableToArray(
            ObservableList<Condition> conditions) {
        ArrayList<Condition> toReturn = new ArrayList<>();
        for (Condition condition : conditions) {
            if (condition != null) {
                toReturn.add(condition);
            }
        }
        return toReturn;
    }

    /**
     * refreshes current and past conditions table with its up to date data
     */
    @FXML
    public void refreshConditionTable() {
        Callback<TableColumn, TableCell> cellFactory = p -> new EditingConditionsCell();
        Callback<TableColumn, TableCell> cellFactoryDate = p -> new EditDateCell();

        initializeConditionLists();

        curConditionsTable.setItems(curConditionsObservableList);
        initializeCurrentConditionsColumn(cellFactory);
        initializeCurrentChronicColumn();
        initializeCurrentDateOfDiagnosisColumn(cellFactoryDate);

        curConditionsTable.getColumns()
                .setAll(curDescriptionColumn, curChronicColumn, curDateOfDiagnosisColumn);

        pastConditionsTable.setItems(pastConditionsObservableList);

        initializePastConditionsColumn(cellFactory);
        initializePastDateCuredColumn(cellFactoryDate);
        initializePastDateOfDiagnosisColumn(cellFactoryDate);

        pastConditionsTable.getColumns()
                .setAll(pastDescriptionColumn, pastDateOfDiagnosisColumn, pastDateCuredColumn);

        setCurrentConditionsTableSort();
        refreshPageElements();
        forceConditionSortOrder();

    }

    /**
     * Sets custom sort order of the current conditions table.
     */
    private void setCurrentConditionsTableSort() {
        curConditionsTable.sortPolicyProperty().set(
                (Callback<TableView<Condition>, Boolean>) param -> {
                    Comparator<Condition> comparator = (o1, o2) -> {
                        if (o1.getChronic() && o2.getChronic()) {
                            if (param.getComparator()
                                    == null) { // if no comparator is set then return 0 (nothing changes)
                                return 0;
                            } else { // otherwise sort the two conditions
                                return param.getComparator().compare(o1, o2);
                            }
                        } else if (o1
                                .getChronic()) { // o1 is chronic and o2 isn't so return -1 (o1 comes first)
                            return -1;
                        } else if (o2
                                .getChronic()) { // o2 is chronic and o1 isn't so return 1 (o2 comes first)
                            return 1;
                        } else if (param.getComparator()
                                == null) { // there is no comparator so return 0 (nothing changes)
                            return 0;
                        } else { // otherwise just compare them as usual
                            return param.getComparator().compare(o1, o2);
                        }
                    };
                    FXCollections.sort(curConditionsTable.getItems(), comparator);
                    return true;
                });
    }

    /**
     * sets the past date of diagnosis column to the new condition
     * @param cellFactoryDate cell factory of the date that the condition was added
     */
    private void initializePastDateOfDiagnosisColumn(
            Callback<TableColumn, TableCell> cellFactoryDate) {
        pastDateOfDiagnosisColumn.setCellValueFactory(new PropertyValueFactory("dateOfDiagnosis"));
        pastDateOfDiagnosisColumn.setCellFactory(cellFactoryDate);

        pastDateOfDiagnosisColumn.setOnEditCommit(
                (EventHandler<CellEditEvent<Condition, LocalDate>>) t -> {
                    if (!(t.getNewValue().isAfter(
                            t.getTableView().getItems().get(t.getTablePosition().getRow())
                                    .getDateCured())
                            || t.getNewValue().isAfter(LocalDate.now()))) {
                        controller.removeCondition(t.getTableView().getItems().get(
                                t.getTablePosition().getRow()));
                        (t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setDateOfDiagnosis(t.getNewValue());
                        controller.addCondition(t.getTableView().getItems().get(
                                t.getTablePosition().getRow()));
                    }
                    refreshConditionTable();
                });
    }

    /**
     * Sets the date that the condition was cured in the column
     * @param cellFactoryDate cell factory of the date the condition was cured
     */
    private void initializePastDateCuredColumn(Callback<TableColumn, TableCell> cellFactoryDate) {
        pastDateCuredColumn.setCellValueFactory(new PropertyValueFactory("dateCured"));
        pastDateCuredColumn.setCellFactory(cellFactoryDate);

        pastDateCuredColumn.setOnEditCommit(
                (EventHandler<CellEditEvent<Condition, LocalDate>>) t -> {
                    if (t.getNewValue().isBefore(
                            t.getTableView().getItems().get(t.getTablePosition().getRow())
                                    .getDateOfDiagnosis())
                            || t.getNewValue().isAfter(LocalDate.now())) {
                    } else {
                        controller.removeCondition(t.getTableView().getItems().get(
                                t.getTablePosition().getRow()));
                        (t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setDateCured(t.getNewValue());
                        controller.addCondition(t.getTableView().getItems().get(
                                t.getTablePosition().getRow()));
                    }
                    refreshConditionTable();
                });
    }

    /**
     * sets the past conditions column to the corresponding condition
     * @param cellFactory cell factory that contains column data.
     */
    private void initializePastConditionsColumn(Callback<TableColumn, TableCell> cellFactory) {
        pastDescriptionColumn.setCellValueFactory(new PropertyValueFactory("name"));
        pastDescriptionColumn.setCellFactory(cellFactory);
        pastDescriptionColumn.setOnEditCommit(
                (EventHandler<CellEditEvent<Condition, String>>) t -> (t.getTableView().getItems()
                        .get(
                                t.getTablePosition().getRow())).setName(t.getNewValue()));
    }

    /**
     * Sets the current date of diagnosis column
     * @param cellFactoryDate cell factory of the current date
     */
    private void initializeCurrentDateOfDiagnosisColumn(
            Callback<TableColumn, TableCell> cellFactoryDate) {
        curDateOfDiagnosisColumn.setCellValueFactory(new PropertyValueFactory("dateOfDiagnosis"));
        curDateOfDiagnosisColumn.setCellFactory(cellFactoryDate);

        curDateOfDiagnosisColumn.setOnEditCommit(
                (EventHandler<CellEditEvent<Condition, LocalDate>>) t -> {
                    if (!t.getNewValue().isAfter(LocalDate.now())) {
                        controller.removeCondition(t.getTableView().getItems().get(
                                t.getTablePosition().getRow()));
                        (t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setDateOfDiagnosis(t.getNewValue());
                        controller.addCondition(t.getTableView().getItems().get(
                                t.getTablePosition().getRow()));
                    }

                    refreshConditionTable();
                });
    }

    /**
     * sets the current chronic column
     */
    private void initializeCurrentChronicColumn() {
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
    }

    /**
     * sets the current conditions column
     * @param cellFactory
     */
    private void initializeCurrentConditionsColumn(Callback<TableColumn, TableCell> cellFactory) {
        curDescriptionColumn.setCellValueFactory(new PropertyValueFactory("name"));
        curDescriptionColumn.setCellFactory(cellFactory);
        curDescriptionColumn.setOnEditCommit(
                (EventHandler<CellEditEvent<Condition, String>>) t -> {
                    controller.removeCondition(t.getTableView().getItems().get(
                            t.getTablePosition().getRow()));
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setName(t.getNewValue());
                    controller.addCondition(t.getTableView().getItems().get(
                            t.getTablePosition().getRow()));
                });
    }

    /**
     * Sets the conditions lists and populates the observable lists.
     */
    private void initializeConditionLists() {
        if (curConditionsObservableList == null) {
            curConditionsObservableList = FXCollections.observableArrayList();
        }
        if (pastConditionsObservableList == null) {
            pastConditionsObservableList = FXCollections.observableArrayList();
        }

        if (controller.getCurrentConditions() != null) {
            curConditionsObservableList.addAll(controller.getCurrentConditions());
        }
        if (controller.getCuredConditions() != null) {
            pastConditionsObservableList.addAll(controller.getCuredConditions());
        }
    }

    /**
     * forces the sort order of the conditions tables to default to the diagnoses date in Descending
     * order
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
     * Button handler to add condition to the current conditions for the current profile.
     *
     * @param event clicking on the add button.
     */
    @FXML
    private void handleAddNewCondition(ActionEvent event) throws IOException {
        createPopup(event, "/view/AddCondition.fxml", "AddCondition");
        ProfileAddConditionView addConditionView = new ProfileAddConditionView();
        addConditionView.setup(this, currentProfile);
    }


    /**
     * Button handler to handle delete button clicked, only available to clinicians
     *
     * @param event clicking on the button.
     */
    @FXML
    private void handleDeleteCondition(ActionEvent event) throws IOException {
        controller.delete();
        refreshConditionTable();
    }

    /**
     * Button handler to handle toggle chronic button clicked, only available to clinicians
     *
     * @param event clicking on the button.
     */
    @FXML
    private void handleToggleChronicButtonClicked(ActionEvent event) {
        controller.toggleChronic();
        refreshConditionTable();
    }


    /**
     * Button handler to handle toggle cured button clicked, only available to clinicians
     *
     * @param event clicking on the button.
     */
    @FXML
    private void handleToggleCuredButtonClicked(ActionEvent event) {
        controller.toggleCured();
        refreshConditionTable();
    }

        public Profile getCurrentProfile() {
        return currentProfile;
        }

    /**
     * hides items that shouldn't be visible to either a donor or clinician
     */
    @FXML
    private void hideItems() {
        if (isOpenedByClinician) {
            //user is a clinician looking at donors profile, maximise functionality
            curConditionsTable.setEditable(true);
            pastConditionsTable.setEditable(true);
            toggleChronicButton.setDisable(false);
            toggleChronicButton.setVisible(true);
            toggleCuredButton.setDisable(false);
            toggleCuredButton.setVisible(true);
            addNewConditionButton.setVisible(true);
            deleteConditionButton.setVisible(true);
        } else {
            // user is a standard profile, limit functionality
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
     * Enables the relevant buttons on medications tab for how many drugs are selected
     */
    @FXML
    private void refreshPageElements() {
        ArrayList<Condition> allConditions = getSelectedConditions();
        hideItems();
        disableButtonsIfNoItems(allConditions);
    }

    public ArrayList<Condition> getSelectedConditions() {
        ArrayList<Condition> conditions = convertConditionObservableToArray(pastConditionsTable.getSelectionModel().getSelectedItems());
        conditions.addAll(convertConditionObservableToArray(curConditionsTable.getSelectionModel().getSelectedItems()));
        return conditions;
    }

    /**
     * Support function to disable medications buttons if no items present
     *
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

    public void initialize(Profile p, boolean isOpenedByClinician) {
        currentProfile = p;

         this.isOpenedByClinician = isOpenedByClinician;

        curConditionsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        pastConditionsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        refreshConditionTable();
        refreshPageElements();

        curChronicColumn.setSortable(false);

        disableTableHeaderReorder();

    }
}
