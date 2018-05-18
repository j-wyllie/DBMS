package GUI;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import odms.controller.GuiMain;
import org.junit.*;
import org.testfx.api.FxToolkit;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

/**
 * Tests for checking that the correct file was imported by the admin.
 * Can't be run in headless mode, as the file chooser is not supported.
 * Run these tests manually before you push.
 */
@Ignore
public class AdminProfileTests extends TestFxMethods{

    @BeforeClass
    public static void headless() throws TimeoutException {
        //GUITestSetup.headless();
    }

    @After()
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        FxToolkit.cleanupStages();
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

    @Before
    public void loginAdmin() {
        clickOn("#usernameField").write("admin");
        clickOn("#passwordField").write("admin");

        clickOn("#loginButton");
    }

    @Test
    public void testLoadingDatabaseRefreshesAdmin() throws Exception {
        clickOn("#dataManagementTab");
        clickOn("#buttonImport");
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        sleep(100);
        press(KeyCode.ENTER).release(KeyCode.ENTER);         //remove this line when story finished


        Scene scene = getTopScene();
        Label adminLabel = (Label) scene.lookup("#givenNamesLabel");

        assertEquals("Given names : admin", adminLabel.getText());
    }

    @Test
    public void testCorrectDataImported() throws Exception {
        clickOn("#dataManagementTab");
        clickOn("#buttonImport");
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        sleep(100);
        press(KeyCode.ENTER).release(KeyCode.ENTER);         //remove this line when story finished

        clickOn("#searchTab");
        doubleClickOn(row("#searchTable", 0));

        Scene scene = getTopScene();
        Label fullName = (Label) scene.lookup("#donorFullNameLabel");
        assertEquals("Queef Ketchup", fullName.getText());
    }




}
