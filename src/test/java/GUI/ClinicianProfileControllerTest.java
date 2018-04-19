package GUI;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import odms.controller.ClinicianProfileController;
import odms.controller.GuiMain;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TableViewMatchers;

import java.awt.*;
import java.util.List;

import static org.testfx.api.FxToolkit.registerPrimaryStage;

public class ClinicianProfileControllerTest extends ApplicationTest {

    private GuiMain guiMain;

    //Runs tests in background if headless is set to true. This gets it working with the CI.

    @BeforeClass
    public static void setupSpec() throws Exception {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
        registerPrimaryStage();
    }


    @After()
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    /**
     * logs in the clinician and opens up the search tab
     */
    public void logInClinician() {
        clickOn("#usernameField").write("0");
        clickOn("#loginButton");
    }

    @Test
    public void openSearchedProfileTest() {
        logInClinician();
        clickOn("#searchTab");
        TableView searchDonorTable = lookup("#searchTable").queryTableView();
        System.out.println(cellValue("#searchTable", 1, 1));
        clickOn(searchDonorTable);
    }

    @Test
    public void editSearchedProfileTest() {

    }


    /**
     * @param tableSelector Ein CSS-Selektor oder Label.
     * @return Die TableView, sofern sie anhand des Selektors gefunden wurde.
     */
    private TableView<?> getTableView(String tableSelector) {
        Node node = lookup(tableSelector).queryTableView();
        if (!(node instanceof TableView)) {
        }
        return (TableView<?>) node;
    }

    /**
     * @param tableSelector Selektor zur Identifikation der Tabelle.
     * @param row Zeilennummer
     * @param column Spaltennummer
     * @return Der Wert der gegebenen Zelle in der Tabelle. Es handelt sich nicht um das, was auf der UI dransteht,
     *         sondern um den Wert, also nicht notwendigerweise ein String.
     */
    protected Object cellValue(String tableSelector, int row, int column) {
        return getTableView(tableSelector).getColumns().get(column).getCellData(row);
    }

    /**
     * @param tableSelector Selektor zur Identifikation der Tabelle.
     * @param row Zeilennummer
     * @return Die entsprechende Zeile.
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
        if (node instanceof TableRow) {
            return (TableRow<?>) node;
        } else {
            throw new RuntimeException("Expected Group with only TableRows as children");
        }
    }

    /**
     * @param tableSelector Selektor zur Identifikation der Tabelle.
     * @param row Zeilennummer
     * @param column Spaltennummer
     * @return Die entsprechende Zelle.
     */
    protected TableCell<?, ?> cell(String tableSelector, int row, int column) {
        List<Node> current = row(tableSelector, row).getChildrenUnmodifiable();
        while (current.size() == 1 && !(current.get(0) instanceof TableCell)) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        Node node = current.get(column);
        if (node instanceof TableCell) {
            return (TableCell<?, ?>) node;
        } else {
            throw new RuntimeException("Expected TableRowSkin with only TableCells as children");
        }
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
}
