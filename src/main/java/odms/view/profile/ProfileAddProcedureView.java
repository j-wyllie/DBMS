package odms.view.profile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import odms.controller.data.ProfileDataIO;
import odms.controller.procedure.ProcedureAddController;
import odms.model.enums.OrganEnum;
import odms.model.profile.Procedure;
import odms.model.profile.Profile;

import java.time.LocalDate;
import java.util.ArrayList;

import static odms.controller.GuiMain.getCurrentDatabase;

public class ProfileAddProcedureView {

    private static Profile searchedDonor;

    private ProcedureAddController controller = new ProcedureAddController(this);

    @FXML
    private TextField summaryField;

    @FXML
    private DatePicker dateOfProcedureDatePicker;

    @FXML
    private TextField descriptionField;

    @FXML
    private static Label warningLabel;

    @FXML
    private Button addButton;

    @FXML
    private static ListView<OrganEnum> affectedOrgansListView;

    private static ObservableList<OrganEnum> donatedOrgans;

    @FXML
    public void handleAddButtonClicked(ActionEvent actionEvent) {
        try {
            controller.add();
            //todo controller.refreshProcedureTable();
            ProfileDataIO.saveData(getCurrentDatabase(), "example/example.json");
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            warningLabel.setVisible(true);
        }
    }

    @FXML
    private void onEnter(ActionEvent event) {
        handleAddButtonClicked(event);
    }

    /**
     * Run whenever this view is called
     *
     * @param p
     */
    public static void init(Profile p) {
        warningLabel.setVisible(false);
        searchedDonor = p;
        affectedOrgansListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        donatedOrgans = FXCollections
                .observableArrayList(p.getOrgansDonated());
        affectedOrgansListView.setItems(donatedOrgans);
    }

    public Profile getSearchedDonor() {
        return searchedDonor;
    }

    public String getSummaryField(){
        return summaryField.getText();
    }

    public LocalDate getDateOfProcedureDatePicker() {
        return dateOfProcedureDatePicker.getValue();
    }

    public String getDescriptionField() {
        return descriptionField.getText();
    }

    public ArrayList getAffectedOrgansListView() {
        return new ArrayList<>(affectedOrgansListView.getSelectionModel().getSelectedItems());
    }
}