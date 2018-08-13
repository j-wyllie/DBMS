package odms.view.profile;

import static odms.controller.AlertController.saveChanges;
import static odms.controller.data.MedicationDataIO.getSuggestionList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import odms.commons.model.medications.Drug;
import odms.commons.model.profile.Profile;
import odms.controller.profile.Medications;
import odms.view.CommonView;

/**
 * View that contains FXML elements and input handlers for the Medications view.
 */
public class MedicationsGeneral extends CommonView {

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
    private Button buttonClearCache;

    // init controller corresponding to this view
    private Medications controller = new Medications(this);
    private Profile currentProfile;
    private Boolean isOpenedByClinician;

    private ObservableList<Drug> currentMedication = FXCollections.observableArrayList();
    private ObservableList<Drug> historicMedication = FXCollections.observableArrayList();
    private ContextMenu suggestionMenu = new ContextMenu();

    /**
     * Refresh the current and historic medication tables with the most up to date data.
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
     * Button handler to save changes made on medications tab. If a save is made else where in the
     * program changes to the medications will also be saved.
     *
     * @param event clicking on the save button
     * @throws IOException Notification could cause an exception
     */
    @FXML
    private void handleSaveMedications(ActionEvent event) throws IOException {
        if (saveChanges()) {
            //ProfileDataIO.saveData(getCurrentDatabase());
            controller.saveDrugs();
            //todo sort out show notification
            showNotification("Medications Tab", event);
        }
    }

    /**
     * Button handler to view a drugs active ingredients
     *
     * @param event clicking on the active ingredients button
     */
    @FXML
    private void handleViewActiveIngredients(ActionEvent event) {
        ArrayList<String> activeIngredients = null;
        try {
            activeIngredients = controller.viewActiveIngredients();
        } catch (IOException e) {
            e.printStackTrace();
            tableViewActiveIngredients
                    .setPlaceholder(new Label("There was an error getting active ingredient data"));
        }
        tableViewActiveIngredients.getItems().clear();
        if (activeIngredients == null || activeIngredients.isEmpty()) {
            tableViewActiveIngredients
                    .setPlaceholder(new Label("There is no active ingredient for this drug"));
        } else {
            ObservableList<String> activeIngredientsList = FXCollections.observableArrayList();
            activeIngredientsList.add("Active ingredients for " + controller.getSelectedDrug().getDrugName() + ":");
            activeIngredientsList.addAll(activeIngredients);
            tableViewActiveIngredients.setItems(activeIngredientsList);
            tableColumnActiveIngredients
                    .setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
            tableViewActiveIngredients.getColumns().setAll(tableColumnActiveIngredients);
        }

    }

    /**
     * Button handler to add medications to the current medications for the current profile.
     *
     * @param event clicking on the add button.
     */
    @FXML
    private void handleAddNewMedications(ActionEvent event) {
        if (!textFieldMedicationSearch.getText().isEmpty()) {
            String medicationName = textFieldMedicationSearch.getText();
            controller.addDrug(new Drug(medicationName), currentProfile);
            refreshMedicationsTable();
        }
    }

    /**
     * Button handler to get and display drug interactions on TableView
     * tableViewDrugInteractionsName and tableViewDrugInteractions.
     *
     * @param event clicking on the show interactions button.
     */
    @FXML
    private void handleShowInteractions(ActionEvent event) {
        try {
            tableViewDrugInteractions.getItems().clear();
            Map<String, String> interactionsRaw = controller.getRawInteractions(currentProfile);
            ObservableList<String> drugsList = controller.getObservableDrugsList();
            tableViewDrugInteractionsNames.setItems(drugsList);
            tableColumnDrugInteractions
                    .setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
            tableViewDrugInteractionsNames.getColumns().setAll(tableColumnDrugInteractions);

            if (interactionsRaw.isEmpty()) {
                tableViewDrugInteractions
                        .setPlaceholder(new Label("There are no interactions for these drugs."));
            } else if (interactionsRaw.containsKey("error")) {
                tableViewDrugInteractions
                        .setPlaceholder(new Label("There was an error getting interaction data."));
            } else {
                ObservableList<Map.Entry<String, String>> interactions = FXCollections
                        .observableArrayList(interactionsRaw.entrySet());
                tableViewDrugInteractions.setItems(interactions);
                tableColumnSymptoms.setCellValueFactory((TableColumn.CellDataFeatures
                        <Map.Entry<String, String>, String> param) ->
                        new SimpleStringProperty(param.getValue().getKey()));
                tableColumnDuration.setCellValueFactory((TableColumn.CellDataFeatures
                        <Map.Entry<String, String>, String> param) ->
                        new SimpleStringProperty(param.getValue().getValue()));
                tableViewDrugInteractions.getColumns()
                        .setAll(tableColumnSymptoms, tableColumnDuration);
            }
        } catch (IOException e) {
            tableViewDrugInteractions
                    .setPlaceholder(new Label("There was an error getting interaction data."));
        }
    }


    /**
     * Clears the cache and handles the messages.
     * @param event clicking on the clear cache button.
     */
    @FXML
    private void handleClearCache(ActionEvent event) {
        controller.clearCache();
    }


    /**
     * Button handler to remove medications from the current medications and move them to historic.
     *
     * @param event clicking on the add button.
     */
    @FXML
    private void handleMoveMedicationToHistoric(ActionEvent event) {
        controller.moveToHistory(currentProfile);
        refreshMedicationsTable();
    }

