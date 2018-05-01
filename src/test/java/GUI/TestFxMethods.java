package GUI;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import odms.controller.GuiMain;
import odms.tools.TestDataCreator;
import org.junit.After;
import org.junit.BeforeClass;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

abstract class TestFxMethods extends ApplicationTest {

    private GuiMain guiMain;

    @After()
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
        guiMain = new GuiMainDummy();
        guiMain.setCurrentDatabase(new TestDataCreator().getDatabase());
        guiMain.start(stage);
    }


    /**
     * logs in the clinician and opens up the search tab
     */
    protected void loginAsClinician() {
        clickOn("#usernameField").write("0");
        clickOn("#loginButton");
    }


    /**
     * gets current stage with all windows.
     * @return All of the current windows
     */
    protected javafx.scene.Scene getTopScene() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        // It is needed to get the first found modal window.
        final List<Window> allWindows = new ArrayList<>(robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        return (javafx.scene.Scene) allWindows.get(0).getScene();
    }

    /**
     * gets current stage with all windows. Used to check that an alert controller has been created and is visible
     * @return All of the current windows
     */
    protected javafx.stage.Stage getAlertDialogue() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        // It is needed to get the first found modal window.
        final List<Window> allWindows = new ArrayList<>(robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        return (javafx.stage.Stage) allWindows
                .stream()
                .filter(window -> window instanceof javafx.stage.Stage)
                .filter(window -> ((javafx.stage.Stage) window).getModality() == Modality.APPLICATION_MODAL)
                .findFirst()
                .orElse(null);
    }

    /**
     * @param tableSelector The id of the table to be used
     * @return Returns a table view node from the given ID
     */
    protected TableView<?> getTableView(String tableSelector) {
        Node node = lookup(tableSelector).queryTableView();
        if (!(node instanceof TableView)) {
        }
        return (TableView<?>) node;
    }

    /**
     * @param tableSelector The id of the table that contains the cell wanted
     * @param row           row number
     * @param column        column number
     * @return returns the cell data.
     */
    protected Object cellValue(String tableSelector, int row, int column) {
        return getTableView(tableSelector).getColumns().get(column).getCellData(row);
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
}
