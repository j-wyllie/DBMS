package odms.controller;

import static odms.controller.LoginController.getCurrentUser;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import odms.profile.Organ;
import odms.profile.Profile;
import odms.user.User;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.table.TableFilter;

public class ClinicianProfileController extends CommonController {

    private static User currentUser = getCurrentUser();

    @FXML
    private Label clinicianFullName;

    @FXML
    private Label givenNamesLabel;

    @FXML
    private Label staffIdLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label regionLabel;

    @FXML
    private TableView<Profile> searchTable;

    @FXML
    private TableColumn<Profile, String> fullNameColumn;

    @FXML
    private TableColumn<Profile, Integer> ageColumn;

    @FXML
    private TableColumn<Profile, String> genderColumn;

    @FXML
    private TableColumn<Profile, String> regionColumn;

    @FXML
    private TextField searchField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField ageRangeField;

    @FXML
    private TextField regionField;

    @FXML
    private CheckComboBox genderCombobox;

    @FXML
    private CheckComboBox typeCombobox;

    @FXML
    private CheckComboBox organsCombobox;

    @FXML
    private TextField transplantSearchField;


    @FXML
    private TableView transplantTable;

    private ObservableList<Profile> donorObservableList;

    private ObservableList<Entry<Profile, Organ>> receiverObservableList;

    private Profile selectedDonor;

    private ObservableList<String> genderStrings = FXCollections.observableArrayList();

    private ObservableList<String> typeStrings = FXCollections.observableArrayList();

    private ObservableList<String> organsStrings = FXCollections.observableArrayList();


    /**
     * Scene change to log in view.
     * @param event clicking on the logout button.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
        showLoginScene(event);
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
        String scene = "/view/ClinicianProfileEdit.fxml";
        String title = "Edit Profile";

        showScene(event, scene, title, true);
    }


    /**
     * Button handler to update donor table based on search results.
     * @param event releasing a key on the keyboard.
     */
    @FXML
    private void handleSearchDonors(KeyEvent event) {
        updateSearchTable();
    }


    /**
     * Clears the searchTable and updates with search results of profiles from the fuzzy search.
     */
    private void updateSearchTable() {

        System.out.println("update search table");

        List selectedGenders;
        List selectedTypes;
        ObservableList selectedOrgans;

        selectedGenders = genderCombobox.getCheckModel().getCheckedItems();
        selectedTypes = typeCombobox.getCheckModel().getCheckedItems();
        selectedOrgans = organsCombobox.getCheckModel().getCheckedItems();


        String searchString = searchField.getText();
        String regionSearchString = regionField.getText();

        int ageSearchInt;
        try {
            ageSearchInt = Integer.parseInt(ageField.getText());
        } catch (NumberFormatException e) {
            ageSearchInt = -999;
        }

        int ageRangeSearchInt;
        try {
            ageRangeSearchInt = Integer.parseInt(ageRangeField.getText());
        } catch (NumberFormatException e) {
            ageRangeSearchInt = -999;
        }


        searchTable.getItems().clear();
        donorObservableList.addAll(GuiMain.getCurrentDatabase().searchProfiles(searchString, ageSearchInt, ageRangeSearchInt, regionSearchString, selectedGenders, selectedTypes, selectedOrgans));
        searchTable.setItems(donorObservableList);
    }

    /**
     * Sets all the clinicians details in the GUI.
     */
    @FXML
    private void setClinicianDetails(){
        clinicianFullName.setText(currentUser.getName());
        givenNamesLabel.setText(givenNamesLabel.getText() + currentUser.getName());
        staffIdLabel.setText(staffIdLabel.getText() + currentUser.getStaffId().toString());
        addressLabel.setText(addressLabel.getText() + currentUser.getWorkAddress());
        regionLabel.setText(regionLabel.getText() + currentUser.getRegion());
    }

