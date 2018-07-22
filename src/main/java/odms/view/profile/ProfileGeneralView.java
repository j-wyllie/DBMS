package odms.view.profile;

import java.time.format.DateTimeFormatter;
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

    private Profile currentProfile;
    // init controller corresponding to this view
    private ProfileGeneralTabController controller = new ProfileGeneralTabController(this);

    private void setUpDetails() {
        if (currentProfile.getGivenNames() != null) {
            givenNamesLabel.setText(currentProfile.getGivenNames());
        }
        if (currentProfile.getPreferredName() != null) {
            labelPreferredName
                    .setText(currentProfile.getPreferredName());
        }
        if (currentProfile.getLastNames() != null) {
            lastNamesLabel.setText(currentProfile.getLastNames());
        }
//        if (currentProfile.get().getIrdNumber() != null) {
//            irdLabel.setText(currentProfile.get().getIrdNumber().toString());
//        }
        if (currentProfile.getDateOfBirth() != null) {
            dobLabel.setText(currentProfile.getDateOfBirth()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        if (currentProfile.getDateOfDeath() != null) {
            dodLabel.setText(currentProfile.getDateOfDeath()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        if (currentProfile.getGender() != null) {
            genderLabel.setText(currentProfile.getGender());
        }
        if (currentProfile.getPreferredGender() != null) {
            labelGenderPreferred.setText(currentProfile.getPreferredGender());
        }
        if (currentProfile.getHeight() != 0.0) {
            heightLabel.setText(currentProfile.getHeight() + "cm");
        }
        if (currentProfile.getWeight() != 0.0) {
            weightLabel.setText(currentProfile.getWeight() + "kg");
        }
        if (currentProfile.getPhone() != null) {
            phoneLabel.setText(currentProfile.getPhone());
        }
        if (currentProfile.getEmail() != null) {
            emailLabel.setText(currentProfile.getEmail());
        }
        if (currentProfile.getAddress() != null) {
            addressLabel.setText(currentProfile.getAddress());
        }
        if (currentProfile.getRegion() != null) {
            regionLabel.setText(currentProfile.getRegion());
        }
        if (currentProfile.getDateOfBirth() != null) {
            ageLabel.setText(Integer.toString(currentProfile.getAge()));
        }
    }

    public void initialize(Profile p) {
        currentProfile = p;
        setUpDetails();
    }
}
