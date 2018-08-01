package odms.controller.profile;

import java.io.File;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import odms.model.profile.Profile;
import odms.view.profile.ProfileGeneralView;

public class ProfileGeneralTabController {

    ProfileGeneralView view;

    @FXML
    private ImageView profileImage;


    public ProfileGeneralTabController(ProfileGeneralView v) {
        view = v;
    }

    private Profile currentProfile;

    public void setLabels() {
        if (currentProfile.getGivenNames() != null) {
            view.setGivenNamesLabel(currentProfile.getGivenNames());
        }
        if (currentProfile.getPreferredName() != null) {
            view.setLabelPreferredName(currentProfile.getPreferredName());
        }
        if (currentProfile.getLastNames() != null) {
            view.setLastNamesLabel(currentProfile.getLastNames());
        }
        if (currentProfile.getNhi() != null) {
            view.setNhiLabel(currentProfile.getNhi());
        }
        if (currentProfile.getDateOfBirth() != null) {
            view.setDobLabel(currentProfile.getDateOfBirth()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        if (currentProfile.getDateOfDeath() != null) {
            view.setDodLabel(currentProfile.getDateOfDeath()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        if (currentProfile.getGender() != null) {
            view.setGenderLabel(currentProfile.getGender());
        }
        if (currentProfile.getPreferredGender() != null) {
            view.setLabelGenderPreferred(currentProfile.getPreferredGender());
        }
        if (currentProfile.getHeight() != 0.0) {
            view.setHeightLabel(currentProfile.getHeight() + "cm");
        }
        if (currentProfile.getWeight() != 0.0) {
            view.setWeightLabel(currentProfile.getWeight() + "kg");
        }
        if (currentProfile.getPhone() != null) {
            view.setPhoneLabel(currentProfile.getPhone());
        }
        if (currentProfile.getEmail() != null) {
            view.setEmailLabel(currentProfile.getEmail());
        }
        if (currentProfile.getAddress() != null) {
            view.setAddressLabel(currentProfile.getAddress());
        }
        if (currentProfile.getRegion() != null) {
            view.setRegionLabel(currentProfile.getRegion());
        }
        if (currentProfile.getDateOfBirth() != null) {
            view.setAgeLabel(Integer.toString(currentProfile.getAge()));
        }

        try {
            view.setProfileImage();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentProfile(Profile currentProfile) {
        this.currentProfile = currentProfile;
    }
}
