package odms.controller.user;

import java.io.IOException;
import java.time.LocalDateTime;
import javafx.fxml.FXML;
import odms.controller.CommonController;
import odms.controller.history.HistoryController;
import odms.model.history.History;
import odms.model.user.User;
import odms.view.user.ClinicianProfileEditView;

public class ClinicianProfileEditController extends CommonController {

    private ClinicianProfileEditView view;

    public ClinicianProfileEditController(ClinicianProfileEditView view) {
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
        HistoryController.updateHistory(action);
    }
}
