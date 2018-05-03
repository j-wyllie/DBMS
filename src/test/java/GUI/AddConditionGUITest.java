package GUI;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//@Ignore
public class AddConditionGUITest extends TestFxMethods {
    //Runs tests in background if headless is set to true. This gets it working with the CI.
    @BeforeClass
    public static void headless() throws TimeoutException {
         GUITestSetup.headless();
    }

    @Test
    /**
     * Test that a diagnosis can be added to a profile by a clinician
     */
    public void testAddingCondition() {
        loginAsClinician();
        openSearchedProfile("Galil AR");
        clickOn("#medicalHistoryTab");

        TableView currentConditions = getTableView("#curConditionsTable");
        Integer initialSize = currentConditions.getItems().size();

        clickOn("#addNewConditionButton");
        clickOn("#nameField").write("Heart Disease");

        Scene scene = getTopScene();
        TextField date = (TextField) scene.lookup("#dateDiagnosedField");

        // Check that this already has the current date in it
        LocalDate now = LocalDate.now();
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
    public void testAddCuredCondition() {
        loginAsClinician();
        openSearchedProfile("Galil AR");
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
     * Test adding a condition with no name
     */
    public void testAddNoName() {
        loginAsClinician();
        openSearchedProfile("Galil AR");
        clickOn("#medicalHistoryTab");
        clickOn("#addNewConditionButton");
        clickOn("#addButton");

        Scene scene = getTopScene();
        Label date = (Label) scene.lookup("#warningLabel");
        assertTrue(date.isVisible());
    }

    @Test
    /**
     * Test adding a cured condition without a cured date
     */
    public void testAddCuredNoDate() {
        loginAsClinician();
        openSearchedProfile("Galil AR");
        clickOn("#medicalHistoryTab");
        clickOn("#addNewConditionButton");
        clickOn("#nameField").write("Influenza");
        clickOn("#curedCheckBox");
        clickOn("#addButton");

        Scene scene = getTopScene();
        Label date = (Label) scene.lookup("#warningLabel");
        assertTrue(date.isVisible());
    }

    @Test
    /**
     * Test trying to add a cured condition with a diagnosis date after the cured date
     */
    public void testCuredDatesOutOfOrder() {
        loginAsClinician();
        openSearchedProfile("Galil AR");
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
        openSearchedProfile("Galil AR");
        clickOn("#medicalHistoryTab");

        clickOn("#addNewConditionButton");
        clickOn("#nameField").write("Influenza");
        clickOn("#dateDiagnosedField");
        deleteLine();
        write("01-03-2018");
        clickOn("#curedCheckBox");

        LocalDate now = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        clickOn("#dateCuredField").write(now.format(formatter));
        clickOn("#addButton");

        Scene scene = getTopScene();
        Label date = (Label) scene.lookup("#warningLabel");
        assertTrue(date.isVisible());
    }

    @Test
    /**
     * Test adding a condition with invalid diagnosis date
     */
    public void testInvalidDiagnosisDate() {
        loginAsClinician();
        openSearchedProfile("Galil AR");
        clickOn("#medicalHistoryTab");

        clickOn("#addNewConditionButton");
        clickOn("#nameField").write("Influenza");
        clickOn("#dateDiagnosedField");
        deleteLine();
        write("Team 200");
        clickOn("#addButton");

        Scene scene = getTopScene();
        Label date = (Label) scene.lookup("#warningLabel");
        assertTrue(date.isVisible());
    }

    @Test
    /**
     * Test adding a condition with an invalid cured date
     */
    public void testInvalidCuredDate() {
        loginAsClinician();
        openSearchedProfile("Galil AR");
        clickOn("#medicalHistoryTab");
        clickOn("#addNewConditionButton");
        clickOn("#nameField").write("Influenza");
        clickOn("#dateDiagnosedField");
        deleteLine();
        write("01-03-2018");
        clickOn("#curedCheckBox");
        clickOn("#dateCuredField").write("Team 200");
        clickOn("#addButton");

        Scene scene = getTopScene();
        Label date = (Label) scene.lookup("#warningLabel");
        assertTrue(date.isVisible());
    }
}
