package TestFX;

import java.util.concurrent.TimeoutException;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.junit.After;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

public abstract class TestFXTest extends ApplicationTest {

    private final static Boolean headlessMode = false;

    protected static void setupTestFX() {
        if (headlessMode) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("testfx.setup.timeout", "2500");
            System.setProperty("headless.geometry", "1920x1080-32");
        } else {
            System.setProperty("testfx.robot.write_sleep", "10");
        }

    }

    @After
    public void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
        FxToolkit.cleanupStages();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

}
