package odms.controller;

import static odms.controller.AlertController.InvalidUsername;
import static odms.controller.LoginController.getCurrentDonor;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commandlineview.CommandUtils;
import odms.data.DonorDataIO;
import odms.donor.Condition;
import odms.donor.Donor;
import java.io.Console;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DonorProfileController {

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
    private TableView curDiseasesTable;

    @FXML
    private TableColumn curDescriptionColumn;

    @FXML
    private TableColumn curChronicColumn;

    @FXML
    private TableColumn curDateOfDiagnosisColumn;

    @FXML
    private TableView pastDiseasesTable;

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

    private ObservableList<Condition> curConditionsObservableList;
    private ObservableList<Condition> pastConditionsObservableList;





    /**
     * initializes and refreshes the current and past conditions tables
     */
    @FXML
    private void makeTable(ArrayList<Condition> curConditions, ArrayList<Condition> pastConditions){                      //TODO need a function to get all current conditions, rather than just all

        if (curConditions != null) {curConditionsObservableList = FXCollections.observableArrayList(curConditions);}
        else {curConditionsObservableList = FXCollections.observableArrayList(); }
        if (pastConditions != null) {pastConditionsObservableList = FXCollections.observableArrayList(pastConditions);}
        else {pastConditionsObservableList = FXCollections.observableArrayList(); }

        Condition placeholdCondition = new Condition(false, true, "Space aids", LocalDate.of(2005, 12, 5), null);
        Condition placeholdCondition2 = new Condition(true, false, "Shortness", LocalDate.of(2005, 12, 7), LocalDate.of(2012, 3, 10));
        Condition placeholdCondition3 = new Condition(false, false, "Ginger", LocalDate.of(2005, 12, 10), null);


        curConditionsObservableList.add(placeholdCondition);
        curConditionsObservableList.add(placeholdCondition3);
        pastConditionsObservableList.add(placeholdCondition2);

        curDiseasesTable.setItems(curConditionsObservableList);
        pastDiseasesTable.setItems(pastConditionsObservableList);

        curDescriptionColumn.setCellValueFactory(new PropertyValueFactory("condition"));
        curChronicColumn.setCellValueFactory(new PropertyValueFactory("isChronicString"));
        curDateOfDiagnosisColumn.setCellValueFactory(new PropertyValueFactory("dateOfDiagnosis"));

        pastDescriptionColumn.setCellValueFactory(new PropertyValueFactory("condition"));
        pastDateOfDiagnosisColumn.setCellValueFactory(new PropertyValueFactory("dateOfDiagnosis"));
        pastDateCuredColumn.setCellValueFactory(new PropertyValueFactory("dateCured"));

        curDiseasesTable.getColumns().setAll(curDescriptionColumn, curChronicColumn, curDateOfDiagnosisColumn);
        pastDiseasesTable.getColumns().setAll(pastDescriptionColumn, pastDateOfDiagnosisColumn, pastDateCuredColumn);


    }



    /**
     * Button handler to handle toggle chronic button clcked, only available to clinicians
     * @param event clicking on the button.
     */
    @FXML
    private void handleToggleChronicButtonClicked(ActionEvent event) {
    }


    /**
     * Button handler to handle toggle cured button clcked, only available to clinicians
     * @param event clicking on the button.
     */
    @FXML
    private void handleToggleCuredButtonClicked(ActionEvent event) {
    }


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
        Parent parent = FXMLLoader.load(getClass().getResource("/view/EditDonorProfile.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize() {
        Donor currentDonor = getCurrentDonor();

        System.out.println(currentDonor.getAllConditions());
        makeTable(currentDonor.getAllConditions(), currentDonor.getCuredConditions());


        try {
            donorFullNameLabel
                    .setText(currentDonor.getGivenNames() + " " + currentDonor.getLastNames());
            donorStatusLabel.setText(donorStatusLabel.getText() + "Unregistered");

            if (currentDonor.getRegistered() != null && currentDonor.getRegistered() == true) {
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
            if(currentDonor.getHeight() != null && currentDonor.getWeight() != null){
                bmiLabel.setText(bmiLabel.getText() + Math.round(currentDonor.calculateBMI() * 100.00) / 100.00);
            }
            if(currentDonor.getDateOfBirth() != null){
                ageLabel.setText(ageLabel.getText() + Integer.toString(currentDonor.calculateAge()));
            }
            if(currentDonor.getId() != null){
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
            String history = DonorDataIO.getHistory();
            Gson gson = new Gson();

            if(history.equals("")) {
                history = gson.toJson(CommandUtils.getHistory());
            } else {
                history = history.substring(0, history.length()-1);
                history = history+","+gson.toJson(CommandUtils.getHistory()).substring(1);
            }
            history = history.substring(1, history.length()-1);
            String[] actionHistory = history.split(",");

            ArrayList<String> userHistory = new ArrayList<>();

            for(String str : actionHistory){
                if(str.contains("Donor " + getCurrentDonor().getId())){
                    userHistory.add(str);
                }
            }
            historyView.setText(userHistory.toString());
        } catch (Exception e) {
            InvalidUsername();
        }
    }
}
