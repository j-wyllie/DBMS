package GUI;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import odms.controller.GuiMain;
import odms.controller.LoginController;
import odms.data.ProfileDataIO;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeoutException;

public class LoginCreateAccountGUITest extends TestFxMethods {

    //Runs tests in background if headless is set to true. This gets it working with the CI.
    @BeforeClass
    public static void headless() throws TimeoutException {
        GUITestSetup.headless();
    }

    @After
    public void cleanup() {
        closeStages();
    }

    /**
     * tests that the current donor is set when a successful username is entered
     */
    @Test
    public void loginValidUser(){
        clickOn("#usernameField").write("1");
        clickOn("#loginButton");
        assertEquals("1", LoginController.getCurrentProfile().getId().toString());
    }

    /**
     * Tests that a popup is shown when an invalid username is entered
     */
    @Test
    public void loginInvalidUser(){
        clickOn("#usernameField").write("1234");
        clickOn("#loginButton");
        final javafx.stage.Stage actualAlertDialog = getAlertDialogue();
        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(dialogPane.getContentText(), "Please enter a valid username.");
        closeDialog(dialogPane);
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

        assertEquals("Jack Travis Hay", userFullName.getText());

        GuiMain.getCurrentDatabase().deleteProfile(getProfileIdFromWindow());

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
        closeDialog(dialogPane);

        //tests invalid date format
        clickOn("#givenNamesField").write("Jack Travis");
        clickOn("#surnamesField").write("Hay");
            //clickOn("#dobField").write("14.11.1997");
        clickOn("#irdField").write("100132122");
        clickOn("#createAccountButton");

        actualAlertDialog = getAlertDialogue();
        dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals("Please enter your details correctly.", dialogPane.getContentText());
        closeDialog(dialogPane);

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

}
