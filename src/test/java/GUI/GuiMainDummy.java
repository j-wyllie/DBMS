package GUI;

import odms.controller.GuiMain;
import odms.data.ProfileDatabase;
import odms.tools.TestDataCreator;

public class GuiMainDummy extends GuiMain {
    private static ProfileDatabase profileDb = new TestDataCreator().getDatabase();
}
