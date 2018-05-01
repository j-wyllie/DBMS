package odms.tools;

import GUI.GuiMainDummy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import odms.controller.GuiMain;
import org.junit.After;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.concurrent.TimeoutException;

abstract class TestFXTest extends ApplicationTest {

    TestDataCreator testDataCreator;

    @Override
    public void start(Stage stage) throws Exception {
        testDataCreator = new TestDataCreator();
        GuiMain guiMain = new GuiMainDummy();
        guiMain.setCurrentDatabase(testDataCreator.getDatabase());
        guiMain.start(stage);
    }

    @After
    public void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
        testDataCreator = new TestDataCreator();
    }




}