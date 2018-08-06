package GUI;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import odms.controller.GuiMain;
import org.junit.*;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

@Ignore
public class ClinicianProfileGUITest extends TestFxMethods {

    /**
     * Runs tests in background if headless is set to true. This gets it working with the CI.
     */
    @BeforeClass
    public static void headless() throws TimeoutException {
        GUITestSetup.headless();
    }

    @Before
    public void loginUser() {
        loginAsClinician();
    }

    @After
    public void cleanup() {
        closeStages();
    }

    /**
     * Tests that a donor's profile can be opened by a clinician and that the name can be
     * successfully updated. The name is checked in the database and in the GUI to make sure it
     * updates. Changes the donor back to the original.
     */
    @Test
    public void editDonorNameTest() {
        // Open up the first donor
        openSearchedProfile("Ash Ketchup");

        Integer userId = getProfileIdFromWindow();

        // Opening edit tab
        clickOn("#editButton");

        // Editing donor
        clickOn("#givenNamesField");
        deleteLine();
        write("Bob");

        clickOn("#lastNamesField");
        deleteLine();
        write("Seger");

        //Button saveButton = (Button) scene2.lookup("#saveButton");
        clickOn("#saveButton");

        closeYesConfirmationDialogue();

        // Checks database has been updated
        assertEquals("Bob", GuiMain.getCurrentDatabase().getProfile(userId).getGivenNames());
        assertEquals("Seger", GuiMain.getCurrentDatabase().getProfile(userId).getLastNames());

        // Checks GUI has been updated.
        Scene scene = getTopScene();
        Label updatedGivenNames = (Label) scene.lookup("#givenNamesLabel");
        Label updatedLastNames = (Label) scene.lookup("#lastNamesLabel");
        assertEquals("Bob", updatedGivenNames.getText().substring(14));
        assertEquals("Seger", updatedLastNames.getText().substring(11));

        GuiMain.getCurrentDatabase().getProfile(userId).setGivenNames("Ash");
        GuiMain.getCurrentDatabase().getProfile(userId).setLastNames("Ketchup");

        saveDatabase();
    }
}
