package GUI;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import odms.controller.AlertController;
import odms.controller.DonorProfileController;
import odms.controller.GuiMain;
import odms.controller.LoginController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoginControllerTest extends ApplicationTest {

    private GuiMain guiMain;



    //Runs tests in background if headless is set to true. This gets it working with the CI.
    static {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
        }
    }

    @After()
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});

    }

    /**
     * Initializes the main gui and starts the program from the login screen.
     * @param stage current stage
     * @throws Exception throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception{
        guiMain = new GuiMain();
        guiMain.start(stage);
    }

    /**
     * tests that the current donor is set when a successful username is entered
     */
    @Test
    public void loginValidUser(){
        clickOn("#usernameField");
        write("1");
        login();
        assertEquals("1", LoginController.getCurrentDonor().getId().toString());
    }

    /**
     * Tests that a popup is shown when an invalid username is entered
     */
    @Test
    public void loginInvalidUser(){
        clickOn("#usernameField").write("1234");
        login();
        final javafx.stage.Stage actualAlertDialog = getTopModalStage();
        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(dialogPane.getContentText(), "Please enter a valid username.");
        closeDialogue(dialogPane);
    }

    /**
     * creates a valid user and adds it to the database.
     */
    @Test
    public void createValidUser(){
        clickOn("#createAccountLink");
        clickOn("#givenNamesField").write("Jack Travis");
        clickOn("#surnamesField").write("Hay");
        clickOn("#dobField").write("14-11-1997");
        clickOn("#irdField").write("88888888");
        clickOn("#createAccountButton");

        assertEquals("88888888", LoginController.getCurrentDonor().getIrdNumber().toString());
    }

    @Test
    public void createInvalidUser(){

        //tests empty fields
        clickOn("#createAccountLink");
        clickOn("#createAccountButton");

        javafx.stage.Stage actualAlertDialog = getTopModalStage();
        DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(dialogPane.getContentText(), "Please enter your details correctly.");
        closeDialogue(dialogPane);

        //tests invalid date format
        clickOn("#givenNamesField").write("Jack Travis");
        clickOn("#surnamesField").write("Hay");
        clickOn("#dobField").write("14.11.1997");
        clickOn("#irdField").write("123456788");
        clickOn("#createAccountButton");

        actualAlertDialog = getTopModalStage();
        dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(dialogPane.getContentText(), "Date entered is not in the format dd-mm-yyyy.");
        closeDialogue(dialogPane);

        //tests duplicate IRD number.
        clickOn("#dobField").eraseText(10).write("14-11-1997");
        clickOn("#createAccountButton");

        actualAlertDialog = getTopModalStage();
        dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(dialogPane.getContentText(), "Please enter a valid IRD number.");
        closeDialogue(dialogPane);

        //tests empty IRD field.
        clickOn("#irdField").eraseText(9);
        clickOn("#createAccountButton");

        actualAlertDialog = getTopModalStage();
        dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(dialogPane.getContentText(), "Please enter your details correctly.");
        closeDialogue(dialogPane);

    }

    /**
     * Clicks on the login button to log the user in.
     */
    private void login(){
        clickOn("#loginButton").write("1");
    }


    /**
     * Closes the currently open alert dialogue
     * @param alert the alert DialogPane to be closed
     */
    private void closeDialogue(DialogPane alert){
        robotContext();
        Button closeButton = (Button) alert.lookupButton(ButtonType.CLOSE);
        closeButton.setId("Close");
        clickOn("#Close");
    }

    /**
     * gets current stage with all windows. Used to check that an alert controller has been created and is visible
     * @return All of the current windows
     */
    private javafx.stage.Stage getTopModalStage() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        // It is needed to get the first found modal window.
        final List<Window> allWindows = new ArrayList<>(robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        return (javafx.stage.Stage) allWindows
                .stream()
                .filter(window -> window instanceof javafx.stage.Stage)
                .filter(window -> ((javafx.stage.Stage) window).getModality() == Modality.APPLICATION_MODAL)
                .findFirst()
                .orElse(null);
    }

}
