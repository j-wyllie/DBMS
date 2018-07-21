package odms.view.profile;

import java.time.format.DateTimeFormatter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import odms.controller.profile.ProfileGeneralTabController;
import odms.model.profile.Profile;
import odms.view.CommonView;

public class ProfileGeneralView extends CommonView {

    @FXML
    private Label phoneLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label regionLabel;
    @FXML
    private Label givenNamesLabel;
    @FXML
    private Label lastNamesLabel;
    @FXML
    private Label irdLabel;
    @FXML
    private Label dobLabel;
    @FXML
    private Label dodLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label heightLabel;
    @FXML
    private Label weightLabel;
    @FXML
    private Label labelGenderPreferred;
    @FXML
    private Label labelPreferredName;
    @FXML
    private Label ageLabel;

    public ObjectProperty<Profile> currentProfile = new SimpleObjectProperty<>();
    // init controller corresponding to this view
    private ProfileGeneralTabController controller = new ProfileGeneralTabController(this);

    public void setUpDetails() {
        if (currentProfile.get().getGivenNames() != null) {
            givenNamesLabel.setText(currentProfile.get().getGivenNames());
        }
        if (currentProfile.get().getPreferredName() != null) {
            labelPreferredName
                    .setText(currentProfile.get().getPreferredName());
        }
        if (currentProfile.get().getLastNames() != null) {
            lastNamesLabel.setText(currentProfile.get().getLastNames());
        }
        if (currentProfile.get().getIrdNumber() != null) {
            irdLabel.setText(currentProfile.get().getIrdNumber().toString());
        }
        if (currentProfile.get().getDateOfBirth() != null) {
            dobLabel.setText(currentProfile.get().getDateOfBirth()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        if (currentProfile.get().getDateOfDeath() != null) {
            dodLabel.setText(currentProfile.get().getDateOfDeath()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        if (currentProfile.get().getGender() != null) {
            genderLabel.setText(currentProfile.get().getGender());
        }
        if (currentProfile.get().getPreferredGender() != null) {
            labelGenderPreferred.setText(currentProfile.get().getPreferredGender());
        }
        if (currentProfile.get().getHeight() != 0.0) {
            heightLabel.setText(currentProfile.get().getHeight() + "cm");
        }
        if (currentProfile.get().getWeight() != 0.0) {
            weightLabel.setText(currentProfile.get().getWeight() + "kg");
        }
        if (currentProfile.get().getPhone() != null) {
            phoneLabel.setText(currentProfile.get().getPhone());
        }
        if (currentProfile.get().getEmail() != null) {
            emailLabel.setText(currentProfile.get().getEmail());
        }
        if (currentProfile.get().getAddress() != null) {
            addressLabel.setText(currentProfile.get().getAddress());
        }
        if (currentProfile.get().getRegion() != null) {
            regionLabel.setText(currentProfile.get().getRegion());
        }
        if (currentProfile.get().getDateOfBirth() != null) {
            ageLabel.setText(Integer.toString(currentProfile.get().getAge()));
        }
    }

    public void initialize() {
    }
}
