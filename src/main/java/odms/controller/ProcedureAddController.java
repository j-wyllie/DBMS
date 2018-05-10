package odms.controller;

import static odms.controller.GuiMain.getCurrentDatabase;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import odms.data.ProfileDataIO;
import odms.profile.Organ;
import odms.profile.Procedure;
import odms.profile.Profile;

public class ProcedureAddController {

    private Profile searchedDonor;
    private ProfileDisplayController controller;

    @FXML
    private TextField summaryField;

    @FXML
    private DatePicker dateOfProcedureDatePicker;

    @FXML
    private TextField descriptionField;

    @FXML
    private Label warningLabel;

    @FXML
    private Button addButton;

    @FXML
    private ListView<Organ> affectedOrgansListView;

    private ObservableList<Organ> donatedOrgans;

    @FXML
    public void handleAddButtonClicked(ActionEvent actionEvent) {
        String summary = summaryField.getText();
        LocalDate dateOfProcedure = dateOfProcedureDatePicker.getValue();
        String longDescription = descriptionField.getText();

        Procedure procedure;

        try {
            LocalDate dob = controller.getSearchedDonor().getDateOfBirth();
            if (dob.isAfter(dateOfProcedure)){
                throw new IllegalArgumentException();
            }

            if (longDescription.equals("")) {
                procedure = new Procedure(summary, dateOfProcedure);

            } else {
                procedure = new Procedure(summary, dateOfProcedure, longDescription);
            }

            procedure.setOrgansAffected(new ArrayList<>(affectedOrgansListView.getSelectionModel().getSelectedItems()));

            addProcedure(procedure);

        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException |DateTimeException e) {
            warningLabel.setVisible(true);
        }
    }

    /**
     * Adds a new condition to the current profile
     * @param procedure
     */
    private void addProcedure(Procedure procedure) {
        searchedDonor.addProcedure(procedure);
        controller.refreshProcedureTable();
        ProfileDataIO.saveData(getCurrentDatabase(), "example/example.json");
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onEnter(ActionEvent event) {
        handleAddButtonClicked(event);
    }

    /**
     * Run whenever this controller is called
     * @param controller
     */
    public void init(ProfileDisplayController controller) {
        warningLabel.setVisible(false);
        this.controller = controller;
        searchedDonor = controller.getSearchedDonor();

        affectedOrgansListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        donatedOrgans =  FXCollections.observableArrayList(controller.getSearchedDonor().getOrgansDonated());
        affectedOrgansListView.setItems(donatedOrgans);
    }

}
