package odms.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import odms.donor.Donor;
import odms.user.User;

import java.io.IOException;
import java.util.ArrayList;

import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

public class ClinicianProfileController {

    //Get the default clinician
    private static User currentUser = GuiMain.getUserDatabase().getClinician(0);

    @FXML
    private Label clinicianFullName;

    @FXML
    private Label givenNamesLabel;

    @FXML
    private Label lastNamesLabel;

    @FXML
    private Label staffIdLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label regionLabel;

    @FXML
    private TableView searchTable;

    @FXML
    private TableColumn fullNameColumn;

    @FXML
    private TableColumn ageColumn;

    @FXML
    private TableColumn genderColumn;

    @FXML
    private TableColumn regionColumn;

    @FXML
    private TextField searchField;

    private ObservableList<Donor> donorObservableList;

    private Donor selectedDonor;

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
        Parent parent = FXMLLoader.load(getClass().getResource("/view/EditClinicianProfile.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

    /**
     * Sets all the clinicians details in the GUI.
     */
    @FXML
    private void setClinicianDetails(){
        clinicianFullName.setText(currentUser.getName());
        givenNamesLabel.setText(givenNamesLabel.getText() + currentUser.getName());
        staffIdLabel.setText(staffIdLabel.getText() + currentUser.getStaffId().toString());
        regionLabel.setText(regionLabel.getText() + currentUser.getRegion());
    }

    /**
     * initializes and refreshes the search table
     */
    @FXML
    private void makeTable(ArrayList<Donor> donors){
        donorObservableList = FXCollections.observableArrayList(donors);
        searchTable.setItems(donorObservableList);
        TableColumn<Donor, String> ageCol = new TableColumn("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory("fullName"));
        regionColumn.setCellValueFactory(new PropertyValueFactory("region"));
        ageColumn.setCellValueFactory(new PropertyValueFactory("age"));
        genderColumn.setCellValueFactory(new PropertyValueFactory("gender"));
        ageCol.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().calculateAge())));
        searchTable.getColumns().setAll(fullNameColumn, ageCol, genderColumn, regionColumn);

        searchTable.setOnMouseClicked( event -> {
            if( event.getClickCount() == 2 ) {
                createNewDonorWindow((Donor) searchTable.getSelectionModel().getSelectedItem());
            }});
    }

    /**
     * Creates a new window when a row in the search table is double clicked.
     * The new window contains a donors profile.
     * @param donor The donor object that has been clicked on
     */
    @FXML
    private void createNewDonorWindow(Donor donor) {
        selectedDonor = donor;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/SearchedDonorProfile.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            SearchedDonorProfileController controller = fxmlLoader.<SearchedDonorProfileController>getController();
            controller.setSearchedDonor(selectedDonor);
            controller.initialize();

            Stage stage = new Stage();
            stage.setTitle(selectedDonor.getFullName() + "'s Profile");
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void initialize(){
        setClinicianDetails();
        makeTable(GuiMain.getCurrentDatabase().getDonors(false));
    }
}
