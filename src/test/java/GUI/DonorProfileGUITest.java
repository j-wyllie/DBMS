package GUI;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import odms.model.profile.Condition;
import odms.model.profile.Profile;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

@Ignore
public class DonorProfileGUITest extends TestFxMethods {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private LocalDate now = LocalDate.now();

    //Runs tests in background if headless is set to true. This gets it working with the CI.
    @BeforeClass
    public static void headless() throws TimeoutException {
        GUITestSetup.headless();
    }

    /**
     * Test that the history tab can be opened but none of the buttons to edit it should be
     * available
     */
    @Test
    public void openHistoryTabForDonor() {
        loginAsDonor(1);
        clickOn("#medicalHistoryTab");

        Scene scene = getTopScene();
        Button toggleCured = (Button) scene.lookup("#toggleCuredButton");
        Button toggleChronic = (Button) scene.lookup("#toggleChronicButton");
        Button addNewCondition = (Button) scene.lookup("#addNewConditionButton");
        Button deleteCondition = (Button) scene.lookup("#deleteConditionButton");
        assertFalse(toggleCured.isVisible());
        assertFalse(toggleChronic.isVisible());
        assertFalse(addNewCondition.isVisible());
        assertFalse(deleteCondition.isVisible());
    }

    /**
     * Test that the history tab is available when accessed by a clinician Also test that all of the
     * buttons that can be used to edit the history are available
     */
    @Test
    public void openHistoryForClinician() {
        loginAsClinician();
        openSearchedProfile("Galil AR");

        Scene scene = getTopScene();
        Button toggleCured = (Button) scene.lookup("#toggleCuredButton");
        Button toggleChronic = (Button) scene.lookup("#toggleChronicButton");
        Button addNewCondition = (Button) scene.lookup("#addNewConditionButton");
        Button deleteCondition = (Button) scene.lookup("#deleteConditionButton");
        assertTrue(toggleCured.isVisible());
        assertTrue(toggleChronic.isVisible());
        assertTrue(addNewCondition.isVisible());
        assertTrue(deleteCondition.isVisible());
    }

    @Test
    /**
     * Test that the clinician can toggle a disease's chronic status
     */
    public void testChronicToggle() {
        loginAsClinician();
        openSearchedProfile("Galil AR");
        clickOn("#medicalHistoryTab");

        TableView currentConditions = getTableView("#curConditionsTable");

        clickOn(row("#curConditionsTable", 0));
        Condition condition = (Condition) currentConditions.getSelectionModel().getSelectedItem();
        Boolean initialChronic = condition.getChronic();

        clickOn("#toggleChronicButton");

        assertNotEquals(initialChronic, condition.getChronic());
    }

    @Test
    /**
     * Test that the clinician can toggle a condition from present to past
     */
    public void testPresentToPastToggle() {
        loginAsClinician();
        openSearchedProfile("Galil AR");
        clickOn("#medicalHistoryTab");

        TableView pastConditions = getTableView("#pastConditionsTable");
        Integer initialSize = pastConditions.getItems().size();

        clickOn(row("#curConditionsTable", 0));
        clickOn("#toggleChronicButton");
        clickOn(row("#curConditionsTable", 0));
        clickOn("#toggleCuredButton");

        assertEquals(pastConditions.getItems().size(), initialSize + 1);
    }

    @Test
    /**
     * Test that the clinician can toggle a condition from past to present
     */
    public void testPastToPresentToggle() {
        loginAsClinician();
        openSearchedProfile("Galil AR");
        clickOn("#medicalHistoryTab");

        TableView currentConditions = getTableView("#curConditionsTable");
        Integer initialSize = currentConditions.getItems().size();

        clickOn(row("#pastConditionsTable", 0));
        clickOn("#toggleCuredButton");

        assertEquals(currentConditions.getItems().size(), initialSize + 1);
    }

    @Test
    /**
     * Test that a current condition's name can be changed by (triple) clicking it
     */
    public void testChangeCurrentCondition() {
        loginAsClinician();
        openSearchedProfile("Galil AR");
        clickOn("#medicalHistoryTab");

        TableView currentConditions = getTableView("#curConditionsTable");
        doubleClickOn(cell("#curConditionsTable", 0, 0));
        Condition condition = (Condition) currentConditions.getSelectionModel().getSelectedItem();

        clickOn(cell("#curConditionsTable", 0, 0));
        deleteLine();
        write("Broken Heart");
        press(KeyCode.ENTER);

        assertEquals(condition.getName(), "Broken Heart");
    }

