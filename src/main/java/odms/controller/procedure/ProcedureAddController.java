package odms.controller.procedure;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import odms.view.profile.ProfileAddProcedureView;
import odms.view.profile.ProfileDisplayController;
import odms.controller.data.ProfileDataIO;
import odms.model.enums.OrganEnum;
import odms.model.profile.Procedure;
import odms.model.profile.Profile;

import java.time.LocalDate;
import java.util.ArrayList;

import static odms.controller.GuiMain.getCurrentDatabase;

public class ProcedureAddController {

    private ProfileDisplayController controller;

    private ObservableList<OrganEnum> donatedOrgans;

    private ProfileAddProcedureView view;

    public ProcedureAddController(ProfileAddProcedureView v) {
        view = v;
    }

    public void add() throws IllegalArgumentException{
        String summary = view.getSummaryField();
        LocalDate dateOfProcedure = view.getDateOfProcedureDatePicker();
        String longDescription = view.getDescriptionField();

        Procedure procedure;
        LocalDate dob = controller.getCurrentProfile().getDateOfBirth();
        if (dob.isAfter(dateOfProcedure)) {
            throw new IllegalArgumentException();
        }
        if (longDescription.equals("")) {
            procedure = new Procedure(summary, dateOfProcedure);

        } else {
            procedure = new Procedure(summary, dateOfProcedure, longDescription);
        }

        procedure.setOrgansAffected(view.getAffectedOrgansListView());

        addProcedure(procedure);
    }

    /**
     * Add a procedure to the current profile
     *
     * @param procedure
     */
    private void addProcedure(Procedure procedure) {
        ArrayList<Procedure> procedures = view.getSearchedDonor().getAllProcedures();
        procedures.add(procedure);
    }



}
