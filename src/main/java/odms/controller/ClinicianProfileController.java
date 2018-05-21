package odms.controller;

import static odms.controller.LoginController.getCurrentUser;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

import java.io.IOException;
import java.util.ArrayList;
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
import javafx.stage.Stage;
import odms.profile.Organ;
import odms.profile.Profile;
import odms.user.User;
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
    private TableColumn<Profile, String> donorReceiverColumn;

    @FXML
    private TableColumn<Profile, Integer> ageColumn;

    @FXML
    private TableColumn<Profile, String> genderColumn;

    @FXML
    private TableColumn<Profile, String> regionColumn;

    @FXML
    private TextField searchField;

    @FXML
    private TextField transplantSearchField;

    @FXML
    private TableView transplantTable;

    @FXML
    private Label labelResultCount;

    @FXML
    private Label labelCurrentOnDisplay;

    @FXML
    private Label labelToManyResults;

    @FXML
    private Button buttonShowAll;

    @FXML
    private Button buttonShowNext;

    private ObservableList<Profile> donorObservableList;

    private ObservableList<Entry<Profile, Organ>> receiverObservableList;

    private Profile selectedDonor;

    private ArrayList<Profile> profileSearchResults;

    // Constant that holds the number of search results displayed on a page at a time.
    private static final int PAGESIZE = 25;

    // Constant that holds the max number of search results that can be displayed.
    private static final int MAXPAGESIZE = 200;

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
     * Button handler to display all search results in the search table
     * @param event clicking on the show all button.
     */
    @FXML
    private void handleGetAllResults(ActionEvent event) {
        buttonShowAll.setVisible(false);
        buttonShowNext.setVisible(false);
        updateTable(true, false);
        labelCurrentOnDisplay.setText("displaying 1 to " + searchTable.getItems().size());
    }

    @FXML
    private void handleGetXResults(ActionEvent event) {
        updateTable(false, true);
        labelCurrentOnDisplay.setText("displaying 1 to " + searchTable.getItems().size());
    }

    /**
     * Button handler to update donor table based on search results. Makes call to get fuzzy search results of profiles.
     * @param event releasing a key on the keyboard.
     */
    @FXML
    private void handleSearchDonors(KeyEvent event) {
        String searchString = searchField.getText();
        labelToManyResults.setVisible(false);
        profileSearchResults = GuiMain.getCurrentDatabase().searchProfiles(searchString);
        updateTable(false, false);
        if (profileSearchResults == null) {
            labelCurrentOnDisplay.setText("displaying 0 to 0");
            labelResultCount.setText("0 results found");
            buttonShowAll.setVisible(false);
            buttonShowNext.setVisible(false);
        } else {
            if (profileSearchResults.size() <= PAGESIZE) {
                labelCurrentOnDisplay.setText("displaying 1 to " + profileSearchResults.size());
                buttonShowAll.setVisible(false);
                buttonShowNext.setVisible(false);
            } else {
                labelCurrentOnDisplay.setText("displaying 1 to " + PAGESIZE);
                if (profileSearchResults.size() > MAXPAGESIZE) {
                    labelToManyResults.setVisible(true);
                    buttonShowAll.setVisible(false);
                    buttonShowNext.setVisible(false);
                } else {
                    buttonShowAll.setText("Show all " + profileSearchResults.size() + " results");
                    buttonShowNext.setText("Show next 25 results");
                    buttonShowAll.setVisible(true);
                    buttonShowNext.setVisible(true);
                }
            }
        }
    }

    /**
     * Clears the searchTable and updates with objects from the profileSearchResults arrayList. Results displayed
     * depend on the variable showAll, if this is false it will display only 50 or less (if profileSearchResults is
     * smaller than 50) results. If it is true all results will be displayed, as long as profileSearchResults is under
     * 200 objects in size.
     * @param showAll boolean, if true will display all objects in class variable profileSearchResults.
     */
    private void updateTable(boolean showAll, boolean showNext) {
        int size = donorObservableList.size();
        searchTable.getItems().clear();
        if (profileSearchResults != null) {
            if (profileSearchResults.size() == 1) {
                labelResultCount.setText(profileSearchResults.size() + " result found");
            } else {
                labelResultCount.setText(profileSearchResults.size() + " results found");
            }

            if (showAll) {
                if (profileSearchResults.size() > 200) {
                    labelResultCount.setText(0 + " results found");
                } else {
                    donorObservableList.addAll(profileSearchResults);
                }
            } else if (showNext) {
                if (profileSearchResults.size() > (size + PAGESIZE)) {
                    donorObservableList.addAll(profileSearchResults.subList(0, size + PAGESIZE));
                    if (profileSearchResults.subList(size + PAGESIZE, profileSearchResults.size()).size() < PAGESIZE) {
                        buttonShowNext.setText("Show next " + profileSearchResults.subList(size + PAGESIZE, profileSearchResults.size()).size() + " results");
                    }
                } else {
                    donorObservableList.addAll(profileSearchResults);
                    buttonShowNext.setVisible(false);
                    buttonShowAll.setVisible(false);
                }

            } else if (profileSearchResults.size() > PAGESIZE) {
                donorObservableList.addAll(profileSearchResults.subList(0, PAGESIZE));
            } else {
                donorObservableList.addAll(profileSearchResults);
            }
            searchTable.setItems(donorObservableList);
        }
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
        labelResultCount.setText(0 + " results found");
        if (donors.size() > 30) {
            donors.clear();
            donors.addAll(donors.subList(0, 30));
        }
        searchTable.getItems().clear();

        donorObservableList = FXCollections.observableArrayList(donors);
        searchTable.setItems(donorObservableList);
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        donorReceiverColumn.setCellValueFactory(new PropertyValueFactory<>("donorReceiver"));
        searchTable.getColumns().setAll(fullNameColumn, donorReceiverColumn, ageColumn, genderColumn, regionColumn);

        searchTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    searchTable.getSelectionModel().getSelectedItem() != null) {
                createNewDonorWindow((Profile) searchTable.getSelectionModel().getSelectedItem());
            }
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
        try {
            makeTransplantWaitingList(GuiMain.getCurrentDatabase().getAllOrgansRequired());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize(){
        setClinicianDetails();

        makeTable(GuiMain.getCurrentDatabase().getProfiles(false));
        searchTable.getItems().clear();
        searchTable.setPlaceholder(new Label("There are " + GuiMain.getCurrentDatabase().getProfiles(false).size() + " profiles"));
        try {
            makeTransplantWaitingList(GuiMain.getCurrentDatabase().getAllOrgansRequired());
        } catch (Exception e) {
            e.printStackTrace();
        }


        TableFilter filter = new TableFilter<>(transplantTable);
        //filter.getColumnFilters().setAll(transplantTable.getItems());

    }
}
