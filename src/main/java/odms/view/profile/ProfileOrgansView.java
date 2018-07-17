package odms.view.profile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.controller.profile.ProfileOrganEditController;
import odms.model.enums.OrganEnum;
import odms.model.enums.OrganSelectEnum;
import odms.model.profile.Profile;
import odms.view.CommonView;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class ProfileOrgansView extends CommonView {
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

    private static OrganSelectEnum windowType;

    public void initialize(Profile p) {
        currentProfile = p;
        listViewDonating.setCellFactory(param -> new ProfileOrgansView.HighlightedCell());
        listViewReceiving.setCellFactory(param -> new ProfileOrgansView.HighlightedCell());

        listViewDonated.setItems(observableListDonated);
        listViewDonating.setItems(observableListDonating);
        listViewReceiving.setItems(observableListReceiving);
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
     * Repopulate the ObservableLists with any Organ changes and repopulate the check list for
     * conflicting organs.
     * <p>
     * Populates the checklist with donating organs for highlighting.
     */
    public void populateOrganLists() {
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
        //todo replace with view
        ProfileOrganEditController controller = fxmlLoader.getController();
        controller.setWindowType(windowType);
        //controller.initialize(); don't think we need this

        Stage stage = new Stage();
        stage.setTitle(selectType.toString());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(source.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.centerOnScreen();
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
    protected void populateOrganList(ObservableList<String> destinationList,
            Set<OrganEnum> organs) {
        destinationList.clear();

        if (organs != null) {
            for (OrganEnum organ : organs) {
                destinationList.add(organ.getNamePlain());
            }
            Collections.sort(destinationList);
        }
    }

}
