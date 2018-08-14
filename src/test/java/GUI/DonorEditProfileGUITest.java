package GUI;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import org.junit.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

@Ignore
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
        sleep(500);

        clickOn("#editButton");
        sleep(500);
    }

    @After
    public void cleanup() {
        closeStages();
    }

    @Ignore
    @Test
    public void editGivenNames() {
        clickOn("#editButton");
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
        clickOn("#phoneField");
        deleteLine();
        write("0276666666");
        clickOn("#saveButton");
        sleep(3000);
        closeYesConfirmationDialogue();

        Scene scene = getTopScene();
        String phoneText = ((Label) scene.lookup("#phoneLabel")).getText().substring(8);
        assertEquals("0276666666", phoneText);
    }

    @Test
    public void editEmailTest() {
        clickOn("#emailField");
        deleteLine();
        write("test@test.com");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        Scene scene = getTopScene();
        String emailText = ((Label) scene.lookup("#emailLabel")).getText().substring(8);
        assertEquals("test@test.com", emailText);
    }

    @Ignore
    public void editAddressTest() {

    }

    @Ignore
    public void editRegionTest() {

    }

    @Ignore
    public void editNHITest() {

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
    public void editGenderTest() {
        Scene scene = getTopScene();
        clickOn(scene.lookup("#genderField"));
        deleteLine();
        write("Male");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        scene = getTopScene();
        Label updatedGender = (Label) scene.lookup("#genderLabel");
        assertEquals("Male", updatedGender.getText().substring(9));
    }

    @Test
    public void editHeightTest() {

        Scene scene = getTopScene();
        clickOn(scene.lookup("#heightField"));
        deleteLine();
        write("aaa");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        alertDialog = getAlertDialogue();
        dialogPane = (DialogPane) alertDialog.getScene().getRoot();
        assertEquals(errorNotAllFieldsUpdatedString, dialogPane.getContentText());
        closeDialog(dialogPane);

        clickOn(scene.lookup("#heightField"));
        deleteLine();
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        scene = getTopScene();
        Label updatedHeight = (Label) scene.lookup("#heightLabel");
        assertEquals("", updatedHeight.getText().substring(9));

        clickOn("#editButton");
        clickOn("#heightField");
        write("1.65");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        scene = getTopScene();
        updatedHeight = (Label) scene.lookup("#heightLabel");
        assertEquals("1.65m", updatedHeight.getText().substring(9));
    }

    @Test
    public void editWeightTest() {
        Scene scene = getTopScene();
        clickOn(scene.lookup("#weightField"));
        deleteLine();
        write("aaa");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        alertDialog = getAlertDialogue();
        dialogPane = (DialogPane) alertDialog.getScene().getRoot();
        assertEquals(errorNotAllFieldsUpdatedString, dialogPane.getContentText());
        closeDialog(dialogPane);

//        clickOn(scene.lookup("#weightField"));
//        deleteLine();
//        clickOn("#saveButton");
//        closeYesConfirmationDialogue();
//
//        scene = getTopScene();
//        Label updatedWeight = (Label) scene.lookup("#weightLabel");
//        assertEquals("", updatedWeight.getText().substring(9));

        //clickOn("#editButton");
        clickOn("#weightField");
        deleteLine();
        write("70");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        scene = getTopScene();
        Label updatedWeight = (Label) scene.lookup("#weightLabel");
        assertEquals("70.0kg", updatedWeight.getText().substring(9));
    }

    @Ignore
    public void editBloodTypeTest() {

    }

    @Test
    public void editSmokerTest() {
        Scene scene;

        clickOn("#editMedicalTab");
        clickOn("#smokerTrueRadio");
        clickOn("#saveMedicalButton");
        closeYesConfirmationDialogue();
        clickOn("#medicalTab");

        scene = getTopScene();
        Label updatedSmoker = (Label) scene.lookup("#smokerLabel");
        assertEquals("true", updatedSmoker.getText().substring(9));

        clickOn("#generalTab");
        clickOn("#editButton");
        clickOn("#editMedicalTab");
        clickOn("#smokerFalseRadio");
        clickOn("#saveMedicalButton");
        closeYesConfirmationDialogue();
        clickOn("#medicalTab");

        scene = getTopScene();
        updatedSmoker = (Label) scene.lookup("#smokerLabel");
        assertEquals("false", updatedSmoker.getText().substring(9));
    }

    @Ignore
    public void editAlcoholConsumptionTest() {

    }

    @Ignore
    public void editBloodPressureTest() {

    }

    @Ignore
    public void editChronicDiseasesTest() {

    }

    @Ignore
    public void editOrgansToDonateTest() {

    }

    @Ignore
    public void editPastDonationsTest() {

    }

    @Test
    public void validateBMITest() {
        Scene scene = getTopScene();
        clickOn(scene.lookup("#heightField"));
        deleteLine();

        write("1.65");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();
        scene = getTopScene();
        Button editButton = (Button) scene.lookup("#editButton");
        clickOn(editButton);

        clickOn("#weightField");
        deleteLine();
        write("70");
        clickOn("#saveButton");
        closeYesConfirmationDialogue();

        scene = getTopScene();
        Label bmi = (Label) scene.lookup("#bmiLabel");

        assertEquals("25.71166207529844", bmi.getText().substring(6));
    }
}
