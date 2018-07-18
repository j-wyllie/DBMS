package odms.view.profile;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import odms.controller.profile.ProfileHistoryTabController;
import odms.model.profile.Profile;


public class ProfileHistoryViewTODO {
    @FXML
    private TextArea historyView;

    public ObjectProperty<Profile> currentProfile = new SimpleObjectProperty<>();
    // init controller corresponding to this view
    private ProfileHistoryTabController controller = new ProfileHistoryTabController(this);

    public Profile getProfile() {
        return currentProfile.get();
    }

    private void setHistoryView() {
        historyView.setText(controller.getHistory());
    }

    //todo do we need this function?????
    public void initialize() {
        if (currentProfile != null) {
            setHistoryView();
        }
    }
}
