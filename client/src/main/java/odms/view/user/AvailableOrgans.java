package odms.view.user;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import odms.controller.GuiMain;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.view.CommonView;

import java.util.Map;

public class AvailableOrgans extends CommonView {
    @FXML
    private TableView availableOrgansTable;
    private ObservableList<Map.Entry<Profile,OrganEnum>> listOfAvailableOrgans;
    private ClinicianProfile parentView;

    public void initialize(User currentUser, ClinicianProfile p) {
        populateTable();
        parentView = p;
    }

    public void populateTable() {
        availableOrgansTable.getColumns().clear();
        TableColumn<Map.Entry<Profile, OrganEnum>, String> organCol = new TableColumn<>(
                "Organ");
        organCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getValue().getName()));

        TableColumn<Map.Entry<Profile, OrganEnum>, String> dateOfDeathNameCol = new TableColumn<>(
                "Date of Death");
        dateOfDeathNameCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getKey().getDateOfDeath().toString()));

        TableColumn<Map.Entry<Profile, OrganEnum>, String> countdownCol = new TableColumn<>(
                "Countdown");
        countdownCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue().getKey().getRegion()));

        TableColumn<Map.Entry<Profile, OrganEnum>, String> donorIdCol = new TableColumn<>(
                "Donor ID");
        donorIdCol.setCellValueFactory(
                cdf -> new SimpleStringProperty((cdf.getValue().getKey().getId()).toString()));

        TableColumn<Map.Entry<Profile, OrganEnum>, String> expiryProgressBarCol = new TableColumn<>(
                "Expiry Progress Bar");
        expiryProgressBarCol.setCellValueFactory(
                cdf -> new SimpleStringProperty((cdf.getValue().getValue().getDate()).toString()));

        availableOrgansTable.getColumns().add(organCol);
        availableOrgansTable.getColumns().add(dateOfDeathNameCol);
        availableOrgansTable.getColumns().add(countdownCol);
        availableOrgansTable.getColumns().add(donorIdCol);
        availableOrgansTable.getColumns().add(expiryProgressBarCol);
        availableOrgansTable.getItems().clear();
        setList();
        availableOrgansTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    availableOrgansTable.getSelectionModel().getSelectedItem() != null) {
                createNewDonorWindow(((Map.Entry<Profile, OrganEnum>) availableOrgansTable.getSelectionModel()
                        .getSelectedItem()).getKey(), parentView);
            }
        });
    }

    public void setList() {
        listOfAvailableOrgans = FXCollections.observableArrayList();
        availableOrgansTable.setItems(listOfAvailableOrgans);
    }
}
