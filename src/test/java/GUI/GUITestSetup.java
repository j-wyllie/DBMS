package GUI;

public class GUITestSetup {
    public static void headless() {
        {
            System.setProperty("prism.verbose", "true"); // optional
            System.setProperty("java.awt.headless", "true");
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("glass.platform", "Monocle");
            System.setProperty("monocle.platform", "Headless");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("testfx.setup.timeout", "2500");
            System.setProperty("headless.geometry", "1600x1200-32");
        }
    }

}
