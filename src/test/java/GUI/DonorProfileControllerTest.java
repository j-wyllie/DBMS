package GUI;

import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import odms.profile.Profile;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.api.FxAssert.verifyThat;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class DonorProfileControllerTest extends ApplicationTest {
    private GuiMain guiMain;

    //Runs tests in background if headless is set to true. This gets it working with the CI.
    @BeforeClass
    public static void headless() {
        GUITestSetup.headless();
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    /**
     * Initializes the main gui
     * @param stage current stage
     * @throws Exception throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception{
        guiMain = new GuiMain();
        guiMain.start(stage);
    }

    /**
     * Test that the history tab can be opened but none of the buttons to edit it should be available
     */
    @Test
    public void openHistoryTabForDonor() {
        loginDonor();
        clickOn("#medicalHistoryTab");
        Scene scene = getTopModalStage();

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
        logInClinician();
        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile firstDonor = (Profile) searchTable.getItems().get(0);

        doubleClickOn(row("#searchTable", 0));
        //opening the first donor
        Scene scene = getTopModalStage();

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
    /*public void testAddDiagnonsis () {
        logInClinician();
        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile firstDonor = (Profile) searchTable.getItems().get(0);
        doubleClickOn(row("#searchTable", 0));

        //opening the first donor
        Scene scene = getTopModalStage();
        Button addNewCondition = (Button) scene.lookup("#addNewConditionButton");
        clickOn(addNewCondition);

        Scene scene2 = getTopModalStage();
        TextField name = (TextField) scene2.lookup("#nameField");
        clickOn(name).write("Heart Disease");

        TextField date = (TextField) scene2.lookup("#dateDiagnosedField");

        // Check that this already has the current date in it
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        assertEquals(now.format(formatter), date.getText());

        CheckBox chronic = (CheckBox) scene2.lookup("#chronicCheckBox");
        clickOn(chronic);
    }*/

    /**
     * logs in a donor
     */
    public void loginDonor() {
        clickOn("#usernameField").write("1");
        clickOn("#loginButton");
    }

    /**
     * logs in the clinician and opens up the search tab
     */
    public void logInClinician() {
        clickOn("#usernameField").write("0");
        clickOn("#loginButton");
    }

    /**
     * gets current stage with all windows.
     * @return All of the current windows
     */
    protected Scene getTopModalStage() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        // It is needed to get the first found modal window.
        final List<Window> allWindows = new ArrayList<>(robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);
        return (Scene) allWindows.get(0).getScene();
    }

    /**
     * @param tableSelector The id of the table to be used
     * @return Returns a table view node from the given ID
     */
    private TableView<?> getTableView(String tableSelector) {
        Node node = lookup(tableSelector).queryTableView();
        if (!(node instanceof TableView)) {
        }
        return (TableView<?>) node;
    }

    /**
     * @param tableSelector Id of table that contains the row
     * @param row           row number
     * @return returns a table row
     */
    protected TableRow<?> row(String tableSelector, int row) {

        TableView<?> tableView = getTableView(tableSelector);

        List<Node> current = tableView.getChildrenUnmodifiable();
        while (current.size() == 1) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        current = ((Parent) current.get(1)).getChildrenUnmodifiable();
        while (!(current.get(0) instanceof TableRow)) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        Node node = current.get(row);
        return (TableRow<?>) node;
    }
}