    @Test
    /**
     * Test that a past condition's name can be changed by (triple) clicking on it
     */
    public void testChangePastCondition() {
        loginAsClinician();
        openSearchedProfile("Galil AR");
        clickOn("#medicalHistoryTab");

        TableView pastConditions = getTableView("#pastConditionsTable");
        doubleClickOn(cell("#pastConditionsTable", 0, 0));
        Condition condition = (Condition) pastConditions.getSelectionModel().getSelectedItem();

        clickOn(cell("#pastConditionsTable", 0, 0));
        deleteLine();
        write("Heart too muscular");
        press(KeyCode.ENTER);

        assertEquals(condition.getName(), "Heart too muscular");
    }

    @Test
    /**
     * Test that the date of diagnoses of the current condition can be updated to a valid date
     * A valid date being a date inbetween the dob and current date
     * (Triple click)
     */
    public void testNewValidDiagnosesDateCurrentCondition() {
        loginAsClinician();
        openSearchedProfile("Galil AR");

        TableView searchTable = getTableView("#searchTable");
        Profile profile = (Profile) searchTable.getSelectionModel().getSelectedItem();

        clickOn("#medicalHistoryTab");

        TableView currentConditions = getTableView("#curConditionsTable");
        doubleClickOn(cell("#curConditionsTable", 0, 2));
        Condition condition = (Condition) currentConditions.getSelectionModel().getSelectedItem();

        clickOn(cell("#curConditionsTable", 0, 2));
        deleteLine();

        write(profile.getDateOfBirth().plusDays(1).format(formatter));
        press(KeyCode.ENTER);

        assertEquals(condition.getDateOfDiagnosis(), profile.getDateOfBirth().plusDays(1));
    }

    @Test
    /**
     * Test that the date of diagnoses of a current condition can not be updated to an invalid date
     * An invalid date is either before the dob or after the current date
     */
    public void testNewInvalidDiagnosesDateCurrentCondition() {
        loginAsClinician();
        openSearchedProfile("Galil AR");

        clickOn("#medicalHistoryTab");

        TableView currentConditions = getTableView("#curConditionsTable");
        doubleClickOn(cell("#curConditionsTable", 0, 2));
        Condition condition = (Condition) currentConditions.getSelectionModel().getSelectedItem();
        LocalDate oldDate = condition.getDateOfDiagnosis();

        clickOn(cell("#curConditionsTable", 0, 2));
        deleteLine();

        write(now.plusDays(1).format(formatter));
        press(KeyCode.ENTER);

        assertEquals(condition.getDateOfDiagnosis(), oldDate);
    }

    @Test
    /**
     * Test that the date of diagnoses of the past condition can be updated to a valid date
     */
    public void testNewValidDiagnosesDatePastCondition() {
        loginAsClinician();
        openSearchedProfile("Galil AR");

        TableView searchTable = getTableView("#searchTable");
        Profile profile = (Profile) searchTable.getSelectionModel().getSelectedItem();

        clickOn("#medicalHistoryTab");

        TableView pastConditions = getTableView("#pastConditionsTable");
        doubleClickOn(cell("#pastConditionsTable", 0, 1));
        Condition condition = (Condition) pastConditions.getSelectionModel().getSelectedItem();

        clickOn(cell("#pastConditionsTable", 0, 1));
        deleteLine();

        write(profile.getDateOfBirth().plusDays(1).format(formatter));
        press(KeyCode.ENTER);

        assertEquals(condition.getDateOfDiagnosis(), profile.getDateOfBirth().plusDays(1));
    }

    @Test
    /**
     * Test that the date of diagnonses of a past condition can not be update to an invalid date
     */
    public void testNewInvalidDiagnosisDatePastCondition() {
        loginAsClinician();
        openSearchedProfile("Galil AR");

        TableView searchTable = getTableView("#searchTable");

        clickOn("#medicalHistoryTab");

        TableView pastConditions = getTableView("#pastConditionsTable");
        doubleClickOn(cell("#pastConditionsTable", 0, 1));
        Condition condition = (Condition) pastConditions.getSelectionModel().getSelectedItem();
        LocalDate oldDate = condition.getDateOfDiagnosis();

        clickOn(cell("#pastConditionsTable", 0, 1));
        deleteLine();

        write(now.plusDays(1).format(formatter));
        press(KeyCode.ENTER);

        assertEquals(condition.getDateOfDiagnosis(), oldDate);
    }

    @Test
    /**
     * Test that the date cured of a past condition can be updated to a valid date
     */
    public void testNewValidCuredDate() {
        loginAsClinician();
        openSearchedProfile("Galil AR");

        TableView searchTable = getTableView("#searchTable");
        clickOn("#medicalHistoryTab");

        TableView pastConditions = getTableView("#pastConditionsTable");
        doubleClickOn(cell("#pastConditionsTable", 0, 2));
        Condition condition = (Condition) pastConditions.getSelectionModel().getSelectedItem();

        clickOn(cell("#pastConditionsTable", 0, 2));
        deleteLine();

        write(now.format(formatter));
        press(KeyCode.ENTER);

        assertEquals(condition.getDateCured(), now);
    }
}
