package GUI;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import org.junit.*;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

public class DonorEditProfileGUITest extends TestFxMethods {
    private final String errorRequiredFieldsString = "Error. Required fields were left blank.";
    private final String errorNotAllFieldsUpdatedString = "Error. Not all fields were updated.";

    private javafx.stage.Stage alertDialog;
    private DialogPane dialogPane;

    /**
     * Runs tests in background if headless is set to true. This gets it working with the CI.
     */
    @BeforeClass
    public static void headless() throws TimeoutException {
        GUITestSetup.headless();
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
        assertEquals(errorRequiredFieldsString, dialogPane.getContentText());
        closeDialog(dialogPane);

        clickOn(scene.lookup("#givenNamesField"));
        write("Bob");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        // Checks GUI has been updated.
        Scene scene2 = getTopScene();
        Label updatedGivenNames = (Label) scene2.lookup("#givenNamesLabel");
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
        assertEquals(errorRequiredFieldsString, dialogPane.getContentText());
        closeDialog(dialogPane);

        clickOn(scene.lookup("#lastNamesField"));
        write("Seger");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        // Checks GUI has been updated.
        Scene scene2 = getTopScene();
        Label updatedGivenNames = (Label) scene2.lookup("#lastNamesLabel");
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
        assertEquals(errorRequiredFieldsString, dialogPane.getContentText());
        closeDialog(dialogPane);

        clickOn(scene.lookup("#dobField"));
        write("abcdefg");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        alertDialog = getAlertDialogue();
        dialogPane = (DialogPane) alertDialog.getScene().getRoot();
        assertEquals(errorNotAllFieldsUpdatedString, dialogPane.getContentText());
        closeDialog(dialogPane);

        clickOn(scene.lookup("#dobField"));
        deleteLine();
        write("14-11-1997");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        // Checks GUI has been updated.
        Scene scene2 = getTopScene();
        Label updatedDob = (Label) scene2.lookup("#dobLabel");
        assertEquals("14-11-1997", updatedDob.getText().substring(16));
    }

    @Test
    public void editDateOfDeathTest() {
        Scene scene = getTopScene();
        clickOn(scene.lookup("#dodField"));
        deleteLine();

        clickOn(scene.lookup("#dodField"));
        write("abcdefg");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        alertDialog = getAlertDialogue();
        dialogPane = (DialogPane) alertDialog.getScene().getRoot();
        assertEquals(errorNotAllFieldsUpdatedString, dialogPane.getContentText());
        closeDialog(dialogPane);

        clickOn(scene.lookup("#dodField"));
        deleteLine();
        write("14-11-2100");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        alertDialog = getAlertDialogue();
        dialogPane = (DialogPane) alertDialog.getScene().getRoot();
        assertEquals(errorNotAllFieldsUpdatedString, dialogPane.getContentText());
        closeDialog(dialogPane);

        clickOn(scene.lookup("#dodField"));
        deleteLine();
        write("14-11-1700");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        alertDialog = getAlertDialogue();
        dialogPane = (DialogPane) alertDialog.getScene().getRoot();
        assertEquals(errorNotAllFieldsUpdatedString, dialogPane.getContentText());
        closeDialog(dialogPane);

        clickOn(scene.lookup("#dodField"));
        deleteLine();
        write(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        // Checks GUI has been updated.
        Scene scene2 = getTopScene();
        Label updatedDod = (Label) scene2.lookup("#dodLabel");
        assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                updatedDod.getText().substring(16)
        );
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
