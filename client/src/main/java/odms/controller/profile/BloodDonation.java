package odms.controller.profile;

import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.Profile;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Class that controls all model interactions for the blood donation view.
 */
@Slf4j
public class BloodDonation {
    private odms.view.profile.BloodDonation view;

    public BloodDonation(odms.view.profile.BloodDonation view) {
        this.view = view;
    }

    /**
     * Method that sets the base amount of points a person can earn in this donation.
     */
    public int setPoints(Profile profile) {
        int bloodTypePoints;
        switch (profile.getBloodType()) {
            case "O+":
                bloodTypePoints = 2;
                break;
            case "O-":
                bloodTypePoints = 3;
                break;
            case "A+":
                bloodTypePoints = 2;
                break;
            case "A-":
                bloodTypePoints = 4;
                break;
            case "B+":
                bloodTypePoints = 3;
                break;
            case "B-":
                bloodTypePoints = 5;
                break;
            case "AB+":
                bloodTypePoints = 4;
                break;
            case "AB-":
                bloodTypePoints = 5;
                break;
            default:
                bloodTypePoints = 0;
                break;
        }
        try {
            if (LocalDateTime.now().minusYears(1).isBefore(profile.getLastBloodDonation())) {
                bloodTypePoints += 1;
            }
        } catch (NullPointerException e) {
            log.error(e.getMessage(), e);
        }
        return bloodTypePoints;
    }

    /**
     * Method that is called when donate button is clicked to increment points on profile.
     */
    public void updatePoints(Profile profile, Boolean plasmaChecked) {
        int bloodTypePoints = setPoints(profile);
        if (plasmaChecked) {
            bloodTypePoints += 2;
        }
        LocalDateTime timestamp = LocalDateTime.now();
        profile.addBloodDonationPoints(bloodTypePoints);
        profile.setLastBloodDonation(timestamp);
        ProfileDAO server = DAOFactory.getProfileDao();
        try {
            server.update(profile);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

}
