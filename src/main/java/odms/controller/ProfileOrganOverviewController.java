package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

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
        profile = LoginController.getCurrentProfile();

        populateOrganList(observableListDonated, profile.getOrgansDonated());
        populateOrganList(observableListDonating, profile.getOrgansDonating());
        populateOrganList(observableListReceived, profile.getOrgansRequired());

        tableDonated.setItems(observableListDonated);
        tableDonating.setItems(observableListDonating);
        tableReceiving.setItems(observableListReceived);

    }

}
