//package GUI;
//
//import java.util.concurrent.TimeoutException;
//
//import static org.testfx.api.FxToolkit.registerPrimaryStage;
//
//public final class GUITestSetup {
//
//    private GUITestSetup() {
//        throw new UnsupportedOperationException();
//    }
//
//    /**
//     * Configure TestFX tests to run in headless mode
//     */
//    public static void headless() {
//        try {
//            System.setProperty("testfx.robot", "glass");
//            System.setProperty("testfx.headless", "true");
//            System.setProperty("prism.order", "sw");
//            System.setProperty("prism.text", "t2k");
//            System.setProperty("testfx.setup.timeout", "2500");
//            System.setProperty("headless.geometry", "1920x1080-32");
//
//            registerPrimaryStage();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
