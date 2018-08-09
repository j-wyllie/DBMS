package odms.controller.data.database;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.time.LocalDate;
import odms.controller.database.MySqlCommonDAO;
import odms.controller.database.MySqlProfileDAO;
import odms.model.profile.Profile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MySqlCommonDAOTest extends MySqlCommonTests {
    private MySqlCommonDAO mySqlCommonDAO;
    private MySqlProfileDAO mySqlProfileDAO;

    private Profile testProfile0 = new Profile("Joshua", "Wyllie", LocalDate.of(1997, 7, 18), "ABC1234");

    private PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private String expectedBadOutput = "Please enter a valid read-only query.\n";
    private String expectedGoodOutput =
            "+--------------------------+--------------------------------------------------------+----------------------------+----------------------------+------------------------------------+--------------------------------------------------------+-------------------------+\n"
            + "| NHI                      | GivenNames                                             | Height                     | Weight                     | Gender                             | Lastnames                                              | Dod                     |\n"
            + "+--------------------------+--------------------------------------------------------+----------------------------+----------------------------+------------------------------------+--------------------------------------------------------+-------------------------+\n"
            + "| ABC1234                  | Joshua                                                 | 0.0                        | 0.0                        | null                               | Wyllie                                                 | null                    |\n"
            + "+--------------------------+--------------------------------------------------------+----------------------------+----------------------------+------------------------------------+--------------------------------------------------------+-------------------------+\n"
            + "\n";

    @Before
    public void setup() {
        mySqlCommonDAO = new MySqlCommonDAO();
        mySqlProfileDAO = new MySqlProfileDAO();
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
    public void testReadOnlyQuery() throws SQLException {
        mySqlProfileDAO.add(testProfile0);
        String query = "SELECT NHI, GivenNames, Height, Weight, Gender, Lastnames, dod FROM profiles";
        mySqlCommonDAO.queryDatabase(query);
        assertEquals(expectedGoodOutput, outContent.toString());
    }

    @Test
    public void TestMalformedQuery() {
        String query = "SELECT * FROM tim_likes_cookies";
        mySqlCommonDAO.queryDatabase(query);
        assertEquals(expectedBadOutput, outContent.toString());
    }
}