    /**
     * Button handler to remove medications from the historic list and add them back to the current
     * list of drugs.
     *
     * @param event clicking on the add button.
     */
    @FXML
    private void handleMoveMedicationToCurrent(ActionEvent event) {
        controller.moveToCurrent(currentProfile);
        refreshMedicationsTable();
    }

    /**
     * Button handler to delete medications
     *
     * @param event clicking on the delete button.
     */
    @FXML
    private void handleDeleteMedication(ActionEvent event) {
        controller.deleteDrug(currentProfile, controller.getSelectedDrug());
        refreshMedicationsTable();
    }

    /**
     * Button handler to open medicationHistory scene
     *
     * @param event clicking on delete button.
     * @throws IOException If MedicationHistoryTODO fxml is not found.
     */
    @FXML
    private void handleViewMedicationHistory(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ProfileMedicationsHistory.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        MedicationHistory view = fxmlLoader.getController();
        view.setProfile(currentProfile);
        view.initialize();
        Stage stage = new Stage();
        stage.setTitle("medication history");
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
                    controller.addDrug(new Drug(medicationName),currentProfile);
                }
                textFieldMedicationSearch.clear();
                refreshMedicationsTable();
            }
        });
    }

    /**
     * init the medications view.
     * @param p current profile object
     * @param b boolean that will be true if scene was opened by a clinician/admin user
     */
    public void initialize(Profile p, Boolean b) {
        isOpenedByClinician = b;
        currentProfile = p;
        hideItems();
        tableViewCurrentMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewHistoricMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setMedicationSearchFieldListener();
        refreshMedicationsTable();
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public Drug getSelectedCurrentDrug() {
        return tableViewCurrentMedications.getSelectionModel().getSelectedItem();
    }

    public ObservableList<Drug> getSelectedCurrentDrugs() {
        return tableViewCurrentMedications.getSelectionModel().getSelectedItems();
    }

    public ObservableList<Drug> getSelectedHistoricDrugs() {
        return tableViewHistoricMedications.getSelectionModel().getSelectedItems();
    }

    public Drug getSelectedHistoricDrug() {
        return tableViewHistoricMedications.getSelectionModel().getSelectedItem();
    }

    /**
     * Refreshes button elements, from disabled to not disabled, depending on how many drugs are
     * selected.
     */
    public void refreshPageElements() {
        ArrayList<Drug> drugs = controller.convertObservableToArray(
                tableViewCurrentMedications.getSelectionModel().getSelectedItems()
        );

        if (drugs.size() != 2) {
            if (drugs.size() == 1) {
                Drug toAdd = tableViewHistoricMedications.getSelectionModel().getSelectedItem();
                if (toAdd != null) {
                    drugs.add(toAdd);
                }
            } else if (drugs.isEmpty()) {
                drugs = controller.convertObservableToArray(
                        tableViewHistoricMedications.getSelectionModel().getSelectedItems());
            }

            if (drugs.isEmpty()) {
                buttonMedicationHistoricToCurrent.setDisable(true);
                buttonMedicationCurrentToHistoric.setDisable(true);
                buttonDeleteMedication.setDisable(true);
                buttonShowDrugInteractions.setDisable(true);
                buttonViewActiveIngredients.setDisable(true);
            } else if (drugs.size() == 1) {
                buttonMedicationHistoricToCurrent.setDisable(false);
                buttonMedicationCurrentToHistoric.setDisable(false);
                buttonDeleteMedication.setDisable(false);
                buttonShowDrugInteractions.setDisable(true);
                buttonViewActiveIngredients.setDisable(false);
            }
        }
        if (drugs.size() == 2) {
            buttonMedicationHistoricToCurrent.setDisable(false);
            buttonMedicationCurrentToHistoric.setDisable(false);
            buttonDeleteMedication.setDisable(false);
            buttonShowDrugInteractions.setDisable(false);
            buttonViewActiveIngredients.setDisable(false);
        }

    }

    /**
     * hides items that shouldn't be visible to either a donor or clinician.
     */
    @FXML
    private void hideItems() {
        if (isOpenedByClinician) {
            //user is a clinician looking at donors profile, maximise functionality
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
            buttonClearCache.setVisible(true);
            buttonMedicationHistoricToCurrent.setDisable(true);
            buttonMedicationCurrentToHistoric.setDisable(true);
            buttonDeleteMedication.setDisable(true);
            buttonShowDrugInteractions.setDisable(true);
            buttonViewActiveIngredients.setDisable(true);
        } else {
            // user is a standard profile, limit functionality
            buttonSaveMedications.setVisible(false);
            buttonDeleteMedication.setVisible(false);
            buttonShowDrugInteractions.setVisible(false);
            buttonViewActiveIngredients.setVisible(false);
            buttonAddMedication.setVisible(false);
            buttonMedicationCurrentToHistoric.setVisible(false);
            buttonMedicationHistoricToCurrent.setVisible(false);
            textFieldMedicationSearch.setVisible(false);
            tableViewActiveIngredients.setVisible(false);
            tableViewDrugInteractionsNames.setVisible(false);
            tableViewDrugInteractions.setVisible(false);
            buttonClearCache.setVisible(false);
        }
    }



}
