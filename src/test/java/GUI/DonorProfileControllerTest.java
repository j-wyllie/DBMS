package GUI;

import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import odms.medications.Drug;
import odms.profile.Profile;
import org.junit.*;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeoutException;

@Ignore
public class DonorProfileControllerTest extends TestFxMethods {


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
        clickOn("#medicationsTab");

        clickOn("#textFieldMedicationSearch").write("s");
        clickOn("#tableViewDrugInteractionsNames");
        clickOn("#buttonAddMedication");

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
        clickOn("#medicationsTab");

        clickOn("#textFieldMedicationSearch").write("s");
        clickOn("#tableViewDrugInteractionsNames");
        clickOn("#buttonAddMedication");

        TableView currentMedicationsTable = getTableView("#tableViewCurrentMedications");
        Drug firstDrug = (Drug) currentMedicationsTable.getItems().get(0);
        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn("#buttonDeleteMedication");

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
        clickOn("#medicationsTab");

        clickOn("#textFieldMedicationSearch").write("s");
        clickOn("#tableViewDrugInteractionsNames");
        clickOn("#buttonAddMedication");

        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn("#buttonMedicationCurrentToHistoric");
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
        clickOn("#medicationsTab");

        clickOn("#textFieldMedicationSearch").write("s");
        clickOn("#tableViewDrugInteractionsNames");
        clickOn("#buttonAddMedication");

        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn("#buttonMedicationCurrentToHistoric");
        clickOn(row("#tableViewHistoricMedications", 0));
        clickOn("#buttonMedicationHistoricToCurrent");

        TableView currentMedicationsTable = getTableView("#tableViewCurrentMedications");
        Drug firstDrug = (Drug) currentMedicationsTable.getItems().get(0);


        assert(donor.getCurrentMedications().contains(firstDrug));
        closeCurrentWindow();
        closeCurrentWindow();
    }

    @Test
    public void getActiveIngredientsTest() {
        // Check correct data is displayed in GUI.

        // Add medication to current
        clickOn("#searchTab");
        doubleClickOn(row("#searchTable", 1));
        clickOn("#medicationsTab");
        clickOn("#textFieldMedicationSearch").write("s");
        clickOn("#textFieldMedicationSearch");
        clickOn("#buttonAddMedication");


        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn("#buttonViewActiveIngredients");

        TableView activeIngredients = getTableView("#tableViewActiveIngredients");
        String firstLine = (String) activeIngredients.getItems().get(0);
        String secondLine = (String) activeIngredients.getItems().get(1);

        assertEquals("Active ingredients for Seconal sodium:", firstLine);
        assertEquals("Secobarbital sodium", secondLine);
        closeCurrentWindow();
        closeCurrentWindow();
    }

    @Test
    public void getDrugInteractionsTest() {
        // Check correct data is displayed in GUI.

        // Add medications to current
        clickOn("#searchTab");
        doubleClickOn(row("#searchTable", 1));
        clickOn("#medicationsTab");
        clickOn("#textFieldMedicationSearch").write("acetaminophen");
        clickOn("#tableViewDrugInteractionsNames");
        clickOn("#buttonAddMedication");

        clickOn("#textFieldMedicationSearch");
        deleteLine();
        clickOn("#textFieldMedicationSearch").write("warfarin-sodium");
        clickOn("#tableViewDrugInteractionsNames");
        clickOn("#buttonAddMedication");

        // Move one to historic
        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn("#buttonMedicationCurrentToHistoric");

        // Select both
        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn(row("#tableViewHistoricMedications", 0));

        clickOn("#buttonShowDrugInteractions");

        assertEquals("Interactions between:",
                getTableView("#tableViewDrugInteractionsNames").getItems().get(0).toString());
        assertEquals("warfarin-sodium",
                getTableView("#tableViewDrugInteractionsNames").getItems().get(1).toString());
        assertEquals("acetaminophen",
                getTableView("#tableViewDrugInteractionsNames").getItems().get(2).toString());
        closeCurrentWindow();
        closeCurrentWindow();
    }
}
