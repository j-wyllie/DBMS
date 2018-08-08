package odms.controller.data.database;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import odms.controller.database.MySqlCommonDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MySqlCommonDAOTest extends MySqlCommonTests {
    private MySqlCommonDAO mySqlCommonDAO;

    private PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private String expectedBadOutput = "Please enter a valid read-only query.\n";
    private String expectedGoodOutput =
        "+-----------------+--------------------------------------------------------+----------+\n"
        + "| Id              | Name                                                   | Valid    |\n"
        + "+-----------------+--------------------------------------------------------+----------+\n"
        + "| 13              | NZ                                                     | 1        |\n"
        + "+-----------------+--------------------------------------------------------+----------+\n\n";

    @Before
    public void setup() {
        mySqlCommonDAO = new MySqlCommonDAO();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStream() {
        System.setOut(originalOut);
    }

    @Test
    public void testNotReadOnlyQuery() {
        String query = "DROP TABLE countries";
        mySqlCommonDAO.queryDatabase(query);
        assertEquals(expectedBadOutput, outContent.toString());
    }

    @Test
    public void testReadOnlyQuery() {
        String query = "SELECT * FROM countries WHERE Name='NZ'";
        mySqlCommonDAO.queryDatabase(query);
        assertEquals(expectedGoodOutput, outContent.toString());
    }
}
