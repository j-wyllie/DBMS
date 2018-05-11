package odms.controller;

import static odms.controller.ProfileOrganEditController.setWindowType;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.enums.OrganSelectEnum;

public class ProfileOrganOverviewController extends ProfileOrganCommonController {
    private ObservableList<String> observableListDonated = FXCollections.observableArrayList();
    private ObservableList<String> observableListDonating = FXCollections.observableArrayList();
    private ObservableList<String> observableListReceived = FXCollections.observableArrayList();

    @FXML
    private ListView<String> tableDonated;

    @FXML
    private ListView<String> tableDonating;

    @FXML
    private ListView<String> tableReceiving;

    public void initialize() {
        currentProfile = LoginController.getCurrentProfile();

        populateOrganLists();

        tableDonated.setItems(observableListDonated);
        tableDonating.setItems(observableListDonating);
        tableReceiving.setItems(observableListReceived);
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

    private void populateOrganLists() {
        populateOrganList(observableListDonated, currentProfile.getOrgansDonated());
        populateOrganList(observableListDonating, currentProfile.getOrgansDonating());
        populateOrganList(observableListReceived, currentProfile.getOrgansRequired());
    }

    /**
     * Refresh the ListViews to reflect changes made from the edit pane.
     */
    private void refreshListViews() {
        tableDonated.refresh();
        tableDonating.refresh();
        tableReceiving.refresh();
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
        controller.setCurrentProfile(currentProfile);
        controller.initialize();

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

}
