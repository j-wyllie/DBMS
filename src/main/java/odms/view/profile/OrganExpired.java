package odms.view.profile;

import java.io.IOException;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.model.enums.OrganEnum;
import odms.model.enums.OrganSelectEnum;
import odms.model.profile.Profile;


public class OrganExpired extends OrganCommon{

    protected ObservableList<String> observableExpiredOrganList = FXCollections.observableArrayList();
    private Profile currentProfile;
    Set<OrganEnum> organs = new HashSet<>();
    private odms.controller.profile.OrganExpired controller = new odms.controller.profile.OrganExpired(this);

    private OrganSelectEnum windowType;

    @FXML
    private ListView<String> expiredOrganName;
    @FXML
    private ListView<String> expiredAvailability;
    @FXML
    private ListView<String> expiredNotes;
    @FXML
    private Button btnCancel;

    /**
     * Initialize the current view instance and populate organ lists.
     *
     * @param profile the profile to set on view instance
     */
    public void initialize(Profile profile) {
        currentProfile = profile;


        if (currentProfile != null) {

            organs = currentProfile.getOrgansDonated();
            populateOrganList(observableExpiredOrganList, organs);
            //expiredOrganName.setItems(observableExpiredOrganList);
        }

    }

    /**
     * Cancel the current changes in the view and close the window.
     */
    @FXML
    public void onBtnCancelClicked() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void setWindowType(OrganSelectEnum windowType) {
        this.windowType = windowType;
    }

}
