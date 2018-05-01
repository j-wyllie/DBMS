package GUI;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import odms.controller.GuiMain;
import odms.profile.Profile;
import odms.tools.TestDataCreator;
import org.junit.*;
import org.testfx.api.FxToolkit;

import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class DonorEditProfileGUITest extends TestFxMethods {

    javafx.stage.Stage alertDialog;
    DialogPane dialogPane;
    /**
     * Runs tests in background if headless is set to true. This gets it working with the CI.
     */
    @BeforeClass
    public static void headless() throws TimeoutException {
        //GUITestSetup.headless();
    }


    @Before
    public void loginUser() {
        loginAsDonor(1);
        Scene scene = getTopScene();
        clickOn((scene.lookup("#editButton")));
    }

    @Test
    public void editGivenNames() {
        Scene scene = getTopScene();
        clickOn(scene.lookup("#givenNamesField"));
        deleteLine();
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        alertDialog = getAlertDialogue();
        dialogPane = (DialogPane) alertDialog.getScene().getRoot();
        assertEquals("Error. Essential fields were left blank.", dialogPane.getContentText());
        closeDialog(dialogPane);

        clickOn(scene.lookup("#givenNamesField"));
        write("Bob");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        // Checks GUI has been updated.
        Scene scene2 = getTopScene();
        Label updatedGivenNames = (Label) scene2.lookup("#givenNamesLabel");
        System.out.println(updatedGivenNames.getText());
        assertEquals("Bob", updatedGivenNames.getText().substring(14));
    }

    @Test
    public void editLastNames() {
        Scene scene = getTopScene();
        clickOn(scene.lookup("#lastNamesField"));
        deleteLine();
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        alertDialog = getAlertDialogue();
        dialogPane = (DialogPane) alertDialog.getScene().getRoot();
        assertEquals("Error. Essential fields were left blank.", dialogPane.getContentText());
        closeDialog(dialogPane);

        clickOn(scene.lookup("#lastNamesField"));
        write("Seger");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        // Checks GUI has been updated.
        Scene scene2 = getTopScene();
        Label updatedGivenNames = (Label) scene2.lookup("#lastNamesLabel");
        System.out.println(updatedGivenNames.getText());
        assertEquals("Seger", updatedGivenNames.getText().substring(11));
    }

    @Test
    public void editPhoneTest() {

    }

    @Test
    public void editEmailTest() {

    }

    @Test
    public void editAddressTest() {

    }

    @Test
    public void editRegionTest() {

    }

    @Test
    public void editIRDTest() {

    }

    @Test
    public void editDateOfBirthTest() {
        Scene scene = getTopScene();
        clickOn(scene.lookup("#dobField"));
        deleteLine();
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        alertDialog = getAlertDialogue();
        dialogPane = (DialogPane) alertDialog.getScene().getRoot();
        assertEquals("Error. Essential fields were left blank.", dialogPane.getContentText());
        closeDialog(dialogPane);

        clickOn(scene.lookup("#dobField"));
        write("abcdefg");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        alertDialog = getAlertDialogue();
        dialogPane = (DialogPane) alertDialog.getScene().getRoot();
        assertEquals("Error. Not all fields were updated.", dialogPane.getContentText());
        closeDialog(dialogPane);

        clickOn(scene.lookup("#dobField"));
        deleteLine();
        write("14-11-1997");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        // Checks GUI has been updated.
        Scene scene2 = getTopScene();
        Label updatedGivenNames = (Label) scene2.lookup("#dobLabel");
        System.out.println(updatedGivenNames.getText());
        assertEquals("14-11-1997", updatedGivenNames.getText().substring(16));
    }

    @Test
    public void editDateOfDeathTest() {

    }

    @Test
    public void editAgeTest() {

    }

    @Test
    public void editGenderTest() {

    }

    @Test
    public void editHeightTest() {

    }

    @Test
    public void editWeightTest() {

    }

    @Test
    public void editBloodTypeTest() {

    }

    @Test
    public void editSmokerTest() {

    }

    @Test
    public void editAlcoholConsumptionTest() {

    }

    @Test
    public void editBloodPressureTest() {

    }

    @Test
    public void editChronicDiseasesTest() {

    }

    @Test
    public void editOrgansToDonateTest() {

    }

    @Test
    public void editPastDonationsTest() {

    }

    @Test
    public void validateBMITest() {

    }
}
