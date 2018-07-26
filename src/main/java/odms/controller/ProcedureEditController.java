package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import odms.cli.CommandUtils;
import javafx.scene.control.*;
import odms.data.ProfileDataIO;
import odms.enums.OrganEnum;
import odms.history.History;
import odms.profile.Procedure;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static odms.App.getProfileDb;

public class ProcedureEditController {
    @FXML
    private Label procedureSummaryLabel;
    @FXML
    private Label procedureDateLabel;
    @FXML
    private Label procedureDescriptionLabel;
    @FXML
    private Label procedureOrgansLabel;
    @FXML
    private TextArea descEntry;
    @FXML
    private DatePicker dateOfProcedureDatePicker;
    @FXML
    private Button saveButton;
    @FXML
    private TextField summaryEntry;
    @FXML
    private Button editButton;
    @FXML
    private Label warningLabel;

    @FXML
    private ListView<OrganEnum> affectedOrgansListView;

    private Procedure currentProcedure;
    private ProfileDisplayController controller;
    private RedoController redoController= new RedoController();
    private UndoController undoController= new UndoController();

    @FXML
    public void initialize(Procedure selectedProcedure, ProfileDisplayController selectedController) {
        warningLabel.setVisible(false);
        controller = selectedController;
        currentProcedure = selectedProcedure;
        procedureSummaryLabel.setText(currentProcedure.getSummary());
        procedureDateLabel.setText("Date " +" "+currentProcedure.getDate().toString());
        procedureDescriptionLabel.setText("Description: " +" "+currentProcedure.getLongDescription());
        procedureOrgansLabel.setText("Donations Affected: " +" "+currentProcedure.getOrgansAffected().toString());
        affectedOrgansListView.setDisable(true);
        affectedOrgansListView.setVisible(false);
        descEntry.setDisable(true);
        descEntry.setVisible(false);
        dateOfProcedureDatePicker.setDisable(true);
        dateOfProcedureDatePicker.setVisible(false);
        summaryEntry.setDisable(true);
        summaryEntry.setVisible(false);
        saveButton.setDisable(true);
        saveButton.setVisible(false);
        try{
            affectedOrgansListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            ObservableList<OrganEnum> organsDonated = FXCollections
                    .observableArrayList(controller.getCurrentProfile().getOrgansDonated());
            affectedOrgansListView.setItems(organsDonated);
            editButton.setVisible(true);
        } catch (NullPointerException e){
            System.out.println("Not clinician");
            editButton.setVisible(false);
        }

    }

    public void handleUndoButtonClicked(ActionEvent actionEvent) {
        undoController.undo(getProfileDb());
        procedureSummaryLabel.setText(currentProcedure.getSummary());
        procedureDateLabel.setText("Date " +" "+currentProcedure.getDate().toString());
        procedureDescriptionLabel.setText("Description: " +" "+currentProcedure.getLongDescription());
        procedureOrgansLabel.setText("Organs Affected: " +" "+currentProcedure.getOrgansAffected().toString());
        ProfileDataIO.saveData(getProfileDb(), "example/example.json");
    }

    public void handleRedoButtonClicked(ActionEvent actionEvent) {
        redoController.redo(getProfileDb());
        procedureSummaryLabel.setText(currentProcedure.getSummary());
        procedureDateLabel.setText("Date " +" "+currentProcedure.getDate().toString());
        procedureDescriptionLabel.setText("Description: " +" "+currentProcedure.getLongDescription());
        procedureOrgansLabel.setText("Donations Affected: " +" "+currentProcedure.getOrgansAffected().toString());
        ProfileDataIO.saveData(getProfileDb(), "example/example.json");
    }

    public void handleEditButtonClicked(ActionEvent actionEvent) {
        warningLabel.setVisible(false);
        affectedOrgansListView.setDisable(false);
        affectedOrgansListView.setVisible(true);
        descEntry.setDisable(false);
        descEntry.setVisible(true);
        dateOfProcedureDatePicker.setDisable(false);
        dateOfProcedureDatePicker.setVisible(true);
        saveButton.setDisable(false);
        saveButton.setVisible(true);
        summaryEntry.setDisable(false);
        summaryEntry.setVisible(true);
        descEntry.setText(currentProcedure.getLongDescription());
        dateOfProcedureDatePicker.setValue(currentProcedure.getDate());
        summaryEntry.setText(currentProcedure.getSummary());
        procedureSummaryLabel.setText("");
        procedureDateLabel.setText("Date:");
        procedureDescriptionLabel.setText("Description:");
        procedureOrgansLabel.setText("Donations Affected:");
    }

    public void handleSaveButtonClicked(ActionEvent actionEvent) {
        History action = new History ("Profile ",controller.getCurrentProfile().getId(),"EDITED","",
                controller.getCurrentProfile().getAllProcedures().indexOf(currentProcedure),LocalDateTime.now());
        String oldValues = " PREVIOUS("+currentProcedure.getSummary()+","+currentProcedure.getDate()+","+
                currentProcedure.getLongDescription()+")"+" OLDORGANS"+currentProcedure.getOrgansAffected();
        currentProcedure.setLongDescription(descEntry.getText());
        currentProcedure.setSummary(summaryEntry.getText());

        // date validation
        LocalDate dateOfProcedure = dateOfProcedureDatePicker.getValue();
        LocalDate dob = controller.getCurrentProfile().getDateOfBirth();
        if (dob.isAfter(dateOfProcedure)){
            warningLabel.setVisible(true);
            return;
        } else {
            currentProcedure.setDate(dateOfProcedure);
            warningLabel.setVisible(false);
        }

        currentProcedure.setOrgansAffected(new ArrayList<>(affectedOrgansListView.getSelectionModel().getSelectedItems()));
        String newValues = " CURRENT("+currentProcedure.getSummary()+","+currentProcedure.getDate()+","+
                currentProcedure.getLongDescription()+")"+" NEWORGANS"+currentProcedure.getOrgansAffected();
        action.setHistoryData(oldValues+newValues);
        HistoryController.updateHistory(action);
        ProfileDataIO.saveData(getProfileDb(), "example/example.json");
        affectedOrgansListView.setDisable(true);
        affectedOrgansListView.setVisible(false);
        descEntry.setDisable(true);
        descEntry.setVisible(false);
        dateOfProcedureDatePicker.setDisable(true);
        dateOfProcedureDatePicker.setVisible(false);
        saveButton.setDisable(true);
        saveButton.setVisible(false);
        summaryEntry.setDisable(true);
        summaryEntry.setVisible(false);
        procedureSummaryLabel.setText(procedureSummaryLabel.getText() +" "+currentProcedure.getSummary());
        procedureDateLabel.setText(procedureDateLabel.getText() +" "+currentProcedure.getDate().toString());
        procedureDescriptionLabel.setText(procedureDescriptionLabel.getText() +" "+currentProcedure.getLongDescription());
        procedureOrgansLabel.setText(procedureOrgansLabel.getText() +" "+currentProcedure.getOrgansAffected().toString());
        controller.refreshProcedureTable();
    }
}
