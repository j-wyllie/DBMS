package odms.cli;

import odms.data.IrdNumberConflictException;
import odms.data.ProfileDatabase;
import odms.profile.Profile;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static odms.cli.CommandUtils.validateCommandType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommandUtilsTest {
    private ProfileDatabase profileDb;
    private Profile profileOne;
    private Profile profileTwo;

    // Output Handling
    private PrintStream stdout;
    private ByteArrayOutputStream result;

    @Before
    public void setup() {
        // Capture output stream
        stdout = System.out;
        result = new ByteArrayOutputStream();
        System.setOut(new PrintStream(result));

        // Database setup
        this.profileDb = new ProfileDatabase();

        // TODO if test data is commonly created for test cases, consider abstraction to a
        // TODO centralised TestDataCreator class
        // Create some test profiles
        ArrayList<String> donorOneAttr = new ArrayList<>();
        donorOneAttr.add("given-names=\"John\"");
        donorOneAttr.add("last-names=\"Wayne\"");
        donorOneAttr.add("dob=\"17-01-1998\"");
        donorOneAttr.add("ird=\"123456789\"");

        ArrayList<String> donorTwoAttr = new ArrayList<>();
        donorTwoAttr.add("given-names=\"Sam\"");
        donorTwoAttr.add("last-names=\"Sick\"");
        donorTwoAttr.add("dob=\"17-01-1997\"");
        donorTwoAttr.add("ird=\"123456878\"");

        try {
            profileOne = new Profile(donorOneAttr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            profileTwo = new Profile(donorTwoAttr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            profileDb.addProfile(profileOne);
            profileDb.addProfile(profileTwo);
        } catch (IrdNumberConflictException e) {
            e.printStackTrace();
        }

    }

    @After
    public void cleanup() {
        try {
            Files.deleteIfExists(Paths.get("CommandUtilsTest.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Reset output stream
        System.setOut(stdout);
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
    public void testCreateProfileCommand() {
        String givenNames = "Given Names";
        String lastNames = "Last Names";
        String dob = "12-08-1989";
        String irdNumber = "987654321";

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

        odms.cli.commands.Profile.createProfile(profileDb, createProfileStr);

        Profile profile = profileDb.searchIRDNumber(Integer.valueOf(irdNumber)).get(0);

        assertEquals(profile.getGivenNames(), givenNames);
        assertEquals(profile.getLastNames(), lastNames);
        assertEquals(profile.getDateOfBirth(), dobConverted);
        assertEquals(profile.getIrdNumber(), Integer.valueOf(irdNumber));
    }

    @Test
    public void testDeleteProfileCommand() {
        String irdNumber = "123456789";
        String deleteProfileStr = "profile " +
            "ird=\"" + irdNumber + "\" "
            + "> delete";
        odms.cli.commands.Profile.deleteProfileBySearch(profileDb, deleteProfileStr);

        assertEquals(profileDb.searchIRDNumber(123456789).size(), 0);
    }

    @Test
    public void testUpdateProfileCommand() {
        String givenNames = "Boaty McBoatface";
        String irdNumber = "123456789";
        String updateProfileStr = "profile " +
            "ird=\"" + irdNumber + "\" "
            + "> "
            + "given-names=\"" + givenNames + "\"";

        odms.cli.commands.Profile.updateProfilesBySearch(profileDb, updateProfileStr);

        Profile updatedProfile = profileDb.searchIRDNumber(Integer.valueOf(irdNumber)).get(0);
        assertEquals(updatedProfile.getGivenNames(), givenNames);
    }
    
    @Test
    public void testProfileDateCreatedCommand() {
        String irdNumber = "123456789";
        String deleteProfileStr = "profile " +
            "ird=\"" + irdNumber + "\" "
            + "> date-created";
        Profile profile = profileDb.searchIRDNumber(Integer.valueOf(irdNumber)).get(0);
        odms.cli.commands.Profile.viewDateTimeCreatedBySearch(profileDb, deleteProfileStr);

        assertTrue(
            result.toString().trim().split("\\r?\\n")[3]
                .contains(profile.getTimeOfCreation().toString())
        );

    }

}
