package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import odms.data.ProfileDataIO;
import odms.profile.Organ;
import odms.profile.Procedure;
import odms.profile.Profile;

import java.time.LocalDate;
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
    private ListView<Organ> affectedOrgansListView;

    private ObservableList<Organ> donatedOrgans;

    protected Procedure currentProcedure;
    protected DonorProfileController controller;

    @FXML
    public void initialize(Procedure selectedProcedure, DonorProfileController selectedController) {
        controller = selectedController;
        currentProcedure = selectedProcedure;
        procedureSummaryLabel.setText(procedureSummaryLabel.getText() +" "+currentProcedure.getSummary());
        procedureDateLabel.setText(procedureDateLabel.getText() +" "+currentProcedure.getDate().toString());
        procedureDescriptionLabel.setText(procedureDescriptionLabel.getText() +" "+currentProcedure.getLongDescription());
        procedureOrgansLabel.setText(procedureOrgansLabel.getText() +" "+currentProcedure.getOrgansAffected().toString());
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
        } catch (NullPointerException e){
            System.out.println("Not clinician");
        }

    }

    public void handleUndoButtonClicked(ActionEvent actionEvent) {
        undo();
    }

    public void handleRedoButtonClicked(ActionEvent actionEvent) {
        redo();
    }

    public void handleEditButtonClicked(ActionEvent actionEvent) {
        try {
            if(!controller.getSearchedDonor().equals(null)) {
                affectedOrgansListView.setDisable(false);
                affectedOrgansListView.setVisible(true);
            }
        } catch (NullPointerException e) {
            System.out.println("Not clinician");
        }
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
        procedureSummaryLabel.setText("Procedure:");
        procedureDateLabel.setText("Date:");
        procedureDescriptionLabel.setText("Description:");
        procedureOrgansLabel.setText("Organs Affected:");
    }

    public void handleSaveButtonClicked(ActionEvent actionEvent) {
        currentProcedure.setLongDescription(descEntry.getText());
        currentProcedure.setSummary(summaryEntry.getText());
        currentProcedure.setDate(LocalDate.parse(dateEntry.getText()));
        try {
            if(!controller.getSearchedDonor().equals(null)) {
                currentProcedure.setOrgansAffected(new ArrayList<>(affectedOrgansListView.getSelectionModel().getSelectedItems()));
            }
        } catch (NullPointerException e) {
            System.out.println("Not clinician");
        }
        ProfileDataIO.saveData(getCurrentDatabase(), "example/example.json");
        try {
            if(!controller.getSearchedDonor().equals(null)) {
                affectedOrgansListView.setDisable(true);
                affectedOrgansListView.setVisible(false);
            }
        } catch (NullPointerException e) {
            System.out.println("Not clinician");
        }
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
