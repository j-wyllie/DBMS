package GUI;

import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxToolkit.registerPrimaryStage;

public class GUITestSetup {
    public static void headless() throws TimeoutException{
        {
            System.setProperty("prism.verbose", "true"); //
            System.setProperty("java.awt.headless", "true");
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("glass.platform", "Monocle");
            System.setProperty("monocle.platform", "Headless");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("testfx.setup.timeout", "2500");
            System.setProperty("headless.geometry", "1920x1080-32");
        }
        registerPrimaryStage();
    }

}
