//package GUI;
//
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.MouseButton;
//import javafx.stage.Stage;
//import odms.controller.GuiMain;
//import org.junit.After;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.testfx.api.FxToolkit;
//
//import java.util.concurrent.TimeoutException;
//
//import static org.junit.Assert.assertEquals;
//
//public class UserLoginTest extends TestFxMethods {
//
//    //Runs tests in background if headless is set to true. This gets it working with the CI.
//    @BeforeClass
//    public static void headless() throws TimeoutException {
//        GUITestSetup.headless();
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
//        GuiMain guiMain = new GuiMain();
//        guiMain.start(stage);
//    }
//
//    @Test
//    public void testBlankPassword() {
//        clickOn("#usernameField").write("admin");
//
//        press(KeyCode.ENTER);
//
//        Stage alertDialog = getAlertDialogue();
//        assertEquals("Error", alertDialog.getTitle());
//    }
//
//    @Test
//    public void testIncorrectPassword() {
//        clickOn("#usernameField").write("admin");
//        clickOn("#passwordField").write("incorrect");
//
//        press(KeyCode.ENTER);
//
//        Stage alertDialog = getAlertDialogue();
//        assertEquals("Error", alertDialog.getTitle());
//    }
//
//    @Test
//    public void testCorrectPassword() {
//        clickOn("#usernameField").write("admin");
//        clickOn("#passwordField").write("admin");
//
//        clickOn("#loginButton");
//        Scene scene = getTopScene();
//
//        Label adminLabel = (Label) scene.lookup("#donorStatusLabel");
//
//        assertEquals("administrator", adminLabel.getText());
//    }
//
//    @Test
//    public void testClinicianLogin() {
//        clickOn("#usernameField").write("0");
//
//        clickOn("#loginButton");
//        Scene scene = getTopScene();
//
//        Label clinicianLabel = (Label) scene.lookup("#donorStatusLabel");
//
//        assertEquals("clinician", clinicianLabel.getText());
//    }
//}
