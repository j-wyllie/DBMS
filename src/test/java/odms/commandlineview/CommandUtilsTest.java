package odms.commandlineview;

import static odms.commandlineview.CommandUtils.*;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class CommandUtilsTest {

    private String createProfileTest;
    private String viewDonorTest;
    private String viewDonationsTest;
    private String viewDateCreatedTest;
    private String updateDonorTest;
    private String printAllTest;
    private String printDonorsTest;
    private String helpTest;
    private String addOrganTest;
    private String deleteOrganTest;
    private String invalidCommandTest;

    @Before
    public void setup() {
        createProfileTest = "create-profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\"";
        viewDonorTest = "donor dob=\"03-03-1998\" > view";
        viewDonationsTest = "donor dob=\"03-03-1998\" > donations";
        viewDateCreatedTest = "donor dob=\"03-03-1998\" > date-created";
        updateDonorTest = "donor given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > height=\"169\" given-names=\"Abby Rose\"";
        printAllTest = "print all";
        printDonorsTest = "print donors";
        helpTest = "help";
        addOrganTest = "donor given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > add-organ=\"liver, kidney\"";
        deleteOrganTest = "donor given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > remove-organ=\"liver, kidney\"";
        invalidCommandTest = "This is not a command";
    }

    @Test
    public void TestCommandValidation() {
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
}
