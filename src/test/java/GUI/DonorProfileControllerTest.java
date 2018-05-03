package GUI;


import javafx.scene.Scene;
import javafx.scene.control.*;
import odms.controller.GuiMain;
import odms.medications.Drug;
import odms.profile.Profile;
import org.junit.*;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeoutException;


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

        clickOn(scene.lookup("#medicationsTab"));
        Scene scene2 = getTopScene();

        clickOn("#textFieldMedicationSearch").write("s");
        sleep(2000);
        clickOn(scene2).lookup("#tableViewDrugInteractionsNames");
        clickOn(scene2.lookup("#buttonAddMedication"));

        assertEquals("s", donor.getCurrentMedications().get(0).getDrugName());
        closeCurrentWindow();
        closeCurrentWindow();
    }


    @Test
    public void deleteMedicationTest() {
        //open up the first donor

        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile donor = (Profile) searchTable.getItems().get(1);
        doubleClickOn(row("#searchTable", 1));
        Scene scene = getTopScene();

        clickOn(scene.lookup("#medicationsTab"));
        Scene scene2 = getTopScene();

        clickOn("#textFieldMedicationSearch").write("s");
        sleep(2000);
        clickOn(scene2).lookup("#tableViewDrugInteractionsNames");
        clickOn(scene2.lookup("#buttonAddMedication"));

        TableView currentMedicationsTable = getTableView("#tableViewCurrentMedications");
        Drug firstDrug = (Drug) currentMedicationsTable.getItems().get(0);
        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn(scene2.lookup("#buttonDeleteMedication"));

        assert(!donor.getCurrentMedications().contains(firstDrug));
        closeCurrentWindow();
        closeCurrentWindow();
    }


    @Test
    public void moveMedicationToHistoricTest() {
        //open up the first donor

        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile donor = (Profile) searchTable.getItems().get(1);
        doubleClickOn(row("#searchTable", 1));
        Scene scene = getTopScene();

        clickOn(scene.lookup("#medicationsTab"));
        Scene scene2 = getTopScene();

        clickOn("#textFieldMedicationSearch").write("s");
        sleep(2000);
        clickOn(scene2).lookup("#tableViewDrugInteractionsNames");
        clickOn(scene2.lookup("#buttonAddMedication"));

        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn(scene2.lookup("#buttonMedicationCurrentToHistoric"));

        TableView historicMedicationsTable = getTableView("#tableViewHistoricMedications");
        Drug firstDrug = (Drug) historicMedicationsTable.getItems().get(0);

        assert(donor.getHistoryOfMedication().contains(firstDrug));
        closeCurrentWindow();
        closeCurrentWindow();
    }


    @Test
    public void moveMedicationToCurrentTest() {
        //open up the first donor

        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile donor = (Profile) searchTable.getItems().get(1);
        doubleClickOn(row("#searchTable", 1));
        Scene scene = getTopScene();

        clickOn(scene.lookup("#medicationsTab"));
        Scene scene2 = getTopScene();

        clickOn("#textFieldMedicationSearch").write("s");
        sleep(2000);
        clickOn(scene2).lookup("#tableViewDrugInteractionsNames");
        clickOn(scene2.lookup("#buttonAddMedication"));

        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn(scene2.lookup("#buttonMedicationCurrentToHistoric"));
        clickOn(row("#tableViewHistoricMedications", 0));
        clickOn(scene2.lookup("#buttonMedicationHistoricToCurrent"));

        TableView currentMedicationsTable = getTableView("#tableViewCurrentMedications");
        Drug firstDrug = (Drug) currentMedicationsTable.getItems().get(0);


        assert(donor.getCurrentMedications().contains(firstDrug));
        closeCurrentWindow();
        closeCurrentWindow();
    }
}
