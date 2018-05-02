package odms.controller;

import static odms.controller.AlertController.InvalidUsername;
import static odms.controller.LoginController.getCurrentProfile;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;
import static odms.data.MedicationDataIO.getActiveIngredients;
import static odms.data.MedicationDataIO.getSuggestionList;

import com.google.gson.Gson;
import javafx.beans.property.SimpleStringProperty;
import java.util.Timer;
import java.util.TimerTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import odms.cli.CommandUtils;
import odms.data.MedicationDataIO;
import odms.data.ProfileDataIO;
import odms.profile.Profile;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.medications.Drug;

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
    private Label chronicDiseasesLabel;

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
    private Button logoutButton;

    @FXML
    private Button buttonShowDrugInteractions;

    @FXML
    private Button buttonViewActiveIngredients;

    private Boolean isClinician = false;


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
    private void handleUndoButtonClicked(ActionEvent event)  {
        undo();
    }

    /**
     * Button handler to redo last undo action.
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event)  {
        redo();
    }

    /**
     * Button handler to make fields editable.
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {
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
        Profile currentDonor;
        if (searchedDonor != null) {
            currentDonor = searchedDonor;
        } else {
            currentDonor = getCurrentProfile();
        }

        String medicationName = textFieldMedicationSearch.getText();

        currentDonor.addDrug(new Drug(medicationName));

        refreshTable();
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
                    return;
                }
                drugs.add(toAdd);
            }
            else if (drugs.size() == 0) {
                drugs = convertObservableToArray(tableViewHistoricMedications.getSelectionModel().getSelectedItems());
            }

            if (drugs.size() != 2) {
                return; }
        }

        try {
            interactionsRaw = MedicationDataIO.getDrugInteractions(drugs.get(0).getDrugName(), drugs.get(1).getDrugName(), searchedDonor.getGender(), searchedDonor.getAge());

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
        Profile currentDonor;
        if (searchedDonor != null) {
            currentDonor = searchedDonor;
        } else {
            currentDonor = getCurrentProfile();
        }

        Drug drug = tableViewCurrentMedications.getSelectionModel().getSelectedItem();
        if (drug == null) { return; }
        currentDonor.moveDrugToHistory(drug);

        refreshTable();
    }

    /**
     * Button handler to remove medications from the historic list and add them back to the current list of drugs.
     * @param event clicking on the add button.
     */
    @FXML
    private void handleMoveMedicationToCurrent(ActionEvent event)   {
        Profile currentDonor;
        if (searchedDonor != null) {
            currentDonor = searchedDonor;
        } else {
            currentDonor = getCurrentProfile();
        }

        Drug drug = tableViewHistoricMedications.getSelectionModel().getSelectedItem();
        if (drug == null) { return; }
        currentDonor.moveDrugToCurrent(drug);

        refreshTable();
    }

    /**
     * Button handler to delete medications
     * @param event clicking on the delete button.
     */
    @FXML
    private void handleDelete(ActionEvent event)  {
        Profile currentDonor;
        if (searchedDonor != null) {
            currentDonor = searchedDonor;
        } else {
            currentDonor = getCurrentProfile();
        }

        Drug drug = tableViewHistoricMedications.getSelectionModel().getSelectedItem();
        if (drug == null) { drug = tableViewCurrentMedications.getSelectionModel().getSelectedItem(); }
        if (drug == null) { return; }

        currentDonor.deleteDrug(drug);

        refreshTable();
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
                Profile currentDonor;
                if (searchedDonor != null) {
                    currentDonor = searchedDonor;
                } else {
                    currentDonor = getCurrentProfile();
                }
                String medicationName = textFieldMedicationSearch.getText();
                if (!medicationName.equals("")) {
                    currentDonor.addDrug(new Drug(medicationName));
                }
                textFieldMedicationSearch.clear();
            }
            refreshTable();
        });
    }

    /**
     * Refresh the current and historic medication tables with the most up to date data
     */
    @FXML
    private void refreshTable() {
        Profile currentDonor;
        if (searchedDonor != null) {
            currentDonor = searchedDonor;
        } else {
            currentDonor = getCurrentProfile();
        }


        tableViewCurrentMedications.getItems().clear();
        if (currentDonor.getCurrentMedications() != null) {currentMedication.addAll(currentDonor.getCurrentMedications());}
        tableViewHistoricMedications.getItems().clear();
        if (currentDonor.getHistoryOfMedication() != null) {historicMedication.addAll(currentDonor.getHistoryOfMedication());}

        tableViewCurrentMedications.setItems(currentMedication);
        tableColumnMedicationNameCurrent.setCellValueFactory(new PropertyValueFactory("drugName"));
        tableViewCurrentMedications.getColumns().setAll(tableColumnMedicationNameCurrent);

        tableViewHistoricMedications.setItems(historicMedication);
        tableColumnMedicationNameHistoric.setCellValueFactory(new PropertyValueFactory("drugName"));
        tableViewHistoricMedications.getColumns().setAll(tableColumnMedicationNameHistoric);

    }

    /**
     * sets all of the items in the fxml to their respective values
     */
    @FXML
    private void setPage(Profile currentDonor){

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

            refreshTable();
            setMedicationSearchFieldListener();

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
            buttonAddMedication.setVisible(true);
            buttonDeleteMedication.setVisible(true);
            buttonMedicationCurrentToHistoric.setVisible(true);
            buttonMedicationHistoricToCurrent.setVisible(true);
            textFieldMedicationSearch.setVisible(true);

            buttonShowDrugInteractions.setVisible(true);
            buttonViewActiveIngredients.setVisible(true);

            tableViewActiveIngredients.setVisible(true);
            tableViewDrugInteractionsNames.setVisible(true);
            tableViewDrugInteractions.setVisible(true);

            logoutButton.setVisible(false);
        } else {
            buttonAddMedication.setVisible(false);
            buttonDeleteMedication.setVisible(false);
            buttonMedicationCurrentToHistoric.setVisible(false);
            buttonMedicationHistoricToCurrent.setVisible(false);
            textFieldMedicationSearch.setVisible(false);

            buttonShowDrugInteractions.setVisible(false);
            buttonViewActiveIngredients.setVisible(false);

            tableViewActiveIngredients.setVisible(false);
            tableViewDrugInteractionsNames.setVisible(false);
            tableViewDrugInteractions.setVisible(false);

            logoutButton.setVisible(true);
        }
    }

    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize() {
        tableViewCurrentMedications.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
        tableViewHistoricMedications.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        if(searchedDonor != null) {
            setPage(searchedDonor);

            if (!isClinician) {
                hideItems();
            }
            //Profile currentDonor = getCurrentProfile();
        }
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
