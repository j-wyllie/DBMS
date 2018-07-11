package odms.view.profile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import odms.model.profile.Profile;
import odms.view.CommonView;

public class ProfileMedicationHistoryView extends CommonView{
    //todo
    private Profile profile;

    private ObservableList<String> observableListMedHistory;

    @FXML
    private ListView<String> viewMedicationHistory;

    @FXML
    private Button buttonClose;

    /**
     * Populate the ListView in the init method
     */
    @FXML
    public void initialize() {

        observableListMedHistory = FXCollections.observableArrayList();
        if (profile != null) {
            observableListMedHistory.addAll(profile.getMedicationTimestamps());
            viewMedicationHistory.setItems(observableListMedHistory);
        } else {
            viewMedicationHistory.setPlaceholder(new Label("There is no medication history"));
        }
    }


    /**
     * Cancel the current changes in the view and close the window.
     *
     * @param event close button has been clicked
     */
    @FXML
    private void handleCloseButtonClicked(ActionEvent event) {
        Stage stage = (Stage) buttonClose.getScene().getWindow();
        stage.close();
    }


    /**
     * Configure the currently selected profile
     *
     * @param profile the profile to operate against.
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

}
