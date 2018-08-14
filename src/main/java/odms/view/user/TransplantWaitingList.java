package odms.view.user;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import odms.controller.GuiMain;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.view.CommonView;
import org.controlsfx.control.table.TableFilter;

public class TransplantWaitingList extends CommonView {

    private User currentUser;
    private ObservableList<Entry<Profile, OrganEnum>> receiverObservableList;

    @FXML
    private TableView transplantTable;

    @FXML
    private TextField transplantSearchField;
    private ClinicianProfile parentView;

    /**
     * Initializes and refreshes the search table Adds a listener to each row so that when it is
     * double clicked a new donor window is opened. Calls the setTooltipToRow function.
     */
    @FXML
    private void makeTransplantWaitingList(List<Entry<Profile, OrganEnum>> receivers) {
        transplantTable.getColumns().clear();

        receiverObservableList = FXCollections.observableList(receivers);
        //transplantTable.setItems(receiverObservableList);
        //transplantOrganRequiredCol.setCellValueFactory(new PropertyValueFactory<>("organ"));
        //transplantOrganDateCol.setCellFactory(new PropertyValueFactory<>("date"));
        //transplantReceiverNameCol.setCellValueFactory(new PropertyValueFactory("fullName"));
        //transplantRegionCol.setCellValueFactory(new PropertyValueFactory("region"));

        TableColumn<Entry<Profile, OrganEnum>, String> transplantOrganRequiredCol = new TableColumn<>(
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
                                .getSelectedItem()).getKey(), parentView, currentUser);
            }
        });
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

    public void initialize(User currentUser, ClinicianProfile parentView) {
        this.parentView = parentView;
        this.currentUser = currentUser;
        try {
            makeTransplantWaitingList(GuiMain.getCurrentDatabase().getAllOrgansRequired());
            TableFilter filter = new TableFilter<>(transplantTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleSearchWaitingList(KeyEvent keyEvent) {
        //todo copy working method from other branch.
    }
}
