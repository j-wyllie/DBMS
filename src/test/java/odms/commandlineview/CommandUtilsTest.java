package odms.commandlineview;

import static odms.commandlineview.CommandUtils.*;
import static org.junit.Assert.assertEquals;

import odms.data.DonorDataIO;
import odms.data.DonorDatabase;
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

    @Before
    public void setup() {
        createProfileTest = new ArrayList<>(Arrays.asList("create-profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\"".split(" ")));
        viewDonorTest = new ArrayList<>(Arrays.asList("donor dob=\"03-03-1998\" > view".split(" ")));
        viewDonationsTest = new ArrayList<>(Arrays.asList("donor dob=\"03-03-1998\" > donations".split(" ")));
        viewDateCreatedTest = new ArrayList<>(Arrays.asList("donor dob=\"03-03-1998\" > date-created".split(" ")));
        updateDonorTest = new ArrayList<>(Arrays.asList("donor given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > height=\"169\" given-names=\"Abby Rose\"".split(" ")));
        printAllTest = new ArrayList<>(Arrays.asList("print all".split(" ")));
        printDonorsTest = new ArrayList<>(Arrays.asList("print donors".split(" ")));
        helpTest = new ArrayList<>(Arrays.asList("help".split(" ")));
        addOrganTest = new ArrayList<>(Arrays.asList("donor given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > add-organ=\"liver, kidney\"".split(" ")));
        deleteOrganTest = new ArrayList<>(Arrays.asList("donor given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > remove-organ=\"liver, kidney\"".split(" ")));
        invalidCommandTest = new ArrayList<>(Arrays.asList("This is not a command".split(" ")));
    }

    @Test
    public void testCommandValidation() {
        assertEquals(Commands.PRINTALL, validateCommandType(printAllTest));
        assertEquals(Commands.PRINTDONORS, validateCommandType(printDonorsTest));
        assertEquals(Commands.HELP, validateCommandType(helpTest));
        assertEquals(Commands.PROFILECREATE, validateCommandType(createProfileTest));
        assertEquals(Commands.PROFILEVIEW, validateCommandType(viewDonorTest));
        assertEquals(Commands.DONORDATECREATED, validateCommandType(viewDateCreatedTest));
        assertEquals(Commands.DONORDONATIONS, validateCommandType(viewDonationsTest));
        assertEquals(Commands.DONORUPDATE, validateCommandType(updateDonorTest));
        assertEquals(Commands.ORGANADD, validateCommandType(addOrganTest));
        assertEquals(Commands.ORGANREMOVE, validateCommandType(deleteOrganTest));
        assertEquals(Commands.INVALID, validateCommandType(invalidCommandTest));
    }

    @Test
    public void testViewAttrBySearch() {
        DonorDatabase testDb = DonorDataIO.loadData("example/example.json");

//        CommandUtils.viewAttrBySearch();
    }

    @Test
    public void testArgumentCompleter() {
        ArgumentCompleter completer = Commands.commandAutoCompletion();
        assertEquals(completer.getClass(), ArgumentCompleter.class);
    }

}
