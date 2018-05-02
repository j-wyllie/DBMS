package GUI;

import java.util.concurrent.TimeoutException;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import odms.controller.GuiMain;
import odms.profile.Condition;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class DonorProfileGUITest extends TestFxMethods {
    private GuiMain guiMain;

    //Runs tests in background if headless is set to true. This gets it working with the CI.
    @BeforeClass
    public static void headless() throws TimeoutException {
        //GUITestSetup.headless();
    }

    /**
     * Test that the history tab can be opened but none of the buttons to edit it should be available
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
     * Test that the history tab is available when accessed by a clinician
     * Also test that all of the buttons that can be used to edit the history are available
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
        clickOn("#searchTab");

        TableView searchTable = getTableView("#searchTable");

        doubleClickOn(row("#searchTable", 0));
        clickOn("#medicalHistoryTab");

        TableView currentConditions = getTableView("#curConditionsTable");
        Integer initialSize = currentConditions.getItems().size();

        clickOn(row("#pastConditionsTable", 0));
        clickOn("#toggleCuredButton");

        assertEquals(currentConditions.getItems().size(), initialSize + 1);
    }

    
}
