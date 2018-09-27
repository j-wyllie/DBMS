package odms.view.user;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.controller.database.DAOFactory;
import odms.view.CommonView;

/**
 * View for the transplant waiting list. Contains all GUI element accessors for the transplant
 * waiting view scene.
 */
@Slf4j
public class TransplantWaitingList extends CommonView {

    private User currentUser;
    private List<Entry<Profile, OrganEnum>> receiverList;
    private odms.controller.user.TransplantWaitingList controller = new
            odms.controller.user.TransplantWaitingList();

    @FXML
    private TableView<Entry<Profile, OrganEnum>> transplantTable;

    @FXML
    private TextField transplantSearchField;
    private ClinicianProfile parentView;

    /**
     * Initializes and refreshes the search table Adds a listener to each row so that when it is
     * double clicked a new donor window is opened. Calls the setTooltipToRow function.
     * @param receivers list of profile, organ pairs
     */
    @FXML
    private void makeTransplantWaitingList(List<Entry<Profile, OrganEnum>> receivers) {
        transplantTable.getColumns().clear();

        transplantTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ObservableList<Entry<Profile, OrganEnum>> receiverObservableList = FXCollections
                .observableList(receivers);

        TableColumn<Entry<Profile, OrganEnum>, String> transplantOrganRequiredCol = new TableColumn<>(
                "Organs Required");

        transplantOrganRequiredCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getValue().getNamePlain()));

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
                cdf -> new SimpleStringProperty(
                        (cdf.getValue().getKey().getOrganDate(cdf.getValue().getValue().getName()))
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

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
                        (transplantTable.getSelectionModel()
                                .getSelectedItem()).getKey(), parentView, currentUser);
            }
        });
    }

    /**
     * Refresh the search and transplant medication tables with the most up to date data.
     */
    @FXML
    public void refreshTable() {
        try {
            makeTransplantWaitingList(DAOFactory.getProfileDao().getAllReceiving());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Set the currentUser and parentView variables. Populates the waiting list table.
     * @param currentUser current user logged in
     * @param parentView The clinicianDisplay view object
     */
    public void initialize(User currentUser, ClinicianProfile parentView) {
        this.parentView = parentView;
        this.currentUser = currentUser;
        try {
            receiverList = controller.getWaitingList();
            makeTransplantWaitingList(receiverList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.5));
        transplantSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished(ae -> handleSearchWaitingList());
            pauseTransition.playFromStart();
        });
    }

    /**
     * Searches the transplant waiting list based on the search string entered in the search field.
     */
    private void handleSearchWaitingList() {
        makeTransplantWaitingList(controller.searchWaitingList(receiverList,
                transplantSearchField.getText()));
    }
}
