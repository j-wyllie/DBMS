package odms.cli;

import static odms.cli.CommandUtils.validateCommandType;
import static org.junit.Assert.assertEquals;

import odms.data.ProfileDataIO;
import odms.data.ProfileDatabase;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandUtilsTest {

    private ArrayList<String> createProfileTest;
    private ArrayList<String> viewDonorTest;
    private ArrayList<String> viewDonationsTest;
    private ArrayList<String> viewDateCreatedTest;
    private ArrayList<String> updateDonorTest;
    private ArrayList<String> printAllTest;
    private ArrayList<String> printDonorsTest;
    private ArrayList<String>  helpTest;
    private ArrayList<String> addOrganTest;
    private ArrayList<String> deleteOrganTest;
    private ArrayList<String> invalidCommandTest;

    private String createProfileTestStr;
    private String viewDonorTestStr;
    private String viewDonationsTestStr;
    private String viewDateCreatedTestStr;
    private String updateDonorTestStr;
    private String printAllTestStr;
    private String printDonorsTestStr;
    private String helpTestStr;
    private String addOrganTestStr;
    private String deleteOrganTestStr;
    private String invalidCommandTestStr;

    @Before
    public void setup() {
        // Command Arrays
        createProfileTest = new ArrayList<>(Arrays.asList("create-profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" ird=\"123456789\"".split(" ")));
        viewDonorTest = new ArrayList<>(Arrays.asList("profile dob=\"03-03-1998\" > view".split(" ")));
        viewDonationsTest = new ArrayList<>(Arrays.asList("profile dob=\"03-03-1998\" > donations".split(" ")));
        viewDateCreatedTest = new ArrayList<>(Arrays.asList("profile dob=\"03-03-1998\" > date-created".split(" ")));
        updateDonorTest = new ArrayList<>(Arrays.asList("profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > height=\"169\" given-names=\"Abby Rose\"".split(" ")));
        printAllTest = new ArrayList<>(Arrays.asList("print all".split(" ")));
        printDonorsTest = new ArrayList<>(Arrays.asList("print donors".split(" ")));
        helpTest = new ArrayList<>(Arrays.asList("help".split(" ")));
        addOrganTest = new ArrayList<>(Arrays.asList("profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > add-organ=\"liver, kidney\"".split(" ")));
        deleteOrganTest = new ArrayList<>(Arrays.asList("profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > remove-organ=\"liver, kidney\"".split(" ")));
        invalidCommandTest = new ArrayList<>(Arrays.asList("This is not a command".split(" ")));

        // Command Strings
        createProfileTestStr = "create-profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" ird=\"123456789\"";
        viewDonorTestStr = "profile dob=\"03-03-1998\" > view";
        viewDonationsTestStr = "profile dob=\"03-03-1998\" > donations";
        viewDateCreatedTestStr = "profile dob=\"03-03-1998\" > date-created";
        updateDonorTestStr = "profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > height=\"169\" given-names=\"Abby Rose\"";
        printAllTestStr = "print all";
        printDonorsTestStr = "print donors";
        helpTestStr = "help";
        addOrganTestStr = "profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > add-organ=\"liver, kidney\"";
        deleteOrganTestStr = "profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > remove-organ=\"liver, kidney\"";
        invalidCommandTestStr = "This is not a command";
    }

    @Test
    public void testCommandValidation() {
        assertEquals(Commands.PRINTALL, validateCommandType(printAllTest, printAllTestStr));
        assertEquals(Commands.PRINTDONORS, validateCommandType(printDonorsTest, printDonorsTestStr));
        assertEquals(Commands.HELP, validateCommandType(helpTest, helpTestStr));
        assertEquals(Commands.PROFILECREATE, validateCommandType(createProfileTest, createProfileTestStr));
        assertEquals(Commands.PROFILEVIEW, validateCommandType(viewDonorTest, viewDonorTestStr));
        assertEquals(Commands.DONORDATECREATED, validateCommandType(viewDateCreatedTest, viewDateCreatedTestStr));
        assertEquals(Commands.DONORDONATIONS, validateCommandType(viewDonationsTest, viewDonationsTestStr));
        assertEquals(Commands.DONORUPDATE, validateCommandType(updateDonorTest, updateDonorTestStr));
        assertEquals(Commands.ORGANADD, validateCommandType(addOrganTest, addOrganTestStr));
        assertEquals(Commands.ORGANREMOVE, validateCommandType(deleteOrganTest, deleteOrganTestStr));
        assertEquals(Commands.INVALID, validateCommandType(invalidCommandTest, invalidCommandTestStr));
    }

    @Test
    public void testViewAttrBySearch() {
        ProfileDatabase testDb = ProfileDataIO.loadData("example/example.json");

//        CommandUtils.viewAttrBySearch();
    }

    @Test
    public void testArgumentCompleter() {
        ArgumentCompleter completer = Commands.commandAutoCompletion();
        assertEquals(completer.getClass(), ArgumentCompleter.class);
    }

}
