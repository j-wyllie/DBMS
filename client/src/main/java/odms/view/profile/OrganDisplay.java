package odms.view.profile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.enums.OrganSelectEnum;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.controller.database.DAOFactory;
import odms.view.CommonView;
import odms.view.user.TransplantWaitingList;

/**
 * Organ Display list and table views for profiles.
 */
@Slf4j
public class OrganDisplay extends CommonView {

    private static OrganSelectEnum windowType;

    private Profile currentProfile;
    private odms.controller.profile.OrganDisplay controller =
            new odms.controller.profile.OrganDisplay();

    private ObservableList<String> checkList = FXCollections.observableArrayList();

    private ObservableList<String> observableListDonated = FXCollections.observableArrayList();
    private ObservableList<String> observableListDonating = FXCollections.observableArrayList();
    private ObservableList<OrganEnum> observableListReceiving = FXCollections.observableArrayList();

    @FXML
    private ListView<String> listViewDonated;

    @FXML
    private ListView<String> listViewDonating;

    @FXML
    private Button donatingButton;

    @FXML
    private Label receivingLabel;

    @FXML
    private Button receivingButton;

    @FXML
    private GridPane organGridPane;

    @FXML
    private Button expiredButton;

    @FXML
    private Label donatedLabel;

    @FXML
    private Button donatedButton;

    @FXML
    private TableView<OrganEnum> tableViewReceiving;

    @FXML
    private TableColumn<OrganEnum, String> tableColumnOrgan;

    @FXML
    private TableColumn<OrganEnum, String> tableColumnDate;

    private TransplantWaitingList transplantWaitingListView;
    private User currentUser;

    /**
     * init organ display scene. Sets variables and object visibility status.
     *
     * @param p current profile being viewed
     * @param isClinician boolean, is true if is profile was opened by clinician/admin user
     * @param transplantWaitingList view for the transplantWaitingList. Will have null value if
     * @param user  the current logged in clin/admin
     */
    public void initialize(
            Profile p,
            Boolean isClinician,
            TransplantWaitingList transplantWaitingList,
            User user) {

        transplantWaitingListView = transplantWaitingList;
        currentProfile = controller.getUpdatedProfileDetails(p);
        currentUser = user;
        listViewDonating.setCellFactory(param -> new OrganDisplay.HighlightedCell());

        tableColumnDate.setCellFactory(param -> new HighlightedDateCell());
        tableColumnOrgan.setCellFactory(param -> new HighlightedOrganCell());

        listViewDonated.setItems(observableListDonated);
        listViewDonating.setItems(observableListDonating);

        tableViewReceiving.setPlaceholder(new Label(""));
        tableViewReceiving.setItems(observableListReceiving);
        tableColumnOrgan.setCellValueFactory(cdf -> new SimpleStringProperty(
                cdf.getValue().getNamePlain())
        );
        tableColumnDate.setCellValueFactory(cdf -> new SimpleStringProperty(
                cdf.getValue().getDate(currentProfile).format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                ))
        );
        tableViewReceiving.getColumns().setAll(tableColumnOrgan, tableColumnDate);
        tableViewReceiving.setSelectionModel(null);

        if (currentProfile.getDateOfDeath() != null) {
            donatingButton.setDisable(true);
            receivingButton.setDisable(true);
        }

        if (!isClinician) {
            viewAsProfileSetup();
        }

        populateOrganLists();

