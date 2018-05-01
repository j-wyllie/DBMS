package GUI;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import odms.controller.GuiMain;
import odms.controller.LoginController;
import odms.data.ProfileDataIO;
import odms.tools.TestDataCreator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.osgi.service.TestFx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class LoginCreateAccountGUITest extends TestFxMethods {

    private GuiMain guiMain;


    //Runs tests in background if headless is set to true. This gets it working with the CI.
    @BeforeClass
    public static void headless() throws TimeoutException {
        GUITestSetup.headless();
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
        guiMain = new GuiMainDummy();
        guiMain.setCurrentDatabase(new TestDataCreator().getDatabase());
        guiMain.start(stage);
    }

    /**
     * tests that the current donor is set when a successful username is entered
     */
    @Test
    public void loginValidUser(){
        clickOn("#usernameField").write("1");
        login();
        assertEquals("1", LoginController.getCurrentProfile().getId().toString());
    }

    /**
     * Tests that a popup is shown when an invalid username is entered
     */
    @Test
    public void loginInvalidUser(){
        clickOn("#usernameField").write("1234");
        login();
        final javafx.stage.Stage actualAlertDialog = getAlertDialogue();
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

        Scene newScene= getTopScene();
        Label userFullName = (Label) newScene.lookup("#donorFullNameLabel");
        Label userId = (Label) newScene.lookup("#userIdLabel");
        Integer id = Integer.parseInt(userId.getText().substring(10, userId.getText().length()));
        assertEquals("Jack Travis Hay", userFullName.getText());
        ProfileDataIO profileDataIO = new ProfileDataIO();
        guiMain.getCurrentDatabase().deleteProfile(id);
        profileDataIO.saveData(guiMain.getCurrentDatabase(), "example/example.json");
    }

    /**
     * creates a user with invalid fields. Checks that the correct popups
     * are displayed.
     */
    @Test
    public void createInvalidUser(){

        //tests empty fields
        clickOn("#createAccountLink");
        clickOn("#createAccountButton");

        javafx.stage.Stage actualAlertDialog = getAlertDialogue();
        DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(dialogPane.getContentText(), "Please enter your details correctly.");
        closeDialogue(dialogPane);

        //tests invalid date format
        clickOn("#givenNamesField").write("Jack Travis");
        clickOn("#surnamesField").write("Hay");
        clickOn("#dobField").write("14.11.1997");
        clickOn("#irdField").write("100132122");
        clickOn("#createAccountButton");

        actualAlertDialog = getAlertDialogue();
        dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals("Date entered is not in the format dd-mm-yyyy.", dialogPane.getContentText());
        closeDialogue(dialogPane);

        //tests duplicate IRD number.
        clickOn("#dobField").eraseText(10).write("14-11-1997");
        clickOn("#createAccountButton");

//        actualAlertDialog = getTopModalStage();
//        dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
//        assertEquals(dialogPane.getContentText(), "Please enter a valid IRD number.");
//        closeDialogue(dialogPane);
//
//        //tests empty IRD field.
//        clickOn("#irdField").eraseText(9);
//        clickOn("#createAccountButton");
//
//        actualAlertDialog = getTopModalStage();
//        dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
//        assertEquals(dialogPane.getContentText(), "Please enter your details correctly.");
//        closeDialogue(dialogPane);

    }

    /**
     * Clicks on the login button to log the user in.
     */
    private void login(){
        clickOn("#loginButton");
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
}
