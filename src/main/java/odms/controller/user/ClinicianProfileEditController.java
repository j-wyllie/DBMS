package odms.controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.controller.CommonController;
import odms.controller.data.UserDataIO;
import odms.controller.history.HistoryController;
import odms.model.history.History;
import odms.model.user.User;
import odms.view.user.ClinicianProfileEditView;

import java.io.IOException;
import java.time.LocalDateTime;

import static odms.controller.AlertController.*;
import static odms.controller.GuiMain.getUserDatabase;

public class ClinicianProfileEditController extends CommonController {

    private ClinicianProfileEditView view;

    public ClinicianProfileEditController(ClinicianProfileEditView v){
        view = v;
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
