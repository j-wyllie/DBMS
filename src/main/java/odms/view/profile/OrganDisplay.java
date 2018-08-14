package odms.view.profile;

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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.controller.database.DAOFactory;
import odms.model.enums.OrganEnum;
import odms.model.enums.OrganSelectEnum;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.view.CommonView;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class OrganDisplay extends CommonView {
    private Profile currentProfile;

    private ObservableList<String> checkList = FXCollections.observableArrayList();

    private ObservableList<String> observableListDonated = FXCollections.observableArrayList();
    private ObservableList<String> observableListDonating = FXCollections.observableArrayList();
    private ObservableList<String> observableListReceiving = FXCollections.observableArrayList();

    @FXML
    private ListView<String> listViewDonated;

    @FXML
    private ListView<String> listViewDonating;

    @FXML
    private ListView<String> listViewReceiving;
    @FXML
    private Label donatingLabel;

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




    private static OrganSelectEnum windowType;
    private User currentUser;

    public void initialize(Profile p, Boolean isClinician, User user) {
        currentProfile = p;
        currentUser = user;
        listViewDonating.setCellFactory(param -> new OrganDisplay.HighlightedCell());
        listViewReceiving.setCellFactory(param -> new OrganDisplay.HighlightedCell());

        listViewDonated.setItems(observableListDonated);
        listViewDonating.setItems(observableListDonating);
        listViewReceiving.setItems(observableListReceiving);
        try {
        if(!currentProfile.getDateOfDeath().equals(null)) {
            donatingButton.setDisable(true);
            receivingButton.setDisable(true);
        }}catch (NullPointerException e) {
            donatingButton.setDisable(false);
            receivingButton.setDisable(false);
        }

        if (!isClinician) {
            if (DAOFactory.getOrganDao().getDonating(currentProfile).isEmpty()) {
                visibilityLists(listViewDonating, donatingLabel, donatingButton, 0, false);
            } else {
                visibilityLists(listViewDonating, donatingLabel, donatingButton, 0, true);
            }
            if (DAOFactory.getOrganDao().getRequired(currentProfile).isEmpty()) {
                visibilityLists(listViewReceiving, receivingLabel, receivingButton, 1, false);
            } else {
                visibilityLists(listViewReceiving, receivingLabel, receivingButton, 1, true);
            }

            RowConstraints zeroHeight = new RowConstraints();
            zeroHeight.setPrefHeight(0);
            organGridPane.getRowConstraints().set(2, zeroHeight);
            expiredButton.setVisible(false);
        } else {
            if (currentProfile.getDateOfDeath() == null) {
                RowConstraints zeroHeight = new RowConstraints();
                zeroHeight.setPrefHeight(0);
                organGridPane.getRowConstraints().set(2, zeroHeight);
                expiredButton.setVisible(false);
            } else {
                listViewDonating.setMouseTransparent(false);
            }
        }

        populateOrganLists();

        listViewDonating.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    listViewDonating.getSelectionModel().getSelectedItem() != null) {
                giveReasonForOverride(event, listViewDonating.getSelectionModel().getSelectedItem());
            }
        });
    }

    @FXML
    private void handleBtnDonatingClicked(ActionEvent event) throws IOException {
        showOrgansSelectionWindow(event, OrganSelectEnum.DONATING);
    }

    @FXML
    private void handleBtnDonatedClicked(ActionEvent event) throws IOException {
        showOrgansSelectionWindow(event, OrganSelectEnum.DONATED);
    }

    @FXML
    private void handleBtnRequiredClicked(ActionEvent event) throws IOException {
        showOrgansSelectionWindow(event, OrganSelectEnum.REQUIRED);
    }

    @FXML
    private void handleExpiredClicked(ActionEvent event) throws IOException {
        showExpiredOrgans(event);
    }

    /**
     * Repopulate the ObservableLists with any Organ changes and repopulate the check list for
     * conflicting organs.
     * Populates the checklist with donating organs for highlighting.
     */
    private void populateOrganLists() {
        populateOrganList(observableListDonated, currentProfile.getOrgansDonated());
        populateOrganList(observableListDonating, currentProfile.getOrgansDonating());
        populateOrganList(observableListReceiving, currentProfile.getOrgansRequired());

        checkList.clear();

        for (String organ : observableListDonating) {
            if (observableListReceiving.contains(organ)) {
                checkList.add(organ);
            }
        }
    }

    /**
     * Removes a specific list from view.
     *
     * @param list List to set invisible
     * @param label Label to set invisible.
     * @param button Button to set invisible.
     */

    private void visibilityLists(ListView<String> list, Label label, Button button, Integer column, Boolean bool){
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
         * Refresh the ListViews to reflect changes made from the edit pane.
         */
    private void refreshListViews() {
        populateOrganLists();

        listViewDonated.refresh();
        listViewDonating.refresh();
        listViewReceiving.refresh();
    }

    /**
     * Display the Organ Edit view.
     *
     * @param event      the base action event
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
//        stage.setOnCloseRequest(ob -> {
        stage.setOnHiding((ob) -> {
            populateOrganLists();
            refreshListViews();
        });
        stage.show();
    }

    /**
     * Display the Organ Expired view.
     *
     * @param event      the base action event
     * @throws IOException if the fxml cannot load
     */
    private void showExpiredOrgans(ActionEvent event)
            throws IOException {
        Node source = (Node) event.getSource();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ProfileExpiredOrgans.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        OrganExpired view = fxmlLoader.getController();
        view.initialize(currentProfile);

        Stage stage = new Stage();
        stage.setTitle("Expired Organs");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(source.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.centerOnScreen();
//        stage.setOnCloseRequest(ob -> {
        stage.setOnHiding((ob) -> {
            populateOrganLists();
            refreshListViews();
        });
        stage.show();
    }

    private static void setWindowType(OrganSelectEnum type) {
        windowType = type;
    }

    /**
     * Override the Cell Formatting for colour highlighting.
     */
    class HighlightedCell extends ListCell<String> {

        private final String highlight = "cell-highlighted";

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            // Handle null item case
            if (item == null) {
                setText("");
                getStyleClass().remove(highlight);
                return;
            }

            setText(item);

            if (checkList.contains(item)) {
                getStyleClass().add(highlight);

            } else {
                getStyleClass().remove(highlight);
            }
        }
    }

    /**
     * Support function to populate an observable list with organs from an organ set.
     *
     * @param destinationList list to populate
     * @param organs          source list of organs to populate from
     */
    private void populateOrganList(ObservableList<String> destinationList,
            Set<OrganEnum> organs) {
        destinationList.clear();

        if (organs != null) {
            for (OrganEnum organ : organs) {
                destinationList.add(organ.getNamePlain());
            }
            Collections.sort(destinationList);
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
            e.printStackTrace();
        }
    }
}
