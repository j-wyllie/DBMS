package odms.cli;

import static odms.cli.CommandUtils.validateCommandType;
import static org.junit.Assert.assertEquals;

import odms.data.ProfileDatabase;
import odms.profile.Profile;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class CommandUtilsTest {
    private ProfileDatabase profileDb;

    @Before
    public void setup() {
        this.profileDb = new ProfileDatabase();
    }

    @After
    public void cleanup() {
        try {
            Files.deleteIfExists(Paths.get("CommandUtilsTest.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCommandValidation() {
        // Command Strings
        String createProfileTestStr = "create-profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" ird=\"123456789\"";
        String viewDonorTestStr = "profile dob=\"03-03-1998\" > view";
        String viewDonationsTestStr = "profile dob=\"03-03-1998\" > donations";
        String viewDateCreatedTestStr = "profile dob=\"03-03-1998\" > date-created";
        String updateDonorTestStr = "profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > height=\"169\" given-names=\"Abby Rose\"";
        String printAllTestStr = "print all";
        String printDonorsTestStr = "print donors";
        String helpTestStr = "help";
        String addOrganTestStr = "profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > add-organ=\"liver, kidney\"";
        String deleteOrganTestStr = "profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > remove-organ=\"liver, kidney\"";
        String invalidCommandTestStr = "This is not a command";

        // Command Arrays
        ArrayList<String> createProfileTest = new ArrayList<>(Arrays.asList(createProfileTestStr.split("\\s")));
        ArrayList<String> viewDonorTest = new ArrayList<>(Arrays.asList(viewDonorTestStr.split("\\s")));
        ArrayList<String> viewDonationsTest = new ArrayList<>(Arrays.asList(viewDonationsTestStr.split("\\s")));
        ArrayList<String> viewDateCreatedTest = new ArrayList<>(Arrays.asList(viewDateCreatedTestStr.split("\\s")));
        ArrayList<String> updateDonorTest = new ArrayList<>(Arrays.asList(updateDonorTestStr.split("\\s")));
        ArrayList<String> printAllTest = new ArrayList<>(Arrays.asList(printAllTestStr.split("\\s")));
        ArrayList<String> printDonorsTest = new ArrayList<>(Arrays.asList(printDonorsTestStr.split("\\s")));
        ArrayList<String> helpTest = new ArrayList<>(Arrays.asList(helpTestStr.split("\\s")));
        ArrayList<String> addOrganTest = new ArrayList<>(Arrays.asList(addOrganTestStr.split("\\s")));
        ArrayList<String> deleteOrganTest = new ArrayList<>(Arrays.asList(deleteOrganTestStr.split("\\s")));
        ArrayList<String> invalidCommandTest = new ArrayList<>(Arrays.asList(invalidCommandTestStr.split("\\s")));

        // Check tokens validate appropriately
        assertEquals(Commands.PRINTALL, validateCommandType(printAllTest, printAllTestStr));
        assertEquals(Commands.PRINTDONORS, validateCommandType(printDonorsTest, printDonorsTestStr));
        assertEquals(Commands.HELP, validateCommandType(helpTest, helpTestStr));
        assertEquals(Commands.PROFILECREATE, validateCommandType(createProfileTest, createProfileTestStr));
        assertEquals(Commands.PROFILEVIEW, validateCommandType(viewDonorTest, viewDonorTestStr));
        assertEquals(Commands.PROFILEDATECREATED, validateCommandType(viewDateCreatedTest, viewDateCreatedTestStr));
        assertEquals(Commands.PROFILEDONATIONS, validateCommandType(viewDonationsTest, viewDonationsTestStr));
        assertEquals(Commands.PROFILEUPDATE, validateCommandType(updateDonorTest, updateDonorTestStr));
        assertEquals(Commands.ORGANADD, validateCommandType(addOrganTest, addOrganTestStr));
        assertEquals(Commands.ORGANREMOVE, validateCommandType(deleteOrganTest, deleteOrganTestStr));
        assertEquals(Commands.INVALID, validateCommandType(invalidCommandTest, invalidCommandTestStr));
    }

    @Test
    public void testArgumentCompleter() {
        ArgumentCompleter completer = Commands.commandAutoCompletion();
        assertEquals(completer.getClass(), ArgumentCompleter.class);
    }

    @Test
    public void testInvalidCommand() {

    }

    @Test
    public void testHelpCommand() {

    }

    @Test
    public void testPrintAll() {

    }

    @Test
    public void testPrintDonors() {

    }

    @Test
    public void testUndoRedoCommand() {

    }

    @Test
    public void testExportCommand() {

    }

    @Test
    public void testImportCommand() {

    }

    @Test
    public void testCreateProfileCommand() {
        String givenNames = "Given Names";
        String lastNames = "Last Names";
        String dob = "12-08-1989";
        String irdNumber = "123456789";

        LocalDate dobConverted = LocalDate.of(
                Integer.valueOf(dob.split("-")[2]),
                Integer.valueOf(dob.split("-")[1]),
                Integer.valueOf(dob.split("-")[0])
        );

        String createProfileStr = "create-profile " +
                "given-names=\"" + givenNames + "\" " +
                "last-names=\"" + lastNames + "\" " +
                "dob=\"" + dob + "\" " +
                "ird=\"" + irdNumber + "\"";

        CommandUtils.createProfile(profileDb, createProfileStr);

        Profile profile = profileDb.searchIRDNumber(123456789).get(0);

        assertEquals(profile.getGivenNames(), givenNames);
        assertEquals(profile.getLastNames(), lastNames);
        assertEquals(profile.getDateOfBirth(), dobConverted);
        assertEquals(profile.getIrdNumber(), Integer.valueOf(irdNumber));
    }

    @Test
    public void testDeleteProfileCommand() {

    }

    @Test
    public void testUpdateProfileCommand() {

    }

    @Test
    public void testProfileDateCreatedCommand() {

    }

    @Test
    public void testProfileDonationsCommand() {

    }

    @Test
    public void testProfileUpdateCommand() {

    }

    @Test
    public void testOrganAddCommand() {

    }

    @Test
    public void testOrganRemoveCommand() {

    }

    @Test
    public void testOrganDonateCommand() {

    }
}
