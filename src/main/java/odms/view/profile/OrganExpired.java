package odms.view.profile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.model.enums.OrganEnum;
import odms.model.enums.OrganSelectEnum;
import odms.model.profile.ExpiredOrgan;
import odms.model.profile.Profile;
import odms.model.user.User;


public class OrganExpired extends OrganCommon{

    protected ObservableList<ExpiredOrgan> observableExpiredOrganList = FXCollections.observableArrayList();
    private Profile currentProfile;
    private User currentUser;
    List<ExpiredOrgan> organs = new ArrayList<>();
    private odms.controller.profile.OrganExpired controller = new odms.controller.profile.OrganExpired(this);

    private OrganSelectEnum windowType;


    @FXML
    private Button btnCancel;
    @FXML
    private TableView<ExpiredOrgan> expiredOrganTable;
    @FXML
    private TableColumn<ExpiredOrgan, String> expiredOrganColumn;
    @FXML
    private TableColumn<ExpiredOrgan, String> expiredClinicianColumn;
    @FXML
    private TableColumn<ExpiredOrgan, LocalDateTime> expiredTimeColumn;
    @FXML
    private TableColumn<ExpiredOrgan, String> expiredNoteColumn;
    @FXML
    private Button btnRevert;


    /**
     * Initialize the current view instance and populate organ lists.
     *
     * @param profile the profile to set on view instance
     */
    public void initialize(Profile profile, User user) {
        currentProfile = profile;
        currentUser = user;



        organs = controller.getExpiredOrgans(profile);
        observableExpiredOrganList.addAll(organs);

        expiredOrganTable.setItems(observableExpiredOrganList);
        expiredOrganColumn.setCellValueFactory(new PropertyValueFactory<>("Organ"));
        expiredClinicianColumn.setCellValueFactory(new PropertyValueFactory<>("ClinicianName"));
        expiredNoteColumn.setCellValueFactory(new PropertyValueFactory<>("Note"));
        expiredTimeColumn.setCellValueFactory(new PropertyValueFactory<>("ExpiryDate"));
        expiredOrganTable.getColumns().setAll(expiredOrganColumn, expiredClinicianColumn, expiredTimeColumn, expiredNoteColumn);

        expiredOrganTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    expiredOrganTable.getSelectionModel().getSelectedItem() != null) {
                editOrganNote(event, expiredOrganTable.getSelectionModel().getSelectedItem());
            }
        });

    }

    /**
     * Cancel the current changes in the view and close the window.
     */
    @FXML
    public void onBtnCancelClicked() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * Revert the organ to be non-expired
     */
    @FXML
    public void onBtnRevertClicked() {
        String organ = expiredOrganTable.getSelectionModel().getSelectedItem().getOrgan();
        Integer profileId = currentProfile.getId();
        controller.revertExpired(profileId, organ);
        observableExpiredOrganList.remove(expiredOrganTable.getSelectionModel().getSelectedItem());

    }

    /**
     * Launch pane to edit reasoning for organ expiry override.
     *
     * @param event the JavaFX event
     * @param organ the organ to specify reason for
     */
    private void editOrganNote(Event event, ExpiredOrgan organ) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ProfileOrganOverride.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());
            OrganOverride overrideView = fxmlLoader.getController();
            overrideView.initialize(organ.getOrgan(), currentProfile, currentUser);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Manual Organ Override");
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.centerOnScreen();
            stage.setOnHiding(ob -> refreshTableView());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refresh the ListViews to reflect changes made from the edit pane.
     */
    private void refreshTableView() {
        organs = controller.getExpiredOrgans(currentProfile);
        observableExpiredOrganList.clear();
        observableExpiredOrganList.addAll(organs);
        expiredOrganTable.refresh();
    }

    public void setWindowType(OrganSelectEnum windowType) {
        this.windowType = windowType;
    }

}
