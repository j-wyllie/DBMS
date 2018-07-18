package odms.view.profile;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import odms.controller.profile.ProfileMedicalTabController;
import odms.model.profile.Profile;
import odms.view.CommonView;

public class ProfileMedicalViewTODO extends CommonView {

    @FXML
    private Label bloodTypeLabel;
    @FXML
    private Label smokerLabel;
    @FXML
    private Label alcoholConsumptionLabel;
    @FXML
    private Label bloodPressureLabel;
    @FXML
    private Label bmiLabel;

    public ObjectProperty<Profile> currentProfile = new SimpleObjectProperty<>();
    // init controller corresponding to this view
    private ProfileMedicalTabController controller = new ProfileMedicalTabController(this);

    private void setUpDetails() {
        if (currentProfile.get().getAlcoholConsumption() != null) {
            alcoholConsumptionLabel.setText(
                    alcoholConsumptionLabel.getText() +
                            currentProfile.get().getAlcoholConsumption()
            );
        }
        if (currentProfile.get().getBloodPressure() != null) {
            bloodPressureLabel
                    .setText(bloodPressureLabel.getText() + currentProfile.get().getBloodPressure());
        }
        if (currentProfile.get().getBloodType() != null) {
            bloodTypeLabel.setText(bloodTypeLabel.getText() + currentProfile.get().getBloodType());
        }
        if (currentProfile.get().getHeight() != 0.0 && currentProfile.get().getWeight() != 0.0) {
            bmiLabel.setText(bmiLabel.getText() + currentProfile.get().getBMI());
        }
        if (currentProfile.get().getIsSmoker() != null) {
            smokerLabel.setText(smokerLabel.getText() + currentProfile.get().getIsSmoker());
        }
    }

    //todo do we need this function?????
    public void initialize() {
        if (currentProfile != null) {
            setUpDetails();
        }
    }


}
