package GUI;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import odms.controller.GuiMain;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;
import org.controlsfx.control.CheckComboBox;
import org.junit.*;
import org.testfx.api.FxToolkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class ClinicianProfileControllerTest extends TestFxMethods {

    //Runs tests in background if headless is set to true. This gets it working with the CI.
    @BeforeClass
    public static void headless() {
        GUITestSetup.headless();
    }

    @After()
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Before
    public void loginUser() {
        logInClinician();
    }


    /**
     * Initializes the main gui
     *
     * @param stage current stage
     * @throws Exception throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        GuiMain guiMain = new GuiMain();
        guiMain.start(stage);
    }

    /**
     * logs in the clinician and opens up the search tab
     */
    private void logInClinician() {
        clickOn("#usernameField").write("0");
        clickOn("#loginButton");
    }


    /**
     * Checks that the correct Profiles are displayed when using search tables filters
     */
    @Test
    public void filterSearchTableTest() {

        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Scene scene = getTopScene();

        clickOn("#ageField").write("10");
        clickOn("#ageRangeField").write("30");

        Profile firstDonor = (Profile) searchTable.getItems().get(0);

        assertTrue(firstDonor.getAge() > 10 && firstDonor.getAge() < 30);

        //resetting
        clickOn("#ageField").write("0");
        clickOn("#ageRangeField").write("120");

        CheckComboBox genderCombobox = (CheckComboBox) scene.lookup("#genderCombobox");
        //to overcome 'not on FX' thread exception
        Platform.runLater(() -> genderCombobox.getCheckModel().check(0));
        clickOn(scene.lookup("#genderCombobox"));
        firstDonor = (Profile) searchTable.getItems().get(0);

        assertEquals("male", firstDonor.getGender());

        CheckComboBox organsCombobox = (CheckComboBox) scene.lookup("#organsCombobox");
        //to overcome 'not on FX' thread exception
        Platform.runLater(() -> {
            organsCombobox.getCheckModel().check(6); //kidney
            organsCombobox.getCheckModel().check(7); //liver
            organsCombobox.getCheckModel().check(8); //intestine


        });
        clickOn(scene.lookup("#organsCombobox"));
        firstDonor = (Profile) searchTable.getItems().get(0);

        assertTrue(firstDonor.getOrgansDonating().contains(OrganEnum.KIDNEY));
        assertTrue(firstDonor.getOrgansDonating().contains(OrganEnum.INTESTINE));
        assertTrue(firstDonor.getOrgansDonating().contains(OrganEnum.LIVER));

    }


    /**
     * Checks that the correct donor's profile is opened from the search table.
     */
    @Test
    public void openSearchedProfileTest() {
        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile firstDonor = (Profile) searchTable.getItems().get(0);

        doubleClickOn(row("#searchTable", 0));

        // Opening the first donor
        Scene scene = getTopScene();
        Label donorName = (Label) scene.lookup("#donorFullNameLabel");
        assertEquals(firstDonor.getFullName(), donorName.getText()); //checks name label is equal
    }

    /**
     * Tests that a donor's profile can be opened by a clinician and that the name can be
     * successfully updated. The name is checked in the database and in the GUI to make sure it
     * updates. Changes the donor back to the original.
     */
    @Test
    public void editSearchedProfileTest() {
        // Open up the first donor
        clickOn("#searchTab");
        doubleClickOn(row("#searchTable", 0));
        Scene scene = getTopScene();

        Label userIdLabel = (Label) scene.lookup("#userIdLabel");
        Integer userId = Integer
                .parseInt(userIdLabel.getText().substring(10)); //gets id of user being edited.

        String originalGivenNames = ((Label) scene.lookup("#givenNamesLabel")).getText()
                .substring(14);
        String originalLastNames = ((Label) scene.lookup("#lastNamesLabel")).getText()
                .substring(11);
        // Opening edit tab
        clickOn((scene.lookup("#editButton")));

        // Editing donor
        Scene scene2 = getTopScene();
        TextField givenNames = (TextField) scene2.lookup("#givenNamesField");
        TextField lastNames = (TextField) scene2.lookup("#lastNamesField");
        clickOn(givenNames).eraseText(originalGivenNames.length()).write("Bob");
        clickOn(lastNames).eraseText(originalLastNames.length()).write("Seger");

        Button saveButton = (Button) scene2.lookup("#saveButton");
        clickOn(saveButton);

        Stage stage = getAlertDialogue();
        DialogPane dialogPane = (DialogPane) stage.getScene().getRoot();
        Button yesButton = (Button) dialogPane.lookupButton(ButtonType.YES);
        clickOn(yesButton);

        // Checks database has been updated
        assertEquals("Bob", GuiMain.getCurrentDatabase().getProfile(userId).getGivenNames());
        assertEquals("Seger", GuiMain.getCurrentDatabase().getProfile(userId).getLastNames());

        // Checks GUI has been updated.
        scene2 = getTopScene();
        Label updatedGivenNames = (Label) scene2.lookup("#givenNamesLabel");
        Label updatedLastNames = (Label) scene2.lookup("#lastNamesLabel");
        assertEquals("Bob", updatedGivenNames.getText().substring(14));
        assertEquals("Seger", updatedLastNames.getText().substring(11));

        // Reset user through GUI
        // Open edit donor back up
        Scene donorScene = getTopScene();
        // editDonorButton = (Button) searchedDonorScene.lookup("#editDonorButton2");
        clickOn(donorScene.lookup("#editButton"));

        scene2 = getTopScene();
        givenNames = (TextField) scene2.lookup("#givenNamesField");
        lastNames = (TextField) scene2.lookup("#lastNamesField");
        clickOn(givenNames).eraseText(3).write(originalGivenNames);
        clickOn(lastNames).eraseText(5).write(originalLastNames);

        Button saveButton2 = (Button) scene2.lookup("#saveButton");
        clickOn(saveButton2);

        Stage stage2 = getAlertDialogue();
        DialogPane dialogPane3 = (DialogPane) stage2.getScene().getRoot();
        yesButton = (Button) dialogPane3.lookupButton(ButtonType.YES);
        clickOn(yesButton);
    }

    @Test
    public void searchProfileNameTest() {
        // Search for profiles with name Jack Smith
        clickOn("#searchTab");
        clickOn("#searchField").write("Jack Smith");

        doubleClickOn(row("#searchTable", 0));
        Scene scene = getTopScene();

        Label userIdLabel = (Label) scene.lookup("#userIdLabel");
        Integer userId = Integer
                .parseInt(userIdLabel.getText().substring(10)); //gets id of user being edited.
        assertEquals(1, (int) userId);
    }
}