        if (isClinician) {
        listViewDonating.setMouseTransparent(false);
            listViewDonating.setOnMousePressed(event -> {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                        listViewDonating.getSelectionModel().getSelectedItem() != null) {
                    giveReasonForOverride(event,
                            listViewDonating.getSelectionModel().getSelectedItem());
                }
            });
        }

    }

    /**
     * Adjusts which elements are visible if the current observer is a profile.
     */
    private void viewAsProfileSetup() {
        if (DAOFactory.getOrganDao().getDonations(currentProfile).isEmpty()) {
            visibilityLists(listViewDonated, donatedLabel, donatedButton, 2, false);
        } else {
            visibilityLists(listViewDonated, donatedLabel, donatedButton, 2, true);
        }
        if (DAOFactory.getOrganDao().getRequired(currentProfile).isEmpty()) {
            ColumnConstraints zeroWidth = new ColumnConstraints();
            zeroWidth.setPrefWidth(0);
            organGridPane.getColumnConstraints().set(1, zeroWidth);

            tableViewReceiving.setPrefWidth(0);
            receivingLabel.setVisible(false);
            tableViewReceiving.setVisible(false);
        }
        receivingButton.setVisible(false);
        donatedButton.setVisible(false);

        expiredButton.setVisible(false);

        if (currentProfile.getDateOfDeath() == null) {
            RowConstraints zeroHeight = new RowConstraints();
            zeroHeight.setPrefHeight(0);
            // Expired button row in fxml is row 3.
            organGridPane.getRowConstraints().set(3, zeroHeight);
        } else {
            listViewDonating.setMouseTransparent(false);
        }
    }

    /**
     * Launch pane to add reasoning for organ expiry override.
     *
     * @param event the JavaFX event
     * @param organ the organ to specify reason for
     */
    private void giveReasonForOverride(Event event, String organ) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ProfileOrganOverride.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());
            OrganOverride overrideView = fxmlLoader.getController();
            overrideView.initialize(organ, currentProfile, currentUser);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Manual Organ Override");
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.centerOnScreen();
            stage.setOnHiding(ob -> refreshListViews());
            stage.show();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Handle the expired organs edit button being clicked.
     *
     * @param event the button press event.
     * @throws IOException if an IOException occurs.
     */
    @FXML
    private void handleBtnExpiredClicked(ActionEvent event) throws IOException {
        showExpiredOrgans(event);
    }

    /**
     * Handle the donating organs edit button being clicked.
     *
     * @param event the button press event.
     * @throws IOException if an IOException occurs.
     */
    @FXML
    private void handleBtnDonatingClicked(ActionEvent event) throws IOException {
        showOrgansSelectionWindow(event, OrganSelectEnum.DONATING);
    }

    /**
     * Handle the donated organs edit button being clicked.
     *
     * @param event the button press event.
     * @throws IOException if an IOException occurs.
     */
    @FXML
    private void handleBtnDonatedClicked(ActionEvent event) throws IOException {
        showOrgansSelectionWindow(event, OrganSelectEnum.DONATED);
    }

    /**
     * Handle the receiving organs edit button being clicked.
     *
     * @param event the button press event.
     * @throws IOException if an IOException occurs.
     */
    @FXML
    private void handleBtnRequiredClicked(ActionEvent event) throws IOException {
        showOrgansSelectionWindow(event, OrganSelectEnum.REQUIRED);
    }

    /**
     * Repopulate the ObservableLists with any Organ changes and repopulate the check list for
     * conflicting organs. Populates the checklist with donating organs for highlighting.
     */
    private void populateOrganLists() {
        currentProfile = controller.getUpdatedProfileDetails(currentProfile);

        populateOrganList(observableListDonating, controller.getDonatingOrgans(currentProfile));
        populateOrganList(observableListDonated, currentProfile.getOrgansDonated());
        populateListReceiving(observableListReceiving, currentProfile.getOrgansRequired());

        checkList.clear();

        for (OrganEnum organ : observableListReceiving) {
            if (observableListDonating.contains(organ.getNamePlain())) {
                checkList.add(organ.getNamePlain());
            }
        }

    }

    /**
     * Populate the receiving organs list, clearing the old.
     *
     * @param listReceiving the existing list of receiving organs.
     * @param organs the new list of items to populate the receiving list with.
     */
    private void populateListReceiving(
            ObservableList<OrganEnum> listReceiving,
            Set<OrganEnum> organs
    ) {

        listReceiving.clear();

        if (organs != null) {
            listReceiving.addAll(organs);
            Collections.sort(listReceiving);
        }
    }

    /**
     * Support function to populate an observable list with organs from an organ set.
     *
     * @param destinationList list to populate
     * @param organs source list of organs to populate from
     */
    private void populateOrganList(
            ObservableList<String> destinationList,
            Set<OrganEnum> organs
    ) {

        destinationList.clear();

        if (organs != null) {
            for (OrganEnum organ : organs) {
                destinationList.add(organ.getNamePlain());
            }
            Collections.sort(destinationList);
        }
    }


    /**
     * Refresh the ListViews to reflect changes made from the edit pane.
     */
    private void refreshListViews() {
        populateOrganLists();

        if (currentProfile.getDateOfDeath() != null) {
            receivingButton.setDisable(true);
        }

        listViewDonated.refresh();
        listViewDonating.refresh();
        tableViewReceiving.refresh();
    }

    private static void setWindowType(OrganSelectEnum type) {
        windowType = type;
    }

    /**
     * Display the Organ Expired view.
     *
     * @param event the base action event
     * @throws IOException if the fxml cannot load
     */
    private void showExpiredOrgans(ActionEvent event)
            throws IOException {
        Node source = (Node) event.getSource();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ProfileExpiredOrgans.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        OrganExpired view = fxmlLoader.getController();
        view.initialize(currentProfile, currentUser);

        Stage stage = new Stage();
        stage.setTitle("Expired Organs");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(source.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.centerOnScreen();
        stage.setOnHiding(ob -> {
            refreshListViews();
        });
        stage.show();
    }

    /**
     * Display the Organ Edit view.
     *
     * @param event the base action event
     * @param selectType the organ list type being changed
     * @throws IOException if the fxml cannot load
     */
    private void showOrgansSelectionWindow(ActionEvent event, OrganSelectEnum selectType)
            throws IOException {
        Node source = (Node) event.getSource();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ProfileOrganEdit.fxml"));
        setWindowType(selectType);

        Scene scene = new Scene(fxmlLoader.load());

        OrganEdit view = fxmlLoader.getController();
        view.setWindowType(windowType);
        view.initialize(currentProfile);

        Stage stage = new Stage();
        stage.setTitle(selectType.toString());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(source.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.centerOnScreen();
        stage.setOnHiding(ob -> {
            refreshListViews();
            if (transplantWaitingListView != null) {
                transplantWaitingListView.refreshTable();
            }
        });
        stage.show();
    }

    /**
     * Removes a specific list from view.
     *
     * @param list List to set invisible
     * @param label Label to set invisible.
     * @param button Button to set invisible.
     * @param column column to set values on.
     * @param bool boolean value, true if column should be visible.
     */
    private void visibilityLists(ListView<String> list, Label label, Button button, Integer column,
            Boolean bool) {
        if (!bool) {
            ColumnConstraints zeroWidth = new ColumnConstraints();
            zeroWidth.setPrefWidth(0);
            organGridPane.getColumnConstraints().set(column, zeroWidth);
        }

        list.setVisible(bool);
        label.setVisible(bool);
        button.setVisible(bool);
    }

    /**
     * Override the Cell Formatting for organ colour highlighting.
     */
    class HighlightedCell extends ListCell<String> {

        private static final String HIGHLIGHT = "cell-highlighted";

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            // Handle null item case
            if (item == null) {
                setText("");
                getStyleClass().remove(HIGHLIGHT);
                return;
            }

            setText(item);

            if (checkList.contains(item)) {
                getStyleClass().add(HIGHLIGHT);
            } else {
                getStyleClass().remove(HIGHLIGHT);
            }
        }
    }

    /**
     * Override the Cell Formatting for organ colour highlighting.
     */
    class HighlightedOrganCell extends TableCell<OrganEnum, String> {

        private static final String HIGHLIGHT = "cell-highlighted";

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            // Handle null item case
            if (item == null) {
                setText("");
                getStyleClass().remove(HIGHLIGHT);
                return;
            }

            setText(item);

            if (checkList.contains(item)) {
                this.getStyleClass().add(HIGHLIGHT);

            } else {
                this.getStyleClass().remove(HIGHLIGHT);
            }
        }
    }

    /**
     * Override the Cell Formatting for date colour highlighting.
     *
     * Date requires it's own class due to JavaFX now allowing you to programmatically access
     * the cell objects.
     */
    class HighlightedDateCell extends TableCell<OrganEnum, String> {

        private static final String HIGHLIGHT = "cell-highlighted";

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            // Handle null item case
            if (item == null) {
                setText("");
                getStyleClass().remove(HIGHLIGHT);
                return;
            }

            setText(item);

            TableColumn organCol = tableViewReceiving.getColumns().get(0);
            String associatedOrgan = (String) organCol.getCellObservableValue(
                    this.getTableRow().getIndex()
            ).getValue();

            if (checkList.contains(associatedOrgan)) {
                this.getStyleClass().add(HIGHLIGHT);
            } else {
                this.getStyleClass().remove(HIGHLIGHT);
            }
        }
    }
}
