package odms.view.user;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import odms.App;
import odms.cli.CommandGUI;
import odms.cli.CommandLine;
import odms.controller.GuiMain;
import odms.controller.data.DataManagementControllerPOTENTIALTODO;
import odms.controller.history.RedoController;
import odms.controller.history.UndoController;
import odms.controller.user.ClinicianProfileEditController;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.model.user.UserType;
import odms.view.CommonView;
import odms.view.profile.ProfileDisplayControllerTODO;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.table.TableFilter;

import java.io.IOException;
import java.util.*;

public class ClinicianProfileView extends CommonView {
    // Constant that holds the number of search results displayed on a page at a time.
    private static final int PAGESIZE = 25;
    // Constant that holds the max number of search results that can be displayed.
    private static final int MAXPAGESIZE = 200;
    private static Collection<Stage> openProfileStages = new ArrayList<>();
    private User currentUser;
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
    private TextField ageField;
    @FXML
    private TextField ageRangeField;
    @FXML
    private TextField regionField;
    @FXML
    private ComboBox genderCombobox;
    @FXML
    private ComboBox typeCombobox;
    @FXML
    private CheckComboBox<OrganEnum> organsCombobox;
    @FXML
    private TextField transplantSearchField;
    @FXML
    private Label donorStatusLabel;
    @FXML
    private CheckBox ageRangeCheckbox;
    @FXML
    private Tab viewUsersTab;
    @FXML
    private Tab consoleTab;
    @FXML
    private ViewUsersView viewUsersView;
    @FXML
    private TableView transplantTable;
    @FXML
    private TextArea displayTextArea;
    @FXML
    private Tab dataManagementTab;
    @FXML
    private AnchorPane dataManagement;
    @FXML
    private DataManagementControllerPOTENTIALTODO dataManagementControllerPOTENTIALTODO;
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
    private ObservableList<Profile> donorObservableList = FXCollections.observableArrayList();
    private ObservableList<Map.Entry<Profile, OrganEnum>> receiverObservableList;
    private Profile selectedDonor;
    private RedoController redoController = new RedoController();
    private UndoController undoController = new UndoController();
    private CommandGUI commandGUI;
    private ObservableList<String> genderStrings = FXCollections.observableArrayList();
    private ObservableList<String> typeStrings = FXCollections.observableArrayList();
    private ObservableList<String> organsStrings = FXCollections.observableArrayList();
    private ArrayList<Profile> profileSearchResults = new ArrayList<>();

    /**
     * Checks if there are unsaved changes in any open window.
     *
     * @return true if there are unsaved changes.
     */
    public static boolean checkUnsavedChanges(Stage currentStage) {
        for (Stage stage : openProfileStages) {
            //todo maybe need to move isEdited from common controller to common view?
            if (isEdited(stage) && stage.isShowing()) {
                return true;
            }
        }

        return isEdited(currentStage);
    }

