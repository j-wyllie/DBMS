package odms.controller.profile;

import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.profile.Profile;
import odms.data.DefaultLocale;

public class ProfileGeneral {

    odms.view.profile.ProfileGeneral view;

    @FXML
    private ImageView profileImage;

    public ProfileGeneral(odms.view.profile.ProfileGeneral v) {
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
            view.setDobLabel(DefaultLocale.format(currentProfile.getDateOfBirth()));
        }
        if (currentProfile.getDateOfDeath() != null) {
            view.setDodLabel(DefaultLocale.format(currentProfile.getDateOfDeath()));
        }
        if (currentProfile.getGender() != null) {
            view.setGenderLabel(currentProfile.getGender());
        }
        if (currentProfile.getPreferredGender() != null && !currentProfile.getPreferredGender().equals("")) {
            view.setLabelGenderPreferred(currentProfile.getPreferredGender());
        } else {
            view.setLabelGenderPreferred(currentProfile.getGender());
        }
        if (currentProfile.getHeight() != 0.0) {
            view.setHeightLabel(DefaultLocale.format(currentProfile.getHeight()) + "cm");
        }
        if (currentProfile.getWeight() != 0.0) {
            view.setWeightLabel(DefaultLocale.format(currentProfile.getWeight()) + "kg");
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

        // profile is deceased
        if (currentProfile.getDateOfDeath() != null) {

            if (currentProfile.getCountryOfDeath() == null ) {
                if (currentProfile.getCountry() != null) {
                    currentProfile.setCountryOfDeath(currentProfile.getCountry());
                    view.getCountryLabel().setText("Country of Death : " + CountriesEnum.getValidNameFromString(currentProfile.getCountry().getName()));
                } else {
                    view.getCountryLabel().setText("Country of Death : ");
                }
            } else {
                view.getCountryLabel().setText("Country of Death : " + CountriesEnum.getValidNameFromString(currentProfile.getCountryOfDeath().getName()));
            }

            if (currentProfile.getCityOfDeath() == null) {
                if (currentProfile.getCity() != null) {
                    currentProfile.setCityOfDeath(currentProfile.getCity());
                    view.getCityLabel().setText("City of Death : " + currentProfile.getCityOfDeath());
                }
            } else {
                view.getCityLabel().setText("City of Death : " + currentProfile.getCityOfDeath());
            }

            if (currentProfile.getRegionOfDeath() == null) {
                if (currentProfile.getRegion() != null) {
                    currentProfile.setRegionOfDeath(currentProfile.getRegion());
                    view.getRegionLabel().setText("Region of Death : " + currentProfile.getRegionOfDeath());
                }
            } else {
                view.getRegionLabel().setText("Region of Death : " + currentProfile.getRegionOfDeath());
            }

        } else {
            //profile is alive

            if (currentProfile.getRegion() != null) {
                view.getRegionLabel().setText("Region : " + currentProfile.getRegion());
            }
            if (currentProfile.getCountry() != null) {
                view.getCountryLabel().setText("Country : " + CountriesEnum.getValidNameFromString(currentProfile.getCountry().getName()));
            }
            if (currentProfile.getCity() != null) {
                view.getCityLabel().setText("City : " + currentProfile.getCity());
            }
        }
    }

    public void setCurrentProfile(Profile currentProfile) {
        this.currentProfile = currentProfile;
    }
}
