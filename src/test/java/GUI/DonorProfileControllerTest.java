package GUI;


import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import odms.controller.ClinicianProfileController;
import odms.controller.GuiMain;
import odms.controller.LoginController;
import odms.medications.Drug;
import odms.profile.Profile;
import org.junit.*;
import org.testfx.api.FxToolkit;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TableViewMatchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.api.FxToolkit.setupScene;
import static org.testfx.api.FxToolkit.showStage;



public class DonorProfileControllerTest extends ApplicationTest {

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
     * Initializes the main gui and starts the program from the login screen.
     * @param stage current stage
     * @throws Exception throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception{
        guiMain = new GuiMain();
        guiMain.start(stage);
    }

    /**
     * logs in the donor and opens up the search tab
     */
    public void logInDonor() {
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

    @Test
    public void addMedicationTest() {
        //open up the first donor

        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile donor = (Profile) searchTable.getItems().get(1);
        doubleClickOn(row("#searchTable", 1));
        Scene scene = getTopModalStage();

        Label userIdLabel = (Label) scene.lookup("#userIdLabel");
        Integer userId = Integer.parseInt(userIdLabel.getText().substring(10)); //gets id of user being edited.

        clickOn(scene.lookup("#medicationsTab"));
        Scene scene2 = getTopModalStage();

        clickOn("#textFieldMedicationSearch").write("1 medication");
        clickOn(scene2.lookup("#buttonAddMedication"));

        assertEquals("1 medication", donor.getCurrentMedications().get(0).getDrugName());


    }


    @Test
    public void deleteMedicationTest() {
        //open up the first donor

        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile donor = (Profile) searchTable.getItems().get(1);
        doubleClickOn(row("#searchTable", 1));
        Scene scene = getTopModalStage();

        Label userIdLabel = (Label) scene.lookup("#userIdLabel");
        Integer userId = Integer.parseInt(userIdLabel.getText().substring(10)); //gets id of user being edited.

        clickOn(scene.lookup("#medicationsTab"));
        Scene scene2 = getTopModalStage();

        clickOn("#textFieldMedicationSearch").write("2 medication");
        clickOn(scene2.lookup("#buttonAddMedication"));

        TableView currentMedicationsTable = getTableView("#tableViewCurrentMedications");
        System.out.println(currentMedicationsTable.getItems());
        Drug firstDrug = (Drug) currentMedicationsTable.getItems().get(0);
        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn(scene2.lookup("#buttonDeleteMedication"));

        assert(!donor.getCurrentMedications().contains(firstDrug));


    }



    @Test
    public void moveMedicationToHistoricTest() {
        //open up the first donor

        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile donor = (Profile) searchTable.getItems().get(1);
        doubleClickOn(row("#searchTable", 1));
        Scene scene = getTopModalStage();

        Label userIdLabel = (Label) scene.lookup("#userIdLabel");
        Integer userId = Integer.parseInt(userIdLabel.getText().substring(10)); //gets id of user being edited.

        clickOn(scene.lookup("#medicationsTab"));
        Scene scene2 = getTopModalStage();

        clickOn("#textFieldMedicationSearch").write("3 medication");
        clickOn(scene2.lookup("#buttonAddMedication"));

        TableView currentMedicationsTable = getTableView("#tableViewCurrentMedications");
        Drug firstDrug = (Drug) currentMedicationsTable.getItems().get(0);
        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn(scene2.lookup("#buttonMedicationCurrentToHistoric"));

        assert(donor.getHistoryOfMedication().contains(firstDrug));


    }

    @Test
    public void moveMedicationToCurrentTest() {
        //open up the first donor

        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile donor = (Profile) searchTable.getItems().get(1);
        doubleClickOn(row("#searchTable", 1));
        Scene scene = getTopModalStage();

        Label userIdLabel = (Label) scene.lookup("#userIdLabel");
        Integer userId = Integer.parseInt(userIdLabel.getText().substring(10)); //gets id of user being edited.

        clickOn(scene.lookup("#medicationsTab"));
        Scene scene2 = getTopModalStage();

        clickOn("#textFieldMedicationSearch").write("4 medication");
        clickOn(scene2.lookup("#buttonAddMedication"));

        TableView currentMedicationsTable = getTableView("#tableViewCurrentMedications");
        clickOn(row("#tableViewCurrentMedications", 0));
        clickOn(scene2.lookup("#buttonMedicationCurrentToHistoric"));

        TableView historicMedicationsTable = getTableView("#tableViewHistoricMedications");
        Drug firstDrug = (Drug) historicMedicationsTable.getItems().get(0);
        clickOn(row("#tableViewHistoricMedications", 0));
        clickOn(scene2.lookup("#buttonMedicationHistoricToCurrent"));

        assert(donor.getCurrentMedications().contains(firstDrug));




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

    /**
     * gets current stage with all windows. Used to check that an alert controller has been created and is visible
     * @return All of the current windows
     */
    private javafx.stage.Stage getAlertDialogue() {
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


}
