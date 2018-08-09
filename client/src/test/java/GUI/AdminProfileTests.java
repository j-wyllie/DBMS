//package GUI;
//
//import javafx.scene.Scene;
//import javafx.scene.control.DialogPane;
//import javafx.scene.control.Label;
//import javafx.scene.control.TableView;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.MouseButton;
//import javafx.stage.Stage;
//import odms.controller.GuiMain;
//import odms.model.profile.Profile;
//import org.junit.*;
//import org.testfx.api.FxRobot;
//import org.testfx.api.FxToolkit;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * Tests for checking that the correct file was imported by the admin. Can't be run in headless
// * mode, as the file chooser is not supported. Run these tests manually before you push.
// */
//@Ignore
//public class AdminProfileTests extends TestFxMethods {
//
//    @BeforeClass
//    public static void headless() {
//        //GUITestSetup.headless();
//    }
//
//    @After()
//    public void tearDown() throws Exception {
//        FxToolkit.hideStage();
//        FxToolkit.cleanupStages();
//        release(new KeyCode[]{});
//        release(new MouseButton[]{});
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
//        guiMain = new GuiMain();
//        guiMain.start(stage);
//    }
//
//    @Before
//    public void loginAdmin() {
//        clickOn("#usernameField").write("admin");
//        clickOn("#passwordField").write("admin");
//
//        clickOn("#loginButton");
//    }
//
//    @Test
//    public void testLoadingDatabaseRefreshesAdmin() throws Exception {
//        clickOn("#dataManagementTab");
//        clickOn("#buttonImport");
//        press(KeyCode.DOWN).release(KeyCode.DOWN);
//        press(KeyCode.ENTER).release(KeyCode.ENTER);
//        press(KeyCode.DOWN).release(KeyCode.DOWN);
//        press(KeyCode.ENTER).release(KeyCode.ENTER);
//
//        sleep(100);
//        press(KeyCode.ENTER).release(KeyCode.ENTER);         //remove this line when story finished
//
//        Scene scene = getTopScene();
//        Label adminLabel = (Label) scene.lookup("#givenNamesLabel");
//
//        assertEquals("Given names : admin", adminLabel.getText());
//    }
//
//    @Test
//    public void testDataImportedNoUnsavedChanges() throws Exception {
//        clickOn("#dataManagementTab");
//        clickOn("#buttonImport");
//        press(KeyCode.DOWN).release(KeyCode.DOWN);
//        press(KeyCode.ENTER).release(KeyCode.ENTER);
//        press(KeyCode.DOWN).release(KeyCode.DOWN);
//        press(KeyCode.ENTER).release(KeyCode.ENTER);
//
//        sleep(100);
//
//        clickOn("#searchTab");
//        doubleClickOn(row("#searchTable", 0));
//
//        Scene scene = getTopScene();
//        Label fullName = (Label) scene.lookup("#donorFullNameLabel");
//        assertEquals("Queef Ketchup", fullName.getText());
//    }
//
//    @Test
//    public void testDataImportWithUnsavedChanges() throws Exception {
//        FxRobot fxRobot = new FxRobot();
//
//        Stage stage1 = (Stage) getTopScene().getWindow();
//
//        clickOn("#searchTab");
//
//        //Open a profile
//        TableView searchTable = getTableView("#searchTable");
//        Profile profile = (Profile) searchTable.getItems().get(0);
//        doubleClickOn(row("#searchTable", 0));
//
//        clickOn("#medicationsTab");
//        clickOn("#textFieldMedicationSearch").write("asp");
//        sleep(1300); //wait for the api to do the request
//        press(KeyCode.DOWN).release(KeyCode.DOWN);
//        sleep(100);
//        press(KeyCode.ENTER).release(KeyCode.ENTER);
//        press(KeyCode.ESCAPE).release(KeyCode.ESCAPE);
//
//        clickOn("#addMedicationButton");
//        sleep(100);
//
//        fxRobot.interact(stage1::toFront);
//        clickOn("#dataManagementTab");
//        clickOn("#buttonImport");
//        press(KeyCode.DOWN).release(KeyCode.DOWN);
//        press(KeyCode.ENTER).release(KeyCode.ENTER);
//        press(KeyCode.DOWN).release(KeyCode.DOWN);
//        press(KeyCode.ENTER).release(KeyCode.ENTER);
//
//        sleep(1000);
//
//        Scene alertDialog = getTopScene();
//        DialogPane dialogPane = (DialogPane) alertDialog.getRoot();
//        assertEquals("You have unsaved changes.\n" +
//                "Do you want to continue without saving?", dialogPane.getContentText());
//    }
//}
