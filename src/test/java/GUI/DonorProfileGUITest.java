package GUI;

import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.xml.soap.Text;
import odms.controller.GuiMain;
import odms.profile.Condition;
import odms.profile.Profile;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertNotEquals;
import static org.testfx.api.FxAssert.verifyThat;
import java.time.LocalDateTime;

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

        // logout
        clickOn("#logoutButton");
    }

    /**
     * Test that the history tab is available when accessed by a clinician
     * Also test that all of the buttons that can be used to edit the history are available
     */
    @Test
    public void openHistoryForClinician() {
        loginAsClinician();
        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile firstDonor = (Profile) searchTable.getItems().get(0);

        doubleClickOn(row("#searchTable", 0));
        //opening the first donor
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
     * Test that a diagnosis can be added to a profile by a clinician
     */
    public void testAddingDiagnoses () {
        loginAsClinician();
        clickOn("#searchTab");

        TableView searchTable = getTableView("#searchTable");
        Profile firstDonor = (Profile) searchTable.getItems().get(0);

        doubleClickOn(row("#searchTable", 0));
        clickOn("#medicalHistoryTab");

        TableView currentConditions = getTableView("#curConditionsTable");
        Integer initialSize = currentConditions.getItems().size();

        clickOn("#addNewConditionButton");
        clickOn("#nameField").write("Heart Disease");

        Scene scene = getTopScene();
        TextField date = (TextField) scene.lookup("#dateDiagnosedField");

        // Check that this already has the current date in it
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        assertEquals(now.format(formatter), date.getText());

        clickOn("#chronicCheckBox");
        clickOn("#addButton");

        // Check that the new condition is added
        assertEquals(currentConditions.getItems().size(), initialSize + 1);
    }

    @Test
    /**
     * Test adding a cured condition to the past conditions table
     */
    public void testAddCuredDisease() {
        loginAsClinician();
        clickOn("#searchTab");

        TableView searchTable = getTableView("#searchTable");

        doubleClickOn(row("#searchTable", 0));
        clickOn("#medicalHistoryTab");

        TableView pastConditions = getTableView("#pastConditionsTable");
        Integer pastInitialSize = pastConditions.getItems().size();

        clickOn("#addNewConditionButton");
        clickOn("#nameField").write("Influenza");
        clickOn("#dateDiagnosedField");
        deleteLine();
        write("01-03-2018");
        clickOn("#curedCheckBox");
        clickOn("#dateCuredField").write("01-04-2018");
        clickOn("#addButton");

        assertEquals(pastConditions.getItems().size(), pastInitialSize + 1);
    }

    @Test
    /**
     * Test trying to add a cured condition with a diagnosis date after the cured date
     */
    public void testCuredConditionDatesOutOfOrder() {
        loginAsClinician();
        clickOn("#searchTab");

        TableView searchTable = getTableView("#searchTable");
        Profile firstDonor = (Profile) searchTable.getItems().get(0);

        doubleClickOn(row("#searchTable", 0));
        clickOn("#medicalHistoryTab");

        TableView pastConditions = getTableView("#pastConditionsTable");
        Integer pastInitialSize = pastConditions.getItems().size();

        clickOn("#addNewConditionButton");
        clickOn("#nameField").write("Influenza");
        clickOn("#dateDiagnosedField");
        deleteLine();
        write("01-04-2018");
        clickOn("#curedCheckBox");
        clickOn("#dateCuredField").write("01-03-2018");
        clickOn("#addButton");

        Scene scene = getTopScene();
        Label date = (Label) scene.lookup("#warningLabel");
        assertTrue(date.isVisible());
    }

    @Test
    /**
     * Test trying to add a cured condition with the cured date after the current date
     */
    public void testCuredDateAfterToday() {
        loginAsClinician();
        clickOn("#searchTab");

        TableView searchTable = getTableView("#searchTable");

        doubleClickOn(row("#searchTable", 0));
        clickOn("#medicalHistoryTab");

        TableView pastConditions = getTableView("#pastConditionsTable");
        Integer pastInitialSize = pastConditions.getItems().size();

        clickOn("#addNewConditionButton");
        clickOn("#nameField").write("Influenza");
        clickOn("#dateDiagnosedField");
        deleteLine();
        write("01-03-2018");
        clickOn("#curedCheckBox");

        LocalDateTime now = LocalDateTime.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        clickOn("#dateCuredField").write(now.format(formatter));
        clickOn("#addButton");

        Scene scene = getTopScene();
        Label date = (Label) scene.lookup("#warningLabel");
        assertTrue(date.isVisible());
    }

    @Test
    /**
     * Test that the clinician can toggle a disease's chronic status
     */
    public void testChronicToggle() {
        loginAsClinician();
        clickOn("#searchTab");

        doubleClickOn(row("#searchTable", 0));
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
        clickOn("#searchTab");

        TableView searchTable = getTableView("#searchTable");

        doubleClickOn(row("#searchTable", 0));
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
