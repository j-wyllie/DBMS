package odms.commandlineview;

import static odms.commandlineview.Command.*;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class CommandTest {

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

        assertEquals(1, ValidateCommandType(printAllTest));
        assertEquals(2, ValidateCommandType(printDonorsTest));
        assertEquals(3, ValidateCommandType(helpTest));
        assertEquals(4, ValidateCommandType(createProfileTest));
        assertEquals(5, ValidateCommandType(viewDonorTest));
        assertEquals(6, ValidateCommandType(viewDateCreatedTest));
        assertEquals(7, ValidateCommandType(viewDonationsTest));
        assertEquals(8, ValidateCommandType(updateDonorTest));
        assertEquals(9, ValidateCommandType(addOrganTest));
        assertEquals(10, ValidateCommandType(deleteOrganTest));
        assertEquals(11, ValidateCommandType(invalidCommandTest));


    }
}
