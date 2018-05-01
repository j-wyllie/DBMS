package odms.controller;

import static odms.controller.AlertController.InvalidUsername;
import static odms.controller.LoginController.getCurrentProfile;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.cli.CommandUtils;
import odms.data.ProfileDataIO;
import odms.profile.Organ;
import odms.profile.Procedure;
import odms.profile.Profile;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
    private Button logoutButton;

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

    Boolean isClinician = false;

    private ObservableList<Procedure> previousProceduresObservableList;
    private ObservableList<Procedure> pendingProceduresObservableList;



    /**
     * Scene change to log in view.
     * @param event clicking on the logout button.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/EditDonorProfile.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        EditDonorProfileController controller = fxmlLoader.<EditDonorProfileController>getController();
        controller.setDonor(searchedDonor);
        controller.initialize();

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setScene(scene);
        appStage.show();
    }

    @FXML
    public void handleAddProcedureButtonClicked(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/AddProcedure.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            AddProcedureController controller = fxmlLoader.<AddProcedureController>getController();
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
     * sets all of the items in the fxml to their respective values
     * @param currentDonor donors profile
     */
    @FXML
    private void setPage(Profile currentDonor){

        makeProcedureTable(currentDonor.getPreviousProcedures(), currentDonor.getPendingProcedures());

        // Test data; remove later
        currentDonor.addProcedure(new Procedure("facial reconstruction", "20-2-2019", "dgfdsgfdsgdfsgfdgdsfg"));
        currentDonor.addDonationFromString("heart");
        currentDonor.getAllProcedures().get(0).addAffectedOrgan(currentDonor, Organ.HEART);
        currentDonor.addProcedure(new Procedure("tumor removal", "18-4-2013", "dgfdsgfdsgdfsgfdgdsfg"));
        currentDonor.addProcedure(new Procedure("circumcision", "20-8-2012", "dgfdsgfdsgdfsgfdgdsfg"));
        currentDonor.addProcedure(new Procedure("euthanization", "2-7-2018", "dgfdsgfdsgdfsgfdgdsfg"));

        refreshProcedureTable();

        try {
            donorFullNameLabel
                    .setText(currentDonor.getFullName());
            donorStatusLabel.setText(donorStatusLabel.getText() + "Unregistered");

            if (currentDonor.getRegistered() != null && currentDonor.getRegistered() == true) {
                donorStatusLabel.setText("Donor Status: Registered");
            }
            if (currentDonor.getGivenNames() != null) {
                System.out.println(givenNamesLabel.getText() + currentDonor.getGivenNames());
                givenNamesLabel.setText(givenNamesLabel.getText() + currentDonor.getGivenNames());

            }
            if (currentDonor.getLastNames() != null) {
                lastNamesLabel.setText(lastNamesLabel.getText() + currentDonor.getLastNames());
            }
            if (currentDonor.getIrdNumber() != null) {
                irdLabel.setText(irdLabel.getText() + currentDonor.getIrdNumber());
            }
            if (currentDonor.getDateOfBirth() != null) {
                dobLabel.setText(dobLabel.getText() + currentDonor.getDateOfBirth());
            }
            if (currentDonor.getDateOfDeath() != null) {
                dodLabel.setText(dodLabel.getText() + currentDonor.getDateOfDeath());
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
        } catch (Exception e) {
            e.printStackTrace();
            InvalidUsername();
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
        if (currentDonor.getPendingProcedures() != null) {
            pendingProceduresObservableList.addAll(currentDonor.getPendingProcedures());
        } else {
            return;
        }

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


    /**
     * Hides items that shouldn't be visible to either a donor or clinician
     */
    @FXML
    private void hideItems() {
        if(isClinician){
            logoutButton.setVisible(false);

            addNewProcedureButton.setDisable(false);
            addNewProcedureButton.setVisible(true);
            deleteProcedureButton.setDisable(false);
            deleteProcedureButton.setVisible(true);
        } else {
            addNewProcedureButton.setDisable(true);
            addNewProcedureButton.setVisible(false);
            deleteProcedureButton.setDisable(true);
            deleteProcedureButton.setVisible(false);
        }
    }

    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize() {
        if(getCurrentProfile() != null) {
            Profile currentDonor = getCurrentProfile();
            hideItems();
            setPage(currentDonor);
            makeProcedureTable(currentDonor.getPreviousProcedures(), currentDonor.getPendingProcedures());
        }
    }

    /**
     * sets the donor if it is being opened by a clinician
     * @param donor
     */
    public void setDonor(Profile donor) {
        isClinician = true;
        searchedDonor = donor;
        hideItems();
        setPage(searchedDonor);
    }

    /**
     * Creates a new edit procedure window
     */
    @FXML
    public void createNewProcedureWindow(Procedure selectedProcedure) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/EditProcedure.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            EditProcedureController controller = fxmlLoader.<EditProcedureController>getController();
            controller.setProcedure(selectedProcedure);
            controller.initialize();

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }




    public Profile getSearchedDonor() { return searchedDonor; }

}
