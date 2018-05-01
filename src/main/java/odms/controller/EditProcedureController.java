package odms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import odms.profile.Procedure;
import odms.profile.Profile;

import static odms.controller.LoginController.getCurrentProfile;

public class EditProcedureController {
    protected Procedure currentProcedure;
    public void setProcedure(Procedure procedure) {
        currentProcedure = procedure;

    }

    @FXML
    public void initialize() {
    }

    public void handleLogoutButtonClicked(ActionEvent actionEvent) {
    }

    public void handleUndoButtonClicked(ActionEvent actionEvent) {
    }

    public void handleRedoButtonClicked(ActionEvent actionEvent) {
    }

    public void handleEditButtonClicked(ActionEvent actionEvent) {
    }
}