    /**
     * closes all open profile windows that the user has opened.
     */
    public static void closeAllOpenProfiles() {
        for (Stage stage : openProfileStages) {
            if (stage.isShowing()) {
                stage.close();
            }
        }
    }

    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
        currentUser = null;
        changeScene(event, "/view/Login.fxml");
    }

    /**
     * Button handler to undo last action.
     *
     * @param event clicking on the undo button.
     */
    @FXML
    private void handleUndoButtonClicked(ActionEvent event) throws IOException {
        //todo replace with standardised?
        undoController.undo(GuiMain.getCurrentDatabase());
        Parent parent = FXMLLoader.load(getClass().getResource("/view/ClinicianProfile.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

    /**
     * Button handler to redo last undo action.
     *
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event) throws IOException {
        //todo replace with standardised?
        redoController.redo(GuiMain.getCurrentDatabase());
        Parent parent = FXMLLoader.load(getClass().getResource("/view/ClinicianProfile.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

    /**
     * Button handler to make fields editable.
     *
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ClinicianProfileEdit.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        //todo replace scene change with standardised and controller with view
        ClinicianProfileEditView v = fxmlLoader.getController();
        v.setCurrentUser(currentUser);
        v.initialize();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Edit profile");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleAgeRangeCheckboxChecked() {
        if (ageRangeCheckbox.isSelected()) {
            ageRangeField.setDisable(false);
            ageField.setPromptText("Lower Age");
            ageRangeField.setPromptText("Upper Age");
            ageRangeField.clear();
        } else {
            ageRangeField.setDisable(true);
            ageField.setPromptText("Age");
        }
        updateLabels();
    }

    /**
     * Button handler to display all search results in the search table
     *
     * @param event clicking on the show all button.
     */
    @FXML
    private void handleGetAllResults(ActionEvent event) {
        buttonShowAll.setVisible(false);
        buttonShowNext.setVisible(false);
        updateTable(true, false);
        labelCurrentOnDisplay.setText("displaying 1 to " + searchTable.getItems().size());
    }

    /**
     * Button handler to display next 25 search results in the search table
     *
     * @param event clicking on the show all button.
     */
    @FXML
    private void handleGetXResults(ActionEvent event) {
        updateTable(false, true);
        labelCurrentOnDisplay.setText("displaying 1 to " + searchTable.getItems().size());
    }

    /**
     * Mouse handler to update search table based on search results.
     *
     * @param event clicking the mouse
     */
    @FXML
    private void handleSearchDonorsMouse(MouseEvent event) {
        updateLabels();
    }

    /**
     * Button handler to update donor table based on search results. Makes call to get fuzzy search
     * results of profiles.
     *
     * @param event releasing a key on the keyboard.
     */
    @FXML
    private void handleSearchDonors(KeyEvent event) {
        updateLabels();
    }

    /**
     * updates the display labels and button status in the search tab.
     */
    private void updateLabels() {
        labelToManyResults.setVisible(false);

        updateSearchTable();

        if (profileSearchResults == null || profileSearchResults.size() == 0) {
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
     * Clears the searchTable and updates with search results of profiles from the fuzzy search.
     */
    private void updateSearchTable() {
        String selectedGender = null;
        String selectedType = null;
        ObservableList<OrganEnum> selectedOrgans;

        selectedOrgans = organsCombobox.getCheckModel().getCheckedItems();

        if (!typeCombobox.getSelectionModel().isEmpty()) {
            selectedType = typeCombobox.getValue().toString();
        }

        if (!genderCombobox.getSelectionModel().isEmpty()) {
            selectedGender = genderCombobox.getValue().toString();
        }

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
            if (ageRangeCheckbox.isSelected()) {
                ageRangeSearchInt = Integer.parseInt(ageRangeField.getText());
            } else {
                ageRangeSearchInt = -999;
            }
        } catch (NumberFormatException e) {
            ageRangeSearchInt = -999;
        }

        searchTable.getItems().clear();
        donorObservableList.clear();
        donorObservableList.addAll(GuiMain.getCurrentDatabase().searchProfiles(
                searchString,
                ageSearchInt,
                ageRangeSearchInt,
                regionSearchString,
                selectedGender,
                selectedType,
                new HashSet<>(selectedOrgans)
        ));
        updateTable(false, false);
    }

    /**
     * Clears the searchTable and updates with objects from the profileSearchResults arrayList.
     * Results displayed depend on the variable showAll, if this is false it will display only 50 or
     * less (if profileSearchResults is smaller than 50) results. If it is true all results will be
     * displayed, as long as profileSearchResults is under 200 objects in size.
     *
     * @param showAll  boolean, if true will display all objects in class variable
     *                 profileSearchResults.
     * @param showNext boolean, if true will display next 25 results.
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
                    if (profileSearchResults.subList(size + PAGESIZE, profileSearchResults.size())
                            .size() < PAGESIZE) {
                        buttonShowNext.setText("Show next " + profileSearchResults
                                .subList(size + PAGESIZE, profileSearchResults.size()).size()
                                + " results");
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
    private void setClinicianDetails() {
        donorStatusLabel.setText(currentUser.getUserType().getName());
        clinicianFullName.setText(currentUser.getName());
        givenNamesLabel.setText(
                givenNamesLabel.getText() + (
                        currentUser.getName() != null ? currentUser.getName() : ""));
        staffIdLabel.setText(
                staffIdLabel.getText() + (
                        currentUser.getStaffID() != null ? currentUser.getStaffID() : ""));
        addressLabel.setText(
                addressLabel.getText() +
                        (currentUser.getWorkAddress() != null ? currentUser.getWorkAddress() : "")
        );
        regionLabel.setText(
                regionLabel.getText() +
                        (currentUser.getRegion() != null ? currentUser.getRegion() : "")
        );
    }

    /**
     * Initializes and refreshes the search table Adds a listener to each row so that when it is
     * double clicked a new donor window is opened. Calls the setTooltipToRow function.
     */
    @FXML
    private void makeSearchTable(ArrayList<Profile> donors) {
        labelResultCount.setText(0 + " results found");
        searchTable.getItems().clear();

        donorObservableList = FXCollections.observableArrayList(donors);
        searchTable.setItems(donorObservableList);
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        donorReceiverColumn.setCellValueFactory(new PropertyValueFactory<>("donorReceiver"));
        searchTable.getColumns()
                .setAll(fullNameColumn, donorReceiverColumn, ageColumn, genderColumn, regionColumn);

        searchTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    searchTable.getSelectionModel().getSelectedItem() != null) {
                createNewDonorWindow(searchTable.getSelectionModel().getSelectedItem());
            }
        });

        genderCombobox.addEventHandler(ComboBox.ON_HIDING, event -> {
            updateLabels();
        });
        genderCombobox.addEventHandler(ComboBox.ON_SHOWING, event -> {
            updateLabels();
        });
        organsCombobox.addEventHandler(ComboBox.ON_HIDING, event -> {
            updateLabels();
        });

        addTooltipToRow();
    }

    /**
     * adds a tooltip to each row of the table containing their organs donated.
     */
    private void addTooltipToRow() {
        searchTable.setRowFactory(tableView -> {
            final TableRow<Profile> row = new TableRow<>();

            row.hoverProperty().addListener((observable) -> {
                final Profile donor = row.getItem();
                String donations = "";
                if (row.isHover() && donor != null) {
                    if (donor.getOrgansDonated().size() > 0) {
                        donations = ". Donor: " + donor.getOrgansDonated().toString();
                    }
                    row.setTooltip(new Tooltip(donor.getFullName() + donations));
                }
            });
            return row;
        });
    }

    /**
     * Limits the characters entered in textfield to only digits and maxLength
     *
     * @param maxLength that can be entered in the textfield
     * @return
     */
    public EventHandler<KeyEvent> numeric_Validation(final Integer maxLength) {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                TextField txt_TextField = (TextField) e.getSource();
                if (txt_TextField.getText().length() >= maxLength) {
                    e.consume();
                }
                if (e.getCharacter().matches("[0-9.]")) {
                    if (txt_TextField.getText().contains(".") && e.getCharacter().matches("[.]")) {
                        e.consume();
                    } else if (txt_TextField.getText().length() == 0 && e.getCharacter()
                            .matches("[.]")) {
                        e.consume();
                    }
                } else {
                    e.consume();
                }
            }
        };
    }

    /**
     * Creates a new window when a row in the search table is double clicked. The new window
     * contains a donors profile.
     *
     * @param donor The donor object that has been clicked on
     */
    @FXML
    private void createNewDonorWindow(Profile donor) {
        selectedDonor = donor;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/ProfileDisplay.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            //todo replace with standardised method and view
            ProfileDisplayControllerTODO controller = fxmlLoader.getController();
            controller.setProfileViaClinician(selectedDonor);
            controller.initialize();

            Stage stage = new Stage();
            stage.setTitle(selectedDonor.getFullName() + "'s profile");
            stage.setScene(scene);
            stage.show();
            stage.setOnCloseRequest((WindowEvent event) -> {
                closeStage(stage);
            });
            openProfileStages.add(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes and refreshes the search table Adds a listener to each row so that when it is
     * double clicked a new donor window is opened. Calls the setTooltipToRow function.
     */
    @FXML
    private void makeTransplantWaitingList(List<Map.Entry<Profile, OrganEnum>> receivers) {
        transplantTable.getColumns().clear();

        receiverObservableList = FXCollections.observableList(receivers);
        //transplantTable.setItems(receiverObservableList);
        //transplantOrganRequiredCol.setCellValueFactory(new PropertyValueFactory<>("organ"));
        //transplantOrganDateCol.setCellFactory(new PropertyValueFactory<>("date"));
        //transplantReceiverNameCol.setCellValueFactory(new PropertyValueFactory("fullName"));
        //transplantRegionCol.setCellValueFactory(new PropertyValueFactory("region"));

        TableColumn<Map.Entry<Profile, OrganEnum>, String> transplantOrganRequiredCol = new TableColumn<>(
                "Organs Required");
        //organRequiredCol.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue(0));
        transplantOrganRequiredCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getValue().getName()));

        TableColumn<Map.Entry<Profile, OrganEnum>, String> transplantReceiverNameCol = new TableColumn<>(
                "Name");
        transplantReceiverNameCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getKey().getFullName()));

        TableColumn<Map.Entry<Profile, OrganEnum>, String> transplantRegionCol = new TableColumn<>(
                "Region");
        transplantRegionCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getKey().getRegion()));

        TableColumn<Map.Entry<Profile, OrganEnum>, String> transplantDateCol = new TableColumn<>(
                "Date");
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

                createNewDonorWindow(
                        ((Map.Entry<Profile, OrganEnum>) transplantTable.getSelectionModel()
                                .getSelectedItem()).getKey());
            }
        });

        addTooltipToRow();
    }

    /**
     * Initializes the controller for the view users Tab
     */
    public void handleViewUsersTabClicked() {
        viewUsersView.setCurrentUser(currentUser);
        viewUsersView.setUpUsersTable();
    }

    /**
     * Refresh the search and transplant medication tables with the most up to date data
     */
    @FXML
    private void refreshTable() {
        makeSearchTable(GuiMain.getCurrentDatabase().getProfiles(false));
        try {
            makeTransplantWaitingList(GuiMain.getCurrentDatabase().getAllOrgansRequired());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTabDataManagementClicked() {
        dataManagementControllerPOTENTIALTODO.setCurrentUser(currentUser);
    }

    /**
     * Hides/Shows certain nodes if the clinician does / does not have permission to view them
     */
    private void setupAdmin() {
        if (currentUser.getUserType() == UserType.CLINICIAN) {
            dataManagementTab.setDisable(true);
            viewUsersTab.setDisable(true);
            consoleTab.setDisable(true);
        } else {
            dataManagementTab.setDisable(false);
            viewUsersTab.setDisable(false);
            consoleTab.setDisable(false);

            // Initialize command line GUI
            commandGUI = new CommandGUI(displayTextArea);
            System.setIn(commandGUI.getIn());
            System.setOut(commandGUI.getOut());

            // Start the command line in an alternate thread
            CommandLine commandLine = new CommandLine(App.getProfileDb(), commandGUI.getIn(),
                    commandGUI.getOut());
            commandGUI.initHistory(commandLine);
            Thread t = new Thread(commandLine);
            t.start();
        }
    }

    @FXML
    public void initialize() {
        if (currentUser != null) {
            ageRangeField.setDisable(true);
            ageField.addEventHandler(KeyEvent.KEY_TYPED, numeric_Validation(10));
            ageRangeField.addEventHandler(KeyEvent.KEY_TYPED, numeric_Validation(10));

            genderStrings.clear();
            genderStrings.add("any");
            genderStrings.add("male");
            genderStrings.add("female");
            genderCombobox.getItems().setAll(genderStrings);
            genderCombobox.getSelectionModel().selectFirst();

            organsStrings.clear();
            organsStrings.addAll(OrganEnum.toArrayList());
            organsCombobox.getItems().setAll(OrganEnum.values());

            typeStrings.clear();
            typeCombobox.getItems().clear();
            typeStrings.add("any");
            typeStrings.add("donor");
            typeStrings.add("receiver");
            typeCombobox.getItems().addAll(typeStrings);
            typeCombobox.getSelectionModel().selectFirst();

            typeCombobox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    updateSearchTable();
                }
            });

            genderCombobox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    updateSearchTable();
                }
            });

            TableFilter filter = new TableFilter<>(transplantTable);

            setClinicianDetails();
            setupAdmin();
            makeSearchTable(GuiMain.getCurrentDatabase().getProfiles(false));
            searchTable.getItems().clear();
            searchTable.setPlaceholder(new Label(
                    "There are " + GuiMain.getCurrentDatabase().getProfiles(false).size()
                            + " profiles"));
            try {
                makeTransplantWaitingList(GuiMain.getCurrentDatabase().getAllOrgansRequired());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private void closeStage(Stage stage) {
        openProfileStages.remove(stage);
    }

}
