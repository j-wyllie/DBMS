package odms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import odms.data.ProfileDataIO;
import odms.profile.Procedure;
import odms.profile.Profile;

import java.time.LocalDate;
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
        descEntry.setDisable(true);
        descEntry.setVisible(false);
        dateEntry.setDisable(true);
        dateEntry.setVisible(false);
        summaryEntry.setDisable(true);
        summaryEntry.setVisible(false);
        saveButton.setDisable(true);
        saveButton.setVisible(false);

    }

    public void handleUndoButtonClicked(ActionEvent actionEvent) {
        undo();
    }

    public void handleRedoButtonClicked(ActionEvent actionEvent) {
        redo();
    }

    public void handleEditButtonClicked(ActionEvent actionEvent) {
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
        ProfileDataIO.saveData(getCurrentDatabase(), "example/example.json");
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