    /**
     * Initializes and refreshes the search table
     * Adds a listener to each row so that when it is double clicked
     * a new donor window is opened.
     * Calls the setTooltipToRow function.
     */
    @FXML
    private void makeTable(ArrayList<Profile> donors){
        searchTable.getItems().clear();

        donorObservableList = FXCollections.observableArrayList(donors);
        searchTable.setItems(donorObservableList);
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        searchTable.getColumns().setAll(fullNameColumn, ageColumn, genderColumn, regionColumn);

        searchTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    searchTable.getSelectionModel().getSelectedItem() != null) {
                createNewDonorWindow((Profile) searchTable.getSelectionModel().getSelectedItem());
            }
        });

        genderCombobox.addEventHandler(ComboBox.ON_HIDING, event -> {
            updateSearchTable();
        });
        genderCombobox.addEventHandler(ComboBox.ON_SHOWING, event -> {
            updateSearchTable();
        });
        organsCombobox.addEventHandler(ComboBox.ON_HIDING, event -> {
            updateSearchTable();
        });
        organsCombobox.addEventHandler(ComboBox.ON_SHOWING, event -> {
            updateSearchTable();
        });
        typeCombobox.addEventHandler(ComboBox.ON_HIDING, event -> {
            updateSearchTable();
        });
        typeCombobox.addEventHandler(ComboBox.ON_SHOWING, event -> {
            updateSearchTable();
        });


        addTooltipToRow();
    }

    /**
     * adds a tooltip to each row of the table
     * containing their organs donated.
     */
    private void addTooltipToRow() {
        searchTable.setRowFactory(tableView -> {
            final TableRow<Profile> row = new TableRow<>();

            row.hoverProperty().addListener((observable) -> {
                final Profile donor = row.getItem();
                String donations = "";
                if (row.isHover() && donor != null) {
                    if(donor.getOrgansDonated().size() > 0) {
                        donations = ". Donor: " + donor.getOrgansDonated().toString();
                    }
                    row.setTooltip(new Tooltip(donor.getFullName() + donations));
                }
            });
            return row;
        });
    }

    /**
     * Creates a new window when a row in the search table is double clicked.
     * The new window contains a donors profile.
     * @param donor The donor object that has been clicked on
     */
    @FXML
    private void createNewDonorWindow(Profile donor) {
        selectedDonor = donor;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/ProfileDisplay.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            ProfileDisplayController controller = fxmlLoader.getController();
            controller.setDonor(selectedDonor);
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

    /**
     * Initializes and refreshes the search table
     * Adds a listener to each row so that when it is double clicked
     * a new donor window is opened.
     * Calls the setTooltipToRow function.
     */
    @FXML
    private void makeTransplantWaitingList(List<Entry<Profile, Organ>> receivers){
        transplantTable.getColumns().clear();

        receiverObservableList = FXCollections.observableList(receivers);
        //transplantTable.setItems(receiverObservableList);
        //transplantOrganRequiredCol.setCellValueFactory(new PropertyValueFactory<>("organ"));
        //transplantOrganDateCol.setCellFactory(new PropertyValueFactory<>("date"));
        //transplantReceiverNameCol.setCellValueFactory(new PropertyValueFactory("fullName"));
        //transplantRegionCol.setCellValueFactory(new PropertyValueFactory("region"));

        TableColumn<Map.Entry<Profile, Organ>, String> transplantOrganRequiredCol  = new TableColumn<>("Organs Required");
        //organRequiredCol.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue(0));
        transplantOrganRequiredCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getValue().getName()));

        TableColumn<Map.Entry<Profile, Organ>, String> transplantReceiverNameCol  = new TableColumn<>("Name");
        transplantReceiverNameCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getKey().getFullName()));

        TableColumn<Map.Entry<Profile, Organ>, String> transplantRegionCol  = new TableColumn<>("Region");
        transplantRegionCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getKey().getRegion()));

        TableColumn<Map.Entry<Profile, Organ>, String> transplantDateCol  = new TableColumn<>("Date");
        transplantDateCol.setCellValueFactory(
                cdf -> new SimpleStringProperty((cdf.getValue().getValue().getDate()).toString()));

        transplantTable.getColumns().add(transplantOrganRequiredCol);
        transplantTable.getColumns().add(transplantReceiverNameCol);
        transplantTable.getColumns().add(transplantRegionCol);
        transplantTable.getColumns().add(transplantDateCol);
        transplantTable.setItems(receiverObservableList);

        transplantTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() &&
                event.getClickCount() == 2 &&
                transplantTable.getSelectionModel().getSelectedItem() != null) {

                createNewDonorWindow(((Entry<Profile, Organ>) transplantTable.getSelectionModel().getSelectedItem()).getKey());
            }
        });

        addTooltipToRow();

    }

    /**
     * Refresh the search and transplant medication tables with the most up to date data
     */
    @FXML
    private void refreshTable() {
        makeTable(GuiMain.getCurrentDatabase().getProfiles(false));
        try {
            makeTransplantWaitingList(GuiMain.getCurrentDatabase().getAllOrgansRequired());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize(){

        genderStrings.add("male");
        genderStrings.add("female");
        genderStrings.add("other");
        genderCombobox.getItems().setAll(genderStrings);

        organsStrings.addAll(Organ.toArrayList());
        organsCombobox.getItems().setAll(organsStrings);

        typeStrings.add("receiver");
        typeStrings.add("donor");
        typeCombobox.getItems().setAll(typeStrings);


        setClinicianDetails();
        makeTable(GuiMain.getCurrentDatabase().getProfiles(false));
        try {
            makeTransplantWaitingList(GuiMain.getCurrentDatabase().getAllOrgansRequired());
        } catch (Exception e) {
            e.printStackTrace();
        }

        TableFilter filter = new TableFilter<>(transplantTable);
        //filter.getColumnFilters().setAll(transplantTable.getItems());

    }
}
