package odms.view.profile;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.controller.profile.ProcedureEdit;
import odms.commons.model.enums.OrganEnum;
import odms.view.CommonView;

import java.time.LocalDate;
import java.util.ArrayList;

public class ProcedureDetailed extends CommonView {
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
    private ProcedureEdit controller = new ProcedureEdit(this);
    private ObjectProperty<Profile> profile;
    private ProceduresDisplay parent;

    @FXML
    public void initialize(Procedure selectedProcedure, ObjectProperty<Profile> currentProfile,
            ProceduresDisplay p) {
        parent = p;
        profile = currentProfile;
        warningLabel.setVisible(false);
        currentProcedure = selectedProcedure;
        procedureSummaryLabel.setText(currentProcedure.getSummary());
        procedureDateLabel.setText("Date " + " " + currentProcedure.getDate().toString());
        procedureDescriptionLabel
                .setText("Description: " + " " + currentProcedure.getLongDescription());
        procedureOrgansLabel.setText(
                "Donations Affected: " + " " + currentProcedure.getOrgansAffected().toString());
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
        //todo get rid of calls to model?
        try {
            affectedOrgansListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            ObservableList<OrganEnum> organsDonated = FXCollections
                    .observableArrayList(profile.get().getOrgansDonated());
            affectedOrgansListView.setItems(organsDonated);
            editButton.setVisible(true);
        } catch (NullPointerException e) {
            System.out.println("Not clinician");
            editButton.setVisible(false);
        }

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
        //todo change save data?
        controller.save();
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
        procedureSummaryLabel
                .setText(procedureSummaryLabel.getText() + " " + currentProcedure.getSummary());
        procedureDateLabel.setText(
                procedureDateLabel.getText() + " " + currentProcedure.getDate().toString());
        procedureDescriptionLabel.setText(
                procedureDescriptionLabel.getText() + " " + currentProcedure.getLongDescription());
        procedureOrgansLabel.setText(
                procedureOrgansLabel.getText() + " " + currentProcedure.getOrgansAffected()
                        .toString());
        parent.refreshProcedureTable();
    }

    public Profile getProfile() {return profile.getValue();}
    public Procedure getCurrentProcedure() {return currentProcedure;}
    public String getDescEntry() {return descEntry.getText();}
    public String getSummaryEntry() {return summaryEntry.getText();}
    public LocalDate getDateOfProcedure() {return dateOfProcedureDatePicker.getValue();}
    public ArrayList getAffectedOrgansListView() {return new ArrayList<>(affectedOrgansListView.getSelectionModel().getSelectedItems());}

}
