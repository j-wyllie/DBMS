package GUI;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.Window;
import odms.controller.GuiMain;
import odms.profile.Profile;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClinicianProcedureTests extends ApplicationTest {

    private GuiMain guiMain;

    //Runs tests in background if headless is set to true. This gets it working with the CI.
    @BeforeClass
    public static void headless() {
        GUITestSetup.headless();
    }

    @After()
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Before
    public void loginUser() {
         logInClinician();
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
     * logs in the clinician and opens up the search tab
     */
    public void logInClinician() {
        clickOn("#usernameField").write("0");
        clickOn("#loginButton");
    }

    /**
     * Checks that a procedure can be created with correct inputs.
     */
    @Test
    public void createProcedureTest() {


        //Open First Donor
        clickOn("#searchTab");
        doubleClickOn(row("#searchTable", 0));
        Scene scene = getTopModalStage();

        //Give user a new procedure named 'Generic Procedure'.
        clickOn(scene.lookup("#procedureTab"));
        clickOn(scene.lookup("#addNewProcedureButton"));
        Scene scene2 = getTopModalStage();
        TextField procedureSummary = (TextField) scene2.lookup("#summaryField");
        TextField procedureDate = (TextField) scene2.lookup("#dateOfProcedureField");
        clickOn(procedureSummary).write("!");
        clickOn(procedureDate).write("12-12-9999");
        clickOn("#addButton");

        //Check procedure exists in table with correct name and date.
        clickOn("#pendingDateColumn");
        assertEquals(cell("#pendingProcedureTable", 0, 1).getItem().toString(), "9999-12-12");
        assertEquals(cell("#pendingProcedureTable", 0, 0).getItem(), "!");

        //Delete the entry afterwards
        clickOn(row("#pendingProcedureTable", 0));
        clickOn("#deleteProcedureButton");
        closeCurrentWindow();

    }

    /**
     * Checks that a created procedure is able to be deleted.
     */
    @Test
    public void deleteProcedureTest() {


        //Open First Donor
        clickOn("#searchTab");
        doubleClickOn(row("#searchTable", 0));
        Scene scene = getTopModalStage();

        //Give user a new procedure named 'Generic Procedure'.
        clickOn(scene.lookup("#procedureTab"));
        clickOn(scene.lookup("#addNewProcedureButton"));
        Scene scene2 = getTopModalStage();
        TextField procedureSummary = (TextField) scene2.lookup("#summaryField");
        TextField procedureDate = (TextField) scene2.lookup("#dateOfProcedureField");
        clickOn(procedureSummary).write("!");
        clickOn(procedureDate).write("12-12-9999");
        clickOn("#addButton");

        //Check length of the table.
        Integer tableLength = getTableView("#pendingProcedureTable").getItems().size();

        //Delete the said procedure.
        clickOn(row("#pendingProcedureTable", 0));
        clickOn("#deleteProcedureButton");
        Integer newTableLength = getTableView("#pendingProcedureTable").getItems().size();

        //Check Deleted procedure is deleted.
        assertTrue(tableLength == newTableLength+1);
        closeCurrentWindow();
    }

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

    /**
     * @param tableSelector ID of the table that contains the cell wanted
     * @param row           row number
     * @param column        column number
     * @return the cell of the table
     */
    protected TableCell<?, ?> cell(String tableSelector, int row, int column) {
        List<Node> current = row(tableSelector, row).getChildrenUnmodifiable();
        while (current.size() == 1 && !(current.get(0) instanceof TableCell)) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        Node node = current.get(column);
        return (TableCell<?, ?>) node;
    }

    /**
     * gets current stage with all windows.
     * @return All of the current windows
     */
    private javafx.scene.Scene getTopModalStage() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        // It is needed to get the first found modal window.
        final List<Window> allWindows = new ArrayList<>(robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        return (javafx.scene.Scene) allWindows.get(0).getScene();
    }
}
