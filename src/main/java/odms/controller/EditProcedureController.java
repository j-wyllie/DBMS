package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import odms.cli.CommandUtils;
import odms.data.ProfileDataIO;
import odms.profile.Organ;
import odms.profile.Procedure;
import odms.profile.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.LoginController.getCurrentProfile;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

public class EditProcedureController {
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
    private ListView<Organ> affectedOrgansListView;

    private ObservableList<Organ> donatedOrgans;

    private Procedure currentProcedure;
    private DonorProfileController controller;

    @FXML
    public void initialize(Procedure selectedProcedure, DonorProfileController selectedController) {
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
            donatedOrgans =  FXCollections.observableArrayList(controller.getSearchedDonor().getDonatedOrgans());
            affectedOrgansListView.setItems(donatedOrgans);
            editButton.setVisible(true);
        } catch (NullPointerException e){
            System.out.println("Not clinician");
            editButton.setVisible(false);
        }

    }

    public void handleUndoButtonClicked(ActionEvent actionEvent) {
        undo();
        procedureSummaryLabel.setText(currentProcedure.getSummary());
        procedureDateLabel.setText("Date " +" "+currentProcedure.getDate().toString());
        procedureDescriptionLabel.setText("Description: " +" "+currentProcedure.getLongDescription());
        procedureOrgansLabel.setText("Organs Affected: " +" "+currentProcedure.getOrgansAffected().toString());
        ProfileDataIO.saveData(getCurrentDatabase(), "example/example.json");
    }

    public void handleRedoButtonClicked(ActionEvent actionEvent) {
        redo();
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
        String action = "Donor "+controller.getSearchedDonor().getId()+" PROCEDURE "+controller.getSearchedDonor().getAllProcedures().indexOf(currentProcedure)+" EDITED";
        String oldValues = " PREVIOUS("+currentProcedure.getSummary()+","+currentProcedure.getDate()+","+currentProcedure.getLongDescription()+")"+" OLDORGANS"+currentProcedure.getOrgansAffected();
        System.out.println(action);
        currentProcedure.setLongDescription(descEntry.getText());
        currentProcedure.setSummary(summaryEntry.getText());
        currentProcedure.setDate(LocalDate.parse(dateEntry.getText()));
        currentProcedure.setOrgansAffected(new ArrayList<>(affectedOrgansListView.getSelectionModel().getSelectedItems()));
        String newValues = " CURRENT("+currentProcedure.getSummary()+","+currentProcedure.getDate()+","+currentProcedure.getLongDescription()+")"+" NEWORGANS"+currentProcedure.getOrgansAffected();
        action += oldValues+newValues;
        action = action +"END at " + LocalDateTime.now();
        System.out.println(action);
        if (CommandUtils.getHistory().size() != 0) {
            if (CommandUtils.getPosition() != CommandUtils.getHistory().size() - 1) {
                CommandUtils.currentSessionHistory.subList(CommandUtils.getPosition(),
                        CommandUtils.getHistory().size() - 1).clear();
            }
        }
        CommandUtils.currentSessionHistory.add(action);
        CommandUtils.historyPosition = CommandUtils.currentSessionHistory.size() - 1;
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
