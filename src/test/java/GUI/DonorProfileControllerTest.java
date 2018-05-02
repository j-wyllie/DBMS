package GUI;


import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import odms.controller.ClinicianProfileController;
import odms.controller.GuiMain;
import odms.controller.LoginController;
import odms.medications.Drug;
import odms.profile.Profile;
import org.junit.*;
import org.testfx.api.FxToolkit;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TableViewMatchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.api.FxToolkit.setupScene;
import static org.testfx.api.FxToolkit.showStage;



public class DonorProfileControllerTest extends TestFxMethods {

    private GuiMain guiMain;


    //Runs tests in background if headless is set to true. This gets it working with the CI.
    @BeforeClass
    public static void headless() throws TimeoutException {
        GUITestSetup.headless();
    }

    @Before
    public void loginUser() {
        loginAsClinician();
    }

    @Test
    public void addMedicationTest() {
        //open up the first donor

        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile donor = (Profile) searchTable.getItems().get(1);
        doubleClickOn(row("#searchTable", 1));
        Scene scene = getTopScene();

        Label userIdLabel = (Label) scene.lookup("#userIdLabel");
        Integer userId = Integer.parseInt(userIdLabel.getText().substring(10)); //gets id of user being edited.

        clickOn(scene.lookup("#medicationsTab"));
        Scene scene2 = getTopScene();

        clickOn("#textFieldMedicationSearch").write("1 medication");
        clickOn(scene2.lookup("#buttonAddMedication"));

        assertEquals("1 medication", donor.getCurrentMedications().get(0).getDrugName());
    }


    @Test
    public void deleteMedicationTest() {
        //open up the first donor

        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile donor = (Profile) searchTable.getItems().get(1);
        doubleClickOn(row("#searchTable", 1));
        Scene scene = getTopScene();

        Label userIdLabel = (Label) scene.lookup("#userIdLabel");
        Integer userId = Integer.parseInt(userIdLabel.getText().substring(10)); //gets id of user being edited.

        clickOn(scene.lookup("#medicationsTab"));
        Scene scene2 = getTopScene();

        clickOn("#textFieldMedicationSearch").write("2 medication");
        clickOn(scene2.lookup("#buttonAddMedication"));

        TableView currentMedicationsTable = getTableView("#tableViewCurrentMedications");
        System.out.println(currentMedicationsTable.getItems());
        Drug firstDrug = (Drug) currentMedicationsTable.getItems().get(0);
        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn(scene2.lookup("#buttonDeleteMedication"));

        assert(!donor.getCurrentMedications().contains(firstDrug));
    }


    @Test
    public void moveMedicationToHistoricTest() {
        //open up the first donor

        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile donor = (Profile) searchTable.getItems().get(1);
        doubleClickOn(row("#searchTable", 1));
        Scene scene = getTopScene();

        Label userIdLabel = (Label) scene.lookup("#userIdLabel");
        Integer userId = Integer.parseInt(userIdLabel.getText().substring(10)); //gets id of user being edited.

        clickOn(scene.lookup("#medicationsTab"));
        Scene scene2 = getTopScene();

        clickOn("#textFieldMedicationSearch").write("3 medication");
        clickOn(scene2.lookup("#buttonAddMedication"));

        TableView currentMedicationsTable = getTableView("#tableViewCurrentMedications");
        Drug firstDrug = (Drug) currentMedicationsTable.getItems().get(0);
        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn(scene2.lookup("#buttonMedicationCurrentToHistoric"));

        assert(donor.getHistoryOfMedication().contains(firstDrug));
    }


    @Test
    public void moveMedicationToCurrentTest() {
        //open up the first donor

        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile donor = (Profile) searchTable.getItems().get(1);
        doubleClickOn(row("#searchTable", 1));
        Scene scene = getTopScene();

        Label userIdLabel = (Label) scene.lookup("#userIdLabel");
        Integer userId = Integer.parseInt(userIdLabel.getText().substring(10)); //gets id of user being edited.

        clickOn(scene.lookup("#medicationsTab"));
        Scene scene2 = getTopScene();

        clickOn("#textFieldMedicationSearch").write("4 medication");
        clickOn(scene2.lookup("#buttonAddMedication"));

        TableView currentMedicationsTable = getTableView("#tableViewCurrentMedications");
        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn(scene2.lookup("#buttonMedicationCurrentToHistoric"));

        TableView historicMedicationsTable = getTableView("#tableViewHistoricMedications");
        Drug firstDrug = (Drug) historicMedicationsTable.getItems().get(0);
        clickOn(row("#tableViewHistoricMedications", 0));
        clickOn(scene2.lookup("#buttonMedicationHistoricToCurrent"));

        assert(donor.getCurrentMedications().contains(firstDrug));
    }
}
