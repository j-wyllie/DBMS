package odms.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.controller.CommonController;
import odms.controller.data.AddressIO;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;
import org.controlsfx.control.PopOver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Organ map controller.
 */
@Slf4j
public class OrganMap extends CommonController {

    private static final double VBOX_PADDING = 10.0;

    private odms.view.user.OrganMap view;

    public void setView(odms.view.user.OrganMap v) {
        view = v;
    }

    /**
     * Gets all of the dead donors.
     *
     * @return Observable list of dead donors.
     */
    public ObservableList<Profile> getDeadDonors() {
        ArrayList<Profile> deadDonors = new ArrayList<>();
        ProfileDAO database = DAOFactory.getProfileDao();
        List<Profile> allDonors = null;
        try {
            allDonors = database.getDead();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        if (allDonors != null) {
            for (Profile profile : allDonors) {
                // If the profile has organs to donate
                if (!profile.getOrgansDonatingNotExpired().isEmpty()) {
                    deadDonors.add(profile);
                }
            }
        }
        return FXCollections.observableArrayList(deadDonors);
    }

    /**
     * Gets all of the dead donors according to a given search string.
     *
     * @param searchString The string to search by
     * @return Observable list of dead donors.
     */
    public ObservableList<Profile> getDeadDonorsFiltered(String searchString) {
        ArrayList<Profile> deadDonors = new ArrayList<>();
        ProfileDAO database = DAOFactory.getProfileDao();
        List<Profile> allDonors = null;
        try {
            allDonors = database.getDeadFiltered(searchString);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        if (allDonors != null) {
            for (Profile profile : allDonors) {
                // If the profile has organs to donate
                if (!profile.getOrgansDonatingNotExpired().isEmpty()) {
                    deadDonors.add(profile);
                }
            }
        }
        return FXCollections.observableArrayList(deadDonors);
    }

    /**
     * Gets all of the receivers for a particular donor.
     *
     * @param donatingProfile The donor that was selected.
     * @return Observable list of receivers.
     */
    public ObservableList<Profile> getReceivers(Profile donatingProfile) {
        List<Profile> receivingProfiles = new ArrayList<>();

        Set<OrganEnum> donatingOrgans = donatingProfile.getOrgansDonatingNotExpired();

        receivingProfiles.addAll(AvailableOrgans
                .getSuitableRecipients(donatingOrgans, donatingProfile));

        return FXCollections.observableArrayList(receivingProfiles);
    }

    /**
     * Creates a new pop over containing profile info and a match and open profile button.
     *
     * @param profile Profile marker clicked on.
     * @return a pop over.
     */
    public PopOver createNewPopOver(Profile profile) {

        VBox vbox = createVbox(profile);

        PopOver popOver = new PopOver(vbox);
        popOver.animatedProperty().setValue(true);

        return popOver;
    }

    /**
     * Creates a vbox containing the PopOver content.
     *
     * @param profile Profile marker clicked on.
     * @return VBox.
     */
    private VBox createVbox(Profile profile) {
        Label lblName = new Label(profile.getFullPreferredName());
        VBox vBox = new VBox(lblName, view.getOpenProfileBtn(), view.getMatchBtn());
        vBox.setSpacing(VBOX_PADDING);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(VBOX_PADDING));

        return vBox;
    }

    /**
     * Gets a profiles lat long for the region and country.
     *
     * @param profile Profile being displayed.
     * @return A list containing the lat and long.
     */
    public List<Double> getProfileLatLong(Profile profile) {
        return AddressIO.getLongLatRegion(profile.getRegion(), profile.getCountry());
    }
}
