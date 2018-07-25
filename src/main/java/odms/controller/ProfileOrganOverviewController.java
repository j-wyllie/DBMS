package odms.controller;

import static odms.controller.ProfileOrganEditController.setWindowType;

import java.io.IOException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.enums.OrganEnum;
import odms.enums.OrganSelectEnum;

public class ProfileOrganOverviewController extends ProfileOrganCommonController {
    private ObservableList<String> checkList = FXCollections.observableArrayList();

    private ObservableList<String> observableListDonated = FXCollections.observableArrayList();
    private ObservableList<String> observableListDonating = FXCollections.observableArrayList();
    private ObservableList<OrganEnum> observableListReceiving = FXCollections.observableArrayList();

    @FXML
    private ListView<String> listViewDonated;

    @FXML
    private ListView<String> listViewDonating;

    @FXML
    private TableView tableViewReceiving;

    @FXML
    private TableColumn tableColumnOrgan;

    @FXML
    private TableColumn tableColumnDate;

    /**
     * Override the Cell Formatting for colour highlighting.
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

    @FXML
    public void initialize() {
        listViewDonating.setCellFactory(param -> new HighlightedCell());
        listViewDonated.setItems(observableListDonated);
        listViewDonating.setItems(observableListDonating);

        tableViewReceiving.setItems(observableListReceiving);
        tableColumnOrgan.setCellValueFactory(new PropertyValueFactory("name"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<OrganEnum, LocalDate>("date"));
        tableViewReceiving.getColumns().setAll(tableColumnOrgan, tableColumnDate);
        tableViewReceiving.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

    /**
     * Repopulate the ObservableLists with any Organ changes and repopulate the
     * check list for conflicting organs.
     *
     * Populates the checklist with donating organs for highlighting.
     */
    public void populateOrganLists() {
        populateOrganList(observableListDonated, currentProfile.get().getOrgansDonated());
        populateOrganList(observableListDonating, currentProfile.get().getOrgansDonating());
        populateOrganReceivingList(observableListReceiving, currentProfile.get().getOrgansRequired());
        checkList.clear();

        for (String organ : observableListDonating) {
            if (observableListReceiving.contains(organ)) {
                checkList.add(organ);
            }
        }
    }

    /**
     * Refresh the ListViews to reflect changes made from the edit pane.
     */
    private void refreshListViews() {
        populateOrganLists();

        listViewDonated.refresh();
        listViewDonating.refresh();
        tableViewReceiving.refresh();
    }

    /**
     * Display the Organ Edit view.
     *
     * @param event the base action event
     * @param selectType the organ list type being changed
     * @throws IOException if the fxml cannot load
     */
    private void showOrgansSelectionWindow(ActionEvent event, OrganSelectEnum selectType) throws IOException {
        Node source = (Node) event.getSource();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ProfileOrganEdit.fxml"));
        setWindowType(selectType);

        Scene scene = new Scene(fxmlLoader.load());
        ProfileOrganEditController controller = fxmlLoader.getController();
        controller.setCurrentProfile(currentProfile.get());
        controller.initialize();

        Stage stage = new Stage();
        stage.setTitle(selectType.toString());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(source.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.centerOnScreen();
        stage.setOnCloseRequest(ob -> {
            populateOrganLists();
            refreshListViews();
        });
        stage.show();
    }
}
