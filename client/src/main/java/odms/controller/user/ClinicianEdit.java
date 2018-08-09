package odms.controller.user;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javafx.fxml.FXML;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.database.DAOFactory;
import odms.controller.history.CurrentHistory;
import odms.model.user.User;

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
        odms.model.history.History action = new odms.model.history.History("Clinician", currentUser.getStaffID(),
                "updated", "previous " + currentUser.getAttributesSummary() +
                " new " + currentUser.getAttributesSummary(), -1, LocalDateTime.now());
        currentUser.setName(view.getGivenNamesField());
        currentUser.setStaffID(Integer.valueOf(view.getStaffIdField()));
        currentUser.setWorkAddress(view.getAddressField());
        currentUser.setRegion(view.getRegionField());
        CurrentHistory.updateHistory(action);
        try {
            DAOFactory.getUserDao().update(currentUser);
        } catch (SQLException e) {
            AlertController.invalidEntry("Database could not update the user.");
        }
    }
}
