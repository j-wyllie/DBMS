//package GUI;
//
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.TableCell;
//import javafx.scene.control.TextField;
//import javafx.stage.Stage;
//import javafx.stage.Window;
//import odms.controller.GuiMain;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//@Ignore
//public class ClinicianProcedureTests extends TestFxMethods {
//
//    //Runs tests in background if headless is set to true. This gets it working with the CI.
//    @BeforeClass
//    public static void headless() {
//        GUITestSetup.headless();
//    }
//
//    @Before
//    public void loginUser() {
//        logInClinician();
//    }
//
//    /**
//     * Initializes the main gui
//     *
//     * @param stage current stage
//     * @throws Exception throws Exception
//     */
//    @Override
//    public void start(Stage stage) throws Exception {
//        GuiMain guiMain = new GuiMain();
//        guiMain.start(stage);
//    }
//
//    /**
//     * logs in the clinician and opens up the search tab
//     */
//    private void logInClinician() {
//        clickOn("#usernameField").write("0");
//        clickOn("#loginButton");
//    }
//
//    /**
//     * Checks that a procedure can be created with correct inputs.
//     */
//    @Test
//    public void createProcedureTest() {
//        //Open First Donor
//        clickOn("#searchTab");
//        doubleClickOn(row("#searchTable", 0));
//        Scene scene = getTopModalStage();
//
//        //Give user a new procedure named 'Generic procedure'.
//        clickOn(scene.lookup("#procedureTab"));
//        clickOn(scene.lookup("#addNewProcedureButton"));
//        Scene scene2 = getTopModalStage();
//        TextField procedureSummary = (TextField) scene2.lookup("#summaryField");
//        TextField procedureDate = (TextField) scene2.lookup("#dateOfProcedureField");
//        clickOn(procedureSummary).write("!");
//        clickOn(procedureDate).write("12-12-9999");
//        clickOn("#addButton");
//
//        //Check procedure exists in table with correct name and date.
//        clickOn("#pendingDateColumn");
//        assertEquals(cell("#pendingProcedureTable", 0, 1).getItem().toString(), "9999-12-12");
//        assertEquals(cell("#pendingProcedureTable", 0, 0).getItem(), "!");
//
//        //Delete the entry afterwards
//        clickOn(row("#pendingProcedureTable", 0));
//        clickOn("#deleteProcedureButton");
//        closeCurrentWindow();
//    }
//
//    /**
//     * Checks that a created procedure is able to be deleted.
//     */
//    @Test
//    public void deleteProcedureTest() {
//        //Open First Donor
//        clickOn("#searchTab");
//        doubleClickOn(row("#searchTable", 0));
//        Scene scene = getTopModalStage();
//
//        //Give user a new procedure named 'Generic procedure'.
//        clickOn(scene.lookup("#procedureTab"));
//        clickOn(scene.lookup("#addNewProcedureButton"));
//        Scene scene2 = getTopModalStage();
//        TextField procedureSummary = (TextField) scene2.lookup("#summaryField");
//        TextField procedureDate = (TextField) scene2.lookup("#dateOfProcedureField");
//        clickOn(procedureSummary).write("!");
//        clickOn(procedureDate).write("12-12-9999");
//        clickOn("#addButton");
//
//        //Check length of the table.
//        Integer tableLength = getTableView("#pendingProcedureTable").getItems().size();
//
//        //Delete the said procedure.
//        clickOn(row("#pendingProcedureTable", 0));
//        clickOn("#deleteProcedureButton");
//        Integer newTableLength = getTableView("#pendingProcedureTable").getItems().size();
//
//        //Check Deleted procedure is deleted.
//        assertTrue(tableLength == newTableLength + 1);
//        closeCurrentWindow();
//    }
//
//    /**
//     * @param tableSelector ID of the table that contains the cell wanted
//     * @param row           row number
//     * @param column        column number
//     * @return the cell of the table
//     */
//    protected TableCell<?, ?> cell(String tableSelector, int row, int column) {
//        List<Node> current = row(tableSelector, row).getChildrenUnmodifiable();
//        while (current.size() == 1 && !(current.get(0) instanceof TableCell)) {
//            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
//        }
//
//        Node node = current.get(column);
//        return (TableCell<?, ?>) node;
//    }
//
//    /**
//     * gets current stage with all windows.
//     *
//     * @return All of the current windows
//     */
//    private Scene getTopModalStage() {
//        // Get a list of windows but ordered from top[0] to bottom[n] ones.
//        // It is needed to get the first found modal window.
//        final List<Window> allWindows = new ArrayList<>(
//                robotContext().getWindowFinder().listWindows());
//        Collections.reverse(allWindows);
//
//        return allWindows.get(0).getScene();
//    }
//}
