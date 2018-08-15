package odms.controller.user;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javafx.fxml.FXML;
import odms.commons.model.history.History;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.data.ImageDataIO;
import odms.controller.database.DAOFactory;
import odms.controller.history.CurrentHistory;
import odms.commons.model.user.User;

public class ClinicianEdit extends CommonController {

    private odms.view.user.ClinicianEdit view;

    public ClinicianEdit(odms.view.user.ClinicianEdit view) {
        this.view = view;
    }

    /**
     * Button handler to save the changes made to the fields.
     *
     */
    @FXML
    public void save() throws IOException {
        User currentUser = view.getUser();
        History action = new History("Clinician", currentUser.getStaffID(),
                "updated", "previous " + currentUser.getAttributesSummary() +
                " new " + currentUser.getAttributesSummary(), -1, LocalDateTime.now());
        currentUser.setName(view.getGivenNamesField());
        currentUser.setStaffID(Integer.valueOf(view.getStaffIdField()));
        currentUser.setWorkAddress(view.getAddressField());
        currentUser.setRegion(view.getRegionField());

        if (view.getChosenFile() != null) {
            currentUser.setPictureName(
                    ImageDataIO.deleteAndSaveImage(
                            view.getChosenFile(),
                            currentUser.getStaffID().toString()
                    )
            );
        } else if (view.getRemovePhoto()) {
            ImageDataIO.deleteImage(currentUser.getStaffID().toString());
            currentUser.setPictureName(null);
        }

        CurrentHistory.updateHistory(action);

        try {
            DAOFactory.getUserDao().update(currentUser);
        } catch (SQLException e) {
            AlertController.invalidEntry("user could not be updated on database.");
        }
    }
}
