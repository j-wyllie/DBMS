package GUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class DonorProfileControllerTest extends ApplicationTest {
    private GuiMain guiMain;
    private Parent root;

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
    protected javafx.scene.Scene getTopModalStage() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        // It is needed to get the first found modal window.
        final List<Window> allWindows = new ArrayList<>(robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        return (javafx.scene.Scene) allWindows.get(0).getScene();
    }
}
