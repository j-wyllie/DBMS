package odms.controller;

import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.RedoController.redo;
import static odms.controller.UndoController.undo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import odms.History.History;
import odms.cli.CommandUtils;
import odms.data.ProfileDataIO;
import odms.enums.OrganEnum;
import odms.profile.Procedure;

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
    private TextField dateEntry;
    @FXML
    private Button saveButton;
    @FXML
    private TextField summaryEntry;
    @FXML
    private Button editButton;

    @FXML
    private ListView<OrganEnum> affectedOrgansListView;

    private Procedure currentProcedure;
    private ProfileDisplayController controller;

    @FXML
    public void initialize(Procedure selectedProcedure, ProfileDisplayController selectedController) {
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
        dateEntry.setDisable(true);
        dateEntry.setVisible(false);
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
        undo(GuiMain.getCurrentDatabase());
        procedureSummaryLabel.setText(currentProcedure.getSummary());
        procedureDateLabel.setText("Date " +" "+currentProcedure.getDate().toString());
        procedureDescriptionLabel.setText("Description: " +" "+currentProcedure.getLongDescription());
        procedureOrgansLabel.setText("Organs Affected: " +" "+currentProcedure.getOrgansAffected().toString());
        ProfileDataIO.saveData(getCurrentDatabase(), "example/example.json");
    }

    public void handleRedoButtonClicked(ActionEvent actionEvent) {
        redo(GuiMain.getCurrentDatabase());
        procedureSummaryLabel.setText(currentProcedure.getSummary());
        procedureDateLabel.setText("Date " +" "+currentProcedure.getDate().toString());
        procedureDescriptionLabel.setText("Description: " +" "+currentProcedure.getLongDescription());
        procedureOrgansLabel.setText("Donations Affected: " +" "+currentProcedure.getOrgansAffected().toString());
        ProfileDataIO.saveData(getCurrentDatabase(), "example/example.json");
    }

    public void handleEditButtonClicked(ActionEvent actionEvent) {
        affectedOrgansListView.setDisable(false);
        affectedOrgansListView.setVisible(true);
        descEntry.setDisable(false);
        descEntry.setVisible(true);
        dateEntry.setDisable(false);
        dateEntry.setVisible(true);
        saveButton.setDisable(false);
        saveButton.setVisible(true);
        summaryEntry.setDisable(false);
        summaryEntry.setVisible(true);
        descEntry.setText(currentProcedure.getLongDescription());
        dateEntry.setText(currentProcedure.getDate().toString());
        summaryEntry.setText(currentProcedure.getSummary());
        procedureSummaryLabel.setText("");
        procedureDateLabel.setText("Date:");
        procedureDescriptionLabel.setText("Description:");
        procedureOrgansLabel.setText("Donations Affected:");
    }

    public void handleSaveButtonClicked(ActionEvent actionEvent) {
        History action = new History ("Profile ",controller.getCurrentProfile().getId(),"EDITED","",controller.getCurrentProfile().getAllProcedures().indexOf(currentProcedure),LocalDateTime.now());
        String oldValues = " PREVIOUS("+currentProcedure.getSummary()+","+currentProcedure.getDate()+","+currentProcedure.getLongDescription()+")"+" OLDORGANS"+currentProcedure.getOrgansAffected();
        System.out.println(action);
        currentProcedure.setLongDescription(descEntry.getText());
        currentProcedure.setSummary(summaryEntry.getText());
        currentProcedure.setDate(LocalDate.parse(dateEntry.getText()));
        currentProcedure.setOrgansAffected(new ArrayList<>(affectedOrgansListView.getSelectionModel().getSelectedItems()));
        String newValues = " CURRENT("+currentProcedure.getSummary()+","+currentProcedure.getDate()+","+currentProcedure.getLongDescription()+")"+" NEWORGANS"+currentProcedure.getOrgansAffected();
        action.setHistoryData(oldValues+newValues);
        HistoryController.updateHistory(action);
        ProfileDataIO.saveData(getCurrentDatabase(), "example/example.json");
        affectedOrgansListView.setDisable(true);
        affectedOrgansListView.setVisible(false);
        descEntry.setDisable(true);
        descEntry.setVisible(false);
        dateEntry.setDisable(true);
        dateEntry.setVisible(false);
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
